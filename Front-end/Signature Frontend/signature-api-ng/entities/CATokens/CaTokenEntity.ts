import {BaseEntity} from "../BaseEntity";
import {Prisma} from "@prisma/client";
import {autoInjectable, delay, inject} from "tsyringe";
import {CaTokenService} from "../../services/caToken.service";
import {CAEntity} from "../CAs";
import {ActorEntity} from "../actors";
import {CertificateEntity} from "../certificates";
import {LocalID} from "../../api/APIIDs";
import {TableNames} from "../../services/base.service";
import { CAStatus } from "../../classes/interfaces/DBConstants";
import { PrismaContext } from "../../classes/interfaces/DBInterfaces";
import { $ok } from "../../utils/commons";
import { InternalError } from "../../utils/errors";
import { ICaToken } from ".";


@autoInjectable()
export class CaTokenEntity extends BaseEntity implements ICaToken {
    static tableName: TableNames = 'caToken';
    public id: LocalID;
    public sessionId: LocalID;
    public token: string;
    public status: number;
    public cguVersion: string;
    public actorId: LocalID;
    public caId: LocalID;
    createdAt: Date | null
    updatedAt: Date | null


    declare private _actor: ActorEntity | undefined; 
    private declare _authority: CAEntity | undefined ;
    declare private _certificates: CertificateEntity[] | undefined ;
   
    constructor(caToken: ICaToken, @inject(delay(() => CaTokenService)) private readonly service?: CaTokenService) {
        super();
        this.id = Number(caToken.id);
        this.sessionId = Number(caToken.sessionId);
        this.status = caToken.status;
        this.token = caToken.token;
        this.cguVersion = caToken.cguVersion;
        this.actorId = Number(caToken.actorId);
        this.caId = Number(caToken.caId);
        this.createdAt = caToken.createdAt;
        this.updatedAt = caToken.updatedAt;
        this._actor = caToken.actor ? new ActorEntity(caToken.actor) : undefined;
        this._authority = caToken.ca ? new CAEntity(caToken.ca) : undefined;
    }
    
    public isValid() : boolean { return this.status == CAStatus.Valid ; }
    
    public set Actor(actor: ActorEntity | undefined){
        this._actor = actor;
    }
    
    public get Actor(): ActorEntity | undefined{
        return this._actor;
    }
    public set Authority(authority: CAEntity | undefined){
        this._authority = authority;
    }
    public get Authority(): CAEntity | undefined {
        return this._authority ? new CAEntity(this._authority): undefined;
    }

    public set setCertificates(certificates: CertificateEntity[]){
        this._certificates = certificates;
    }

    async insert(data: Prisma.CaTokenCreateInput, c: PrismaContext){
        return this.service?.insert(data, c);
    }

    // TODO: can we move it to base service?
    public async modify(data: Prisma.CaTokenUpdateInput, c: PrismaContext){
        return this.service?.modify(data, this.id, c);
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
    public getRelated<M>(property: Prisma.CaTokenInclude, c: PrismaContext){
        return this.service?.getRelated<M>(property, this.id, c);
    }
    
}