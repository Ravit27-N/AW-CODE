import { $count } from "../../utils/commons";

import { RoleType } from "../APIConstants";
import { LocalID, ActorIDs, DocumentIDs } from "../APIIDs";

export interface AutomatNodeCreation {
	stepIndex:number ;		// the step which this node relates to
	roleType:RoleType ;		// is it approval or signing or expedition
	tag:string ; 			// the process tag
	aids:ActorIDs ; 		// actors who can signe or approve at this stage
	dids:DocumentIDs ;		// documents which should be signed or approved
	concernedActors:number ;// number of actor which should sign or aprove or whatever
}

export interface AutomatNode extends AutomatNodeCreation {
	working_documents:ActorIDs[] ;	// per remaining document a list of actor ids who have signed the document
	done_aids:ActorIDs ;			// actors who have fullfilled their duties
	done_dids:DocumentIDs ;		// documents which have been fully signed
}

export function workingDocumentsCount(self:AutomatNode) : number {
	let n = 0 ;
	self.working_documents.forEach(actorIds => { if (actorIds.length > 0) n++ ; })
	return n ;
}

export interface SigningNode {
	did:LocalID ;
	aids:ActorIDs ;
}


export function automatNodeSigningNodes(self:AutomatNode) : SigningNode[]
{
	let ret:SigningNode[] = [] ;
	const n = $count(self.dids) ;
	for (let i = 0 ; i < n ; i++) {
		const signedAids = self.working_documents[i] ;
		const remainingAids = self.aids.filter(aid => !signedAids.includes(aid)) ;
		if ($count(remainingAids)) {
			ret.push({did:self.dids[i], aids:remainingAids})
		}
	}
	return ret ;
}
