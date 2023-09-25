import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { TableSelection } from '@cxm-smartflow/shared/common-typo';
import {ISelectionSnackbar, SelectionSnackbarService} from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Store } from '@ngrx/store';
import {ResponseEnvelopeReference} from "@cxm-smartflow/envelope-reference/data-access";
import {
  deleteEnvelopeReferences,
  entriesBatchOfModification,
  fetchEnvelopeReferences,
  setSelectionPanel
} from "@cxm-smartflow/envelope-reference/data-access";
import {
  selectEnvelopeReferencesList,
  selectFilteredModified,
  selectSelectionOpened
} from "@cxm-smartflow/envelope-reference/data-access";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {debounceTime, distinctUntilChanged, map, skip, takeUntil} from "rxjs/operators";
import {MatSnackBarRef} from "@angular/material/snack-bar";
import {appRoute} from "@cxm-smartflow/shared/data-access/model";
import {Router} from "@angular/router";
import {EnvelopeReferenceFormUpdateMode} from "@cxm-smartflow/envelope-reference/util";
import {
  EnvelopeReferenceFormControlService
} from "@cxm-smartflow/envelope-reference/data-access";
import {MatSort} from "@angular/material/sort";
import {arraysOfObjectsHaveSameElements} from "@cxm-smartflow/envelope-reference/util";

@Component({
  selector: 'cxm-smartflow-list-envelope-reference-table',
  templateUrl: './list-envelope-reference-table.component.html',
  styleUrls: ['./list-envelope-reference-table.component.scss']
})

export class ListEnvelopeReferenceTableComponent extends TableSelection implements OnInit ,OnDestroy, AfterViewInit {
  destroyed$ = new Subject<boolean>();
  tableColumns = ['select', 'reference', 'format', 'description','active','actions'];
  pagination = { page: 1, pageSize: 0, total: 0 };
  envelopeReferences = new MatTableDataSource<ResponseEnvelopeReference>();
  listModificationId: string[] = [];
  envelopeReferenceList: ResponseEnvelopeReference[] = [];
  usingProfileFilter = true;
  searchValue$: Observable<any>;
  searchTerm$ = new BehaviorSubject<string>('');
  filters: any;
  keyword = '';
  showTooltip$ = new BehaviorSubject(false);
  selectionSnackbarRef: MatSnackBarRef<any>;
  @ViewChild(MatSort, { static: true }) matSort: MatSort;
  payloadPageable =  {page: 0, sort: ['createdAt'], size: 10, keyword: ''};

  constructor(private store: Store,
              private selectionSnackbar: SelectionSnackbarService,
              private envelopeReferenceFormControlService: EnvelopeReferenceFormControlService,
              private router: Router
  ) {
    super();
  }


  ngOnInit() {
    this.store.dispatch(fetchEnvelopeReferences({payload: this.payloadPageable}));
    this.store.select(selectEnvelopeReferencesList).pipe(takeUntil(this.destroyed$)).subscribe((response: any) => {
      if (response && !arraysOfObjectsHaveSameElements(this.envelopeReferences.data,response.contents)) {
        this.envelopeReferences = new MatTableDataSource(response.contents || []);
        console.log(response)
        this.pagination = {
          page: response.page,
          pageSize: response.pageSize,
          total: response.total
        }
        this.listModificationId = [];
        this.selection.clear();

      }
    });
    this.setupSelectionSnackbar();
    this.searchTerm$.pipe(skip(1)).pipe(distinctUntilChanged(), debounceTime(800)).pipe(takeUntil(this.destroyed$))
      .subscribe((value) => {
        this.keyword = value;
        this.loadEnvelopeReference(
          {page: 0, sort: [`${this.matSort.active || 'createdAt'},${this.matSort.direction || 'asc'}`], size: 10, keyword: value}
        )
      });
  }

  paginationUpdated(pagination: any) {
    this.loadEnvelopeReference(
      { page:  pagination.pageIndex - 1, sort: [`${this.matSort.active || 'createdAt'},${this.matSort.direction || 'asc'}`], size: 10, keyword:this.keyword}
    )
  }

  getDatasource(): MatTableDataSource<any> {
    return this.envelopeReferences
  }

  searchTermChanged(searchTerm: string) {
    this.searchTerm$.next(searchTerm);

  }


