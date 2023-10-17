import {BaseEntity} from "../BaseEntity";
import {Prisma} from "@prisma/client";
import {autoInjectable, delay, inject} from "tsyringe";
import {UserJwtService} from "../../services/userJwt.service";
import {$decrypt, $encrypt, $hash, HashMethods} from "../../utils/crypto";
import {$length, $ok, $timestampDb2client} from "../../utils/commons";
import {InternalError} from "../../utils/errors";
import {TableNames} from "../../services/base.service";
import {LocalID} from "../../api/APIIDs";
import { PrismaContext, JWTAuthData } from "../../classes/interfaces/DBInterfaces";
import {IUserJwt, UserJwtCreateInput, UserJwtUpdateInput} from ".";

@autoInjectable()
export class UserJwtEntity extends BaseEntity implements IUserJwt {
    static tableName: TableNames = 'userJwt';

    public id: LocalID;
    public user: string;
    public authData: Prisma.JsonValue;

    createdAt: Date | null
    updatedAt: Date | null

    private static __ck = '81ABD59747024B62AADE1615973630A9' ;
    constructor(user: IUserJwt, @inject(delay(() => UserJwtService)) private readonly service?: UserJwtService) {
        super();
        this.id = Number(user.id);
        this.user = user.user;
        this.authData = user.authData;
        this.createdAt = user.createdAt;
        this.updatedAt = user.updatedAt;
    }

    async insert(data: UserJwtCreateInput, c: PrismaContext){
        return this.service?.insert(data, c);
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

    // TODO: can we move it to base service?
    public async modify(data: UserJwtUpdateInput, c: PrismaContext){
        return this.service?.modify(data, this.id, c);
    }

    public creationDate(): string {
        return $timestampDb2client(this.createdAt);
    }

    public modificationDate(): string {
        return $timestampDb2client(this.updatedAt);
    }
    public get getAuthData(){
        return this.parsePrisma<JWTAuthData>(this.authData);
    }
    public jwt() : string|null {
        return $decrypt(this.getAuthData.jwtA, UserJwtEntity.__ck) as string | null ;
    }

    public static authDataWithJWT(jwt:string) : JWTAuthData {
        if (!$length(jwt)) {
            throw new InternalError('cannot handle and empty JWT') ;
        }
        const encrypted = $encrypt(jwt, UserJwtEntity.__ck) ;
        if (!$ok(encrypted)) {
            throw new InternalError('cannot prepare JWT data') ;
        }
        const hash = $hash(Buffer.from(<string>encrypted), HashMethods.SHA256) ;
        if (!$ok(hash)) {
            throw new InternalError('cannot hash JWT data') ;
        }
        return {
            jwtA:<string>encrypted,
            jwtB:hash!
        }
    }
}