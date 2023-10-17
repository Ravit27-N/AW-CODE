
import { $count, $length, $unsigned, $now } from "../../utils/commons";
import { $removeFile } from "../../utils/fs";

import { ForbiddenError } from "../../utils/errors";

import { APIServer } from "../../server";
import { UserRole } from "../APIConstants";
import { APIAuth } from "../APIInterfaces";
import {DownloadService} from "../../services/download.service";
import {DownloadEntity, IDownload} from "../../entities/downloads";
import {FileService} from "../../services/file.service";
import {FileEntity, IFile} from "../../entities/files";

export const purgeDownloads = async (auth:APIAuth) : Promise<number> => {
	let n = 0 ;
	if (auth.role !== UserRole.Maintenance && auth.role !== UserRole.System) {
		throw new ForbiddenError(`Uploads cannot be purged by user ${auth.user}.`);
	}
	const api = APIServer.api() ;
	try {
		n = await api.transaction(async trx => {
			let total = 0 ;
			const now = $now() ;
			const downloadService = new DownloadService();
			let downloads = await downloadService.findMany<IDownload>({
				where: {
					expiresAt:{
						lte: now
					}
				}
			}, {trx});

			let files:string[] = [] ;
			if ($count(downloads)) {
				let fileIDs:number[] = []
				downloads.forEach(d => {
					const download = new DownloadEntity(d);
					if ($length(download.path)) { files.push(<string>(download.path)) ; }
					else {
						const fid = $unsigned(download.fileId) ;
						if (fid) fileIDs.push(fid) ;
					}
				}) ;
				const fileService = new FileService();
				let fileRefs = await fileService.findMany<IFile>({
					where:{
						id:{
							in: fileIDs	
						}
					}
				}) ;
				let toBeDeletedFileIds:number[] = [] ;
				fileRefs?.forEach(f => {
					const fileEntity = new FileEntity(f);
					const d = f.otherData as any ;
					// we verify that our download can be deleted 
					// (for now manifest files are not deletable from here :, so we may have a purge session, which does that)
					if(!d?.neverPurgeWithDownload){
						fileEntity.fillPathsIn(files)
						toBeDeletedFileIds.push(f.id);
					}
					
					
				}) ;

				await downloadService.deleteMany({
					where:{
						expiresAt: {
							lte: now
						}
					}
				})

				if ($count(toBeDeletedFileIds)) {
					await fileService.deleteMany({
						where:{
							id:{
								in: toBeDeletedFileIds
							}
						}
					});
				}

				files.forEach(p => $removeFile(p)) ;
			}
			return total ;
		}) ;
	}
	catch (e) {
		// here we have a rollback
		APIServer.api().error(e);
		throw e ;
	}
	return n ;
};
