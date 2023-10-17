import {$length, $ok, $now, $timeBetweenDates, stringifyPrisma} from '../../utils/commons'
import {$password} from '../../utils/crypto'
import { CertignaRequestError, DatabaseError, ForbiddenError, InternalError } from "../../utils/errors";

import { Certigna, GenericCertificateEntity, CertificateEntity } from "../../classes/CertignaEndPoint";
import { ActorType } from "../APIConstants";
import { APIAuth } from "../APIInterfaces";
import {UserJwtService} from "../../services/userJwt.service";
import {UserJwtEntity} from "../../entities/userJwt";
import { CertificateData } from '../../classes/interfaces/ICertificateTypeMap';
import { PrismaContext } from '../../classes/interfaces/DBInterfaces';

const CERTIFICATE_PASSWORD_LENGTH = 16 ;

export const certignaJWT = async (user:string, password:string, forceLogin:boolean, c:PrismaContext) : Promise<[string, boolean]> =>
{
	if (!$ok(c.trx)) {
		throw new InternalError('certignaJWT() should be called inside a transaction') ;
	}
	if (!$length(user) || !$length(password)) {
		throw new InternalError('certignaJWT() should be called with a valid user and password')
	}
	const certigna = Certigna.endPoint() ;
	const userService = new UserJwtService();
	let userJWT = await userService.getWhere({user: user}, c);

	let jwt:string|null = null ;
	let isNew = false ;

	if (!forceLogin && $ok(userJWT) && $timeBetweenDates(userJWT!.modificationDate(), $now()) < certigna.jwtTtl) {
		jwt = userJWT!.jwt() ;
	}

	if (!$length(jwt)) {

		const newJwt = await certigna.certificatesAPILogin(user, password) ;

		if (!$length(newJwt)) {
			throw new ForbiddenError('Impossible to login for certificate generation') ;
		}
		if (newJwt !== jwt) {
			// we must save our new token
			isNew = true ;
			if ($ok(userJWT)) {
				userJWT = <UserJwtEntity>await userJWT?.modify({
					authData: stringifyPrisma(UserJwtEntity.authDataWithJWT(<string>newJwt))
				}, c)
			}
			else {
				userJWT = await userService?.insert({
					user:user,
					authData: stringifyPrisma(UserJwtEntity.authDataWithJWT(<string>newJwt))
				}, c)
			}
			if (!$ok(userJWT)) {
				throw new DatabaseError('Impossible to save new JWT') ;
			}
		}
		jwt = newJwt ;
	}

	return [<string>jwt, isNew] ;
}

export interface CertificateGeneration extends GenericCertificateEntity
{
	lifespan:number ; // we made the lifespan mandatory
	emailAddress:string ;
}

export const generateCertignaCertificate = async (auth:APIAuth, input:CertificateGeneration, role:ActorType, c:PrismaContext) : Promise<CertificateData> =>
{
	if (!$ok(c.trx)) {
		throw new InternalError('generateCertignaCertificate() should be called inside a transaction') ;
	}
	let [jwt, isNewJWT] = await certignaJWT(auth.user, auth.password, false, c) ;
	const certigna = Certigna.endPoint() ;

	const certificatePasword = $password(16, {
		hasLowercase:true,
		hasUppercase:true,
		hasNumeric:true,
		hasSpecials:true
	}) ;

	if ($length(certificatePasword) !== CERTIFICATE_PASSWORD_LENGTH) {
		throw new CertignaRequestError()
	}
	let certificateEntity = await certigna.generateCertificate(jwt, {
		password:<string>certificatePasword,
		role:role === ActorType.Entity ? 'PRO' : 'PERSO',
		...input
	}) ;


	if (!$ok(certificateEntity)) {
		if (!isNewJWT) {
			[jwt, isNewJWT] = await certignaJWT(auth.user, auth.password, true, c) ;
			certificateEntity = await certigna.generateCertificate(jwt, {
				password:<string>certificatePasword,
				role:role === ActorType.Entity ? 'PRO' : 'PERSO',
				...input
			}) ;
		}
		if (!$ok(certificateEntity)) {
			throw new CertignaRequestError('Impossible to generate certificate') ;
		}
	}

	const SN = certificateEntity?.serialnumber ;
	if (!$length(SN)) {
		throw new CertignaRequestError('BAD certificate serial number') ;
	}

	let certificate = await certigna.downloadCertificate(jwt, <string>SN) ;
	if (!$length(certificate)) {
		if (!isNewJWT) {
			[jwt, isNewJWT] = await certignaJWT(auth.user, auth.password, true, c) ;
			certificate = await Certigna.endPoint().downloadCertificate(jwt, <string>SN) ;
		}
		if (!$length(certificate)) {
			throw new CertignaRequestError('Impossible to generate certificate') ;
		}
	}

	return <CertificateData>{
		data:(<Buffer>certificate).toString('base64'), // we keep the base64 content here
		password:<string>certificatePasword,
		lifespan:input.lifespan,
		...<CertificateEntity>certificateEntity
	} ;

}

export const revoqueCertignaCertificate = async (auth:APIAuth, SN:string, c:PrismaContext) : Promise<void> =>
{
	if (!$ok(c.trx)) {
		throw new InternalError('revoqueCertignaCertificate() should be called inside a transaction') ;
	}
	let [jwt, isNewJWT] = await certignaJWT(auth.user, auth.password, false, c) ;
	let flag = await Certigna.endPoint().revoqueCertificate(jwt, SN) ; // TODO: later we could add a reason here
	if (!flag) {
		if (!isNewJWT) {
			[jwt, isNewJWT] = await certignaJWT(auth.user, auth.password, true, c) ;
			flag = await Certigna.endPoint().revoqueCertificate(jwt, SN) ;
		}
		// if we fail, we ignore it, and the certificate will stay in Certigna's repository until it auto-revokes
	}
}
