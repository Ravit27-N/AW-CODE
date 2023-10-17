import { $unsigned } from "../utils/commons";
import { Languages, NumberDictionary, StringDictionary, uint } from "../utils/types";

export const validityState = {
	invalid: 0,
	valid: 1
}

export enum AcceptedLanguages {
	FR = Languages.fr,
	EN = Languages.en
}


export enum ActorType {
	Person = 0,
	Entity = 1
}

export enum AuthHeaders {
	Id = 'certignauser',
	Role = 'certignarole',
	Password = 'certignahash',
	Language = 'defaultlanguage'
}

export enum AuthType {
	Refused = 0,
	Authorized = 7
	// 7 is meant for 1 + 2 + 4 + 8 e.g. Listing, Aproving and Signing.
	// We can use that number for later masking check
}

export enum CertificateStatus {
	Expired = validityState.invalid,
	Valid   = validityState.valid
}

export enum DocumentStatus {
    Genuine = 1,
    Approbation = 2,
    Approved = 3,
	Signing = 4,
	Signed = 5
}


export enum ScenarioStatus {
	UnderConstruction = 1,
	UnderConstructionAfterSplit = 2,
	ActiveScenario = 4,
	WellTerminated = 10,
	WellExpiredAfterSplit = 11, /* NOT USED */
	Deleted = 20, /* NOT USED */
	Canceled = 21,
	Expired = 22 /* NOT USED */
}

export enum SessionStatus {
    Genuine = 1,
    UnderConstruction = 2,
	Idle = 3,
	Active = 4,
	WellTerminated = 10,
	Deleted = 20,
	Canceled = 21,
	WellTerminatedArchived = 30,
	WrongTerminatedArchived = 40
}

export enum SessionStatusTag {
    Genuine = "Genuine",
    UnderConstruction = "UnderConstruction",
	Idle = "Idle",
	Active = "Active",
	WellTerminated = "WellTerminated",
	Deleted = "Deleted",
	Canceled = "Canceled",
	WellTerminatedArchived = "WellTerminatedArchived",
	WrongTerminatedArchived = "WrongTerminatedArchived"
}

export function sessionStatus2Tag(s:SessionStatus):SessionStatusTag {
    const SessionStatusToTag:Array<SessionStatusTag|null> = [
        null,
        SessionStatusTag.Genuine,
        SessionStatusTag.UnderConstruction,
        SessionStatusTag.Idle,
        SessionStatusTag.Active,
        SessionStatusTag.WellTerminated,
        SessionStatusTag.Deleted,
        SessionStatusTag.Canceled,
        SessionStatusTag.WellTerminatedArchived,
        SessionStatusTag.WrongTerminatedArchived,
    ] ;
    return SessionStatusToTag[Math.min(SessionStatus.WrongTerminatedArchived, $unsigned(s))]! ;
}

export enum SigningProcess {
	Approval = 'approval',              // n person chosen in a group of p approve the same document
	Sign = 'sign',
	Cosign = 'cosign',                  // n person chosen in a group of p sign the same document
	Countersign = 'countersign',        // all group of p person sign the same document one after an other
	OrderedCosign = 'ordered-cosign',   // idem but the signature itself is not a counter-signature (may result in different signature kind)
	IndividualSign = 'individual-sign', // n person chosen in a group of p each sign their own version of the document
	To = 'to',
	Cc = 'cc',
	Refuse = 'refuse',
	Receive = 'receive',
	Viewer = 'viewer'
}


export enum SignatureFormat {
	PAdES = 1,
	XAdES = 2,
	CAdES = 3
}

export enum SignatureFormatTag {
	PAdES = 'PAdES',
	XAdES = 'XAdES',
	CAdES = 'CAdES'
}

export function signatureFormat2Tag(l:SignatureFormat):SignatureFormatTag {
    const SignatureFormatToTag:Array<SignatureFormatTag|null> = [
        null,
        SignatureFormatTag.PAdES,
        SignatureFormatTag.XAdES,
        SignatureFormatTag.CAdES
    ]
    return SignatureFormatToTag[Math.min(SignatureFormat.CAdES, $unsigned(l))]! ;

}

export enum SignatureLevel {
	B = 1,
	T = 2,
	LT = 3,
	LTA = 4
}


export enum SignatureLevelTag {
	B = 'B',
	T = 'T',
	LT = 'LT',
	LTA = 'LTA'
}

export function signatureLevel2Tag(l:SignatureLevel):SignatureLevelTag
{
    const SignatureLevelToTag:Array<SignatureLevelTag|null> = [
        null,
        SignatureLevelTag.B,
        SignatureLevelTag.T,
        SignatureLevelTag.LT,
        SignatureLevelTag.LTA
    ]
    return SignatureLevelToTag[Math.min(SignatureLevel.LTA, $unsigned(l, SignatureLevel.B as uint))]! ;
}

export enum RoleType {
	Approval,
	Signature,
	Expedition
}

export enum SignatureType {
	Envelopped = 1,
	Envelopping = 2,
	Detached = 3
}

export enum SignatureTypeTag {
	Envelopped = 'Envelopped',
	Envelopping = 'Envelopping',
	Detached = 'Detached'
}

export function signatureType2Tag(l:SignatureType):SignatureTypeTag
{
    const SignatureTypeToTag:Array<SignatureTypeTag|null> = [
        null,
        SignatureTypeTag.Envelopped,
        SignatureTypeTag.Envelopping,
        SignatureTypeTag.Detached
    ]
    return SignatureTypeToTag[Math.min(SignatureType.Detached, $unsigned(l, SignatureType.Envelopped as uint))]! ;
}


/*
	We decided that PAdES is an envelopped signature
*/

export enum UserRole {
	Request = 1,
	Action = 2,
	Maintenance = 3,
	System = 4
}

export enum APIRole {
	Listing = 0,
	Reading,
	Creation,
	Update,
	Deletion,
	Signature,
	Maintenance
}

export const APIRoleNames = [
	'listing',
	'reading',
	'creation',
	'update',
	'deletion',
	'signature'
] ;

export enum APIFileType {
	PDF = 0,
	XML = 1,
	PNG = 2,
	JPEG = 3,
	ZIP = 4
}

export const APIMimeTypes:NumberDictionary = {
	"application/pdf": APIFileType.PDF,
	"application/xml": APIFileType.XML,
	"image/jpeg": APIFileType.JPEG,
	"image/jpg": APIFileType.JPEG,
	"image/png": APIFileType.PNG,
} ;

export const APIRawMimeTypes:string[] = [
	"application/pdf",
	"application/xml",
	"image/jpeg",
	"image/jpg",
	"image/png",
	"application/octet-stream"
] ;

export const APIExtensions:StringDictionary = {
	'pdf':	"application/pdf",
	'xml':	"application/xml",
	'jpeg':	"image/jpeg",
	'jpg':	"image/jpg",
	'png':	"image/png"
}


export type MimeFileInfosDescriptor = {extensions:string[], mimeType:string, name:string}[] ;
export const APIFileInfos:MimeFileInfosDescriptor = [
	{
		extensions: ["pdf"],
		mimeType: "application/pdf",
		name: "PDF"
	},
	{
		extensions: ["xml"],
		mimeType: "application/xml",
		name: "XML"
	},
	{
		extensions: ["png"],
		mimeType: "image/png",
		name: "PNG"
	},
	{
		extensions: ["jpg", "jpeg"],
		mimeType: "image/jpeg",
		name: "JPEG"
	}
] ;
