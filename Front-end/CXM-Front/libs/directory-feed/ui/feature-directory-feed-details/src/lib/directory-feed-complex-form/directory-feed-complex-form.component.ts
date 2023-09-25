import {
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import {
  destroyDirectoryFields,
  DirectoryFeedField,
  DirectoryFeedFormMode,
  DirectoryFeedService,
  keepDirectoryFieldKeyLabel,
  ListDirectoryFeedValue,
  loadDirectoryFeedDetails,
  selectDirectoryFeedDetail, selectDirectoryFields,
} from '@cxm-smartflow/directory-feed/data-access';
import { HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  takeUntil,
} from 'rxjs/operators';
import {UserProfileUtil, UserUtil} from "@cxm-smartflow/shared/data-access/services";
import {DirectoryManagement} from "@cxm-smartflow/shared/data-access/model";

@Component({
  selector: 'cxm-smartflow-directory-feed-complex-form',
  templateUrl: './directory-feed-complex-form.component.html',
  styleUrls: ['./directory-feed-complex-form.component.scss'],
})
export class DirectoryFeedComplexFormComponent
  implements OnInit, OnChanges, OnDestroy {
  @Input() directoryId: number;

  feedField$ = new BehaviorSubject<DirectoryFeedField>({
    directoryId: 0,
    directoryName: '',
    fields: [],
    shareDirectory: false,
  });
  feedValue$ = new BehaviorSubject<ListDirectoryFeedValue>({
    contents: [],
    page: 0,
    pageSize: 0,
    total: 0,
    isLoad: false,
  });

  formMode = DirectoryFeedFormMode.MODIFY;

  page$ = new BehaviorSubject<number>(1);
  pageSize$ = new BehaviorSubject<number>(10);
  total$ = new BehaviorSubject<number>(0);
  sortByField$ = new BehaviorSubject('');
  sortDirection$ = new BehaviorSubject('');
  filter: string;
  searchValue$ = new Subject<string>();
  loading$ = new BehaviorSubject(false);
  isShowErrorTooltip = new BehaviorSubject(false);
  destroy$ = new Subject<boolean>();
  isAdmin = UserUtil.isAdmin();
  _canDelete = UserProfileUtil.getInstance().canModify({
    func: DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    priv: DirectoryManagement.DELETE_DATA_DIRECTORY_FEED,
    checkAdmin: true,
    ownerId: UserUtil.getOwnerId(),
  });

  _canModify = UserProfileUtil.getInstance().canModify({
    func: DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    priv: DirectoryManagement.MODIFY_DATA_DIRECTORY_FEED,
    checkAdmin: true,
    ownerId: UserUtil.getOwnerId(),
  });

  constructor(
    private directoryFeedService: DirectoryFeedService,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.searchValue$
      .pipe(distinctUntilChanged(), debounceTime(800))
      .pipe(takeUntil(this.destroy$))
      .subscribe((searchValue: string) => {
        this.fetchDirectoryFeedValue(
          this.page$.value,
          this.pageSize$.value,
          searchValue,
          this.sortByField$.value,
          this.sortDirection$.value
        );
      });

    this.store
      .select(selectDirectoryFeedDetail)
      .pipe(
        takeUntil(this.destroy$),
        filter((data) => data.isLoad)
      )
      .subscribe((feedValues) => {
        this.loading$.next(!feedValues.isLoad);
        this.feedValue$.next(feedValues);
        this.page$.next(feedValues.page);
        this.pageSize$.next(feedValues.pageSize);
        this.total$.next(feedValues.total);
        this.isShowErrorTooltip.next(
          !feedValues.total && this.filter?.length > 0
        );
      });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.directoryId !== 0) {
      this.fetchDirectoryField()
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          this.loading$.next(true);
          this.fetchDirectoryFeedValue(
            this.page$.value,
            this.pageSize$.value,
            this.filter,
            this.sortByField$.value,
            this.sortDirection$.value
          );
        });
    }
  }

  ngOnDestroy(): void {
    this.feedField$?.unsubscribe();
    this.feedValue$?.unsubscribe();
    this.page$?.unsubscribe();
    this.pageSize$?.unsubscribe();
    this.total$?.unsubscribe();
    this.sortByField$?.unsubscribe();
    this.sortDirection$?.unsubscribe();
    this.loading$?.unsubscribe();
    this.searchValue$?.unsubscribe();
    this.destroy$.unsubscribe();
    this.store.dispatch(destroyDirectoryFields());
  }

  onSearch(filter: string) {
    this.filter = filter;
    this.searchValue$.next(filter);
  }

  onSorting(event: any) {
    const { active, direction } = event;
    this.sortByField$.next(active);
    this.sortDirection$.next(direction);
    this.loading$.next(true);
    this.fetchDirectoryFeedValue(
      this.page$.value,
      this.pageSize$.value,
      this.filter,
      this.sortByField$.value,
      this.sortDirection$.value
    );
  }

  onPageChange(pagination: {
    pageSize: number;
    pageIndex: number;
    length: number;
  }) {
    this.loading$.next(true);
    this.fetchDirectoryFeedValue(
      pagination.pageIndex,
      pagination.pageSize,
      this.filter,
      this.sortByField$.value,
      this.sortDirection$.value
    );
  }

  private fetchDirectoryField(): Observable<any> {
    this.loading$.next(true);
    this.store
      .select(selectDirectoryFields)
      .pipe(
        takeUntil(this.destroy$),
        filter((field) => field.isLoaded === true)
      )
      .subscribe(
        (response: DirectoryFeedField) => {
          this.feedField$.next({...response});
          this.validatePermission(response.shareDirectory);
          const labelKey = response.fields
            .filter((field) => field.key)
            .map((field) => field.field)[0];
          this.store.dispatch(keepDirectoryFieldKeyLabel({ label: labelKey }));
        },
        (error: HttpErrorResponse) => {
          this.loading$.next(false);
        }
      );
    return of(true);
  }

  validatePermission(isDirectoryShared: boolean) {
    if (!this.isAdmin) {
      if (isDirectoryShared) {
        this.formMode = DirectoryFeedFormMode.VIEW;
      } else if (!this._canModify && !this._canDelete) {
        this.formMode = DirectoryFeedFormMode.VIEW;
      }
    } else {
      this.formMode = DirectoryFeedFormMode.MODIFY;
    }
  }

  private fetchDirectoryFeedValue(
    page: number,
    pageSize: number,
    filter: string,
    sortByField: string,
    sortDirection: string
  ) {
    this.store.dispatch(
      loadDirectoryFeedDetails({
        directoryId: this.directoryId,
        page,
        pageSize,
        sortDirection,
        sortByField,
        filter,
      })
    );
  }
}
