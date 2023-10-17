import {$date2string, $ok} from '../../utils/commons';
import {Resp, Verb} from '../../utils/tsrequest'

import {APIServer} from '../../server'
import {
	APIHeaders,
	ClosingSessionResource,
	ExpiringURLResource,
	OTPResource,
	SessionApproveDocumentsBody,
	SessionCheckOTPBody,
	SessionClosureBody,
	SessionDirectSignDocumentsBody,
	SessionExtendBody,
	SessionOTPBody,
	SessionRecipientBody, SessionRefuseBody,
	SessionSignDocumentsBody,
} from '../APIInterfaces'
import {APIRole} from '../APIConstants'
import {closeSession, extendSession, getSessionByID, getSessionManifestDownload} from '../controllers/sessionController'
import {
	generateOtp,
	checkOTP,
	approveDocuments,
	signDocuments, recipient, refuse
} from '../controllers/sessionSignature'
import {$gid} from '../APIIDs';
import {
	apiHeadersSchema,
	sessionApproveDocumentsBodySchema,
	sessionCheckOTPBodySchema,
	sessionClosureBodySchema,
	sessionDirectSignDocumentsBodySchema,
	sessionExtendBodySchema,
	sessionOTPBodySchema
} from '../APISchemas'
import {InternalError} from '../../utils/errors';
import {NO_CONTEXT} from '../../classes/interfaces/DBConstants';


export interface SessionParams	{ id:number ; }


