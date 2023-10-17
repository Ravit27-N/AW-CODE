import { injectable } from "tsyringe";
import {BaseTypeMap} from "../classes/interfaces/BaseTypeMap";
import {IDelegate} from "../classes/interfaces/IDelegate";
import {Nullable} from "../utils/types";
import {$count, $isobject, $ok} from "../utils/commons";
import {SessionEntity} from "../entities/sessions";
import {GlobalID, LocalID} from "../api/APIIDs";
import { Prisma } from "@prisma/client";
import { NO_CONTEXT } from "../classes/interfaces/DBConstants";
import { PrismaContext } from "../classes/interfaces/DBInterfaces";

export type TableNames = Uncapitalize<keyof typeof Prisma.ModelName>;
@injectable()
export class BaseService<D extends IDelegate, T extends BaseTypeMap> {
    protected declare tableName: TableNames;
    constructor(protected delegate: D) {}

    public get getDelegate(): D {
        return this.delegate;
    }

    public async aggregate<M>(data: T["aggregate"], c: PrismaContext = NO_CONTEXT) {
        return <M>await this.model(c.trx).aggregate(data);
    }

    public async count(data: T["count"], c: PrismaContext = NO_CONTEXT): Promise<number> {
        const result = await this.model(c.trx).count(data);
        return <number>result;
    }

    public async create<M>(data: T["create"], c: PrismaContext = NO_CONTEXT): Promise<M> {
        return <M>await this.model(c.trx).create(data);
    }
    
    public async delete(data: T["delete"], c: PrismaContext = NO_CONTEXT){
        return this.model(c.trx).delete(data);
    }

    public async deleteMany(data: T["deleteMany"], c: PrismaContext = NO_CONTEXT){
        return this.model(c.trx).deleteMany(data);
    }

    public async update<M>(data: T["update"], c: PrismaContext = NO_CONTEXT): Promise<M> {
        return <M>await this.model(c.trx).update(data);
    }

    public async findMany<TT>(data: T["findMany"], c: PrismaContext = NO_CONTEXT): Promise<TT[]> {
        const result = await this.model(c.trx).findMany(data);
        return result ? (result as TT[]) : [];
    }
    public async findFirst<TT>(data: T["findFirst"], c: PrismaContext = NO_CONTEXT): Promise<TT | null> {
        const result = await this.model(c.trx).findFirst(data);
        return result ? (result as TT) : null;
    }
    
    protected model(trx: PrismaContext['trx']): D{
        // @ts-ignore
        return $ok(trx)? trx![this.tableName] : this.delegate;
    }

    public async objectWithPublicID<M>(identifier:Nullable<number | GlobalID>, c:PrismaContext) : Promise <M | null> {
        if ($ok(identifier) && <number>identifier > 0) {
            let r = null ;
            if (($ok(c.include) && $isobject(c.include))) {
                let fetched = await this.model(c.trx).findMany({
                    where: {
                        publicId: <number>identifier
                    },
                    include: c.include
                })
                if ($count(fetched) == 1) r = fetched[0] ;
            }
            else {
                r = await this.model(c.trx).findFirst({
                    where: {
                        publicId: <number>identifier
                    }
                })
            }
            if ($ok(r)) {
                return <M>r ;
            }
        }
        return null ;
    }

    async  unrelate<M>(field: string, id: LocalID, c: PrismaContext): Promise<M>{
        let relate = await this.model(c.trx).update({
            where: {
                id: id
            },
            data:{
                [field]: null
            }
        })
        // new related entity ??
        return <M>relate;
    }
    
    async getRelated<M>(property: object, id: LocalID, c: PrismaContext, condition: object = {}, key?: string): Promise<M | undefined>{
        condition = {
            ...condition,
            id: id
        }
        let relate = await this.model(c.trx).findFirst({
            where: condition,
            include: property
        })
        // new related entity ??
        if(!$ok(relate)) return undefined;
        const data = relate[key ? key : Object.keys(property)[0]]
        return data ? <M>data : undefined;
    }

    public async sessionObjectWithPublicID<M>(
        session:Nullable<SessionEntity>,
        identifier:Nullable<number | bigint>,
        c:PrismaContext) : Promise <M | null> {
        if ($ok(identifier) && $ok(session) && (<number>identifier) > 0) {
            const condition = {
                sessionId: (<SessionEntity>session).id,
                publicId: <number>identifier
            }
            let fetched = null ;
            if (($ok(c.include) && $isobject(c.include))) {
                
                fetched = await this.model(c.trx).findMany({
                    where: condition,
                    include: c.include
                })
            }else {
                fetched = await this.model(c.trx).findMany({
                    where: condition
                })
            }
            
            if ($count(fetched) == 1) {
                let fetchedObject = (<M[]><unknown>fetched)[0] ; // BAD casting but typescript is a hell of type checking
                if ($ok(fetchedObject)) {
                    // WARNING: never use with object without a session
                    // the bad casting continues, but we need it to assign the session to the fetched object if
                    // needed here
                    if (!$ok((<any><unknown>fetchedObject)?.session)) {
                        (<any><unknown>fetchedObject).session = session ;
                    }
                    return fetchedObject ;
                }
            }
        }
        return null ;
    }
    
    protected rawEncrypt(value?: string|null) {
        return `hex(aes_encrypt(${value}, unhex('F3229A0B371ED2D9441B830D21A390C3')))`;
    }
    
}
