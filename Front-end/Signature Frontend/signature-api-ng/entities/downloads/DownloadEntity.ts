import {BaseEntity} from "../BaseEntity";
import {Prisma} from "@prisma/client";
import {autoInjectable, delay, inject} from "tsyringe";
import {DownloadService} from "../../services/download.service";
import {SessionEntity} from "../sessions";
import { $ok, $timestampDb2client} from "../../utils/commons";
import {FileEntity} from "../files";
import {LocalID} from "../../api/APIIDs";
import {TableNames} from "../../services/base.service";
import { RelativeIdentifier, PrismaContext } from "../../classes/interfaces/DBInterfaces";
import { InternalError } from "../../utils/errors";
import {DownloadUpdateInput, IDownload} from "./type";

@autoInjectable()
export class DownloadEntity extends BaseEntity implements IDownload {
    static tableName: TableNames = 'download';

    public id: LocalID;
    public publicId: LocalID;
    public user: string;
    public downloadsCount: number;
    public expiresAt: Date | null;
    public path: string | null;
    public size: number;
    public ttl: number;
    public fileId: LocalID | null ;
    public sessionId: LocalID;

    public createdAt: Date | null;
    public updatedAt: Date | null;
    
    private declare _session: SessionEntity | undefined ;
    private declare _file: FileEntity | undefined;
    constructor(download: IDownload, @inject(delay(() => DownloadService)) private readonly service?: DownloadService) {
        super();
        this.id = Number(download.id);
        this.publicId = Number(download.publicId);
        this.user = download.user;
        this.downloadsCount = download.downloadsCount;
        this.path = download.path;
        this.size = download.size;
        this.ttl = download.ttl;
        this.fileId = Number(download.fileId)
        this.sessionId = Number(download.sessionId);
        this.createdAt = download.createdAt;
        this.updatedAt = download.updatedAt;
        this.expiresAt = download.expiresAt;
        
        this.setSession = download.session ? new SessionEntity(download.session) : undefined;
        this.setFile = download.files ? new FileEntity(download.files): undefined;
    }
    
    public get getSession(): SessionEntity | undefined {
        return this._session;
    }

    public set setSession(value: SessionEntity | undefined) {
        this._session = value;
    }

    public get getFile(): FileEntity|undefined {
        return this._file;
    }

    public set setFile(value: FileEntity|undefined) {
        this._file = value;
    }

    public expirationDate(): string | null {
        return $ok(this.expiresAt) ? $timestampDb2client(new Date(this.expiresAt!)): null ;
    }
    protected internalUrl(_?:RelativeIdentifier) : string | null {
        return  `/download/${this.publicId}`;
    }

    public creationDate(): string {
        return $timestampDb2client(this.createdAt);
    }

    public modificationDate(): string {
        return $timestampDb2client(this.updatedAt);
    }
    
    public async update(data: DownloadUpdateInput, c?: PrismaContext){
        return this.service?.modify(data, this.id, c);
    }

    //
    public getRelated<M>(property: Prisma.DownloadInclude, c: PrismaContext){
        return this.service?.getRelated<M>(property, this.id, c);
    }

    public async delete(c: PrismaContext) {

        if (!$ok(c.trx)) {
            throw new InternalError('$delete() should be called inside a transaction');
        }
        const data: any = {
            where: {
                id: this.id
            }
        };
        return await this.service?.delete(data, c);
    }
    
}