import {$count, $isnumber, $length, $now, $ok} from '../../utils/commons';
import {$hashfile, $uuid} from '../../utils/crypto';
import {$filesize, $path, $removeFile, $writeString} from '../../utils/fs';
import {$inspect} from '../../utils/utils';
import AdmZip from 'adm-zip';
import {FileError, ForbiddenError, InternalError, NotFoundError} from '../../utils/errors'
import {sessionWithPublicID} from './sessionController'
import {APIFileType} from '../APIConstants'
import {APIServer} from '../../server';
import {APIAuth} from '../APIInterfaces';
import {$lid, GlobalID, LocalID} from '../APIIDs';
import {Certigna} from '../../classes/CertignaEndPoint';
import env from '../../env-config';
import {Nullable} from '../../utils/types';
import {ISession, SessionEntity} from "../../entities/sessions";
import {DocumentEntity, IDocument} from "../../entities/documents";
import {FileService} from "../../services/file.service";
import {FileCreateInput, FileEntity, IFile} from "../../entities/files";
import {DownloadService} from "../../services/download.service";
import { FileStatus } from '../../classes/interfaces/DBConstants';
import { PrismaContext, LastFile } from '../../classes/interfaces/DBInterfaces';
import {DownloadEntity} from "../../entities/downloads";
import {DocumentService} from "../../services/document.service";

export const getSessionDocumentByID = async (
	auth:APIAuth,
	sessionOrID:GlobalID|ISession,
	did:number|LocalID,
	c:PrismaContext) : Promise<DocumentEntity> =>
{
	const docService = new DocumentService();
	let session = $isnumber(sessionOrID) ? await sessionWithPublicID(auth, <GlobalID>sessionOrID, {trx: c.trx}) : new SessionEntity(<ISession>sessionOrID);
	let doc = null;
	if ($ok(session)) {
		doc = await docService.sessionObjectWithPublicID<IDocument>(session, did, c);
	}
	if (!$ok(doc)) {
		throw new NotFoundError(`Document with IDs (${session.publicId},${did}) was not found.`);
	}
	return new DocumentEntity(<IDocument>doc);
}

export const removeSessionDocument = async (auth:APIAuth, sessionPublicID:number|GlobalID, did:number|LocalID) : Promise<string> => {

	const api = APIServer.api() ;
	let returnValue = undefined ;
	try {
		let paths:string[] = [] ;
		// since we will cascade destruction, we need to be in a transaction
		returnValue = await api.transaction(async trx => {
			const context = {trx: trx};
			let doc = await getSessionDocumentByID(auth, sessionPublicID, did, context); // we load the files we want to clean
			if (!(await doc.canBeDeleted(context))) {
				throw new ForbiddenError(`Document with IDs (${sessionPublicID},${did}) cannot be deleted.`);
			}
			let url = doc.url(sessionPublicID);
			doc?.getDocFiles?.forEach(file => {
				file.fillPathsIn(paths);
			});

			await doc.delete(context);
			return url;
		})
		// here we are committed, we can destroy files on disk
		paths.forEach(p => $removeFile(p)) ;
	}
	catch (e) {
		// here we may have a rollback
		APIServer.api().error(e);
		throw e ;
	}
	return returnValue ;
}

