
import {Prisma} from "@prisma/client";
import { APICountry } from "../../api/APICountries";
import { DocumentIDs, GlobalID, LocalID } from "../../api/APIIDs";
import { AuthType, RoleType, SignatureFormat, SignatureLevel, SignatureType, APIRole, UserRole } from "../../api/APIConstants";
import { ManifestData, UserData, StepNode } from "../../api/APIInterfaces";
import { Automat } from "../../api/automat/automat";

export interface PrismaContext<I = object>{
	trx?: Prisma.TransactionClient;
	include?: I ;
}
export type RelativeIdentifier = string | number| bigint
export interface ActorInterface {
	sessionId:number|bigint ;
	country: APICountry ;
	email: string ;
	name: string ;
	publicId: LocalID ;
	rolesArray: string[] ;
	type: number ;

	administrativeCode?: string | undefined ;
	firstName?: string | undefined  ;
	login?: string | undefined ;
	manifestData?: ManifestData ;
	mobile?: string | undefined ;
	userData?: UserData ;
	authType:AuthType;
}
export interface FileInterface {
	fileType:number ;
	path:string ;
	size:number ;
	user:string ;

	hash?: string | undefined ;
	sealPath?: string | undefined ;
}

export interface FileRefInterface extends FileInterface
{
	fileName: string ;
	status:number ;
	timestamped_at:string ;

	uploaded_at?:string | undefined
}

export interface LastFile {
	aid?:number ;               // if set it means the file is devoted to a specific actor
	fileId:number ;             // the internal identifier (database primary key) of a FileRef object
	signatureFileId?:number ;   //
}

export type LastFilesDictionary = { [key:string] : LastFile[] }
export interface ScenarioOtherData {
	dids:number[] ;                             // list of all document ids used in the scenario
	aids:number[] ;                             // list of all actor ids implicated in the scenario
	documentURLs:string[] ;                     // same list of all documents as URLs (kind of dupplicates of dids)
	automat:Automat ;
	originalLastFiles?:LastFilesDictionary ;    // files used at the beginning of the scenario
	sourceFiles?:LastFilesDictionary ;          // files used for the signature / approbation. At the beginning of the scenario sourceFiles = originalLastFiles
	generatedFiles?:LastFilesDictionary ;       //
}

export interface ScenarioUpdateInterface {
	otherData:ScenarioOtherData ;
	signatureFormat: number ;
	signatureLevel: number ;
	stepsDefinition: StepNode[] ;
	status: number ;
	manifestData?: ManifestData ;
	userData?: UserData ;
}

export interface ScenarioInterface extends ScenarioUpdateInterface {
	rank: number ;
	sessionId:number | bigint;
	publicId: LocalID;
}

export interface SignedDocument {
	tag:string ;	    // process
	date:string ;	    // event date
	dsigid:string ;		// document signature ID
	sigid:string ; 	 	// file signature ID (can be the same as dsigid)
	threadid:string ;   // thread id 
	did:number ;		// document ID
	aid:number ;		// actor ID
	roleType:RoleType ;
	requesId?:string ;  // direct from the server
	cid?:number ;	    // certificate ID
	format?:SignatureFormat ;
	level?:SignatureLevel ;
	type?:SignatureType ;
	token?:string ;     // CA Token for signature
	otp?:string ;	    // signature otp, can be identical on several signatures
	o_fileid?:number ;
	c_fileid?:number ;
	d_fileid?:number ;
}

export enum SessionContextEventType {
	Closure = 1,
	CreateScenario = 2,
	ActivateScenario = 3,
	SplitScenario = 4,
	GenerateOTP = 5,
	CheckOTP = 6,
	GenerateCertificate = 7,
	ApproveDocuments = 8,
	SignDocuments = 9,
	CancelScenario = 10,
	RefuseDocuments = 11,
	ReceiveDocuments = 12
}

export interface SessionContextEvent {
	user:string ;
	'event-type':SessionContextEventType ;
	date:string ;
	'scenario-id'?:LocalID ;
	reason?:string ;
	'actor-id'?:LocalID ;
	'document-ids'?:DocumentIDs ;
	'operation-id'?:string ;
	'manifest-data'?:ManifestData ;
	token?:string ;
	'certificate-id'?:GlobalID ;
	tag?:string ;
}

export interface SessionOtherData {
	// signatures and sessionContextEvents will be used for the manifest generation
	// they are 2 kinds of events
	signatures?:SignedDocument[] ;
	sessionContextEvents:SessionContextEvent[] ;
} ;

export interface SessionInterface {
	publicId: GlobalID ;
	status: number ;
	ttl:number ;
	user: string ;

	manifestData?: ManifestData ;
	otherData?: SessionOtherData | undefined ;
	userData?: UserData ;
}

export interface DocumentInterface {
	sessionId: number
	fileName: string ;
	publicId: LocalID ;
	title: string  ;

	abstract?: string | undefined ;
	otherData?: DocumentOtherData | undefined ;
	manifestData?: ManifestData ;
	userData?: UserData ;
} ;

export interface DocumentOtherData {
	lastFiles : LastFile[] ;
}

export interface TokenOtherData {
	dids?: LocalID[] ;
	tag?: string ;
} ;

export interface UploadInterface extends FileInterface {
	uploaded_at: string ;
	publicId: GlobalID ;
	ttl:number ;
} ;

export interface RequestInterface {
	apiRole:APIRole ;
	date:string ; 
	requestId:string ;
	requestMethod:string ;
	requestUrl:string ;
	role:UserRole ;
	status: number ;
	user:string ;

	duration?:number ; 
	error?: string ; 
	ip?:string ; 
	reply?: object ;
	request?: object ;
}

export interface JWTAuthData {
	jwtA:string,
	jwtB:string
}

export interface UserJWTInterface {
	user:string ;
	authData: JWTAuthData ;
}

