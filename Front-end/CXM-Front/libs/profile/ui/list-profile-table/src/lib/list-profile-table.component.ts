import {
  AfterViewInit,
  Component,
  Input,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Store } from '@ngrx/store';
import {
  clearStateProfile,
  loadProfileListFilterChangeAction,
  ProfileModel,
  ProfileStorageService,
  redirectToUpdateProfile,
  selectProfileList,
  unloadProfilelist,
} from '@cxm-smartflow/profile/data-access';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { Sort } from '@angular/material/sort';
import {
  SortDirection,
  UserManagement,
} from '@cxm-smartflow/shared/data-access/model';
import { PageEvent } from '@angular/material/paginator';
import {
  CanAccessibilityService,
  CanModificationService,
  CanVisibilityService,
  SnackBarService,
  UserUtil,
} from '@cxm-smartflow/shared/data-access/services';
import { takeUntil } from 'rxjs/operators';
import {UserFormControlService} from "@cxm-smartflow/user/data-access";

@Component({
  selector: 'cxm-smartflow-list-profile-table',
  templateUrl: './list-profile-table.component.html',
  styleUrls: ['./list-profile-table.component.scss'],
})
export class ListProfileTableComponent
  implements OnInit, OnDestroy, AfterViewInit {
  tableColumns = [
    'name',
    'displayName',
    'lastModified',
    'clientName',
    'createdAt',
    'actions',
  ];

  dataSource$ = new BehaviorSubject<ProfileModel[]>([]);
  pageIndex = 1;
  pageSize = 10;
  total = 0;
  destroyed$ = new Subject<void>();
  sortAction = 'lastModified';
  sortDirection: SortDirection = 'desc';
  canEdit = UserManagement.EDIT_PROFILE;
  @Input() isHasFilter = false;
  isAdmin$: Observable<boolean>;

  constructor(
    private store: Store,
    private canAccess: CanAccessibilityService,
    private canModify: CanModificationService,
    private canVisible: CanVisibilityService,
    private snackbarService: SnackBarService,
    private profileStorage: ProfileStorageService,
    private userFormControl: UserFormControlService
  ) {
    // load value from the local storage.
    const storage = this.profileStorage.getProfileListStorage();

    this.pageIndex = storage?.page || 1;
    this.pageSize = storage?.pageSize || 10;
    this.sortAction = storage?.sortByField || 'lastModified';
    // @ts-ignore
    this.sortDirection = storage?.sortDirection || 'desc';
    this.isAdmin$ = of(UserUtil.isAdmin());

    // remove client name from table layout if user not admin.
    if (!UserUtil.isAdmin()) {
      this.tableColumns = this.tableColumns.filter(
        (column: string) => column !== 'clientName'
      );
    }
  }

  ngOnInit(): void {
    this.store
      .select(selectProfileList)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((response) => {
        this.dataSource$.next(this.mapPrivilege(response.contents || []));
        this.pageIndex = response?.page;
        this.pageSize = response?.pageSize;
        this.total = response?.total;
      });
  }

  ngOnDestroy(): void {
    this.store.dispatch(unloadProfilelist());
    this.store.dispatch(clearStateProfile());
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  sortEvent(sort: Sort): void {
    this.storeSortState(sort.active, sort.direction);
    this.store.dispatch(
      loadProfileListFilterChangeAction({
        sortDirection: sort.direction,
        sortByField: sort.active,
      })
    );
  }

  navigateToModify(name: string, profileId: number, createdBy: string, clientId?: number) {
    this.store.dispatch(redirectToUpdateProfile({ id: profileId, clientId }));
  }

  paginationChange(page: PageEvent) {
    this.storePaginationState(page.pageIndex, page.pageSize);
    this.store.dispatch(
      loadProfileListFilterChangeAction({
        page: page.pageIndex,
        pageSize: page.pageSize,
      })
    );
  }

  storePaginationState(page: number, pageSize: number): void {
    const data = {
      ...this.profileStorage.getProfileListStorage(),
      page,
      pageSize,
    };
    this.profileStorage.setProfileListStorage(data);
  }

  storeSortState(sortByField: string, sortDirection: string): void {
    const data = {
      ...this.profileStorage.getProfileListStorage(),
      page: this.pageIndex,
      pageSize: this.pageSize,
      sortByField,
      sortDirection,
    };
    this.profileStorage.setProfileListStorage(data);
  }

  showTooltip(id: string, content: string): string {
    const el = document.querySelector(id);
    return el ? (el.scrollWidth > el.clientWidth ? content : '') : '';
  }

  private mapPrivilege(profiles: ProfileModel[]): ProfileModel[] {
    return profiles.map((e) => {
      const can: string[] = [];
      const canMangeUser = this.canVisible.hasVisibility(UserManagement.CXM_USER_MANAGEMENT, UserManagement.EDIT_PROFILE, e?.ownerId || 0, true);

      if (canMangeUser) {
        can.push(UserManagement.EDIT_PROFILE);
      }

      return {
        ...e,
        can,
      };
    });
  }

  ngAfterViewInit(): void {
    if (!UserUtil.isAdmin()) {
      // scaling or responsive layout profile table.
      document.querySelectorAll(
        '.col-name, .col-role, .col-modified, .col-created, .clientName'
      ).forEach((item) => {
        item?.setAttribute('style', 'width: 25% !important;');
      });
    }
  }
}
