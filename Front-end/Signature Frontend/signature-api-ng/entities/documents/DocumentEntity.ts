import { BaseEntity } from "../BaseEntity";
import { Prisma } from "@prisma/client";
import { autoInjectable, delay, inject } from "tsyringe";
import { DocumentService } from "../../services/document.service";
import { DocumentStatus, SessionStatus } from "../../api/APIConstants";
import {
    $count,
    $identifier,
    $isunsigned,
    $length,
    $ok,
    $timestampDb2client,
    stringifyPrisma
} from "../../utils/commons";
import { SessionService } from "../../services/session.service";
import { SessionEntity, ISession } from "../sessions";
import { OTPService } from "../../services/otp.service";
import { LocalID } from "../../api/APIIDs";
import JsonValue = Prisma.JsonValue;
import { DocumentResource, ManifestData, UserData } from "../../api/APIInterfaces";
import { TableNames } from "../../services/base.service";
import { DocumentOtherData, RelativeIdentifier, PrismaContext, TokenOtherData, LastFile } from "../../classes/interfaces/DBInterfaces";
import { InternalError } from "../../utils/errors";
import {DocumentUpdateInput, DocumentWhereInput, IDocument} from ".";
import {DocFileEntity} from "../docFiles";
import {FileEntity} from "../files";

@autoInjectable()
export class DocumentEntity extends BaseEntity implements IDocument {
    static tableName: TableNames = 'document';

    public id: LocalID;
    public publicId: LocalID;
    public abstract: string | null;
    public fileName: string;
    public title: string;
    public otherData: Prisma.JsonValue = {};
    public userData: Prisma.JsonValue = {};
    public manifestData: Prisma.JsonValue = {};
    public fileId: LocalID | null;
    public sessionId: LocalID;

    private _session?: SessionEntity;
    private _docFiles?: DocFileEntity[];
    private _genuineFile?: FileEntity;

    public createdAt: Date | null;
    public updatedAt: Date | null;
    constructor(
        document: IDocument,
        @inject(delay(() => DocumentService)) private readonly service?: DocumentService,
        @inject(delay(() => SessionService)) private readonly sessionService?: SessionService) {
        super();
        this.id = Number(document.id);
        this.publicId = Number(document.publicId);
        this.abstract = document.abstract;
        this.fileName = document.fileName;
        this.title = document.title;
        this.fileId = Number(document.fileId);
        this.sessionId = Number(document.sessionId);
        this.updatedAt = document.updatedAt;
        this.createdAt = document.createdAt;
        this.otherData = document.otherData;
        this.manifestData = document.manifestData;
        this.userData = document.userData;
        this._docFiles = document.docFiles ? document.docFiles?.map(i=> new DocFileEntity(i)) : undefined;
        this._genuineFile = document.genuineFile ? new FileEntity(document.genuineFile) : undefined;
        this._session = document.session ? new SessionEntity(document.session) : undefined;
    }

    get getSession(){
        return this._session;
    }
    set setSession(value: SessionEntity | undefined){
        this._session = value;
    }

    get getDocFiles(){
        return this._docFiles;
    }
    set setDocFiles(value: DocFileEntity[] | undefined){
        this._docFiles = value;
    }

    get getGenuineFile(){
        return this._genuineFile;
    }
    set setGenuineFile(value: FileEntity | undefined){
        this._genuineFile = value;
    }

    public get getManifestData() {
        return this.parsePrisma<ManifestData>(this.manifestData);
    }
    public get getUserData() {
        return this.parsePrisma<UserData>(this.userData)
    }
    public get getOtherData() {
        return $ok(this.otherData) ? this.parsePrisma<DocumentOtherData>(this.otherData) : null;
    }

    public set setOtherData(data: JsonValue) {
        this.otherData = data
    }

    protected internalUrl(relativeIdentifier?: RelativeIdentifier): string | null 
    { 
        return `/session/${!!relativeIdentifier ? relativeIdentifier : this.getSession?.publicId}/document/${this.publicId}`;
    }

    public async modify(data: DocumentUpdateInput, c: PrismaContext) {
        const dataReturn = await this.service?.update<IDocument>({
            where: {
                id: this.id
            },
            data: data
        }, c);
        return new DocumentEntity(<IDocument>dataReturn);
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
        return this.service?.delete(data, c);
    }
    //
    public getRelated<M>(property: Prisma.DocumentInclude, c: PrismaContext, condition?: DocumentWhereInput) {
        return this.service?.getRelated<M>(property, this.id, c, condition);
    }

    public async mySession(c: PrismaContext): Promise<SessionEntity> {

        const session = await this.sessionService?.findFirst<ISession>({
            where: {
                id: this.sessionId
            }
        }, c)

        if (!$ok(session)) {
            throw new Error('Database loading session error');
        }
        return new SessionEntity(<ISession>session);
    }

    public async documentStatus(c: PrismaContext): Promise<DocumentStatus> {
        const session = await this.mySession(c);
        return await session.findDocumentStatus(this.publicId, c);
    }

    public async canBeDeleted(c: PrismaContext): Promise<boolean> {
        const session = await this.mySession(c);

        if (session.status !== SessionStatus.Genuine) return false;

        const scenarios = await session.scenariosUnderConstruction(c);
        if ($ok(scenarios)) {
            for (let s of scenarios!) {
                const otherData = s.getOtherData
                if (otherData.dids.includes(this.publicId)) return false;
            }
        }

        const otpService = new OTPService();
        let tokens = await otpService.findMany<TokenOtherData>({
            where: {
                actor: {
                    sessionId: this.sessionId
                }
            },
            select: {
                otherData: true,
                actor: true
            }
        })

        if ($count(tokens)) {
            for (let t of tokens) { // this is a bad cast since we only have otherData in this object.
                if (t.dids?.includes(this.publicId)) return false;
            }
        }
        return true;
    }

    public getGenuineFiles(): LastFile[] {
        return [{ fileId: <number>this.fileId }];
    }

    public getLastFiles(aid?: LocalID): LastFile[] {
        if (!$ok(this.getOtherData?.lastFiles)) {
            return this.getGenuineFiles();
        }
        if ($isunsigned(aid) && <number>aid > 0) {
            const data = this.getOtherData?.lastFiles.filter(e => e.aid === aid);
            this.setOtherData = stringifyPrisma(data);
        }
        return this.getOtherData!.lastFiles;
    }
    public creationDate(): string {
        return $timestampDb2client(this.createdAt);
    }

    public modificationDate(): string {
        return $timestampDb2client(this.updatedAt);
    }
    public async toAPI(c: PrismaContext): Promise<DocumentResource> {
        const session = await this.mySession(c);
        let returnValue: DocumentResource = {
            id: session.publicId,
            date: this.creationDate(),
            did: this.publicId,
            'file-name': this.fileName,
            title: this.title,
            status: (await this.documentStatus(c)),
        };
        if ($length(this.abstract)) {
            returnValue.abstract = this.abstract;
        }
        if ($ok(this.manifestData)) {
            returnValue['manifest-data'] = this.getManifestData;
        }
        if ($ok(this.userData)) {
            returnValue['user-data'] = this.getUserData;
        }
        return returnValue;
    }

    public identifier(): string { return `${$identifier(this.sessionId)}-${$identifier(this.publicId)}`; }
}