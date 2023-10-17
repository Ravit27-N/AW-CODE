import {
    $count,
    $isstring,
    $keys,
    $length,
    $now,
    $ok,
    $string,
    $unsigned,
    $value,
    $valueorundefine,
    stringifyPrisma
} from '../../utils/commons';
import {$removeFile} from '../../utils/fs';
import {$password, $uuid, HashMethods} from '../../utils/crypto';
import {$inspect} from '../../utils/utils';
import {
    BadRequestError,
    CertignaRequestError,
    ConflictError,
    DatabaseError,
    FileError,
    ForbiddenError,
    InternalError,
    NotFoundError
} from '../../utils/errors';

import {APIServer} from '../../server';
import {APIFileType, RoleType, ScenarioStatus, SessionStatus, SigningProcess} from '../APIConstants';
import {$url2gid, $url2lid, $urls2lids, DocumentIDs, GlobalID, LocalID} from '../APIIDs';
import {
    APIAuth,
    SessionApproveDocumentsBody,
    SessionCheckOTPBody,
    SessionDirectSignDocumentsBody,
    SessionOTPBody,
    SessionRecipientBody,
    SessionRefuseBody,
    SessionSignDocumentNode,
    SignatureResource,
    SigningResource,
    SigningVisualParameters,
    StepProcessingData
} from '../APIInterfaces';
import {automatCurrentNode, isAutomatAtEnd} from '../automat/automat';
import {sessionWithPublicID} from './sessionController';
import {
    certignaVisualParameters,
    checkOTPConformity,
    initiateSigningOrApprobation,
    SigningContext
} from './signatureCommons';
import {getSessionCertificate} from './certificateController';
import {Certigna} from '../../classes/CertignaEndPoint';
import {getSessionActorByID} from './actorController';
import {DocumentService} from "../../services/document.service";
import {DocumentEntity, IDocument} from "../../entities/documents";
import {OTPService} from "../../services/otp.service";
import {OTPCreateInput, OTPEntity} from "../../entities/otp";
import {IScenario, ScenarioEntity} from "../../entities/scenario";
import {FileService} from "../../services/file.service";
import {CaTokenEntity, ICaToken} from '../../entities/CATokens';
import {NO_CONTEXT} from '../../classes/interfaces/DBConstants';
import {
    LastFile,
    LastFilesDictionary,
    PrismaContext,
    ScenarioOtherData,
    SessionContextEvent,
    SessionContextEventType,
    SignedDocument,
    TokenOtherData
} from '../../classes/interfaces/DBInterfaces';
import {ScenarioService} from "../../services/scenario.service";
import {Nullable, UUID} from "../../utils/types";
import {copyLastFilesDictionary, destroyFileWithIdentifier, lastFilesRefenceSet} from "../../entities/BaseEntity";
import {CertificateEntity} from "../../entities/certificates";
import {FileCreateInput, FileEntity, IFile} from "../../entities/files";
import {fillDocumentsLastFiles} from "./scenarioCommons";


export const checkOTP = async (auth: APIAuth, sessionPublicID: GlobalID, body: SessionCheckOTPBody): Promise<boolean> => {
    let [token, session] = await checkOTPConformity(auth, sessionPublicID, body, NO_CONTEXT);
    let returnValue = undefined;
    const mustDelete = $ok(body.delete) && body.delete;
    const api = APIServer.api();
    try {
        returnValue = await api.transaction(async trx => {
            const context = {trx: trx};
            let scenario = await session.myActiveScenario(context);
            if (!$ok(scenario)) {
                throw new ForbiddenError(`There is no active scenario. So nothing to approve or sign.`)
            }

            let docs: DocumentIDs = $urls2lids(body.documents);
            let sessionOtherData = {...session.getOtherData};
            let event: SessionContextEvent = {
                user: auth.user,
                date: token.creationDate(),
                'event-type': SessionContextEventType.CheckOTP,
                'operation-id': $uuid(),
                token: token.otp
            }
            const aid = $url2lid(body.actor);
            if (aid) {
                event['actor-id'] = aid;
            }
            if (docs.length) {
                event['document-ids'] = docs;
            }
            if ($length(body.tag)) {
                event.tag = <string>(body.tag);
            }

            sessionOtherData.sessionContextEvents.push(event);
            await session.updateSession({
                otherData: stringifyPrisma(sessionOtherData)
            })

            if (mustDelete) {
                await token.delete(context);
            }
            return mustDelete;
        });
    } catch (e) {
        APIServer.api().error(e);
        throw e;
    }
    return $ok(returnValue) ? <boolean>returnValue : false;
}

