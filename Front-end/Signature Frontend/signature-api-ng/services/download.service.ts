import {Prisma} from "@prisma/client";
import { injectable } from "tsyringe";
import {BaseService, TableNames} from "./base.service";
import { IDownloadTypeMap } from "../classes/interfaces/IDownloadTypeMap";
import {DownloadEntity, DownloadCreateInput, DownloadUpdateInput, IDownload} from "../entities/downloads";
import {LocalID} from "../api/APIIDs";
import {NO_CONTEXT} from "../classes/interfaces/DBConstants";
import {PrismaContext} from "../classes/interfaces/DBInterfaces";
import {$date2string, $now, $finalDateString} from "../utils/commons";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class DownloadService extends BaseService<
    Prisma.DownloadDelegate<undefined>,
    IDownloadTypeMap>{
    constructor() {
        super(prisma.download);
    }
    protected tableName:TableNames = 'download';

    async insert(data: DownloadCreateInput, c: PrismaContext = NO_CONTEXT) {
        const now = $date2string($now());
        const expiresAt = $finalDateString(now, <number>data.ttl) ;
        let download = await this.create<IDownload>({ data: {...data, expiresAt} }, c);
        return new DownloadEntity(download);
    }

    async modify(data: DownloadUpdateInput, id: LocalID, c: PrismaContext = NO_CONTEXT){
        let download = await this.update<IDownload>({
            where: {
                id: id
            },
            data: data
        }, c)
        return new DownloadEntity(download);
    }

}
