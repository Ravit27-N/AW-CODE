import {autoInjectable} from "tsyringe";
import {BaseEntity} from "../BaseEntity";
import {$isfile} from "../../utils/fs";
import {LocalID} from "../../api/APIIDs";
import {TableNames} from "../../services/base.service";
import { IDocFile } from ".";
import { DocumentEntity } from "../documents";
import { FileEntity } from "../files";

@autoInjectable()
export class DocFileEntity extends BaseEntity implements IDocFile{
  static tableName: TableNames = 'docFile';
  documentId: LocalID;
  fileId: LocalID;
  id: LocalID;
  rank: number;
  status: number;
  type: number;
  usage: number;
  usageTitle: string | null;

  private _file: FileEntity | undefined;
  private _document: DocumentEntity | undefined;

  constructor(file: IDocFile) {
    super();
    this.documentId = Number(file.documentId);
    this.fileId = Number(file.fileId);
    this.id = Number(file.id);
    this.rank = file.rank;
    this.status = file.status;
    this.type = file.type;
    this.usage = file.usage;
    this.usageTitle = file.usageTitle;
    this._file = file.file ? new FileEntity(file.file) : undefined;
    this._document = file.document ? new DocumentEntity(file.document) : undefined;
  }

  get getFile(): FileEntity | undefined{
    return this._file;
  }
  set setFile(value: FileEntity | undefined){
    this._file = value
  }

  get getDocument(): DocumentEntity | undefined{
    return this._document;
  }
  set setDocument(value: DocumentEntity | undefined){
    this._document = value
  }

  public fillPathsIn(paths:string[]) {
    if ($isfile(this.getFile?.path)) paths.push(<string>this.getFile?.path) ;
    if ($isfile(this.getFile?.sealPath)) paths.push(<string>(this.getFile?.sealPath)) ;
  }
  
}