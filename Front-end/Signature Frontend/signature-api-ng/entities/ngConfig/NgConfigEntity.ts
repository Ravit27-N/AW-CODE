import {BaseEntity} from "../BaseEntity";
import {autoInjectable} from "tsyringe";
import {$length, $ok, $timestampDb2client} from "../../utils/commons";
import { $trim } from "../../utils/strings";
import {LocalID} from "../../api/APIIDs";
import {TableNames} from "../../services/base.service";
import { ConfigType, apiGlobals } from "../../classes/interfaces/DBConstants";
import { INgConf } from ".";


@autoInjectable()
export class NgConfigEntity extends BaseEntity implements INgConf {
    static tableName: TableNames = 'ngConf';
    id: LocalID;
    key: string;
    value: string | null;
    rank: number;
    type: number;
    user: string;

    createdAt: Date | null
    updatedAt: Date | null

    constructor(conf: INgConf) {
        super();
        this.id = Number(conf.id);
        this.key = conf.key;
        this.value = conf.value;
        this.rank = conf.rank;
        this.type = conf.type;
        this.user = conf.user;
        this.createdAt = conf.createdAt;
        this.updatedAt = conf.updatedAt;
    }

    public numberValue() : number {
        return $ok(this.value) ? parseInt(<string>this.value, 10) : 0 ;
    }

    public stringValue() : string {
        return $ok(this.value) ? <string>this.value : '' ;
    }

    public creationDate(): string {
        return $timestampDb2client(this.createdAt);
    }

    public modificationDate(): string {
        return $timestampDb2client(this.updatedAt);
    }

    public valueType() : ConfigType {
        return <ConfigType>this.type ;
    }

    public valueUser() : string {
        return this.user === apiGlobals.databaseSystemUser ? '' : this.user ;
    }

    public setValue(value:any, type:ConfigType, user:string) {
        this.type = type ;
        user = $trim(user) ;
        this.user = $length(user) ? user : apiGlobals.databaseSystemUser ;
        if ($ok(value)) this.value = value.toString() ;
    }

}