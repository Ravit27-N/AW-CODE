import { $random, $uuid } from "../utils/crypto";
import { $absolute, $createDirectory, $filename, $isdirectory, $isfile, $isreadable, $path, $readBuffer, $withoutext, $writeBuffer } from "../utils/fs";
import { TSDate } from "../utils/tsdate";
import { TSFusionTemplate } from "../utils/tsfusion";
import { TSTest } from "../utils/tstester";
import { UINT32_MAX } from "../utils/types";
import { $inspect } from "../utils/utils";
import { $identifier } from "../utils/commons";
import { ManifestData } from "../api/APIInterfaces";
import { $length } from "../utils/commons";

// Manifest partial database types

interface PartialID {
    publicId:number ;
    identifier:string ;
}

interface PartialFile extends PartialID {
    fileName:string;
}

interface PartialSession extends PartialID {
    created_at: string ;
    expires_at: string ;
	status: number ;
	ttl:number ;
	user: string ;
}

interface PartialSessionID extends PartialID {
    sessionId:number ;
}

interface PartialActor extends PartialSessionID {
    session:PartialSession ;
    publicId:number ;
    sessionId:number ;
    country: string ;
	email: string ;
	mobile?: string ;
	firstName?: string  ;
	login: string ;
	name: string ;
	type: number ;

}

interface PartialDocument extends PartialSessionID {
    session:PartialSession ;
    abstract?: string ;
	genuineFileId: number ;
	fileName: string ;
	title: string  ;
}

interface PartialCertificate extends PartialSessionID {
	caTokenId: number ;
    ttl:number,
    expires_at:string,
    user?:string ;
    status:number ;
    certificateData:PartialCertificateJSONData
}

interface PartialCertificateJSONData {
    aki?: string ;
    countryName:string ;
    givenName:string ;
    id:string ;
    lifespan:number ;
    notAfter:string ;
    notBefore:string ;
    organizationName?:string ;
    organizationUnitName?:string ;
    password:string ;
    serialnumber:string ;
    surname:string ;
    ski?:string ;
}

// Manifest Partial Types
interface PartialEventDocument {
    fileName:string,
	identifier:string,
    document:PartialDocument
}

interface PartialEvent {
	user:string ;
	event:string ;
	date:TSDate ;
	scenarioId:string ;
	reason:string ;
	actorName:string ;
	actorIdentifier:string ;
	hasActor:boolean ;
	documents:PartialEventDocument[] ;
	operationId:string ;
	manifestData?:ManifestData ;
	token:string ;
	actor?:PartialActor ;
}

interface PartialHolder {
	identifier:string
    signatureDate:TSDate ; // last signature date
    signatures:PartialSignatureData[] ;
}

interface PartialCertificateData extends PartialHolder {
    expirationDate:TSDate ;
    certificate:PartialCertificate ;
}

interface PartialDocumentData extends PartialHolder {
    creationDate:TSDate ;
	fileName:string,
	document:PartialDocument,
}


interface PartialActorData extends PartialHolder {
    name:string ;
    actor:PartialActor ;
}

interface PartialSignatureData {
    date:TSDate ;                  // signature date
    tag:string ;		           // process
    role:string ;                  // process role
    isSignature:boolean ;          // is a signature process
	document:PartialDocument ;     // real document object
    actor:PartialActor ;                  // real actor object
    certificate:PartialCertificate ;      // real certificate object
    actorIdentifier:string ;       // actor identifier
    documentIdentifier:string ;    // document identifier
    certificateIdentifier:string ; // certificate identifier
    identifier:string ;            // document signature ID
    fileIdentifier:string ;        // file signature ID
    threadIdentifier:string ;      // thread identifier
    format:string ;                // PAdES, XAdES or CAdES or '' (for unknown or irrelevant)
    level:string ;                 // B, L, LT, LTA, ''
    type:string ;                  // envelopped, envelopiing and detached (or '')
    token:string ;                 // signature used OTP
}

interface PartialSourceData {
    creationDate:TSDate ;                    // session creation date
    status:string ;                          // session status
    type:string ;                            // session process type
    signatureDate:TSDate ;                   // date of last signature
    templateName:string ;                    // template name
    identifier:string ;                      // session identifier
	date:TSDate,						    // manifest creation date
	fileName:string, 					    // manifest file name
	reference:string,					    // manifest reference
	documents:PartialDocumentData[],       // all approved and signed documents' infos
	actors:PartialActorData[],			    // all approbators and signers
    certificates:PartialCertificateData[],  // all used certificates
    signatures:PartialSignatureData[],     // all signatures
	events:PartialEvent[]				    // all the events
}

function createDBObject<T extends PartialID>():T {
    const id = $random(UINT32_MAX) ;
    return {publicId: id, identifier: $identifier(id)} as unknown as T ;
}

function createSessionObject<T extends PartialSessionID>(sessionObject:PartialSession):T {
    const id = $random(UINT32_MAX) ;
    return {publicId: id, identifier: `${sessionObject.identifier}-${$identifier(id)}`} as unknown as T;
}

