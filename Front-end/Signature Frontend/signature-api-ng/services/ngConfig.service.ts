import {Prisma} from "@prisma/client";
import { injectable } from "tsyringe";
import {BaseService, TableNames} from "./base.service";
import { INgConfigTypeMap } from "../classes/interfaces/INgConfigTypeMap";
import {$ok} from "../utils/commons";
import {NgConfigEntity, INgConf, NgConfCreateInput, NgConfUpdateInput} from "../entities/ngConfig";
import {LocalID} from "../api/APIIDs";
import {NO_CONTEXT} from "../classes/interfaces/DBConstants";
import {PrismaContext} from "../classes/interfaces/DBInterfaces";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class NgConfigService extends BaseService<
    Prisma.NgConfDelegate<undefined>,
    INgConfigTypeMap>{
    constructor() {
        super(prisma.ngConf);
    }
    protected tableName:TableNames = 'ngConf';

    async insert(ngConfig: NgConfCreateInput, c: PrismaContext = NO_CONTEXT) {
        const data = await this.create<INgConf>({ data: ngConfig }, c);
        return new NgConfigEntity(data)
    }
    
    async modify(data: NgConfUpdateInput, id: LocalID, c: PrismaContext = NO_CONTEXT){
        let ngConfig = await this.update<INgConf>({
            where: {
                id: id
            },
            data: data
        }, c)
        return new NgConfigEntity(ngConfig);
    }

    public async getModelVersion(c?:PrismaContext) : Promise<string> {
        let version = await this.findFirst<INgConf>({
            where:{
                key: 'MODEL_VERSION'
            }
        }, c) ;
        const  ngConf = new NgConfigEntity(<INgConf>version);
        return $ok(version) ? ngConf.stringValue() : '' ;
    }
    
}
