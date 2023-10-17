import {Prisma} from "@prisma/client";
import { injectable } from "tsyringe";
import {BaseService, TableNames} from "./base.service";
import {IDocumentTypeMap} from "../classes/interfaces/IDocumentTypeMap";
import {DocumentCreateInput, DocumentEntity, DocumentUpdateInput, IDocument} from "../entities/documents";
import { NO_CONTEXT } from "../classes/interfaces/DBConstants";
import { PrismaContext } from "../classes/interfaces/DBInterfaces";
import {prisma} from "../classes/interfaces/prisma";

@injectable()
export class DocumentService extends BaseService<
    Prisma.DocumentDelegate<undefined>,
    IDocumentTypeMap>{
    constructor() {
        super(prisma.document);
    }
    protected tableName:TableNames = 'document';

    async insert(document: DocumentCreateInput, c: PrismaContext = NO_CONTEXT) {
        const data = await this.create<IDocument>({ data: document }, c);
        return new DocumentEntity(data)
    }
    
    async modify(data: DocumentUpdateInput, id: number, c: PrismaContext = NO_CONTEXT){
        let document = await this.update<IDocument>({
            where: {
                id: id
            },
            data: data
        }, c)
        return new DocumentEntity(document);
    }
    
}
