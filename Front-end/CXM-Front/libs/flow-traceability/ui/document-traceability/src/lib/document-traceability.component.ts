import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  defaultFilterOnViewShipment,
  FlowCriteriaSessionService,
  FlowFilterCriteriaParams,
  loadDocumentTraceabilityList,
  navigateToPreviousUrl,
} from '@cxm-smartflow/flow-traceability/data-access';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Subject } from 'rxjs';
import { Location } from '@angular/common';

@Component({
  selector: 'cxm-smartflow-shipment-tracking',
  templateUrl: './document-traceability.component.html',
  styleUrls: ['./document-traceability.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentTraceabilityComponent implements OnDestroy {
  flowTraceabilityId = new BehaviorSubject(0);
  canPrevious = new BehaviorSubject(false);
  isLoading = new BehaviorSubject(false);
  destroy$ = new Subject<boolean>();

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  backToListFlowTraceability(): void {
    this.store.dispatch(navigateToPreviousUrl({ isBackToFlow: false }));
  }

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    public location: Location,
    private storageService: FlowCriteriaSessionService
  ) {
    const {
      flowTraceabilityId,
      flowName,
    } = this.activatedRoute.snapshot.queryParams;

    const isHasFlowTraceabilityId =
      flowTraceabilityId !== undefined && flowTraceabilityId !== 0;

    this.canPrevious.next(isHasFlowTraceabilityId);

    if (isHasFlowTraceabilityId) {
      const storage = this.storageService.getDocumentShipmentCriteria()?.criteriaParams;
      const flowFilterCriteriaParams: FlowFilterCriteriaParams = {
        filter: storage?.filter ? storage.filter : flowName || '',
        sortByField: storage?.sortByField || defaultFilterOnViewShipment.params.sortByField,
        sortDirection: storage?.sortDirection || defaultFilterOnViewShipment.params.sortDirection,
        startDate: storage?.startDate || '',
        endDate: storage?.endDate || '',
        status: storage?.status || [],
        channels: storage?.channels || [],
        categories: storage?.categories || [],
      }

      this.store.dispatch(
        loadDocumentTraceabilityList({
          flowTraceabilityId,
          ...defaultFilterOnViewShipment,
          params: {
            ...flowFilterCriteriaParams
          },
        })
      );
    }
  }
}
