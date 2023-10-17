import {Upload, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";

export interface IUpload extends ModifyObject<Upload, OnlyBigint<Upload>, number>{
}

export type UploadUpdateInput = ModifyObject<Prisma.UploadUpdateInput, OnlyBigint<Prisma.UploadUpdateInput>, number>;
export type UploadCreateInput = ModifyObject<Prisma.UploadCreateInput, OnlyBigint<Prisma.UploadCreateInput>, number>;
export type UploadWhereInput = ModifyObject<Prisma.UploadWhereInput, OnlyBigint<Prisma.UploadWhereInput>, number>;

