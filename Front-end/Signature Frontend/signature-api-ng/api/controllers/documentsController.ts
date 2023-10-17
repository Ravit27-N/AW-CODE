import {StringArrayDictionary} from '../../utils/types';
import {$count, $length, $ok, $strings, $unsigned, stringifyPrisma} from '../../utils/commons';
import {$map} from '../../utils/array'
import {$ext, $filename, $isfile, $path, $realMoveFile} from '../../utils/fs'

import {
	BadRequestError,
	ConflictError,
	FileError,
	InternalError,
	ManifestDataError,
	NotFoundError
} from '../../utils/errors'
import {checkSessionMutability, sessionWithPublicID} from './sessionController'
import {APIServer} from '../../server'
import {APIAuth, CreateDocumentBody, DocumentsQuery} from '../APIInterfaces';
import {uploadWithURL} from './uploadController';
import {$lid, LocalID} from '../APIIDs';
import {automatSigningDocuments, SigningNodeDictionary} from '../automat/automat';
import {SigningNode} from '../automat/automatNode';
import {DocumentCreateInput, DocumentEntity, IDocument} from "../../entities/documents";
import {DocumentService} from "../../services/document.service";
import {SessionEntity} from "../../entities/sessions";
import {UploadEntity} from "../../entities/uploads";
import { NO_CONTEXT, FileStatus } from '../../classes/interfaces/DBConstants';
import { PrismaContext } from '../../classes/interfaces/DBInterfaces';
import {FileEntity, IFile} from "../../entities/files";
import { Prisma } from '@prisma/client';
import {ScenarioStatus} from "../APIConstants";

export const getSessionDocumentList = async (auth:APIAuth, sessionPublicID: LocalID, q:DocumentsQuery) : Promise<object> => {
	let mask = $unsigned(q['status_mask']) ;
	let tags = $strings(q.tags) ;
	let aid = $lid(q.actor)
	let actorOrTags = false ;

	if (($count(tags) > 0 || aid > 0)) {
		if (mask > 0) {
			throw new ConflictError("Cannot have 'status_mask' tag positionned with 'actor' or 'tags' at the same time") ;
		}
		actorOrTags = true ;
	}

	const context: PrismaContext<Prisma.SessionInclude> = {
		include: {
			scenarios: {
				where:{
					status: ScenarioStatus.ActiveScenario
				}
			},
			documents: true
		}
	};

	let session = await sessionWithPublicID(auth, sessionPublicID, context); // we load the documents and active scenario

	if (actorOrTags) {
		let documentURLsByTag:StringArrayDictionary = {} ;
		if (session.isActive()) {
			let automat = session.getActiveScenario?.getOtherData.automat ;
			if ($ok(automat)) {
				let taggedDocuments = automatSigningDocuments(automat!) ;

				if ($ok(taggedDocuments)) {
					const tdocs = <SigningNodeDictionary>taggedDocuments ;
					const api = APIServer.api() ;
					if ($count(tags)) {
						// here we can have tags only or tags + an actor ID
						tags.forEach(t => {
							let nodes = tdocs[t] ;
							if ($count(nodes)) {
								let docs = $map<SigningNode, string>(nodes, n => {
									return !aid || n.aids.includes(aid) ? api.url('session', sessionPublicID, 'document', n.did) : null ;
								}) ;
								if ($count(docs)) { documentURLsByTag[t] = docs ; }
							}
						}) ;
					}
					else {
						// here we only can have an actor ID
						for (let t in tdocs) {
							let nodes = tdocs[t] ;
							if ($count(nodes)) {
								let docs = $map<SigningNode, string>(nodes, n => {
									return n.aids.includes(aid) ? api.url('session', sessionPublicID, 'document', n.did) : null ;
								}) ;
								if ($count(docs)) { documentURLsByTag[t] = docs ; }
							}
						}

					}
				}

			}
		}
		return documentURLsByTag ;
	}
	else if ($count(session.getDocuments)) {
		let list = session.getDocuments as DocumentEntity[] ;
		if (mask > 0) {
			let newList:DocumentEntity[] = [] ;
			for (let d of list) {
				const entity = d;
				const status = await entity.documentStatus(NO_CONTEXT) ;
				if ((status & mask) > 0) {
					newList.push(entity) ;
				}
			}
			list = newList ;
		}
		if ($count(list)) {
			return {
				documents: session.getDocuments?.map((doc: IDocument) => {
					const d = new DocumentEntity(doc);
					return d.url(sessionPublicID)
				})
			};
		}
	}

	return { documents:[] } ;
}

