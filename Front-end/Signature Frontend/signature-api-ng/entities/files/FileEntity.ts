import {Prisma} from "@prisma/client"
import {BaseEntity} from "../BaseEntity";
import {
  $copyFile,
  $ext,
  $filename,
  $isfile,
  $path,
  $removeFile,
  $withoutext,
  $writeBuffer,
  $writeString
} from "../../utils/fs";
import {APIAuth} from "../../api/APIInterfaces";
import {$length, $ok, $string} from "../../utils/commons";
import {FileError, InternalError} from "../../utils/errors";
import {$hash, $hashfile, $uuid} from "../../utils/crypto";
import {Certigna} from "../../classes/CertignaEndPoint";
import env from "../../env-config";
import {TableNames} from "../../services/base.service";
import {FileStatus, NO_CONTEXT} from "../../classes/interfaces/DBConstants";
import { PrismaContext } from "../../classes/interfaces/DBInterfaces";
import { FileService } from "../../services/file.service";
import { autoInjectable, delay, inject } from 'tsyringe';
import {CopyFileOptions, FileCreateInput, FileUpdateInput, IFile} from ".";
import { CertFileEntity } from "../certFiles";
import { DocFileEntity } from "../docFiles";
import { DownloadEntity } from "../downloads";
import {SessionEntity} from "../sessions";
import { DocumentEntity } from "../documents";

@autoInjectable()
export class FileEntity extends BaseEntity implements IFile {
  static tableName: TableNames = "file"
  fileMetaData: Prisma.JsonValue | null;
  fileName: string;
  fileType: number;
  hash: string;
  id: number;
  otherData: Prisma.JsonValue | null;
  path: string;
  sealPath: string | null;
  size: number;
  status: number;
  timestampedAt: Date;
  ttl: number;
  uploadedAt: Date | null;
  user: string;

  createdAt: Date | null

  private _certificateFiles?: CertFileEntity[];
  private _docFiles?: DocFileEntity[];
  private _downloads?: DownloadEntity[];
  private _sessions?: SessionEntity[];
  private _documents?: DocumentEntity[];

  constructor(file: IFile,
    @inject(delay(() => FileService)) private readonly service?: FileService) {
    super();
    this.fileMetaData = file.fileMetaData;
    this.fileName = file.fileName;
    this.fileType = file.fileType;
    this.hash = file.hash;
    this.id = Number(file.id);
    this.otherData = file.otherData;
    this.path = file.path;
    this.sealPath = file.sealPath;
    this.size = file.size;
    this.status = file.status;
    this.timestampedAt = file.timestampedAt;
    this.ttl = file.ttl;
    this.uploadedAt = file.uploadedAt;
    this.user = file.user;
    this.createdAt = file.createdAt;
    this._certificateFiles = file.certificateFiles ? file.certificateFiles?.map(i=> new CertFileEntity(i)) : undefined;
    this._docFiles = file.docFiles ? file.docFiles?.map(i=> new DocFileEntity(i)) : undefined;
    this._downloads = file.downloads ? file.downloads?.map(i=> new DownloadEntity(i)) : undefined;
    this._sessions = file.sessions ? file.sessions?.map(i=> new SessionEntity(i)) : undefined;
    this._documents = file.documents ? file.documents?.map(i=> new DocumentEntity(i)) : undefined;
  }

  get getSessions(){
    return this._sessions;
  }
  set setSessions(values: SessionEntity[] | undefined){
    this._sessions = values;
  }

  get getDocuments(){
    return this._documents;
  }
  set setDocuments(values: DocumentEntity[] | undefined){
    this._documents = values;
  }

  get getDocFilesownloads(){
    return this._downloads;
  }
  set setDocFilesownloads(values: DownloadEntity[] | undefined){
    this._downloads = values;
  }

  get getDocFiles(){
    return this._docFiles;
  }
  set setDocFiles(values: DocFileEntity[] | undefined){
    this._docFiles = values;
  }

  get getCertificateFiles(){
    return this._certificateFiles;
  }
  set setCertificateFiles(values: CertFileEntity[] | undefined){
    this._certificateFiles = values;
  }

  public async updateFile(data: FileUpdateInput, c: PrismaContext = NO_CONTEXT){
    return this.service?.modify(data, this.id, c);
  }

  public fillPathsIn(paths: string[]) {
    if ($isfile(this.path)) paths.push(this.path);
    if ($isfile(this.sealPath)) paths.push(<string>(this.sealPath));
  }

