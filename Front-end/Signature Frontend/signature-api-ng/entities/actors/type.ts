import { Actor, Prisma} from "@prisma/client";
import {int, ModifyObject, OnlyBigint} from "../../utils/types";
import {ISession} from "../sessions";
import {ICaToken} from "../CATokens";
import {IOTP} from "../otp";

export interface IActor extends ModifyObject<Actor, OnlyBigint<Actor>, number>{
    session?: ISession
    caTokens?: ICaToken[]
    otps?: IOTP[]
}

export type ActorUpdateInput = ModifyObject<Prisma.ActorUpdateInput, OnlyBigint<Prisma.ActorUpdateInput>, number>;
export type ActorCreateInput = ModifyObject<Prisma.ActorCreateInput, OnlyBigint<Prisma.ActorCreateInput>, number>;
export type ActorWhereInput = ModifyObject<Prisma.ActorWhereInput, OnlyBigint<Prisma.ActorWhereInput>, number>;

export interface IActorColumnName extends Omit<IActor, "publicId" | "firstName" | "administrativeCode" | "authType" |
    "rolesArray" | "userData" | "manifestData" | "sessionId"|"createdAt"|"updatedAt"> {
    public_id: number,
    first_name: string,
    administrative_code: string,
    auth_type: int,
    roles_array: Prisma.JsonValue,
    user_data: Prisma.JsonValue,
    manifest_data: Prisma.JsonValue,
    session_id: number,
    created_at: Date,
    updated_at: Date
}
