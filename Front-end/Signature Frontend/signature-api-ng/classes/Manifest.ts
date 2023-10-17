import {Ascending, Nullable, StringDictionary} from '../utils/types';
import {$count, $ectDateFromString, $isunsigned, $length, $now, $ok, $string, $value} from '../utils/commons'
import {$readBuffer} from '../utils/fs'
import {$uuid, HashMethods} from '../utils/crypto'
import {TSFusionTemplate, TSHTMLTemplate} from '../utils/tsfusion';
import {TSDate} from "../utils/tsdate";
import {$inspect, $logterm} from '../utils/utils';
import {JSONObject} from 'ts-json-object'
import {APIAuth, ManifestData, UserData} from '../api/APIInterfaces';

import {CertignaRequestError, InternalError, NotFoundError} from '../utils/errors';
import {DocumentIDs} from '../api/APIIDs';
import {
    AcceptedLanguages,
    sessionStatus2Tag,
    SignatureFormat,
    signatureFormat2Tag,
    SignatureLevel,
    signatureLevel2Tag,
    signatureType2Tag,
    SigningProcess
} from '../api/APIConstants';

import {Certigna} from './CertignaEndPoint';
import {GenericLogger, GenericLoggerInterface} from './GenericLogger';
import puppeteer, {PaperFormat, PDFMargin, PDFOptions} from 'puppeteer'
import {$map} from '../utils/array';
import {$compare, $dateorder, $order, $visualorder} from '../utils/compare';
import env from "../env-config";
import {$trim} from '../utils/strings';
import {SessionEntity} from "../entities/sessions";
import {ActorEntity} from "../entities/actors";
import {ActorService} from "../services/actor.service";
import {DocumentService} from "../services/document.service";
import {DocumentEntity, IDocument} from "../entities/documents";
import {CertificateService} from "../services/certificate.service";
import {CertificateEntity, ICertificate} from "../entities/certificates";
import {PrismaContext, SessionContextEventType} from './interfaces/DBInterfaces';

interface templateOptions {
    url: position | undefined;
    pageNumber:  position | undefined;
    title:  position | undefined;
    date:  position | undefined;
}

type position = 'start'|'center'|'end';
export class ManifestOptions extends JSONObject {
	@JSONObject.union(['letter' , 'legal', 'tabloid', 'ledger', 'a0', 'a1', 'a2', 'a3', 'a4', 'a5', 'a6'])
	@JSONObject.optional("a4")
	declare format: PaperFormat

	@JSONObject.union(['portrait' , 'paysage', 'landscape'])
	@JSONObject.optional("portrait")
	declare orientation:'portrait' | 'paysage' | 'landscape'
	
	@JSONObject.optional
	declare leftMargin: string

	@JSONObject.optional
	declare rightMargin: string
    
	@JSONObject.optional
	declare topMargin: string

	@JSONObject.optional
	declare bottomMargin: string

	@JSONObject.optional(<templateOptions>{})
	declare header: templateOptions
    
	@JSONObject.optional(<templateOptions>{pageNumber:'end'})
	declare footer: templateOptions
}

type TemplateDictionary = { [key:string]: TSHTMLTemplate }


const EventTypeTitles:StringDictionary[] = [
	{ 'fr': "Événement inconnu", 		  'en': "Unknown event"},
	{ 'fr': "Fermeture de la session", 	  'en': "Closing session"},
	{ 'fr': "Création d'un scénario", 	  'en': "Scenario creation"},
	{ 'fr': "Activation d'un scénario",   'en': "Scenario activation"},
	{ 'fr': "Split d'un scénario", 		  'en': "Scenario split"},
	{ 'fr': "Génération d'un OTP", 		  'en': "OTP generation"},
	{ 'fr': "Vérification d'un OTP", 	  'en': "OTP check"},
    { 'fr': "Génération d'un certificat", 'en': "Certificate generation"},
	{ 'fr': "Approbation de documents",   'en': "Documents' approval"},
	{ 'fr': "Signature de documents", 	  'en': "Documents' signature"},
	{ 'fr': "Fin de scenario", 	          'en': "Scenario cancelling"},
    { 'fr': "Refus de documents", 	      'en': "Documents' refuse"},
    { 'fr': "Recevoir de documents", 	  'en': "Documents' receive"}
] ;

