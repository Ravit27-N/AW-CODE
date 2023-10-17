import {$now, $ok, stringifyPrisma} from "../../utils/commons";
import {APIAuth, SessionApproveDocumentsBody, SignatureResource, SigningResource} from "../APIInterfaces";
import {RoleType, ScenarioStatus, SessionStatus, SigningProcess} from "../APIConstants";
import {APIServer} from "../../server";

import {isAutomatAtEnd} from "../automat/automat";
import {initiateSigningOrApprobation} from "./signatureCommons";
import {SessionContextEventType, SignedDocument} from "../../classes/interfaces/DBInterfaces";
import {$uuid} from "../../utils/crypto";

export const approveDocuments = async (auth:APIAuth, sessionPublicID:number, requestID:number|string, body:SessionApproveDocumentsBody) : Promise<SigningResource> => {
    const api = APIServer.api() ;
    // 4 things should be done :
    // 1) we have to update the automat scenario and may be its status and save it
    // 2) we need to add the "signature" (even if it's an approbation) in the session
    // 3) we need to add the approval event with its manifest-data in the event list of the session
    // 4) save the session
    let returnValue = undefined ;
    try {
        returnValue = await api.transaction(async trx => {
            // WARNING: here context is an interface extending EditingContext
            const context = await initiateSigningOrApprobation(api, auth, {trx}, sessionPublicID, 
                body, 'approve', api.conf.approveManifestData, SigningProcess.Approval) ;
            const patchedScenarioData = {
                // we take a shallow copy because anly the automaton changes and the generatedFiles may change
                ... context.scenario.getOtherData,
                automat: context.nextAutomat,
                generatedFiles: {}
            } ;

            // here we can finish a scenario with just approbation in it
            // QUESTION: we have to make sure that its 
            let scenarioFinished = isAutomatAtEnd(patchedScenarioData.automat) ;

            // ============= 1) update the scenario
            // in approbation mode we don't update any files
            let updatedScenario = await context.scenario.updateScenario({
                otherData: stringifyPrisma(patchedScenarioData),
                status:scenarioFinished ? ScenarioStatus.WellTerminated : context.scenario.status
            }, {trx: context.trx}) ;

            updatedScenario!.setSession = context.session; // make our ORM's graph straight
            let now = $now() ;

            let otherData = { ...context.session.getOtherData } ;

            // ============= 2) add the signatures objects in the session
            const actorURL = api.url('session', sessionPublicID, 'actor', context.aid) ;
            const operationID = $uuid() ;
            if (!$ok(otherData.signatures)) { otherData.signatures = [] ; }
            let signatures = <SignedDocument[]>(otherData.signatures) ;
            const signaturesResponses:SignatureResource[] = [] ;

            context.dids.forEach(did => {
                const approbationID = $uuid() ;
                const signature = {
                    tag:context.tag,
                    did:did,                    // document identifier
                    aid:context.aid,            // actor identifier
                    date:now,
                    dsigid:approbationID,
                    sigid:approbationID,
                    threadid:operationID,       // all documents are approved in a same operation
                    roleType:RoleType.Approval,
                    otp:body.otp,               // the OTP used for the signature
                    requesId:`${requestID}`
                } ;
                signatures.push(signature) ;
                signaturesResponses.push({
                    tag:context.tag,
                    signatureId:approbationID,
                    actor:actorURL,
                    document:api.url('session', sessionPublicID, 'document', did)
                }) ;
            }) ;

            // ============= 3) add the approval event in the session
            otherData.sessionContextEvents.push({
                user:auth.user,
                date:now,
                'event-type':SessionContextEventType.ApproveDocuments,
                'actor-id':context.aid,
                'scenario-id':context.scenario.publicId,
                'operation-id':operationID,
                'document-ids':context.dids, 	// identifiers of all approved documents
                'manifest-data':context.manifestData,
                token:body.otp
            }) ;

            // ============= 4) save the session
            await context.session.updateSession({
                otherData: stringifyPrisma(otherData),
                status: scenarioFinished ? SessionStatus.Idle : context.session.status
            }, {trx}) ;

            return {
                threadId:operationID,
                signatures:signaturesResponses,
                otp:body.otp
            } ;
        }) ;
    }
    catch (e) {
        api.error(e);
        throw e ;
    }

    return <SigningResource>returnValue ;
}

