import {$count, $isobject, $keys, $ok, $unsigned, stringifyPrisma} from "../../utils/commons";

import {
	BadRequestError,
	ConflictError,
	DatabaseError,
	ForbiddenError,
	HTTPClientError,
	InternalError,
	NotFoundError
} from "../../utils/errors";

import {APIServer} from "../../server";
import {
	RoleType,
	ScenarioStatus,
	SignatureFormat,
	SignatureLevel,
	SignatureType,
	SigningProcess
} from "../APIConstants";
import {$lid, $url2lid, ActorIDs, DocumentIDs, LocalID} from "../APIIDs";
import {ScenarioBody, StepNode} from "../APIInterfaces";
import {addAutomatNode, Automat, newAutomat} from "../automat/automat";
import {Nullable} from "../../utils/types";
import {ScenarioEntity} from "../../entities/scenario";
import {DocumentService} from "../../services/document.service";
import {SessionEntity} from "../../entities/sessions";
import {DocumentEntity, IDocument} from "../../entities/documents";
import {LastFilesDictionary, PrismaContext} from "../../classes/interfaces/DBInterfaces";
import {ActorService} from "../../services/actor.service";
import {IActor} from "../../entities/actors";


export function missingErrorsInScenarioBody(api:APIServer, b:ScenarioBody) : [string, HTTPClientError|null]
{
	let u:string[] = [] ;
	let v:string[] = [] ;
	let format = $unsigned(b.format) ;
	let level = $unsigned(b.level) ;

	if (!format || !Object.values(SignatureFormat).includes(format)) { u.push('bad signature format') ; }
	if (!level || !Object.values(SignatureLevel).includes(level)) { u.push('bad signature level') ; }
	if (!$count(b.documents)) { u.push('documents') ; }
	if (!$count(b.steps)) { u.push('steps') ; }
	else {
		const signatureTypes = Object.values(SignatureType) ;
		b.steps.forEach((step, index) => {
			if (!$isobject(step)) { u.push(`bad step[${index}] definition`) ; }
			else {
				const roleType = api.roleType(step.process) ;

				// the signing process 'Sign' is only available for actors
				if (roleType === null || step.process === SigningProcess.Sign) { u.push(`bad step[${index}].process tag '${step.process}'`) ; }
				else if (roleType === undefined) { u.push(`step[${index}].process`) ; }

				if (roleType === RoleType.Signature) {
					let type = $unsigned(step.signatureType) ;
					if (!type || !signatureTypes.includes(type)) { u.push(`bad step[${index}].signatureType:${type}`) ; }
					if (((type == SignatureType.Detached || type == SignatureType.Envelopping) && format === SignatureFormat.PAdES) ||
					    (type == SignatureType.Envelopped && format === SignatureFormat.CAdES)) {
							v.push(`bad step[${index}].signatureType/format:${type}/${format}`) ;
					}
				}
				if (!$count(step.steps)) { u.push(`step[${index}].steps`) ; }
			}
		}) ;
	}

	if ($count(u)) { return [`Unspecified or inconcistent items : ${u.join(', ')}`, new BadRequestError()] ;}
	if ($count(v)) { return [`Conflicted items : ${v.join(', ')}`, new ConflictError()] ;}

	return ['', null];
}

// this function is meant to be used inside a database transaction
export const updatedCanceledScenario = async (scenario:ScenarioEntity, c:PrismaContext) : Promise<[ScenarioEntity, string[]]> =>
{

	if (!$ok(c.trx)) {
		throw new InternalError('updatedCanceledScenario() should be called inside a transaction with a valid api server') ;
	}

	const paths = await scenario.destroyScenarioFiles(true, c) ;
	let data = { ... scenario.getOtherData } ; // copy of the old scenario object data

	// we remove all trace of files
	// because when a scenario is cancelled
	// we SHOULD NOT download documents from it !
	data.generatedFiles = {} ;
	data.sourceFiles = {} ;
	data.originalLastFiles = {} ;

	let updatedScenario = await scenario.updateScenario({
		status:ScenarioStatus.Canceled,
		otherData: stringifyPrisma(data),
	}, c)
	return [<ScenarioEntity>updatedScenario, paths];
}

