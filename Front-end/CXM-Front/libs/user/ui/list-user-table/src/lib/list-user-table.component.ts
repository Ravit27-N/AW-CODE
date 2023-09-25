import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSnackBarRef } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TableSelection } from '@cxm-smartflow/shared/common-typo';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { ISelectionSnackbar, SelectionSnackbarService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import {
  deleteUser,
  entriesBatchOfModificationUser,
  ISingleEditedUser,
  loadClientDivision,
  loadClientService,
  loadOrganizationProfile,
  loadServices,
  loadUserList,
  navigateToEditBatchUserForm,
  navigateToUpdateSingleUser,
  ProfileAssigned,
  selectClients,
  selectFilteredModifiedUser, selectIsSearchBoxHasError, selectIsSearchBoxHasFilter,
  selectListOfUser,
  selectOrganizationDivisions,
  selectOrganizationProfiles,
  selectOrganizationServices,
  selectSelectionOpened,
  selectUserListFilters,
  selectUsersList,
  setSelectionPanel, switchReturnAddressLevel,
  UserFormControlService, UserList, UserModel
} from '@cxm-smartflow/user/data-access';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, pluck, skip, takeUntil } from 'rxjs/operators';

import { userTableFiltering } from './list-user-table-remember';

@Component({
  selector: 'cxm-smartflow-list-user-table',
  templateUrl: './list-user-table.component.html',
  styleUrls: ['./list-user-table.component.scss']
})

export class ListUserTableComponent extends TableSelection implements OnInit, OnDestroy, AfterViewInit {

  // USER LIST TABLE PROPERTY
  tableColumns = ['select', 'firstName', 'lastName', 'email', 'client', 'division','service', 'userProfiles'];//updated
  users = new MatTableDataSource<UserModel>();

  listModificationUserId: string[] = []; // multiple or single modification purpose and it assign by userSelected and rowSelected
  userList: ISingleEditedUser[] = [];


  destroyed$ = new Subject<boolean>();
  searchTerm$ = new BehaviorSubject<string>('');
  pagination = {page: 1, pageSize: 0, total: 0};
  userProfileList$: Observable<[]>; // TODO: Load profile list from API
  isAdmin = UserUtil.isAdmin();
  userClientList$: any;//add news
  userDivisionList$: any;//add news
  userServiceList$: any;//add news
  isAdmin$: Observable<boolean>;//add news
  filters: any;
  usingProfileFilter = false;
  usingClientFilter = false;//add news
  usingDivisionFilter = false; //add news
  usingServiceFilter = false;//add news
  usingAdminUserFilter = false;

  showTooltip$ = new BehaviorSubject(false);
  idClients:number;//add news
  idDivisions:number;//add news
  selectionSnackbarRef: MatSnackBarRef<any>;
  searchValue$: Observable<any>;
  searchBoxHasError$: Observable<any>;
  @ViewChild(MatSort, { static: true }) matSort: MatSort;


  ngOnInit(): void {

    // CONNECT TO API FOR PREFILL USER LIST TABLE
    this.store.select(selectUsersList).pipe(takeUntil(this.destroyed$)).subscribe((response: UserList) => {
      if(response){
        this.users = new MatTableDataSource(response.contents || []);

        this.pagination = {
          page: response.page,
          pageSize: response.pageSize,
          total: response.total
        }

        this.listModificationUserId = [];
        this.selection.clear();

        this.showTooltip$.next(response.total <= 0 && this.searchTerm$.value?.trim()?.length > 0);
      }
    });


    // Load clients profiles ;add new
    this.userProfileList$ = this.store.select(selectOrganizationProfiles);
    this.store.dispatch(loadOrganizationProfile(({ clientIds: 0})));

        // Load clients
   if (this.isAdmin) {
    this.userClientList$ = this.store.select(selectClients);
    this.store.dispatch(loadClientService(({ clientId: undefined })));
     }
       // Load divisions
       this.userDivisionList$ = this.store.select(selectOrganizationDivisions);
       if (!UserUtil.isAdmin()) {
     this.store.dispatch(loadClientDivision(({ clientIds: 'true'})));
       }else{
        this.store.dispatch(loadClientDivision(({ clientIds: 0})));
       }
         //add new
           // Load services
           this.userServiceList$ = this.store.select(selectOrganizationServices);
    if (!UserUtil.isAdmin()) {
      this.store.dispatch(loadServices(({ clientIds: 'true'})));
    }else{
      this.store.dispatch(loadServices(({ clientIds: 0 ,divisionIds:0})));
    }



    this.store.select(selectUserListFilters).pipe(takeUntil(this.destroyed$)).subscribe(filters => {
      this.filters = filters;

      // update sort filter
      if (this.matSort) {
        if(filters){
          const { sortByField, sortDirection } = filters;
          this.matSort.active = sortByField;
          this.matSort.direction = sortDirection;
        }
      }
    });

    // Setup search box
    this.searchTerm$.pipe(skip(1)).pipe(distinctUntilChanged(), debounceTime(800)).pipe(takeUntil(this.destroyed$))
      .subscribe((value) => {
        this.filters = { ...this.filters, filter: value, page: 1 };
        this.doLoadUserList(this.filters);
      });

    this.store.select(selectListOfUser).pipe(takeUntil(this.destroyed$), pluck('userList')).subscribe(v => this.userList = v);

    this.setupSelectionSnackbar(); // Setup snackbar for multi-select user to delete

    // Restore filter
    const shouldUseFilter = userTableFiltering.shouldRestoreFilter();
    if(shouldUseFilter) {
      this.filters = shouldUseFilter;
      this.usingProfileFilter = this.filters.profileIds?.length > 0;
      this.usingClientFilter = this.filters.clientIds?.length > 0;//add news
      this.usingDivisionFilter = this.filters.divisionIds?.length > 0;//add news
      this.usingServiceFilter = this.filters.ServiceIds?.length > 0;//add news
      this.usingAdminUserFilter = this.filters.userType?.length > 0;
      this.doLoadUserList(shouldUseFilter);
    } else {
      this.doLoadUserList({
        page: 1,
        pageSize: 10,
        sortDirection: 'asc',
        sortByField: 'email'
      });
    }

    this.searchValue$ = this.store.select(selectIsSearchBoxHasFilter);
    this.searchBoxHasError$ = this.store.select(selectIsSearchBoxHasError);
    this.store.dispatch(switchReturnAddressLevel({ returnAddressLevel: '' }));
  }


