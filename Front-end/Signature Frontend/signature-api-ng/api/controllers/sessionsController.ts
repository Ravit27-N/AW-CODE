import {$count, $unsigned} from '../../utils/commons'

import {BadRequestError, ConflictError, ForbiddenError, ManifestDataError} from '../../utils/errors'

import {SessionStatus, UserRole} from '../APIConstants'
import {APIAuth, CreateSessionBody, SessionsQuery} from '../APIInterfaces'
import {APIServer} from '../../server'
import { Prisma } from '@prisma/client'
import {SessionCreateInput, SessionEntity} from '../../entities/sessions'
import {SessionService} from "../../services/session.service";
import {GlobalID} from "../APIIDs";
import { NO_CONTEXT } from '../../classes/interfaces/DBConstants'

export interface SessionListNode {
	publicId: GlobalID ;
	status?:number | null;
}

export const getSessionList = async (auth:APIAuth, q:SessionsQuery) : Promise<string[]> => {
	
	let query = SessionEntity.expirationAwareListQuery<SessionsQuery, Prisma.SessionWhereInput>(auth, q, NO_CONTEXT);
	const sessionService = new SessionService();
	if (auth.role === UserRole.Action) {
		query = {
			...query,
			user: auth.user,
			OR:{
				actors: {
					every:{
						login: auth.user
					}
				}
			}
		}
	}
	// Todo: update to use service
	let list = await sessionService.findMany<SessionListNode>({
		where: query,
		select: {
			publicId: true,
			status: true
		},
		orderBy: {
			publicId: 'asc'
		}
	});
	// TODO: here we can verify if we have a single bit status_mak which can
	// directly relate to database status value and put it in the request
	// instead of making post treatment
	let mask = $unsigned(q['status_mask']) ;
	if ($count(list) && mask > 0) {
		list = list.filter((n:SessionListNode) => ((( n?.status?? 0) & mask) > 0)) ;

	}
	const api = APIServer.api() ;
	return $count(list) ? list.map((n:SessionListNode) => api.url('session', n.publicId)) : [] ;
};

export const createSession = async (auth:APIAuth, body:CreateSessionBody) : Promise<SessionEntity> => {
	if (auth.role === UserRole.Request) {
		// a request user cannot create sessions
		throw new ForbiddenError() ;
	}
	const api = APIServer.api() ;
	if (!api.verifyManifestData(body['manifest-data'], api.conf.sessionManifestData)) {

		throw new ManifestDataError(`manifest-data did not match allowed keys.`);
	}

	const ttl = $unsigned(body.ttl) ;
	if (!ttl) { throw new BadRequestError('ttl undefined') ; }

	if (ttl < api.conf.ttlMin || ttl > api.conf.ttlMax) {
		throw new ConflictError(`Bad session ttl ${ttl}.`);
	}
	let returnValue= undefined ;
	try {
		returnValue = await api.transaction(async (trx) => {
			const context = {trx};
			let n = await SessionEntity.nextGlobalPublicID(context); // this method updates NGConfig table
			let newSession: SessionCreateInput = {
				publicId: n,
				status: SessionStatus.Genuine,
				ttl: ttl,
				user: auth.user,
				otherData: {sessionContextEvents: []},
				manifestData: body['manifest-data'] ?? {},
				userData: body['user-data'] ?? {}
			};
			const sessionService = new SessionService();
			return await sessionService.insert(newSession, {});

		});
		
	}
	catch (e) {
		// here we have a rollback
		APIServer.api().error(e);
		throw e;
	}
	return returnValue;

};

