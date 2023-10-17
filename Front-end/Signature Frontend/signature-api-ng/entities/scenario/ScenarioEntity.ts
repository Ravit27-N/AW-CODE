import {BaseEntity, destroyFileWithIdentifier, lastFilesRefenceSet} from "../BaseEntity";
import {Prisma} from "@prisma/client";
import {DocumentStatus, RoleType, ScenarioStatus, SessionStatus} from "../../api/APIConstants";
import {autoInjectable, delay, inject} from "tsyringe";
import {$count, $ok, $timestampDb2client} from "../../utils/commons";
import {Nullable} from "../../utils/types";
import { ScenarioService } from "../../services/scenario.service";
import {LocalID} from "../../api/APIIDs";
import {automatCurrentNode} from "../../api/automat/automat";
import {ISession, SessionEntity} from "../sessions";
import {SessionService} from "../../services/session.service";
import {DocumentService} from "../../services/document.service";
import {DocumentEntity, IDocument} from "../documents";
import {ManifestData, ScenarioResource, StepNode, UserData} from "../../api/APIInterfaces";
import JsonValue = Prisma.JsonValue;
import {TableNames} from "../../services/base.service";
import { RelativeIdentifier, PrismaContext, ScenarioOtherData, LastFilesDictionary, LastFile } from "../../classes/interfaces/DBInterfaces";
import { InternalError } from "../../utils/errors";
import {IScenario, ScenarioUpdateInput} from ".";

@autoInjectable()
export class ScenarioEntity extends BaseEntity implements IScenario {
    static tableName: TableNames = 'scenario';

    public id: LocalID;
    public publicId: LocalID;
    public rank: number;
    public signatureFormat: number;
    public signatureLevel: number;
    public status: number;
    public otherData: Prisma.JsonValue = {};
    public stepsDefinition: Prisma.JsonValue = {};
    public userData: Prisma.JsonValue = {};
    public manifestData: Prisma.JsonValue = {};
    createdAt: Date | null
    updatedAt: Date | null
    public sessionId: LocalID;
    private _session?: SessionEntity;

    constructor(
        scenario: IScenario, 
        @inject(delay(() => ScenarioService)) private readonly service?: ScenarioService,
        @inject(delay(() => SessionService)) private readonly sessionService?: SessionService) {
        super();
        this.id = Number(scenario.id);
        this.publicId = Number(scenario.publicId);
        this.rank = scenario.rank;
        this.signatureFormat = scenario.signatureFormat;
        this.status = scenario.status;
        this.signatureLevel = scenario.signatureLevel
        this.otherData = scenario.otherData;
        this.stepsDefinition = scenario.stepsDefinition;
        this.sessionId = Number(scenario.sessionId);
        this.createdAt = scenario.createdAt;
        this.updatedAt = scenario.updatedAt;
        this._session = scenario.session ? new SessionEntity(scenario.session) : undefined;
    }

    public get getStepsDefinition(): StepNode[] | []{
        return this.parsePrisma<StepNode[]>(this.stepsDefinition)
    }
    
    get getManifestData():ManifestData{
        return this.parsePrisma(this.manifestData)
    }

    public get getSession(): SessionEntity | undefined{
        return this._session;
    }

    public set setSession(value: SessionEntity | undefined){
        this._session = value;
    }
    
    public get getManifest(): ManifestData | undefined{
        return this.parsePrisma(this.manifestData);
    }

    public set setManifest(value: JsonValue){
        this.manifestData = value;
    }

    public get getUserData(): UserData | undefined{
        return this.parsePrisma(this.userData);
    }

    public set setUserData(value: JsonValue){
        this.userData = value;
    }

    protected internalUrl(relativeIdentifier?:RelativeIdentifier) : string | null
    {
        return  `/session/${!!relativeIdentifier ? relativeIdentifier : this.getSession!.publicId}/scenario/${this.publicId}`;
    }
    public get getStatus(){
        return <SessionStatus>this.status;
    }

    public async updateScenario(data: ScenarioUpdateInput, c: PrismaContext){
        return this.service?.modfiy(data, this.id, c);
    }

    //
    public getRelated<M>(property: Prisma.ScenarioInclude, c: PrismaContext){
        return this.service?.getRelated<M>(property, this.id, c);
    }
    
    public get getOtherData(){
        return this.parsePrisma<ScenarioOtherData>(this.otherData);
    }

