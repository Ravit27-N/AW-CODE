import {$isnumber, $length, $now, $ok, $unsigned, stringifyPrisma} from '../../utils/commons';
import {$inspect} from '../../utils/utils';
import {ConflictError, ForbiddenError, HTTPClientError, ManifestDataError, NotFoundError} from '../../utils/errors'

import {checkSessionMutability, sessionWithPublicID} from './sessionController'
import {ScenarioStatus, SessionStatus} from '../APIConstants'
import {APIServer} from '../../server';
import {APIAuth, ManifestDataBody, ScenarioBody, ScenarioCancelBody, ScenarioSplitBody} from '../APIInterfaces';
import {missingErrorsInScenarioBody, updatedCanceledScenario, validateScenarioSteps} from './scenarioCommons';
import {aidsForAutomat, Automat, splitedAutomats} from '../automat/automat';
import {GlobalID, LocalID} from '../APIIDs';
import {SessionEntity} from "../../entities/sessions";
import {ScenarioService} from "../../services/scenario.service";
import {IScenario, ScenarioCreateInput, ScenarioEntity, ScenarioUpdateInput} from "../../entities/scenario";
import {PrismaContext, SessionContextEventType} from '../../classes/interfaces/DBInterfaces';
import {copyLastFilesDictionary} from "../../entities/BaseEntity";
import {$removeFile} from "../../utils/fs";

export const getSessionScenarioByID = async (
  auth: APIAuth,
  sessionOrID: GlobalID | SessionEntity,
  sid: LocalID,
  c: PrismaContext
): Promise<ScenarioEntity> => {
  const scenarioService = new ScenarioService();
  let session = $isnumber(sessionOrID) ? await sessionWithPublicID(auth, <GlobalID>sessionOrID, {trx: c.trx}) : <SessionEntity>sessionOrID;
  let scenario = null;
  if ($ok(session)) {
    scenario = await scenarioService.sessionObjectWithPublicID<IScenario>(session, sid, c);
  }
  if (!$ok(scenario)) {
    throw new NotFoundError(`Scenario with IDs (${session.publicId}, ${sid}) was not found.`);
  }
  return new ScenarioEntity(<IScenario>scenario);
}

export const updateSessionScenario = async (auth: APIAuth, sessionPublicID: GlobalID, sid: LocalID, body: ScenarioBody): Promise<ScenarioEntity> => {
  let ret = undefined;
  const api = APIServer.api() ;
  try {
    ret = await api.transaction(async trx => {
      const context = {trx: trx};
      const session = await sessionWithPublicID(auth, sessionPublicID, context);
      let scenario = await getSessionScenarioByID(auth, session, sid, context);
      if (!scenario.isUnderConstruction()) {
        throw new ConflictError(`Scenario with ID (${sessionPublicID},${sid}) cannot be modified.`);
      }

      const api = APIServer.api();
      let [message, error] = missingErrorsInScenarioBody(api, body);

      if ($length(message)) {
        (<HTTPClientError>error).message = `Impossible to update scenario with ID (${sessionPublicID}, ${sid}). ${message}.`;
        throw error;
      }

      if (!api.verifyManifestData(body['manifest-data'], api.conf.scenarioManifestData)) {
        throw new ManifestDataError(`manifest-data did not match allowed keys.`);
      }

      await checkSessionMutability(session);
      let infos = await validateScenarioSteps(api, session, body.documents, body.steps, context);

      const update: ScenarioUpdateInput = {
        otherData: stringifyPrisma({
          dids: (infos?.dids),
          aids: (infos?.aids),
          documentURLs: body.documents,
          automat: (infos?.automat)
        }),
        signatureFormat: $unsigned(body.format),
        signatureLevel: $unsigned(body.level),
        status: scenario.status,
        stepsDefinition: stringifyPrisma(body.steps)
      };
      scenario = <ScenarioEntity>await scenario.updateScenario(update, context);
      scenario.setSession = session; // we keep our graph strait
      return scenario;
    });
  } catch (e) {
    APIServer.api().error(e);
    throw e;
  }

  return ret;
}

