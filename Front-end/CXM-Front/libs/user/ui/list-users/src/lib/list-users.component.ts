import { Component, HostListener, OnDestroy, ViewChild } from '@angular/core';
import {
  exportUsers,
  exportUsersCsv,
  ISingleEditedUser,
  loadUserList,
  UserFormControlService, UserService, validateBatchUserCSV
} from '@cxm-smartflow/user/data-access';
import { Store } from '@ngrx/store';
import { Subject, BehaviorSubject } from 'rxjs';
import { formatDate } from '@angular/common';
import { UserManagement } from '@cxm-smartflow/shared/data-access/model';
import { UserProfileUtil } from '@cxm-smartflow/shared/data-access/services';

@Component({
  selector: 'cxm-smartflow-list-users',
  templateUrl: './list-users.component.html',
  styleUrls: ['./list-users.component.scss']
})
export class ListUsersComponent implements OnDestroy {
  // Validation properties.
  destroy$ = new Subject<boolean>();
  _canCreateCreate = UserProfileUtil.canAccess(UserManagement.CXM_USER_MANAGEMENT, UserManagement.CREATE_USER, true);
  _canImportUser =
    UserProfileUtil.canAccess(UserManagement.CXM_USER_MANAGEMENT, UserManagement.CREATE_USER, true) &&
    UserProfileUtil.canAccess(UserManagement.CXM_USER_MANAGEMENT, UserManagement.MODIFY_USER, true);
  @ViewChild('fileUpload') fileUpload: any;

  users: ISingleEditedUser[];
  submittedCsvExport$ = new BehaviorSubject<boolean>(false);

  loadUserList() {
    this.store.dispatch(loadUserList({
      params: {
        page: 1,
        pageSize: 10,
        sortDirection: 'asc',
        sortByField: 'email'
      }
    }));
  }

  createUserEvent() {
    if (!this.userFormControlService.isCanCreate) return;
    this.userFormControlService.navigateToCreate();
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  @HostListener("window:beforeunload", ["$event"])
  unloadHandler() {
    this.userFormControlService.removeModificationUsersCriteriaStorage()
  }

  constructor(private store: Store,
              public userFormControlService: UserFormControlService,
              public userService : UserService
               ) {
                this.submittedCsvExport$.next(false);
  }

  onFileSelected($event: any): void {
    const files = Array.from($event.target.files);

    if (files.length === 1) {
      this.store.dispatch(validateBatchUserCSV({ files }));
    }

    this.fileUpload.nativeElement.value = '';
  }
  
  exportUsers(): void {
    const storedFilters = localStorage.getItem('rem-list-use-table');
    const filters = storedFilters ? JSON.parse(storedFilters) : {};
 
  
    const profileIds: number[] = filters.profileIds || [];
    const userType: string[] = filters.userType || [];
    const clientIds: number[] = filters.clientIds || [];
    const divisionIds: number[] = filters.divisionIds || [];
    const serviceIds: number[] = filters.serviceIds || [];
    const filter = filters.filter || '';
    const filename = "Exp_utilisateur_"+formatDate(new Date(), 'yyyyMMddhhmmss', 'fr').toString();
  
    const exportData: exportUsersCsv = {
      profileIds,
      userType,
      clientIds,
      divisionIds,
      serviceIds,
      filter,
      filename,
    };
  
    this.store.dispatch(exportUsers({ services: [{ data: exportData }], submittedCsvExport$: this.submittedCsvExport$ }));
  }

}
