import {
  AfterViewInit,
  Component,
  HostListener, Inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatSnackBarRef } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import {
  approvalActions,
  approvalSelector,
  loadRemainingDateShipment,
  RemainingShipmentModel,
  selectRemainingShipmentDate,
} from '@cxm-smartflow/approval/data-access';
import { TableSelection } from '@cxm-smartflow/shared/common-typo';
import {
  ICommentPayload,
  ISelectionCommentSnackbar,
  SelectionSnackbarService,
} from '@cxm-smartflow/shared/ui/comfirmation-message';
import { appRoute } from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { Observable, ReplaySubject, Subject } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  pluck,
  takeUntil,
  withLatestFrom,
} from 'rxjs/operators';
import {
  formatDateToRequest,
  getDateRangeLast7Days,
} from '@cxm-smartflow/flow-traceability/data-access';
import {
  CommonFilterCriteria,
  DateCriteriaType,
  ICriteriaFiltering,
  IEspaceListFilterConfig,
  RestoreCriteriaState,
} from '@cxm-smartflow/shared/common-criteria';
import * as moment from "moment/moment";
import {DateAdapter, MAT_DATE_FORMATS, MatDateFormats} from "@angular/material/core";

// custom uri encoder
export function encodeURI(str: string): any {

  return encodeURIComponent(str).replace(/[!'()*]/g, function (c: any) {
    return '%' + c.charCodeAt(0).toString(16);
  });
}

export const ESPACE_FILTER_LOCAL_STORAGE_KEY = "list-espace-flow";


@Component({
  selector: 'cxm-smartflow-espace-envoy-list',
  templateUrl: './espace-envoy-list.component.html',
  styleUrls: ['./espace-envoy-list.component.scss']
})
export class EspaceEnvoyListComponent extends TableSelection implements OnInit, OnDestroy, AfterViewInit {

  displayedColumns: string[] = ['checkbox', 'flowName', 'fullName', 'createdAt', 'channel', 'subChannel', 'totalRemainingValidationDocument', 'actions', 'modelName'];

  flows = new MatTableDataSource<any>();
  selectionSnackbarRef: MatSnackBarRef<any>;

  selectedItems$ = new ReplaySubject<any[]>(1);

  pagination$: Observable<any>;
  searchTerm$ = new Subject<string>();

  private destroyed$ = new Subject<boolean>();
  private paginationSubject$ = new ReplaySubject(1);

  filters: any;
  usingFilters = false;

  filterComponentConfig: IEspaceListFilterConfig;
  dateCriteria: DateCriteriaType;

  @ViewChild(MatSort, { static: true }) matSort: MatSort;

  searchTermChanged(searchTerm: any) {
    this.searchTerm$.next(searchTerm);
  }

  setupSelectionSnackbar() {

    this.store
      .select(approvalSelector.selectApprovalPanel)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((open) => {
        if (open) {
          const config: ISelectionCommentSnackbar = {
            alter: true,
            main: true,
            validateComment: true,
            alterName: 'espace.selection.button_refuse',
            mainName: 'espace.selection.button_validate',

            doMain: (payload) => this.requestToValidate(payload),
            doAlter: (payload) => this.requestToRefuse(payload),
            getSelectionItem: () => this.selectedItems$,

            message: 'espace.selection.flow_counter',
            comment: 'espace.selection.comment_placeholder',
          };

          this.selectionSnackbarRef = this.selectionSnackbar.openComment(
            config
          );

          this.selectionSnackbarRef
            .afterDismissed()
            .pipe(takeUntil(this.destroyed$))
            .subscribe(() => {
              this.selection.clear();
              this.store.dispatch(
                approvalActions.setAppprovalPanel({ active: false })
              );
            });
        } else {
          this.selectionSnackbarRef?.dismiss();
        }
      });



    // track checkbox changed
    this.selection.changed.pipe(takeUntil(this.destroyed$)).pipe(withLatestFrom(this.selectedItems$))
    .subscribe(([selectionChanged, lastSelectedItems]) => {

      if(selectionChanged.removed.length > 0) {
        const left = lastSelectedItems.filter(x => selectionChanged.removed.includes(x) == false);
        this.selectedItems$.next(left);
      }

      if(selectionChanged.added.length > 0) {
        const left= lastSelectedItems.concat(selectionChanged.added.map(x => x));
        this.selectedItems$.next(left);
      }
    })

    this.selectedItems$.asObservable().pipe(takeUntil(this.destroyed$))
    .pipe(withLatestFrom(this.store.select(approvalSelector.selectApprovalPanel)))
    .subscribe(([selectedItem, open]) => {
      if(open && selectedItem.length <= 0) {
        this.store.dispatch(approvalActions.closeApprovalPanel());
      }

      if(!open && selectedItem.length > 0) {
        this.store.dispatch(approvalActions.openApprovalPanel());
      }

    })
  }


