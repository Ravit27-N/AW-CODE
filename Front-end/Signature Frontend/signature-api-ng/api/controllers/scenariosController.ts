import {$count, $length, $ok, $unsigned, stringifyPrisma} from '../../utils/commons';
import { $inspect } from '../../utils/utils';

import { ConflictError, HTTPClientError, ManifestDataError } from '../../utils/errors' ;

import { APIServer } from '../../server';
import { ScenarioStatus } from '../APIConstants';
import { APIAuth, ScenarioBody } from '../APIInterfaces';
import { missingErrorsInScenarioBody, validateScenarioSteps } from './scenarioCommons';
import { checkSessionMutability, sessionWithPublicID } from './sessionController';
import {Prisma} from ".prisma/client";
import {IScenario, ScenarioCreateInput, ScenarioEntity} from "../../entities/scenario";
import {GlobalID} from "../APIIDs";
import {ScenarioService} from "../../services/scenario.service";
import { PrismaContext } from '../../classes/interfaces/DBInterfaces';


export const getSessionScenariosList = async (auth:APIAuth, sessionPublicID:GlobalID) : Promise<string[]> => {
	const c: PrismaContext<Prisma.SessionInclude> = {
		include: {
			scenarios: true
		}
	}
	let session = await sessionWithPublicID(auth, sessionPublicID, c) ;
	// we did load the scenarios with our session and did sort them by rank
	return $count(session.getScenarios) ? session.getScenarios!.map((s) => {
		return s.url(sessionPublicID);
	}) : [] ;
} ;

export const addScenarioToSession = async (auth:APIAuth, sessionPublicID:GlobalID, body:ScenarioBody) : Promise<ScenarioEntity> => {

	let returnValue = undefined ;
	const api = APIServer.api() ;
	const scenarioService = new ScenarioService();
	let [message, error] = missingErrorsInScenarioBody(api, body) ;

	if ($length(message)) {
		(<HTTPClientError>error).message = `Impossible to add scenario to session ${sessionPublicID}. ${message}.` ;
		throw error;
	}

	if (!api.verifyManifestData(body['manifest-data'], api.conf.scenarioManifestData)) {
		throw new ManifestDataError(`manifest-data did not match allowed keys.`);
	}

	try {
		returnValue = await api.transaction(async trx => {
			const context = {trx:trx} ;
			let session = await sessionWithPublicID(auth, sessionPublicID, context) ;
			await checkSessionMutability(session) ;
			let infos = await validateScenarioSteps(api, session, body.documents, body.steps, context) ;
			const previousScenarios = await scenarioService.findMany<IScenario>({
				where: {
					sessionId: session.id
				},
				select: {
					status: true
				},
				orderBy: {
					rank: "asc"
				}
			})
			const n = $count(previousScenarios) ;
			if (n && previousScenarios[n-1].status != ScenarioStatus.WellTerminated /*&& results[n-1].status != ScenarioStatus.WellExpiredAfterSplit*/) {
				throw new ConflictError('Trying to insert new scenario after a non terminated or bad terminated scenario') ;
			}

			const maxRank = await session.maxScenarioRank(context) ;

			const resource:ScenarioCreateInput = {
				otherData: stringifyPrisma({
					documentURLs:body.documents,
					dids:(infos?.dids),
					aids:(infos?.aids),
					automat:(infos?.automat)
				}),
				publicId:session.sessionNextPublicID(), // for now it's not an asyn fn here
				rank:maxRank+1,
				session: {
					connect: {
						id: session.id
					}
				},
				signatureFormat:$unsigned(body.format),
				signatureLevel:$unsigned(body.level),
				status:ScenarioStatus.UnderConstruction,
				stepsDefinition: stringifyPrisma(body.steps)
			}
			if ($ok(body['manifest-data'])) resource.manifestData = stringifyPrisma(body['manifest-data']);
			if ($ok(body['user-data'])) resource.userData = stringifyPrisma(body['user-data']);

			APIServer.api().log(`addScenarioToSession(${session.publicId},${$inspect(resource)})`)

			let scenario = await scenarioService.insert(resource) ;
			scenario.setSession = await session.updateSession({
				lastPubObject:session.lastPubObject,
			}, context); // we want our graph straight

			return scenario;
		}) ;
	}
	catch (e) {
		APIServer.api().error(e);
		throw e ;
	}
	return returnValue;
}
