import { Prisma} from "@prisma/client";
import { injectable } from "tsyringe";
import {BaseService, TableNames} from "./base.service";
import {IOTPTypeMap} from "../classes/interfaces/IOTPTypeMap";
import {OTPCreateInput, OTPEntity, OTPUpdateInput, IOTP} from "../entities/otp";
import {LocalID} from "../api/APIIDs";
import {$date2string, $finalDateString, $now} from "../utils/commons";
import {NO_CONTEXT} from "../classes/interfaces/DBConstants";
import {PrismaContext} from "../classes/interfaces/DBInterfaces";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class OTPService extends BaseService<
    Prisma.OTPDelegate<undefined>,
    IOTPTypeMap>{
    constructor() {
        super(prisma.oTP);
    }
    protected tableName:TableNames = 'oTP';

    async insert(otp: OTPCreateInput, c: PrismaContext = NO_CONTEXT) {
        const now = $date2string($now());
        const expiresAt = $finalDateString(now, <number>otp.ttl) ;
        const data = await this.create<IOTP>({ data: {...otp, expiresAt} }, c);
        return new OTPEntity(data);
    }
    
    async modify(data: OTPUpdateInput, id: LocalID, c: PrismaContext = NO_CONTEXT){
        let otp = await this.update<IOTP>({
            where: {
                id: id
            },
            data: data
        }, c)
        return new OTPEntity(otp);
    }
    
}
