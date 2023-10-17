import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class IDocumentTypeMap implements BaseTypeMap {
    aggregate?: Prisma.DocumentAggregateArgs;
    count?: Prisma.DocumentCountArgs;
    create?: Prisma.DocumentCreateArgs;
    delete?: Prisma.DocumentDeleteArgs;
    deleteMany?: Prisma.DocumentDeleteManyArgs;
    findFirst?: Prisma.DocumentFindFirstArgs;
    findMany?: Prisma.DocumentFindManyArgs;
    findUnique?: Prisma.DocumentFindUniqueArgs;
    update?: Prisma.DocumentUpdateArgs;
    updateMany?: Prisma.DocumentUpdateManyArgs;
    upsert?: Prisma.DocumentUpsertArgs;
}
