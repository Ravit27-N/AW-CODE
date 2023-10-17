import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class ICertificateTypeMap implements BaseTypeMap {
    aggregate?: Prisma.CertificateAggregateArgs;
    count?: Prisma.CertificateCountArgs;
    create?: Prisma.CertificateCreateArgs;
    delete?: Prisma.CertificateDeleteArgs;
    deleteMany?: Prisma.CertificateDeleteManyArgs;
    findFirst?: Prisma.CertificateFindFirstArgs;
    findMany?: Prisma.CertificateFindManyArgs;
    findUnique?: Prisma.CertificateFindUniqueArgs;
    update?: Prisma.CertificateUpdateArgs;
    updateMany?: Prisma.CertificateUpdateManyArgs;
    upsert?: Prisma.CertificateUpsertArgs;
}

export interface CertificateData {
    aki?: string ;
    countryName:string ;
    data:string, // base64 certificate conten ;
    givenName:string ;
    id:string ;
    lifespan:number ;
    notAfter:string ;
    notBefore:string ;
    organizationName?:string ;
    organizationUnitName?:string ;
    password:string ;
    serialnumber:string ;
    surname:string ;
    ski?:string ;
}