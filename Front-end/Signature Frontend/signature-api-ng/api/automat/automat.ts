/**
 *  This is the automat for the api-ng
 *  If you want to coplexify the management
 * 	you will have to rewrite this automat
 *  but all opérations on it are in this file
 *  so never manipulate it without the current
 *  functions and you should be OK for upgrade
 *  compatibility.
 *
 *  Since Objection.js is a json object
 *  aware ORM, our automat is not a class
 *  but an interface (a JSON object) with
 *  fonctions to manipulate it. The drawback
 *  is that you need to copy the result every
 *  time you want something done
 */

import { $count, $isunsigned, $ok } from "../../utils/commons";

import { ConflictError, InternalError } from "../../utils/errors";

import { LocalID, ActorIDs, DocumentIDs } from "../APIIDs";
import {
	AutomatNode,
	AutomatNodeCreation,
	SigningNode,
	workingDocumentsCount,
	automatNodeSigningNodes
} from './automatNode'

export interface Automat {
	nodes:AutomatNode[] ;
	index:number ;
}

export function newAutomat()
{
	return {
		nodes:[] as any[],
		index:0
	}
}

// ============ AUTOMAT INSTANCE METHODS =====================
export function addAutomatNode(self:Automat, node:AutomatNodeCreation)
{
	let workingDocs = [] ;
	const n = node.dids.length ;

	for (let i = 0 ; i < n ; i++) workingDocs.push([]) ;

	self.nodes.push(
		{
			working_documents:workingDocs, // an array full of empty arrays
			done_aids:[],
			done_dids:[],
			... node
		}
	) ;
}

export function automatCurrentNode(self:Automat) : AutomatNode | null
{ return isAutomatAtEnd(self) ? null : self.nodes[self.index] ; }

export type SigningNodeDictionary = { [key: string]: SigningNode[] } ;

// returns the documents to be signed in the current process
// since the automat is very simple : a suite of elementary processes
// there's for now, only one element in the returned dictionary
// Later, when the automat will allow to have several choices at the
// same time for one person, we may have several entries in this dictionary
export function automatSigningDocuments(self:Automat) : SigningNodeDictionary | null
{
	const node = automatCurrentNode(self) ;
	if (!$ok(node)) { return null ; }
	let signingNode = automatNodeSigningNodes(<AutomatNode>node) ;
	if (!$count(signingNode)) { return null ; }
	let ret:SigningNodeDictionary = {} ;
	ret[(<AutomatNode>node).tag] = signingNode ;
	return ret ;
}

export function isAutomatAtStart(self:Automat) : boolean {
	if (!$ok(self) || !$ok(self.index) || self.index !== 0 || $count(self.nodes) === 0) { return false ; }
	let node = self.nodes[self.index] ;
	return node.done_aids.length === 0 && node.done_dids.length === 0 && workingDocumentsCount(node) === 0 ;
}

export function isAutomatAtEnd(self:Automat) : boolean {
	if (!$ok(self) || !$ok(self.index)) return false ; // over protecting, I know
	const count = $count(self.nodes) ;
	if (!count || self.index >= count) return true ;
	return self.index === count - 1 && self.nodes[self.index].dids.length === 0 ;
}

export function aidsForAutomat(self:Automat) : ActorIDs
{
	let set = new Set<LocalID>() ;
	self.nodes.forEach(node => {
		node.aids.forEach(actor => set.add(actor)) ;
	}) ;
	return Array.from(set) ;
}

// ============ AUTOMAT EVOLUTION METHODS =====================

