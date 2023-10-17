import { $string } from "../utils/commons";
import { $uuid } from "../utils/crypto";
import { TSTest } from "../utils/tstester";

import { ActorType, SignatureFormat, SignatureLevel, SignatureType, SigningProcess } from "../api/APIConstants";
import { NGT } from "../client/test-ng"

import { checkConnection } from "./commons.test";

export const simpleManifest = TSTest.group("Test 2: Simple Manifest", async (group) => {

    const config = {
        user:$string(process.env.TLOGIN2),
        password:$string(process.env.PASSWD2),
        url:$string(process.env.TURL2),
        resourcesFolder:$string(process.env.RESRCS2),
        outputFolder:$string(process.env.OUTPUT2),
        createFolders:true
    } ;
    const api = new NGT(config) ;

    group.unary('verify connection', async (t) => checkConnection(api, config, t) ) ;

    group.unary('generating manifest', async(t) => {
        const pdf = await api.uploadFile('pdf1.pdf', t) ;
        t.expect0(pdf).filled() ;
        
        const sessionURL = await api.createSession({ttl:900}) ;
        t.expect1(sessionURL).filled() ;

        const documentURL = await api.addDocument(sessionURL!, {
            'file-name':'pdf1.pdf',
            title:'My unique PDF',
            upload:pdf!,
        }) ;
        t.expect2(documentURL).filled() ;

        const actorURL = await api.addActor(sessionURL!, {
            name:'DURAND',
            email:'paul.durand@free.fr',
            'first-name':'Paul',
            country:'FR',
            roles:['approval', 'sign'],
            type:ActorType.Person
        }) ;
        t.expect3(actorURL).filled() ;

        const signatureTag = SigningProcess.Cosign ;
        const scenarioURL = await api.addScenario(sessionURL!, {
            documents: [documentURL as string],
            format:SignatureFormat.PAdES,
            level:SignatureLevel.LTA,
            steps:[
                {
                    process:signatureTag,
                    steps:[actorURL!],
                    signatureType:SignatureType.Envelopped,
                    cardinality:'all'
                }
            ]    
        }) ;
        t.expect3(scenarioURL).filled() ;

        const activated = await api.activateScenario(scenarioURL!, {
            'manifest-data':{
                'activation-reference': $uuid()
            }
        }) ;
        t.expect4(activated).true() ;

        const docs = await api.signAvailableDocumentsForActor(sessionURL!, actorURL!, signatureTag, 1, t) ;
        t.expect5(docs).filled() ;
        const doc = docs![0] ;
        t.expect6(doc.actor).is(actorURL) ;
        t.expect7(doc.document).is(documentURL) ;

        const file = await api.getAndSaveDocument(documentURL!, `unique-doc1.pdf`) ;
        t.register('signed file', file) ;
        t.expect8(file).filled() ;

        const closedResource = await api.closeSession(sessionURL!, {
            reason:'end of our work', 
            force:false
        }) ;
        t.expect8(closedResource).OK() ;

        const manifestFile = api.recoverManifest(sessionURL!, t) ;
        t.register('manifest file', manifestFile) ;
        t.expect9(manifestFile).filled() ;
    })
}) ;