export const activateScenario = async (auth: APIAuth, sessionPublicID: GlobalID, sid: LocalID, body: ManifestDataBody): Promise<ScenarioEntity> => {
  let api = APIServer.api();
  const manifestData = body['manifest-data'];

  api.log(`******* ACTIVATE MANIFEST DATA REFERENCE:\n${$inspect(api.conf.activateManifestData)}`);
  api.log(`******* ACTIVATE MANIFEST DATA          :\n${$inspect(manifestData)}`);

  if (!api.verifyManifestData(manifestData, api.conf.activateManifestData)) {
    throw new ManifestDataError(`manifest-data for activating scenario (${sessionPublicID},${sid}) did not match allowed keys.`);
  }

  let ret = undefined;
  try {
    ret = await api.transaction(async trx => {
      const context = {trx: trx};
      const session = await sessionWithPublicID(auth, sessionPublicID, context);
      let scenario = await getSessionScenarioByID(auth, session, sid, context);

      if (!scenario.isUnderConstruction()) {
        throw new ConflictError(`Scenario with IDs (${sessionPublicID},${sid}) cannot be activated (#1).`);
      }

      const maxRank = await session.maxScenarioRank(context);
      if (maxRank !== scenario.rank) {
        throw new ConflictError(`Scenario with IDs (${sessionPublicID},${sid}) cannot be activated (#2).`);
      }

      let scenarioOtherData = {...scenario.getOtherData};
      const lastFiles = await scenario.fetchLastFilesFromDocuments(context);
      let otherData = {...session.getOtherData};
      
      scenarioOtherData.originalLastFiles = lastFiles ;                       // those are our originals which wont change during scenario process
      scenarioOtherData.sourceFiles = copyLastFilesDictionary(lastFiles) ;    // here we have the source of document to be signed as a copy of the first one
      scenarioOtherData.generatedFiles = {} ;                                 // here we have the list of signed documents (empty for now)
      
      // the scenario manifest data is included in the event chain at activation
      // at its last modification date (keep it here before the next scenario update)
      otherData.sessionContextEvents.push({
        user: auth.user,
        date: scenario.modificationDate(),
        'event-type': SessionContextEventType.CreateScenario,
        'scenario-id': scenario.publicId,
        'manifest-data': scenario.getManifestData
      });
      scenario = <ScenarioEntity>await scenario.updateScenario({
        status: ScenarioStatus.ActiveScenario,
        otherData: stringifyPrisma(scenarioOtherData)
      }, context)
      scenario.setSession = session; // strait graph


      otherData.sessionContextEvents.push({
        user: auth.user,
        date: $now(),
        'event-type': SessionContextEventType.ActivateScenario,
        'scenario-id': scenario.publicId,
        'manifest-data': manifestData
      });
      scenario.setSession = await session.updateSession({
        status: SessionStatus.Active,
        otherData: stringifyPrisma(otherData),
      }, context); // strait graph
      return scenario;
    });
  } catch (e) {
    APIServer.api().error(e);
    throw e;
  }
  return ret;
}

export const removeSessionScenario = async (auth: APIAuth, sessionPublicID: GlobalID, sid: LocalID): Promise<string> => {
  let returnValue = undefined;
  const api = APIServer.api() ;
  try {
    returnValue = await api.transaction(async trx => {
      const context = {trx: trx};
      let scenario = await getSessionScenarioByID(auth, sessionPublicID, sid, context);
      if (!scenario.isUnderConstruction()) {
        throw new ForbiddenError(`Scenario with IDs (${sessionPublicID},${sid}) cannot be deleted.`);
      }
      let url = scenario.url(sessionPublicID);
      await scenario.delete(context);
      return url;
    });
  } catch (e) {
    // here we have a rollback
    APIServer.api().error(e);
    throw e;
  }

  return returnValue;
}


export const cancelScenario = async (auth: APIAuth, sessionPublicID: GlobalID, sid: LocalID, body: ScenarioCancelBody): Promise<ScenarioEntity> => {
  let returnedValue = undefined;

  if (!$length(body.reason)) {
    throw new ConflictError(`No reason was given to cancel the scenario.`);
  }

  let api = APIServer.api();
  const manifestData = body['manifest-data'];
  if (!api.verifyManifestData(manifestData, api.conf.cancelManifestData)) {
    throw new ManifestDataError(`manifest-data for cancelling scenario (${sessionPublicID},${sid}) did not match allowed keys.`);
  }

  try {
    let toBeDestroyedFilesPaths:string[] = [] ;
    returnedValue = await api.transaction(async trx => {
      const context = {trx: trx};
      let session = await sessionWithPublicID(auth, sessionPublicID, context);
      let scenario = await getSessionScenarioByID(auth, session, sid, context);

      if (!scenario.isActive()) {
        throw new ConflictError(`Scenario with IDs (${sessionPublicID},${sid}) cannot be canceled.`);
      }

      [scenario, toBeDestroyedFilesPaths] = await updatedCanceledScenario(scenario, context);
      scenario.setSession = session; // strait graph

      let otherData = {...session.getOtherData};
      otherData.sessionContextEvents.push({
        user: auth.user,
        date: $now(),
        'event-type': SessionContextEventType.CancelScenario,
        'scenario-id': scenario.publicId,
        reason: body.reason,
        'manifest-data': manifestData
      });

      await session.updateSession({
        status: SessionStatus.UnderConstruction,
        otherData: stringifyPrisma(otherData),
      })
      return scenario;
    });
    toBeDestroyedFilesPaths.forEach(p => $removeFile(p)) ;
  } catch (e) {
    APIServer.api().error(e);
    throw e;
  }
  return returnedValue;
}