interface ManifestEventDocument {
    fileName:string,
	identifier:string,
    document:DocumentEntity
}
interface ManifestEvent {
	user:string ;
	event:string ;
	date:TSDate ;
	scenarioId:string ;
	reason:string ;
	actorName:string ;
	actorIdentifier:string ;
	hasActor:boolean ;
	documents:ManifestEventDocument[] ;
	operationId:string ;
	manifestData?:ManifestData ;
	token:string ;
	actor?:ActorEntity ;
}

interface SignaturesHolder {
	identifier:string
    signatureDate:TSDate ; // last signature date
    signatures:ManifestSignatureData[] ;
}

interface ManifestCertificateData extends SignaturesHolder {
    expirationDate:TSDate ;
    certificate:CertificateEntity ;
}
interface ManifestDocumentData extends SignaturesHolder {
    creationDate:TSDate ;
	fileName:string,
	document:DocumentEntity,
}

interface ManifestActorData extends SignaturesHolder {
    name:string ;
    actor:ActorEntity ;
}

interface ManifestSignatureData {
    date:TSDate ;                   // signature date
    tag:string ;		            // process tag
    role:string ;                   // process role
    isSignature:boolean ;           // is a signature process
	document:DocumentEntity;      // real document object
    actor:ActorEntity ;                   // real actor object
    certificate?:CertificateEntity ;      // real certificate object
    actorIdentifier:string ;        // actor identifier
    documentIdentifier:string ;     // document identifier
    certificateIdentifier?:string ; // certificate identifier
    identifier:string ;             // document signature ID
    fileIdentifier:string ;         // file signature ID
    threadIdentifier:string ;       // thread identifier
    format:string ;                 // PAdES, XAdES or CAdES or '' (for unknown or irrelevant)
    level:string ;                  // B, L, LT, LTA, ''
    type:string ;                   // envelopped, envelopiing and detached (or '')
    token:string ;                  // signature used OTP
}

interface ManifestSourceData {
    creationDate:TSDate ;                    // session creation date
    status:string ;                          // session status
    type:string ;                            // session process type
    signatureDate:TSDate ;                   // date of last signature
    templateName:string ;                    // template name
    identifier:string ;                      // session identifier
	date:TSDate ;						     // manifest creation date
	fileName:string ; 					     // manifest file name
	reference:string ;					     // manifest reference
    endUser:UserData                          // end-user that create a session
	documents:ManifestDocumentData[] ;       // all approved and signed documents' infos
	actors:ManifestActorData[] ;			 // all approbators and signers
    certificates:ManifestCertificateData[] ; // all used certificates
    signatures:ManifestSignatureData[] ;     // all signatures
	events:ManifestEvent[] ;			     // all the events
}
export class Manifest extends GenericLogger {
	templates:TemplateDictionary = {} ;

	private static instance: Manifest; // the private singleton var

