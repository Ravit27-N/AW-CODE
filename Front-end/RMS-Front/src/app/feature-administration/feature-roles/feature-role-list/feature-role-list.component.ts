import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { MatSort, Sort } from '@angular/material/sort';
import {
  DefaultRoleCriteria,
  InterviewTemplateModel,
  RoleCriteria,
  RoleService,
  UserRoleModel,
} from '../../../core';
import { MatDialog } from '@angular/material/dialog';
import { AwConfirmMessageService, AwSnackbarService } from '../../../shared';
import { debounceTime } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-feature-role-list',
  templateUrl: './feature-role-list.component.html',
  styleUrls: ['./feature-role-list.component.scss'],
})
export class FeatureRoleListComponent implements OnInit, OnDestroy {
  tableColumnHeader: Array<string> = ['no', 'name', 'action'];
  dataSource: Array<any> = [];
  total: number;
  subscription = new Subscription();
  @ViewChild(MatSort, { static: true }) matSort: MatSort;
  defaultCriteria: DefaultRoleCriteria = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: 'name',
    sortDirection: 'asc',
  };
  roleCriteria: RoleCriteria = {
    defaultCriteria: this.defaultCriteria,
    filter: '',
  };

  constructor(
    public dialog: MatDialog,
    private userRoleService: RoleService,
    private awSnackbarService: AwSnackbarService,
    private awConfirmMessageService: AwConfirmMessageService,
    private router: Router,
  ) {}

  async ngOnInit(): Promise<void> {
    if (localStorage.getItem('roles-list')) {
      this.roleCriteria = JSON.parse(localStorage.getItem('roles-list'));
    }
    await this.fetchRoles();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  async fetchRoles(): Promise<void> {
    localStorage.setItem('roles-list', JSON.stringify(this.roleCriteria));
    try {
      await this.userRoleService
        .get(
          this.roleCriteria.defaultCriteria.pageIndex,
          this.roleCriteria.defaultCriteria.pageSize,
          this.roleCriteria.filter,
        )
        .toPromise()
        .then((result: any) => {
          this.dataSource = result.contents;
          this.total = result.total;
        });
    } catch (error) {
      this.showErrorMessage('Cannot communicate with the server');
    }
  }

  async addRoles(): Promise<void> {
    await this.router.navigateByUrl('admin/administration/roles/add');
  }

  async searchValueChange(result: string): Promise<void> {
    this.roleCriteria.filter = result;
    await this.fetchRoles();
  }

  async sortColumnTable(sort: Sort): Promise<void> {
    this.matSort.active = sort.active;
    this.matSort.direction = sort.direction;

    const subscription: Subscription = this.matSort.sortChange
      .pipe(debounceTime(300))
      .subscribe((sortChanged: Sort) => {
        // Update sorting criteria
        this.roleCriteria.defaultCriteria.sortByField = sortChanged.active;
        this.roleCriteria.defaultCriteria.sortDirection = sortChanged.direction;
        // Fetch demands with updated sort criteria
        this.fetchRoles();
      });
    this.subscription.add(subscription);
  }

  getCandidateRowNumber(index: number): number {
    return (
      (this.roleCriteria.defaultCriteria.pageIndex - 1) *
        this.roleCriteria.defaultCriteria.pageSize +
      (index + 1)
    );
  }

  getRole(role: any) {
    return { row: role };
  }

  async pageChangeEvent(event: any): Promise<void> {
    try {
      this.roleCriteria.defaultCriteria.pageIndex = event.index;
      this.roleCriteria.defaultCriteria.pageSize = event.pageSize;
      await this.fetchRoles();
    } catch (e) {
      this.showErrorMessage('Can not fetch roles when apply new pagination');
    }
  }

  edit(interviewTemplateModel: InterviewTemplateModel): void {
    console.log({ interviewTemplateModel });
    // TODO
  }

  async delete(userRole: UserRoleModel): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Remove Role?',
        message: `Are you sure to permanently delete this role?`,
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        await this.userRoleService.delete(userRole.id).toPromise();
        this.showSuccessMessage('The role has been delete successfully.');
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchRoles();
      }
    }
  }

  resetPagination(pageIndex: number = 1, pageSize: number = 10): void {
    this.roleCriteria.defaultCriteria.pageIndex = pageIndex;
    this.roleCriteria.defaultCriteria.pageSize = pageSize;
  }

  private showErrorMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message,
    });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }
}
