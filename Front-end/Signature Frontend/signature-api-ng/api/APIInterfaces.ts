import {Nullable, ObjectDictionary, StringDictionary} from '../utils/types'

import {
	UserRole,
	AcceptedLanguages,
	APIRole,
	ActorType,
	CertificateStatus,
	SessionStatus,
	DocumentStatus,
	ScenarioStatus,
	SignatureType,
	SigningProcess
} from './APIConstants'
import { APICountry } from './APICountries'
import { ActorIDs, GlobalID, LocalID } from './APIIDs'

export interface APIHeaders {
	defaultlanguage?: AcceptedLanguages ;
	certignarole: UserRole ;
	certignahash: string ;
	certignauser: string ;
}


export interface APIAuth {
	user:string ;
	role:UserRole ;
	password:string ;
	apiRole:APIRole ;
	language:AcceptedLanguages ;
}


export interface APIFileToken {
	name:string ;
	size:number ;
	date:string ;
	hash:string ;
	user:string ;
} ;


export interface APIGetListQuery
{
	ttlmin?:number ;
	ttlmax?:number ;
	dynttlmin?:number ;
	dynttlmax?:number ;
	userids?:string[] | string ;
	expirationstatus?: 'all' | 'expired' | 'valid' ;
}



export type ManifestData = Nullable<object>

export type UserData	 = Nullable<object>

export interface ManifestDataBody
{
	'manifest-data'?: ManifestData | null;
}

export interface UserDataBody extends ManifestDataBody
{
	'user-data'?: UserData | null;
}



// =========== request interfaces bodies =============================
export interface CreateSessionBody extends UserDataBody
{
	ttl: number ;
}


export interface CreateDocumentBody extends UserDataBody
{
	abstract?: string ;
	'file-name': string ;
	title: string ;
	upload: string ;
}


export interface CreateActorBody extends UserDataBody
{
	'adm-id'?: string ;
	country: APICountry ;
	email: string ; // email is mandatory because we need it to generate certificate
	'first-name'?: string | null;
	login?: string ;
	mobile?: string | null;
	name: string ;
	roles:string[] | string ;
	type:ActorType ;
}




export interface StepNode {
	process:string ;
	steps: string[] ; // in the first version of the interface, there's only first level steps with actors' URLs
	signatureType?:SignatureType ; // useless for approval steps
	orderApproval?: boolean; // use for only approval step
	cardinality?:Nullable<number|'all'|'one'> ;
}

export interface ScenarioBody extends UserDataBody
{
	documents: string[] ;
	format:number ;
	level:number ;
	steps:StepNode[] ;
}


export interface CertificateFileNode {
	filename:string ;
	url:string ;
}

export interface CreateCertificateBody
{
	actor: string ;
	authority: string ;
	token: string ;
	ttl?: Nullable<number> ;
	'supporting-documents'?:Nullable<CertificateFileNode[]> ;
}


export interface SessionsQuery extends APIGetListQuery
{
	'status_mask'?: string|number  ;
}


export interface DocumentsQuery
{
	actor?:string|LocalID ;
	tags?:string[] | string ;
	'status_mask'?: string|number ;
}


export interface ActorsQuery	{ tags?:string[] | string ; }

export interface CertificatesQuery extends APIGetListQuery
{
	caid?:GlobalID | string ;
	actorIds?: string[] | string | ActorIDs | LocalID ;
}


export interface SessionOTPBody {
	actor:string ;
	documents:string[] ;
	length:number ;
	numeric:boolean ;
	ttl?:Nullable<number> ;
	tag?:Nullable<string> ;
}


export interface SessionCheckOTPBody {
	actor?:string ;
	documents?:string[] ;
	otp:string ;
	tag?:string ;
	delete?:boolean ;
}

export interface SessionRecipientBody extends SessionApproveDocumentsBody {
}

export interface SessionRefuseBody extends SessionApproveDocumentsBody {
	reason?: string;
}

export interface SessionApproveDocumentsBody extends ManifestDataBody {
	actor:string ;
	documents:string[] ;
	otp:string ;
	tag?:string ;
}


