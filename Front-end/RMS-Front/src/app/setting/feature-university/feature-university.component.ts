
import { UniversityModel } from 'src/app/core/model/university';

import { UniversityService } from './../../core/service/university.service';
import {MatSort, Sort} from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Subject } from 'rxjs';
import {Component, OnInit, ViewChild} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import {DefaultCriteria, ProjectCriteria, MessageService, PAGINATION_SIZE} from 'src/app/core';
import { AdduniversityComponent } from '../university/adduniversity/adduniversity.component';
import { IsLoadingService } from '@service-work/is-loading';
import {KeyValue} from '@angular/common';
import {DialogViewUniversityComponent, UpdateuniversityComponent} from '../university';

@Component({
  selector: 'app-feature-university',
  templateUrl: './feature-university.component.html',
  styleUrls: ['./feature-university.component.scss'],
})
export class FeatureUniversityComponent implements OnInit {
  search: string;
  active: string;
  length: number;
  status = 'all';
  displayedColumns: string[] = ['#', 'name', 'address', 'createdAt', 'Action'];
  dataSource: MatTableDataSource<UniversityModel>;
  content: UniversityModel[];
  private detectChange$ = new Subject<boolean>();
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  paginationSize = PAGINATION_SIZE;

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

  constructor(
    public dialog: MatDialog,
    private service: UniversityService,
    private message: MessageService,
    private isloadingService: IsLoadingService,
  ) {
    this.dataSource = new MatTableDataSource(this.content);
  }

  ngOnInit() {
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

  delete(row): void {
    const dialogRef = this.dialog.open(ComfirmDailogComponent, {
      data: { title: row.name },
      width: '450px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.service.delete(row.id).subscribe(
          () => {
            this.message.showSuccess('Delete Sucess', 'University');
            this.detectChange$.next(true);
          },
          (err) => {
            if (err.apierror.statusCode === 403) {
              this.message.showError(
                name + ' is already use with candidate',
                'University',
              );
            } else {
              this.message.showError(err.apierror.statusCode, 'University');
            }
          },
        );
      }
    });
  }

  openDialog(row: any): void {
    this.dialog.open(DialogViewUniversityComponent, {
      data: row,
      width: '40%',
      panelClass: 'overlay-scrollable',
    });
  }
  selectData(value): void {
    this.status = value;
    this.detectChange$.next(true);
  }
  clear(): void {
    const filterValue = (this.search = '');
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.paginator.firstPage();
    this.detectChange$.next(true);
  }

  openEdit(row): void {
    const dialogRef = this.dialog.open(UpdateuniversityComponent, {
      data: row,
      width: '40%',
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }
  add(): void {
    const dialogRef = this.dialog.open(AdduniversityComponent, {
      width: '40%',
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.detectChange$.next(true);
    });
  }

  loadDataByFunction(): void {
    const subscription = this.service
      .getList(
        this.filterListCriteria.defaultCriteria.pageIndex,
        this.filterListCriteria.defaultCriteria.pageSize,
        this.filterListCriteria.filter,
        this.filterListCriteria.defaultCriteria.sortDirection,
        this.filterListCriteria.defaultCriteria.sortByField)
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