function _missingErrorsInDocumentBody(b:CreateDocumentBody) : string {
	let u:string[] = [] ;

	if (!$length(b['file-name'])) { u.push('file-name') ; }
	if (!$length(b['title'])) { u.push('title') ; }

	return $count(u) > 0 ? `Unspecified or inconcistent items : ${u.join(', ')}`:'' ;

}


async function _addDocument(session:SessionEntity, body:CreateDocumentBody, upload:UploadEntity, c:PrismaContext) : Promise <DocumentEntity>
{

	const service = new DocumentService();
	if (!$ok(c.trx)) {
		throw new InternalError('_addActor() should be called inside a transaction') ;
	}

	let exists = $isfile(upload.path) ;
	const api = APIServer.api() ;

	if (exists && $length(upload.sealPath)) {
		exists = $isfile(upload.sealPath) ;
	}
	if (!exists) {
		throw new NotFoundError(`Upload ${upload.publicId} files are not found.`);
	}

	let verification = $length(upload.sealPath) ? await (upload.verifyFileSeal()) : false ;
	if (!verification) {
		throw new FileError(`Upload file seal could not be verified.`);
	}

	let destinationPath = $path(api.storagePathFiles, $filename(upload.path)) ;
	if (!$realMoveFile(upload.path, destinationPath)) {
		throw new FileError(`Impossible to move upload file to storage.`);
	}

	let destinationSealPath = $path(api.storagePathSeals, $filename(<string>(upload.sealPath))) ;
	if (!$realMoveFile(upload.sealPath, destinationSealPath)) {
		throw new FileError(`Impossible to move upload seal path to storage.`);
	}
	
	const createFile = {
		timestampedAt: upload.uploadedAt ?? '',
		fileName: body['file-name'],
		fileType: upload.fileType,
		hash: upload.hash ?? "",
		path: destinationPath,
		sealPath: destinationSealPath,
		size: upload.size,
		status: FileStatus.Valid,
		uploadedAt: upload.uploadedAt,
		user: upload.user
	}
	// we first, need to create a file object
	// const file = await fileService.insert(createFile)

	let newDocument: DocumentCreateInput = {
		session: {
			connect: {id: session.id}
		},
		genuineFile:{
			create: createFile
		},
		publicId: session.sessionNextPublicID(), // for now, it's not an async fn here
		fileName: body['file-name'], // this is the genuine file name here
		title: body.title
	};
	if ($length(body.abstract)) newDocument.abstract = body.abstract;
	if ($ok(body['manifest-data'])) newDocument.manifestData = stringifyPrisma(body['manifest-data']);
	if ($ok(body['user-data'])) newDocument.userData = stringifyPrisma(body['user-data']);

	const docEntity = await service.insert(newDocument, c)

	const file = await docEntity.getRelated<IFile>({
		genuineFile: true
	}, c, {
		genuineFile: {
			status: FileStatus.Valid
		}
	});
	docEntity.setGenuineFile = file ? new FileEntity(file) : undefined;
	const sessionEntity = await docEntity.mySession(c); // we want our graph straight
	await sessionEntity.updateSession({
		lastPubObject: session.lastPubObject
	}, c)
	await upload.cleanAndDelete(c) ;

	return docEntity;
}

export const addDocumentToSession = async (auth: APIAuth, sessionPublicID: LocalID, body: CreateDocumentBody): Promise<DocumentEntity> => {
	let message = _missingErrorsInDocumentBody(body);
	if ($length(message)) {
		throw new BadRequestError(`Impossible to add a document to session ${sessionPublicID}. ${message}.`);
	}
	const api = APIServer.api();

	if (!api.verifyManifestData(body['manifest-data'], api.conf.documentManifestData)) {
		throw new ManifestDataError(`manifest-data did not match allowed keys.`);
	}

	let returnValue = undefined;
	try {
		returnValue = await api.transaction(async trx => {
			const ctx = {trx: trx};
			const upload = await uploadWithURL(body.upload, ctx);

			if ($ext(body['file-name']).toLowerCase() !== $ext(upload.path)) {
				throw new ConflictError("'file-name' extension is incompatible with upload extension");
			}


			let session = await sessionWithPublicID(auth, sessionPublicID, ctx);

			await checkSessionMutability(session);
			return await _addDocument(session, body, upload, ctx);
		});
	} catch (e) {
		// here we have a rollback
		APIServer.api().error(e);
		throw e;
	}
	return returnValue;

}
