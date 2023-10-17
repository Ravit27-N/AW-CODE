import {Scenario, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";
import {ISession} from "../sessions";

export interface IScenario extends ModifyObject<Scenario, OnlyBigint<Scenario>, number>{
    session?: ISession;
}

export type ScenarioUpdateInput = ModifyObject<Prisma.ScenarioUpdateInput, OnlyBigint<Prisma.ScenarioUpdateInput>, number>;
export type ScenarioCreateInput = ModifyObject<Prisma.ScenarioCreateInput, OnlyBigint<Prisma.ScenarioCreateInput>, number>;
export type ScenarioWhereInput = ModifyObject<Prisma.ScenarioWhereInput, OnlyBigint<Prisma.ScenarioWhereInput>, number>;