  paginationUpdate(event: any) {
    this.store.dispatch(
      approvalActions.filterFlowApproveChanged({
        filters: {
          ...this.filters,
          page: event.pageIndex,
          pageSize: event.pageSize,
        },
      })
    );
  }


  criteriaChanged(criteria: ICriteriaFiltering) {
    if(!criteria.start && !criteria.end)
    criteria = {
      ...criteria,
      end: this.dateCriteria.start || '',
      start: this.dateCriteria.start || ''
    };

    criteria = this.resetPagination(criteria);

    delete criteria.resetType;
    this.store.dispatch(approvalActions.filterFlowApproveChanged({ filters: { ...this.filters, ...criteria } }));
  }

  resetPagination(criteria: ICriteriaFiltering): ICriteriaFiltering {
    if (
      criteria.resetType === CommonFilterCriteria.USER &&
      criteria.users.length !== (this.filters?.users?.length || 0)
    ) {
      return { ...criteria, page: 1 };
    }

    if (
      criteria.resetType === CommonFilterCriteria.CATEGORY &&
      (criteria.channels.length !== (this.filters?.channels?.length || 0) ||
        criteria.categories.length !== (this.filters?.categories?.length || 0))
    ) {
      return { ...criteria, page: 1 };
    }

    if (criteria.resetType === CommonFilterCriteria.DATE_PICKER) {
      return { ...criteria, page: 1 };
    }
    return criteria;
  }

  sortDESC(user: any) {
    return user.map((x: any) => ({ ...x, key: x.firstName + ' ' + x.lastName }))
      .sort((a: any, b: any) => {
        return (a.key < b.key ? -1 : 1) * (-1);
      });
  }

  requestToValidate(payload: ICommentPayload) {
    this.selectedItems$.asObservable().subscribe(items => {
      this.store.dispatch(approvalActions.submitApprove({ flows: items, comment: payload.comment }));
    }).unsubscribe();

  }

  requestToRefuse(payload: ICommentPayload) {
    this.selectedItems$.asObservable().subscribe(items => {
      this.store.dispatch(approvalActions.submitRefuse({ flows: items, comment: payload.comment }));
    }).unsubscribe();
  }

  getDatasource(): MatTableDataSource<any> {
    return this.flows;
  }

  ignoreSelection(data: any[]): any[] {
    return data.filter(x => x._editable === true);
  }

  handleConsult(flow: any) {
    this.router.navigateByUrl(`${appRoute.cxmApproval.navigateToValidateFlowDocument}/${flow.id}/${encodeURI(flow.flowName)}`);
  }

