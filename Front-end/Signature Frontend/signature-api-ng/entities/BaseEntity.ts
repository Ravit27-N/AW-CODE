
import {
    $count,
    $finalDateString,
    $isarray,
    $isnumber,
    $isstring, $isunsigned, $keys,
    $length,
    $now,
    $ok,
    $string,
    $toint
} from "../utils/commons";
import {BadRequestError, ConflictError, ForbiddenError, InternalError} from "../utils/errors";
import {APIServer} from "../server";
import {APIAuth, APIGetListQuery} from "../api/APIInterfaces";
import {UserRole} from "../api/APIConstants";
import {ModifyObject, Nullable, OnlyBigint} from "../utils/types";
import {$trim} from "../utils/strings";
import {Prisma} from "@prisma/client";
import {$gid, $lid, GlobalID, LocalID} from "../api/APIIDs";
import {TableNames} from "../services/base.service";
import { ConfigType, apiGlobals, PQueryOperator } from "../classes/interfaces/DBConstants";
import {RelativeIdentifier, PrismaContext, LastFilesDictionary, LastFile} from "../classes/interfaces/DBInterfaces";
import { autoInjectable} from 'tsyringe';
import crypto from 'crypto';
import env from "../env-config";

@autoInjectable()
export class BaseEntity {
    declare static tableName: TableNames;
    protected parsePrisma<T>(json: any) {
        const dataStr = typeof json !== 'string' ? JSON.stringify(json) : json;
        return JSON.parse(dataStr) as T;
    }

    // public creationDate(): string {
    //     return $timestampDb2client(this.createdAt);
    // }
    //
    // public modificationDate(): string {
    //     return $timestampDb2client(this.updatedAt);
    // }

    protected internalUrl(_?: RelativeIdentifier): string | null {
        return null;
    }

    public longUrl(relativeIdentifier?: RelativeIdentifier): string {
        let internalUrl = this.internalUrl(relativeIdentifier);
        const api = APIServer.api();
        return internalUrl ? `${api.prefix}${api.version}${internalUrl}` : "";
    }

    public shortUrl(relativeIdentifier?: RelativeIdentifier): string {
        let internalUrl = this.internalUrl(relativeIdentifier);
        return internalUrl ? internalUrl : "";
    }

    public url(relativeIdentifier?: RelativeIdentifier): string {
        return APIServer.api().conf.longIdentifiers ? this.longUrl(relativeIdentifier) : this.shortUrl(relativeIdentifier);
    }

    public static async nextGlobalPublicID(c: PrismaContext): Promise<number> {
        if (!$ok(c.trx)) {
            throw new InternalError('nextGlobalPublicIDWithTableName(): should be used within a transaction');
        }
        let returnedValue: number = 0;
        let identifier = `$_${this.tableName}_NID`;
        if ($length(identifier) > 0) {
            const ngConfig = c.trx!.ngConf;
            let getConfig = await ngConfig.findFirst({
                where: {
                    key: identifier
                },
                select: {
                    value: true
                },
            });
            if ($ok(getConfig)) {
                returnedValue = $toint(getConfig!.value) + 1;
                await ngConfig.updateMany({
                    where: {
                        key: identifier
                    },
                    data: {
                        value: $string(returnedValue),
                    }
                });
            } else {
                returnedValue = 1;
                // we need to insert a NGConfig 
                await ngConfig.create({
                    data: {
                        key: identifier,
                        rank: 0,
                        type: ConfigType.Unsigned,
                        user: apiGlobals.databaseSystemUser,
                        value: `${returnedValue}`
                    }
                });
            }
        }
        return returnedValue;
    }

    public static addNumberToQuery<P>(property: P, value: Nullable<string | number>,
                                      operator: PQueryOperator = PQueryOperator.equal, min?: number, max?: number): P {
        if (!$ok(value)) {
            return <P>{};
        }
        if ($isstring(value)) {
            if (!$length($trim(<string>value))) {
                return <P>{};
            }
            value = parseInt(<string>value, 10);
            if (isNaN(value)) return <P>{};
        }
        if (($isnumber(min) && (value as number) < (min as number)) ||
            ($isnumber(max) && (value as number) > (max as number))) {
            return <P>{};
        }

        const op: string = $ok(operator) ? <string>operator : PQueryOperator.equal;
        property = {
            ...property,
            [op]: value
        }
        return property;
    }

    public static expirationAwareListQuery<Q extends APIGetListQuery, P>(auth: APIAuth, q: Q, query: IExpireConditionProperty<P>): P {
        if ($ok(q)) {
            if ($ok(q.ttlmin)) {
                let ttl = this.addNumberToQuery<IExpireConditionProperty<P>['ttl']>(query.ttl, q.ttlmin, PQueryOperator.graterThanEqual, 0);
                if (!$ok(ttl)) {
                    throw new BadRequestError('bad ttlmin parameter in query');
                }
                query.ttl = ttl;
            }
            if ($ok(q.ttlmax)) {
                let ttl = this.addNumberToQuery<IExpireConditionProperty<P>['ttl']>(query.ttl, q.ttlmax, PQueryOperator.lessThanEqual, 0);
                if (!$ok(ttl)) {
                    throw new BadRequestError('bad ttlmax parameter in query');
                }
                query.ttl = ttl;
            }

            if ($ok(q.userids)) {
                if (auth.role === UserRole.Action) {
                    throw new ForbiddenError('Action user cannot specify user identifiers in their request')
                }
                if ($isstring(q.userids) && $length(<string>(q.userids))) {
                    query = {
                        ...query,
                        user: <string>q.userids
                    }
                } else if ($isarray(q.userids)) {
                    // paranoid, I know but typing does not mean we receive strings...
                    query = {
                        ...query,
                        user: {
                            in: (<[any]>(q?.userids)).map((e: any) => `${e}`)
                        }
                    }
                }
            }

            const now = $now();
            let ttlsearch = false;
            let expiredAt: IExpireConditionProperty<P>['expiresAt'] = {};

            if ($ok(q.dynttlmin)) {
                expiredAt = {
                    ...expiredAt,
                    gte: $finalDateString(now, <number>q.dynttlmin)
                }
                ttlsearch = true;
            }
            if ($ok(q.dynttlmax)) {
                expiredAt = {
                    ...expiredAt,
                    lte: $finalDateString(now, <number>q.dynttlmax)
                }
                ttlsearch = true;
            }
            if (!ttlsearch) {
                // by default we get only the non-expired objects but
                // this can be changed with the expirationstatus param
                if (!$length(q.expirationstatus) || q.expirationstatus === 'valid') {
                    expiredAt = {
                        ...expiredAt,
                        gt: now
                    }
                } else if (q.expirationstatus === 'expired') {
                    expiredAt = {
                        ...expiredAt,
                        lte: now
                    }
                }
            }
            query = {
                ...query,
                expiresAt: expiredAt
            }
        }
        return <P>query;
    }

