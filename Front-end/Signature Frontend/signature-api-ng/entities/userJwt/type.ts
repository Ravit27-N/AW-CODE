import {UserJwt, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";

export interface IUserJwt extends ModifyObject<UserJwt, OnlyBigint<UserJwt>, number>{
}


export type UserJwtUpdateInput = ModifyObject<Prisma.UserJwtUpdateInput, OnlyBigint<Prisma.UserJwtUpdateInput>, number>;
export type UserJwtCreateInput = ModifyObject<Prisma.UserJwtCreateInput, OnlyBigint<Prisma.UserJwtCreateInput>, number>;
export type UserJwtWhereInput = ModifyObject<Prisma.UserJwtWhereInput, OnlyBigint<Prisma.UserJwtWhereInput>, number>;