  doLoadUserList(filters: {
    page: number;
    pageSize: number;
    sortByField: string;
    sortDirection: string;
    filter?: string;
    clientIds?: string[],//add news
    divisionIds?: string[],//add news
    ServiceIds?: string[],//add news
    profileIds?: string[],
    userType?: string[]
  }): void {
    userTableFiltering.rememberFilter(filters);
    this.store.dispatch(loadUserList({ params: { ...filters } }));
  }


  /**
   * This method used to format output list name of profiles
   * @param profiles refer to {@link ProfileAssigned}
   */
  showProfile(profiles: ProfileAssigned[]) {
    return profiles.map(p => p.name).join(', ');
  }

  navigateTo(updatedUserId: string) {
    if (this.userFormControlService.isCanEdit) {
      this.store.dispatch(navigateToUpdateSingleUser({ updatedUserId }));
    }
  }

  setupSelectionSnackbar() {
    this.store.select(selectSelectionOpened).pipe(distinctUntilChanged(), takeUntil(this.destroyed$)).subscribe(open => {
      if (open) {

        const config: ISelectionSnackbar = {
          edit: this.userFormControlService.isCanEdit, delete: this.userFormControlService.isCanDelete,

          doEdit: () => this.requestToModifyUsers(),

          doDelete: () => this.attemptToDeleteUsers(),

          getSelectionItem: () => this.store.select(selectFilteredModifiedUser).pipe(map(y => y.filteredModifiedUser)),

          message: 'user.list.selected_user'
        };

        this.selectionSnackbarRef = this.selectionSnackbar.open(config);

        this.selectionSnackbarRef.afterDismissed().pipe(takeUntil(this.destroyed$))
          .subscribe(() => {
            this.selection.clear();// clear selection
            this.store.dispatch(setSelectionPanel({ active: false }));
          });

      } else {
        this.selectionSnackbarRef?.dismiss();
      }
    });


    // track checkbox changed
    this.selection.changed.pipe(takeUntil(this.destroyed$)).subscribe(selectionChanges => {

      if (selectionChanges.removed.length > 0) {
        this.listModificationUserId = this.listModificationUserId.filter(x => selectionChanges.removed.map(y => y.id).includes(x) == false);
      }

      if (selectionChanges.added.length > 0) {
        this.listModificationUserId = this.listModificationUserId.concat(selectionChanges.added.map(x => x.id));
      }


      const modificationUsers = (this.userList?.filter(e => this.listModificationUserId?.find(k => k === e?.id)).map(e => e?.email));
      if (modificationUsers?.length > 0) {
        this.userFormControlService.checkIsCanModify(<string[]>modificationUsers);
        this.userFormControlService.checkIsCanEdit(<string[]>modificationUsers);
        this.userFormControlService.checkIsCanDelete(<string[]>modificationUsers);
      }

      this.store.dispatch(entriesBatchOfModificationUser({
        modificationBatchUserId: this.listModificationUserId,
        userList: this.userList
      }));

    });
  }

  requestToModifyUsers() {
    if (!this.userFormControlService.isCanEdit) {
      return;
    }

    // Redirect to single or multiple modify
    if (this.listModificationUserId?.length === 1) {
      this.store.dispatch(navigateToUpdateSingleUser({ updatedUserId: this.listModificationUserId[0] }));
    } else if (this.listModificationUserId?.length > 1) {
      this.store.dispatch(navigateToEditBatchUserForm({ editBatchUsers: this.listModificationUserId }));
    }
  }