const _documentsDownload = async(
	auth:APIAuth,
	session:SessionEntity|undefined,
	downloadedFiles:LastFile[],
	tobeRemoved:string[],
	c:PrismaContext
) : Promise<DownloadEntity> => {

	const fileService = new FileService();
	const downloadService = new DownloadService();
	if (!$ok(c.trx)) {
		throw new InternalError('_documentsDownload() method should be called inside a transaction') ;
	}
	const n = $count(downloadedFiles) ;
	if (!n) {
		throw new NotFoundError(`No files to be downloaded.`);
	}
	let dbFiles:FileEntity[] = [] ;

	for (let source of downloadedFiles) {
		let file = await fileService.findFirst<IFile>({
			where: {
				id: source.fileId!
			}
		});
		const fileEntity = new FileEntity(<IFile>file)
		if (!$ok(fileEntity)) { throw new NotFoundError('Download file not found') ; }
		dbFiles.push(fileEntity) ;
	}

	const api = APIServer.api() ;
	let now = $now() ;
	let downloadFileInterface:Nullable<FileCreateInput> = undefined ;

	api.log(`Files to be downloaded:\n${$inspect(dbFiles)}`)
	if (n == 1) {
		// one file, we take it as it, copy it to the directory and all is good
		downloadFileInterface = await dbFiles[0].fileInterfaceOfCopyToDirectory(auth, api.conf.downloadsPath, now) ; // no options for now
	}
	else {
		// we have several files, we create a zip file
		const zip = new AdmZip();
		dbFiles.forEach(file => { zip.addLocalFile(file.path); }) ;
		const fileBase = $uuid(), fileName = `${fileBase}.zip`;
		let filePath = $path(api.conf.downloadsPath, 'files', fileName) ;
		zip.writeZip(filePath) ;

		// then seal it
		const hash = await $hashfile(filePath) ;
		if (!$length(hash)) {
			throw new FileError(`Cannot activate calculate hash of zip file to download.`);
		}
		const fileSize = $filesize(filePath) ; // FIXME: get errors here !!!


        // FIXME: verify that both those server login and password were defined
		let seal = await Certigna.endPoint().seal(env.SIGN_SERVER_LOGIN!, env.SIGN_SERVER_PASS!, {
			name:fileName,
			user:auth.user,
			size:fileSize,
			hash:<string>hash,
			date:now
		}) ;

		if (!$ok(seal)) {
			throw new FileError(`Cannot seal zip file to download.`);
		}

		let sealPath = $path(api.conf.downloadsPath, 'seals', `${fileBase}.xml`) ;
		if (!$writeString(sealPath, seal)) {
			throw new FileError(`Cannot save seal file of zip file to download.`);
		}
		downloadFileInterface = {
			fileName:fileName,
			fileType:APIFileType.ZIP,
			hash:<string>hash,
			path:filePath,
			sealPath:sealPath,
			size:fileSize,
			status:FileStatus.Valid,
			timestampedAt:now,
			user:auth.user
		} ;

	}

	const sp = downloadFileInterface.sealPath ;
	if ($length(sp)) { tobeRemoved.push(<string>sp) ; }
	tobeRemoved.push(downloadFileInterface.path) ;

	let pid = await DownloadEntity.nextGlobalPublicID(c) ; // this method updates NGConfig table
	let newFile = await fileService.insert(downloadFileInterface, c)
	const download = await downloadService.insert({
		publicId:pid,
		session: {
			connect: {
				id: session?.id
			}
		},
		file:{
			connect: {
				id: newFile.id
			}	
		},
		size:downloadFileInterface.size,
		ttl:api.conf.downloadTtl,
		user:downloadFileInterface.user
	}, c)
	download.setFile = newFile;
	download.setSession = session ; // we want our graph straight

	return download ;
}


export const getSessionDocumentCurrentVersionDownload = async (
	auth: APIAuth, sessionPublicID: number | GlobalID, did: number | LocalID,
	optionalActorID?: number | LocalID, c?: PrismaContext
): Promise<DownloadEntity> => {
	let paths:string[] = [] ;
	let returnValue = undefined ;
	const api = APIServer.api() ;
	try {
		returnValue = await api.transaction(async trx => {
			// Because we call this function from other transaction
			const currentTrx = $ok(c) ? c?.trx : trx;
			let doc = await getSessionDocumentByID(auth, sessionPublicID, did, {trx: currentTrx});
			let lastFiles = doc.getLastFiles($lid(optionalActorID));
			return _documentsDownload(auth, doc.getSession, lastFiles, paths, {trx: currentTrx});
		});
	}
	catch (e) {
		paths.forEach(f => $removeFile(f)) ;
		APIServer.api().error(e);
		throw e ;
	}
	return returnValue ;
}

export const getSessionDocumentGenuineVersionDownload = async (
	auth: APIAuth, sessionPublicID: number | GlobalID, did: number | LocalID
): Promise<DownloadEntity> => {

	let paths:string[] = [] ;
	let returnValue = undefined ;
	const api = APIServer.api() ;
	try {
		returnValue = await api.transaction(async trx => {
			let doc = await getSessionDocumentByID(auth, sessionPublicID, did, {trx:trx}) ; // we load the genuineFile
			let lastFiles = doc.getGenuineFiles() ;
			return _documentsDownload(auth, doc.getSession, lastFiles, paths, {trx:trx}) ;
		}) ;
	}
	catch (e) {
		paths.forEach(f => $removeFile(f)) ;
		APIServer.api().error(e);
		throw e ;
	}
	return returnValue ;
}