export function automatCopyWithActorAction(self:Automat, actionActorID:LocalID, actionTag:string, actionDocumentIDs:DocumentIDs) : Automat
{
	let index = self.index ;
	if (!$ok(self) || !$isunsigned(index)) {
		throw new InternalError('automatCopyWithActorAction() on bad automat or bad automat index') ;
	}

	if (isAutomatAtEnd(self)) {
		throw new ConflictError('Automat is terminated')
	}

	let node = automatCurrentNode(self) ;
	if (!node) {
		throw new InternalError('automatCopyWithActorAction() bad automat: no nodes are defined here') ;
	}

	if (!$count(node.dids) || $count(node.done_aids) >= node.concernedActors) { index++ ; } // no more document, no more actors to be used at this step
	const n = $count(self.nodes) ;
	if (index >= n) {
		// we were on the last step. should not be here because should have been caught by isAutomatAtEnd() function
		throw new ConflictError('Automat was terminated')
	}

	node = self.nodes[index] ;
	if (actionTag !== node.tag) {
		// bad action for this step
		throw new ConflictError(`Bad action '${actionTag}' for automat step (needed '${node.tag}')`) ;
	}

	let actorIndex = node.aids.indexOf(actionActorID) ;
	if (actorIndex < 0 || node.done_aids.includes(actionActorID)) {
		// actor as already done its duty or is not found
		throw new ConflictError(`Actor with ID ${actionActorID} as already done is duty for this automat node)`) ;
	}

	let documentIndexes:number[] = [] ;
	for (let did of actionDocumentIDs) {
		const dindex = node.dids.indexOf(did) ;
		if (dindex < 0) {
			throw new ConflictError(`Document ID ${did} was not found for this automat node)`) ;
		}
		if (node.done_dids.includes(did)) {
			throw new ConflictError(`Document ID ${did} was already done for this automat node)`) ;
		}
		documentIndexes.push(dindex) ;
		const waids = node.working_documents[dindex] ;
		if (waids.length >= node.concernedActors) {
			throw new ConflictError(`All needed actors have already signed or approved the document with IDfor this step`) ;
		}
		if (node.working_documents[dindex].includes(actionActorID)) {
			throw new ConflictError(`The document with ID ${did} was already signed or approved by actor with ID ${actionActorID}) for this step`) ;
		}
	}

	// here we have a valid action, a valid list of documents and a valid actor in a valid step
	// and the actor action must be recorded in the current Node
	const currentNode = self.nodes[index]
	const temporaryWorkingDocuments = [... currentNode.working_documents] ;

	// first we mark that the actor has taken an action on passed documents
	for (let i = 0, n = actionDocumentIDs.length  ; i < n ; i++) {
		temporaryWorkingDocuments[documentIndexes[i]].push(actionActorID) ;
	}

	// now we reconstruct our current node, taking into
	// acount our actor action
	let actorActionsCount = 0 ;
	const newNode:AutomatNode = {
		... currentNode,
		aids: [... currentNode.aids],
		done_aids: [... currentNode.done_aids],
		done_dids: [... currentNode.done_dids],
		dids: [],
		working_documents:[]
	}

	for (let i = 0, n = currentNode.dids.length ; i < n ; i++) {
		let wnode = temporaryWorkingDocuments[i] ;
		if (wnode.includes(actionActorID)) { actorActionsCount ++ ; }
		if (wnode.length >= currentNode.concernedActors) {
			// this document as fullfilled all its need of action
			newNode.done_dids.push(currentNode.dids[i]) ;
		}
		else {
			// this document needs more action, we keep it in the dids and in working_documents 
			newNode.dids.push(currentNode.dids[i]) ;
			newNode.working_documents.push(wnode) ;
		}
	}

	if (actorActionsCount >= newNode.dids.length + newNode.done_dids.length ) {
		// here we are sure that this actor has made its duty
		// because he has approved or signed all the documents in this node  
		newNode.done_aids.push(actionActorID) ;
		newNode.aids.splice(actorIndex, 0) ;
	}

	const nodesCopy = [... self.nodes] ;
	nodesCopy[index] = newNode ;

	return {
		index: newNode.dids.length === 0 ? index + 1 : index, // if all documents of the node are fulfilled, our automat must progress
		nodes: nodesCopy
	}
}

function _automatSplitIndex(self:Automat) : number | null
{
	if (isAutomatAtEnd(self) || isAutomatAtStart(self)) return null ;
	const node = self.nodes[self.index] ;
	if (node.dids.length === 0) { return self.index + 1 ; } // at the end of current step we plit at the next one
	if (workingDocumentsCount(node) > 0) { return null ; } // we are in the middle of something
	return self.index ;
}

export function splitedAutomats(self:Automat) : { previous:Automat, next:Automat } | null
{
	const n = _automatSplitIndex(self) ;

	if (!$ok(n)) return null ;

	return {
		previous:{
			nodes:self.nodes.slice(0, <number>n),
			index: <number>n
		},
		next:{
			nodes:self.nodes.slice(<number>n),
			index:0
		}
	}
}
