import {BaseEntity} from "../BaseEntity";
import {TableNames} from "../../services/base.service";
import {LocalID} from "../../api/APIIDs";
import { ICertificateFile } from ".";
import { CertificateEntity } from "../certificates";
import { FileEntity } from "../files";


export class CertFileEntity extends BaseEntity implements ICertificateFile{
  static tableName: TableNames = "certificateFile"
  id: LocalID;
  rank: number;
  status: number;
  usage: number;
  type: number;
  usageTitle: string | null;
  certificateId: LocalID;
  fileId: LocalID;
  
  private _certificate: CertificateEntity | undefined;
  private _file: FileEntity | undefined;

  constructor(certFile: ICertificateFile) {
    super();
    this.id = Number(certFile.id);
    this.rank = certFile.rank;
    this.status = certFile.status;
    this.usage = certFile.usage;
    this.type = certFile.type;
    this.usageTitle = certFile.usageTitle;
    this.certificateId = Number(certFile.certificateId);
    this.fileId = Number(certFile.fileId);
    this._file = certFile.file ? new FileEntity(certFile.file) : undefined;
    this._certificate = certFile.certificate ? new CertificateEntity(certFile.certificate) : undefined;
  }
  
  get getFile(): FileEntity | undefined{
    return this._file
  }

  set setFile(value: FileEntity | undefined){
    this._file = value
  }

  get getCertificate(): CertificateEntity | undefined{
    return this._certificate
  }

  set setCertificate(value: CertificateEntity | undefined){
    this._certificate = value
  }

}