    private async _generateSourceData(auth:APIAuth, templateName:string, session:SessionEntity, c:PrismaContext, rolesTranslations:{[key:string]:StringDictionary}) : Promise<ManifestSourceData> {
		let actors:ManifestActorData[] = [] ;
		let actorsByIds = new Map<number|bigint, ManifestActorData> ;
		let documents:ManifestDocumentData[] = [] ;
		let documentsByIds = new Map<number|bigint, ManifestDocumentData> ;
        let certificates:ManifestCertificateData[] = [] ;
        let certificatesByIds = new Map<number, ManifestCertificateData>() ;
        let tagSet = new Set<string> ;
        const actorService = new ActorService();
        const docService = new DocumentService();
        const certService = new CertificateService();

		const sessionEvents = session.getOtherData.sessionContextEvents ;
		const lang = auth.language ;
        const past = TSDate.past() ;
        const returnStatic = {
            status:_translate(sessionStatus2Tag(session.getStatus), lang),
            templateName: templateName,
            signatureDate: past,
            identifier:session.identifier(),
            creationDate:$ectDateFromString(session.createdAt),
			date:$ectDateFromString($now()),    // manifest creation date
			fileName:`${$uuid()}.pdf`, 		    // manifest file name
			reference:$uuid(),					// manifest reference
            endUser:session.getUserData         // manifest to show end-user
        } ;

        function _pushSignature(self:SignaturesHolder, signature:ManifestSignatureData) {
            if (!$ok(self.signatureDate) || $compare(self.signatureDate, signature.date) === Ascending) {
                self.signatureDate = signature.date ;
            }
            if (!$ok(returnStatic.signatureDate) || $compare(returnStatic.signatureDate, signature.date) === Ascending) {
                returnStatic.signatureDate = signature.date ;
            }
            self.signatures.push(signature) ;
        }

        function _roleDescription(s:Nullable<string>, language:AcceptedLanguages):string {
            s = $trim(s).toLowerCase() ;
            if (s.length) {
                const node = rolesTranslations[s] ;
                if ($ok(node)) {
                    const ret = (node)[language] ;
                    if ($length(ret)) { return ret ; }
                }
            }
            return rolesTranslations['unknown'][language] ;
        }
        await actorService.getsWhere({sessionId: session.id}).then(function (actorEntities) {
            if ($ok(actorEntities)){
                actorEntities?.forEach(actorEntity => {
                    const localActor: ManifestActorData = {
                        actor: actorEntity,
                        identifier: actorEntity.identifier(),
                        name: actorEntity.completeName(),
                        signatures: [],
                        signatureDate: past
                    }
                    actors.push(localActor);
                    actorsByIds.set(actorEntity.publicId, localActor);
                });
            }
        });

		// constructing documents and actors for our source
		for (let e of sessionEvents) {
			if (e['event-type'] === SessionContextEventType.ApproveDocuments || e['event-type'] === SessionContextEventType.SignDocuments 
                ||  e['event-type'] === SessionContextEventType.RefuseDocuments ) {
				const aid = e['actor-id'] ;
				if ($isunsigned(aid) && aid! > 0 && !actorsByIds.has(aid!)) {
                    throw new NotFoundError(`Actor with ID (${session.publicId}, ${aid}) not found`) ;
				}
                const documentIds = e['document-ids'] ;
				if ($count(documentIds) > 0) {
					for (let did of <DocumentIDs>documentIds) {
						if ($isunsigned(did) && did > 0 && !documentsByIds.has(did)) {
							let doc = await docService.sessionObjectWithPublicID<IDocument>(session, did, c) ;
							if (!$ok(doc)) {
								throw new NotFoundError(`Document with ID (${session.publicId}, ${did}) not found`) ;
							}
                            const docEntity = new DocumentEntity(<IDocument> doc);
                            const localDocument:ManifestDocumentData = {
                                creationDate:$ectDateFromString(docEntity.createdAt),
                                document:docEntity,
                                fileName:docEntity.fileName,
                                identifier:docEntity.identifier(),
                                signatures:[],
                                signatureDate:past // MODIFIED LATER
                            }
							documents.push(localDocument) ;
							documentsByIds.set(did, localDocument) ;
						}
					}
				}
			}
		}

		const noToken = _translate('no-token', lang) ;
        const signatures:ManifestSignatureData[] = [] ;
        if ($count(session.getOtherData?.signatures)) {
            for (let s of session.getOtherData.signatures!) {
                const actor = $isunsigned(s.aid) && s.aid > 0 ? actorsByIds.get(s.aid) : undefined ;
                const document = $isunsigned(s.did) && s.did > 0 ? documentsByIds.get(s.did) : undefined ;
                if ($ok(actor) && $ok(document)) {
                    let certificate = $isunsigned(s.cid) && s.cid! > 0 ? certificatesByIds.get(s.cid!) : undefined ;
                    if (!$ok(certificate)) {
                        let cert = await certService.sessionObjectWithPublicID<ICertificate>(session, s.cid, {trx:c.trx}) ;
                        if ($ok(cert)) {
                            const certEntity = new CertificateEntity(<ICertificate> cert);
                            certificate = {
                                expirationDate:$ectDateFromString(certEntity.expirationDate()),
                                identifier:certEntity.identifier(),
                                certificate:certEntity,
                                signatures:[],
                                signatureDate:past // MODIFIED LATER
                            }
                            certificatesByIds.set(s.cid!, certificate) ;
                            certificates.push(certificate) ;
                        }
                    }

                    if ($ok(certificate) || !$ok(s.cid) || s.cid === 0) {
                        const sign:ManifestSignatureData = {
                            date:$ectDateFromString(s.date),
                            tag:s.tag,
                            isSignature:_isSignature(s.tag),
                            actor:actor!.actor,
                            role:_roleDescription(s.tag, lang),
                            actorIdentifier:actor!.identifier,
                            document:document!.document,
                            documentIdentifier:document!.identifier,
                            certificate:certificate?.certificate,
                            certificateIdentifier:certificate?.identifier,
                            identifier:s.dsigid,
                            fileIdentifier:s.sigid,
                            threadIdentifier:s.threadid,
                            format:$ok(s.format) ? signatureFormat2Tag(s.format!) : '',
                            level:$ok(s.level) ? signatureLevel2Tag(s.level!) : '',
                            type:$ok(s.type) ? _translate(signatureType2Tag(s.type!),lang) : '',
                            token:$value(s.otp, noToken),
                        } ;

                        if ($length(s.tag)) { tagSet.add(s.tag.toLowerCase()) ; }

                        if ($ok(certificate)) _pushSignature(certificate!, sign) ;
                        _pushSignature(document!, sign) ;
                        _pushSignature(actor!, sign) ;
                        signatures.push(sign) ;
                    }
                }
            }
        }

		// we now construct the event list
        // TODO: update type
		let events:ManifestEvent[] = sessionEvents.map((e:any) => {
			const actor = $ok(e['actor-id']) ? actorsByIds.get(e['actor-id']!) : null ;
			let event:ManifestEvent = {
				user:e.user,
				date:$ectDateFromString(e.date),
				event:EventTypeTitles[e['event-type']][lang],
				scenarioId:e['scenario-id'] ? `${e['scenario-id']}`: '',
				reason:$ok(e.reason) ? <string>e.reason : '',
				hasActor:false,
				actorName:'',
				actorIdentifier:'',
				documents:[],
				operationId:$ok(e['operation-id']) ? <string>e['operation-id'] : '',
				manifestData:$ok(e['manifest-data']) ? ['manifest-data'] : {},
                token:$value(e.token, noToken)
			}
			if ($ok(actor)) {
				event.hasActor = true ;
				event.actorName = actor!.name ;
				event.actorIdentifier = actor!.identifier ;
				event.actor = actor!.actor ;
			}
			if ($count(e['document-ids'])) {
                event.documents = $map(<DocumentIDs>e['document-ids'], (did) => {
                    const d = documentsByIds.get(did) ;
                    return $ok(d) ? {
                        fileName:d!.fileName,
                        identifier:d!.identifier,
                        document:d!.document
                    } : undefined ;
                }) ;
			}
			return event ;
		})

        actors.sort((a,b) => $visualorder(a.name, b.name)) ;
        documents.sort((a,b) => $visualorder(a.fileName, b.fileName)) ;
        signatures.sort((a,b) => $dateorder(a.date, b.date)) ;
        certificates.sort((a,b) => $order(a.identifier, b.identifier)) ;

        // events should be already in right order
        const signingType = tagSet.has(SigningProcess.Sign) ||
                            tagSet.has(SigningProcess.Cosign) ||
                            tagSet.has(SigningProcess.Countersign) ||
                            tagSet.has(SigningProcess.IndividualSign) ||
                            tagSet.has(SigningProcess.OrderedCosign) ;
        let approvalType = tagSet.has(SigningProcess.Approval) ;
        if (!approvalType) {
            for (let tag of tagSet) {
                if (!Object.values(SigningProcess).includes(tag as SigningProcess)) { approvalType = true ; break ;}
            }
        }
        const typeKey = signingType ? (approvalType ? "approvalAndSign" : "sign") : "approval" ;
		return {
            type:_translate(typeKey, lang),     // type of global process
			documents:documents,				// all approved and signed documents' infos
			actors:actors,						// all approbators and signers
            signatures:signatures,              // all signatures
            certificates:certificates,          // all certificates
			events:events,						// all the events
            ... returnStatic
		}
	}