    public static addGlobalIDsToQuery(values: Nullable<string[] | string | GlobalID[] | GlobalID>,): Nullable<OperatorAndValues> 
    {
        return this._addIDsToQuery(values, true);
    }

    public static addLocalIDsToQuery(values:Nullable<string[] | string | LocalID[] | LocalID>,) : Nullable<OperatorAndValues>  
    {
        return this._addIDsToQuery(values, false) ;
    }

    private static _addIDsToQuery(values: Nullable<string[] | string | number[] | number | GlobalID[] | GlobalID>, global: boolean,): Nullable<OperatorAndValues> {
        if (!$ok(values)) {
            throw new ConflictError();
        }
        if ($isarray(values)) {
            if (!$count(values as Array<number | string>)) {
                return null; /* there's nothing to insert */
            }
            const ids: LocalID[] = [];
            for (let p of <string[] | number[]>values) {
                const n = global ? $gid(p) : $lid(p);
                if (!n) {
                    throw new ConflictError();
                }
                ids.push(n);
            }
            return {
                operator: PQueryOperator.in,
                value: ids
            }
        } else if ($isstring(values) && !$length(<string>values)) {
            return null; // there's nothing to insert
        } else {
            const n = global ? $gid(<string | number>values) : $lid(<string | number>values);
            if (!n) {
                throw new ConflictError();
            }
            return {
                operator: PQueryOperator.equal,
                value: n
            }
        }
    }
    public static toManyURLs<C extends BaseEntity>(elements:C[] | undefined, r?:RelativeIdentifier) : string[]
    {
        return $count(elements) ? (<C[]>elements)?.map((e:C) => (e.url(r))) : [] ;
    }
    
    protected decryptField(value:string|null): string
    {
        if(!$ok(value)){
            return '';
        }
        const algorithm = env.AES_ALGORITHM ?? 'aes-128-ecb';
        const encryptKey = env.AES_ENCRYPT_KEY ?? '';
        
        const key = Buffer.from(encryptKey, 'hex');
        let encryptedText = Buffer.from(value!, 'hex');
        let decipher = crypto.createDecipheriv(algorithm, key, '');
        let decrypted = decipher.update(encryptedText);
        decrypted =  Buffer.concat([decrypted, decipher.final()]);
        return decrypted.toString();
    }
}

export interface OperatorAndValues{
    operator: PQueryOperator,
    value: string[] | string | number[] | number | GlobalID[] | GlobalID;
}

export type NumberFilter = ModifyObject<Prisma.BigIntFilter, OnlyBigint<Prisma.BigIntFilter>, number>

export class IExpireConditionProperty<W> {
    AND?: Prisma.Enumerable<W>
    OR?: Prisma.Enumerable<W>
    NOT?: Prisma.Enumerable<W>
    publicId?: NumberFilter | LocalID
    createdAt?: OptionalDate
    updatedAt?: OptionalDate
    expiresAt?: OptionalDate
    ttl?: NumberFilter | number
    user?: Prisma.StringFilter | string
}

export type OptionalDate = Prisma.DateTimeNullableFilter | Date | string | null;

export async function destroyFileWithIdentifier(fileID:number, c:PrismaContext, referenceSet?:Set<number>):Promise<string[]> {
    let ret:string[] = [] ;
    if ($isunsigned(fileID) && !referenceSet?.has(fileID)) {
        let file = await c.trx!.file.findFirst({where: {id: fileID}})
        if ($ok(file)) {
            if ($length(file!.path))     { ret.push(file!.path) ; }
            if ($length(file!.sealPath)) { ret.push(file!.sealPath!) ; }
            await c.trx!.file.delete({where: {id: fileID}});
        }
        referenceSet?.add(fileID) ;
    }
    return ret ;
}

export function lastFilesRefenceSet(source:Nullable<LastFilesDictionary>):Set<number> {
    const referenceSet = new Set<number>() ;
    const keys = $keys(source) ;
    for (let key of keys) {
        for (let f of source![key]) { referenceSet.add(<number>f.fileId) ; }
    }
    return referenceSet ;
}

export function copyLastFilesDictionary(source:Nullable<LastFilesDictionary>):LastFilesDictionary {
    const target:LastFilesDictionary = {} ;
    const keys = $keys(source) ;
    keys.forEach(k => {
        const array:LastFile[] = [] ;
        source![k].forEach(f => { array.push({... f}) ; })
        target[k] = array ;
    }) ;
    return target ;
}


