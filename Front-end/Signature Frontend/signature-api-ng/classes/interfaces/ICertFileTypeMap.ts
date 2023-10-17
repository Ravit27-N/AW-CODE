import {BaseTypeMap} from "./BaseTypeMap";
import {Prisma} from "@prisma/client";

export class ICertificateFileTypeMap implements BaseTypeMap {
  aggregate?: Prisma.CertificateFileAggregateArgs;
  count?: Prisma.CertificateFileCountArgs;
  create?: Prisma.CertificateFileCreateArgs;
  delete?: Prisma.CertificateFileDeleteArgs;
  deleteMany?: Prisma.CertificateFileDeleteManyArgs;
  findFirst?: Prisma.CertificateFileFindFirstArgs;
  findMany?: Prisma.CertificateFileFindManyArgs;
  findUnique?: Prisma.CertificateFileFindUniqueArgs;
  update?: Prisma.CertificateFileUpdateArgs;
  updateMany?: Prisma.CertificateFileUpdateManyArgs;
  upsert?: Prisma.CertificateFileUpsertArgs;
}
