import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class ISessionTypeMap implements BaseTypeMap {
    aggregate?: Prisma.SessionAggregateArgs;
    count?: Prisma.SessionCountArgs;
    create?: Prisma.SessionCreateArgs;
    delete?: Prisma.SessionDeleteArgs;
    deleteMany?: Prisma.SessionDeleteManyArgs;
    findFirst?: Prisma.SessionFindFirstArgs;
    findMany?: Prisma.SessionFindManyArgs;
    findUnique?: Prisma.SessionFindUniqueArgs;
    update?: Prisma.SessionUpdateArgs;
    updateMany?: Prisma.SessionUpdateManyArgs;
    upsert?: Prisma.SessionUpsertArgs;
}
