import { $string } from "../utils/commons";
import { $hash, HashMethods } from "../utils/crypto";
import { TSTest } from "../utils/tstester";

import { SignatureLevel, SignatureType } from "../api/APIConstants";
import { NGT } from "../client/test-ng"
import { $now } from "../utils/commons";

import { checkConnection, fileTokenToXMLString } from "./commons.test";

export const directSignature = TSTest.group("Test 1: Direct signature", async (group) => {
    const config = {
        user:$string(process.env.TLOGIN1),
        password:$string(process.env.PASSWD1),
        url:$string(process.env.TURL1),
        resourcesFolder:$string(process.env.RESRCS1),
        outputFolder:$string(process.env.OUTPUT1),
        createFolders:true
    } ;
    const api = new NGT(config) ;
	const now = $now() ;

    group.unary('verify connection', async (t) => checkConnection(api, config, t)) ;

    group.unary('Check PAdES signature with server certificate', async(t) => {
        const s = await api.directPadesSign('pdf1.pdf', SignatureLevel.LT, 'server') ;
        t.expect(s).filled() ;
    }) ;
	group.unary('Check XAdES signature with server certificate', async(t) => {
        const token = {
            name:'John DOE',
            user:'testlogin',
            size:142857,
            hash:$string($hash(Buffer.from("Je ne sais pas pouquoi la pluie quitte la haut ses oripaux, que sont ces lourds nuages gris pour se briser sur nos coteaux"), HashMethods.SHA256)),
            date:now
        } ;
        let xmlString = fileTokenToXMLString(token) ;
    	const s1 = await api.directXadesSign(xmlString, SignatureType.Envelopped, 'seal-envelopped.xml', SignatureLevel.B, 'server') ;
        t.expect1(s1).filled() ;

        const s2 = await api.directXadesSign(xmlString, SignatureType.Envelopping, 'seal-envelopping.xml', SignatureLevel.B, 'server') ;
        t.expect2(s2).filled() ;
    }) ;

    group.unary('Check PAdES signature with generated certificate', async(t) => {
        const s = await api.directPadesSign('pdf2.pdf', SignatureLevel.LTA, 'generate') ;
        t.expect(s).filled() ;
    })

}) ;
