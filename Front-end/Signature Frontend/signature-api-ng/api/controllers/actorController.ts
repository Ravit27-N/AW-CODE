import {$isnumber, $ok} from '../../utils/commons';

import {ForbiddenError, NotFoundError} from '../../utils/errors'

import {sessionWithPublicID} from './sessionController'
import {GlobalID, LocalID} from '../APIIDs';
import {APIAuth} from '../APIInterfaces';
import {APIServer} from '../../server';
import {SessionEntity} from "../../entities/sessions";
import {ActorService} from "../../services/actor.service";
import {PrismaContext} from '../../classes/interfaces/DBInterfaces';
import {ActorEntity, IActor} from '../../entities/actors';

export const getSessionActorByID = async (auth:APIAuth, sessionOrID:GlobalID|SessionEntity, aid:LocalID, c:PrismaContext) : Promise<ActorEntity> => {
	const actorService = new ActorService();
	let session = $isnumber(sessionOrID) ? await sessionWithPublicID(auth, <GlobalID>sessionOrID, {trx:c.trx}) : <SessionEntity>sessionOrID  ;
	let actor = await actorService.sessionObjectWithPublicID<IActor>(session, aid, c) ;
	if (!$ok(actor)) {
		throw new NotFoundError(`Actor with IDs (${session.publicId},${aid}) was not found.`);
	}
	return new ActorEntity(<IActor>actor) ;
}

export const removeSessionActor = async (auth:APIAuth, sessionPublicID:GlobalID, aid:LocalID) : Promise<string> =>
{
	const api = APIServer.api() ;
	let returnValue = undefined ;
	try {
		
		returnValue = await api.transaction(async (trx) => {
			const context = {trx:trx} ;
			let actor = await getSessionActorByID(auth, sessionPublicID, aid, context) ;
			if (!(await actor.canBeDeleted(context))) {
				throw new ForbiddenError(`Actor with IDs (${sessionPublicID},${aid}) cannot be deleted.`);
			}
			const url = actor.url(sessionPublicID);
			await actor.delete(context) ;
			return url ;
		}) ;
	}
	catch (e) {
		// here we have a rollback
		APIServer.api().error(e);
		throw e ;
	}

	return returnValue ;
}