export const generateOtp = async (auth: APIAuth, sessionPublicID: GlobalID, body: SessionOTPBody): Promise<OTPEntity> => {
    const otpService = new OTPService();
    const docService = new DocumentService();
    const len = $unsigned(body.length);
    if (!len || len > 256) {
        throw new NotFoundError(`OTP length ${len} not in range [1..256]`);
    }
    const api = APIServer.api();
    const letters = !body.numeric;
    const otp = $password(len, {hasNumeric: true, hasUppercase: letters, hasLowercase: letters});
    if ($length(otp) !== len) {
        throw new InternalError()
    }

    let returnValue = undefined;
    try {
        returnValue = await api.transaction(async trx => {
            const context = {trx: trx};
            let session = await sessionWithPublicID(auth, sessionPublicID, context);
            const api = APIServer.api();
            if (session.isClosed() || session.isExpired()) {
                throw new ForbiddenError(`Session ${sessionPublicID} is closed or already expired.`);
            }

            const aid = $url2lid(body.actor);
            let realActor = await getSessionActorByID(auth, session, aid, context);

            let documents: DocumentIDs = [];
            let documentsSet = new Set<LocalID>();
            let ttl = $unsigned(body.ttl);

            for (let url of body.documents) {
                const did = $url2lid(url);
                if (!did) {
                    throw new BadRequestError("One of the document url was malformed");
                }
                if (documentsSet.has(did)) {
                    throw new ConflictError(`Try to add the same document twice.`);
                }
                let realDoc = await docService.sessionObjectWithPublicID<IDocument>(session, did, context);
                if (!$ok(realDoc)) {
                    throw new NotFoundError(`Document with URL '${url}' not found`);
                }
                documents.push(did);
                documentsSet.add(did);
            }
            let otherData: TokenOtherData = {
                dids: documents
            }
            if ($length(body.tag)) {
                otherData.tag = <string>(body.tag);
            }

            const optInsert: OTPCreateInput = {
                actor: {
                    connect: {
                        id: realActor.id
                    }
                },
                ttl: ttl ? ttl : api.conf.otpTtl,
                otp: <string>otp,
                otherData: stringifyPrisma(otherData)
            }

            const token = await otpService.insert(optInsert, context)

            let sessionOtherData = session.getOtherData;

            let event: SessionContextEvent = {
                user: auth.user,
                date: token.creationDate(),
                'event-type': SessionContextEventType.GenerateOTP,
                'actor-id': aid,
                'operation-id': $uuid(),
                'document-ids': documents,
                token: <string>otp,
            }
            if ($length(body.tag)) {
                event.tag = <string>(body.tag);
            }


            sessionOtherData.sessionContextEvents.push(event);

            await session.updateSession(
                {
                    otherData: stringifyPrisma(sessionOtherData)
                },
                context
            );
            return token;
        });
    } catch (e) {
        APIServer.api().error(e);
        throw e;
    }

    return returnValue;
}

export const approveDocuments = async (
    auth: APIAuth, sessionPublicID: GlobalID, requestID: number | string, body: SessionApproveDocumentsBody
): Promise<SigningResource> => {

    const api = APIServer.api();

    // 4 things should be done :
    // 1) we have to update the automat scenario and may be its status and save it
    // 2) we need to add the "signature" (even if it's an approbation) in the session
    // 3) we need to add the approval event with its manifest-data in the event list of the session
    // 4) save the session
    let returnValue = undefined;
    try {
        returnValue = await api.transaction(async trx => {
            // WARNING: here context is an interface extending EditingContext
            const stepProcessingData: StepProcessingData = {
                processType: 'approve',
                manifestDefault: api.conf.approveManifestData,
                defaultTag: SigningProcess.Approval,
            }
            const stepProcess = await stepProcessing(
                api,
                auth,
                sessionPublicID,
                requestID,
                body,
                stepProcessingData,
                {trx}
            )

            return {
                threadId: stepProcess.threadId,
                signatures: stepProcess.signatures,
                otp: body.otp
            };
        });
    } catch (e) {
        api.error(e);
        throw e;
    }

    return <SigningResource>returnValue;
}

