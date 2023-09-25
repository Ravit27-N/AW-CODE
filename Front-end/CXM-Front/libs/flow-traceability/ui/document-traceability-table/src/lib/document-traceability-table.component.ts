import { AfterViewInit, ChangeDetectionStrategy, Component, OnDestroy, ViewChild } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import {
  DocumentModel,
  documentTraceabilityListFilterChangeAction,
  downloadDocDocumentTraceabilityAction,
  FlowCriteriaSessionService,
  FlowFilterCriteriaParams,
  navigateToDetailAction,
  selectFlowDocumentList,
  SortDirection
} from '@cxm-smartflow/flow-traceability/data-access';
import { PaginatorComponent } from '@cxm-smartflow/shared/ui/paginator';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Subject } from 'rxjs';
import { CanVisibilityService } from '@cxm-smartflow/shared/data-access/services';
import { FlowTraceability, FlowType } from '@cxm-smartflow/shared/data-access/model';
import { replaceStatusLabelByDash } from '@cxm-smartflow/flow-traceability/util';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-document-traceability-table',
  templateUrl: './document-traceability-table.component.html',
  styleUrls: ['./document-traceability-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DocumentTraceabilityTableComponent
  implements OnDestroy, AfterViewInit {
  tableColumns = [
    'document',
    'destination',
    'sendingDate',
    'channel',
    'subChannel',
    'status',
    'action'
  ];

  @ViewChild(PaginatorComponent) paginator: PaginatorComponent;

  data$ = new BehaviorSubject<DocumentModel[]>([]);
  params = new BehaviorSubject<FlowFilterCriteriaParams>({});
  flowTraceabilityId = new BehaviorSubject<number>(0);
  isLoading = new BehaviorSubject(false);
  isFilterSearchBox = new BehaviorSubject<boolean>(false);
  isFilterCriteriaNotFound = new BehaviorSubject<boolean>(false);
  destroy$ = new Subject<boolean>();
  page = 1;
  pageSize = 15;
  total$ = new BehaviorSubject<number>(0);

  sortAction = 'sendingDate';
  sortDirection: SortDirection = 'desc';

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  paginationChange(page: PageEvent) {
    this.page = page.pageIndex;
    this.pageSize = page.pageSize;
    this.storageService.setDocumentShipmentCriteria(
      this.mappingStorageData(this.page, this.pageSize, this.params.value)
    );

    this.store.dispatch(
      documentTraceabilityListFilterChangeAction({
        flowTraceabilityId: this.flowTraceabilityId.value,
        page: this.page,
        pageSize: this.pageSize,
        params: this.params.value
      })
    );
  }

  sortEvent(sort: Sort): void {
    let sortByField: string;
    switch (sort.active) {
      case 'sendingDate':
        sortByField = 'dateStatus';
        break;
      case 'category':
        sortByField = 'subChannel';
        break;
      case 'destination':
        sortByField = 'recipient';
        break;
      default:
        sortByField = sort.active;
        break;
    }
    const data = this.storageService.getDocumentShipmentCriteria().criteriaParams;
    this.params.next({
      ...this.params.value,
      sortByField: sortByField,
      sortDirection: sort.direction,
      searchByFiller: data?.searchByFiller ? data.searchByFiller : "",
      fillers: data?.fillers ? data.fillers : []
    });

    if (location.pathname.includes('list-flow-document')) {
      this.storageService.setDocumentCriteria(this.mappingStorageData(this.page, this.pageSize, this.params.value))
    } else {
      this.storageService.setDocumentShipmentCriteria(
        this.mappingStorageData(this.page, this.pageSize, this.params.value)
      );
    }

    this.store.dispatch(
      documentTraceabilityListFilterChangeAction({
        flowTraceabilityId: this.flowTraceabilityId.value,
        page: this.page,
        pageSize: this.pageSize,
        params: this.params.value
      })
    );
  }

  mappingStorageData(
    page?: number,
    pageSize?: number,
    criteriaParams?: FlowFilterCriteriaParams
  ) {
    const data = this.storageService.getDocumentShipmentCriteria();
    return {
      ...data,
      page,
      pageSize,
      criteriaParams
    };
  }

  downloadDoc(event: MouseEvent, row: any) {
    // event.stopPropagation();
    this.store.dispatch(downloadDocDocumentTraceabilityAction({flow: row, _type: FlowType.DOCUMENT}));
  }

  navigateToDocDetail(row: any) {// TODO: Point of test

    if (
      this.canVisibilityService.hasVisibility(
        FlowTraceability.CXM_FLOW_TRACEABILITY,
        FlowTraceability.SELECT_AND_OPEN_DOCUMENT,
        row?.ownerId
      )
    ) {
      this.store.dispatch(navigateToDetailAction({ flow: row }));
    }
  }

  constructor(
    private store: Store,
    private canVisibilityService: CanVisibilityService,
    private storageService: FlowCriteriaSessionService
  ) {
    const storage = this.storageService.getDocumentShipmentCriteria();
    this.page = storage.page || 1;
    this.pageSize = storage.pageSize || 10;
    this.sortAction = this.validateField(storage.criteriaParams?.sortByField || 'sendingDate');
    // @ts-ignore
    this.sortDirection = storage.criteriaParams?.sortDirection || 'desc';
    this.params.next(storage.criteriaParams || {});
  }


  replaceStatusLabel(status: string) {
    return replaceStatusLabelByDash(status);
  }

  validateField(field: string) {
    switch (field) {
      case 'dateStatus':
        return 'sendingDate';
      case 'subChannel':
        return 'category';
      case 'recipient':
        return 'destination';
      default:
        return field;
    }
  }

  ngAfterViewInit(): void {
    this.store
      .select(selectFlowDocumentList)
      .pipe(takeUntil(this.destroy$))
      .subscribe((data: any) => {
        const flowDocument = data?.flowDocument;

        this.isLoading.next(data?.isLoading);
        this.data$.next(flowDocument.contents || []);
        this.flowTraceabilityId.next(data?.flowTraceabilityId || 0);
        this.params.next(data?.params);

        this.page = flowDocument?.page;
        this.pageSize = flowDocument?.pageSize;
        this.total$.next(flowDocument?.total);
        this.isFilterCriteriaNotFound.next(data.isFilterCriteriaNotFound);
        this.isFilterSearchBox.next((data.params?.filter || '').length > 0);
      });
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
}
