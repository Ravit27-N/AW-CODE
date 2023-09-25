import { DialogviewcandidateComponent } from '../statuecandidate/dialogviewcandidate/dialogviewcandidate.component';
import { UpdatestatuscandidateComponent } from './../updatestatuscandidate/updatestatuscandidate.component';
import { AddstatuscandidateComponent } from './../addstatuscandidate/addstatuscandidate.component';
import { MessageService } from './../../core/service/message.service';
import { StatusCandidateService } from './../../core/service/status-candidate.service';
import { Subject } from 'rxjs';
import { StatusCandidateModel } from './../../core/model/statuscandidate';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import {
  DefaultCriteria,
  ProjectCriteria,
  PAGINATION_SIZE,
} from 'src/app/core';
import { RestoreDialogComponent } from '../_dialog/restoreDialog/restoreDialog.component';
import { IsLoadingService } from '@service-work/is-loading';
import { KeyValue } from '@angular/common';

@Component({
  selector: 'app-feature-status-candidate',
  templateUrl: './feature-status-candidate.component.html',
  styleUrls: ['./feature-status-candidate.component.scss'],
})
export class FeatureStatusCandidateComponent implements OnInit {
  search: string;
  active: string;
  length: number;
  status = '';
  displayedColumns: string[] = [
    '#',
    'title',
    'description',
    'active',
    'Action',
  ];
  dataSource: MatTableDataSource<StatusCandidateModel>;
  content: StatusCandidateModel[];
  private detectChange$ = new Subject<boolean>();
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
  filterStatuses: Array<KeyValue<string, string>> = [
    { key: '', value: 'All' },
    { key: 'Active', value: 'Active' },
    { key: 'Inactive', value: 'Inactive' },
    { key: 'Deleted', value: 'Deleted' },
  ];
  paginationSize = PAGINATION_SIZE;

  constructor(
    public dialog: MatDialog,
    private service: StatusCandidateService,
    private message: MessageService,
    private isloadingService: IsLoadingService,
  ) {}

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
  click(row): void {
    if (!row.deleted) {
      this.service.changeStatus(row.id, !row.active).subscribe(
        () => {
          this.message.showSuccess('Sucess Update Status', 'Status Candidate');
          if (this.status !== '') {
            this.detectChange$.next(true);
          }
        },
        () => {
          this.message.showError('Fail Update Status', 'Status Candidate');
        },
      );
    }
  }
  delete(row): void {
    const dialogRef = this.dialog.open(ComfirmDailogComponent, {
      data: { title: row.title },
      width: '450px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.service.softDelete(row.id, true).subscribe(
          () => {
            this.message.showSuccess('Delete Sucess', 'Status Candidate');
            this.detectChange$.next(true);
          },
          () => {
            this.message.showError('Delete Fail', 'Status Candidate');
          },
        );
      }
    });
  }

  restore(row: any): void {
    const dialogRef = this.dialog.open(RestoreDialogComponent, {
      data: row.title,
      width: '450px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.service.softDelete(row.id, false).subscribe(
          () => {
            this.message.showSuccess('Restore Sucess', 'Status Candidate');
            this.detectChange$.next(true);
          },
          () => {
            this.message.showError('Restore Fail', 'Status Candidate');
          },
        );
        this.paginator.firstPage();
      }
    });
  }

  openDialog(row: any): void {
    this.dialog.open(DialogviewcandidateComponent, {
      data: row,
      width: '40%',
      panelClass: 'overlay-scrollable',
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
  add(): void {
    const dialogRef = this.dialog.open(AddstatuscandidateComponent, {
      width: '40%',
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
  edit(row): void {
    const dialogRef = this.dialog.open(UpdatestatuscandidateComponent, {
      width: '40%',
      data: row,
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
  // pageEvent(event: PageEvent): void {
  //   this.paginator.pageIndex = event.pageIndex;
  //   this.paginator.pageSize = event.pageSize;
  //   this.detectChange$.next(true);
  // }

  loadDataByFunction(): void {
    const subscription = this.service
      .getList(
        this.filterListCriteria.defaultCriteria.pageIndex,
        this.filterListCriteria.defaultCriteria.pageSize,
        this.statusSelect,
        this.filterListCriteria.defaultCriteria.sortByField,
        this.filterListCriteria.defaultCriteria.sortDirection,
        this.filterListCriteria.filter
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