interface LocalDocumentNode {
    base64?: string,
    url: string,
    did: LocalID,
    parameters?: SigningVisualParameters
}

/**
 *
 * certificateURL is the URL of a previous generated certificate or undefined or null.
 * if this parameter is not set, we try to use internal server certificate associated
 * to the authentication to sign the given document
 *
 */
export const signDocuments = async (
    auth: APIAuth,
    sessionPublicID: number,
    requestID: number | string,
    body: SessionDirectSignDocumentsBody,
    certificateURL: Nullable<string> = undefined
): Promise<SigningResource> => {
    let returnValue = undefined;
    const api = APIServer.api();
    let localDocuments: LocalDocumentNode[] = [];

    // (Pre initalization) : we first normalize our documents
    for (let element of body.documents) {
        let node: LocalDocumentNode;
        if ($isstring(element)) {
            node = {
                url: <string>element,
                did: $url2lid(<string>element)
            }
        } else {
            const u = (<SessionSignDocumentNode>element)['document-url'];
            node = {
                base64: (<SessionSignDocumentNode>element).base64,
                url: u,
                did: $url2lid(u),
                parameters: (<SessionSignDocumentNode>element)['visual-parameters']
            }
        }
        if (node.did === 0) {
            throw new BadRequestError('Bad document id to be signed');
        }
        localDocuments.push(node);
    }

    // here we verify our parameters and calculate the new automat step (if it's possible)
    const checkedBody = {
        actor: body.actor,
        documents: localDocuments.map(node => node.url),
        'manifest-data': body['manifest-data'],
        otp: $length(body.otp) ? <string>(body.otp) : '',
        tag: body.tag
    }

    let newCreatedFilePaths: string[] = []; // these files will be destroyed if our operation fails

    try {
        let toBeDestroyedFilesPaths: string[] = [];  // those files will be destroid if the transaction is OK 
        returnValue = await api.transaction(async trx => {

            // =======================================================================================
            // (INIT 1) establilshing the working context
            // =======================================================================================

            // the returned context of the next function is a subclass of EditingContext
            // this function also check the OTP if there's one available in our request body
            // and does provoque an au
            let context = await initiateSigningOrApprobation(api, auth, {trx}, sessionPublicID,
                checkedBody, 'sign', api.conf.signatureManifestData); // no default tag specified here

            // we register our genuine document files we don't want to destroy during the incoming process
            const noDestructionFileIDReferenceSet = lastFilesRefenceSet(context.scenario.getOtherData.originalLastFiles);

            // =======================================================================================
            // (INIT 2) checking if we have a certificate and if everything is valid is there's one
            // =======================================================================================

            let certificate: Nullable<CertificateEntity> = undefined;
            let token: Nullable<CaTokenEntity> = undefined;

            // then we get our certificate if we use a generated previous certificate
            if ($length(certificateURL) > 0) {
                api.log(`will load certificate with ID (${sessionPublicID}, ${$url2gid(certificateURL!)})`)
                certificate = await getSessionCertificate(
                    auth,
                    sessionPublicID,
                    $url2gid(certificateURL),
                    {trx: trx}
                );

                // loading the token and the actor associated to the certificate
                if (!$ok(certificate) || !$length(certificate?.getCertificateData?.password) || !$length(certificate?.getCertificateData?.data)) {
                    throw new ConflictError("Impossible to retrieves certificate or certificate's internal data");
                }
                let getToken = await certificate.getRelated<ICaToken>({
                    caToken: {
                        include: {
                            actor: true
                        }
                    }
                }, context);
                if (!$ok(getToken)) {
                    throw new ConflictError("Impossible to retrieves certificate's token");
                }
                token = new CaTokenEntity(<ICaToken>getToken);
                const actorEntity = token.Actor;
                if (actorEntity!.publicId !== context.aid) {
                    throw new ConflictError('Impossible to use given certificate URL with this actor');
                }
                api.log(`Found certificate = ${$inspect(certificate)}`)
            } else {
                api.log(`will use server user's registered certificate for signing`)
            }

            // =======================================================================================
            // (INIT 3) constructing the base of our new scenario other data
            // =======================================================================================
            const newScenarioData: ScenarioOtherData = {
                // we take a shallow copy because anly the automaton, 
                // the sourceFiles and the generated may change 
                ...context.scenario.getOtherData,
                automat: context.nextAutomat,
                generatedFiles: copyLastFilesDictionary(context.scenario.getOtherData.generatedFiles), // this one will evolve   		 
                sourceFiles: copyLastFilesDictionary(context.scenario.getOtherData.sourceFiles)
            };

            // =======================================================================================
            // (INIT 4) preparing and checking our future signature requests
            // =======================================================================================
            const previousAutomat = context.scenario.getOtherData.automat;
            const previousCurrentNode = automatCurrentNode(previousAutomat);
            if (!$ok(previousCurrentNode)) {
                throw new ConflictError(`Bad automat node for signing documents.`);
            }
            const previousStepIndex = previousCurrentNode!.stepIndex;
            const previousStep = context.scenario.getStepsDefinition[previousStepIndex];

            let futureRequestBase = {
                format: context.scenario.signatureFormat,
                login: auth.user,
                password: auth.password,
                hashMethod: HashMethods.SHA256,
                level: context.scenario.signatureLevel,
                type: previousStep.signatureType,

                // if we use a generated certificate, we add the certificate in all signature requests
                certificateBase64Data: $ok(certificate) ? certificate!.getCertificateData.data : undefined,
                certificatePwd: $ok(certificate) ? certificate!.getCertificateData.password : undefined
            };

            const certigna = Certigna.endPoint();

            // we need to verify that the future signature request
            // on certigna enpoint will be OK with dummy fileName

            certigna.verifySignatureRequest({...futureRequestBase, fileName: 'dummy.pdf'});

            // =======================================================================================
            // (ACTION 1) Signing all documents
            // =======================================================================================
            // files you have to sign are allways contained in the sourceFiles dictionary
            const now = $now(); // same signature time stamp for all the documents
            const operationID = $uuid();
            const localSourceFiles = $value(context.scenario.getOtherData.sourceFiles, {});
            const generatedSignatures: SignedDocument[] = [];
            const documentsSignaturesResponses: SignatureResource[] = [];
            const isIndividualSign = previousCurrentNode!.tag === SigningProcess.IndividualSign;

            const documentService = new DocumentService();
            for (let documentDefinition of localDocuments) {
                const documentID = documentDefinition.did;
                const filesToSign = localSourceFiles[$string(documentID)];
                if (!$count(filesToSign)) {
                    throw new ConflictError(`No files to sign for document/actor IDs (${sessionPublicID},${documentDefinition.did}/${context.aid})`);
                }
                const futureDocumentRequestBase = $ok(documentDefinition.parameters) ?
                    {
                        ...futureRequestBase,
                        visibleSignatureParameters: certignaVisualParameters(<SigningVisualParameters>documentDefinition.parameters)
                    } :
                    {...futureRequestBase};
                const documentSignatureID = $uuid();
                const docstringID = $string(documentID);
                let generatedFiles = newScenarioData.generatedFiles![docstringID];
                if (!$ok(generatedFiles)) {
                    generatedFiles = [];
                    newScenarioData.generatedFiles![docstringID] = generatedFiles;
                }

                const fileService = new FileService();
                // we sign each file by calling the Certigna's endPoint
                // fileToSign of document is only one, if have multiple please check with document's base64 contents
                for (let f of filesToSign) {
                    const file = await fileService.findFirst<IFile>({
                        where: {
                            id: f.fileId
                        }
                    });
                    if (!$ok(file)) {
                        throw new NotFoundError(`Source file ${f.fileId} of document with ID (${sessionPublicID},${documentID}) is not found`)
                    }
                    let signContent = $ok(documentDefinition.base64) ? <string>documentDefinition.base64 : file!.path;
                    let response = await certigna.signDocument(signContent, {
                        fileName: file!.fileName,
                        ...futureDocumentRequestBase
                    })
                    if (!$length(response)) {
                        throw new CertignaRequestError('Signature endpoint error');
                    }

                    // saving the signed file and its sealPath on disk
                    const signedFileName = `${$uuid()}.pdf`;
                    const newFileStruct = await FileEntity.fileWithBuffer(auth, response!, api.conf.storagePath, now, signedFileName, APIFileType.PDF);
                    if (!$ok(newFileStruct)) {
                        throw new FileError(`Impossible to create new signed file on disk for document with ID (${sessionPublicID},${documentID}).`);
                    }

                    // we register the created files in case of the whole procedure fails and we need to delete them
                    newCreatedFilePaths.push(newFileStruct!.path);
                    newCreatedFilePaths.push(newFileStruct!.sealPath!);

                    const fileRef = await fileService.insert(<FileCreateInput>newFileStruct);
                    if (!$ok(fileRef)) {
                        throw new DatabaseError(`Impossible to create new signed file reference in database for document with ID (${sessionPublicID},${documentID}).`);
                    }

                    let realDoc = await documentService.sessionObjectWithPublicID<IDocument>(context.session, documentID, {trx});
                    if (!$ok(realDoc)) {
                        throw new NotFoundError(`Document with id (${sessionPublicID}, ${documentID}) not found`)
                    }

                    // registering our signed document in our generated files
                    let generatedLastFile: LastFile = {
                        fileId: fileRef.id
                    };
                    if (isIndividualSign) {
                        // in a context of individual signature, we register the signer in the generated file.
                        generatedLastFile.aid = context.aid;
                    }
                    // Update the last document to signed file
                    // At the currently we update the lastFiles of document to LastFile,
                    const realDocEntity = new DocumentEntity(<IDocument>realDoc);
                    const documentLastFiles = <LastFilesDictionary>{lastFiles: [generatedLastFile]}
                    await realDocEntity.modify({
                        otherData: stringifyPrisma(documentLastFiles)
                    }, {trx});

                    generatedFiles.push(generatedLastFile);

                    // register our signature
                    generatedSignatures.push({
                        tag: context.tag,
                        did: documentID,
                        aid: context.aid,
                        date: now,
                        dsigid: documentSignatureID,
                        sigid: $uuid(),
                        threadid: operationID,
                        roleType: RoleType.Signature,
                        otp: body.otp,
                        requesId: `${requestID}`
                    });
                }

                // register our documents' signatures response
                documentsSignaturesResponses.push({
                    tag: context.tag,
                    signatureId: documentSignatureID,
                    actor: api.url('session', sessionPublicID, 'actor', context.aid),
                    document: api.url('session', sessionPublicID, 'document', documentID)
                });
            }

            // here we did sign all the documents' files we needed to sign.

            // =======================================================================================
            // (ACTION 2) Manage generated files and source files
            // =======================================================================================

            const nextAutomat = context.nextAutomat;
            const nextCurrentNode = automatCurrentNode(nextAutomat);

            if (!isIndividualSign || nextCurrentNode?.stepIndex !== previousStepIndex) {
                // we are not in an individual-sign operation or we just finished a complete individual-sign node
                // we need to replace the source files of our documents by the generated files
                const sourceFiles = newScenarioData.sourceFiles!;
                const keys = $keys(newScenarioData.generatedFiles);
                for (let key of keys) {
                    for (const f of sourceFiles[key]) {
                        const paths = await destroyFileWithIdentifier(f.fileId, context, noDestructionFileIDReferenceSet);
                        toBeDestroyedFilesPaths = [...toBeDestroyedFilesPaths, ...paths];
                    }
                    sourceFiles[key] = [...newScenarioData.generatedFiles![key]];
                }
                newScenarioData.generatedFiles = {};
            }
            // else {
            // We did make an action in an individualSign node which is not completed. 
            // We keep the same source files and our generatedFiles dictionary may grow
            // So we keep our newScenarioData.sourceFiles and newScenarioData.generatedFiles as they are
            // }

            // =======================================================================================
            // (ACTION 3) Update scenario, documents and session
            // =======================================================================================
            const isScenarioFinished = !$ok(nextCurrentNode);

            if (isScenarioFinished) {
                await fillDocumentsLastFiles(context.session, context.scenario, newScenarioData.sourceFiles!, context);
            }


            let updatedScenario = await context.scenario.updateScenario({
                otherData: stringifyPrisma(newScenarioData),
                status: isScenarioFinished ? ScenarioStatus.WellTerminated : context.scenario.status
            }, {trx});
            updatedScenario!.setSession = context.session; // strait ORM graph


            // here we modify and save the session
            let otherData = {...context.session.getOtherData};
            if (!$ok(otherData.sessionContextEvents)) {
                otherData.sessionContextEvents = [];
            }

            otherData.sessionContextEvents.push({
                user: auth.user,
                date: now,
                'event-type': SessionContextEventType.SignDocuments,
                'actor-id': context.aid,
                'scenario-id': context.scenario.publicId,
                'operation-id': operationID,
                'document-ids': context.dids,
                'manifest-data': context.manifestData,
                token: body.otp
            });

            if (!$ok(otherData.signatures)) {
                otherData.signatures = generatedSignatures;
            } else {
                otherData.signatures = [...otherData.signatures!, ...generatedSignatures];
            }

            await context.session.updateSession({
                otherData: stringifyPrisma(otherData),
                status: isScenarioFinished ? SessionStatus.Idle : context.session.status
            }, {trx});

            return {
                threadId: operationID,
                signatures: documentsSignaturesResponses,
                otp: body.otp,
                token: $valueorundefine(token?.token)
            };

        });

        newCreatedFilePaths = []; // transaction is OK, no files to be removed
        toBeDestroyedFilesPaths.forEach(p => $removeFile(p)); // files are removed only if the transaction is OK
    } catch (e) {
        APIServer.api().error(e);

        newCreatedFilePaths.forEach(f => $removeFile(f));
        throw e;

    }
    return <SigningResource>returnValue;

}

