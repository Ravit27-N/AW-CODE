import {BaseService, TableNames} from "./base.service";
import {File, Prisma} from "@prisma/client";
import {IFileTypeMap} from "../classes/interfaces/IFileTypeMap";
import {injectable} from "tsyringe";
import {FileCreateInput, FileEntity, FileUpdateInput, FileWhereInput, IFile} from "../entities/files";
import { NO_CONTEXT } from "../classes/interfaces/DBConstants";
import { PrismaContext } from "../classes/interfaces/DBInterfaces";
import {LocalID} from "../api/APIIDs";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class FileService extends BaseService<
Prisma.FileDelegate<undefined>,
IFileTypeMap>{
  constructor() {
    super(prisma.file);
  }
  protected tableName:TableNames = 'file';

  public async getsWhere(conditions: FileWhereInput, c: PrismaContext = NO_CONTEXT): Promise<File[]|null>{
    return await this.findMany<File>({where: conditions}, c);
  }

  public async  insert(data: FileCreateInput, c: PrismaContext = NO_CONTEXT){
    const file = await this.create<IFile>({
      data: data
    }, c);
    return new FileEntity(file);
  }

  async modify(data: FileUpdateInput, id: LocalID, c: PrismaContext = NO_CONTEXT) {
    let file = await this.update<IFile>({
      where: {
        id: id
      },
      data: data
    }, c)
    return new FileEntity(file);
  }

}
