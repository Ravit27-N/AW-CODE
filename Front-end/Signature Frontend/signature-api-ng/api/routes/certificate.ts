import { Resp, Verb } from '../../utils/tsrequest'

import { APIServer } from '../../server'
import { APIHeaders, CertificateResource, DeletedURLResource } from '../APIInterfaces'
import { APIRole } from '../APIConstants'
import { getSessionCertificate, removeSessionCertificate } from '../controllers/certificateController'
import { $gid, $lid } from '../APIIDs';
import { apiHeadersSchema } from '../APISchemas';
import { $ok } from '../../utils/commons'
import { InternalError } from '../../utils/errors'
import { Prisma } from '@prisma/client'


export interface CertificateParam	{ id:number ; cid:number ; }

export function certificateRoutes() {
	const api = APIServer.api() ;
	const tags={tags:['certificate']};
	// getSessionCertificate
	api.server.route<{
		Params: CertificateParam,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/certificate/:cid`,
			method: Verb.Get,
			schema:{
				...tags,
				headers:apiHeadersSchema,
				params:{
					id:{type:'number'},
					cid:{type:"number"}
				}
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, ['id', 'cid'], APIRole.Reading) ; // TODO: should'nt be APIRole.Signature here ?
					let sessionPublicID = $gid(request.params.id) ;
					let certificate = await getSessionCertificate<Prisma.CertificateInclude>(
						auth, 
						sessionPublicID, 
						$lid(request.params.cid), 
						{
							include:{
								caToken:{
									include:{
										actor: true,
										ca: true
									}
								}
							}
						}
					) ;
					const creationDate = certificate.creationDate() ;
					const expirationDate = certificate.expirationDate() ;
                    if (!$ok(expirationDate)) { throw new InternalError('Certificate has no expiration date') ; }
                    // const actor = new ActorEntity(<Actor>certificate.catoken?.actor);
					reply.header('Date', creationDate) ;
					reply.header('Expires', expirationDate) ;
					api.jsonReply<CertificateResource>(reply, Resp.OK, {
						actor: api.url('session', sessionPublicID, 'actor', certificate.getCaToken!.Actor!.publicId),
						authority: api.url('ca', certificate.getCaToken!.Authority!.publicId),
						date: creationDate,
						expires: expirationDate,
						session: api.url('session', sessionPublicID),
						SN: certificate.getCertificateData.serialnumber,
						status: certificate.status,
						ttl: certificate.ttl
					}) ;
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		},
	) ;
	// removeSessionCertificate
	api.server.route<{
		Params: CertificateParam,
		Headers: APIHeaders
	}
	>(
		{
			url: `${api.prefix}${api.version}/session/:id/certificate/:cid`,
			method: Verb.Delete,
			schema:{
				...tags,
				headers:apiHeadersSchema,
				params:{
					id:{type:'number'},
					cid:{type:"number"}
				}
			},
			handler: async (request, reply) => {
				try {
					const auth = api.prepareRequest(request, reply, ['id', 'cid'], APIRole.Update) ;
					let url = await removeSessionCertificate(auth, $gid(request.params.id), $lid(request.params.cid)) ;
					api.jsonReply<DeletedURLResource>(reply, Resp.OK, {deleted:url}) ;
				}
				catch (e) { await api.requestError(reply, e) ; }
			}
		},
	) ;
}