export enum SigningTextAlignment {
	LeftAlignment = 0,
	CenterAlignment = 1,
	RightAlignment = 2
}

export interface SigningVisualParameters {
	'font-size': number ;
	height:number ;
	'page-number':number ;
	'text-align':SigningTextAlignment ;
	'image-content'?:string ;
	text?:string ;
	width:number ;
	x:number ;
	y:number ;
}

export interface SessionSignDocumentNode {
	base64?: string;
	'document-url': string;
	'visual-parameters'?: SigningVisualParameters;
}

export interface SessionDirectSignDocumentsBody extends ManifestDataBody {
	certificate:string ;
	actor:string ;
	documents:string[] | SessionSignDocumentNode[] ;
	otp?:string ;
	tag:string ;
	signMode?: "server"|"generate";
}

export interface SessionSignDocumentsBody extends SessionDirectSignDocumentsBody {
	certificate:string ;
}

export interface SessionClosureBody extends ManifestDataBody
{
	reason:string ;
	force:boolean ;
}



export interface SessionExtendBody
{
	ttl: number ;
}

export interface ScenarioSplitBody extends UserDataBody
{
	reason:string ;
}


export interface ScenarioCancelBody extends ManifestDataBody
{
	reason:string ;
}



// =========== request returned resources =============================

export interface DeletedURLResource {
	deleted:string ;
}
export interface URLResource {
	url:string ;
	date:string ;
}

export interface ExpiringURLResource {
	url:string ;
	date:string ;
	expires:string ;
}

export interface SessionResource extends CreateSessionBody {
	date:string ;
	expires:string ;
	id:GlobalID ;
	status:SessionStatus | null;

	actors?:string[] ;
	documents?:string[] ;
	scenarios?:string[] ;
}
export interface ClosingSessionResource {
	status:SessionStatus | null;
	url?:string ;
	date?:string ;
	expires?:string ;
}

export interface ActorResource extends CreateActorBody {
	aid:LocalID ;
	date:string ;
	id:GlobalID ;
}
export interface DocumentResource extends UserDataBody {
	date:string ;
	did:LocalID ;
	'file-name': string ;
	id:GlobalID ;
	status:DocumentStatus ;
	title: string ;
	abstract?: string | null;
}

export interface ScenarioResource extends ScenarioBody {
	sid:LocalID ;
	date:string ;
	id:GlobalID ;
	status:ScenarioStatus ;
}

export interface CertificateResource {
	actor:string ;
	authority:string ;
	date:string ;
	expires:string ;
	session:string ;
	SN:string ;
	status:CertificateStatus ;
	ttl:number ;
}

export interface AuthorityResource {
	caid: number | bigint ;
	'cgu-version':string ;
	'long-name':string ;
	name:string ;
}

export interface OTPResource {
	date:string ;
	expires:string ;
	otp:string ;
}

// TODO: a CheckOTPResource ?

export interface SignatureResource {
	tag:string ;
	signatureId:string ;
	actor:string ;
	document:string ;
}

export type ProcessType = 'recipient' | 'sign' | 'approve' | 'refuse';

export interface StepProcessingData{
	processType: ProcessType;
	manifestDefault: ObjectDictionary;
	defaultTag: SigningProcess;
}

export interface RecipientResource extends SignatureResource {
}

export interface DocumentDownload{
	url:string ;
	date: string;
	expires: string|null ;
}

export interface RecipientResponse extends Omit<SigningResource, "signatures">
{
	signatures: RecipientResource[] ;
}

export interface SigningResource
{
	signatures:SignatureResource[] ;
	threadId:string ;
	otp?:string ;
	token?:string ;
}

export interface CGUResource {
	actor:string ;
	authority:string ;
	'download-url':string ;
	session:string ;
	token:string ;
	version:string ;
}

export interface AcceptedUploadsQuery
{
	type?: 'all' | 'signing' ;
}



export interface AcceptedUploads {
	'accepted-extensions':StringDictionary
}

