import { Component, HostListener, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  CsvSuiviData,
  FlowCriteriaSessionService,
  FlowFilterCriteriaParams,
  defaultFilterOnViewShipment,
  exportSuivi,
  navigateToPreviousUrl,
} from '@cxm-smartflow/flow-traceability/data-access';
import { ActivatedRoute } from '@angular/router';


@Component({
  selector: 'cxm-smartflow-document-of-flow-traceability',
  templateUrl: './document-of-flow-traceability.component.html',
  styleUrls: ['./document-of-flow-traceability.component.scss'],
})
export class DocumentOfFlowTraceabilityComponent implements OnDestroy  {
  destroy$ = new Subject<boolean>();
  isHasFlowTraceabilityId = false;

  constructor(
    private store: Store,
    private storageService: FlowCriteriaSessionService,
    private activatedRoute: ActivatedRoute
  ) {
    const { flowTraceabilityId } = this.activatedRoute.snapshot.queryParams;
    this.isHasFlowTraceabilityId =
      flowTraceabilityId !== undefined && flowTraceabilityId !== 0;
  }


  backToListFlowTraceability(): void {
    this.storageService.clearDocumentShipmentCriteria();
    this.store.dispatch(navigateToPreviousUrl({ isBackToFlow: true }));
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  @HostListener('window:beforeunload', ['$event'])
  unloadHandler() {
    if (this.isHasFlowTraceabilityId) {
      this.storageService.clearDocumentShipmentCriteria();
    } else {
      this.storageService.clearDocumentCriteria();
    }
  }

  exportSuivi(): void {
    const storedFilters = localStorage.getItem('documentCriteria');
    const filters = storedFilters ? JSON.parse(storedFilters) : {};
    const criteriaParams = filters.criteriaParams;
    
  
    const startDate = criteriaParams?.startDate || new Date().toISOString().replace(/[-:.TZ]/g, '');
    const endDate = criteriaParams?.endDate || '';
    
    
    const channels: string[] = criteriaParams?.channels || [];
    const categories: string[] = criteriaParams?.categories || [];
    const fillers: string[] = criteriaParams?.fillers || [];
    const status: string = criteriaParams?.status || '';
    const filter = criteriaParams?.filter || '';
    const sortByField = criteriaParams?.sortByField || defaultFilterOnViewShipment.params.sortByField;
    const sortDirection =  criteriaParams?.sortDirection || defaultFilterOnViewShipment.params.sortDirection;
    const page = criteriaParams?.page || defaultFilterOnViewShipment.page;
    const pageSize =  criteriaParams?.pageSize || defaultFilterOnViewShipment.pageSize;
    const exportData: CsvSuiviData = {
      channels,
      categories,
      fillers,
      status,
      filter,
      startDate,
      endDate,
      sortByField,
      sortDirection,
      page,
      pageSize
    };
  
    this.store.dispatch(exportSuivi({ services: [{ data: exportData }] }));
  }
  
}
