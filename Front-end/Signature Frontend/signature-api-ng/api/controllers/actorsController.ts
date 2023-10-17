import { emailRegex,Nullable, StringArrayDictionary } from '../../utils/types';
import {$count, $length, $ok, $strings, $phone, stringifyPrisma} from '../../utils/commons';
import {$trim} from '../../utils/strings';
import { BadRequestError, ConflictError, InternalError, ManifestDataError } from '../../utils/errors'

import { sessionWithPublicID, checkSessionMutability } from './sessionController'
import {ActorType, AuthType, ScenarioStatus} from '../APIConstants'
import { APICountries } from '../APICountries';
import { APIServer } from '../../server'
import { GlobalID, LocalID } from '../APIIDs';
import { APIAuth, CreateActorBody, ActorsQuery } from '../APIInterfaces';
import { Automat, automatSigningDocuments, SigningNodeDictionary } from '../automat/automat';
import { Certigna } from '../../classes/CertignaEndPoint';
import {Prisma} from "@prisma/client";
import {SessionEntity} from "../../entities/sessions";
import {ActorService} from "../../services/actor.service";
import { apiGlobals } from '../../classes/interfaces/DBConstants';
import { PrismaContext } from '../../classes/interfaces/DBInterfaces';
import {ActorCreateInput, ActorEntity} from '../../entities/actors';

export const getSessionActorList = async (auth:APIAuth,sessionPublicID:GlobalID, q:ActorsQuery) : Promise<object> => 
{
	const context: PrismaContext<Prisma.SessionInclude> = {
		include: {
			scenarios: {
				where: {
					status: ScenarioStatus.ActiveScenario
				}
			},
			actors: true
		}
	}
	let session = await sessionWithPublicID(auth, sessionPublicID, context) ; // we load the actors and the active scenario with our session
	let tags = $strings(q.tags) ;

	if ($count(tags)) {
		let returnedValue:StringArrayDictionary = {} ;
		if (session.isActive()) {
			let automat = session.getActiveScenario?.getOtherData.automat ;
		
			if ($ok(automat)) {
				let taggedDocuments = automatSigningDocuments(<Automat>automat) ;
				if ($ok(taggedDocuments)) {
					const tdocs = <SigningNodeDictionary>taggedDocuments ;
					const api = APIServer.api() ;
					tags.forEach (t => {
						let actorSet = new Set<LocalID>() ;
						let documents = tdocs[t] ;
					
						documents.forEach(d => {
							d.aids.forEach(u => actorSet.add(u)) ;
						}) ;
						let actorIDs = Array.from(actorSet) ;
						if ($count(actorIDs)) {
							returnedValue[t] = actorIDs.map(aid => api.url('session', sessionPublicID, 'actor', aid)) ;
						}
					}) ;	
				}	
			}
		}
		return returnedValue ;
	}
	return { actors: $count(session.getActors) ? session.getActors?.map((a:ActorEntity) => a.url(sessionPublicID)) : [] } ;
}

// private function
function _roles(param:Nullable<string[] | string>) : string[]
{
	// TODO: we also should validate roles against an API list
	let a = $strings(param) ;
	let roles:string[] = [] ;
	if ($count(a)) {
		a.forEach(s => { 
			s = $trim(s) ; 
			if ($length(s) > 0) roles.push(s) ; 
		}) ;
	}
	return roles ;
}

function _missingErrorsInActorBody(b:CreateActorBody) : string
{
	let u:string[] = [] ;
	let roles = _roles(b.roles) ;

	if (!$count(roles)) { u.push('roles') ; }
	if (!$length(b['name'])) { u.push('name') ; }
	if (!$length(b['country']) || !$ok((<any>APICountries)[b['country'].toUpperCase()])) { u.push('country') ; }
	if (!$length(b['email'])) { u.push('email') ; }
	if (b.type == ActorType.Entity && !$length(b['adm-id'])) { u.push('adm-id') ; }
	return $count(u) > 0 ? `Unspecified or inconsistent items : ${u.join(', ')}`:'' ;
}



