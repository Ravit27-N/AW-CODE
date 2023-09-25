import {
  AfterViewInit,
  Component,
  Input,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Store } from '@ngrx/store';
import {
  CriteriaStorage,
  DateRangeModel,
  DateRangeType,
  documentTraceabilityListFilterChangeAction,
  filterCriteriaFlowChangeAction,
  FlowCriteriaSessionService,
  FlowFilterCriteriaParams,
  loadClientFillers,
  loadFlowDocumentFilterCriteria,
  loadFlowTraceabilityFilterCriteria,
  selectFlowClientFiller,
  selectFlowDocumentShowSearchBoxTooltip,
  selectShowSearchBoxTooltip,
  unloadDocumentTraceabilityList,
} from '@cxm-smartflow/flow-traceability/data-access';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil, withLatestFrom } from 'rxjs/operators';
import { ActivatedRoute } from '@angular/router';
import { FalsyUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-flow-traceability-filter',
  templateUrl: './flow-traceability-filter.component.html',
  styleUrls: ['./flow-traceability-filter.component.scss'],
})
export class FlowTraceabilityFilterComponent implements OnInit, OnDestroy, AfterViewInit {

  @Input() isFlowCriteria = true;
  // Template properties.
  dateRangeType = new BehaviorSubject<DateRangeType>('flowTraceability');
  filter: string;
  showTooltip$ = new BehaviorSubject(false);
  showTooltipBackground = false;
  // Payload properties.
  _page = 1;
  _pageSize = 10;
  _criteria = new BehaviorSubject<FlowFilterCriteriaParams>({});
  // Validation properties.
  _destroy$ = new Subject<boolean>();
  _searchTerm$ = new Subject<string>();
  _flowTraceabilityId = 0;

  clientFillerConfig$: Observable<{ fillers: any[], loaded: boolean }>;