export const recipient = async (
    auth: APIAuth, sessionPublicID: GlobalID, requestID: number | string, body: SessionRecipientBody
): Promise<SigningResource> => {
    const api = APIServer.api();

    let returnValue = undefined;
    try {
        returnValue = await api.transaction(async trx => {
            const stepProcessingData: StepProcessingData = {
                processType: 'recipient',
                // Todo: make recipientManifestData
                manifestDefault: api.conf.approveManifestData,
                defaultTag: SigningProcess.To,
            }
            const stepProcess = await stepProcessing(
                api,
                auth,
                sessionPublicID,
                requestID,
                body,
                stepProcessingData,
                {trx}
            )

            return {
                threadId: stepProcess.threadId,
                signatures: stepProcess.signatures,
                otp: body.otp
            };
        });
    } catch (e) {
        api.error(e);
        throw e;
    }

    return <SigningResource>returnValue;
}

export const refuse = async (
    auth: APIAuth, sessionPublicID: GlobalID, requestID: number | string, body: SessionRefuseBody
): Promise<SigningResource> => {
    const api = APIServer.api();

    let returnValue = undefined;
    try {
        returnValue = await api.transaction(async trx => {
            const stepProcessingData: StepProcessingData = {
                processType: 'refuse',
                manifestDefault: api.conf.approveManifestData,
                defaultTag: SigningProcess.Refuse,
            }
            // Refuse on the document with tags: approval, cosign...

            let context = await initiateSigningOrApprobation(
                api, auth, {trx: trx}, sessionPublicID,
                body, stepProcessingData.processType, stepProcessingData.manifestDefault,
                stepProcessingData.defaultTag
            );
            let scenarioFinished = isAutomatAtEnd(context.nextAutomat);
            const sessionStatus = scenarioFinished ? SessionStatus.Idle : <number>context.session.status;
            // Update the session
            const sessionSignatures = await _updateSession(api, auth, sessionPublicID,
                requestID, body, context, sessionStatus, SessionContextEventType.RefuseDocuments);

            return {
                threadId: sessionSignatures.threadId,
                signatures: sessionSignatures.signatures,
                otp: body.otp
            };
        });
    } catch (e) {
        api.error(e);
        throw e;
    }

    return <SigningResource>returnValue;
}


