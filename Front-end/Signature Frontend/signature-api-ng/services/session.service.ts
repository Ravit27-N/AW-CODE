import {Prisma} from "@prisma/client";
import {delay, inject, injectable} from "tsyringe";
import {BaseService, TableNames} from "./base.service";
import {ISessionTypeMap} from "../classes/interfaces/ISessionTypeMap";
import {SessionCreateInput, SessionEntity, SessionUpdateInput, ISession} from "../entities/sessions";
import {ActorEntity, IActor} from "../entities/actors";
import {$date2string, $finalDateString, $now, $ok} from "../utils/commons";
import {LocalID} from "../api/APIIDs";
import {NO_CONTEXT} from "../classes/interfaces/DBConstants";
import {PrismaContext} from "../classes/interfaces/DBInterfaces";
import {ActorService} from './actor.service';
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class SessionService extends BaseService<
    Prisma.SessionDelegate<undefined>,
    ISessionTypeMap> {
    constructor(
        @inject(delay(() => ActorService)) private readonly actorService?: ActorService) {
        super(prisma.session);
    }

    protected tableName: TableNames = 'session';

    async getActor(sessionId: LocalID, user: string, c: PrismaContext = NO_CONTEXT) {
        const actor = await this.actorService?.findFirst<IActor>({
            where: {
                sessionId: sessionId,
                login: user
            }
        }, c)
        return $ok(actor) ? new ActorEntity(<IActor>actor) : null;
    }

    async insert(session: SessionCreateInput, c: PrismaContext = NO_CONTEXT) {
        const now = $date2string($now());
        const expiresAt = $finalDateString(now, <number>session.ttl);
        const newSession = await this.create<ISession>({
            data: {
                ...session, expiresAt
            }
        }, c);
        return new SessionEntity(newSession);
    }

    async modify(data: SessionUpdateInput, id: LocalID, c: PrismaContext = NO_CONTEXT) {
        let session = await this.update<ISession>({
            where: {
                id: id
            },
            data: data
        }, c)
        return new SessionEntity(session);
    }

}