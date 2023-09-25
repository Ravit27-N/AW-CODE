import {
  enqueRoute,
  filterClientBoxChange,
  getAllClient,
  ListProfileCriteria,
  loadProfileListFilterChangeAction,
  ProfileStorageService,
  ProfileTabs,
  redirectCreateProfile,
  searchTermChange,
  selectClientCriteria,
  selectHasOptionFilter,
  selectIsFilterError,
  updateFilterOption
} from '@cxm-smartflow/profile/data-access';
import { Component, HostListener, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { CanAccessibilityService, CanVisibilityService, UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { UserManagement } from '@cxm-smartflow/shared/data-access/model';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-list-profiles',
  templateUrl: './list-profiles.component.html',
  styleUrls: ['./list-profiles.component.scss'],
})
export class ListProfilesComponent implements OnInit {
  canCreateProfile$ = new BehaviorSubject<boolean>(false);
  createdBy = new BehaviorSubject('');
  destroyed$ = new Subject<void>();
  isAdmin = UserUtil.isAdmin();

  datasource$: Observable<ListProfileCriteria[]>;
  isFilterError$: Observable<boolean>;
  hasFilter$: Observable<boolean>;
  restoreSelectedClientIds$ = new BehaviorSubject<number[]>([]);
  searchBoxValue$ = new BehaviorSubject<string>('');
  canListProfile$ = new BehaviorSubject<boolean>(false);

  userManagement: typeof UserManagement = UserManagement;

  constructor(
    private store: Store,
    private translate: TranslateService,
    private router: Router,
    private canVisible: CanVisibilityService,
    private profileStorage: ProfileStorageService,
    private canAccess: CanAccessibilityService
  ) {
    this.canCreateProfile$.next(canAccess.canAccessible(UserManagement.CXM_USER_MANAGEMENT, UserManagement.CREATE_PROFILE, true));
    this.canListProfile$.next(canVisible.hasVisibility(UserManagement.CXM_USER_MANAGEMENT, UserManagement.LIST_PROFILE, UserUtil.getOwnerId(), true));
  }

  ngOnInit(): void {
    this.store.dispatch(enqueRoute(this.router, ProfileTabs.listProfile));

    const storage = this.profileStorage.getProfileListStorage();
    this.searchBoxValue$.next(storage?.filter || '');
    this.hasFilter$ = this.store.select(selectHasOptionFilter);
    this.isFilterError$ = this.store.select(selectIsFilterError);
    this.store.dispatch(updateFilterOption({filter: storage.filter || '', clientIds: storage.clientIds || []}));
    this.store.dispatch(
      loadProfileListFilterChangeAction({
        page: storage?.page || 1,
        pageSize: storage?.pageSize || 10,
        sortByField: storage?.sortByField || 'lastModified',
        sortDirection: storage?.sortDirection || 'desc',
        clientIds: storage?.clientIds || [],
        filter: storage?.filter || ''
      })
    );

    if (this.isAdmin) {
      this.store.dispatch(getAllClient());
      this.datasource$ = this.store.select(selectClientCriteria).pipe(filter(e => e));
      this.restoreSelectedClientIds$.next(storage?.clientIds || []);
    }
  }

  onCreateNewProfile(): void {
    if (this.canCreateProfile$.value) {
      this.store.dispatch(redirectCreateProfile());
    }
  }

  @HostListener('window:beforeunload', ['$event'])
  unloadHandler() {
    this.profileStorage.removeProfileListStorage();
  }

  searchTermChanged(filter: string): void {
    this.searchBoxValue$.next(filter);
    this.profileStorage.setProfileListStorage({
      ...this.profileStorage.getProfileListStorage(),
      filter,
      page: 1,
      pageSize: 10,
    });
    this.store.dispatch(searchTermChange({ filter }));
  }

  filterClientBoxChange(clientIds: number[]) {
    this.profileStorage.setProfileListStorage({
      ...this.profileStorage.getProfileListStorage(),
      clientIds,
      page: 1,
      pageSize: 10,
    });
    this.store.dispatch(filterClientBoxChange({ clientIds }));
  }
}
