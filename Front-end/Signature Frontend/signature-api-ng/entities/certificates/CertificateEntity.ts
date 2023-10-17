import {BaseEntity} from "../BaseEntity";
import {Prisma} from "@prisma/client";
import {autoInjectable, delay, inject} from "tsyringe";
import {$identifier, $ok, $timestampDb2client} from "../../utils/commons";
import {CaTokenEntity} from "../CATokens";
import { CertificateService } from "../../services/certificate.service";
import {CertificateData} from "../../classes/interfaces/ICertificateTypeMap";
import {LocalID} from "../../api/APIIDs";
import {TableNames} from "../../services/base.service";
import { RelativeIdentifier, PrismaContext } from "../../classes/interfaces/DBInterfaces";
import { InternalError } from "../../utils/errors";
import { ICertificate } from ".";
import { CertFileEntity } from "../certFiles";


@autoInjectable()
export class CertificateEntity extends BaseEntity implements ICertificate {
    static tableName: TableNames = 'certificate';

    public id: LocalID;
    public publicId: LocalID;
    public sessionId: LocalID;
    public expiresAt: Date | null;
    public status: number;
    public ttl: number;
    public user: string | null;
    public certificateData: Prisma.JsonValue;
    public caTokenId: LocalID;

    createdAt: Date | null;

    // relation data
    private _caToken: CaTokenEntity | undefined;
    private _certFiles: CertFileEntity[] | undefined;

    constructor(cert: ICertificate, @inject(delay(() => CertificateService)) private readonly service?: CertificateService) {
        super();
        this.id = Number(cert.id);
        this.publicId = Number(cert.publicId);
        this.caTokenId = Number(cert.publicId);
        this.sessionId = Number(cert.sessionId);
        this.status = cert.status;
        this.ttl = cert.ttl;
        this.user = cert.user;
        this.certificateData = cert.certificateData;
        this.createdAt = cert.createdAt;
        this.expiresAt = cert.expiresAt;
        this._caToken = cert.caToken ? new CaTokenEntity(cert.caToken) : undefined;
        this._certFiles = cert.certFiles ? cert.certFiles?.map(i=> new CertFileEntity(i)) : undefined;
    }

    protected internalUrl(relativeIdentifier?:RelativeIdentifier) : string | null
    {
        return  `/session/${!!relativeIdentifier ? relativeIdentifier : this.getCaToken?.Actor?.getSession?.publicId}/certificate/${this.publicId}`;
    }

    get getCaToken(): CaTokenEntity | undefined {
        return this._caToken;
    }

    set setCaToken(value: CaTokenEntity | undefined) {
        this._caToken = value;
    }

    get getCertFiles(): CertFileEntity[] | undefined {
        return this._certFiles;
    }

    set setCertFiles(value: CertFileEntity[] | undefined) {
        this._certFiles = value;
    }

    public get getCertificateData() {
        return this.parsePrisma<CertificateData>(this.certificateData);
    }

    public getRelated<M>(property: Prisma.CertificateInclude, c: PrismaContext, key?: string) {
        return this.service?.getRelated<M>(property, this.id, c,{}, key);
    }
    
    public unrelate<M>(field: 'manifestFileId'|'activeScenarioId', c: PrismaContext){
        return this.service?.unrelate<M>(field, this.id, c);
    }

    public expirationDate(): string {
        return $timestampDb2client(this.expiresAt);
    }

    public creationDate(): string {
        return $timestampDb2client(this.createdAt);
    }
    public identifier() : string { return `${$identifier(this.sessionId)}-${$identifier(this.caTokenId)}-${$identifier(this.publicId)}` ;}
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