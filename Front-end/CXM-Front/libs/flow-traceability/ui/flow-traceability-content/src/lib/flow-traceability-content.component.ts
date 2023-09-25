import { Component, HostListener, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';
import { FlowCriteriaSessionService } from '@cxm-smartflow/flow-traceability/data-access';

@Component({
  selector: 'cxm-smartflow-flow-traceability-content',
  templateUrl: './flow-traceability-content.component.html',
  styleUrls: ['./flow-traceability-content.component.scss'],
})
export class FlowTraceabilityContentComponent implements OnDestroy {
  destroy$ = new Subject<boolean>();

  constructor(private store: Store, private router: Router, private storageService: FlowCriteriaSessionService) {}

  navigateToFlowDocument(): void {
    this.router.navigate([
      appRoute.cxmFlowTraceability.navigateToListFLowDocument,
    ]);
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  @HostListener("window:beforeunload", ["$event"])
  unloadHandler() {
    this.storageService.clearFlowCriteria();
  }
}
