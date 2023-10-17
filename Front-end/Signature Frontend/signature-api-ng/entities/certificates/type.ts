import {Certificate, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";
import { ICaToken } from "../CATokens";
import {ICertificateFile} from "../certFiles";

export interface ICertificate extends ModifyObject<Certificate, OnlyBigint<Certificate>, number> {
  caToken?: ICaToken;
  certFiles?: ICertificateFile[]
}

export type CertificateUpdateInput = ModifyObject<Prisma.CertificateUpdateInput, OnlyBigint<Prisma.CertificateUpdateInput>, number>;
export type CertificateCreateInput = ModifyObject<Prisma.CertificateCreateInput, OnlyBigint<Prisma.CertificateCreateInput>, number>;
export type CertificateWhereInput = ModifyObject<Prisma.CertificateWhereInput, OnlyBigint<Prisma.CertificateWhereInput>, number>;