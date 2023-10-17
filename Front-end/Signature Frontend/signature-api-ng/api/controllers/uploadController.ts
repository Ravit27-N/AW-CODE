
import { $length, $ok } from '../../utils/commons'

import { NotFoundError, ForbiddenError} from '../../utils/errors'
import { UserRole } from '../APIConstants'
import {$url2gid, GlobalID, LocalID} from '../APIIDs'
import { APIAuth } from '../APIInterfaces'
import { APIServer } from '../../server'
import { Nullable } from '../../utils/types'
import {UploadService} from "../../services/upload.service";
import {IUpload, UploadEntity} from "../../entities/uploads";
import { PrismaContext } from '../../classes/interfaces/DBInterfaces'


export const uploadWithPublicID = async (uid:Nullable<GlobalID>, c:PrismaContext) : Promise<UploadEntity> =>
{
	const service = new UploadService()

	let upload = await service.objectWithPublicID<IUpload>(uid, c) ;
	const uploadEntity = new UploadEntity(<IUpload>upload);
	if (!$ok(upload)) {
		throw new NotFoundError(`Impossible to find upload with id ${uid}.`);
	}
	if (uploadEntity?.isExpired()) {
		throw new ForbiddenError(`Upload with id ${uid} is expired.`);
	}
	return uploadEntity ;
}

export const uploadWithURL = async (url:Nullable<string>, c:PrismaContext) : Promise<UploadEntity> =>
{
	if (!$length(url)) {
		throw new NotFoundError(`Impossible to find upload with undefined url.`);
	}
	return uploadWithPublicID($url2gid(url), c) ;
}

export const deleteUpload = async (auth:APIAuth, uid:LocalID) : Promise<string> =>
{
	const service = new UploadService();
	const api = APIServer.api() ;
	let returnValue = undefined ;
	try {
		returnValue = await api.transaction(async trx => {
			const context = {trx:trx} ;
			let upload = await service.objectWithPublicID<IUpload>(uid, context) ;
			const uploadEntity = new UploadEntity(<IUpload>upload);
			if (!$ok(upload)) {
				throw new NotFoundError(`Impossible to find upload with id ${uid}.`);
			}
			if (auth.user !== upload?.user && auth.role !== UserRole.Maintenance && auth.role !== UserRole.System) {
				throw new ForbiddenError(`Upload ${uid} cannot be deleted by user ${auth.user}.`);
			}
			let url = uploadEntity.url() ;
			await uploadEntity.cleanAndDelete(context) ;

			return url ;
		}) ;
	}
	catch (e) {
		// here we have a rollback
		APIServer.api().error(e);
		throw e ;
	}

	return returnValue ;
}
