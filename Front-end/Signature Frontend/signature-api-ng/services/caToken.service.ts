import {CaToken, Prisma} from "@prisma/client";
import {injectable} from "tsyringe";
import {BaseService, TableNames} from "./base.service";
import {ICaTokenTypeMap} from "../classes/interfaces/ICATokenTypeMap";
import {CaTokenEntity, CaTokenWhereInput, ICaToken} from "../entities/CATokens";
import { NO_CONTEXT } from "../classes/interfaces/DBConstants";
import { PrismaContext } from "../classes/interfaces/DBInterfaces";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class CaTokenService extends BaseService<
    Prisma.CaTokenDelegate<undefined>,
    ICaTokenTypeMap>{
    constructor() {
        super(prisma.caToken);
    }
    protected tableName:TableNames = 'caToken';
    
    async getsWhere(conditions: CaTokenWhereInput, c: PrismaContext = NO_CONTEXT): Promise<ICaToken[]|null>{
        return await this.findMany<ICaToken>({where: conditions}, c);
    }

    async getWhere(conditions: CaTokenWhereInput, c: PrismaContext = NO_CONTEXT): Promise<CaToken|null>{
        return await this.findFirst<CaToken>({where: conditions}, c);
    }
    
    async  insert(data: Prisma.CaTokenCreateInput, c: PrismaContext = NO_CONTEXT){
        const caToken = await this.create<ICaToken>({
            data: data
        }, c);
        return new CaTokenEntity(caToken);
    }
    
    async modify(data: Prisma.CaTokenUpdateInput, id: number, c: PrismaContext = NO_CONTEXT){
        const caToken = await this.update<ICaToken>({
            where: {
                id: id
            },
            data: data
        }, c);
        return new CaTokenEntity(caToken);
    }
    
}