export const manifestTemplate = TSTest.group("Manifest format static test", async (group) => {

    const glob = {} ;
    const date =  new TSDate() ;
    const start = date.dateByAdding(-1) ;
    const end = date.dateByAdding(2) ;

    const fileref = createDBObject<PartialFile>() ;
    fileref.fileName = "myFile.pdf" ;

    const session = createDBObject<PartialSession>() ;
    const creation = date.dateByAddingDays(-7) ;
    const expiration = date.dateByAdding(0, 1) ;
    session.created_at = creation.toISOString() ;
    session.user = "loginone" ;
    session.expires_at = expiration.toISOString() ;
    session.ttl = expiration.timeSinceDate(creation) ;
    session.status = 1 ;

    const actor = createSessionObject<PartialActor>(session) ;
    actor.session = session ;
    actor.name = "DURAND" ;
    actor.firstName = "Francis" ;
    actor.email = "francis.durand@mymail.com" ;
    actor.country = "FR" ;
    actor.login = "fdurand" ;
    actor.type = 0 ;
    actor.mobile = "+33 7 44 99 70 00"

    const document = createSessionObject<PartialDocument>(session) ;
    document.session = session ;
    document.title = "Mon beau document" ;
    document.fileName = fileref.fileName ;
    document.abstract = "This is a document about testing the fusion template" ;

    const certificate = createSessionObject<PartialCertificate>(session) ;
    certificate.caTokenId = $random(UINT32_MAX) ;
    certificate.expires_at = end.toISOString() ;
    certificate.status = 1 ;
    certificate.ttl = end.timeSinceDate(date) ;
    certificate.user = session.user ;
    certificate.certificateData = {
        aki:$uuid(),
        countryName:"France",
        givenName:actor.name,
        id:$uuid(),
        lifespan:end.timeSinceDate(start),
        notAfter:end.toISOString(),
        notBefore:start.toISOString(),
        password:"1234",
        serialnumber:$uuid(),
        surname:actor.firstName,
        ski:$uuid()
    }
    const signatureID = $random(UINT32_MAX) ;
    const signature:PartialSignatureData = {
        date:date,
        role:"signature",
        isSignature:true,
        tag:"sign",
        format:"PAdES",
        certificate:certificate,
        document:document,     // real document object
        actor:actor,                  // real actor object
        certificateIdentifier:certificate.identifier,
        documentIdentifier:document.identifier,
        actorIdentifier:actor.identifier,
        fileIdentifier:fileref.identifier,
        identifier:`${session.identifier}-${$identifier(signatureID)}`,
        threadIdentifier:$uuid(),
        type:"enveloping",
        token:"123Z78",
        level:"LT"
    } ;
    const actorName = `${actor.firstName} ${actor.name}` ;
    const fusionData:PartialSourceData = {
        creationDate:creation,
        status:"correctement signé et clos",
        type:"signature uniquement",
        signatureDate:date,
        templateName:"MyTemplate",
        identifier:session.identifier,
        date:date,				            // manifest creation date
        fileName:`${$uuid()}.pdf`, 		    // manifest file name
        reference:$uuid(),					// manifest reference
        actors:[{
            identifier:actor.identifier,
            name:actorName,
            actor:actor,
            signatureDate:date,
            signatures:[signature]
        }],
        documents:[{
            creationDate:creation,
            identifier:document.identifier,
            signatureDate:date,
            signatures:[signature],
            document:document,
            fileName:document.fileName
        }],
        signatures:[
            signature
        ],
        certificates:[{
            expirationDate:end,
            certificate:certificate,
            identifier:certificate.identifier,
            signatureDate:date,
            signatures:[signature]
        }],
        events:[
            {
                user:session.user,
                event:"creating session",
                date:creation,
                scenarioId:$uuid(),
                reason:"I like to create a session",
                actorName: actorName,
                actorIdentifier:actor.identifier,
                documents:[],
                hasActor:true,
                operationId:$uuid(),
                token:"",
                actor:actor
            },
            {
                user:session.user,
                event:"uploading document",
                date:creation,
                scenarioId:$uuid(),
                reason:'',
                actorName: actorName,
                actorIdentifier:actor.identifier,
                hasActor:true,
                documents:[{
                    fileName:document.fileName,
                    identifier:document.identifier,
                    document:document
                }],
                operationId:$uuid(),
                token:"THISISMYTOKEN",
                actor:actor

            }
        ]
    }

    group.unary("Manifest Template", async (t) => {
        const path = $absolute(process.env.MANIFEST_TEST_TEMPLATE) ;
        const output = $absolute(process.env.MANIFEST_OUTPUT_FOLDER) ;
        const intermediatePath = $path(output, $withoutext($filename(path))+'_intermediate.html') ;
        const outputPath = $path(output, $withoutext($filename(path))+'_output.html') ;

        if (!$isdirectory(output)) {
            t.expectZ($createDirectory(output)).true() ;
        }

        t.register("template path", path) ;
        t.expect0($isfile(path)).true() ;
        t.expect1($isreadable(path)).true() ;
        const templateBuffer = $readBuffer(path) ;
        t.register("template length", $length(templateBuffer)) ;
        t.expect3(templateBuffer).filled() ;

        const template = TSFusionTemplate.fromHTMLData(templateBuffer!, {
            debugParsing:false
        }) ;
        t.expectA(template).OK() ;
        t.expectB($writeBuffer(intermediatePath, template!.source)).true()
        let errors:string[] = [] ;
        t.register("fusionData", fusionData) ;
        const res = template?.fusionWithDataContext(fusionData, glob, errors) ;
        t.register('errors', $inspect(errors)) ;
        t.expectC(res).filled() ;
        t.expectD($writeBuffer(outputPath, res!)).true()
    }) ;
}) ;
