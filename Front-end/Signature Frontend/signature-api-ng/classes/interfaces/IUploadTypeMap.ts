import {BaseTypeMap} from "./BaseTypeMap";
import {Prisma} from "@prisma/client";

export class IUploadTypeMap implements BaseTypeMap{
  aggregate?: Prisma.UploadAggregateArgs;
  count?: Prisma.UploadCountArgs;
  create?: Prisma.UploadCreateArgs;
  delete?: Prisma.UploadDeleteArgs;
  deleteMany?: Prisma.UploadDeleteManyArgs;
  findFirst?: Prisma.UploadFindFirstArgs;
  findMany?: Prisma.UploadFindManyArgs;
  findUnique?: Prisma.UploadFindUniqueArgs;
  update?: Prisma.UploadUpdateArgs;
  updateMany?: Prisma.UploadUpdateManyArgs;
  upsert?: Prisma.UploadUpsertArgs;
}