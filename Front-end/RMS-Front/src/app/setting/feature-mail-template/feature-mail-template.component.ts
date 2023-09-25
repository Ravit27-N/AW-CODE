import { MessageService } from './../../core/service/message.service';
import { AddmailtemplateComponent } from '../addmailtemplate/addmailtemplate.component';
import {
  MailTemplateFormModel,
  MailTemplateModel,
} from './../../core/model/Mailtemplate';
import { MailtemplateService } from '../../core/service/Mailtemplate.service';
import { Subject } from 'rxjs';
import { DialogviewComponent } from '../mail-template/dialogview/dialogview.component';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import {
  DefaultCriteria,
  ProjectCriteria,
  PAGINATION_SIZE,
} from 'src/app/core';
import { UpdatemailtemplateComponent } from '../updatemailtemplate/updatemailtemplate.component';
import { RestoreDialogComponent } from '../_dialog/restoreDialog/restoreDialog.component';
import { IsLoadingService } from '@service-work/is-loading';
import { KeyValue } from '@angular/common';

@Component({
  selector: 'app-feature-mail-template',
  templateUrl: './feature-mail-template.component.html',
  styleUrls: ['./feature-mail-template.component.scss'],
})
export class FeatureMailTemplateComponent implements OnInit {
  id: string;
  subject: string;
  body: string;
  status = '';
  content: MailTemplateFormModel[];
  search: string;
  displayedColumns: string[] = ['#', 'subject', 'body', 'active', 'Action'];
  dataSource: MatTableDataSource<MailTemplateModel>;
  private detectChange$ = new Subject<boolean>();
  length: number;
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

  statusSelect = '';
  total = 0;
  paginationSize = PAGINATION_SIZE;

  filterStatuses: Array<KeyValue<string, string>> = [
    { key: '', value: 'All' },
    { key: 'Active', value: 'Active' },
    { key: 'Inactive', value: 'Inactive' },
    { key: 'Deleted', value: 'Deleted' },
  ];

  constructor(
    public dialog: MatDialog,
    private service: MailtemplateService,
    private message: MessageService,
    private isloadingService: IsLoadingService,
  ) {}

  click(row): void {
    if (row.deleted) {
      return;
    }
    this.service.updateActive(row.id, !row.active).subscribe(
      () => {
        this.message.showSuccess('Update Status', 'Mail Template');
        if (this.status !== '') {
          this.detectChange$.next(true);
        }
      },
      () => {
        this.message.showError('Error Update Status', 'Mail Template');
      },
    );
  }

  openDialog(r: any): void {
    this.dialog.open(DialogviewComponent, {
      data: r,
      width: '40%',
    });
  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource(this.content);
    this.setUp();
  }

  setUp() {
    this.loadDataByFunction();
  }

  page(): void {
    this.paginator.firstPage();
  }
  applyFilter(event?: Event): void {
    if (this.search.length >= 3 || this.search === '' || this.search === null) {
      const filterValue = (event.target as HTMLInputElement).value;
      this.dataSource.filter = filterValue.trim().toLowerCase();
      this.paginator.firstPage();
      this.detectChange$.next(true);
    }
  }

  delete(id: number, subject: string): void {
    const dialogRef = this.dialog.open(ComfirmDailogComponent, {
      data: { title: subject },
      width: '450px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.service.updateDeleted(id, true).subscribe(
          () => {
            this.message.showSuccess('Delete Sucess', 'Delete Mail Template');
            this.detectChange$.next(true);
          },
          () => {
            this.message.showError('Delete Fail', 'Delete Mail Template');
            this.detectChange$.next(true);
          },
        );
      }
    });
  }

  restore(id: number, subject): void {
    const dialogRef = this.dialog.open(RestoreDialogComponent, {
      data: subject,
      width: '450px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.service.updateDeleted(id, false).subscribe(
          () => {
            this.message.showSuccess('Restore Sucess', 'Restore Mail Template');
            this.detectChange$.next(true);
          },
          () => {
            this.message.showError('Restore Fail', 'Restore Mail Template');
            this.detectChange$.next(true);
          },
        );
        this.paginator.firstPage();
      }
    });
  }

  clear(): void {
    const filterValue = (this.search = '');
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.paginator.firstPage();
    this.detectChange$.next(true);
  }
  add(): void {
    const dialogRef = this.dialog.open(AddmailtemplateComponent, {
      width: '40%',
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
      this.setUp();
    });
  }
  edit(row): void {
    const dialogRef = this.dialog.open(UpdatemailtemplateComponent, {
      width: '40%',
      data: row,
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
      this.setUp();
    });
  }

  loadDataByFunction(): void {
    const subscription = this.service
      .getList(
        this.filterListCriteria.defaultCriteria.pageIndex,
        this.filterListCriteria.defaultCriteria.pageSize,
        this.filterListCriteria.filter,
        this.filterListCriteria.defaultCriteria.sortDirection,
        this.filterListCriteria.defaultCriteria.sortByField,
        this.statusSelect,
      )
      .subscribe((respone) => {
        this.length = respone.total;
        this.total = respone.total;
        this.dataSource = new MatTableDataSource(respone.contents);
      });
    this.isloadingService.add(subscription, {
      key: 'reminder',
      unique: 'reminder',
    });
  }

  pageEvent(event: PageEvent): void {
    this.filterListCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.filterListCriteria.defaultCriteria.pageSize = event.pageSize;
    this.loadDataByFunction();
  }

  filterChange(criteria: any) {
    this.filterListCriteria.status =
      criteria.status === 'All' ? '' : criteria.status;
    this.statusSelect = criteria.status === 'All' ? '' : criteria.status;
    this.loadDataByFunction();
  }

  getStatusFilter(candidateFilterStatus: string): string {
    return this.filterStatuses
      .filter((status) => status.value === candidateFilterStatus)
      .map((status) => status.key)
      .map((key) => key)
      .toString();
    this.loadDataByFunction();
  }

  getMailRow(candidateField: any) {
    return { row: candidateField };
  }

  searchFilterChange(event: any) {
    this.filterListCriteria.filter = event;
    this.filterListCriteria.defaultCriteria.pageIndex = 1;
    this.filterListCriteria.defaultCriteria.pageSize = 10;
    this.loadDataByFunction();
  }

  sortData(sort: Sort) {
    this.filterListCriteria.sortDirection = sort.direction;
    this.filterListCriteria.sortByField = sort.active;
    this.filterListCriteria.defaultCriteria.pageIndex = 1;
    this.loadDataByFunction();
  }
}
