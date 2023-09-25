import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  CheckedListKeyValue,
  CriteriaStorage,
  FlowCriteriaSessionService,
  loadUserInService,
  selectAllUsers,
} from '@cxm-smartflow/flow-traceability/data-access';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { CustomAngularMaterialUtil, FalsyUtil, Sort } from '@cxm-smartflow/shared/utils';
import { UserModel } from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-button-dropdown-users',
  templateUrl: './button-dropdown-users.component.html',
  styleUrls: ['./button-dropdown-users.component.scss'],
})
export class ButtonDropdownUsersComponent implements OnInit, OnDestroy {

  @Input() customCssClass = '';
  @Output() userChange = new EventEmitter<string[]>();
  usingUserFilter = false;

  // Template properties.
  users: CheckedListKeyValue[] = [];
  sortDirection = Sort.DESC;
  // Unsubscribe properties.
  private _destroy$ = new Subject<boolean>();
  // Validation properties.
  private _usersStorage?: any[] = [];
  allUsers: CheckedListKeyValue[] = [];

  /**
   * Constructor
   */
  constructor(
    private _store: Store,
    private _storageService: FlowCriteriaSessionService
  ) {
    // Initial criteria from localstorage.
    this._initUserDropdownCriteria();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * On init
   */
  ngOnInit(): void {
    // Fetch all users.
    this._store.dispatch(loadUserInService());
    this.usingUserFilter = this._usersStorage ? this._usersStorage.length > 0 : false;
    // Select all users from store.
    this._store.select(selectAllUsers).pipe(takeUntil(this._destroy$), filter(state => !FalsyUtil.isEmptyObject(state)))
      .subscribe((res) => {
          this.users = res.map((item: UserModel) => ({
            value: item.id,
            key: item.firstName + ' ' + item.lastName,
            other: item.username,
            checked: this._usersStorage && this._usersStorage.includes(item.id || 0),
          }));
          this.allUsers = this.users;
          const uniqueUsers = new Map<string, CheckedListKeyValue>();
          this.users.forEach((user) => {
            if (!uniqueUsers.has(user.key)) {
              uniqueUsers.set(user.key, user);
            }
          });
          this.users = Array.from(uniqueUsers.values());

          this.usingUserFilter = (this.users && this.users.some(x => x.checked));
          // After API response, sort the user base on default sort-direction.
          this._sortUsers(this.sortDirection);
      });
  }


  ngOnDestroy(): void {
    // Unsubscribe all subscribers.
    this._destroy$.next(true);
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  /**
   * Select checkbox
   * @param event
   */
  selectCheckbox(event: string[]): void {
    let userEventValues: string[] = [];
    this.allUsers.filter(user => event.includes(user.value)).forEach(user => {
        userEventValues = userEventValues.concat(Array.from(this.allUsers)
          .filter((userFilter) => userFilter.key === user.key).map(u => u.value));
    })
    this.saveInStorage(userEventValues);
    this.userChange.emit(userEventValues);
    this.usingUserFilter = (this.users && this.users.some(x => x.checked));
  }

  /**
   * - Clear state in localstorage
   * - Uncheck all checkboxes
   */
  resetUserDropdownForm(): void {
    this.sortDirection = Sort.DESC;
    this._sortUsers(this.sortDirection);
    // Clear state in localstorage.
    this.saveInStorage([]);
    // Uncheck all checkboxes.
    this.users = this.users.map((item) => ({ ...item, checked: false }));
    // Emit value to subscriber.
    this.userChange.emit([]);
    this.usingUserFilter = this.users && this.users.some(x => x.checked);
  }

  /**
   * Add custom class to current component.
   */
  addCustomCssClass(): void {
    document.querySelector('.users-custom-dropdown')?.classList?.add(this.customCssClass);
  }

  /**
   * - Change sort-direction icon
   * - Sort user base on sort-direction
   */
  changeDirection(): void {
    this.sortDirection = this.sortDirection === Sort.ASC? Sort.DESC : Sort.ASC;
    this._sortUsers(this.sortDirection);
  }

  /**
   * - Clear old criteria in localstorage property
   * - Set new criteria in localstorage
   * @param data
   */
   saveInStorage(data: string[]): void {
    // Clear old criteria in localstorage property.
    this._usersStorage = [];

    // Set new criteria in localstorage.
    this._storageService.setFlowCriteria(this._mappingStorageData(data, this._storageService.getFlowCriteria()));
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  /**
   * Initial criteria from localstorage.
   */
  private _initUserDropdownCriteria(): void {
    this._usersStorage = this._storageService.getFlowCriteria() ?.criteriaParams?.users;
  }

  /**
   * Sort user base on sort-direction
   * @param sortDirection
   */
  private _sortUsers(sortDirection: string): void {
    const sorted = this.users.sort((a: CheckedListKeyValue, b: CheckedListKeyValue) => a.key?.localeCompare(b.key));
    this.users = sortDirection === Sort.ASC? sorted : sorted.reverse();
  }

  private _mappingStorageData(users?: string[], data?: CriteriaStorage): CriteriaStorage {
    return {
      ...data,
      criteriaParams: { ...data?.criteriaParams, users: users },
    };
  }

  mainMenuOpen(): void {
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(): void {
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }
}