/*
	Use this method during a transaction
	on a terminated scenario which means
	that generatedFiles are empty
	and sourceFiles contains the future
	documents' last files.
	The scenario.session is loaded if not
	passed as parameter nor present in 
    our objects' graph
*/
export const fillDocumentsLastFiles = async (sessionParam:Nullable<SessionEntity>, scenario:ScenarioEntity, 
                                             files:LastFilesDictionary, c:PrismaContext) : Promise<DocumentEntity[]> =>
{
	// here we use the scenario sourceFiles in order to
	// populate the scenario documents last files
	let ret:DocumentEntity[] = [] ;
	const docService = new DocumentService();
	if ($ok(files)) {
		// const sourceFiles = <LastFilesDictionary>otherData.sourceFiles ;
		const session = $ok(sessionParam) ? sessionParam! : await scenario.mySession({trx:c.trx});
        for (const f of $keys(files)) {
           const did = $lid(f);
           if ((!did)){
               throw new NotFoundError(`Document with bad id '${f}')`) ;
           }
            const realDoc = await docService.sessionObjectWithPublicID<IDocument>(session, did, c) ;
            if (!$ok(realDoc)) { throw new NotFoundError(`Document with id (${session.publicId}, ${did}) not found`) ; }
            const scenarioUpdateObj = { lastFiles:[...files[f]] }
            const realDocEntity = new DocumentEntity(<IDocument>realDoc);
            const newDocument = await realDocEntity.modify({
                otherData: stringifyPrisma(scenarioUpdateObj)
            }, c)
           //WARNING: if otherData of document structure evolves, so should this
            if (!$ok(newDocument)) {
                throw new DatabaseError(`Impossible to update document with id (${session.publicId}, ${did})`) ;
            }
            ret.push(newDocument) ;
        }
	}
	return ret;
}

/**
 * this function is used on non-active session
 * to add or patch a scenario. It will verify if the given
 * documents carn be signed or approved in the manner describe
 * in steps and that the actor have not already signe or approved
 * one of these documents in the same manner that is described here.
 * This function assumes that session, documents and steps are OK
 * vars and well formed.
 * In the first version of the API, this functions returns a simple
 * linear structure for a linear automat
 */

export interface ScenarioInfos {
	aids:LocalID[] ;
	dids:LocalID[] ;
	automat:Automat ;
}

