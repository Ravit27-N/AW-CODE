import * as fromAction$ from '../../../../../data-access/src/lib/stores/flow-deposit-list';
import * as fromSelector$ from '../../../../../data-access/src/lib/stores/flow-deposit-list';
import {
  confirmDeleteFlowDeposit,
  downloadFile,
  FlowDepositFilterCriteriaModel,
  FlowDepositModel,
  modifiedFlowDeposit
} from '@cxm-smartflow/flow-deposit/data-access';

import { AfterViewInit, Component, HostListener, OnDestroy, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Subject } from 'rxjs';
import { Sort } from '@angular/material/sort';
import { Store } from '@ngrx/store';
import { takeUntil } from 'rxjs/operators';
import { PageEvent } from '@angular/material/paginator';
import { SortDirection } from '@angular/material/sort/sort-direction';
import { DepositManagement, FlowType } from '@cxm-smartflow/shared/data-access/model';
import { CanAccessibilityService } from '@cxm-smartflow/shared/data-access/services';
import { FlowCriteriaSessionService } from '@cxm-smartflow/flow-traceability/data-access';

@Component({
  selector: 'cxm-smartflow-postal',
  templateUrl: './postal.component.html',
  styleUrls: ['./postal.component.scss']
})
export class PostalComponent implements OnInit, AfterViewInit, OnDestroy {

  destroyed$ = new Subject<boolean>();

  // table properties.
  displayColumns: string [] = [
    'flowName',
    'createdBy',
    'createdAt',
    'channel',
    'subChannel',
    'depositMode',
    'status',
    'action'
  ];

  data$ = new BehaviorSubject<FlowDepositModel[]>([]);
  isFilterSearchBox = new BehaviorSubject<boolean>(false);
  isFilterCriteriaNotFound = new BehaviorSubject<boolean>(false);

  page = 1;
  pageSize = 10;
  total = 0;
  $hasFilter = new BehaviorSubject<boolean>(false);

  params: FlowDepositFilterCriteriaModel = {
    sortByField: 'createdAt',
    sortDirection: 'DESC',
    filter: '',
    channels: [],
    subChannels: [],
    users: [],
    depositModes: []
  };

  sortActive = 'createdAt';
  sortDirection: SortDirection = 'desc';

  // Privileges.
  depositPrivilege = DepositManagement;
  canCreate$ = new BehaviorSubject(false);

  constructor(private translate: TranslateService, private route: Router, private store: Store, private canAccessService: CanAccessibilityService, private storageService: FlowCriteriaSessionService) {}

  ngOnInit(): void {
    this.validatePrivilege();
    this.doLoadFlowDepositList();
  }

  private validatePrivilege() {
    this.canCreate$.next(this.canAccessService.canAccessible(this.depositPrivilege.CXM_FLOW_DEPOSIT, this.depositPrivilege.CXM_FLOW_DEPOSIT_SEND_A_LETTER, true));
  }

  private doLoadFlowDepositList() {
    this.store.dispatch(fromAction$.loadFlowDepositList({
      page: this.page,
      pageSize: this.pageSize,
      params: this.params
    }));
  }

  filterFromFirstPage(page?: number): void {
    const firstPage = 1;
    this.page = (this.hasFilter() || this.params?.filter?.length) && !page ? firstPage : this.page;
  }

  hasFilter(): boolean {
    const filters = Array.of(this.params.channels, this.params.users, this.params.subChannels, this.params.depositModes);
    return filters.map(value => value).some(filter => filter?.length || 0 > 0);
  }

  create() {
    this.route.navigate(['/cxm-deposit/acquisition']);
  }

  sortData(sort: Sort) {
    this.params = {
      ...this.params,
      sortDirection: sort.direction,
      sortByField: sort.active
    };

    this.doLoadFlowDepositList();
  }

  onFilterChange(event: any) {
    const { filter, channels, subChannels, users, depositModes } = event;
    this.params = {
      ...this.params,
      filter: filter || '',
      channels: channels || [],
      subChannels: subChannels || [],
      users: users || [],
      depositModes: depositModes
    };

    this.$hasFilter.next(
      channels?.length > 0 ||
      depositModes?.length > 0 ||
      filter?.length > 0 ||
      subChannels?.length > 0 ||
      users?.length > 0 ||
      this.isFilterCriteriaNotFound.value
    );
    this.filterFromFirstPage();
    this.doLoadFlowDepositList();
  }

  showTooltip(id: string, content: string): string {
    const el = document.querySelector(id);
    return el ? (el.scrollWidth > el.clientWidth ? content : '') : '';
  }

  navigationToFlowDeposit(event: MouseEvent, row: any) {
    this.store.dispatch(modifiedFlowDeposit({ row: row }));
  }

  deleteFlowDeposit(event: MouseEvent, flow?: any): void {
    this.translate
      .get('flow.deposit.list.confirmation.deleteFlow')
      .toPromise()
      .then((messages) => {
        const flowInfo = {
          flowId: flow?.id,
          flowName: flow?.flowName,
          fileId: flow?.fileId
        };
        this.store.dispatch(
          confirmDeleteFlowDeposit({
            data: { confirmMessage: messages, information: flowInfo }
          })
        );
      });
    // event.stopPropagation();
  }

  downloadFile(event: MouseEvent, row: any) {
    this.store.dispatch(downloadFile({ flowDeposit: row, _type: FlowType.DEPOSIT }));
    // event.stopPropagation();
  }

  paginationChange(event: PageEvent) {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;
    this.filterFromFirstPage(this.page);
    this.doLoadFlowDepositList();
  }

  ngAfterViewInit(): void {
    this.store.select(fromSelector$.selectFlowDepositListState)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((data: any) => {
        const { isFilterCriteriaNotFound, showSearchBoxTooltip, params } = data;
        const { contents, page, pageSize, total } = data?.response;

        this.data$.next(contents || []);
        this.total = total || 0;
        this.page = page || 1;
        this.pageSize = pageSize || 10;

        this.isFilterSearchBox.next((params as FlowDepositFilterCriteriaModel)?.filter?.length !== 0);
        this.isFilterCriteriaNotFound.next(isFilterCriteriaNotFound);
        const { channels, depositModes, filter, subChannels, users } = this.params as any;
        this.$hasFilter.next(
          channels?.length > 0 ||
          depositModes?.length > 0 ||
          filter?.length > 0 ||
          subChannels?.length > 0 ||
          users?.length > 0 ||
          this.isFilterCriteriaNotFound.value
        );
      });
  }

  ngOnDestroy(): void {
    // this.store.dispatch(clearFlowDepositListState());
    this.isFilterSearchBox.complete();
    this.isFilterCriteriaNotFound.complete();
    this.data$.complete();
    this.store.complete();
    this.destroyed$.complete();
    this.canCreate$.complete();
  }

  @HostListener('window:beforeunload', ['$event'])
  unloadHandler() {
    this.storageService.clearFlowCriteria();
  }
}