    public async destroyScenarioFiles(destroyGeneratedFiles:boolean, c:PrismaContext):Promise<string[]> {
        const filesDict1 = this.getOtherData.sourceFiles,
            filesDict2 = destroyGeneratedFiles ? this.getOtherData.generatedFiles : undefined ;

        if ($ok(filesDict1) || $ok(filesDict2)) {
            const referenceSet = lastFilesRefenceSet(this.getOtherData.originalLastFiles) ;
            const paths1 = await _destroyFiles(referenceSet, filesDict1, c) ;
            const paths2 = await _destroyFiles(referenceSet, filesDict2, c) ;
            return [...paths1, ...paths2] ;
        }
        return [] ;
    }

    public isActive(): boolean {
        return this.status == ScenarioStatus.ActiveScenario;
    }

    public findDocumentStatus(did: LocalID): DocumentStatus | null {
        if (this.isActive()) {
            const node = automatCurrentNode(this.getOtherData.automat);
            if ($ok(node)) {
                if (node?.dids.includes(did)) {
                    if (node.roleType === RoleType.Approval) return DocumentStatus.Approbation;
                    if (node.roleType === RoleType.Signature) return DocumentStatus.Signing;
                }
            }
        }
        return null;
    }

    public async mySession(c:PrismaContext) : Promise<SessionEntity> {
        const session = await this.sessionService?.findFirst<ISession>({
            where: {
                id: this.sessionId
            }
        }, c)

        if (!$ok(session)) {
            throw new Error('Database loading session error');
        }
        return new SessionEntity(<ISession>session);
    }

    public isUnderConstruction() : boolean {
        return this.status == ScenarioStatus.UnderConstruction ||
          this.status == ScenarioStatus.UnderConstructionAfterSplit ;

    }

    public creationDate(): string {
        return $timestampDb2client(this.createdAt);
    }

    public modificationDate(): string {
        return $timestampDb2client(this.updatedAt);
    }

    public async fetchLastFilesFromDocuments(c: PrismaContext): Promise<LastFilesDictionary> {
        const docService = new DocumentService();
        let ret: LastFilesDictionary = {};
        if ($count(this.getOtherData.dids)) {
            const session = await this.mySession(c);
            for (let did of this.getOtherData.dids) {
                let realDoc = await docService.sessionObjectWithPublicID<IDocument>(session, did, c);
                if (!$ok(realDoc)) {
                    throw new Error('Database loading document error');
                }
                const docEntity = new DocumentEntity(<IDocument>realDoc)
                docEntity.getLastFiles()
                ret[did] = docEntity.getLastFiles() ?? [];
            }
        }
        return ret;
    }

    public async toAPI(c:PrismaContext) : Promise<ScenarioResource> {
        let returnValue:ScenarioResource = {
            date:this.creationDate(),
            id: (await this.mySession(c)).publicId,
            sid:this.publicId,
            status:this.status,
            documents:this.getOtherData.documentURLs,
            format:this.signatureFormat,
            level:this.signatureLevel,
            steps:this.getStepsDefinition,
        } ;
        if ($ok(this.manifestData)) {
            returnValue['manifest-data'] = this.getManifest ;
        }
        if ($ok(this.userData)) {
            returnValue['user-data'] = this.getUserData ;
        }
        return returnValue ;
    } ;

    public sourceFileReferences(did: LocalID, aid: LocalID): LastFile[] {
        const sourceFiles = this.getOtherData?.sourceFiles;
        if (!$ok(sourceFiles)) return [];
        let lastFiles = (<LastFilesDictionary>sourceFiles)[<number>did];
        if (!$count(lastFiles)) return [];

        return lastFiles.filter(f => !$ok(f.aid) || f.aid === aid)
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
}

async function _destroyFiles(referenceSet:Set<number>, toBeDestroyedFiles:Nullable<LastFilesDictionary>, c:PrismaContext):Promise<string[]>
{
    let ret:string[] = [] ;
    if ($ok(toBeDestroyedFiles)) {
        for (let key in toBeDestroyedFiles!) {
            let documentFiles = (toBeDestroyedFiles!)[key] ;
            for (let f of documentFiles) {
                const toBeDestroyedFilesPaths = await destroyFileWithIdentifier(<number>f.fileId, c, referenceSet) ;
                ret = [...ret, ...toBeDestroyedFilesPaths] ;
            }
        }
    }
    return ret ;
}
