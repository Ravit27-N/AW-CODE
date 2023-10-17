import { Prisma} from "@prisma/client";
import {injectable} from "tsyringe";
import {BaseService, TableNames} from "./base.service";
import {IActorTypeMap} from "../classes/interfaces/IActorTypeMap";
import {ActorCreateInput, ActorEntity, ActorWhereInput, IActor} from "../entities/actors";
import { NO_CONTEXT } from "../classes/interfaces/DBConstants";
import { PrismaContext } from "../classes/interfaces/DBInterfaces";
import {LocalID} from "../api/APIIDs";
import {stringifyPrisma} from "../utils/commons";
import env from "../env-config";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class ActorService extends BaseService<
    Prisma.ActorDelegate<undefined>,
    IActorTypeMap>{
    constructor() {
        super(prisma.actor);
    }
    protected tableName: TableNames = 'actor';
    
    public async getsWhere(conditions: ActorWhereInput, c: PrismaContext = NO_CONTEXT): Promise<ActorEntity[]|null>{
        const actors = await this.findMany<IActor>({where: conditions}, c);
        return actors.map(i=> new ActorEntity(i));
    }

    public async insert(data: ActorCreateInput, sessionId: LocalID, c: PrismaContext = NO_CONTEXT): Promise<ActorEntity> {
        const model = c.trx ? c.trx : prisma;
        const encryptKey = env.AES_ENCRYPT_KEY ?? '';
        
        const insertActor = await model.$executeRaw(
            Prisma.sql`INSERT INTO actors (public_id, name, email, first_name, mobile, administrative_code, country, login,
                                           auth_type, type, roles_array, user_data, manifest_data, session_id)
                       VALUES (${data.publicId}, hex(aes_encrypt(${data.name}, unhex(${encryptKey}))),
                               hex(aes_encrypt(${data.email}, unhex(${encryptKey}))),
                               hex(aes_encrypt(${data.firstName}, unhex(${encryptKey}))),
                               hex(aes_encrypt(${data.mobile}, unhex(${encryptKey}))),
                               hex(aes_encrypt(${data.administrativeCode}, unhex(${encryptKey}))),
                               ${data.country}, ${data.login},
                               ${data.authType}, ${data.type},
                               ${stringifyPrisma(data.rolesArray)},
                               ${data.userData}, ${data.manifestData}, ${sessionId})`
        ).then(async () =>{
            return  model.$queryRaw<{id:number}[]>(
                Prisma.sql`SELECT LAST_INSERT_ID() as id`
            );
        });
        // TODO: do not select it again, just map it to IActor
        const actor = await this.findFirst<IActor>({
            where: {
                id: insertActor[0]?.id
            }
        }, c);
        
        return new ActorEntity(<IActor>actor);
    }
    
}