export const splitScenario = async (auth: APIAuth, sessionPublicID: GlobalID, sid: LocalID, body: ScenarioSplitBody): Promise<ScenarioEntity> => {
  let returnValue = undefined;
  const api = APIServer.api();
  const scenarioService = new ScenarioService();
  if (!$length(body.reason)) {
    throw new ConflictError(`No reason was given to split the scenario.`);
  }

  const manifestData = body['manifest-data'];
  if (!api.verifyManifestData(body['manifest-data'], api.conf.scenarioManifestData)) {
    throw new ManifestDataError(`manifest-data did not match allowed keys for activated scenario by split operation.`);
  }

  try {
    returnValue = await api.transaction(async trx => {
      const context = {trx: trx};
      let session = await sessionWithPublicID(auth, sessionPublicID, context);
      let scenario = await getSessionScenarioByID(auth, session, sid, context);

      if (!scenario.isActive()) {
        throw new ConflictError(`Scenario with IDs (${sessionPublicID},${sid}) is not active and cannot be splitted.`);
      }
      let splittedAutomats = splitedAutomats(scenario.getOtherData.automat);

      if (!$ok(splittedAutomats)) {
        throw new ConflictError(`Scenario with IDs (${sessionPublicID},${sid}) cannot be splited.`);
      }
      const firstStep = <number>splittedAutomats?.next.index;

      let patchedOtherData = {...scenario.getOtherData};

      patchedOtherData.aids = aidsForAutomat(<Automat>(splittedAutomats?.previous));
      patchedOtherData.automat = <Automat>(splittedAutomats?.previous);

      const resource: ScenarioCreateInput = {
        otherData: stringifyPrisma({
          dids: scenario.getOtherData.dids,
          aids: aidsForAutomat(<Automat>(splittedAutomats?.next)),
          documentURLs: scenario.getOtherData.documentURLs,
          automat: <Automat>(splittedAutomats?.next)
        }),
        publicId: session.sessionNextPublicID(), // for now it's not an asyn fn here
        rank: scenario.rank + 1,
        session: {
          connect: {
            id: session.id
          }
        },
        signatureFormat: scenario.signatureFormat,
        signatureLevel: scenario.signatureFormat,
        status: ScenarioStatus.UnderConstruction, // the new scenario is underConstruction.
        stepsDefinition: stringifyPrisma(scenario.getStepsDefinition.slice(firstStep))
      }

      if ($ok(manifestData)) resource.manifestData = stringifyPrisma(manifestData);
      if ($ok(body['user-data'])) resource.userData = stringifyPrisma(body['user-data']);

      // there is no cancel ManifestData for the old scenario nor any activate ManifestData data for
      // the new one. We nethertheless need to register the new activation Event on the session
      let otherData = {...session.getOtherData};

      // we truncate the old scenario and we cancel it
      const updatedScenario = <ScenarioEntity>await scenario.updateScenario({
        otherData: stringifyPrisma(patchedOtherData),
        stepsDefinition: stringifyPrisma(scenario.getStepsDefinition.slice(0, firstStep)),
        status: ScenarioStatus.WellTerminated
      }, context)

      const newScenario = await scenarioService.insert(resource);

      // updating session context events.
      // 1) to indicate that the old scenario did split
      // 2) to add the new scenario creation event
      // 3) to add the simulateneous scenario activation event
      otherData.sessionContextEvents.push({
        user: auth.user,
        date: updatedScenario.modificationDate(),
        'event-type': SessionContextEventType.SplitScenario,
        'scenario-id': updatedScenario.publicId,
        reason: body.reason,
      });

      otherData.sessionContextEvents.push({
        user: auth.user,
        date: newScenario.creationDate(),
        'event-type': SessionContextEventType.CreateScenario,
        'scenario-id': newScenario.publicId,
        'manifest-data': newScenario.getManifestData
      });

      otherData.sessionContextEvents.push({
        user: auth.user,
        date: newScenario.creationDate(),
        'event-type': SessionContextEventType.ActivateScenario,
        'scenario-id': newScenario.publicId,
      });

      newScenario.setSession = await session.updateSession({
        lastPubObject: session.lastPubObject,
        otherData: stringifyPrisma(otherData),
      }, context); // strait graph
      return newScenario;
    });
  } catch (e) {
    APIServer.api().error(e);
    throw e;
  }

  return returnValue;
}