	public async generateManifest(
		auth:APIAuth,
		session:SessionEntity,
		c:PrismaContext,
        rolesTranslations:{[key:string]:StringDictionary} = {},
		templateName:string = 'standard',
		opts?:ManifestOptions
	) : Promise<Buffer> {
		const template = this.getTemplate(templateName) ;
		if (!$ok(template)) {
			throw new InternalError(`No template found with name '${templateName}'`) ;
		}
		this.log(`Using template '${templateName}' to generate manifest:\n${$inspect(template)}`) ;

		const dataSource = await this._generateSourceData(auth, templateName, session, c, rolesTranslations) ;

        const glob = {} ;
        const errors:string[] = [] ;
        const html = template!.fusionWithDataContext(dataSource, glob, errors) ;
		if (!$length(html)) {
			throw new InternalError(`Impossible to generate HTML file from template '${templateName}'`) ;
		}

		this.log(`${$length(html)} bytes HTML generated from template '${templateName}'`) ;
		this.log(`typeof puppeteer.launch = ${puppeteer.launch}`) ;

		const browser = await puppeteer.launch() ;
		const page = await browser.newPage() ;
		await page.setContent(html!.toString('utf-8')) ;

		let margin:PDFMargin = {} ;
		if ($ok(opts?.leftMargin)) { margin.left = opts?.leftMargin ; }
		if ($ok(opts?.rightMargin)) { margin.right = opts?.rightMargin ; }
		if ($ok(opts?.topMargin)) { margin.top = opts?.topMargin ; }
		if ($ok(opts?.bottomMargin)) { margin.bottom = opts?.bottomMargin ; }

		let pdfOptions:PDFOptions = {
			format:$ok(opts?.format)?<PaperFormat>(opts?.format.toLowerCase()):'a4',
			printBackground:true,
			landscape:opts?.orientation === 'paysage' || opts?.orientation === 'landscape',
			margin:margin
		}
		if ($ok(opts?.header)) {
            pdfOptions.footerTemplate = '<span></span>';
			pdfOptions.displayHeaderFooter = true ;
            pdfOptions.headerTemplate = this._addHeaderFooterTemplate(opts!.header, 'top');
		}
		if ($ok(opts?.footer)) {
            pdfOptions.displayHeaderFooter = true ;
            if(!$ok(opts?.header)){
                pdfOptions.headerTemplate = '<span></span>';
            }
            pdfOptions.footerTemplate = this._addHeaderFooterTemplate(opts!.footer, 'bottom');
		}

		const pdf = await page.pdf(pdfOptions) ;

		if (!$length(pdf)) {
			throw new InternalError(`Impossible to produce PDF file from HTML generated from template '${templateName}'`) ;
		}
		// now we want to PAdES sign this PDF File
		const endPoint = Certigna.endPoint() ;

		// we make a PAdES signature with a server hosted certificate whose credential
		// are stored in our configuration file
		let signedPDF = await endPoint.signDocument(<Buffer>pdf, {
			format:SignatureFormat.PAdES,
			login:$string(env.SIGN_SERVER_LOGIN),
			password:$string(env.SIGN_SERVER_PASS),
			fileName:`${$uuid()}.pdf`,
			hashMethod:HashMethods.SHA256,
			level:SignatureLevel.LTA
		})
		await browser.close();

		if (!$length(signedPDF)) {
			throw new CertignaRequestError(`Impossible to generated PDF file from template '${templateName}'`) ;
		}


		return <Buffer>signedPDF ;
	}
    
