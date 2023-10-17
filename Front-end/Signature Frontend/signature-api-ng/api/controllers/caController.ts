import {$count, $length, $ok, stringifyPrisma} from "../../utils/commons" ;
import { $isfile, $path, $removeFile, $writeBuffer } from "../../utils/fs" ;
import { $uuid } from "../../utils/crypto";
import { $inspect } from "../../utils/utils" ;

import { NotFoundError, FileError, ForbiddenError } from "../../utils/errors";

import { Certigna } from "../../classes/CertignaEndPoint";
import { APIServer } from "../../server";
import { getSessionActorByID } from "./actorController";
import { sessionWithPublicID } from "./sessionController";
import { GlobalID, LocalID } from "../APIIDs";
import { APIAuth, CGUResource } from "../APIInterfaces";
import { UserRole } from "../APIConstants";
import {CAEntity} from "../../entities/CAs";
import {CAService} from "../../services/ca.service";
import {CaTokenService} from "../../services/caToken.service";
import {DownloadEntity} from "../../entities/downloads";
import {CaTokenEntity} from "../../entities/CATokens";
import {DownloadService} from "../../services/download.service";
import {CAData} from "../../classes/interfaces/ICATypeMap";
import { NO_CONTEXT, TokenStatus } from "../../classes/interfaces/DBConstants";
import { PrismaContext } from "../../classes/interfaces/DBInterfaces";

export const authorityWithPublicID = async (auth:APIAuth, caid:GlobalID, c:PrismaContext) : Promise<CAEntity> => {
	// since auth is not used here everybody reads a CA content
	if (auth.role === UserRole.Maintenance || auth.role === UserRole.System) {
		throw new ForbiddenError('System or maintenance users cannot access certification authorities') ;
	}
	let caService = new CAService();
	let authority = await caService.objectWithPublicID<CAEntity>(caid, c);
	let authorityEntity = new CAEntity(authority!);
	if (!$ok(authorityEntity) || !authorityEntity?.isValid()) {
		throw new NotFoundError(`Certification Authority with ID ${caid} was not found.`);
	}
	return authorityEntity ;
};
export const getCAByID = authorityWithPublicID ;

export const getCACGU = async (auth:APIAuth, caid:GlobalID, sessionPublicID:GlobalID, aid:LocalID) : Promise<CGUResource> =>
{
	let returnValue = undefined ;
	let updateRemoteCGU = false ;
	let CA_cguPath:any = undefined ;
	let api = APIServer.api() ;

	api.log(`Want to get CGUs for authority with ID ${caid}`) ;

	try {
		returnValue = await api.transaction(async (trx) => {
			const context = {trx: trx};
			let authority = await  authorityWithPublicID(auth, caid, context);
			let session = await sessionWithPublicID(auth, sessionPublicID, context);
			let actor = await getSessionActorByID(auth, session, aid, context);

			let remoteCGUContent:Buffer|null = null ;
			let CA_cguVersion = authority.CGUVersion() ;
			let CA_cguSize = authority.getCAData.cguSize ;
			api.log('Authority = '+$inspect(authority)) ;
			CA_cguPath = authority.getCAData.cguLocalPath ;

			let isLocalCGU = $isfile(CA_cguPath) ;

			if (!isLocalCGU) {
				// we will get the cgu version
				let endPoint = Certigna.endPoint() ;
				let remoteCGUVersion = await endPoint.getTOUVersion(authority.getCAData.aki) ;
				if (!$ok(remoteCGUVersion)) {
					throw new NotFoundError('Impossible find CGU version') ;
				}
				if (CA_cguVersion != remoteCGUVersion) {
					remoteCGUContent = await endPoint.getTOU(authority.getCAData.aki) ;
					CA_cguSize = $length(remoteCGUContent) ;
					if (!CA_cguSize) {
						throw new FileError('Impossible to reach new CGU file') ;
					}
					CA_cguPath = $path(api.downloadsPathFiles, $uuid()) ;
					updateRemoteCGU = true ;
					CA_cguVersion = <string>remoteCGUVersion ;
				}
			}
			if (updateRemoteCGU) {
				// first, if we have a new CGU for our certification authority,
				// we need to update it
				if (!$writeBuffer(CA_cguPath, <Buffer>remoteCGUContent)) {
					throw new FileError('Impossible to write CGU file');
				}
				const newCaData: CAData = {
					aki: authority.getCAData.aki,
					longName: authority.getCAData.longName,
					cguVersion: CA_cguVersion,
					cguPath: CA_cguPath,
					cguSize: CA_cguSize,
					cguLocalPath: undefined, // keep it here because cguLocalPath determines the whole token management
				};
				await authority.update({
					caData: stringifyPrisma(newCaData)
				}, NO_CONTEXT)
			}

			// Have we an active token for this ac and actor ?
			const tokenService = new CaTokenService();
			let tokens = await tokenService.getsWhere({
				caId: authority.id,
				actorId: actor.id,
				status: TokenStatus.Active
			}, {trx: trx});
			
			let token: CaTokenEntity | null = $count(tokens) ? new CaTokenEntity(tokens![0]) : null ;

			// if the AC as a  CGU different than the last token, we need a new active token here
			// and to archive the previous one
			if (!$ok(token) || token?.cguVersion != CA_cguVersion) {
				if ($ok(token))	{
					// the previous token is no more active
					await token?.modify({status:TokenStatus.Archived}, NO_CONTEXT);
				}

				token = await tokenService.insert({
					actor: {
						connect: {
							id: actor.id
						}
					},
					ca:{
						connect: {
							id: authority.id
						}
					},
					sessionId:session.id,
					token:$uuid(),
					status:TokenStatus.Active,
					cguVersion: `${CA_cguVersion}`
				}, NO_CONTEXT) ;

				token.Actor = actor ;  			// we want
				token.Authority = authority ; 	// our graph strait
			}


			// TODO: (maybe)
			// each call to this method will produce a new download
			// there's an optimmization to be done here in order to
			// reuse a previous valid download.
			// this is not really space consuming because downloads are purged
			// and the file is not put in a FileRef so, it's really the same file
			// if CGUs don't change
			let pid = await DownloadEntity.nextGlobalPublicID(context) ; // this method updates NGConfig table
			const downloadService = new DownloadService();
			let newDownload = await downloadService.insert({
				session:{
					connect: {
						id: session.id
					}
				},
				user:auth.user,
				publicId:pid,
				size:CA_cguSize,
				path:CA_cguPath,
				ttl:api.conf.downloadTtl
			}, context) ;
			newDownload.setSession = session ; // we want our graph straight

			return {
				actor:actor.url(sessionPublicID),
				authority:authority.url(),
				'download-url':newDownload.url(sessionPublicID),
				session:session.url(),
				token:token.token,
				version:token.cguVersion
			} ;
			
		});
	}
	catch (e) {
		// here we may have a rollback
		// and so we need to destroy non-used created CGU File
		if (updateRemoteCGU && $isfile(CA_cguPath)) {
			$removeFile(CA_cguPath) ;
		}
		APIServer.api().error(e);
		throw e ;
	}
	return <CGUResource>returnValue ;
};
