import {BaseEntity} from "../BaseEntity";
import {Prisma} from "@prisma/client";
import {autoInjectable, delay, inject} from "tsyringe";
import {OTPService} from "../../services/otp.service";
import { $ok, $timestampDb2client} from "../../utils/commons";
import {LocalID} from "../../api/APIIDs";
import {TableNames} from "../../services/base.service";
import { TokenOtherData, PrismaContext } from "../../classes/interfaces/DBInterfaces";
import { InternalError } from "../../utils/errors";
import {IOTP, OTPUpdateInput} from ".";
import { ActorEntity } from "../actors";

@autoInjectable()
export class OTPEntity extends BaseEntity implements IOTP {
    static tableName: TableNames = 'oTP';
    public id: LocalID;
    public otp: string;
    public otherData: Prisma.JsonValue;
    public createdAt: Date | null;
    public expiresAt: Date | null;
    public ttl: number;
    public actorId: LocalID;

    private _actor?: ActorEntity;

    constructor(otp: IOTP, @inject(delay(() => OTPService)) private readonly service?: OTPService) {
        super();
        this.id = Number(otp.id);
        this.otp = otp.otp;
        this.otherData = otp.otherData;
        this.ttl = otp.ttl;
        this.actorId = Number(otp.actorId);
        this.createdAt = otp.createdAt;
        this.expiresAt = otp.expiresAt;
        this._actor = otp.actor ? new ActorEntity(otp.actor) : undefined;
    }

    get getActor(){
        return this._actor;
    }
    set setActor(value: ActorEntity | undefined){
        this._actor = value;
    }

    get getOtherData(): TokenOtherData | undefined{
        return this.parsePrisma(this.otherData)
    }

    public async update(data: OTPUpdateInput, c: PrismaContext){
        return this.service?.modify(data, this.id, c);
    }

    public creationDate(): string {
        return $timestampDb2client(this.createdAt);
    }
    
    public getRelated<M>(property: Prisma.OTPInclude, c: PrismaContext){
        return this.service?.getRelated<M>(property, this.id, c);
    }

    public expirationDate(): string | null {
        return $ok(this.expiresAt) ? $timestampDb2client(new Date(this.expiresAt!)) : null ;
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
}