    // pdfOptions supports only inline styles
    private _addHeaderFooterTemplate(templateName: templateOptions, position: 'top'|'bottom'): string {
        let template = `<div style="color: black;font-size: 10px; width: 100%;display: flex;position: relative;">`;
        Object.entries(templateName).forEach(([key, value],) => {
            let posit: string;
            if (value == 'end') {
                posit = `position: absolute; ${position}: 10px;right: 40px;`;
            } else if (value == 'start') {
                posit = `position: absolute; ${position}: 10px;left: 40px;`;
            } else {
                posit = `margin-left: auto; margin-right: auto;padding-${position}: 10px`;
            }
            if (key == 'pageNumber') {
                template += `<div style="${posit}"><span class="pageNumber"></span> / <span class="totalPages"></span></div>`;
            } else {
                template += `<div style="${posit}"><span class="${key}"></span></div>`;
            }
        });
        template += '</div>';
        return template;
    }

	public addTemplateFile(path:Nullable<string>, name:string = 'standard'):TSHTMLTemplate | null {
		if (!$length(path) || !$length(name)) { return null ; }
        const templateData = $readBuffer(path) ;
        if (!$length(templateData)) { return null } ;
        try {
            const template = TSFusionTemplate.fromHTMLData(templateData!) ;
            if ($ok(template)) {
                this.templates[name] = template! ;
                return template ;
            }
            return null ;
        }
        catch (e) {
			$logterm(`&oDid encouter error &r${e?.name} &oduring template interpretation: "&w${e?.message}&o"&0`) ;
            return null ;
        }
	}

