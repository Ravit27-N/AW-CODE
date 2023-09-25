import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { appRoute } from "@cxm-smartflow/template/data-access";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { exhaustMap, filter, map, switchMap, tap, withLatestFrom } from "rxjs/operators";
import {FlowCriteriaSessionService, FlowTraceabilityService} from "../../services";
import * as documentDetailAction from '../flow-document-detail/flow-document-detail.action';
import * as docNav from './document-navigator.reducer';
import {defaultFilterOnViewShipment} from "../document-traceability";

@Injectable()
export class FlowDocumentNavigatorEffect  {

  // check if document load without flow
  loadDocumentDetailEffect$ = createEffect(() => this.actions.pipe(
    ofType(documentDetailAction.loadFlowDocumentDetailSuccess),
    filter(args => args.documentDetail.channel === 'Postal'),
    withLatestFrom(this.store.select(docNav.selectDocNavigatorState)),
    switchMap(([args, navState]) => {

      if(navState.flowid == null) {
        return [docNav.fetchDocumentDetails({ flowid: args.documentDetail.flowTraceabilityId, docId: args.documentDetail.id  })]
      }

      return [docNav.setOpenFlowNavDoc({ data: args })]
    })
  ));

  fetchDocOfFlowEffect$ = createEffect(() => this.actions.pipe(
    ofType(docNav.fetchDocOfFlow),
    exhaustMap(args => {
      let documentCriteria = this.storageService.getDocumentShipmentCriteria();
      if (!documentCriteria.criteriaParams) {
        documentCriteria = {...documentCriteria, criteriaParams: defaultFilterOnViewShipment.params};
      }
      return this.flowService.getFlowDocumentPagination(args.flowid, documentCriteria.page || 1, documentCriteria.pageSize || 10, documentCriteria.criteriaParams).pipe(
        map(res => docNav.findOpenedDoc({alldocument: res.contents, opendocId: args.docId}))
      )
    })
  ))

  fetchDocumentDetailsEffect$ = createEffect(() => this.actions.pipe(
    ofType(docNav.fetchDocumentDetails),
    exhaustMap(args => {
      let documentCriteria = this.storageService.getDocumentShipmentCriteria();
      if (!documentCriteria.criteriaParams) {
        documentCriteria = {...documentCriteria, criteriaParams: defaultFilterOnViewShipment.params};
      }
      return this.flowService.getDocumentDetailsPagination(args.flowid, documentCriteria.criteriaParams).pipe(
        map(res => docNav.findOpenedDoc({alldocument: Array.from(res).map(value => { return { id: value, document: null }}), opendocId: args.docId}))
      )
    })
  ))


  prevDoc$ = createEffect(() => this.actions.pipe(
    ofType(docNav.navPrevDoc),
    withLatestFrom(this.store.select(docNav.selectDocNavigatorState)),
    tap(([args, nav]) => {
      if(nav.hasPrev) {
        const prevId = nav.docs[Math.max(0, nav.currentIndex - 1)];
        this.router.navigate([appRoute.cxmFlowTraceability.navigateToViewDocumentDetail],{ queryParams: { id: prevId }, skipLocationChange: false, replaceUrl: true  });
      }
    })
  ), { dispatch: false })


  nextDoc$ = createEffect(() => this.actions.pipe(
    ofType(docNav.navNextDoc),
    withLatestFrom(this.store.select(docNav.selectDocNavigatorState)),
    tap(([args, nav]) => {
      if(nav.hasNext) {
        const nextId = nav.docs[Math.min(nav.docs.length, nav.currentIndex + 1)];
        this.router.navigate([appRoute.cxmFlowTraceability.navigateToViewDocumentDetail],{ queryParams: { id: nextId },  skipLocationChange: false, replaceUrl: true  });
      }
    })
  ), { dispatch: false })

  constructor(private actions: Actions, private router: Router, private store: Store, private flowService: FlowTraceabilityService, private storageService: FlowCriteriaSessionService) { }

}
