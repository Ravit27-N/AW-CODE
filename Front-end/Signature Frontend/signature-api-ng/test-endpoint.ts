import {$length, $ok, $string, $exit, $now} from './utils/commons'
import {$inspect, $logterm} from "./utils/utils";
import {$hash, HashMethods} from './utils/crypto';

import { SignatureLevel, SignatureType,   } from "./api/APIConstants";
import { NGT } from "./client/test-ng";
import { fileTokenToXMLString } from "./classes/CertignaEndPoint";
import  env from "./env-config";

const api = new NGT({
    user:$string(env.SIGN_SERVER_LOGIN),
    password:$string(env.SIGN_SERVER_PASS),
    url:"URL TO BE DEFINED",
    resourcesFolder:"TO BE DEFINED",
    outputFolder:"TO BE ALSO DEFINED",
    debug:true
}) ;

const start = async (): Promise<void> => {
	const now = $now() ;
	$logterm(`Starting Endpoint Test at ${now}...`) ;

	// make a ping on our api
	$logterm(`Pinging api...`)
	const resp = await api.ping() ;
	api.check($ok(resp) && $length(resp?.requestId) > 0, 'ping api') ;
	$logterm(`Ping did return:\n${$inspect(resp)}`) ;

	// Trying to server-sign a document
	$logterm('Check PAdES signature with server certificate ...') ;
	await api.directPadesSign('pdf1.pdf', SignatureLevel.LT, 'server') ;

    const hash = $hash(Buffer.from("Je ne sais pas pouquoi la pluie quitte la haut ses oripaux, que sont ces lourds nuages gris pour se briser sur nos coteaux"), HashMethods.SHA256) ;
	api.check($ok(hash), 'buffer hash') ;

    const token = {
		name:'John DOE',
		user:'testlogin',
		size:142857,
		hash:hash!,
		date:now
	} ;
	let xmlString = fileTokenToXMLString(token) ;

	$logterm('Check XAdES signature with server certificate ...') ;
	await api.directXadesSign(xmlString, SignatureType.Envelopped, 'seal-envelopped.xml', SignatureLevel.B, 'server') ;
	await api.directXadesSign(xmlString, SignatureType.Envelopping, 'seal-envelopping.xml', SignatureLevel.B, 'server') ;

	$logterm('Check PAdES signature with generated certificate ...') ;
	await api.directPadesSign('pdf1.pdf', SignatureLevel.LTA, 'generate') ;

	$exit('Endpoint Test concluded with no errors', 0)

} ;

start() ;
