import { $ok } from '../../utils/commons';
import { Resp, Verb } from '../../utils/tsrequest'

import { APIServer } from '../../server'
import { InternalError } from '../../utils/errors';
import { APIRole } from '../APIConstants';
import { $gid } from '../APIIDs';
import { APIHeaders, CreateCertificateBody, CertificatesQuery, ExpiringURLResource,   } from '../APIInterfaces'
import { apiHeadersSchema, certificatesQuerySchema, createCertificateBodySchema } from '../APISchemas';
import { getSessionCertificateList, generateCertificateForSession } from '../controllers/certificatesController'
import { SessionParams } from './session';

export function certificatesRoutes() {
	const api = APIServer.api() ;

	const tags={tags:['certificates']};
	// getSessionCertificateList
	api.server.route<{
		Params: SessionParams
		Querystring: CertificatesQuery,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/certificates`,
			method: Verb.Get,
			schema:{
				...tags,
				params:{
					id:{type:"number"}
				},
				headers:apiHeadersSchema,
				querystring:certificatesQuerySchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Reading) ;
					let list = await getSessionCertificateList(auth, $gid(request.params.id), request.query) ;
					api.jsonReply(reply, Resp.OK, {...list}) ;
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		}
	) ;

	// generateCertificateForSession
	api.server.route<{
		Params: SessionParams
		Body: CreateCertificateBody,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/certificates`,
			method: Verb.Post,
			schema:{
				...tags,
				headers:apiHeadersSchema,
				params:{
					id:{type:'number'}
				},
				body:createCertificateBodySchema
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, 'id', APIRole.Signature) ;
					let certificate = await generateCertificateForSession(auth, $gid(request.params.id), request.body) ;
					const url = certificate.url(request.params.id) ;
					const date = certificate.creationDate() ;
					const expires = certificate.expirationDate() ;
                    if (!$ok(expires)) { throw new InternalError('Certificate has no expiration date') ; }

					reply.header('Location', url) ;
					reply.header('Date', date) ;
					reply.header('Expires', expires)
					api.jsonReply<ExpiringURLResource>(reply, Resp.Created, { url:url, date:date, expires: expires}) ;
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		}	
	) ;
}