export const stepProcessing = async (
    api: APIServer,
    auth: APIAuth,
    sessionPublicID: GlobalID,
    requestID: number | string,
    body: SessionRecipientBody,
    stepProcessingData: StepProcessingData,
    c: PrismaContext
): Promise<{ threadId: UUID, signatures: SignatureResource[] }> => {
    let context = await initiateSigningOrApprobation(
        api, auth, {trx: c.trx}, sessionPublicID,
        body, stepProcessingData.processType, stepProcessingData.manifestDefault,
        stepProcessingData.defaultTag
    );
    let patchedScenarioData = {...context.scenario.getOtherData};
    patchedScenarioData.automat = context.nextAutomat;
    let scenarioFinished = isAutomatAtEnd(patchedScenarioData.automat);
    const scenarioSerivce = new ScenarioService();

    // ============= 1) update the scenario
    // in approbation mode we don't update the files
    const updatedScenario = await scenarioSerivce.update<IScenario>({
        data: {
            otherData: stringifyPrisma(patchedScenarioData),
            status: scenarioFinished ? ScenarioStatus.WellTerminated : context.scenario.status
        },
        where: {
            id: context.scenario.id
        }
    });
    const updatedScenarioEntity = new ScenarioEntity(updatedScenario);
    updatedScenarioEntity.setSession = context.session;
    const sessionStatus = scenarioFinished ? SessionStatus.Idle : <number>context.session.status;
    const sessionEventType = stepProcessingData.processType == 'approve' ?
        SessionContextEventType.ApproveDocuments
        : SessionContextEventType.ReceiveDocuments;
    const sessionSignatures = await _updateSession(api, auth, sessionPublicID, requestID,
        body, context, sessionStatus, sessionEventType);
    return {
        threadId: sessionSignatures.threadId,
        signatures: sessionSignatures.signatures
    }
}

