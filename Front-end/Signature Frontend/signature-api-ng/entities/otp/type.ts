import {OTP, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";
import {IActor} from "../actors";

export interface IOTP extends ModifyObject<OTP, OnlyBigint<OTP>, number>{
    actor?: IActor
}

export type OTPUpdateInput = ModifyObject<Prisma.OTPUpdateInput, OnlyBigint<Prisma.OTPUpdateInput>, number>;
export type OTPCreateInput = ModifyObject<Prisma.OTPCreateInput, OnlyBigint<Prisma.OTPCreateInput>, number>;
export type OTPWhereInput = ModifyObject<Prisma.OTPWhereInput, OnlyBigint<Prisma.OTPWhereInput>, number>;

