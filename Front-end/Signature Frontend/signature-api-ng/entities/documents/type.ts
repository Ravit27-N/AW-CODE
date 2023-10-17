import {Document, Prisma} from "@prisma/client";
import {ModifyObject, OnlyBigint} from "../../utils/types";
import {IFile} from "../files";
import {IDocFile} from "../docFiles";
import {ISession} from "../sessions";

export interface IDocument extends ModifyObject<Document, OnlyBigint<Document>, number> {
  session?: ISession
  docFiles?: IDocFile[];
  genuineFile?: IFile;
}

export type DocumentUpdateInput = ModifyObject<Prisma.DocumentUpdateInput, OnlyBigint<Prisma.DocumentUpdateInput>, number>;
export type DocumentCreateInput = ModifyObject<Prisma.DocumentCreateInput, OnlyBigint<Prisma.DocumentCreateInput>, number>;
export type DocumentWhereInput = ModifyObject<Prisma.DocumentWhereInput, OnlyBigint<Prisma.DocumentWhereInput>, number>;