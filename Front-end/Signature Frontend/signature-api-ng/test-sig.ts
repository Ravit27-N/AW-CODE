import { $count, $length, $ok, $string, $exit } from "./utils/commons";
import { $uuid } from "./utils/crypto";
import { StringArrayDictionary } from "./utils/types";

import {   $inspect, $logterm } from "./utils/utils";
import { SignatureFormat, SignatureLevel, SignatureType, SigningProcess } from "./api/APIConstants";
import { $url2lid } from "./api/APIIDs";
import { CreateActorBody, ScenarioBody } from "./api/APIInterfaces";

import { NGT } from "./client/test-ng";
import  env from './env-config';

const api = new NGT({
    user:$string(env.SIGN_SERVER_LOGIN),
    password:$string(env.SIGN_SERVER_PASS),
    url:"URL TO BE DEFINED",
    resourcesFolder:"TO BE DEFINED",
    outputFolder:"TO BE ALSO DEFINED",
    debug:true
}) ;


const start=async ():Promise<void> => {
 // make a ping on our api
 $logterm(">>>>>>>>> &yCHECKING API &x>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
 $logterm('\n>>>> Pinging api...') ;
 const resp = await api.ping() ;
 api.check($ok(resp) && $length(resp?.requestId) > 0, 'ping api') ;
 $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
  $logterm("\n >>>>>> START SIG-1 CREATE A SESSION TTL 2 DAYS ");
  let session1:string|null = await api.createSession(
    {ttl: 7200,
    "manifest-data":{
      operationDesc: "Signature du contrat Innovation & Trust",
      sourceService: "Service Juridique"
      },
    "user-data":{
      internalID: "70E9C6FF-3314-4C05-948A-8718389E372A",
      desc: "Signature du contrat Innovation & Trust"
    }
    });
  api.check($length(session1)>0,`create a session: ${session1}`);
  $logterm("\n <<<<<< END SIG-1 CREATE A SESSION TTL 2 DAYS ");
  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

  $logterm("\n >>>>>> START SIG-3 Upload a document ");
  const pdf1 = await api.uploadFile('test.pdf');
  api.check($length(pdf1)>0,`upload a document: ${pdf1}`);


  $logterm(`\n >>>>>>  add document ${pdf1} to session ${session1} `);
  let ret = await api.addDocument(session1!,{
    "file-name":"test.pdf",
    title:"add document to session",
    upload:pdf1!,
  })
  const document1 = ret;

  api.check($length(ret)>0, `add document to a session ${ret}`);
  $logterm(`\n <<<<<< END SIG-3 add document ${pdf1} to session ${session1} `);
  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

  $logterm("\n>>>>>> START SIG-4 CREATE TWO ACTORS");
  let actor11:CreateActorBody={
    "first-name": "Clément",
    name: "Olivier",
    login: "",
    email: "clement.olivier@presta.tessi.fr",
    mobile: "+33675507110",
    country: "FR",
    type: 0,
    roles: ["sign", "to", "cc"],
    "user-data": {},
    "manifest-data": {}
    }
  let actor21:CreateActorBody = {
    "first-name": "Ouahiba",
    name: "Benchourak",
    login: "",
    email: "ouahiba.benchourak@presta.tessi.fr",
    mobile: "+33762832292",
    country: "FR",
    type: 0,
    roles: ["sign", "to", "cc"],
    "user-data": {},
    "manifest-data": {}
    }
  $logterm(" ADD ACTOR1 TO SESSION");
  let actor1 = await api.addActor(session1!,actor11);
  api.check($length(actor1)>0, `ADD ACTOR 1 TO SESSION : ${ret}`);
  $logterm("ADD ACTOR2 TO SESSION");
  let actor2 = await api.addActor(session1!,actor21);
  api.check($length(actor2)>0, `ADD ACTOR 2 TO SESSION : ${ret}`);
  $logterm(" \n<<<<<< END SIG-4 CREATE TWO ACTORS");
   $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

  $logterm("\n >>>>>> START SIG-5   CREATE A SCENARIO FOR AN ORDERED COSIGN PROCESS ");
  const signatureTag = SigningProcess.OrderedCosign ;
  const scenario1Body:ScenarioBody = {
		documents: [document1!],
		format:SignatureFormat.PAdES,
		level:SignatureLevel.LT,
		steps:[
			{
				process:signatureTag,
				steps:[actor1!, actor2!],
        signatureType: SignatureType.Envelopped,
			},
		],
    "user-data": {},
    "manifest-data": {}
	}

  let scenario1 = await api.addScenario(session1!,scenario1Body);
  api.check($length(scenario1)>0, `ADD SCENARIO TO A SESSION : ${scenario1}`);
  $logterm("\n <<<<<<< END SIG-5   CREATE A SCENARIO FOR AN ORDERED COSIGN PROCESS ");

  $logterm("\n >>>>>> START SIG-6 ACTIVATE A SCENARIO ",scenario1);
  const activated = await api.activateScenario(scenario1!, {
		'manifest-data':{
			'activation-reference': $uuid()
		}
	}) ;

	api.check(activated, 'checking activating scenario1') ;
  $logterm("\n <<<<<<< END SIG-6  SCENARIO ACTIVATION ");
  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
  $logterm(`\n >>>>>> START SIG-22 Documents to sign listing`);
  const res = await api.taggedDocuments(session1!, {actor:$url2lid(actor1)}) ;
	api.check($ok(res), `checking documents return for '${$inspect(res)}' approval`) ;
	const legalDocs = (<StringArrayDictionary>res)[signatureTag] ;
	api.check($count(legalDocs) === 1, `checking documents count return for '${signatureTag}' approval`) ;
	$logterm(`${signatureTag} documents = ${$inspect(legalDocs)}`) ;
  $logterm(`\n >>>>>> END SIG-22 Documents to sign listing`);
 $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

  $logterm("\n>>>>>>> START  SIG-18 OTP CREATION");
  $logterm("\n GENERATE A TOKEN FOR THE FIRST SIGNATORY");
  const otp1 = await api.generateOTP(session1!,{
    actor:actor1!,
    documents:[document1!],
    length:6,
    ttl:86400,
    numeric:true,
    tag: signatureTag
  })
  api.check($length(otp1?.otp)===6,`CREATE OTP for actor 1 : ${$inspect(otp1)}`);
  $logterm("\n GENERATE A TOKEN FOR THE SECOND SIGNATORY");
  const otp2 = await api.generateOTP(session1!,{
    actor:actor2!,
    documents:[document1!],
    length:6,
    ttl:86400,
    numeric:true,
    tag: signatureTag
  })
  api.check($length(otp2?.otp)===6,`CREATE OTP for actor 2 : ${$inspect(otp2)} ${$inspect(otp1)}`);
  $logterm("\n<<<<<<< END  SIG-18 OTP CREATION");
  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;


  $logterm("\n>>>>>>> START SIG-11 : OTP VALIDATION");
  $logterm("\n>>>>>> VERIFY OTP FOR ACTOR 1");
  const checkOtp1 = await api.checkOTP(session1!,{
    otp:otp1!.otp,
    delete:false,
    actor:actor1!,
    documents:[document1!],
    tag:signatureTag
  })
  api.check(checkOtp1,`VERIFY  OTP FOR FIRST ACTOR  `);
  $logterm("\n>>>>>> VERIFY OTP FOR ACTOR 2");
  const checkOtp2 = await api.checkOTP(session1!,{
    otp:otp2!.otp,
    delete:false,
    actor:actor2!,
    documents:[document1!],
    tag:signatureTag
  })
  api.check(checkOtp2,`VERIFY  OTP FOR SENCOND ACTOR  `);
  $logterm("\n<<<<<<< END SIG-11 : OTP VALIDATION");

  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
  $logterm("\n >>>>> START SIG-7 DOCUMENT VALIDATION");
  const approvation = await api.approveDocuments(session1!,{
    actor:actor1!,
    documents:[document1!],
    otp: <string>otp1?.otp,
    tag:signatureTag
  });
  api.check($ok(approvation),`Approved documents. ${$inspect(approvation)}`);
  $logterm('-------------------------------------------------') ;
	$logterm(`Done approval by: ${actor2}:\n${$inspect(approvation?.signatures)}`) ;
	$logterm('-------------------------------------------------') ;
  $logterm("\n >>>>> END SIG-7 DOCUMENT VALIDATION");
  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
  $logterm("\n >>>>>> START SIG 9 Generate a signature certificate ")

  $logterm("/n >>>>> START SIG-10 CA CGU URL and Token Creation");

  await api.currentAuthority();
  const cgu = await api.getCGU(<string>api.ca, session1!, actor2!) ;
  api.check($ok(cgu) && $length(cgu?.token) === 36, `checking authority '${<string>api.ca}' CGUs for '${session1}' and '${actor2}'`) ;
  $logterm("Get CGU:", $inspect(cgu)) ;
  $logterm("\n >>>>> END SIG-10 CA CGU URL and Token Creation");
  const token = cgu?.token as string ;
  $logterm(`Generate certificate for actor '${actor2}'`) ;

  const certret = await api.createCertificate(session1!, {
    actor: actor2!,
    authority: <string>api.ca,
    token: token,
    ttl: 1200
  }) ;

  api.check($length(certret)>0,`CERTIFICATE : ${certret}`);


  $logterm("\n >>>>>> END SIG 9 Generate a signature certificate ");
  $logterm("\n >>>>>> START SIG-21 Actors with documents to sign listing");
  $logterm("\n >>>>>> Actors with documents to sign listing");
  let actors = await api.taggedActors(session1!,[signatureTag]);
  api.check($ok(actors),`Actors ${$inspect(actors)}`);
  $logterm("\n >>>>>> END SIG-21 Actors with documents to sign listing");

  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
  $logterm('\n >>>>> START SIG-8 Document Signature');
  /**
   *  This method consist of serveral steps
   * 1). Get documents available for signature for actor
   * 2). GET CGU,
   * 3). Generate certificate for actor
   * 4). Sign Documents available for actor
   * */
  await api.signAvailableDocumentsForActor(session1!,actor2!,signatureTag,1);


  $logterm('\n >>>>>> END SIG-8 Document Signature');

  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

  $logterm("\n >>>>>> START SIG-12 Signed document download");
  await api.getAndSaveDocument(document1!,`singed_file.pdf`);
  $logterm("\n <<<<<< END SIG-12 Signed document download");
  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
  $logterm("\n <<<<<<< START SIG-24 I retrieve actor information");

  const aid1= await api.getActor(actor1!);
  api.check($ok(aid1),`  INFORMATION FIRST ACTOR : ${$inspect(aid1)}`);

  $logterm("\n <<<<<<< END SIG-24 I retrieve actor information");
  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;

  $logterm("\n >>>>>> START SIG-20 List of sessions with at least one active scenario");

  $logterm("\n >>>>> List sessions with at least one active scenario <<<<<<<<");
  let list = await api.sessionsList({status_mask:3});
  api.check($count(list)>=1,`There is one session with an active scenario. ${list}`);
  $logterm("\n >>>>>> END SIG-20 List of sessions with at least one active scenario");
  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;






  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
  const documents = await api.documentsList(session1!);
  api.check($count(documents)==1,`Documents: ${$inspect(documents)}`)
  $logterm(`\n >>>>>> START SIG-25 Document description retrieval`);
  const document = await api.getDocument(documents[0]);
  api.check($ok(document),`Document Description ${$inspect(document)}`);
  $logterm(`\n >>>>>> END SIG-25 Document description retrieval`);

  $logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
  $logterm(`\n >>>>> START SIG-26 Document link retrieval`);
  const link = await api.getCurrentDocument(documents[0]);// document url e.g  /v1/session/123/document/001
  api.check($length(link)>0, `Link download document: ${link}`);
  $logterm(`\n >>>>> END SIG-26 Document link retrieval`);


  $logterm("\n START SIG-27 Certification authorities listing ");
  const authorities = await api.certificationAuthoritiesList() ;
  api.check($count(authorities)>0,`certification-authorities: ${$inspect(authorities)}`);
  $logterm("\n END SIG-27 Certification authorities listing ");

	$logterm('\n>>>> Will close session') ;
	let close = await api.closeSession(session1!, {reason:'end of our work', force:false})
	api.check($ok(close), 'closing of session1') ;

	let sesret = await api.getSession(session1!) ;
	api.check($ok(sesret), `checking session '${session1}' content after closing`) ;
	$logterm('-------------------------------------------------') ;
	$logterm(`Closed Session content:\n${$inspect(sesret)}`) ;
	$logterm('-------------------------------------------------') ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
  $logterm("\n START SIG-29 Manifest of proof generation ");
  $logterm(">>>>>>>>> GENERATE AND DOWNLOAD MANIFEST >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") ;
	await api.recoverManifest(session1!) ;
	$logterm("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<") ;
  $logterm("\n END SIG-29 Manifest of proof generation ");

  $exit('Test concluded with no errors', 0)
}

start();



