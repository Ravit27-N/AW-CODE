import {BaseEntity} from "../BaseEntity";
import {Prisma} from "@prisma/client";
import { SessionStatus} from "../../api/APIConstants";
import {autoInjectable, delay, inject} from "tsyringe";
import {CAService} from "../../services/ca.service";
import {$length, $ok, $timestampDb2client} from "../../utils/commons";
import {AuthorityResource} from "../../api/APIInterfaces";
import { $filesize } from "../../utils/fs";
import {APIServer} from "../../server";
import {CAData, ImportedCA } from "../../classes/interfaces/ICATypeMap";
import {LocalID} from "../../api/APIIDs";
import {TableNames} from "../../services/base.service";
import { CAStatus } from "../../classes/interfaces/DBConstants";
import { RelativeIdentifier, PrismaContext } from "../../classes/interfaces/DBInterfaces";
import { InternalError } from "../../utils/errors";
import {CAUpdateInput, ICA} from ".";
import { CaTokenEntity } from "../CATokens";
import {prisma} from "../../classes/interfaces/prisma";

@autoInjectable()
export class CAEntity extends BaseEntity implements ICA {
    static tableName: TableNames = 'cA';

    public id: LocalID;
    public publicId: LocalID;
    public importId: string;
    public status: number;
    public name: string;

    createdAt: Date | null
    updatedAt: Date | null
    public caData: Prisma.JsonValue;

    private _caTokens: CaTokenEntity[] | undefined

    constructor(ca: ICA, @inject(delay(() => CAService)) private readonly service?: CAService) {
        super();
        this.id = Number(ca.id);
        this.publicId = Number(ca.publicId);
        this.importId = ca.importId
        this.status = ca.status;
        this.name = ca.name;
        this.caData = ca.caData;
        this.createdAt = ca.createdAt;
        this.updatedAt = ca.updatedAt;
        this._caTokens = ca.catokens ? ca.catokens?.map(i=> new CaTokenEntity(i)) : undefined
    }
    
    get caTokens(): CaTokenEntity[] | undefined {
        return this._caTokens;
    }

    set caTokens(values: CaTokenEntity[] | undefined){
        this._caTokens = values;
    }

    public get getCAData(){
        return this.parsePrisma<CAData>(this.caData);
    }

    protected internalUrl(_?:RelativeIdentifier) : string | null {
        return  `/ca/${this.publicId}`;
    }

    public isValid() : boolean { return this.status == CAStatus.Valid ; }
    
    public CGUVersion() : string {
        return $length(this.getCAData?.cguVersion) ? <string>this.getCAData?.cguVersion : '<unknown>' ;
    }

    public get getStatus(){
        return <SessionStatus>this.status;
    }

    public creationDate(): string {
        return $timestampDb2client(this.createdAt);
    }

    public modificationDate(): string {
        return $timestampDb2client(this.updatedAt);
    }

    // TODO: can we move it to base service?
    public async update(data: CAUpdateInput, c: PrismaContext){
        return this.service?.modify(data, this.id, c);
    }

    //
    public getRelated<M>(property: Prisma.CAInclude, c: PrismaContext){
        return this.service?.getRelated<M>(property, this.id, c);
    }
    public async delete(c: PrismaContext) {

        if (!$ok(c.trx)) {
            throw new InternalError('$delete() should be called inside a transaction');
        }
        const data: any = {
            where: {
                id: this.id
            }
        };
        return this.service?.delete(data, c);
    }

    public async toAPI(_:PrismaContext) : Promise<AuthorityResource> {
        return {
            caid: this.publicId,
            'cgu-version':this.CGUVersion(),
            'long-name':$length(this.getCAData?.longName) ? <string>this.getCAData?.longName : this.name ,
            name:this.name
        } ;
    }

    public static async synchronizeCertificationAuthorities(imports:ImportedCA[], timeOut: number) {
        // we load all known autorities in the database
        // all known but different are patched
        // all unknown are added
        // all missing in the definition are marked as invalid
        // we do all that in the same transaction
        // we never delete a certification authority
        try {
            await prisma.$transaction(async (trx) => {
                const context = {trx:trx} ;
                const caService = new CAService();
                let authorities = await caService.findMany<ICA>({}, context);
                let dbset:CADictionary = {} ;
                let uuidSet = new Set() ;
                authorities?.forEach(authority => {
                    const author = new CAEntity(authority);
                    dbset[author.importId] = author ;
                })

                for (const i of imports) {
                    if (!uuidSet.has(i.uuid)) {
                        let authority = dbset[i.uuid] ;
                        if ($ok(authority)) {
                            // we have to update the previous CA
                            await authority.update({
                                name:i.name,
                                status:CAStatus.Valid,
                                caData:{
                                    aki:i.aki,
                                    longName:i.longName,
                                    cguVersion:i.cguVersion,
                                    cguLocalPath:i.cguLocalPath,
                                    cguSize:$filesize(i.cguLocalPath)
                                }
                            }, context);
                            
                            delete dbset[i.uuid] ; // removed from initial set
                        }
                        else {
                            // we have to create a new certification authority
                            let n = await this.nextGlobalPublicID({trx:trx}) ; // this method updates NGConfig table
                            await caService.insert({
                                publicId:n,
                                importId:i.uuid,
                                name:i.name,
                                status:CAStatus.Valid,
                                caData:{
                                    aki:i.aki,
                                    longName:i.longName,
                                    cguVersion:i.cguVersion,
                                    cguLocalPath:i.cguLocalPath,
                                    cguSize:$filesize(i.cguLocalPath)
                                }
                            }, context);
                        }
                        uuidSet.add(i.uuid) ; // never add an authority twice !	
                    }
                } 
                // here, CA remaining in dbset should be invalidated
                // because they are not in the new imported CAs
                for (let uuid in dbset) {
                    await dbset[uuid].update({
                        status: CAStatus.Invalid
                    }, context);
                }
            }, {
                timeout: timeOut
            }) ;
            // here we are commited
        }
        catch (e) {
            // here we may have a rollback
            // we will halt the server with an error
            APIServer.api().error(e);
            throw e ;
        }
    }
}

type CADictionary = { [key: string]: CAEntity } ;