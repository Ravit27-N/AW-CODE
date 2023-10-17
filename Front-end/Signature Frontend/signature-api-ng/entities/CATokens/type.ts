import {CaToken, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";
import { IActor } from "../actors";
import { ICA } from "../CAs";
import {ICertificate} from "../certificates";

export interface ICaToken extends ModifyObject<CaToken, OnlyBigint<CaToken>, number> {
  actor?: IActor;
  ca?: ICA;
  certificates?: ICertificate
}

export type CaTokenUpdateInput = ModifyObject<Prisma.CaTokenUpdateInput, OnlyBigint<Prisma.CaTokenUpdateInput>, number>;
export type CaTokenCreateInput = ModifyObject<Prisma.CaTokenCreateInput, OnlyBigint<Prisma.CaTokenCreateInput>, number>;
export type CaTokenWhereInput = ModifyObject<Prisma.CaTokenWhereInput, OnlyBigint<Prisma.CaTokenWhereInput>, number>;