import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSnackBarRef } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { approvalDocActions, ApprovalDocModel, approvalDocSelector } from '@cxm-smartflow/approval/data-access';
import { TableSelection } from '@cxm-smartflow/shared/common-typo';
import {
  ICommentPayload,
  ISelectionCommentSnackbar,
  SelectionSnackbarService
} from '@cxm-smartflow/shared/ui/comfirmation-message';
import { appRoute } from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { Observable, ReplaySubject, Subject } from 'rxjs';
import { pluck, take, takeUntil, withLatestFrom } from 'rxjs/operators';
import { API_TYPE } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-espace-validation-consult-page',
  templateUrl: './espace-validation-consult-page.component.html',
  styleUrls: ['./espace-validation-consult-page.component.scss']
})
export class EspaceValidationConsultPageComponent extends TableSelection implements OnInit, OnDestroy, AfterViewInit {

  tableColumns: string[] = ['checkbox', 'docName', 'recipient', 'channel', 'subChannel', 'actions'];

  documents = new MatTableDataSource<ApprovalDocModel>();
  flowsName$ = new ReplaySubject<string>(1);
  selectionSnackbarRef: MatSnackBarRef<any>;

  selectedItems$ = new ReplaySubject<any[]>(1);

  filters: any;

  // Validation properties.
  destroy$ = new Subject<boolean>();

  apiType = API_TYPE;

  @ViewChild(MatSort, { static: true }) matSort: MatSort;

  private paginationSubject$ = new ReplaySubject(1);
  pagination$: Observable<any>;

  getDatasource(): MatTableDataSource<any> {
    return this.documents;
  }

  ignoreSelection(data: any[]): any[] {
    return data.filter(x => x._editable === true);
  }

  private setupSelectionSnackbar() {

    this.store.select(approvalDocSelector.selectApprovalDocPanel).pipe(takeUntil(this.destroy$))
      .subscribe(open => {
        if (open) {
          const config: ISelectionCommentSnackbar = {
            alter: true,
            main: true,
            validateComment: true,
            alterName: 'espace.selection.button_refuse',
            mainName: 'espace.selection.button_validate',

            doMain: (payload) => this.requestToValidate(payload),
            doAlter: (pyload) => this.requestToRefuse(pyload),
            getSelectionItem: () => this.selectedItems$,

            message: 'espace.selection.flow_document_counter',
            comment: 'espace.selection.comment_placeholder'
          };

          this.selectionSnackbarRef = this.selectionSnackbar.openComment(config);

          this.selectionSnackbarRef.afterDismissed().pipe(takeUntil(this.destroy$))
            .subscribe(() => {
              this.selection.clear();
              this.store.dispatch(approvalDocActions.closeApprovalDocPanel());
            });
        } else {
          this.selectionSnackbarRef?.dismiss();
        }
      });


    // track chechbox changed
    this.selection.changed.pipe(takeUntil(this.destroy$)).pipe(withLatestFrom(this.selectedItems$))
      .subscribe(([selectionChanged, lastSelectedItems]) => {

        if (selectionChanged.removed.length > 0) {
          const left = lastSelectedItems.filter(x => selectionChanged.removed.includes(x) == false);
          this.selectedItems$.next(left);
        }

        if (selectionChanged.added.length > 0) {
          const left = lastSelectedItems.concat(selectionChanged.added.map(x => x));
          this.selectedItems$.next(left);
        }

      });


    this.selectedItems$.asObservable().pipe(takeUntil(this.destroy$))
      .pipe(withLatestFrom(this.store.select(approvalDocSelector.selectApprovalDocPanel)))
      .subscribe(([selectedItem, open]) => {
        if (open && selectedItem.length <= 0) {
          this.store.dispatch(approvalDocActions.closeApprovalDocPanel());
        }

        if (!open && selectedItem.length > 0) {
          this.store.dispatch(approvalDocActions.openApprovalDocPanel());
        }
      });
  }


  requestToValidate(payload: ICommentPayload) {
    this.activateRoute.params.pipe(take(1), pluck('id')).subscribe(flowId => {
      this.selectedItems$.asObservable().pipe(take(1)).subscribe(items => {
        this.store.dispatch(approvalDocActions.submitApproveDoc({
          docs: items,
          comment: payload.comment,
          flowId: Number(flowId)
        }));
      });
    });
  }

  requestToRefuse(payload: ICommentPayload) {
    this.activateRoute.params.pipe(take(1), pluck('id')).subscribe(flowId => {
      this.selectedItems$.asObservable().pipe(take(1)).subscribe(items => {
        this.store.dispatch(approvalDocActions.submitRefuseDoc({
          docs: items,
          comment: payload.comment,
          flowId: Number(flowId)
        }));
      });
    });
  }

  paginationUpdate(event: any) {
    const id = this.activateRoute.snapshot.params.id;
    this.store.dispatch(approvalDocActions.filterFlowApproveChanged({
      filters: {
        ...this.filters,
        page: event.pageIndex,
        pageSize: event.pageSize
      }, id
    }));
  }


  handleReturn() {
    this.router.navigateByUrl(appRoute.cxmApproval.navigateToValidateFlow);
  }

  ngOnInit(): void {
    this.setupSelectionSnackbar();

    this.pagination$ = this.paginationSubject$.asObservable();

    this.store.select(approvalDocSelector.selectApprovalDoclist).pipe(takeUntil(this.destroy$))
      .subscribe((items) => {
        this.documents = new MatTableDataSource(items.contents);
        this.selectedItems$.next([]);
        this.flowsName$.next(items.flowName);
        this.paginationSubject$.next({ page: items.page, pageSize: items.pageSize, total: items.total });
      });

    this.store.select(approvalDocSelector.selectApprovalDocFilter).pipe(takeUntil(this.destroy$))
      .subscribe(filters => {
        this.filters = { ...filters };

        if (this.matSort) {
          const { sortByField, sortDirection } = filters;
          this.matSort.active = sortByField;
          this.matSort.direction = sortDirection;
        }
      });

    const id = this.activateRoute.snapshot.params.id;
    const name = this.activateRoute.snapshot.params.name;
    this.store.dispatch(approvalDocActions.setFlowName({ name }));
    this.store.dispatch(approvalDocActions.loadApprovalDocumentList({ id, filters: this.filters }));
  }

  downloadFile(fileId: string, filename: string) {
    if (fileId && filename) {
      this.store.dispatch(approvalDocActions.downloadFile({ fileId: fileId, filename: filename }));
    }
  }

  ngOnDestroy(): void {
    this.selectionSnackbarRef?.dismiss();
    this.store.dispatch(approvalDocActions.unloadApproveDoc());

    this.destroy$.next(true);
    this.destroy$.complete();
  }

  ngAfterViewInit(): void {
    this.matSort.sortChange.pipe(takeUntil(this.destroy$)).subscribe(sort => {
      this.filters.sortByField = sort.active;
      this.filters.sortDirection = sort.direction;

      const id = this.activateRoute.snapshot.params.id;
      this.store.dispatch(approvalDocActions.filterFlowApproveChanged({ filters: { ...this.filters }, id }));
    });
  }

  constructor(private readonly store: Store, private activateRoute: ActivatedRoute, private router: Router,
              private selectionSnackbar: SelectionSnackbarService,) {
    super();
  }

}
