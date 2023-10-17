import {UserJwt, Prisma} from "@prisma/client";
import {injectable} from "tsyringe";
import {BaseService, TableNames} from "./base.service";
import {IUserJwtTypeMap} from "../classes/interfaces/IUserJwtTypeMap";
import {UserJwtEntity, IUserJwt, UserJwtWhereInput, UserJwtUpdateInput, UserJwtCreateInput} from "../entities/userJwt";
import {LocalID} from "../api/APIIDs";
import {NO_CONTEXT} from "../classes/interfaces/DBConstants";
import {PrismaContext} from "../classes/interfaces/DBInterfaces";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class UserJwtService extends BaseService<
    Prisma.UserJwtDelegate<undefined>,
    IUserJwtTypeMap>{
    constructor() {
        super(prisma.userJwt);
    }
    protected tableName:TableNames = 'userJwt';
    
    async getsWhere(conditions: UserJwtWhereInput, c: PrismaContext = NO_CONTEXT): Promise<UserJwt[]|null>{
        return await this.findMany<UserJwt>({where: conditions}, c);
    }

    async getWhere(conditions: UserJwtWhereInput, c: PrismaContext = NO_CONTEXT): Promise<UserJwtEntity|null>{
        const item = await this.findFirst<IUserJwt>({where: conditions}, c);
        return item ? new UserJwtEntity(item) : null;
    }
    
    async  insert(data: UserJwtCreateInput, c: PrismaContext = NO_CONTEXT){
        const newData = await this.create<IUserJwt>({
            data: data
        }, c);
        return new UserJwtEntity(newData);
    }
    
    async modify(data: UserJwtUpdateInput, id: LocalID, c: PrismaContext = NO_CONTEXT){
        const updatedData = await this.update<IUserJwt>({
            where: {
                id: id
            },
            data: data
        }, c);
        return new UserJwtEntity(updatedData);
    }
}
