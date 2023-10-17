import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class IOTPTypeMap implements BaseTypeMap {
    aggregate?: Prisma.OTPAggregateArgs;
    count?: Prisma.OTPCountArgs;
    create?: Prisma.OTPCreateArgs;
    delete?: Prisma.OTPDeleteArgs;
    deleteMany?: Prisma.OTPDeleteManyArgs;
    findFirst?: Prisma.OTPFindFirstArgs;
    findMany?: Prisma.OTPFindManyArgs;
    findUnique?: Prisma.OTPFindUniqueArgs;
    update?: Prisma.OTPUpdateArgs;
    updateMany?: Prisma.OTPUpdateManyArgs;
    upsert?: Prisma.OTPUpsertArgs;
}
