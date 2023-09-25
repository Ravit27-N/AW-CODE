import {
  Component,
  HostListener,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatSort, Sort } from '@angular/material/sort';
import { DefaultRoleCriteria, RoleCriteria } from '../../../core';
import { MatDialog } from '@angular/material/dialog';
import { UserAdminService } from '../../../core/service/user-admin.service';
import { Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { AwConfirmMessageService, AwSnackbarService } from '../../../shared';
import { Router } from '@angular/router';

@Component({
  selector: 'app-feature-user-list',
  templateUrl: './feature-user-list.component.html',
  styleUrls: ['./feature-user-list.component.scss'],
})
export class FeatureUserListComponent implements OnInit, OnDestroy {
  tableColumnHeader: Array<string> = [
    'no',
    'fullName',
    'name',
    'email',
    'active',
    'created',
    'action',
  ];
  dataSource: Array<any> = [];
  total: number;
  subscription = new Subscription();
  @ViewChild(MatSort, { static: true }) matSort: MatSort;
  defaultCriteria: DefaultRoleCriteria = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: 'createdTimestamp',
    sortDirection: 'desc',
  };

  userCriteria: RoleCriteria = {
    defaultCriteria: this.defaultCriteria,
    filter: '',
  };

  constructor(
    public dialog: MatDialog,
    private userAdminService: UserAdminService,
    private awSnackbarService: AwSnackbarService,
    private router: Router,
    private awConfirmMessageService: AwConfirmMessageService,
  ) {}

  async ngOnInit(): Promise<void> {
    if (localStorage.getItem('user-list')) {
      this.userCriteria = JSON.parse(localStorage.getItem('user-list'));
    }
    await this.fetchUsers();
    await this.sortColumnTable();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  async fetchUsers(): Promise<void> {
    localStorage.setItem('user-list', JSON.stringify(this.userCriteria));
    try {
      await this.userAdminService
        .get(
          this.userCriteria.defaultCriteria.pageIndex,
          this.userCriteria.defaultCriteria.pageSize,
          this.userCriteria.defaultCriteria.sortDirection,
          this.userCriteria.defaultCriteria.sortByField,
          this.userCriteria.filter,
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

  async addUser(): Promise<void> {
    await this.router.navigateByUrl('/admin/administration/users/add');
  }

  @HostListener('window:beforeunload', ['$event'])
  refreshPage(): void {
    localStorage.removeItem('user-list');
  }

  async searchValueChange(result: string): Promise<void> {
    this.userCriteria.filter = result;
    this.resetPagination(1, 10);
    await this.fetchUsers();
  }

  async sortColumnTable(): Promise<void> {
    const subscription: Subscription = this.matSort.sortChange
      .pipe(debounceTime(300))
      .subscribe((sortChanged: Sort) => {
        // Update sorting criteria
        this.userCriteria.defaultCriteria.sortByField = sortChanged.active;
        this.userCriteria.defaultCriteria.sortDirection = sortChanged.direction;
        // Fetch demands with updated sort criteria
        this.fetchUsers();
      });
    this.subscription.add(subscription);
  }

  getUserRowNumber(index: number): number {
    return (
      (this.userCriteria.defaultCriteria.pageIndex - 1) *
        this.userCriteria.defaultCriteria.pageSize +
      (index + 1)
    );
  }

  async slideToggle(id: string, active: boolean): Promise<void> {
    await this.userAdminService
      .changeStatus(id, !active)
      .toPromise()
      .then(() => {
        this.fetchUsers();
        this.showSuccessMessage('User changed status successfully');
      })
      .catch(() => {
        this.showErrorMessage('Cannot communicate with server');
      });
  }

  getUserRow(candidateField: any) {
    return { row: candidateField };
  }

  async pageChangeEvent(event: any): Promise<void> {
    try {
      this.resetPagination(event.pageIndex, event.pageSize);
      await this.fetchUsers();
    } catch (e) {
      this.showErrorMessage('Can not fetch users when apply new pagination');
    }
  }

  async edit(userId: string): Promise<void> {
    await this.router.navigate(['/admin/administration/users/update'], {
      queryParams: { userId },
    });
  }

  async delete(userId: string): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Discard all changes?',
        message: 'Are you sure you want to delete this user?',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();
    if (confirmed) {
      this.userAdminService
        .delete(userId)
        .toPromise()
        .then(() => {
          this.fetchUsers();
          this.showSuccessMessage('Delete user successfully');
        })
        .catch(() => {
          this.showErrorMessage(
            'Delete user error cannot communicate with server',
          );
        });
    }
  }

  private showErrorMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  resetPagination(pageIndex: number, pageSize: number): void {
    this.userCriteria.defaultCriteria.pageIndex = pageIndex;
    this.userCriteria.defaultCriteria.pageSize = pageSize;
  }
}
