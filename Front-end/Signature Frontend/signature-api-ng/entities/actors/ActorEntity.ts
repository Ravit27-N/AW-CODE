import { BaseEntity } from "../BaseEntity";
import { Prisma } from "@prisma/client";
import { ActorService } from "../../services/actor.service";
import { autoInjectable, delay, inject } from "tsyringe";
import { SessionStatus } from "../../api/APIConstants";
import { $length, $ok, $timestampDb2client } from "../../utils/commons";
import { APIServer } from "../../server";
import { $inspect } from "../../utils/utils";
import { CaTokenEntity, ICaToken} from "../CATokens";
import {ScenarioEntity} from "../scenario";
import {ISession, SessionEntity} from "../sessions";
import {OTPService} from "../../services/otp.service";
import {CaTokenService} from "../../services/caToken.service";
import {IOTP, OTPEntity} from "../otp";
import {ActorResource, ManifestData, UserData} from "../../api/APIInterfaces";
import {APICountry} from "../../api/APICountries";
import {LocalID} from "../../api/APIIDs";
import {TableNames} from "../../services/base.service";
import {PrismaContext, RelativeIdentifier} from "../../classes/interfaces/DBInterfaces";
import {InternalError} from "../../utils/errors";
import { ActorUpdateInput, IActor} from ".";

@autoInjectable()
export class ActorEntity extends BaseEntity implements IActor {
    static tableName: TableNames = 'actor';
    public id: LocalID;
    public publicId: LocalID;
    public administrativeCode: string | null;
    public country: string;
    public email: string;
    public firstName: string | null;
    public login: string | null;
    public name: string;
    public mobile: string | null;
    public authType: number;
    public type: number;
    public rolesArray: Prisma.JsonValue;
    public userData: Prisma.JsonValue;
    public manifestData: Prisma.JsonValue;
    public sessionId: LocalID;
    createdAt: Date | null;
    updatedAt: Date | null;

    // relationships
    private declare _session: SessionEntity | undefined;
    private declare _tokens: OTPEntity[] | undefined; 	  // these are the OTP tokens
    private declare _caTokens: CaTokenEntity[] | undefined; // those are the Certification Authorities tokens

    constructor(actor: IActor, @inject(delay(() => ActorService)) private readonly service?: ActorService) {
        super();
        this.id = Number(actor.id);
        this.publicId = Number(actor.publicId);
        this.administrativeCode = this.decryptField(actor.administrativeCode);
        this.country = actor.country;
        this.email = this.decryptField(actor.email);
        this.firstName = this.decryptField(actor.firstName);
        this.login = actor.login;
        this.name = this.decryptField(actor.name);
        this.mobile = this.decryptField(actor.mobile);
        this.authType = actor.authType;
        this.type = actor.type;
        this.rolesArray = actor.rolesArray;
        this.userData = actor.userData;
        this.manifestData = actor.manifestData;
        this.sessionId = Number(actor.sessionId);
        this.createdAt = actor.createdAt;
        this.updatedAt = actor.updatedAt;
        this._caTokens = actor.caTokens ? actor.caTokens?.map(i => new CaTokenEntity(i)) : undefined;
        this._tokens = actor.otps ? actor.otps?.map(i => new OTPEntity(i)) : undefined;
        this._session = actor.session ? new SessionEntity(actor.session) : undefined;
    }

    session?: ISession | undefined;
    catokens?: ICaToken[] | undefined;
    otps?: IOTP[] | undefined;

    get getRoleArray(): string[] {
        return <string[]>this.rolesArray;
    }
    
    public async update(data: ActorUpdateInput, c: PrismaContext) {
        const update = await this.service?.update<IActor>({
            where: {
                id: this.id
            },
            data: data
        }, c);
        return new ActorEntity(<IActor>update);
    }

    public get getSession(): SessionEntity | undefined {
        return this._session;
    }
    public set setSession(value: SessionEntity | undefined) {
        this._session = value;
    }
    public creationDate(): string {
        return $timestampDb2client(this.createdAt);
    }

    public modificationDate(): string {
        return $timestampDb2client(this.updatedAt);
    }
    public async getRelated<M>(property: Prisma.ActorInclude, c: PrismaContext) {
        return this.service?.getRelated<M>(property, this.id, c);
    }
    public async mySession(c: PrismaContext): Promise<SessionEntity | undefined> {
        if (!$ok(this.getSession)) {
            let session = await this.getRelated<ISession>({ session: true }, c);
            this.setSession = new SessionEntity(<ISession>session);
            if (!$ok(this.getSession)) {
                throw new Error('Database loading session error');
            }
        }
        return this.getSession;
    }

    public async canBeDeleted(c: PrismaContext): Promise<boolean> {
        const session = await this.mySession(c);
        if (session?.status !== SessionStatus.Genuine) return false;

        const scenarios = await session.scenariosUnderConstruction(c);
        if ($ok(scenarios)) {
            for (let s of <ScenarioEntity[]>scenarios) {
                if (s.getOtherData.aids.includes(this.publicId)) return false;
            }
        }

        const api = APIServer.api();
        const otpService = new OTPService();
        const caTokenService = new CaTokenService();
        const res1 = await otpService.count({
            where: {
                actorId: this.id,
                actor: {
                    sessionId: this.sessionId
                }
            }
        });
        api.log(`TOKEN QUERY RESULTS = ${$inspect(res1)}`)
        if (res1 > 0) { return false; }

        const res2 = await caTokenService.count({
            where: {
                actorId: this.id,
                sessionId: this.sessionId
            }
        })
        api.log(`CA-TOKEN QUERY RESULTS = ${$inspect(res2)}`)
        return res2 <= 0;
    }

    public identifier(): string {
        return `${this.sessionId.toString(16)}-${this.publicId.toString(16)}`;
    }

    protected internalUrl(relativeIdentifier?: RelativeIdentifier): string | null {
        return `/session/${!!relativeIdentifier ? relativeIdentifier : this.getSession?.publicId}/actor/${this.publicId}`;
    }

    completeName(): string {
        // we don't check the type here since firstName is not set for the legal type
        const n = this.name.toUpperCase()
        return $length(this.firstName) ? `${this.firstName!.capitalize()} ${n}` : n;
    }

    public async toAPI(c: PrismaContext): Promise<ActorResource> {
        let returnValue: ActorResource = {
            aid: this.publicId,
            date: this.creationDate(),
            id: (await this.mySession(c))?.publicId ?? 0,
            country: <APICountry>this.country,
            email: this.email,
            name: this.name,
            roles: this.getRoleArray,
            type: this.type
        };
        if ($length(this.firstName)) {
            returnValue['first-name'] = this.firstName;
        }
        if ($length(this.mobile)) {
            returnValue.mobile = this.mobile;
        }
        if ($ok(this.manifestData)) {
            returnValue['manifest-data'] = <ManifestData>this.manifestData;
        }
        if ($ok(this.userData)) {
            returnValue['user-data'] = <UserData>this.userData;
        }
        return returnValue;
    };
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