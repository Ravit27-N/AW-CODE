import {$ok, $length, $now, $finalDateString, stringifyPrisma} from '../../utils/commons'
import {$removeFile} from '../../utils/fs'
import {Resp} from '../../utils/tsrequest'
import {$uuid} from '../../utils/crypto'
import {
	NotFoundError,
	ForbiddenError,
	ConflictError,
	ManifestDataError,
	InternalError,
	FileError,
	BadRequestError
} from '../../utils/errors'
import { APIServer } from '../../server'
import { APIFileType, APIRoleNames, SessionStatus } from '../APIConstants'
import { ManifestData, APIAuth } from '../APIInterfaces';
import { updatedCanceledScenario } from './scenarioCommons';
import { Manifest } from '../../classes/Manifest';
import {SessionService} from "../../services/session.service";
import {SessionEntity, ISession} from "../../entities/sessions";
import {ScenarioEntity} from "../../entities/scenario";
import {FileService} from "../../services/file.service";
import {FileCreateInput, FileEntity, IFile} from "../../entities/files";
import {DownloadEntity} from "../../entities/downloads";
import {DownloadService} from "../../services/download.service";
import {GlobalID, LocalID} from "../APIIDs";
import {PrismaContext, SessionContextEventType} from '../../classes/interfaces/DBInterfaces'

export const sessionWithPublicID = async (auth:APIAuth, sessionPublicID:number, c:PrismaContext) : Promise<SessionEntity> => {
  let sessionService =  new SessionService();
  let session = await sessionService.objectWithPublicID<ISession>(sessionPublicID, c);
  if (!$ok(session)) {
    throw new NotFoundError(`Session with ID ${sessionPublicID} was not found.`);
  }
  let sessionEntity = new SessionEntity(session!);
  if (!(await sessionEntity?.acceptsUser(auth.apiRole, auth.user, auth.role, c))) {
    throw new ForbiddenError(`Session with ID ${sessionPublicID} does not accept user ${auth.user} for action ${APIRoleNames[auth.apiRole]}.`);
  }

  return sessionEntity ;
};
export const getSessionByID = sessionWithPublicID ; // getSessionByID is meant to be used only in session.ts routes definitions

// this a private function
function _closingSessionStatus(s:SessionStatus) : SessionStatus
{
  return s === SessionStatus.Genuine ?
    SessionStatus.Deleted :
    (s === SessionStatus.UnderConstruction || s === SessionStatus.Idle ?
        SessionStatus.WellTerminated :
        SessionStatus.Canceled
    ) ;
}

// this function SHOULD be called inside a transaction
async function _manifest(auth:APIAuth, session:SessionEntity, c:PrismaContext) : Promise<DownloadEntity> {

  if (!$ok(c.trx)) {
    throw new InternalError('_manifest() should be called inside a transaction') ;
  }

  const fileService = new FileService();
  const downLoadService = new DownloadService();
  const api = APIServer.api() ;

  let file = await session.getRelated<IFile>({file: true}, c);
  let fileRef = file ? new FileEntity(file) : null;
  if (!$ok(fileRef)) {
    // we need to generate the manifest
    const producer = Manifest.producer() ;
    const manifestFile = await producer.generateManifest(auth, session, c, api.rolesTranslations(), undefined, api.conf.manifestOptions) ;
    if (!$ok(manifestFile)) {
      throw new InternalError('Impossible to generate or sign manifest file') ;
    }
    const newFileStruct = await FileEntity.fileWithBuffer(auth, manifestFile, api.conf.storagePath, $now(), $uuid(), APIFileType.PDF)
    if (!$ok(newFileStruct)) {
      throw new FileError('Impossible to save signed PDF manifest file') ;
    }
    // we will add info in otherData to know that this file is 
    // a proof manifest and should not be purged with download purge.

    fileRef = await fileService.insert(<FileCreateInput>{
      ...newFileStruct, otherData: {
        neverPurgeWithDownload: true,
        isManifest: true,
      },
    });
    if (!$ok(fileRef)) {
      $removeFile(newFileStruct?.path) ;
      $removeFile(newFileStruct?.sealPath) ;
    }
    await session.updateSession({
      file: {
        connect: {
          id: fileRef.id
        }
      }
    })
  }
  let pid = await DownloadEntity.nextGlobalPublicID(c) ; // this method updates NGConfig table

  // this function always returns a new download
  let download = await downLoadService.insert({
    publicId:pid,
    session: {
      connect: {
        id: session.id
      }
    },
    file: {
      connect: {
        id: fileRef?.id
      }
    },
    size:fileRef?.size,
    ttl:api.conf.downloadTtl,
    user:auth.user
  }) ;
  download.setSession = session; // we want our graph straight
  return download;
}