	public getTemplate(name:string = 'standard') : TSHTMLTemplate | null {
		const t =  this.templates[name] ;
		return $ok(t) ? t : null ;
	}

	public static producer(logger?:GenericLoggerInterface): Manifest {
		if (!this.instance) {
			this.instance = new Manifest(logger) ;
		}
		return this.instance ;

	}
}

function _isSignature(s:string) {
    s = $trim(s).toLowerCase() ;
    return s === SigningProcess.Sign || s === SigningProcess.Cosign || s === SigningProcess.Countersign ||
           s === SigningProcess.IndividualSign || s === SigningProcess.OrderedCosign ;
}

function _translate(key:string, lang:AcceptedLanguages):string {
    const ManifestTrans:{ [key:string]:StringDictionary } = {
        'no-token': {
            'fr': "Pas d'OTP associé",
            'en': "No OTP"
        },
        'signedDocuments': {
            'fr': "Documents signés: ",
            'en': "Signed Documents:"
        },
        'envelopped': {
            'fr': "signature enveloppée",
            'en': "envelopped signature"
        },
        'envelopping': {
            'fr': "signature enveloppante",
            'en': "envelopping signature"
        },
        'detached': {
            'fr': "signature détachée",
            'en': "detached signature"
        },
        'Genuine': {
            'fr': "juste créé",
            'en': "genuine"
        },
        'UnderConstruction': {
            'fr': "en construction",
            'en': "under construction"
        },
        'Idle': {
            'fr': "suspendu",
            'en': "idle"
        },
        'Active': {
            'fr': "actif",
            'en': "active"
        },
        'WellTerminated': {
            'fr': "correctement signé et clos",
            'en': "signed and closed"
        },
        'Deleted': {
            'fr': "supprimé",
            'en': "deleted"
        },
        'Canceled': {
            'fr': "annulé",
            'en': "canceled"
        },
        'WellTerminatedArchived': {
            'fr': "correctement signé, clos et archivé",
            'en': "signed, closed and archived"
        },
        'WrongTerminatedArchived': {
            'fr': "en erreur, archivé",
            'en': "wrong terminated and archived"
        },
        "approvalAndSign": {
            'fr': "approbation et signature",
            'en': "approval and signature"
        },
        "sign": {
            'fr': "signature uniquement",
            'en': "signature (only)"
        },
        "approval": {
            'fr': "approbation uniquement",
            'en': "approval (only)"
        },
    } ;
    const n = ManifestTrans[key] ;
    return $ok(n) && $ok(n[lang]) ? n[lang]! : key ;
}

