import { Prisma } from "@prisma/client";
import { BaseTypeMap } from "./BaseTypeMap";

export class IScenarioTypeMap implements BaseTypeMap {
    aggregate?: Prisma.ScenarioAggregateArgs;
    count?: Prisma.ScenarioCountArgs;
    create?: Prisma.ScenarioCreateArgs;
    delete?: Prisma.ScenarioDeleteArgs;
    deleteMany?: Prisma.ScenarioDeleteManyArgs;
    findFirst?: Prisma.ScenarioFindFirstArgs;
    findMany?: Prisma.ScenarioFindManyArgs;
    findUnique?: Prisma.ScenarioFindUniqueArgs;
    update?: Prisma.ScenarioUpdateArgs;
    updateMany?: Prisma.ScenarioUpdateManyArgs;
    upsert?: Prisma.ScenarioUpsertArgs;
}
