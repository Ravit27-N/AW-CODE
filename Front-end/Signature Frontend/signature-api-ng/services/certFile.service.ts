import {BaseService, TableNames} from "./base.service";
import {CertificateFile, Prisma} from "@prisma/client";
import {injectable} from "tsyringe";
import {ICertificateFileTypeMap} from "../classes/interfaces/ICertFileTypeMap";
import {
  CertFileEntity,
  CertificateFileCreateInput,
  CertificateFileWhereInput,
  ICertificateFile
} from "../entities/certFiles";
import { NO_CONTEXT } from "../classes/interfaces/DBConstants";
import { PrismaContext } from "../classes/interfaces/DBInterfaces";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class CertFileService extends BaseService<
Prisma.CertificateFileDelegate<undefined>,
ICertificateFileTypeMap>{
  constructor() {
    super(prisma.certificateFile);
  }
  protected tableName:TableNames = 'certificateFile';

  public async getsWhere(conditions: CertificateFileWhereInput, c: PrismaContext = NO_CONTEXT): Promise<CertificateFile[]|null>{
    return await this.findMany<CertificateFile>({where: conditions}, c);
  }

  public async  insert(data: CertificateFileCreateInput, c: PrismaContext = NO_CONTEXT){
    const certFile = await this.create<ICertificateFile>({
      data: data
    }, c);
    return new CertFileEntity(certFile);
  }

}
