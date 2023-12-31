import { StringArrayDictionary } from "./utils/types";
import {$count, $length, $ok, $string, $exit} from './utils/commons'
import {$uuid} from "./utils/crypto";
import {$inspect, $logterm} from "./utils/utils";
import { ActorType, SignatureFormat, SignatureLevel, SignatureType, SigningProcess } from "./api/APIConstants";
import { $url2lid } from "./api/APIIDs";
import { CreateActorBody, ScenarioBody } from "./api/APIInterfaces";
import { NGT } from "./client/test-ng";
import  env from "./env-config";
import { inspect } from "util";

const api = new NGT({
	user: $string(env.SIGN_SERVER_LOGIN),
	password: $string(env.SIGN_SERVER_PASS),
	url: `${env.APP_SCHEMES}://${env.APP_ADDRESS}:${env.APP_PORT}/api/v1`,
	resourcesFolder: "resources",
	outputFolder: "output",
	createFolders: true,
	debug: true
}) ;

const start = async (): Promise<void> => {

	// make a ping on our api
	$logterm(">>>>>>>>> &yCHECKING API &x>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	$logterm('\n>>>> Pinging api...') ;
	const resp = await api.ping() ;
	api.check($ok(resp) && $length(resp?.requestId) > 0, 'ping api') ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;


	$logterm(">>>>>>>>> &yUPLOADS AND SESSION CREATION &x>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	// upload a file
	const pdf1 = await api.uploadFile('pdf1.pdf') ;
	// creating a session
	$logterm("\n>>>> Creating Session 1") ;
	let ret:string|null = await api.createSession({ttl:900}) ;
	api.check($length(ret) > 0, `creating session1 :'${ret}'`) ;
	const session1 = <string>ret ;

	// uploading a new file
	const pdf2 = await api.uploadFile('pdf2.pdf') ;

	$logterm("\n>>>> Trying to create a too short 2nd session") ;
	ret = await api.createSession({ttl:700}) ;
	api.check($ok(ret), 'hopefully failing to create a too short ttl session') ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

	$logterm(">>>>>>>>> ADDING DOCUMENTS TO SESSION >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	// adding both documents to the session
	$logterm(`\n>>>> Will add 1st document, to session 1 ${session1}`) ;
	ret = await api.addDocument(session1, {
		'file-name':'pdf1.pdf',
		title:'My first PDF',
		upload:pdf1!,
	}) ;
	api.check($length(ret) > 0, `adding pdf1 ${ret}  to session1`) ;
	// const document1 = ret as string ;
	$logterm(`\n>>>> Will add 2nd document`) ;
	ret = await api.addDocument(session1, {
		'file-name':'pdf2.pdf',
		title:'My Second PDF',
		upload:pdf2!,
	}) ;
	api.check($length(ret) > 0, `adding pdf2 ${ret} to session1`) ;
	// const document2 = ret as string ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;


	$logterm(">>>>>>>>> ADDING ACTORS TO SESSION >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	const actor1body:CreateActorBody = {
		name:'DURAND',
		email:'paul.durand@free.fr',
		'first-name':'Paul',
		country:'FR',
		roles:['approval', 'sign'],
		type:ActorType.Person,
	}
	$logterm(`\n>>>> Will add actor1 : ${$inspect(actor1body)}`) ;

	ret = await api.addActor(session1, actor1body) ;
	api.check($length(ret) > 0, `adding actor1: ${ret} to session1`) ;
	const actor1 = ret as string ;

	const actor2body:CreateActorBody = {
		name:'MARTIN',
		'first-name':'Jean',
		email:'j-martin@yahoo.com',
		country:'FR',
		roles:['approval', 'sign'],
		mobile:'0645247000',
		type:ActorType.Person
	}
	$logterm(`\n>>>> Will add actor2 : ${$inspect(actor2body)}`) ;
	ret = await api.addActor(session1, actor2body) ;
	api.check($length(ret) > 0, `adding actor2: ${ret} to session1`) ;
	const actor2 = ret as string ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

	$logterm(">>>>>>>>> LISTING AND DESTROYING USELESS SESSIONS >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	let session2 = await api.createSession({ttl:7200}) ;
	api.check($length(session2) > 0, 'creating a second session to be listed') ;

	let session3 = await api.createSession({ttl:7200}) ;
	api.check($length(session3) > 0, 'creating a third session to be listed') ;

	$logterm('\n>>>> Will list sessions:') ;
	let list:string[] = await api.sessionsList() ;
	$logterm(`all sessions list = ${$inspect(list)}`) ;
	api.check($count(list) >= 3, 'checking the number of sessions') ;

	$logterm('\n>>>> Closing all previous sessions but our own:') ;
	for (let url of list) {
		if (url !== session1) {
			const closeRet = await api.closeSession(url, {reason:'not usefull remaining sessions', force:true}) ;
			api.check($ok(closeRet), `closing of session ${url}`) ;
		}
	} ;

	list = await api.sessionsList({ status_mask:3}) ; // only genuine and under construction sessions
	// list =[`/api/v1/session/${num}`]
	$logterm(`\n>>>> Genuine and under construction sessions list:\n${$inspect(list)}`) ;
	api.check($count(list) === 1, 'checking the number of sessions') ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

	$logterm(">>>>>>>>> LISTING CURRENT SESSION DOCUMENTS AND ACTORS >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	$logterm("\n>>>> Will list session1's documents:") ;
	let docs = await api.documentsList(session1) ;
	api.check($count(docs) === 2, 'checking the number of added documents') ;
	$logterm(`session1 documents = ${$inspect(docs)}`) ;

	$logterm("\n>>>> Will list session1's actors:") ;
	let actors = await api.actorsList(session1) ;
	api.check($count(actors) === 2, 'checking the number of added actors') ;
	$logterm(`session1 actors = ${$inspect(actors)}`) ;

	if ($count(actors)) {
		$logterm(`\n>>>> Will get actors' content:`) ;
		for (let a of actors) {
			const aret = await api.getActor(a) ;
			api.check($ok(aret), `checking actor '${a}' content:`) ;
			$logterm('-------------------------------------------------') ;
			$logterm(`Actor content:\n${$inspect(aret)}`) ;
			$logterm('-------------------------------------------------') ;
		}
	}
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

	$logterm(">>>>>>>>> CREATE AND ACTIVATE A SCENARIO FOR CURRENT SESSION >>>>>>>>>>>>>>>>>>>>>>>>>") ;
	const approvalTag = 'legal' ; // this a parametric approbation step
	const signatureTag = SigningProcess.Cosign ;
	const scenario1Body:ScenarioBody = {
		documents: docs,
		format:SignatureFormat.PAdES,
		level:SignatureLevel.LT,
		steps:[
			{
				process:approvalTag,
				steps:[actor1, actor2],
				cardinality:'one'
			},
			{
				process:signatureTag,
				steps:[actor1, actor2],
				signatureType:SignatureType.Envelopped,
				cardinality:'all'
			}
		]
	}

	$logterm(`\n>>>> Will add scenario1 : ${$inspect(scenario1Body)}`) ;
	const rets = await api.addScenario(session1, scenario1Body) ;
	api.check($length(rets) > 0, 'adding scenario1 to session1') ;
	const scenario1 = rets as string ;

	$logterm(`\n>>>> Will activate scenario : ${scenario1}`) ;
	const activated = await api.activateScenario(scenario1, {
		'manifest-data':{
			'activation-reference': $uuid()
		}
	}) ;
	api.check(activated, 'checking activating scenario1') ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;


	$logterm(">>>>>>>>> DOCUMENT'S APPROBATION >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	$logterm(`\n>>>> Get documents available for approval for actor: ${actor2}`) ;
	const documents = await api.taggedDocuments(session1, {actor:$url2lid(actor2), tags:approvalTag}) ;
	api.check($ok(documents), `checking documents return for '${approvalTag}' approval`) ;
	const legalDocs = (<StringArrayDictionary>documents)[approvalTag] ;
	api.check($count(legalDocs) === 2, `checking legal documents count return for '${approvalTag}' approval`) ;
	$logterm(`${approvalTag} documents = ${$inspect(legalDocs)}`) ;

	// OTP generation and checking
	$logterm(`\n>>>> Will generate OTP for approbation`) ;
	const otpResource = await api.generateOTP(session1, {
		actor:actor2,
		documents:legalDocs,
		length:5,
		numeric:true,
		ttl:60,
		tag:approvalTag
	}) ;
	api.check($length(otpResource?.otp) === 5, `OTP generation for actor '${actor2}' and tag '${approvalTag}' approval`) ;

	$logterm(`Did generate OTP:"${otpResource?.otp}"`) ;

	const otpflag = await api.checkOTP(session1, {
		actor:actor2,
		documents:legalDocs,
		otp:<string>otpResource?.otp,
		tag:approvalTag
	}) ;
	api.check(otpflag, `verifiying OTP '${otpResource?.otp}' for actor '${actor2}' and tag '${approvalTag}' approval`) ;


	// OK we, will approve all our documents by actor2 :
	$logterm(`\n>>>> Will ${approvalTag} approve documents = ${$inspect(legalDocs)}`) ;
	const approbation = await api.approveDocuments(session1, {
		actor:actor2,
		documents:legalDocs,
		otp:<string>otpResource?.otp,
		tag:approvalTag
	}) ;
	api.check($ok(approbation) && $count(approbation?.signatures) === 2, `checking documents '${approvalTag}' approval by actor ${actor2}`) ;
	$logterm('-------------------------------------------------') ;
	$logterm(`Done approval by: ${actor2}:\n${$inspect(approbation?.signatures)}`) ;
	$logterm('-------------------------------------------------') ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

	$logterm(">>>>>>>>> DOCUMENT'S SIGNATURES >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	await api.signAvailableDocumentsForActor(session1, actor2, signatureTag, 2) ;
	await api.signAvailableDocumentsForActor(session1, actor1, signatureTag, 2) ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

	$logterm(">>>>>>>>> CHECKING AND SAVING DOCUMENTS >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	$logterm('\n>>>> Checking Documents lists') ;
	const remainingDocs1 = await api.taggedDocuments(session1, {actor:$url2lid(actor1)}) ;
	api.check($ok(remainingDocs1), `checking documents return for '${actor1}' signature ${inspect(remainingDocs1)}`) ;
	const docs1 = (<StringArrayDictionary>remainingDocs1)[signatureTag] ;
	api.check($count(docs1) === 0, `verifying that there's no docs to sign for '${actor1}'`) ;

	const remainingDocs2 = await api.taggedDocuments(session1, {actor:$url2lid(actor2)}) ;
	api.check($ok(remainingDocs2), `checking documents return for '${actor2}' signature ${inspect(remainingDocs2)}`) ;
	const docs2 = (<StringArrayDictionary>remainingDocs2)[signatureTag] ;
	api.check($count(docs2) === 0, `verifying that there's no docs to sign for '${actor1}'`) ;

	const remainingDocs = await api.taggedDocuments(session1, {tags:signatureTag}) ;
	api.check($ok(remainingDocs), `checking documents return for '${signatureTag}' signature tag ${inspect(remainingDocs)}`) ;
	const rdocs = (<StringArrayDictionary>remainingDocs)[signatureTag] ;
	api.check($count(rdocs) === 0, `verifying that there's no docs to sign for tag '${signatureTag}'`) ;

	$logterm('\n>>>> Saving documents') ;
	const ln = $count(legalDocs) ;
	for (let i = 0 ; i <  ln ; i++) {
		await api.getAndSaveDocument(legalDocs[i], `pdf${i+1}.pdf`) ;
	}
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

	$logterm(">>>>>>>>> SCENARIO & SESSION STATE + CLOSING >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	const scenret = await api.getScenario(scenario1) ;
	api.check($ok(scenret), `checking scenario '${scenario1}' content`) ;
	$logterm('-------------------------------------------------') ;
	$logterm(`Scenario content:\n${$inspect(scenret)}`) ;
	$logterm('-------------------------------------------------') ;

	let sesret = await api.getSession(session1) ;
	api.check($ok(sesret), `checking session '${session1}' content`) ;
	$logterm('-------------------------------------------------') ;
	$logterm(`Session content:\n${$inspect(sesret)}`) ;
	$logterm('-------------------------------------------------') ;


	$logterm('\n>>>> Will close session') ;
	let close = await api.closeSession(session1, {reason:'end of our work', force:false})
	api.check($ok(close), 'closing of session1') ;

	sesret = await api.getSession(session1) ;
	api.check($ok(sesret), `checking session '${session1}' content after closing`) ;
	$logterm('-------------------------------------------------') ;
	$logterm(`Closed Session content:\n${$inspect(sesret)}`) ;
	$logterm('-------------------------------------------------') ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

	$logterm(">>>>>>>>> GENERATE AND DOWNLOAD MANIFEST >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	await api.recoverManifest(session1) ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

	$exit('Test concluded with no errors', 0)
}



start() ;