async function _closeAndManifest(
  api:APIServer,
  auth:APIAuth,
  session:SessionEntity,
  reason:string,
  manifestData:ManifestData,
  newStatus:SessionStatus | 0,
  manifest:boolean,
  c:PrismaContext) : Promise <DownloadEntity|null>
{

  if (!$ok(api) || !$ok(c.trx)) {
    throw new InternalError('_closeAndManifest() should be called inside a transaction with a valid api server') ;
  }
  if (newStatus) {
    let otherData = { ...session.getOtherData } ;
    const now = $now() ;
    otherData.sessionContextEvents.push({
      user:auth.user,
      date:now,
      'event-type':SessionContextEventType.Closure,
      reason:reason,
      'manifest-data':manifestData
    }) ;
    const updatedSession = await session.updateSession({
      expiresAt: now, // with that a closed session is always an expired one
      otherData: stringifyPrisma(otherData),
      status: newStatus
    });

    let scenario = <ScenarioEntity>await updatedSession?.myActiveScenario(c);
    

    // it means that we have an active scenario that we need to stop it
    if ($ok(scenario)) {
      await updatedCanceledScenario(scenario, c) ; // we don't use the returned scenario here
    }
    if (SessionStatus.WellTerminated && manifest) {
      return await _manifest(auth, session, c) ;
    }
    return null ;
  }
  return await _manifest(auth, session, c) ;
}

export const checkSessionMutability = async (session: SessionEntity) => {
    if (session.isClosed()) {
        throw new ForbiddenError(`Session ${session.publicId} is closed.`);
    }
    if (session.isActive()) {
        throw new ForbiddenError(`Session ${session.publicId} is active.`);
    }
    if (session.isExpired()) {
        throw new ForbiddenError(`Session ${session.publicId} is expired.`);
    }
}


export const closeSession = async (
    auth: APIAuth,
    sessionPublicID: GlobalID,
    force: boolean,
    reason: string,
    manifestData: ManifestData
)
    : Promise<{ download: DownloadEntity | null, code: number | null, status: number | null } | null> => {
    let returnValue = null;
    if (!$length(reason)) {
        throw new BadRequestError(`No reason was given to close the session.`);
    }
    const api = APIServer.api();

  if (!api.verifyManifestData(manifestData, api.conf.closureManifestData)) {
    throw new ManifestDataError(`manifest-data for session ${sessionPublicID} did not match allowed keys.`);
  }

  try {
    returnValue = await api.transaction(async (trx) =>{
      const context = {trx:trx} ;
      let session = await sessionWithPublicID(auth, sessionPublicID, context) ;
      if (session.isClosed()) {
        throw new ForbiddenError(`Session ${sessionPublicID} was already closed.`);
      }

      if (session.isActive() && (!force || !api.conf.acceptsForcedClosure)) {
        throw new ForbiddenError(`Session ${sessionPublicID} is active and cannot be closed.`);
      }

      const manifestDownload = await _closeAndManifest(
        api,
        auth,
        session,
        reason,
        manifestData,
        _closingSessionStatus(session.getStatus),
        api.conf.manifestOnClosure,
        context) ;
      return {download:manifestDownload, code:($ok(manifestDownload) ? Resp.Created : Resp.OK), status:session.status} ;
    })
  }
  catch (e) {
    api.error(e);
    throw e ;
  }

  return returnValue ;
} ;

export const extendSession = async (auth:APIAuth, sessionPublicID:GlobalID, newttl:LocalID) : Promise<SessionEntity | undefined> => {
  let returnValue = null ;
  const api = APIServer.api() ;
  try {
    returnValue = await api.transaction(async trx => {
      const context = {trx:trx} ;
      let session = await sessionWithPublicID(auth, sessionPublicID, context) ;
      if (session.isClosed() || session.isExpired()) {
        throw new ForbiddenError(`Session ${sessionPublicID} is closed or already expired.`);
      }
      if (newttl <= session.ttl || newttl > api.conf.ttlMax) {
        throw new ConflictError(`Bad new ttl ${newttl} for session ${sessionPublicID}.`);
      }
      let newFinalDate = $finalDateString(session.creationDate(), newttl) ;
      return await session.updateSession({
        expiresAt: newFinalDate,
        ttl: newttl
      }, context)
    }) ;
  }
  catch (e) {
    api.error(e);
    throw e ;
  }
  return returnValue ;
} ;

export const getSessionManifestDownload = async (auth: APIAuth, sessionPublicID: GlobalID): Promise<DownloadEntity | null> => {
  let returnValue: DownloadEntity | null = null;
  const api = APIServer.api();

  try {
    returnValue = await api.transaction(async trx => {
      const context = {trx: trx};
      let session = await sessionWithPublicID(auth, sessionPublicID, context);

      if (session.isOpened()) {
        throw new ForbiddenError(`Session ${sessionPublicID} is not closed.`);
      }

      const ret = await _closeAndManifest(api, auth, session, '', null, 0, false, context);
      if (!$ok(ret)) {
        throw new InternalError();
      }
      return ret;
    });
  } catch (e) {
    api.error(e);
    throw e;
  }
  return returnValue;
} ;