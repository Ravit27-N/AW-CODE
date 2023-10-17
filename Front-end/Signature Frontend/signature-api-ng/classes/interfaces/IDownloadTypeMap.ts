import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class IDownloadTypeMap implements BaseTypeMap {
    aggregate?: Prisma.DownloadAggregateArgs;
    count?: Prisma.DownloadCountArgs;
    create?: Prisma.DownloadCreateArgs;
    delete?: Prisma.DownloadDeleteArgs;
    deleteMany?: Prisma.DownloadDeleteManyArgs;
    findFirst?: Prisma.DownloadFindFirstArgs;
    findMany?: Prisma.DownloadFindManyArgs;
    findUnique?: Prisma.DownloadFindUniqueArgs;
    update?: Prisma.DownloadUpdateArgs;
    updateMany?: Prisma.DownloadUpdateManyArgs;
    upsert?: Prisma.DownloadUpsertArgs;
}
