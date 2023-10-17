import {BaseEntity} from "../BaseEntity";
import {$compareDates, $length, $ok, $timestampDb2client} from "../../utils/commons";
import {InternalError} from "../../utils/errors";
import {$filename, $isfile, $readString, $removeFile} from "../../utils/fs";
import {$logterm} from "../../utils/utils";
import {UploadService} from "../../services/upload.service";
import {verifySeal} from "../../classes/CertignaEndPoint";
import {$hashfile} from "../../utils/crypto";
import {Ascending} from "../../utils/types";
import {LocalID} from "../../api/APIIDs";
import {TableNames} from "../../services/base.service";
import {delay, inject} from "tsyringe";
import { PrismaContext, RelativeIdentifier } from "../../classes/interfaces/DBInterfaces";
import {IUpload} from ".";


export class UploadEntity extends BaseEntity implements IUpload {
  static tableName: TableNames = "upload"
  expiresAt: Date | null;
  fileType: number;
  hash: string;
  id: LocalID;
  path: string;
  publicId: LocalID;
  sealPath: string | null;
  size: number;
  ttl: number;
  uploadedAt: Date | null;
  createdAt: Date | null;
  user: string;
  
  constructor(upload: IUpload, @inject(delay(() => UploadService)) private readonly service?: UploadService) {
    super();
    this.expiresAt = upload.expiresAt;
    this.fileType = upload.fileType;
    this.hash = upload.hash;
    this.id = Number(upload.id);
    this.path = upload.path;
    this.publicId = Number(upload.publicId);
    this.sealPath = upload.sealPath;
    this.size = upload.size;
    this.ttl = upload.ttl;
    this.uploadedAt = upload.uploadedAt;
    this.user = upload.user;
    this.createdAt = upload.createdAt
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
  public creationDate(): string {
    return $timestampDb2client(this.createdAt);
  }

  public async cleanAndDelete(c: PrismaContext) {
    if (!$ok(c.trx)) {
      throw new InternalError('<anUpload>.cleanAndDelete() should be called inside a transaction');
    }
    if ($isfile(this.path)) {
      if (!$removeFile(this.path)) {
        $logterm(`&O&b Warning &0 &w: cannot remove file '&c${this.path}&w'&0`)
      }
    }
    if ($isfile(this.sealPath)) {
      if (!$removeFile(this.sealPath)) {
        $logterm(`&O&b Warning &0 &w: cannot remove file '&c${this.sealPath}&w'&0`)
      }
    }
    await this.delete(c);
  }
  
  protected internalUrl(_?:RelativeIdentifier) : string | null {
    return  `/upload/${this.publicId}`;
  }

  public async verifyFileSeal(): Promise<boolean> {
    if (!$length(this.hash)) {
      return false;
    }


    let v = await verifySeal($readString(this.sealPath), {
      name: $filename(this.path),
      user: this.user,
      hash: this.hash,
      date:  this.uploadedAt!.toString(), // for
      // uploads the upload timestamp act as a file timestamped_at
      size: this.size
    });

    if (v) {
      // if our token file is verified, we will verify the hash of our upload file
      const calculatedHash = await $hashfile(this.path);
      v = calculatedHash === this.hash;
    }
    return v;
  }

  public isExpired(): boolean {
    return $compareDates(new Date(), $timestampDb2client(this.expiresAt)) !== Ascending;
  }

  public fillPathsIn(paths:string[]) {
    if ($isfile(this.path)) paths.push(this.path) ;
    if ($isfile(this.sealPath)) paths.push(<string>(this.sealPath)) ;
  }

  public expirationDate(): string {
    return $timestampDb2client(this.expiresAt);
  }

}