export function sessionRoutes() {
	const api = APIServer.api() ;

	const tags={tags:['session']};
	// getSessionByID
	api.server.route<{
		Params: SessionParams,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id`,
			method: Verb.Get,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				headers:apiHeadersSchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id') ;
					let session = await getSessionByID(auth, $gid(request.params.id), NO_CONTEXT) ;
					
					reply.header('Date', session.creationDate()) ;
					reply.header('Expires',session.expirationDate()) ;
					reply.header('Last-Modified', session.modificationDate()) ;
					let js = await session.toAPI(NO_CONTEXT);
					api.jsonReply<any>(reply, Resp.OK, js) ;
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		},
	
	) ;

	// closeSession
	api.server.route<{
		Body:SessionClosureBody
		Params: SessionParams,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/close`,
			method: Verb.Put,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				headers:apiHeadersSchema,
				body:sessionClosureBodySchema
			},
			handler: async (request, reply) => {

				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Update);

					let returnedValue = await closeSession(auth, $gid(request.params.id), request.body.force, request.body.reason, request.body['manifest-data']);
					if (returnedValue) {
						let resource: ClosingSessionResource = {status: returnedValue.status};
						if (returnedValue.code === Resp.Created && $ok(returnedValue.download)) {
							const url = returnedValue.download?.url();
							const date = returnedValue.download?.creationDate();
							const expires = returnedValue.download?.expirationDate();
							if (!$ok(expires)) throw new InternalError("Downloadable resource has no expiration date");

							reply.header('Location', url);
							reply.header('Date', date);
							reply.header('Expires', expires!);
							resource.url = url;
							resource.date = date;
							resource.expires = expires!;
						}
						api.jsonReply<ClosingSessionResource>(reply, returnedValue.code ?? 0, resource);
					}
				} catch (e) {
					await api.requestError(reply, e);
				}
			}
		},
	) ;

	// extendSession
	api.server.route<{
		Body:SessionExtendBody,
		Params: SessionParams,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/extendSession`,
			method: Verb.Put,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				headers:apiHeadersSchema,
				body:sessionExtendBodySchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Update) ;
					const session = await extendSession(auth, $gid(request.params.id), request.body.ttl) ;
					if(!session){
						throw new Error("session null");
					}
					const url = session.url() ;
					const date = session.creationDate() ;
					const expires = session.expirationDate() ;
					reply.header('Location', url) ;
					reply.header('Date', date) ;
					reply.header('Expires',expires) ;
					api.jsonReply<ExpiringURLResource>(reply, Resp.OK, { url:url, date:date, expires:expires}) ;
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		}
	) ;
	
	// manifest
	api.server.route<{
		Params: SessionParams,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/manifest`,
			method: Verb.Get,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				headers:apiHeadersSchema
				
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id') ; // TODO: APIRole.Signature here in spite of default APIRole.reading ?
					let download = await getSessionManifestDownload(auth, $gid(request.params.id)) ;
					if(!download) throw new Error("download is null");
					const url = download.url() ;
					const date = download.creationDate() ;
					const expires = download.expirationDate() ;
                    if (!$ok(expires)) { throw new InternalError("Downloadable resource has no expiration date") ; }

					reply.header('Location', url) ;
					reply.header('Date', date) ;
					reply.header('Expires', expires!);
					
				api.jsonReply<ExpiringURLResource>(reply, Resp.OK, { url:url, date:date, expires:expires!}) ;
				
				}
				catch (e) {
					
					await api.requestError(reply, e) ; }
			}
		},
	) ;

	// generateOtp
	api.server.route<{
		Body:SessionOTPBody,
		Params: SessionParams,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/generate-otp`,
			method: Verb.Put,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				body:sessionOTPBodySchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Signature);
					let otp = await generateOtp(auth, $gid(request.params.id), request.body);
					const expires = otp.expirationDate();
					if (!$ok(expires)) {
						throw new InternalError("OTP has no expiration date");
					}

					reply.header('Date', $date2string(otp.creationDate()));
					reply.header('Expires', $date2string(expires));
					api.jsonReply<OTPResource>(reply, Resp.OK, {otp: otp.otp, date: otp.creationDate(), expires: expires!});
				} catch (e) {
					await api.requestError(reply, e);
				}
			}
		}
	) ;

	// checkOTP
	api.server.route<{
		Body:SessionCheckOTPBody,
		Params: SessionParams,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/check-otp`,
			method: Verb.Put,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				body:sessionCheckOTPBodySchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Signature) ;
					let deleted = await checkOTP(auth, $gid(request.params.id), request.body) ;
					if (deleted) {
						api.jsonReply(reply, Resp.OK, { deleted:request.body.otp }) ;
					}
					else {
						api.jsonReply(reply, Resp.OK, {
							otp:request.body.otp,
							actor:request.body.actor,
							documents:request.body.documents
						}) ;	
					}
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		}
	) ;

	// approveDocuments
	api.server.route<{
		Body:SessionApproveDocumentsBody,
		Params: SessionParams,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/approve-documents`,
			method: Verb.Put,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				body:sessionApproveDocumentsBodySchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Signature) ;
					let response = await approveDocuments(auth, $gid(request.params.id), request.id, request.body) ;
					api.jsonReply(reply, Resp.OK, {...response}) ;	
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		}
	) ;

	// directSignDocuments
	api.server.route<{
		Body:SessionDirectSignDocumentsBody,
		Params: SessionParams,
		Headers: APIHeaders

	}>(
		{
			url: `${api.prefix}${api.version}/session/:id/direct-sign-documents`,
			method: Verb.Put,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				body:sessionDirectSignDocumentsBodySchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Signature) ;
					let response = await signDocuments(auth, $gid(request.params.id), request.id, request.body /* NO CERTIFICATE URL HERE */) ;
					api.jsonReply(reply, Resp.OK, {...response}) ;
				}
				catch (e) { await api.requestError(reply, e) ; }
			}

		}
	) ;
	
	// signDocuments
	api.server.route<{
		Body:SessionSignDocumentsBody,
		Params: SessionParams,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/sign-documents`,
			method: Verb.Put,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				body:sessionDirectSignDocumentsBodySchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Signature) ;
					let response = await signDocuments(auth, $gid(request.params.id), request.id, request.body, request.body.certificate) ;
					api.jsonReply(reply, Resp.OK, {...response}) ;	
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		}
	) ;
	
	api.server.route<{
		Body:SessionRecipientBody,
		Params: SessionParams,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/recipient`,
			method: Verb.Put,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				body:sessionDirectSignDocumentsBodySchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Signature) ;
					let response = await recipient(auth, $gid(request.params.id), request.id, request.body) ;
					api.jsonReply(reply, Resp.OK, {...response}) ;
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		}
	) ;

	api.server.route<{
		Body:SessionRefuseBody,
		Params: SessionParams,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/refuse`,
			method: Verb.Put,
			schema:{
				...tags,
				params:{id:{type:'number'}},
				body:sessionDirectSignDocumentsBodySchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Signature) ;
					let response = await refuse(auth, $gid(request.params.id), request.id, request.body) ;
					api.jsonReply(reply, Resp.OK, {...response}) ;
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		}
	) ;
}
