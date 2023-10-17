import { $length, $ok } from '../../utils/commons'
import { $filename } from '../../utils/fs'

import { ForbiddenError, NotFoundError } from '../../utils/errors'

import { APIRoleNames } from '../APIConstants'
import { APIAuth } from '../APIInterfaces'
import {GlobalID} from "../APIIDs";
import {DownloadService} from "../../services/download.service";
import {DownloadEntity, IDownload} from "../../entities/downloads";
import {Prisma, File} from "@prisma/client";
import { PrismaContext } from '../../classes/interfaces/DBInterfaces'


export const downloadFile = async (auth:APIAuth, downloadID:GlobalID) : Promise<[string, string]> => {
	const c: PrismaContext<Prisma.DownloadInclude> = {
		include: {
			session: true
		}
	}
	const downloadService = new DownloadService();
	let download = await downloadService.objectWithPublicID<IDownload>(downloadID, c) ;
	//  we load the session with the download
	if (!$ok(download)) {
		throw new NotFoundError(`Download with ID ${downloadID} was not found.`);
	}
	const downloadEntity = new DownloadEntity(download!);
	if (!(await downloadEntity?.getSession?.acceptsUser(auth.apiRole, auth.user, auth.role, {}))) {
		throw new ForbiddenError(`Session with ID ${downloadEntity?.getSession?.publicId} does not accept user ${auth.user} for action ${APIRoleNames[auth.apiRole]}.`);
	}

	if (auth.user !== download?.user) {
		// FIXME: we may be authorized to get a file even if we're not the creator
		throw new ForbiddenError(`Download ${downloadID} cannot be read by user ${auth.user}.`);
	}
	if ($length(download?.path)) { return [<string>download?.path, $filename(<string>download?.path)] ; }

	const file = await downloadEntity?.getRelated<File>({
		file: true
	}, c);
	if (!$ok(file)) {
		throw new NotFoundError();
	}
	return [(<File>file).path, (<File>file).fileName];
} ;
