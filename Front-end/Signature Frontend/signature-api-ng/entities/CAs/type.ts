import {CA, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";
import {ICaToken} from "../CATokens";

export interface ICA extends ModifyObject<CA, OnlyBigint<CA>, number> {
  catokens?: ICaToken[]
}

export type CAUpdateInput = ModifyObject<Prisma.CAUpdateInput, OnlyBigint<Prisma.CAUpdateInput>, number>;
export type CACreateInput = ModifyObject<Prisma.CACreateInput, OnlyBigint<Prisma.CACreateInput>, number>;
export type CAWhereInput = ModifyObject<Prisma.CAWhereInput, OnlyBigint<Prisma.CAWhereInput>, number>;