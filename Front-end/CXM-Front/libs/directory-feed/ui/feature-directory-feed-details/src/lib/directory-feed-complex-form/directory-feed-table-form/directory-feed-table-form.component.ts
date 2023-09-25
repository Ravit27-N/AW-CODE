import {
  AfterContentChecked,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import {
  CheckExistValueParamModel, destroyDirectoryFields,
  DirectoryFeedField,
  DirectoryFeedFormMode,
  DirectoryFeedService,
  DirectoryFeedValue,
  FeedField,
  keepDirectoryFeedLocked,
  ListDirectoryFeedValue,
  selectDirectoryFieldKeyLabel,
  submitDirectoryFeedValue,
  UpdatedDirectoryFeed,
} from '@cxm-smartflow/directory-feed/data-access';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import {
  BehaviorSubject,
  forkJoin,
  Observable,
  of,
  ReplaySubject,
  Subject,
} from 'rxjs';
import { ErrorValidationDirectiveModel } from '@cxm-smartflow/directory-feed/util';
import {
  DirectoryFeedSelection,
  DirectoryFeedSelectionServiceService,
} from '../directory-feed-selection';
import { MatSnackBarRef } from '@angular/material/snack-bar';
import { takeUntil } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { SortDirection } from '@cxm-smartflow/flow-traceability/data-access';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';
import { TableDirectoryFeedSelection } from '../table-directory-feed-selection';
import { CellRecordModified } from '../cell-record-modified';
import { DirectoryFeedErrorHandler } from '../directory-feed-error-handler';
import {UserProfileUtil, UserUtil} from "@cxm-smartflow/shared/data-access/services";
import {DirectoryManagement} from "@cxm-smartflow/shared/data-access/model";

@Component({
  selector: 'cxm-smartflow-directory-feed-table-form',
  templateUrl: './directory-feed-table-form.component.html',
  styleUrls: ['./directory-feed-table-form.component.scss'],
})
export class DirectoryFeedTableFormComponent
  extends TableDirectoryFeedSelection
  implements OnInit, OnChanges, AfterContentChecked, OnDestroy {
  @Input() feedField: DirectoryFeedField;
  @Input() feedValue: ListDirectoryFeedValue;
  @Input() formMode: DirectoryFeedFormMode = DirectoryFeedFormMode.VIEW;
  isShowAction = false;
  isSharedDirectory$ = new BehaviorSubject<boolean>(false);

  @Output() sorting = new EventEmitter<any>();

  datasource$ = new MatTableDataSource<DirectoryFeedValue>([]);
  disableForm$ = new BehaviorSubject(false);

  columns: FeedField[] = [];
  columnDisplayed: string[] = ['custom_checkbox'];

  cellRecordValues = new Map<string, any>();
  cellOriginalRecordObjects = new Map<
    string,
    {
      value: string;
      fieldOrder: string;
      id: number;
      directoryFieldId: number;
    }
  >();

  cellRecordModified = new CellRecordModified();
  cellErrors = new DirectoryFeedErrorHandler(this.translate);

  enableClose$ = new ReplaySubject<boolean>(1);
  isDelete$ = new ReplaySubject<boolean>(1);
  isValidate$ = new ReplaySubject<boolean>(1);
  isCancel$ = new ReplaySubject<boolean>(1);
  selectedItems$ = new BehaviorSubject<any[]>([]);
  selectedChange$ = new BehaviorSubject<number>(0);
  selectedDelete$ = new BehaviorSubject<number>(0);
  lineSelected: number[] = [];

  snackbarOpen = false;
  snackbarRef: MatSnackBarRef<any>;
  destroyed$ = new Subject<boolean>();
  directoryId: number;

  sortAction = '';
  sortDirection: SortDirection = 'desc';
  private checkValueExist: any;

  @ViewChild(MatTable, { static: false }) scrollableTable: MatTable<any>;
  @ViewChild('tableContainer', { static: false }) tableContainerRef: ElementRef;

  _canDeleteLine = UserProfileUtil.getInstance().canModify({
    func: DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    priv: DirectoryManagement.DELETE_DATA_DIRECTORY_FEED,
    checkAdmin: true,
    ownerId: UserUtil.getOwnerId(),
  });

  constructor(
    private cdref: ChangeDetectorRef,
    private directoryFeedSelectionService: DirectoryFeedSelectionServiceService,
    private store$: Store,
    private confirmationService: ConfirmationMessageService,
    private translate: TranslateService,
    private directoryFeedService: DirectoryFeedService
  ) {
    super();
    this.isDelete$.next(true);
    this.enableClose$.next(true);
  }

  hideShowActionColumn(event?: any) {
    const target =
      event || (document.getElementById('directoryFeedForm') as HTMLElement);

    if (!target) {
      return;
    }
    const isScroll = target.clientWidth < target.scrollWidth;
    // Check if the scroll position has reached the end of the horizontal axis
    const isScrollAtEnd =
      Math.round(target.scrollLeft + target.clientWidth) >=
      target.scrollWidth - 50;
    if (isScroll) {
      this.isShowAction = !isScrollAtEnd;
    }
  }

  getDatasource(): MatTableDataSource<any> {
    return this.datasource$;
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.disableForm$.next(this.formMode === DirectoryFeedFormMode.VIEW);

    if (this.feedField && this.feedField.directoryId !== 0) {
      this.isSharedDirectory$.next(this.feedField.shareDirectory);
      this.directoryId = this.feedField.directoryId;
      this.feedField.fields.slice()
        ?.sort((a, b) => (a.fieldOrder > b.fieldOrder ? 1 : -1))
        .forEach((field, index) => {
          if (!this.columns.some((item) => item.field === field.field)) {
            this.columnDisplayed.push(field.field);
            this.columns.push(field);
          }
        });

      if (!this.columnDisplayed.includes('action')) {
        this.columnDisplayed.push('action');
      }
    }

    if (
      this.feedValue &&
      this.feedValue.page !== 0 &&
      this.feedValue.pageSize !== 0
    ) {
      const feedValueContent = this.feedValue?.contents;

      const keepOriginal$ = this.keepOriginalRecord(
        this.feedField,
        feedValueContent
      );
      const initCellChange$ = this.initCellRecords(
        this.feedField,
        feedValueContent
      );

      forkJoin([keepOriginal$, initCellChange$])
        .pipe(takeUntil(this.destroyed$))
        .subscribe(() => {
          this.datasource$ = new MatTableDataSource(feedValueContent);
        });
    }
  }

  ngOnInit(): void {
    this.disableForm$.subscribe((isDisable) => {
      if (isDisable) {
        const index = this.columnDisplayed.indexOf('custom_checkbox');
        if (index !== -1) {
          this.columnDisplayed.splice(index, 1);
          this.columnDisplayed.push('first_empty_column');
        }
      } else {
        const index = this.columnDisplayed.indexOf('first_empty_column');
        if (index !== -1) {
          this.columnDisplayed.splice(index, 1);
        }
      }
    });

    // track checkbox changed
    this.selection.changed
      .pipe(takeUntil(this.destroyed$))
      .subscribe((selectionChanges) => {
        this.initSelectionFunctionality();

        const selectItems = selectionChanges.source?.selected;
        this.selectedItems$.next(selectItems);
        this.selectedDelete$.next(selectItems.length);

        this.lineSelected = selectItems.map(
          (item) => item.lineNumber
        ) as number[];

        if (!this.snackbarOpen && selectItems.length > 0) {
          const data: DirectoryFeedSelection = {
            enableClose: () => this.enableClose$,
            isDelete: () => this.isDelete$,
            isValidate: () => this.isValidate$,
            isCancel: () => this.isCancel$,
            selectedModified: () => this.selectedChange$,
            selectedDelete: () => this.selectedDelete$,
            onDelete: () => this.onDelete(this.selectedItems$.value),
            onValidate: () => this.onValidate(),
            onCancel: () => this.onCancel(),
          };

          this.snackbarRef = this.directoryFeedSelectionService.onOpen(data);
          this.snackbarOpen = true;

          this.snackbarRef
            .afterDismissed()
            .pipe(takeUntil(this.destroyed$))
            .subscribe(() => {
              this.selection.clear();
              this.snackbarOpen = false;
            });
        }

        if (selectItems.length === 0) {
          this.directoryFeedSelectionService.onClose();
        }
        // keep directory feed to locked when the user have change or selected some data.
        this.store$.dispatch(
          keepDirectoryFeedLocked({ isLocked: selectItems.length > 0 })
        );
      });

    this.store$
      .select(selectDirectoryFieldKeyLabel)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((label) => {
        this.sortAction = label;
      });
  }

  onDelete(payload: { lineNumber: number; [property: string]: any }[]) {
    Promise.all([
      this.translate
        .get('directory.directory_feed_details_delete_row_message')
        .toPromise(),
    ]).then(([message]) => {
      this.confirmationService
        .showConfirmationPopup({
          ...message,
          icon: 'close',
          type: 'Warning',
        })
        .subscribe((confirm) => {
          if (confirm) {
            const deleteLines = payload.map((item) => item.lineNumber);
            this.store$.dispatch(
              submitDirectoryFeedValue({
                directoryId: this.directoryId,
                data: { deleted: deleteLines },
              })
            );
          } else {
            this.onCancel();
          }
        });
    });
  }

  onValidate() {
    if (this.isHasCellError()) {
      return;
    }

    const updated = this.getLineNumberAndCellChange();
    this.store$.dispatch(
      submitDirectoryFeedValue({
        directoryId: this.directoryId,
        data: { updated },
      })
    );
    this.cellRecordModified.clear();
  }

  getLineNumberAndCellChange(): UpdatedDirectoryFeed[] {
    return this.cellRecordModified.getLineNumberAndCellChange(
      this.selectedItems$.value
    );
  }

  onCancel() {
    this.cellOriginalRecordObjects.forEach((value, key) => {
      this.cellRecordValues.set(key, value?.value);
    });

    this.cellRecordModified.clear();
    this.selection.clear();
  }

  ngAfterContentChecked(): void {
    this.cdref.detectChanges();

    this.hideShowActionColumn();
  }

  generateCellName(lineNumber: number, column: number): string {
    return this.cellRecordModified.generateCellName(lineNumber, column);
  }

  /**
   * Convert boolean string to boolean type.
   * @param value
   */
  toBoolean(value: any): boolean {
    if (typeof value === 'boolean') {
      return value;
    }
    return value?.toLowerCase() === 'true';
  }

  initCellRecords(
    feedField: DirectoryFeedField,
    contents: DirectoryFeedValue[]
  ): Observable<any> {
    contents.forEach((row: DirectoryFeedValue) => {
      feedField.fields.forEach((column: FeedField, colIndex: number) => {
        const cellValue = row.values?.find(
          (value) => value.directoryFieldId === column.id
        );
        if (cellValue) {
          const cellName = this.generateCellName(
            Number(row.lineNumber),
            colIndex
          );

          const cellChange = this.cellRecordModified.get(cellName);
          this.cellRecordValues.set(
            cellName,
            cellChange?.value || cellValue.value
          );
        }
      });
    });

    return of(true);
  }

  keepOriginalRecord(
    feedField: DirectoryFeedField,
    contents: DirectoryFeedValue[]
  ): Observable<boolean> {
    contents.forEach((row: DirectoryFeedValue) => {
      feedField.fields.forEach((column: FeedField, colIndex: number) => {
        const cellValue = row.values?.find(
          (value) => value.directoryFieldId === column.id
        );
        if (cellValue) {
          const cellName = this.generateCellName(
            Number(row.lineNumber),
            colIndex
          );
          this.cellOriginalRecordObjects.set(cellName, cellValue);
        }
      });
    });

    return of(true);
  }

  onError(
    error: ErrorValidationDirectiveModel,
    lineNumber: number,
    colIndex: number
  ) {
    const cellName = this.generateCellName(lineNumber, colIndex);
    const cellIndex = this.getCellIndex(cellName);
    const column = this.columns[Number(cellIndex)];
    this.cellErrors.set(cellName, error, column);
  }

  private getCellIndex(cellName: string) {
    return cellName.split('_')[1];
  }

  onModelChange(
    lineNumber: number,
    columnIndex: number,
    cellValue: any,
    row: any
  ) {
    const cellName = this.generateCellName(lineNumber, columnIndex);
    const cellOriginal = this.cellOriginalRecordObjects.get(cellName);

    this.cellRecordValues.set(cellName, cellValue);

    if (cellOriginal) {
      const typeStringChange =
        typeof cellValue === 'string' &&
        JSON.stringify(cellValue) !== JSON.stringify(cellOriginal.value);
      const typeBooleanChange =
        typeof cellValue === 'boolean' &&
        cellValue != this.toBoolean(cellOriginal.value);

      if (typeStringChange || typeBooleanChange) {
        const cellModified = {
          ...cellOriginal,
          value: cellValue,
        };
        this.cellRecordModified.set(cellName, cellModified);

        const isSelectionItemExist = this.selectedItems$.value?.some(
          (item) => item?.lineNumber === row?.lineNumber
        );
        if (!isSelectionItemExist) {
          this.singleToggle(row);
        }
        const cellIndex = this.getCellIndex(cellName);
        const column = this.columns[Number(cellIndex)];
        if (column.key && cellValue) {
          const directoryValueId = row?.values[column.fieldOrder - 1].id;
          this.validateKey(column, cellValue, cellName, directoryValueId);
        }
      } else {
        this.cellRecordModified.delete(cellName);
        if (!this.cellRecordModified.isModified(lineNumber)) {
          this.singleToggle(row);
        }
      }
    } else {
      this.cellRecordModified.delete(cellName);
    }
    this.selectedChange$.next(this.getLineNumberAndCellChange().length);

    this.initSelectionFunctionality();
  }

  validateKey(
    column: FeedField,
    value: string,
    cellName: string,
    directorValueId: number
  ) {
    const fieldId = this.feedField.fields[column.fieldOrder - 1].id;
    const checkExistValueParamModel: CheckExistValueParamModel = {
      id: directorValueId,
      fieldId,
      value,
    };

    clearTimeout(this.checkValueExist);
    this.checkValueExist = setTimeout(() => {
      this.directoryFeedService
        .checkExistValue(this.directoryId, checkExistValueParamModel)
        .subscribe((isExist: boolean) => {
          if (isExist) {
            this.cellErrors.set(cellName, { duplicate: isExist }, column);
          }
        });
    }, 800);
  }

  isHasCellError(): boolean {
    const lineChanged = this.getLineNumberAndCellChange().map(
      (item) => item.lineNumber
    );
    return [...this.cellErrors.entries()]
      .filter((item) =>
        lineChanged.includes(Number(this.slitLineNumber(item[0])))
      )
      .map((item) => item[1])
      .some((cell) => cell.value);
  }

  slitLineNumber(cellChange: string) {
    return Number(cellChange.split('_')[0]);
  }

  initSelectionFunctionality() {
    if (this.cellRecordModified.size > 0) {
      this.enableClose$.next(false);
      this.isDelete$.next(false);
      this.isValidate$.next(true);
      this.isCancel$.next(true);
    } else {
      this.enableClose$.next(true);
      this.isDelete$.next(true);
      this.isValidate$.next(false);
      this.isCancel$.next(false);
    }
  }

  @HostListener('window:resize', ['$event'])
  onResizeScreen() {
    this.hideShowActionColumn();
  }

  onTableSorting(event: any) {
    this.sorting.emit(event);
  }

  ngOnDestroy(): void {
    this.snackbarRef?.dismiss();
    this.destroyed$?.unsubscribe();
  }
}
