import { Component, OnDestroy, OnInit } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import {
  confirmCancelFlowTraceability,
  downloadFeedAction,
  filterCriteriaFlowChangeAction,
  FlowCriteriaSessionService,
  FlowFilterCriteriaParams,
  FlowTraceabilityList,
  FlowTraceabilityModel,
  FlowTraceabilityService,
  getFeatureFlowTraceabilityList,
  navigateToDocumentTraceabilityAction,
  removeState,
  SortDirection,
  unloadDocumentTraceabilityList
} from '@cxm-smartflow/flow-traceability/data-access';
import { appRoute, FlowType } from '@cxm-smartflow/shared/data-access/model';
import {
  CanModificationService,
  CanVisibilityService,
  SnackBarService
} from '@cxm-smartflow/shared/data-access/services';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { Router } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';

@Component({
  selector: 'cxm-smartflow-flow-traceability-table',
  templateUrl: './flow-traceability-table.component.html',
  styleUrls: ['./flow-traceability-table.component.scss']
})
export class FlowTraceabilityTableComponent implements OnInit, OnDestroy {
  tableColumns = [
    'flowName',
    'createdBy',
    'createdAt',
    'channel',
    'subChannel',
    'depositMode',
    'status',
    'actions',
	'modelName'
  ];

  data$ = new BehaviorSubject<FlowTraceabilityModel[]>([]);
  globalPropertiesLabel: any;
  isFilterCriteriaNotFound = new BehaviorSubject<boolean>(false);

  params$ = new BehaviorSubject<FlowFilterCriteriaParams>({});
  destroy$ = new Subject<boolean>();
  sortAction = 'createdAt';
  sortDirection: SortDirection = 'desc';

  page = 1;
  pageSize = 10;
  total = 0;
  isLoading = new BehaviorSubject(false);
  routeProps = appRoute;

  constructor(
    private store: Store,
    private router: Router,
    private confirmPopup: ConfirmationMessageService,
    private translate: TranslateService,
    private flowTraceabilityService: FlowTraceabilityService,
    private snackBarService: SnackBarService,
    private canVisibilityService: CanVisibilityService,
    private canModificationService: CanModificationService,
    private storageService: FlowCriteriaSessionService
  ) {
    this.translate
      .get('flowTraceability.confirmationPopUp')
      .subscribe((response) => (this.globalPropertiesLabel = response));

    const storage = this.storageService.getFlowCriteria();
    this.page = storage.page || 1;
    this.pageSize = storage.pageSize || 10;
    this.sortAction = storage.criteriaParams?.sortByField || 'createdAt';
    // @ts-ignore
    this.sortDirection = storage.criteriaParams?.sortDirection || 'desc';
    this.params$.next(storage.criteriaParams || {});
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.store.dispatch(removeState());
  }

  navigateToFlowDetails(id = 0, channel?: string, depositType?: string, fileId?: any): void {
    if (
      (channel && channel.toLowerCase() === 'postal') ||
      (depositType && depositType.toLowerCase() === 'pdf')
    ) {
      this.router.navigate(
        [this.routeProps.cxmFlowTraceability.navigateToFlowDetailDeposit],
        { queryParams: { id } }
      );
    }

    if (
      (channel && channel.toLowerCase() === 'digital') ||
      (depositType && depositType.toUpperCase() === 'CAMPAIGN_SMS')
    ) {
      this.router.navigate(
        [this.routeProps.cxmFlowTraceability.navigateToFlowDetailDigital],
        {
          queryParams: { id, fileId }
        }
      );
    }
  }

  cancelFlowTraceability(event: MouseEvent, flow?: any): void {
    event.stopPropagation();
    this.translate
      .get('flow.traceability.confirmPopups.cancelFlow')
      .toPromise()
      .then((messages) => {
        const flowInfo = {
          flowId: flow?.id,
          flowName: flow?.flowName,
          depositMode: flow?.depositMode,
          fileId: flow?.fileId,
          createdBy: flow?.createdBy
        };
        this.store.dispatch(
          confirmCancelFlowTraceability({
            data: { confirmMessage: messages, information: flowInfo }
          })
        );
      });
  }

  ngOnInit(): void {
    this.store
      .select(getFeatureFlowTraceabilityList)
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        const flowTraceability = data.flowTraceability as FlowTraceabilityList;

        this.isLoading.next(data?.isLoading || false);
        this.data$.next(flowTraceability.contents || []);
        this.params$.next(data.params);
        this.total = flowTraceability.total || 0;
        this.page = flowTraceability.page || 1;
        this.pageSize = flowTraceability.pageSize || 10;
        this.isFilterCriteriaNotFound.next(data.isFilterCriteriaNotFound);
      });
  }

  paginationChange(event: PageEvent) {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;
    this.dispatchFilterCriteria();
  }

  sortEvent(sort: Sort): void {
    this.params$.next({
      ...this.params$.value,
      sortDirection: sort.direction,
      sortByField: sort.active
    });
    this.dispatchFilterCriteria();
  }

  dispatchFilterCriteria(): void {
    this.storageService.setFlowCriteria(
      this.mappingStorageData(this.page, this.pageSize, this.params$.value)
    );
    this.store.dispatch(
      filterCriteriaFlowChangeAction({
        params: this.params$.value,
        page: this.page,
        pageSize: this.pageSize
      })
    );
  }

  downloadFeed(event: MouseEvent, row: any) {
    // event.stopPropagation();
    this.store.dispatch(downloadFeedAction({flow: row, _type: FlowType.FLOW}));
  }

  navigateToDocumentTraceability(event: MouseEvent, row: any) {
    event.stopPropagation();
    this.store.dispatch(unloadDocumentTraceabilityList());
    this.store.dispatch(navigateToDocumentTraceabilityAction(row));
  }

  addCustomCssClass() {
    document
      .querySelector('.action-card-panel')
      ?.classList?.add('cxm-flow-traceability--list');
  }

  showTooltip(id: string, content: string): string {
    const el = document.querySelector(id);
    return el ? (el.scrollWidth > el.clientWidth ? content : '') : '';
  }

  mappingStorageData(
    page?: number,
    pageSize?: number,
    criteriaParams?: FlowFilterCriteriaParams
  ) {
    const data = this.storageService.getFlowCriteria();
    return {
      ...data,
      page,
      pageSize,
      criteriaParams
    };
  }
}
