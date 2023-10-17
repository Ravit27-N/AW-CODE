import {FileStatus} from "../../classes/interfaces/DBConstants";
import {File, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";
import {ICertificateFile} from "../certFiles";
import {IDocFile} from "../docFiles";
import {IDownload} from "../downloads";
import {ISession} from "../sessions";
import {IDocument} from "../documents";

export interface IFile extends ModifyObject<File, OnlyBigint<File>, number>{
    certificateFiles?: ICertificateFile[];
    docFiles?: IDocFile[];
    downloads?: IDownload[];
    sessions?: ISession[];
    documents?: IDocument[];
}

export type CopyFileOptions = {
    fileName?: string;
    fileType?: number;
    status?: FileStatus;
};

export type FileUpdateInput = ModifyObject<Prisma.FileUpdateInput, OnlyBigint<Prisma.FileUpdateInput>, number>;
export type FileCreateInput = ModifyObject<Prisma.FileCreateInput, OnlyBigint<Prisma.FileCreateInput>, number>;
export type FileWhereInput = ModifyObject<Prisma.FileWhereInput, OnlyBigint<Prisma.FileWhereInput>, number>;

