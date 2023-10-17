import {BaseEntity} from "../BaseEntity";
import {Prisma} from "@prisma/client";
import {
    $compareDates,
    $count,
    $identifier,
    $isObject,
    $length,
    $ok,
    $timestampDb2client,
    $unsigned
} from "../../utils/commons";
import {APIRole, DocumentStatus, RoleType, ScenarioStatus, SessionStatus, UserRole} from "../../api/APIConstants";
import {SessionService} from "../../services/session.service";
import {autoInjectable, delay, inject} from "tsyringe";
import {Ascending} from "../../utils/types";
import {IScenario, ScenarioEntity} from "../scenario";
import {LocalID} from "../../api/APIIDs";
import {ScenarioService} from "../../services/scenario.service";
import {ManifestData, SessionResource, UserData} from "../../api/APIInterfaces";
import {ActorEntity, IActor} from "../actors";
import {ActorService} from "../../services/actor.service";
import {DocumentService} from "../../services/document.service";
import {DocumentEntity, IDocument} from "../documents";
import {TableNames} from "../../services/base.service";
import {NO_CONTEXT} from "../../classes/interfaces/DBConstants";
import {
    PrismaContext,
    RelativeIdentifier,
    SessionOtherData,
    SignedDocument
} from "../../classes/interfaces/DBInterfaces";
import {InternalError} from "../../utils/errors";
import {ISession, MaxScenarioRank, SessionUpdateInput} from ".";
import {FileEntity} from "../files";
import {DownloadEntity} from "../downloads";

@autoInjectable()
export class SessionEntity extends BaseEntity implements ISession {
    static tableName: TableNames = 'session';
    public id: LocalID;
    public publicId: LocalID;
    public expiresAt: Date | null;
    public lastPubObject: number;
    public status: number | null;
    public ttl: number;
    public user: string;
    public manifestFileId: LocalID | null = null;
    public otherData: Prisma.JsonValue = {};
    public userData: Prisma.JsonValue = {};
    public manifestData: Prisma.JsonValue = {};
    public createdAt: Date | null;
    public updatedAt: Date | null;
    // relationships
    private _activeScenario: ScenarioEntity | undefined;
    private _actors: ActorEntity[] | undefined;
    private _documents: DocumentEntity[] | undefined;

    private _scenarios?: ScenarioEntity[];
    private _downloads?: DownloadEntity[];
    private _file?: FileEntity;

    constructor(
      session: ISession,
      @inject(delay(() => SessionService)) private readonly service?: SessionService,
      @inject(delay(() => ScenarioService)) private readonly scenarioService?: ScenarioService,
      @inject(delay(() => ActorService)) private readonly actorService?: ActorService,
      @inject(delay(() => DocumentService)) private readonly documentService?: DocumentService
    ) {
        super();
        this.id = Number(session.id);
        this.publicId = Number(session.publicId);
        this.lastPubObject = session.lastPubObject;
        this.status = session.status;
        this.ttl = session.ttl
        this.user = session.user;
        this.otherData = session.otherData;
        this.userData = session.userData;
        this.manifestData = session.manifestData;
        this.createdAt = session.createdAt;
        this.updatedAt = session.updatedAt;
        this.expiresAt = session.expiresAt;
        this._scenarios = session.scenarios ? session.scenarios?.map(i=> new ScenarioEntity(i)) : undefined;
        this._documents = session.documents ? session.documents?.map(i=> new DocumentEntity(i)) : undefined;
        this._activeScenario = $count(session.scenarios) == 1 ? new ScenarioEntity(session.scenarios![0]) : undefined;
        this._actors = session.actors ? session.actors?.map(i=> new ActorEntity(i)) : undefined;
        this._file = session.file ? new FileEntity(session.file) : undefined
    }

    get getFile() {
        return this._file;
    }

    set setFile(value: FileEntity | undefined) {
        this._file = value;
    }

    get getDownloads() {
        return this._downloads;
    }

    set setDownloads(values: DownloadEntity[] | undefined) {
        this._downloads = values;
    }

    get getScenarios() {
        return this._scenarios;
    }

    set setScenarios(scenarios: ScenarioEntity[] | undefined) {
        this._scenarios = scenarios;
    }

    get getActiveScenario(): ScenarioEntity | undefined {
        return this._activeScenario;
    }

