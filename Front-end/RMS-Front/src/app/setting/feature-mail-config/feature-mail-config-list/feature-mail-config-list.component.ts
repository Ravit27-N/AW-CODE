import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import {
  DefaultCriteria,
  MailStatuschangeModel,
  MailStatusChangeService,
  PAGINATION_SIZE,
  ProjectCriteria,
} from '../../../core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { Subject } from 'rxjs';
import { KeyValue } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { AwConfirmMessageService, AwSnackbarService } from '../../../shared';
import { DialogviewmailstatusComponent } from '../../MailStatusChange';

@Component({
  selector: 'app-feature-mail-config-list',
  templateUrl: './feature-mail-config-list.component.html',
  styleUrls: ['./feature-mail-config-list.component.scss'],
})
export class FeatureMailConfigListComponent implements OnInit {
  id: string;
  subject: string;
  body: string;
  status = '';
  statusSelect = '';
  search: string;
  active: string;
  length: number;
  displayedColumns: string[] = [
    '#',
    'title',
    'From',
    'To',
    'Status Candidate',
    'Mail Template',
    'Status',
    'Action',
  ];
  dataSource: MatTableDataSource<MailStatuschangeModel>;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  defaultCriteria: DefaultCriteria = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: '',
    sortDirection: 'desc',
  };
  filterListCriteria: ProjectCriteria = {
    defaultCriteria: { ...this.defaultCriteria },
    filter: '',
    startDate: '',
    endDate: '',
    status: [],
    option: 0,
  };
  totalMailConfigs = 0;
  paginationSize = PAGINATION_SIZE;

  content: MailStatuschangeModel[];
  filterStatuses: Array<KeyValue<string, string>> = [
    { key: '', value: 'All' },
    { key: 'Active', value: 'Active' },
    { key: 'Inactive', value: 'Inactive' },
    { key: 'Deleted', value: 'Deleted' },
  ];
  private detectChange$ = new Subject<boolean>();

  constructor(
    public dialog: MatDialog,
    private service: MailStatusChangeService,
    private awConfirmMessageService: AwConfirmMessageService,
    private awSnackbarService: AwSnackbarService,
  ) {}

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource(this.content);
    this.fetchMailConfigs();
  }

  applyFilter(): void {
    if (this.search.length >= 3 || this.search === '' || this.search === null) {
      this.detectChange$.next(true);
      this.paginator.firstPage();
    }
  }

  async updateMailConfig(row): Promise<void> {
    if (!row.deleted) {
      try {
        this.service.updateActive(row.id, !row.active);
        this.showSuccessMessage(
          'The mail-config has been updated successfully.',
        );
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        this.fetchMailConfigs();
      }
    }
  }

  delete(row): void {
    // const dialogRef = this.dialog.open(ComfirmDailogComponent, {
    //   data: row,
    //   width: '450px',
    // });
    // dialogRef.afterClosed().subscribe((result) => {
    //   if (result) {
    //     this.service.restoreMailConfig(row.id, true).subscribe(
    //       () => {
    //         this.message.showSuccess(
    //           'Delete Sucess',
    //           'Delete Mail Configuration',
    //         );
    //         this.detectChange$.next(true);
    //       },
    //       () => {
    //         this.message.showError('Delete Error', 'Delete Mail Configuration');
    //       },
    //     );
    //   }
    // });
  }

  async restoreMailConfig(row) {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Restore mail-config',
        message:
          'Are you sure to restore this mail-config?\nIf Okay, this will also restore records that are related to this.',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        await this.service.restoreMailConfig(row.id, false).toPromise();
        this.showSuccessMessage(
          'The mail-config has been restored successfully.',
        );
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchMailConfigs();
      }
    }
  }

  openDialog(row: any): void {
    this.dialog.open(DialogviewmailstatusComponent, {
      data: row,
      width: '40%',
    });
  }

  selectdata(value): void {
    this.status = value;
    this.detectChange$.next(true);
  }

  clear(): void {
    const filterValue = (this.search = '');
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.paginator.firstPage();
    this.detectChange$.next(true);
  }

  fetchMailConfigs(): void {
    this.service
      .getList(
        this.filterListCriteria.defaultCriteria.pageIndex,
        this.filterListCriteria.defaultCriteria.pageSize,
        this.filterListCriteria.filter,
        this.filterListCriteria.defaultCriteria.sortDirection,
        this.filterListCriteria.defaultCriteria.sortByField,
        this.statusSelect,
      )
      .toPromise()
      .then((response) => {
        this.length = response.total;
        this.totalMailConfigs = response.total;
        this.dataSource = new MatTableDataSource(response.contents);
      });
  }

  getMailRow(candidateField: any) {
    return { row: candidateField };
  }

  pageEvent(event: PageEvent): void {
    this.filterListCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.filterListCriteria.defaultCriteria.pageSize = event.pageSize;
    this.fetchMailConfigs();
  }

  searchFilterChange(event: any) {
    this.filterListCriteria.filter = event;
    this.filterListCriteria.defaultCriteria.pageIndex = 1;
    this.fetchMailConfigs();
  }

  getStatusFilter(candidateFilterStatus: string): string {
    return this.filterStatuses
      .filter((status) => status.value === candidateFilterStatus)
      .map((status) => status.key)
      .map((key) => key)
      .toString();
  }

  filterChange(criteria: any): void {
    this.filterListCriteria.status =
      criteria.status === 'All' ? '' : criteria.status;
    this.statusSelect = criteria.status === 'All' ? '' : criteria.status;
    this.fetchMailConfigs();
  }

  sortMailConfig(sort: Sort): void {
    this.filterListCriteria.sortDirection = sort.direction;
    this.filterListCriteria.sortByField = sort.active;
    this.filterListCriteria.defaultCriteria.pageIndex = 1;
    this.fetchMailConfigs();
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

