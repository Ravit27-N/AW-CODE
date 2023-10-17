import { ObjectDictionary } from '../../utils/types';
import { $count, $length, $ok } from "../../utils/commons";

import {
	ForbiddenError,
	NotFoundError,
	ManifestDataError,
	BadRequestError
} from "../../utils/errors";
import { APIServer } from "../../server";
import {$url2lid, $urls2lids, DocumentIDs, GlobalID, LocalID} from "../APIIDs";
import {
	APIAuth,
	ManifestData,
	SessionCheckOTPBody,
	SessionApproveDocumentsBody,
	SigningVisualParameters,
	SigningTextAlignment, ProcessType
} from "../APIInterfaces";
import { Automat, automatCopyWithActorAction } from "../automat/automat";
import { sessionWithPublicID } from "./sessionController";
import { CertignaTextAligns, VisibleSignatureParameters } from "../../classes/CertignaEndPoint";
import {IOTP, OTPEntity} from "../../entities/otp";
import {SessionEntity} from "../../entities/sessions";
import {Prisma} from "@prisma/client";
import {OTPService} from "../../services/otp.service";
import { ScenarioEntity} from "../../entities/scenario";
import { PrismaContext } from '../../classes/interfaces/DBInterfaces';

export interface SigningContext extends PrismaContext {
	tag:string ;
	aid:LocalID ;
	dids:LocalID[] ;
	manifestData:ManifestData ;
	session:SessionEntity ;
	scenario:ScenarioEntity ;
	nextAutomat:Automat ;
}

export const checkOTPConformity = async (auth:APIAuth, sessionPublicID:GlobalID, body:SessionCheckOTPBody, c:PrismaContext) : Promise<[OTPEntity, SessionEntity]> => {
	const context = {trx:c.trx}
	let session = await sessionWithPublicID(auth, sessionPublicID, context);
	const otpService = new OTPService();
	if (session.isClosed() || session.isExpired()) {
		throw new ForbiddenError(`Session ${sessionPublicID} is closed or already expired.`);
	}
	if (!$length(body.otp)) { throw new NotFoundError('OTP field not specified'); }

	let query:  Prisma.ActorWhereInput = {
		sessionId: session.id
	}

	// let actorSubquery = await actorService.getsWhere(query, context)

	if ($length(body.actor)) {
		const aid = $url2lid(body.actor) ;
		if (!aid) { throw new BadRequestError('Bad actor identifier') ; }
		query = {
			...query,
			publicId: aid
		}
	}

	let tokens = await otpService.findMany<IOTP>({
		where: {
			actor: {
				...query
			},
			otp: body.otp
		}
	})

	if ($count(tokens) !== 1) { throw new NotFoundError(); }
	const token = new OTPEntity(tokens[0]);
	if ($length(body.tag) && token.getOtherData?.tag !== body.tag) { throw new NotFoundError(); }

	let docs:DocumentIDs = $urls2lids(body.documents) ;
	if (docs.length !== $count(body.documents)) { throw new NotFoundError() ; }

	docs.forEach(did => { if (!token.getOtherData?.dids?.includes(did)) { throw new NotFoundError(); }} ) ;

	return [token, session] ;
}

export function certignaVisualParameters(params:SigningVisualParameters) : VisibleSignatureParameters
{
	let ret:VisibleSignatureParameters = {
		height:params.height,
		width:params.width,
		x:params.x,
		y:params.y,
		page:params['page-number'],
	} ;
	let textOrImage = false ;

	if ($length(params.text)) {
		textOrImage = true ;
		const fontSize = params['font-size'] ;
		if (!$ok(fontSize)) {
			throw new BadRequestError('Bad PAdES font size option') ;
		}
		const align = params['text-align'] ;
		if (!$ok(align) || !Object.values(SigningTextAlignment).includes(align)) {
			throw new BadRequestError('Bad PAdES text alignment option') ;
		}

		ret.textParameters = {
			text:<string>(params.text),
			fontSize:fontSize,
			horizontalAlignment:CertignaTextAligns[align]
		}
	}
	if ($length(params['image-content'])) {
		textOrImage = true ;
		ret.imageParameters = {
			imageContent:<string>params['image-content']
		}
	}
	if (!textOrImage) {
		throw new BadRequestError('No Image or Text in PAdES visual-parameters') ;
	}

	return ret ;
}

/**
 * This function takes transaction as parameter and not an editing context
 * because the returned objet IS an interface extension of EditingContext
 * (i.e. the EditingContext is created by this function)
*/
export async function initiateSigningOrApprobation(
	api: APIServer,
	auth: APIAuth,
	context: PrismaContext,
	sessionPublicID: GlobalID,
	body: SessionApproveDocumentsBody,
	type: ProcessType,
	manifestDefault: ObjectDictionary,
	defaultTag?: string
): Promise<SigningContext> {
	let actorID = $url2lid(body.actor) ;
	if (!$ok(actorID)) {
		throw new BadRequestError(`No actor defined for signing or approval.`);
	}
	let tag = body.tag;
	if (!$length(tag)) {
		if (!$length(defaultTag)) {
			throw new BadRequestError(`No tag indicated for documents to be signed or approved.`);
		}
		tag = defaultTag;
	} else if (type === 'approve' && !api.checkApprovalTag(tag)) {
		throw new BadRequestError(`bad tag ${tag} for approval.`);
	}
	if (!$count(body.documents)) {
		throw new BadRequestError(`No documents listed for signing or approval.`);
	}

	let documentIdentifiers = $urls2lids(body.documents) ;
	if (documentIdentifiers.length !== $count(body.documents)) {
		throw new BadRequestError(`One of the document listed for signing or approval had a wrong url.`);

	}

	let manifestData = body['manifest-data'];

	if (!api.verifyManifestData(manifestData, manifestDefault)) {
		throw new ManifestDataError(`manifest-data did not match allowed keys for approval or signing operations.`);
	}

	let session;
	if (type === 'approve' || $length(body.otp)) {
		[, session] = await checkOTPConformity(auth, sessionPublicID, body, context);
	} else {
		session = await sessionWithPublicID(auth, sessionPublicID, context);
	}

	let scenario = <ScenarioEntity>await session.myActiveScenario(context);
	if (!$ok(scenario)) {
		throw new ForbiddenError(`There is no active scenario. So nothing to approve or sign.`)
	}
	// this function makes the scenario's automaton advance ont its workflow on actor's action
	// it throws if anything is not conform to the scenario
	let newAutomat = automatCopyWithActorAction(scenario.getOtherData.automat, actorID, <string>tag, documentIdentifiers) ;


	return {
		trx: context.trx,	// because this is a sub-interface of PrismaContext
		tag: <string>tag,
		aid:actorID,
		dids:documentIdentifiers,
		manifestData: manifestData,
		session: session,
		scenario: scenario,
		nextAutomat: newAutomat
	}
}
