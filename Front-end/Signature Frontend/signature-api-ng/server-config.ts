import { ObjectDictionary, StringDictionary } from './utils/types';
import { $isstring, $isobject, $length } from './utils/commons';
import { $trim } from './utils/strings';

import { MAX_POSITIVE_DISTANCE } from './utils/commons';

import { JSONObject } from 'ts-json-object'
import { ManifestOptions } from './classes/Manifest'
import {
	DEFAULT_REQUEST_TIMEOUT,
	DEFAULT_CERTIFICATE_GENERATION_TIMEOUT,
	DEFAULT_SIGNATURE_TIMEOUT,
	MIN_CERTIFICATE_TTL,
	MAX_CERTIFICATE_TTL
} from './classes/CertignaEndPoint'

const _constructManifestData = (_:Config, key:string, value:ObjectDictionary) : ObjectDictionary => {
	let ret:ObjectDictionary = {}
	for (let k in value) {
		if (!$length(k)) {
			throw new TypeError(`Config.${key} - empty key`) ;
		}

		if ($trim(k) !== k) {
			throw new TypeError(`Config.${key} - invalid key '${k}'`) ;
		}
		let definition = value[k] ;
		if (!$isobject(definition)) {
			throw new TypeError(`Config.${key} - bad value type for key '${k}'`) ;
		}

		let def:StringDictionary = {}
		for (let l in <StringDictionary>definition) {
			/*
				WARNING : this piece of code should evolve if we recognize more languages
			 */
			let min_l = l.toLowerCase() ;
			if (min_l !== 'fr' && min_l !== 'en') {
				throw new TypeError(`Config.${key} - bad language '${l}' for key '${k}'`) ;
			}
			let title = (<StringDictionary>definition)[l] ;
			if (!$isstring(title) || !$length(title = $trim(title))) {
				throw new TypeError(`Config.${key} - unspecified title for language '${l}' for key '${k}'`) ;
			}

			def[min_l] = title ;
		}
		ret[k] = def ;
	}
	return ret ;
} ;

export class SignServerLogin extends JSONObject
{
	@JSONObject.optional(`pps`)
	declare login: string

	@JSONObject.optional("ySsP")
	declare password:string
}


export class DBConnection extends JSONObject {
	@JSONObject.required
	declare host: string
	@JSONObject.required
	declare user: string
	@JSONObject.required
	declare port:number
	@JSONObject.required
	declare password: string
	@JSONObject.required
	declare database: string
}

export class CADefinition extends JSONObject {
	@JSONObject.required
	declare aki:string

	@JSONObject.required
	declare uuid: string

	@JSONObject.required
	declare name: string

	@JSONObject.map('cgu-version')
	@JSONObject.optional
	declare cguVersion?: string

	@JSONObject.map('long-name')
	@JSONObject.optional
	declare longName?: string

	@JSONObject.map('cgu-path')
	@JSONObject.optional
	declare cguLocalPath?:string
}

export class Config extends JSONObject {

	/* whereas the api accepts to force session closure even if they are active */
	@JSONObject.map('accept-forced-closure')
	@JSONObject.optional(false)
	declare acceptsForcedClosure:boolean

	/* content of the manifest data for scenario activation action */
	@JSONObject.map('activate-manifest-data')
	@JSONObject.optional({})
	@JSONObject.custom(_constructManifestData)
	declare activateManifestData:ObjectDictionary

	/* content of the manifest data for any actor */
	@JSONObject.map('actor-manifest-data')
	@JSONObject.optional({})
	@JSONObject.custom(_constructManifestData)
	declare actorManifestData:ObjectDictionary

	/* supplemental documents' approval categories */
	@JSONObject.map('document-approval-categories')
	@JSONObject.custom(_constructManifestData)
	@JSONObject.optional({})
	declare approvalRoles:ObjectDictionary ;

	/* content of the manifest data for any document's approbation action */
	@JSONObject.map('approve-manifest-data')
	@JSONObject.optional({})
	@JSONObject.custom(_constructManifestData)
	declare approveManifestData:ObjectDictionary

