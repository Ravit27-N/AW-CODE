import {Session, Prisma} from "@prisma/client";
import { ModifyObject, OnlyBigint } from "../../utils/types";
import {IScenario} from "../scenario";
import {IDownload} from "../downloads";
import {IDocument} from "../documents";
import {IActor} from "../actors";
import {IFile} from "../files";

export interface ISession extends ModifyObject<Session, OnlyBigint<Session>, number>{
    scenarios?: IScenario[];
    actors?: IActor[];
    documents?: IDocument[];
    downloads?: IDownload[];
    activeScenario?: IScenario;
    file?: IFile;
}

type MaxScenarioRankBigInt = Awaited<Prisma.PrismaPromise<Prisma.GetScenarioAggregateType<{_max: {rank: true}}>>>;
export type MaxScenarioRank = {
    _max: ModifyObject<MaxScenarioRankBigInt["_max"], OnlyBigint<MaxScenarioRankBigInt["_max"]>, number>
}

export type SessionUpdateInput = ModifyObject<Prisma.SessionUpdateInput, OnlyBigint<Prisma.SessionUpdateInput>, number>;
export type SessionCreateInput = ModifyObject<Prisma.SessionCreateInput, OnlyBigint<Prisma.SessionCreateInput>, number>;
export type SessionWhereInput = ModifyObject<Prisma.SessionWhereInput, OnlyBigint<Prisma.SessionWhereInput>, number>;