  loadEnvelopeReference(payload: any){
    this.store.dispatch(fetchEnvelopeReferences({payload}));
    this.store.select(selectEnvelopeReferencesList).pipe(takeUntil(this.destroyed$)).subscribe((response: any) => {
      if (response ) {
        this.envelopeReferences = new MatTableDataSource(response.contents || []);
        this.pagination = {
          page: response.page,
          pageSize: response.size,
          total: response.total
        }
        this.listModificationId = [];
        this.selection.clear();
      }
    });
  }
  setupSelectionSnackbar() {
    this.store.select(selectSelectionOpened).pipe(distinctUntilChanged(), takeUntil(this.destroyed$)).subscribe(open => {
      if (open) {

        const config: ISelectionSnackbar = {
          edit: this.envelopeReferenceFormControlService.isCanEdit, delete: this.envelopeReferenceFormControlService.isCanDelete,

          doEdit: () => this.requestToModify(),

          doDelete: () =>  this.attemptToDelete(),

          getSelectionItem: () => this.store.select(selectFilteredModified).pipe(map(y => y.filteredModified)),

          message: 'envelope_reference.list.selected_re'
        };
        this.selectionSnackbarRef = this.selectionSnackbar.open(config);
        this.selectionSnackbarRef.afterDismissed().pipe(takeUntil(this.destroyed$))
          .subscribe(() => {
            this.selection.clear();
            this.store.dispatch(setSelectionPanel({active: false}));
          });

      } else {
        this.selectionSnackbarRef?.dismiss();
      }
    });
    this.selection.changed.pipe(takeUntil(this.destroyed$)).subscribe(selectionChanges => {
      if (selectionChanges.removed.length > 0) {
        this.listModificationId = this.listModificationId.filter(x => selectionChanges.removed.map(y => y.id).includes(x) == false);
      }
      if (selectionChanges.added.length > 0) {
        this.listModificationId = this.listModificationId.concat(selectionChanges.added.map(x => x.id));
      }
      this.store.dispatch(entriesBatchOfModification({
        modificationBatchId: this.listModificationId,
        erList: this.envelopeReferenceList
      }));

    });
  }

  handleConsult(row: any) {
    const queryParams = {
      id:row.id,
      mode: EnvelopeReferenceFormUpdateMode.UPDATE_SINGLE
    }
    this.router
      .navigate([appRoute.cxmEnvelopeReference.navigateToModify], { queryParams})
      .then();
  }

  editOne(id: string) {
    const queryParams = {
      id: id,
      mode: EnvelopeReferenceFormUpdateMode.UPDATE_SINGLE
    }
    this.router
      .navigate([appRoute.cxmEnvelopeReference.navigateToModify], { queryParams})
      .then();
  }
  editBatch(ids: string[] ) {
    const queryParams = {
      id:ids.join(','),
      mode: EnvelopeReferenceFormUpdateMode.UPDATE_MULTIPLE
    }
    this.router
      .navigate([appRoute.cxmEnvelopeReference.navigateToModify], { queryParams})
      .then();
  }

  requestToModify() {
    if (!this.envelopeReferenceFormControlService.isCanEdit) {
      return;
    }
    if (this.listModificationId?.length === 1) {
      this.editOne( this.listModificationId[0]);
    } else if (this.listModificationId?.length > 1) {
      this.editBatch( this.listModificationId );
    }
  }

  attemptToDelete() {
    this.envelopeReferenceFormControlService.confirmDelete().then(e => {
      if (e) {
        this.requestToDeleteEnvelopeReference();

      }
    });
  }


  requestToDeleteEnvelopeReference() {
    if (!this.envelopeReferenceFormControlService.isCanDelete) {
      return;
    }
    this.store.dispatch(deleteEnvelopeReferences({ ids : this.listModificationId }));
    this.loadEnvelopeReference({payload: this.payloadPageable});
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
    if (this.selectionSnackbarRef) {
      this.selectionSnackbarRef.dismiss();
      this.store.dispatch(setSelectionPanel({ active: false }));
    }
  }


  ngAfterViewInit(): void {
    this.matSort.sortChange.pipe(takeUntil(this.destroyed$))
      .subscribe((sort) => {
        this.loadEnvelopeReference(
          {page: 0, sort:[`${sort.active},${sort.direction}`], size: 10, keyword: this.keyword}
        )
      });
  }


}
