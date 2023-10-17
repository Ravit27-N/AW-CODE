import {BaseService, TableNames} from "./base.service";
import {Prisma} from "@prisma/client";
import {IUploadTypeMap} from "../classes/interfaces/IUploadTypeMap";
import {injectable} from "tsyringe";
import {UploadEntity, IUpload, UploadCreateInput, UploadWhereInput} from "../entities/uploads";
import {$date2string, $finalDateString, $now} from "../utils/commons";
import {NO_CONTEXT} from "../classes/interfaces/DBConstants";
import {PrismaContext} from "../classes/interfaces/DBInterfaces";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class UploadService extends BaseService<Prisma.UploadDelegate<undefined>, IUploadTypeMap>{
  constructor() {
    super(prisma.upload);
  }
  protected tableName:TableNames = 'upload'

  public async getsWhere(conditions: UploadWhereInput, c: PrismaContext = NO_CONTEXT): Promise<IUpload[]|null>{
    return await this.findMany<IUpload>({where: conditions}, c);
  }

  public async  insert(data: UploadCreateInput, c: PrismaContext = NO_CONTEXT) {
    const now = $date2string($now());
    const expiresAt = $finalDateString(now, <number>data.ttl) ;
    const upload = await this.create<IUpload>({
      data: {...data, expiresAt}
    }, c);
    return new UploadEntity(upload);
  }
}