  public async fileInterfaceOfCopyToDirectory(auth: APIAuth, folder: string, timestamp: string, opts?: CopyFileOptions): Promise<FileCreateInput> {

    if (!$length(auth.user)) {
      throw new InternalError('fileInterfaceOfCopyToDirectory(): user should be set');
    }
    if (!$length(folder)) {
      throw new InternalError('fileInterfaceOfCopyToDirectory(): folder should be set');
    }
    if (!$ok(timestamp)) {
      throw new InternalError('fileInterfaceOfCopyToDirectory(): timestamp should be set');
    }
    if (!$isfile(this.path)) {
      throw new FileError('FileRef.path not found');
    }

    let extension = $ext(this.path);
    let newLocalFileName = $uuid();
    let fileName = `${newLocalFileName}.${extension}`;
    // first, we need to generate a new token
    const hash = $length(this.hash) ? this.hash : (await $hashfile(this.path));
    if (!$length(hash)) {
      throw new InternalError('fileInterfaceOfCopyToDirectory(): error on seal hash code');
    }
    let seal = await Certigna.endPoint().seal($string(env.SIGN_SERVER_LOGIN), $string(env.SIGN_SERVER_PASS), {
      name: fileName,
      user: auth.user,
      size: this.size,
      hash: <string>hash,
      date: timestamp
    });

    if (!$length(seal)) {
      throw new InternalError('fileInterfaceOfCopyToDirectory(): error on seal generation');
    }
    let filePath = $path(folder, 'files', fileName);
    let sealPath = $path(folder, 'seals', `${newLocalFileName}.xml`);

    if (!$writeString(sealPath, seal)) {
      throw new FileError('Impossible to save seal file to new directory');
    }
    if (!$copyFile(this.path, filePath)) {
      $removeFile(sealPath);
      throw new FileError('Impossible to save file to new directory');
    }

    return {
      // if we provide a new fileName, we use it.
      // if it's not the case : if the previous file name was the real file name, we use the new uuid(),
      //						  if not we use the previous logical file name
      fileName: $length(opts?.fileName) ? <string>(opts?.fileName) : (this.fileName === $filename(this.path) ? fileName : this.fileName),
      fileType: $ok(opts?.fileType) ? <FileStatus>opts?.fileType : this.fileType,
      hash: <string>hash,
      path: filePath,
      sealPath: sealPath,
      size: this.size,
      status: $ok(opts?.status) ? <number>(opts?.status) : this.status,
      timestampedAt: timestamp,
      user: auth.user
    };
  }

  public static async fileWithBuffer(auth: APIAuth, buf: Buffer, folder: string, timestamp: string, fileName: string,
                                     fileType: number): Promise<Prisma.FileCreateInput | null> {
    if (!$length(buf)) return null; // cannot create an empty file

    let filePath = $path(folder, 'files', fileName);
    let sealPath = $path(folder, 'seals', `${$withoutext(fileName)}.xml`);

    if (!$writeBuffer(filePath, buf)) return null;
    const hash = $hash(buf);
    if (!$ok(hash)) {
      throw new InternalError('fileWithBuffer(): cound not calculate buffer hash');
    }
    const ret = await this.fileWithExistingPath(auth, filePath, sealPath, timestamp, fileType, hash!, buf.length)

    if (!$ok(ret)) {
      $removeFile(filePath);
      return null;
    }
    return <Prisma.FileCreateInput>ret;
  }

  public async delete(c: PrismaContext) {

    if (!$ok(c.trx)) {
        throw new InternalError('$delete() should be called inside a transaction');
    }
    const data: any = {
        where: {
            id: this.id
        }
    };
    return this.service?.delete(data, c);
}

  public static async fileWithExistingPath(auth: APIAuth, filePath: string, sealPath: string, timestamp: string,
                                           fileType: number, hash: string, size: number): Promise<Prisma.FileCreateInput | null> {
    const isFile = $isfile(filePath);
    const isHash = $length(hash);
    const isSealFile = $length(sealPath);
    const isTimeStamp = $ok(timestamp);
    if (!isFile || !isHash || !size || !isSealFile || !isTimeStamp) return null;
    const fileName = $filename(filePath);
    let seal = await Certigna.endPoint().seal($string(env.SIGN_SERVER_LOGIN), $string(env.SIGN_SERVER_PASS), {
      name: $filename(filePath),
      user: auth.user,
      size: size,
      hash: hash,
      date: timestamp
    });
    if (!$length(seal)) return null;
    if (!$writeString(sealPath, seal)) return null;
    return {
      fileName: fileName,
      fileType: fileType,
      hash: hash,
      path: filePath,
      sealPath: sealPath,
      size: size,
      status: FileStatus.Valid,
      timestampedAt: timestamp,
      user: auth.user,
    };

  }
}