  ngOnInit(): void {
    this.store.dispatch(approvalActions.loadCriteriaFilter());
    this.setupSelectionSnackbar();

    this.pagination$ = this.paginationSubject$.asObservable();

    this.store
      .select(approvalSelector.selectApprovalFilterCriteria)
      .pipe(
        takeUntil(this.destroyed$),
        filter((res) => res.isLoaded)
      )
      .subscribe((criteria) => {
        this.loadDateCriteria(criteria.dates);
        this.filterComponentConfig = {
          category: criteria.categories,
          users: this.sortDESC(criteria.users),
        };
      });

    this.store
      .select(approvalSelector.selecApprovalFlowList)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((items) => {
        this.flows = new MatTableDataSource(items.contents);
        this.selectedItems$.next([]);
        this.filters = {
          ...this.filters,
          page: items.page,
          pageSize: items.pageSize,
        };
        this.paginationSubject$.next({
          page: items.page,
          pageSize: items.pageSize,
          total: items.total,
        });
      });

    this.store
      .select(approvalSelector.selectApprovalFilter)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((filters) => {
        this.filters = filters;

        if (this.matSort) {
          const { sortByField, sortDirection } = filters;
          this.matSort.active = sortByField;
          this.matSort.direction = sortDirection;
        }
      });

    this.store
      .select(selectRemainingShipmentDate)
      .pipe(
        takeUntil(this.destroyed$),
        filter((res: RemainingShipmentModel) => res.isLoaded === true)
      )
      .subscribe((remaining) => {
        this.loadDateCriteria(remaining);
      });
  }

  loadDateCriteria(remainingDate: RemainingShipmentModel): void {
    const criteriaStorage = this.getLocalStorageFilterCriteria();
    if (criteriaStorage && criteriaStorage.start && criteriaStorage.end) {
      this.dateCriteria = {
        start: criteriaStorage.start || '',
        end: criteriaStorage.end || '',
      };
    } else if (remainingDate.total && remainingDate.total > 0) {
      this.dateCriteria = {
        start: formatDateToRequest(remainingDate?.startDate) || '',
        end: formatDateToRequest(remainingDate.endDate) || '',
      };
    } else {
      const ranged = getDateRangeLast7Days(true);
      this.dateCriteria = {
        start: ranged.startDate.toString(),
        end: ranged.endDate.toString(),
      };
    }
  }

  getLocalStorageFilterCriteria(): RestoreCriteriaState {
    return JSON.parse(localStorage.getItem(ESPACE_FILTER_LOCAL_STORAGE_KEY) || '{}');
  }

  resetCriteria(type: CommonFilterCriteria) {
    if(type === CommonFilterCriteria.DATE_PICKER) {
      localStorage.setItem(ESPACE_FILTER_LOCAL_STORAGE_KEY, JSON.stringify({...this.getLocalStorageFilterCriteria(), start: '', end: ''}))
      this.store.dispatch(loadRemainingDateShipment());
    }
  }

  ngOnDestroy(): void {

    this.selectionSnackbarRef?.dismiss();
    this.store.dispatch(approvalActions.unloadApprove());

    this.selection.clear();
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  ngAfterViewInit(): void {
    this.matSort.sortChange.pipe(takeUntil(this.destroyed$)).subscribe(sort => {
      this.filters.sortByField  = sort.active;
      this.filters.sortDirection = sort.direction;
      this.store.dispatch(approvalActions.filterFlowApproveChanged({ filters: { ...this.filters } }));
    })

    this.searchTerm$.pipe(distinctUntilChanged(), debounceTime(800))
    .pipe(takeUntil(this.destroyed$))
    .subscribe((value) => {
      this.filters = { ...this.filters, filter: value, page: 1 };
      this.store.dispatch(approvalActions.filterFlowApproveChanged({ filters: this.filters }));
    })
  }

  isShowErrorTooltip(): Observable<boolean> {
    return this.pagination$.pipe(pluck('total'), map(e => e === 0 && this.filters?.filter?.length > 0));
  }

  @HostListener("window:beforeunload", ["$event"])
  clearStateCriteriaBeforeReload() {
    localStorage.setItem(ESPACE_FILTER_LOCAL_STORAGE_KEY, JSON.stringify({}));
  }

  constructor(private selectionSnackbar: SelectionSnackbarService, private store: Store, private router: Router,
              @Inject(MAT_DATE_FORMATS) private _dateFormats: MatDateFormats,
              private _dateAdapter: DateAdapter<Date>,) {
    super();

    const criteriaStorage = this.getLocalStorageFilterCriteria();
    if (criteriaStorage) {
      this.filters = criteriaStorage;
    }

    const localeSelected = localStorage.getItem('locale') || 'fr';
    moment.locale(localeSelected);
    this._dateAdapter.setLocale(localeSelected);

  }
}
