import {Prisma } from "@prisma/client";
import { injectable } from "tsyringe";
import {BaseService, TableNames} from "./base.service";
import {ScenarioCreateInput, ScenarioEntity, IScenario, ScenarioUpdateInput} from "../entities/scenario";
import {IScenarioTypeMap} from "../classes/interfaces/IScenarioTypeMap";
import {LocalID} from "../api/APIIDs";
import {NO_CONTEXT} from "../classes/interfaces/DBConstants";
import {PrismaContext} from "../classes/interfaces/DBInterfaces";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class ScenarioService extends BaseService<
    Prisma.ScenarioDelegate<undefined>,
    IScenarioTypeMap>{
    constructor() {
        super(prisma.scenario);
    }
    protected tableName: TableNames = 'scenario';

    async insert(session: ScenarioCreateInput, c: PrismaContext = NO_CONTEXT) {
        const data = await this.create<IScenario>({ data: session }, c);
        return new ScenarioEntity(data);
    }
    
    async modfiy(data: ScenarioUpdateInput, id: LocalID, c: PrismaContext = NO_CONTEXT){
        let scenario = await this.update<IScenario>({
            where: {
                id: id
            },
            data: data
        }, c)
        return new ScenarioEntity(scenario);
    }
    
}
