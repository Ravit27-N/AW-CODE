import {Prisma} from "@prisma/client";
import { injectable } from "tsyringe";
import {BaseService, TableNames} from "./base.service";
import {ICertificateTypeMap} from "../classes/interfaces/ICertificateTypeMap";
import {
    CertificateCreateInput,
    CertificateEntity,
    CertificateUpdateInput,
    ICertificate
} from "../entities/certificates";
import {LocalID} from "../api/APIIDs";
import {$date2string, $finalDateString, $now} from "../utils/commons";
import { NO_CONTEXT } from "../classes/interfaces/DBConstants";
import { PrismaContext } from "../classes/interfaces/DBInterfaces";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class CertificateService extends BaseService<
    Prisma.CertificateDelegate<undefined>,
    ICertificateTypeMap>{
    constructor() {
        super(prisma.certificate);
    }
    protected tableName:TableNames = 'certificate';

    public async insert(cert: CertificateCreateInput, c: PrismaContext = NO_CONTEXT) {
        const now = $date2string($now());
        const expiresAt = $finalDateString(now, <number>cert.ttl) ;
        let data = await this.create<ICertificate>({ data: {...cert, expiresAt} }, c);
        return new CertificateEntity(data);
    }
    
    async modify(data: CertificateUpdateInput, id: LocalID, c: PrismaContext = NO_CONTEXT){
        let updated = await this.update<ICertificate>({
            where: {
                id: id
            },
            data: data
        }, c)
        return new CertificateEntity(updated);
    }
    
}
