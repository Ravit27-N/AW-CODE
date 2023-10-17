# API-NG: approbation mechanism

Before reading about approbation mechanism, we invite you to have a look on the document automat.md (or automat.pdf) which describe how you define a scenario, the internal automaton which derives from this scenario definition and how the state of this automaton evolves during approbation and signature procedures.

Approbation is a very simple mechanism. It involves updating the current scenario and the current session and never touches the approved documents' files themselves.

Approbation is used by the function `approveDocuments()` defined in the `sessionApprobation.ts` file.

```typescript
async function approveDocuments(
	auth:APIAuth, 
  sessionPublicID:number, 
  requestID:number|string, 
  body:SessionApproveDocumentsBody) : Promise<SigningResource>
```

First, we verify the data used for approbation and initiate the scenario's automaton transition:

```typescript
   let context = await initiateSigningOrApprobation(
   	api, 
   	auth, 
   	trx, 
   	sessionPublicID, 
   	body, 
   	'approve', 
   	api.conf.approveManifestData, 
   	SigningProcess.Approval
   ) ;
```

After calling this function, we get the new `Automat` structure representing what the workflow automaton is after the approbation action. This structure is set in field  `context.nextAutomat`:

```typescript
const patchedScenarioData = {
    // we take a shallow copy because anly the automaton change 
    // and generatedFiles changes 
    ... context.scenario.otherData,
    automat: context.nextAutomat,
  	generatedFiles:{}
} ; 
```

Since in approbation we don't update any PDF file (there's no real signature involved), we only :

1. make a request in the database in order to update the current scenario by changing the `status` if needed  and update the scenario `otherData` field:

   ```typescript
   let updatedScenario = await context.scenario.$q(context).patchAndFetch({
     otherData:patchedScenarioData,
     status:scenarioFinished ? ScenarioStatus.WellTerminated : context.scenario.status
   }) ;
   updatedScenario.session = context.session ; // strait graph for the database
   ```

2. update the `session.otherData.signatures` by inserting for each approved document a `SignedDocument` structure which describes the Approval's operation made on the document: 

   ```typescript
   const signatures = <SignedDocument[]>(otherData.signatures) ;
   context.dids.forEach(did => {
     const signatureID = $uuid() ;
     const signature = {
       tag:context.tag,
       did:did,                    // document identifier
       aid:context.aid,            // actor identifier
       date:now,
       dsigid:signatureID,
       sigid:signatureID,
       threadid:operationID,       // all documents are approved in a same operation
       roleType:RoleType.Approval,
       otp:body.otp,               // the OTP used for the signature
       requesId:`${requestID}`
     } ;
     signatures.push(signature) ;
     ...
   }) ;
   
   ```

3. update the `sessionContextEvents` by inserting an approbation event:

   ```typescript
   otherData.sessionContextEvents.push({
     user:auth.user,
     date:now,
     'event-type':SessionContextEventType.ApproveDocuments,
     'actor-id':context.aid,
     'scenario-id':context.scenario.publicId,
     'operation-id':operationID,
     'document-ids':context.dids,	// identifiers of all approved documents
     'manifest-data':context.manifestData,
     token:body.otp
   }) ;
   ```

4. finaly update the session in database with a request changing its `status` if needed and updating the `otherData` field

   ```typescript
   await context.session.$q(context).patchAndFetch({
     otherData:otherData,
     status: scenarioFinished ? SessionStatus.Idle : context.session.status
   }) ;
   
   ```

   

As you can see, approve documents is very straightforward from the scenario's point of view:

- make the scenario's automaton evolve considering the approbation action
- don't touch at any file (`originalLastFiles`, `sourceFiles` and `generatedFiles`) don't evolve with an approbation
- register the session's approbation event and session's signatures structures
- make the scenario's and session's `status` evolve if necessary