  attemptToDeleteUsers() {
    this.userFormControlService.confirmDelete().then(e => {
      if (e) {
        this.requestToDeleteUsers();
      }
    });
  }

  requestToDeleteUsers() {
    if (!this.userFormControlService.isCanDelete) {
      return;
    }

    this.store.dispatch(deleteUser({ userIds: this.listModificationUserId }));
  }

  searchTermChanged(searchTerm: string) {
    this.searchTerm$.next(searchTerm);
  }


  paginationUpdated(pagination: any) {
    this.doLoadUserList({ ...this.filters, page: pagination.pageIndex });
  }

  onFilterChanged(profileFilter: any) {
    this.usingProfileFilter = profileFilter.length > 0;

    // TODO: Filter using profile filters
    this.doLoadUserList({ ...this.filters, page: 1, profileIds: profileFilter });
  }
  //add new
  onFilterChangedClient(clientFilter: any) {
    this.usingClientFilter = clientFilter.length > 0;
    this.idClients=clientFilter;

    //get division, service et profile by client ids
  if(this.usingClientFilter){
      this.store.dispatch(loadClientDivision(({ clientIds: this.idClients})));
      this.store.dispatch(loadServices(({ clientIds: this.idClients ,divisionIds:0})));
      this.store.dispatch(loadOrganizationProfile(({ clientIds: this.idClients})));
  }
      this.store.dispatch(loadClientDivision(({ clientIds: 0})));
      this.store.dispatch(loadServices(({ clientIds: 0 ,divisionIds:this.idDivisions})));
      this.store.dispatch(loadOrganizationProfile(({ clientIds: 0})));

    // TODO: Filter using client filters
    this.doLoadUserList({ ...this.filters, page: 1, clientIds: clientFilter });

  }
  onFilterChangedDiv(divisionFilter: any) {
    this.usingDivisionFilter = divisionFilter.length > 0;
    this.idDivisions=divisionFilter;
    //add new for service filter by division ID
    if(this.isAdmin){
    this.store.dispatch(loadServices(({ clientIds: this.idClients ,divisionIds: this.idDivisions})));
  }
    if(!this.isAdmin && this.usingDivisionFilter )
    {
      this.store.dispatch(loadServices(({ clientIds: 0 ,divisionIds: this.idDivisions})));
    }else  this.store.dispatch(loadServices(({ clientIds: 'true'})));
    // TODO: Filter using divison filters
    this.doLoadUserList({ ...this.filters, page: 1, divisionIds: divisionFilter });
  }

  onFilterChangedServ(serviceFilter: any) {
    this.usingServiceFilter = serviceFilter.length > 0;

    // TODO: Filter using service filters
    this.doLoadUserList({ ...this.filters, page: 1, serviceIds: serviceFilter });
  }

  onAdminFilterChange(userType: string[]) {
    this.usingAdminUserFilter = userType.length > 0;
    this.doLoadUserList({ ...this.filters, page: 1, userType: userType  })
  }

  // Required implement for selection
  getDatasource(): MatTableDataSource<any> {
    return this.users;
  }

  restoreProfileFunction() {
    const restoreFilter = userTableFiltering.shouldRestoreFilter();
    return restoreFilter ? restoreFilter.profileIds : [];
  }
  //add new
  restoreClientFunction() {
    const restoreFilter = userTableFiltering.shouldRestoreFilter();
    return restoreFilter ? restoreFilter.clientIds : [];
  }
  restoreDivisionFunction() {
    const restoreFilter = userTableFiltering.shouldRestoreFilter();
    return restoreFilter ? restoreFilter.divisionIds : [];
  }
  restoreServiceFunction() {
    const restoreFilter = userTableFiltering.shouldRestoreFilter();
    return restoreFilter ? restoreFilter.serviceIds : [];
  }
  restoreUserAdminFunction() {
    const restoreFilter = userTableFiltering.shouldRestoreFilter();
    return restoreFilter ? restoreFilter.userType : [];
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();

    // clean up snackbar
    if (this.selectionSnackbarRef) {
      this.selectionSnackbarRef.dismiss();
      this.store.dispatch(setSelectionPanel({ active: false }));
    }
  }


  ngAfterViewInit(): void {
    // Setup sort filter
    this.matSort.sortChange.pipe(takeUntil(this.destroyed$))
      .subscribe(sort => this.doLoadUserList({
        ...this.filters,
        sortByField: sort.active,
        sortDirection: sort.direction
      }));
  }

  constructor(private store: Store,
              private selectionSnackbar: SelectionSnackbarService,
              private userFormControlService: UserFormControlService
  ) {
    super();
    //add new
    this.isAdmin$ = of(UserUtil.isAdmin());
    if (!UserUtil.isAdmin()) {
      this.tableColumns = this.tableColumns.filter(
        (column: string) => column !== 'client'
      );

    }
  }

}