  /**
  * Constructor
  */
  constructor(
    private _store: Store,
    private _translate: TranslateService,
    private _activatedRoute: ActivatedRoute,
    private _storageService: FlowCriteriaSessionService
  ) {
    const { flowTraceabilityId, flowName } = this._activatedRoute.snapshot.queryParams;
    this.filter = flowName || '';
    this._flowTraceabilityId = flowTraceabilityId || 0;
    this._criteria.next({ ...this._criteria.value, filter: flowName || '' });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  /**
  * On init
  */
  ngOnInit(): void {
    // Check type of flow.
    if (!this.isFlowCriteria) {
      if (this._flowTraceabilityId === 0) {
        this.dateRangeType.next('flowDocument');
      } else {
        this.dateRangeType.next('viewDocumentShipment');
      }
    }

    // initial filter criteria from localStorage.
    this._initCriteriaFromStorage();
    this.initialCriteria();

    this.clientFillerConfig$ = this._store.select(selectFlowClientFiller);
    this._store.dispatch(loadClientFillers());
  }

  /**
  * On destroy
  */
  ngOnDestroy(): void {
    // Unsubscribe all subscribers.
    this._destroy$.next(true);
    // Clear flow-document's state in store.
    this._store.dispatch(unloadDocumentTraceabilityList());
  }

  /**
  * After view init
  */
  ngAfterViewInit(): void {
    // Load all flow criteria (faculty, statuses, users, deposit mode).
    this._store.dispatch(loadFlowTraceabilityFilterCriteria());
    // Load all flow-document criteria (faculty, statuses).
    this._store.dispatch(loadFlowDocumentFilterCriteria({ channel: '' }));

    if (this.isFlowCriteria) {
      // Fetch state to check flow search-box filter-component should display error tooltip.
      this._store.select(selectShowSearchBoxTooltip).pipe(takeUntil(this._destroy$)).subscribe((res) => this.showTooltip$.next(res));
    } else {
      // Fetch state to check flow-document search-box filter-component should display error tooltip.
      this._store.select(selectFlowDocumentShowSearchBoxTooltip).pipe(takeUntil(this._destroy$)).subscribe((res) => this.showTooltip$.next(res));
    }

    // Subscribe to search-box filter-component. When user filter, it set a new filter to localstorage
    // and send a request to fetch new state from API.
    this._searchTerm$.pipe(distinctUntilChanged(), debounceTime(800)).pipe(takeUntil(this._destroy$)).subscribe((value) => {
      this.filter = value?.trim();
      this._criteria.next({ ...this._criteria.value, filter: this.filter });
      this._setStorage(this.filter, this._page);
      this._dispatchFilterCriteria();
    });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Accessors
  // -----------------------------------------------------------------------------------------------------
  get notMatchFoundMsg$(): Observable<string> {
    return this._translate.get('flowTraceability.table.header.notFound');
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  /**
  * - Set selected category-criteria to localstorage.
  * - Fetch new state from API with new payload.
  * @param data
  */
  categoryChange(data: any) {
    // Set new selected category-criteria to current payload.
    this._criteria.next({
      ...this._criteria.value,
      categories: data?.categories || [],
      channels: data?.channels,
    });

    // Set payload to localstorage and fetch state from API.
    this._dispatchFilterCriteria();
  }

  /**
  * - Set selected status-criteria to localstorage.
  * - Fetch new state from API with new payload.
  * @param status
  */
  statusChange(status: string[] = []) {
    // Set selected status-criteria to current payload.
    this._criteria.next({ ...this._criteria.value, status });
    // Set payload to localstorage and fetch state from API.
    this._dispatchFilterCriteria();
  }

  /**
  * - Set selected user-criteria to localstorage.
  * - Fetch new state from API with new payload.
  * @param users
  */
  userChange(users: string[] = []) {
    // Set selected user-criteria to current payload.
    this._criteria.next({ ...this._criteria.value, users });

    // Set payload to localstorage and fetch state from API.
    this._dispatchFilterCriteria();
  }

  /**
  * - Set selected deposit-mode to localstorage.
  * - Fetch new state from API with new payload.
  * @param depositModes
  */
  depositModeChange(depositModes: string[] = []) {
    // Set selected deposit-mode to current payload.
    this._criteria.next({ ...this._criteria.value, depositModes });

    // Set payload to localstorage and fetch state from API.
    this._dispatchFilterCriteria();
  }

  /**
  * - Set selected date-range criteria to localstorage.
  * - Fetch new state from API with new payload.
  * @param dateRange
  */
  dateRangeChange(dateRange: DateRangeModel): void {
    // Set selected date-range criteria to current payload.
    this._criteria.next({ ...this._criteria.value, ...dateRange });


    // Set payload to localstorage and fetch state from API.
    this._dispatchFilterCriteria();
  }

  /**
  * Emmit the search-box value to the subscriber.
  * @param searchTerm
  */
  searchTermChanged(searchTerm: any): void {
    this._searchTerm$.next(searchTerm);
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  /**
  * - Fetch criteria of flow or flow-document from localstorage.
  * - Check if flow's criteria from localstorage is truthy, set those criteria to criteria payload.
  * - Request to fetch list of flow with new criteria payload.
  */
  private _dispatchFilterCriteria(): void {
    // Flow traceability.
    if (this.isFlowCriteria) {
      // Fetch flow's criteria from localstorage.
      const params: any = this._storageService.getFlowCriteria();

      // Request to fetch list of flow with new payload criteria.
      this._store.dispatch(
        filterCriteriaFlowChangeAction({
          params: this._criteria.value,
          page: this._page,
          pageSize: this._pageSize,
        })
      );
    } else {
      // Flow document.

      // Fetch flow-document's criteria from localstorage.
      const params: any =  this.dateRangeType.value === 'viewDocumentShipment' ?
      this._storageService.getDocumentShipmentCriteria() :
      this._storageService.getDocumentCriteria();

      // Request to fetch list of flow-document with new payload criteria.
      this._store.dispatch(
        documentTraceabilityListFilterChangeAction({
          flowTraceabilityId: this._flowTraceabilityId,
          page: this._page,
          pageSize: this._pageSize,
          params: this._criteria.value || {},
        })
      );
    }
  }


  /**
  * - Initial criteria and payload from localstorage if the criteria is truthy base on type of flow.
  * @private
  */
  private _initCriteriaFromStorage(): void {
    let storage;

    // Initial search-box filtering-component.
    if (this.isFlowCriteria) {
      // If current criteria belong to flow, fetch state in localstorage
      // and set it to search-box filtering-component.
      storage = this._storageService.getFlowCriteria();
      this.filter = storage.criteriaParams?.filter || '';
    } else {
      if (this.dateRangeType.value === 'flowDocument') {
        // If current criteria belong to flow-document, fetch state in localstorage
        // and set it to search-box filtering-component.
        storage = this._storageService.getDocumentCriteria();
        this.filter = storage.criteriaParams?.filter || this.filter;
      } else {
        // If current criteria belong to flow-document-shipment, fetch state in localstorage
        // and set it to search-box filtering-component.
        storage = this._storageService.getDocumentShipmentCriteria();
        this.filter = storage.criteriaParams?.filter || this.filter;
      }
    }

    // Initial criteria from localstorage to the payload if it's truthy.
    this._page = storage.page || 1;
    this._pageSize = storage.pageSize || 10;
    this._criteria.next({
      ...this._criteria.value,
      ...storage.criteriaParams,
    });
  }

  /**
  * Save flow criteria in localstorage base on type of flow.
  * @param data
   * @param page
  */
  private _setStorage(data: string, page = 1): void {
    if (this.isFlowCriteria) {
      // Save flow-traceability criteria in localstorage.
      this._storageService.setFlowCriteria(
        this._mappingStorageData(data, page, this._storageService.getFlowCriteria())
      );
    } else {
      // Save flow-document criteria in localstorage.
      if (this.dateRangeType.value === 'flowDocument') {
        this._storageService.setDocumentCriteria(
          this._mappingStorageData(data, page, this._storageService.getDocumentCriteria())
        );
      } else {
        // Save flow-document-shipment criteria in localstorage.
        this._storageService.setDocumentShipmentCriteria(
          this._mappingStorageData(data, page, this._storageService.getDocumentShipmentCriteria())
        );
      }
    }
  }

  /**
  * Map flow criteria.
  * @param filter
  * @param data
   * @param page
  */
  private _mappingStorageData(filter?: string, page = 1, data?: CriteriaStorage) {
    return { ...data, criteriaParams: { ...data?.criteriaParams, filter, page } };
  }



  clientFillerChange({ keyTerm, fillers }: any) {
    // filler table result

    if(this.dateRangeType.value === 'viewDocumentShipment') {
      const data = this._storageService.getDocumentShipmentCriteria();
      this._storageService.setDocumentShipmentCriteria({...data,
        criteriaParams: {...data.criteriaParams,
          searchByFiller: keyTerm,
          fillers: fillers  }});
    } else if(this.dateRangeType.value === 'flowDocument') {
      const data = this._storageService.getDocumentCriteria();
      this._storageService.setDocumentCriteria({...data,
        criteriaParams: {...data.criteriaParams,
          searchByFiller: keyTerm,
          fillers: fillers  }});
    }



    this._criteria.next({...this._criteria.value,
      searchByFiller: keyTerm,
      fillers: fillers
    });

    this._dispatchFilterCriteria();
  }

  private initialCriteria() {
    if (this.isFlowCriteria) {
      const params: any = this._storageService.getFlowCriteria();
      if (!FalsyUtil.isEmptyObject(params)) {
        this._criteria.next(params.criteriaParams);
      }
    } else {
      const params: any =  this.dateRangeType.value === 'viewDocumentShipment' ?
      this._storageService.getDocumentShipmentCriteria() :
      this._storageService.getDocumentCriteria();
      if (!FalsyUtil.isEmptyObject(params)) {
        this._criteria.next(params.criteriaParams);
      }
    }
  }

}
