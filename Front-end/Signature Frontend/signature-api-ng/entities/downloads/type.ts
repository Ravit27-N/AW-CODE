import {Download, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";
import {IFile} from "../files";
import {ISession} from "../sessions";

export interface IDownload extends ModifyObject<Download, OnlyBigint<Download>, number>{
    files?: IFile,
    session?: ISession
}

export type DownloadUpdateInput = ModifyObject<Prisma.DownloadUpdateInput, OnlyBigint<Prisma.DownloadUpdateInput>, number>;
export type DownloadCreateInput = ModifyObject<Prisma.DownloadCreateInput, OnlyBigint<Prisma.DownloadCreateInput>, number>;
export type DownloadWhereInput = ModifyObject<Prisma.DownloadWhereInput, OnlyBigint<Prisma.DownloadWhereInput>, number>;