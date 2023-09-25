import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import {
  clearFlowTraceabilityState,
  FlowCriteriaSessionService,
  unloadDocumentTraceabilityList,
} from '@cxm-smartflow/flow-traceability/data-access';
import { Store } from '@ngrx/store';


@Component({
  selector: 'cxm-smartflow-flow-traceability',
  templateUrl: './flow-traceability.component.html',
  styleUrls: ['./flow-traceability.component.scss']
})
export class FlowTraceabilityComponent implements OnDestroy{

  constructor(
    private store: Store,
    private router:  Router,
    private storageService: FlowCriteriaSessionService
  ) {}

   ngOnDestroy(): void{
    this.store.dispatch(clearFlowTraceabilityState());
    this.store.dispatch(unloadDocumentTraceabilityList());
    this.storageService.clearDocumentCriteria();
    this.storageService.clearFlowCriteria();
    this.storageService.clearDocumentShipmentCriteria();
   }

}
