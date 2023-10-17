import {DocFile, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";
import { IFile } from "../files";
import {IDocument} from "../documents";

export interface IDocFile extends ModifyObject<DocFile, OnlyBigint<DocFile>, number>{
  file?: IFile
  document?: IDocument
}

export type DocFileUpdateInput = ModifyObject<Prisma.DocFileUpdateInput, OnlyBigint<Prisma.DocFileUpdateInput>, number>;
export type DocFileCreateInput = ModifyObject<Prisma.DocFileCreateInput, OnlyBigint<Prisma.DocFileCreateInput>, number>;
export type DocFileWhereInput = ModifyObject<Prisma.DocFileWhereInput, OnlyBigint<Prisma.DocFileWhereInput>, number>;