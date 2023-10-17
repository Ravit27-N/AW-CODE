import {BaseTypeMap} from "./BaseTypeMap";
import {Prisma} from "@prisma/client";

export class IFileTypeMap implements BaseTypeMap {
  aggregate?: Prisma.FileAggregateArgs;
  count?: Prisma.FileCountArgs;
  create?: Prisma.FileCreateArgs;
  delete?: Prisma.FileDeleteArgs;
  deleteMany?: Prisma.FileDeleteManyArgs;
  findFirst?: Prisma.FileFindFirstArgs;
  findMany?: Prisma.FileFindManyArgs;
  findUnique?: Prisma.FileFindUniqueArgs;
  update?: Prisma.FileUpdateArgs;
  updateMany?: Prisma.FileUpdateManyArgs;
  upsert?: Prisma.FileUpsertArgs;
}
