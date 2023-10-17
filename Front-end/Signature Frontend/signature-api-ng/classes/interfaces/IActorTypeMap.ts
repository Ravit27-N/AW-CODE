import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class IActorTypeMap implements BaseTypeMap {
    aggregate?: Prisma.ActorAggregateArgs;
    count?: Prisma.ActorCountArgs;
    create?: Prisma.ActorCreateArgs;
    delete?: Prisma.ActorDeleteArgs;
    deleteMany?: Prisma.ActorDeleteManyArgs;
    findFirst?: Prisma.ActorFindFirstArgs;
    findMany?: Prisma.ActorFindManyArgs;
    findUnique?: Prisma.ActorFindUniqueArgs;
    update?: Prisma.ActorUpdateArgs;
    updateMany?: Prisma.ActorUpdateManyArgs;
    upsert?: Prisma.ActorUpsertArgs;
}