// this a private function
async function _addActor(session:SessionEntity, body:CreateActorBody, roles:string[], authType:AuthType, c:PrismaContext) : Promise <ActorEntity> 
{
	if (!$ok(c.trx)) {
		throw new InternalError('_addActor() should be called inside a transaction') ;
	}

	let newActor: ActorCreateInput = {
		session:{
			connect:{
				id: session.id
			}
		},
		publicId: session.sessionNextPublicID(), // for now it's not an asyn fn here
		name: body.name,
		email: body.email,
		type: body.type,
		country: body.country,
		rolesArray: roles,
		authType: authType
	};

	if ($length(body.login)) {
		if (await session.hasActorWithLogin(<string>body.login, c)) {
			throw new ConflictError(`Trying to insert actor with same login '${body.login}' as others.`) ;
		} 
		newActor.login = body.login ;
	}

	if ($length(body['adm-id'])) newActor.administrativeCode = body['adm-id'] ;
	if ($length(body['first-name'])) newActor.firstName = body['first-name'] ;
	if ($ok(body['manifest-data'])) newActor.manifestData = stringifyPrisma(body['manifest-data']) ;
	if ($length(body.mobile)) newActor.mobile = body.mobile ;
	if ($ok(body['user-data'])) newActor.userData = stringifyPrisma(body['user-data']) ;
	
	session = <SessionEntity>await session.updateSession({
		lastPubObject:session.lastPubObject,
	}, c);
	const actorService = new ActorService();
	let actor = await actorService.insert(newActor, session.id, c);
	actor.setSession = session ; // we want our graph straight

	return actor ;
}

export const addActorToSession = async (auth:APIAuth, sessionPublicID:GlobalID, body:CreateActorBody) : Promise<ActorEntity> => {

	// we format the phone and if the result is empty it may issue an error
	let country = (<any>APICountries)[body.country.toUpperCase()] ;
	body.mobile = $phone(body.mobile, country?.dial) ;

	let message = _missingErrorsInActorBody(body) ;
	if ($length(message)) {
		throw new BadRequestError(`Impossible to add actor to session ${sessionPublicID}. ${message}.`);
	}
	
	if (body.type == ActorType.Entity && $length(body['first-name'])) { 
		throw new ConflictError("First name should not be used for actor's entities") ;
	}

	const el = $length(body.email) ;
	const result: boolean = emailRegex.test(body.email);
	if (!result) {
		throw new BadRequestError(`Email is invalid`);
	}
	if (el > apiGlobals.emailLength || el > Certigna.endPoint().emailSizeMax) {
		throw new BadRequestError(`Email of actor is too large (${el} characters).`);
	}

	const api = APIServer.api() ;

	let checkedRoles = api.checkRoles(body.roles, true, true, true, true) ;
	if (checkedRoles.nulls > 0) {
		throw new BadRequestError(`Impossible to add actor to session ${sessionPublicID}. Found empty roles.`);
	}
	if ($count(checkedRoles.rejecteds) > 0) {
		throw new BadRequestError(`Impossible to add actor to session ${sessionPublicID}. Roles ${checkedRoles.rejecteds.join(', ')} are not valid.`);
	}
	if (!$count(checkedRoles.roles)) {
		throw new BadRequestError(`Impossible to add actor to session ${sessionPublicID}. No roles are specified.`);
	}

	if (!api.verifyManifestData(body['manifest-data'], api.conf.actorManifestData)) {
		throw new ManifestDataError(`manifest-data did not match allowed keys.`);
	}

	let returnValue = undefined ;
	try {
		returnValue = await api.transaction(async trx => {
			const context = {trx:trx}
			let session = await sessionWithPublicID(auth, sessionPublicID, context) ;
			await checkSessionMutability(session) 
			return await _addActor(session, body, checkedRoles.roles, checkedRoles.authType, context) ;
		}) ;
	}
	catch (e) {
		// here we have a rollback
		APIServer.api().error(e);
		throw e ;
	}
	return returnValue ;
}