	/* content of the manifest data for any scenario's canceling action */
	@JSONObject.map('cancel-manifest-data')
	@JSONObject.optional({})
	@JSONObject.custom(_constructManifestData)
	declare cancelManifestData:ObjectDictionary

	/* content of the manifest data for any session's closure action */
	@JSONObject.map('closure-manifest-data')
	@JSONObject.optional({})
	@JSONObject.custom(_constructManifestData)
	declare closureManifestData:ObjectDictionary

	/* otp ttl value */
	@JSONObject.map('certificate-ttl')
	@JSONObject.optional((MIN_CERTIFICATE_TTL+MAX_CERTIFICATE_TTL)/2)
	@JSONObject.integer
	@JSONObject.gte(MIN_CERTIFICATE_TTL)
	@JSONObject.lte(MAX_CERTIFICATE_TTL)
	declare certificateTtl:number

	@JSONObject.map('certification-authorities')
	@JSONObject.optional
	@JSONObject.array(CADefinition)
	declare certificationAuthorities:Array<CADefinition>

	/* standard external signature request timeout in ms */
	@JSONObject.map('certificate-requests-timeout')
	@JSONObject.optional(DEFAULT_CERTIFICATE_GENERATION_TIMEOUT)
	@JSONObject.integer
	@JSONObject.gte(10)
	declare certificateRequestsTimeout:number

	/* parameters of the database connection */
	@JSONObject.map('database-connection')
	@JSONObject.optional
	declare dbConnection:DBConnection

	/* content of the manifest data for any document */
	@JSONObject.map('document-manifest-data')
	@JSONObject.optional({})
	@JSONObject.custom(_constructManifestData)
	declare documentManifestData:ObjectDictionary

	/* downloads files path */
	@JSONObject.map('downloads-path')
	@JSONObject.optional("")
	declare downloadsPath:string

	/* downloads ttl value */
	@JSONObject.map('download-ttl')
	@JSONObject.optional(900)
	@JSONObject.integer
	@JSONObject.gte(60)
	declare downloadTtl:number

	/* standard external request timeout in ms */
	@JSONObject.map('external-requests-timeout')
	@JSONObject.optional(DEFAULT_REQUEST_TIMEOUT)
	@JSONObject.integer
	@JSONObject.gte(10)
	declare externalRequestsTimeout:number


	/* whereas if the the api is in production or developpement/testing mode*/
	@JSONObject.map('production-environment')
	@JSONObject.optional(false)
	declare isProduction:boolean

	/*
		WARNING : this definition should evolve if we recognize more languages
	*/
	@JSONObject.map('default-language')
	@JSONObject.union(['en' , 'fr'])
	@JSONObject.optional('en')
	declare language:'en' | 'fr'

	@JSONObject.map('log-level')
	@JSONObject.union(['off' , 'fatal', 'error', 'warn', 'info', 'debug', 'trace', 'all'])
	@JSONObject.optional('debug')
	declare logLevel:'off' | 'fatal' | 'error' | 'warn' | 'info' | 'debug' | 'trace' | 'all'

	/* whereas if the returned URLS includes the versionning or not */
	@JSONObject.map('long-identifiers')
	@JSONObject.optional(true)
	declare longIdentifiers:boolean

	/* parameters of the manifest generation */
	@JSONObject.map('manifest-generation-options')
	@JSONObject.optional
	declare manifestOptions:ManifestOptions

	/* stored files directory path */
	@JSONObject.map('manifest-template-path')
	@JSONObject.optional("")
	declare manifestTemplatePath:string

	/* whereas if the closure of session generates the manifest or not */
	@JSONObject.map('manifest-on-closure')
	@JSONObject.optional(false)
	declare manifestOnClosure:boolean

	/* otp ttl value */
	@JSONObject.map('otp-ttl')
	@JSONObject.optional(300)
	@JSONObject.integer
	@JSONObject.gte(60)
	declare otpTtl:number