const _updateSession = async (api: APIServer,
                              auth: APIAuth,
                              sessionPublicID: GlobalID,
                              requestID: number | string,
                              body: SessionRefuseBody,
                              context: SigningContext,
                              scenarioStatus: ScenarioStatus,
                              sessionContextEventType: SessionContextEventType): Promise<{
    threadId: UUID,
    signatures: SignatureResource[]
}> => {
    let now = $now();

    let otherData = {...context.session.getOtherData};

    // ============= 2) add the signatures objects in the session
    const actorURL = api.url('session', sessionPublicID, 'actor', context.aid);
    const operationID = $uuid();
    if (!$ok(otherData.signatures)) {
        otherData.signatures = [];
    }
    const signatures = <SignedDocument[]>(otherData.signatures);

    const signaturesResponses: SignatureResource[] = [];

    await Promise.all(context.dids.map(async (did) => {
        const signatureID = $uuid();
        const signature: SignedDocument = {
            tag: context.tag,
            did: did,
            aid: context.aid,
            date: now,
            dsigid: signatureID,
            sigid: signatureID,
            threadid: operationID,
            roleType: RoleType.Expedition,
            otp: body.otp,
            requesId: `${requestID}`
        };
        signatures.push(signature);

        const signatureResource: SignatureResource = {
            tag: context.tag,
            signatureId: signatureID,
            actor: actorURL,
            document: api.url('session', sessionPublicID, 'document', did)
        }
        signaturesResponses.push(signatureResource);
    }));

    // ============= 3) add the approval event in the session
    otherData.sessionContextEvents.push({
        user: auth.user,
        date: now,
        'event-type': sessionContextEventType,
        'actor-id': context.aid,
        'scenario-id': context.scenario.publicId,
        'operation-id': operationID,
        'document-ids': context.dids,
        'manifest-data': context.manifestData,
        reason: body.reason,
        token: body.otp
    });

    // ============= 4) save the session
    await context.session.updateSession(
        {
            otherData: stringifyPrisma(otherData),
            status: scenarioStatus
        },
        context
    );
    return {
        threadId: operationID,
        signatures: signaturesResponses
    }
}