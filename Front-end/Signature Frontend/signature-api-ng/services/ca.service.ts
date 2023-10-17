import { Prisma} from "@prisma/client";
import { injectable } from "tsyringe";
import { BaseService, TableNames} from "./base.service";
import { ICATypeMap} from "../classes/interfaces/ICATypeMap";
import { NO_CONTEXT } from "../classes/interfaces/DBConstants";
import { PrismaContext } from "../classes/interfaces/DBInterfaces";
import {CACreateInput, CAEntity, CAUpdateInput, ICA} from "../entities/CAs";
import {prisma} from "../classes/interfaces/prisma";
@injectable()
export class CAService extends BaseService<
    Prisma.CADelegate<undefined>,
    ICATypeMap>{
    constructor() {
        super(prisma.cA);
    }
    protected tableName:TableNames = 'cA';

    public async insert(session: CACreateInput, c: PrismaContext = NO_CONTEXT) {
        const ca = await this.create<ICA>({ data: session }, c);
        return new CAEntity(ca);
    }
    
    public async modify(data: CAUpdateInput, id: number, c: PrismaContext = NO_CONTEXT){
        let ca = await this.update<ICA>({
            where: {
                id: id
            },
            data: data
        }, c)
        return new CAEntity(ca);
    }
    
}