	/* API PORT : default is 8008*/
	@JSONObject.optional(8008)
	@JSONObject.integer
	@JSONObject.gte(80)
	@JSONObject.lte(65535)
	declare port:number

	/* request body size in megabytes */
	@JSONObject.map('request-body-size')
	@JSONObject.optional(10)
	@JSONObject.integer
	@JSONObject.gte(1)
	declare requestBodySize:number

	/* content of the manifest data for any scenario */
	@JSONObject.map('scenario-manifest-data')
	@JSONObject.optional({})
	@JSONObject.custom(_constructManifestData)
	declare scenarioManifestData:ObjectDictionary

	/* content of the manifest data for any session */
	@JSONObject.map('session-manifest-data')
	@JSONObject.optional({})
	@JSONObject.custom(_constructManifestData)
	declare sessionManifestData:ObjectDictionary

	/* login to signing server */
	@JSONObject.map('sign-server-login')
	@JSONObject.optional
	declare signServerLogin:SignServerLogin

	/* content of the manifest data for any document's signature action */
	@JSONObject.map('signature-manifest-data')
	@JSONObject.optional({})
	@JSONObject.custom(_constructManifestData)
	declare signatureManifestData:ObjectDictionary

	/* standard external signature request timeout in ms */
	@JSONObject.map('signature-requests-timeout')
	@JSONObject.optional(DEFAULT_SIGNATURE_TIMEOUT)
	@JSONObject.integer
	@JSONObject.gte(10)
	declare signatureRequestsTimeout:number

	/* stored files directory path */
	@JSONObject.map('storage-path')
	@JSONObject.optional("")
	declare storagePath:string


	/* minimal session ttl value */
	@JSONObject.map('ttl-min')
	@JSONObject.optional(MIN_CERTIFICATE_TTL)
	@JSONObject.integer
	@JSONObject.gte(MIN_CERTIFICATE_TTL)
	declare ttlMin:number

	/* maximal session ttl value */
	@JSONObject.map('ttl-max')
	@JSONObject.optional(MAX_POSITIVE_DISTANCE)
	@JSONObject.integer
	@JSONObject.gte(MIN_CERTIFICATE_TTL)
	declare ttlMax:number

	/* upload files path */
	@JSONObject.map('uploads-path')
	@JSONObject.optional("")
	declare uploadsPath:string

	/* log proof files path */
	@JSONObject.map('logs-file-path')
	@JSONObject.optional("")
	declare logsFilePath:string

	/* log files path */
	@JSONObject.map('logs-path')
	@JSONObject.optional("")
	declare logsPath:string

	/* max size per a log file(number of kilobytes)*/
	@JSONObject.map('log-file-size-max')
	@JSONObject.optional(1024)
	@JSONObject.integer
	@JSONObject.gte(50)
	declare logFileSizeMax:number

	/* max keep files (number in days) */
	@JSONObject.map('keep-logs-max')
	@JSONObject.optional(30)
	@JSONObject.integer
	@JSONObject.gte(1)
	declare keepLogsMax:number

	/* upload files max size in Kb */
	@JSONObject.map('upload-size-max')
	@JSONObject.optional(30000)
	@JSONObject.integer
	@JSONObject.gte(1)
	declare uploadSizeMax:number

	/* upload certificate path. The path is set to a folder which is intended to have a cert.pem and a key.pem files
	@JSONObject.map('seal-certificate-folder')
	@JSONObject.optional("")
	sealCertificateFolder!:string*/

	/* uploads ttl value */
	@JSONObject.map('upload-ttl')
	@JSONObject.optional(900)
	@JSONObject.integer
	@JSONObject.gte(60)
	declare uploadTtl:number

	/* transaction timeout value: it should greater than or equal DEFAULT_SIGNATURE_TIMEOUT of certignaEndpoint */
	@JSONObject.map('db-transaction-timeout')
	@JSONObject.optional(60000)
	@JSONObject.integer
	@JSONObject.gte(5000)
	declare dbTransactionTimeout:number
}