    set setActiveScenario(scenario: ScenarioEntity | undefined) {
        this._activeScenario = scenario;
    }

    get getActors(): ActorEntity[] | undefined {
        return this._actors;
    }

    set setActors(actors: ActorEntity[] | undefined) {
        this._actors = actors;
    }

    get getDocuments(): DocumentEntity[] | undefined {
        return this._documents;
    }

    set setDocuments(value: DocumentEntity[] | undefined){
        this._documents = value;
    }

    public identifier() : string {
        return $identifier(this.publicId);
    }

    protected internalUrl(_?:RelativeIdentifier) : string | null {
        return  `/session/${this.publicId}`;
    }

    public expirationDate(): string {
        return $timestampDb2client(this.expiresAt);
    }

    public get getStatus(){
        return <SessionStatus>this.status;
    }
    public get getOtherData():SessionOtherData{
        return this.parsePrisma<SessionOtherData>(this.otherData);
    }
    
    public get getManifestData(): ManifestData{
        return this.parsePrisma(this.manifestData);
    }

    public creationDate(): string {
        return $timestampDb2client(this.createdAt);
    }

    public modificationDate(): string {
        return $timestampDb2client(this.updatedAt);
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
    public get getUserData(): UserData{
        return this.parsePrisma(this.userData);
    }
    public async acceptsUser(actionRole: APIRole, user:string, userRole:number | string | UserRole, c:PrismaContext) : Promise<boolean> {
        if (!$length(user)) return false ;
        const role = $unsigned(userRole) ;
        if (!role || !Object.values(UserRole).includes(role)) { return false ; }

        if (role === UserRole.Action && user === this.user) { return true ; } // whatever API action we seek, this is the action creator, so right is granted

        if (role === UserRole.Action &&
            (actionRole == APIRole.Listing || actionRole == APIRole.Reading || actionRole == APIRole.Signature)) {
            // we accept a registered actor for reading, listing and finally signing if he has a signing role
            const actor = await this.service?.getActor(this.id, user, c);
            // TODO: update canSign to actor entity
            if ($ok(actor) && (actionRole != APIRole.Signature || this.canSign())) { return true ; }
        }

        if (role !== UserRole.Action && actionRole == APIRole.Listing) { return true ; } // all non-action users can list

        if ((role === UserRole.Maintenance || role === UserRole.System) &&
            (actionRole !== APIRole.Signature)) { return true ; } // maintenance/sustem users can do all they want but signing

        return false ;
    }

    public isClosed() : boolean {
        return this.status == SessionStatus.WellTerminated ||
            this.status == SessionStatus.Deleted ||
            this.status == SessionStatus.Canceled ;
    }
    public isActive() : boolean {
        return this.status == SessionStatus.Active ;
    }
    public canSign(){
        return true;
    }
    
    public async updateSession(data: SessionUpdateInput, c: PrismaContext = NO_CONTEXT){
        return this.service?.modify(data, this.id, c);
    }
    
    //
    public async getRelated<M>(property: Prisma.SessionInclude, c: PrismaContext, condition?: Prisma.SessionWhereInput){
        return this.service?.getRelated<M>(property, this.id, c, condition);
    }

    public unrelate<M>(field: 'manifestFileId', c: PrismaContext){
        return this.service?.unrelate<M>(field, this.id, c);
    }

    public isExpired() : boolean {
        return $compareDates(new Date(), this.expirationDate()) !== Ascending ;
    }

    public async scenariosUnderConstruction(c:PrismaContext): Promise<ScenarioEntity[]|undefined> {
        const data = await this.getRelated<IScenario[]>(
            {
                scenarios: {
                    where: {
                        OR: [
                            {status: ScenarioStatus.UnderConstruction},
                            {status: ScenarioStatus.UnderConstructionAfterSplit}
                        ]
                    }
                }
            }, c);
        return data?.map(i=> new ScenarioEntity(i));
    }

    public wasCertificateUsed(cid:number|bigint) : boolean {
        if (!$count(this.getOtherData?.signatures)) return false ;
        let node = this.getOtherData?.signatures?.find(s => s.cid === cid) ;
        return $ok(node) ;
    }

    public async findDocumentStatus(did: LocalID, c: PrismaContext): Promise<DocumentStatus> {
        if (this.status !== SessionStatus.Genuine && this.status !== SessionStatus.UnderConstruction) {
            const signatures = this.getOtherData?.signatures;
            let n = $count(signatures);
            while (n-- > 0) {
                const signature = (<SignedDocument[]>signatures)[n];
                if (signature.did === did) {
                    if (signature.roleType === RoleType.Signature) return DocumentStatus.Signed;
                    if (signature.roleType === RoleType.Approval) return DocumentStatus.Approved;
                }
            }
            const scenario = await this.getRelated<IScenario[]>({
                scenarios: {
                    where: {
                        status: ScenarioStatus.ActiveScenario
                    }
                }
            }, c);
            const scenarioEntity = new ScenarioEntity(scenario![0])
            if ($ok(scenarioEntity)) {
                const ret = scenarioEntity.findDocumentStatus(did);
                if ($ok(ret)) return <DocumentStatus>ret;
            }
        }
        return DocumentStatus.Genuine;
    }

    // WARNING: This 2 methods are to be used inside a transaction
    // for one or several use of the first method during of the transaction
    // if you don't update the session by other means, you need to use
    // the decond method at the end of your transaction
    public sessionNextPublicID() : number {
        this.lastPubObject = this.lastPubObject + 1 ;
        return this.lastPubObject ;
    }

    public async maxScenarioRank(c:PrismaContext):Promise<number> {
        const results = await this.scenarioService?.aggregate<MaxScenarioRank>({
            _max: {
                rank: true
            }
        }, c)
        return results?._max.rank ?? 0;
    }

    public isOpened(): boolean {
        return !this.isClosed();
    }

    public async toAPI(c: PrismaContext): Promise<SessionResource> {
        let returnValue: SessionResource = {
            id: this.publicId,
            status: this.status,
            ttl: this.ttl,
            date: this.creationDate(),
            expires: this.expirationDate()
        };

        if (!$ok(this.getActors)) {
            const actors = await this.actorService?.findMany<IActor>({
                where: {
                    sessionId: this.id
                }
            }, c);
            this.setActors = actors?.map(i => new ActorEntity(i))
        }
        returnValue.actors = SessionEntity.toManyURLs<ActorEntity>(this.getActors, this.publicId);


        if (!$ok(this.getDocuments)) {
            const documents = await this.documentService?.findMany<IDocument>({
                where: {
                    sessionId: this.id
                }
            }, c);
            this.setDocuments = documents?.map(i => new DocumentEntity(i));
        }
        returnValue.documents = SessionEntity.toManyURLs<DocumentEntity>(this.getDocuments, this.publicId);


        if (!$ok(this.getScenarios)) {
            const data = await this.scenarioService?.findMany<IScenario>({
                where: {
                    sessionId: this.id
                }
            }, c);
            this.setScenarios = data?.map(i=> new ScenarioEntity(i));
        }
        const scenarioEntities = this.getScenarios?.map(i=> new ScenarioEntity(<IScenario>i))
        returnValue.scenarios = SessionEntity.toManyURLs<ScenarioEntity>(scenarioEntities, this.publicId);

        if ($isObject(this.manifestData)) {
            returnValue['manifest-data'] = this.getManifestData;
        }
        if ($isObject(this.userData)) {
            returnValue['user-data'] = this.getUserData;
        }

        return returnValue;
    }

    public async myActiveScenario(c: PrismaContext): Promise<ScenarioEntity | null> {
        if (this.isActive()) {
            if (!$ok(this.getActiveScenario)) {
                const scenario = await this.getRelated<IScenario[]>({
                    scenarios: {
                        where: {
                            status: ScenarioStatus.ActiveScenario
                        }
                    }
                }, c)
                if (!$count(scenario)) {
                    throw new Error('Database loading session error: cannot find active scenario');
                }
                this.setActiveScenario = new ScenarioEntity(scenario![0]);
            }
            return $ok(this.getActiveScenario) ? <ScenarioEntity>this.getActiveScenario : null;
        }
        return null;

    }

    public async hasActorWithLogin(login:string, c:PrismaContext) : Promise<boolean> {
        const others = await this.service?.count({
            where:{
                id: this.id,
                actors:{
                    some:{
                        login: login
                    }
                }
            },
        }, c);
        return others ? others > 0 : false;
    }

}