export async function validateScenarioSteps(
	api:APIServer,
	session:SessionEntity,
	documentURLs:string[],
	steps:StepNode[],
	c:PrismaContext
) : Promise<ScenarioInfos>
{
    let documentIDs = await _getDocumentsIdentifiers(c, session, documentURLs) ;
    let signedDocuments = new Map<LocalID, { approvers:Set<LocalID>, signers:Set<LocalID>, expeditors:Set<LocalID> }>() ;

    // put in there actors from already signed documents
	const otherData = session.getOtherData;
	if ($count(otherData?.signatures)) {
		otherData?.signatures?.forEach(sign => {
            let signedDocument = signedDocuments.get(sign.did) ;
            if (!$ok(signedDocument)) {
                signedDocument = { approvers:new Set<LocalID>(), signers:new Set<LocalID>(), expeditors:new Set<LocalID>()} ;
                signedDocuments.set(sign.did, signedDocument) ;
            }
			switch (sign.roleType) {
				case RoleType.Approval:
                    signedDocument!.approvers.add(sign.aid) ;
					break ;
				case RoleType.Signature:
                    signedDocument!.signers.add(sign.aid) ;
					break ;
				case RoleType.Expedition:
                    signedDocument!.expeditors.add(sign.aid) ;
			}
		})
	}

    // prepare for future signed documents
    documentIDs.forEach( did => {
        if (!signedDocuments.has(did)) {
            signedDocuments.set(did, { approvers:new Set<LocalID>(), signers:new Set<LocalID>(), expeditors:new Set<LocalID>()}) ;
        }
    })

    let actorsSet = new Set<LocalID>() ;
    let automat = newAutomat() ;
	let n = $count(steps) ;
	for (let i = 0 ; i < n ; i++) {
		const step = steps[i] ;
		const process = step.process ;
		const roleType = api.roleType(process) ;
		const orderApproval = step.orderApproval ;

		if (!$ok(roleType) || process === SigningProcess.Sign) {
			throw new BadRequestError(`bad step[${i}].process tag '${process}'`) ;
		}
        
        const stepActorIDs:ActorIDs = await _getActorsIdentifiers(actorsSet, c, session, step.steps, roleType!, process) ;

		// then we verify the step coherence itself
		const acount = $count(stepActorIDs) ;
		let card = 0 ;

		if (!$ok(step.cardinality)) {
			card = roleType === RoleType.Signature && process !== SigningProcess.Cosign ? acount : 1 ;
		}
		else {
			card = step.cardinality === 'one' ? 1 : (step.cardinality === 'all' ? acount : $unsigned(step.cardinality)) ;
		}

		if (!card || card > acount ||
			(card !== acount && (process === SigningProcess.Countersign || process === SigningProcess.OrderedCosign))) {
			throw new ConflictError(`bad step[${i}].cardinality (${card}) with process ${process}`) ;
		}

		switch (roleType) {
			case RoleType.Approval:

                documentIDs.forEach( did => {
                    const signedDoc = signedDocuments.get(did)! ;
                    if (signedDoc.signers.size) {
                        throw new ConflictError(`Cannot approve document with id (${session.publicId}, ${did}) because It is already signed.`);
					}
                    stepActorIDs.forEach( aid => {
                        if (signedDoc.approvers.has(aid)) {
                            throw new ConflictError(`Cannot approve document with id (${session.publicId}, ${did}) twice.`);
						}
                        signedDoc.approvers.add(aid) ;
					});
				});
				// the “ordered” value is read from config
				if($ok(orderApproval) && orderApproval){
					for (let j = 0 ; j < acount ; j++) {
						addAutomatNode(automat, {
							roleType:roleType,
							tag:process,
							aids:[stepActorIDs[j]],
							concernedActors:1,
							dids:documentIDs,
							stepIndex:i
						}) ;
					}
				}else {
					addAutomatNode(automat, {
						roleType:roleType,
						tag:process,
						aids:stepActorIDs,
						concernedActors:card,
						dids:documentIDs,
						stepIndex:i
					}) ;
				}

				break ;
			case RoleType.Signature:
                documentIDs.forEach( did => {
                    for (let aid of stepActorIDs) {
                        const signedDoc = signedDocuments.get(did)! ;
                        if (signedDoc.signers.has(aid)) {
                            throw new ConflictError(`Cannot sign document with id (${session.publicId}, ${did}) twice.`);
                        }
                        signedDoc.signers.add(aid) ;
					}
				});
				switch (process) {
					case SigningProcess.IndividualSign:
					case SigningProcess.Cosign:
						addAutomatNode(automat, {
							roleType:roleType,
							tag:process,
							aids:stepActorIDs,
							concernedActors:card,
							dids:documentIDs,
							stepIndex:i
						}) ;
						break ;
					case SigningProcess.Countersign:
					case SigningProcess.OrderedCosign:
						// here we have card === acount
						for (let j = 0 ; j < acount ; j++) {
							addAutomatNode(automat, {
								roleType:roleType,
								tag:process,
								aids:[stepActorIDs[j]],
								concernedActors:1,
								dids:documentIDs,
								stepIndex:i
							}) ;
						}
						break ;
					default:
						throw new BadRequestError(`Signing process ${process} not found.`);
				}
				break ;
			case RoleType.Expedition:
                documentIDs.forEach( did => {
                    const signedDoc = signedDocuments.get(did)! ;
                    if (signedDoc.signers.size == 0) {
                        throw new ConflictError(`Cannot send document with id (${session.publicId}, ${did}) if it's not signed.`);
					}

					for (let aid of stepActorIDs) {
                        if (signedDoc.expeditors.has(aid)) {
							throw new ConflictError(`Cannot send document with id (${session.publicId}, ${did}) twice.`);
						}
                        signedDoc.expeditors.add(aid) ;
					}
				});
				addAutomatNode(automat, {
					roleType:roleType,
					tag:process,
					aids:stepActorIDs,
					concernedActors:stepActorIDs.length,
					dids:documentIDs,
					stepIndex:i
				}) ;
				break ;
			default:
				throw new BadRequestError(`Bad signing process type.`);
			}
	}

	return {
		aids:Array.from(actorsSet),
		dids:documentIDs,
		automat:automat
	} ;
}

