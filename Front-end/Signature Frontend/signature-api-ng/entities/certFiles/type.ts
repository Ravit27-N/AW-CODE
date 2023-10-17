import {CertificateFile, Prisma} from "@prisma/client";
import {ModifyObject, OnlyBigint} from "../../utils/types";
import { ICertificate } from "../certificates";
import {IFile} from "../files";

export interface ICertificateFile extends ModifyObject<CertificateFile, OnlyBigint<CertificateFile>, number> {
  certificate?: ICertificate
  file?: IFile
}

export type CertificateFileUpdateInput = ModifyObject<Prisma.CertificateFileUpdateInput, OnlyBigint<Prisma.CertificateFileUpdateInput>, number>;
export type CertificateFileCreateInput = ModifyObject<Prisma.CertificateFileCreateInput, OnlyBigint<Prisma.CertificateFileCreateInput>, number>;
export type CertificateFileWhereInput = ModifyObject<Prisma.CertificateFileWhereInput, OnlyBigint<Prisma.CertificateFileWhereInput>, number>;