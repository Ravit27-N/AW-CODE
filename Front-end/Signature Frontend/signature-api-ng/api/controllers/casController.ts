import { $count } from "../../utils/commons";
import { ForbiddenError } from "../../utils/errors";
import { APIServer } from "../../server";
import { UserRole } from "../APIConstants";
import { APIAuth } from "../APIInterfaces";
import {CAService} from "../../services/ca.service";
import { CAStatus, NO_CONTEXT } from "../../classes/interfaces/DBConstants";

export interface CAListNode {
	publicId:number ;
}

export const getCAList = async (auth:APIAuth) : Promise<string[]> => {
	// everybody can list the certification authority list : auth is not used here !
	if (auth.role === UserRole.Maintenance || auth.role === UserRole.System) {
		throw new ForbiddenError('System or maintenance users cannot list certification authorities') ;
	}
	const caService = new CAService();
	const list = await caService.findMany<CAListNode>({
		where:{
			status: CAStatus.Valid
		},
		select:{
			publicId: true
		},
		orderBy:{
			publicId: 'asc'
		}
	},NO_CONTEXT);
	const api = APIServer.api() ;
	return $count(list) ? list.map((n:CAListNode) => api.url('ca', n.publicId)) : [] ;
}
