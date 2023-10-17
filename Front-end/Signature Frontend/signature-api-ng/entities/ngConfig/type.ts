import {NgConf, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";

export interface INgConf extends ModifyObject<NgConf, OnlyBigint<NgConf>, number>{

}

export type NgConfUpdateInput = ModifyObject<Prisma.NgConfUpdateInput, OnlyBigint<Prisma.NgConfUpdateInput>, number>;
export type NgConfCreateInput = ModifyObject<Prisma.NgConfCreateInput, OnlyBigint<Prisma.NgConfCreateInput>, number>;
export type NgConfWhereInput = ModifyObject<Prisma.NgConfWhereInput, OnlyBigint<Prisma.NgConfWhereInput>, number>;

