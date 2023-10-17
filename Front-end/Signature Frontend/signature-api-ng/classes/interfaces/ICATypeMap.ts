import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class ICATypeMap implements BaseTypeMap {
    aggregate?: Prisma.CAAggregateArgs;
    count?: Prisma.CACountArgs;
    create?: Prisma.CACreateArgs;
    delete?: Prisma.CADeleteArgs;
    deleteMany?: Prisma.CADeleteManyArgs;
    findFirst?: Prisma.CAFindFirstArgs;
    findMany?: Prisma.CAFindManyArgs;
    findUnique?: Prisma.CAFindUniqueArgs;
    update?: Prisma.CAUpdateArgs;
    updateMany?: Prisma.CAUpdateManyArgs;
    upsert?: Prisma.CAUpsertArgs;
}

export interface CAData {
    aki:string ;
    longName?:string ;
    cguVersion?:string ;
    cguLocalPath?:string ;
    cguPath?:string ;
    cguSize:number ;
}

export interface ImportedCA {
    aki:string ;
    uuid:string ;
    name:string ;
    longName?:string ;
    cguVersion?:string ;
    cguLocalPath?:string ;
}