async function _getDocumentsIdentifiers(c:PrismaContext, session:SessionEntity, documentURLs:string[]):Promise<DocumentIDs> {
	let documentsSet = new Set<LocalID>() ;
    const documentService = new DocumentService();
    for (let url of documentURLs) {
		const did = $url2lid(url) ;
		if (documentsSet.has(did)) {
			throw new ConflictError(`Document with URL '${url}' cannot be added twice.`)
		}
		let realDoc = await documentService.sessionObjectWithPublicID<IDocument>(session, did, {trx:c.trx}) ;
		if (!$ok(realDoc)) {
			throw new NotFoundError(`Document with URL '${url}' not found`) ;
		}
		documentsSet.add(did) ;
	}
	return Array.from(documentsSet) ;
}

async function _getActorsIdentifiers(globalActorSet:Set<LocalID>, c:PrismaContext, session:SessionEntity, actorURLs:string[], roleType:RoleType, process:string):Promise<ActorIDs> {
	let actorSet = new Set<LocalID>() ;
    const actorService = new ActorService();
	for (let url of actorURLs) {
		const aid = $url2lid(url) ;
		if (actorSet.has(aid)) {
			throw new NotFoundError(`Actor with URL '${url}' is used twice `)  ;
		}
		let realActor = await actorService.sessionObjectWithPublicID<IActor>(session, aid, {trx:c.trx}) ;
		if (!$ok(realActor)) {
			throw new NotFoundError(`Actor with URL '${url}' not found`) ;
		}

		_verifyRoleRights(roleType, process, <string[]>(realActor?.rolesArray), url) ;

		actorSet.add(aid) ;
		globalActorSet.add(aid) ;
	}
	return Array.from(actorSet) ;
}

function _verifyRoleRights(roleType:Nullable<RoleType>, tag:string, roles:string[], user:string) {
	switch (roleType) {
		case RoleType.Approval:
			if (!roles.includes(SigningProcess.Approval) && !roles.includes(tag)) {
				throw new ForbiddenError(`Actor with URL '${user}' cannot aprove with tag '${tag}'`) ;
			}
			break ;
		case RoleType.Signature:
			if (!roles.includes(SigningProcess.Sign) && !roles.includes(tag)) {
				throw new ForbiddenError(`Actor with URL '${user}' cannot sign with tag '${tag}'`) ;
			}
			break ;
		case RoleType.Expedition:
			if (!roles.includes(tag)) {
				// TODO: may be we want just to check if the role is cc and to for now
				throw new ForbiddenError(`Actor with URL '${user}' cannot send with tag '${tag}'`) ;
			}
			break ;
		default:
			throw new BadRequestError(`Bad signing process type for tag ${tag}.`);
	}
}

