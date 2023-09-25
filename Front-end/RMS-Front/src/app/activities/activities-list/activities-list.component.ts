import { ActivityService } from '../../core';
import { ActivityDetailComponent } from '../activity-detail/activity-detail.component';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit, ViewChild } from '@angular/core';
import { EditActivityComponent } from '../edit-activity/edit-activity.component';
import { ActivityModel, PAGINATION_SIZE } from 'src/app/core';
import { IsLoadingService } from '@service-work/is-loading';

@Component({
  selector: 'app-activities-list',
  templateUrl: './activities-list.component.html',
  styleUrls: ['./activities-list.component.css']
})
export class ActivitiesListComponent implements OnInit {

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  displayedColumns: string[] = ['No', 'Candidate', 'Title', 'Date & time', 'Author', 'Description', 'Action'];
  dataSource: MatTableDataSource<ActivityModel>;
  activityList: ActivityModel[];
  pageSize = 10;
  pageIndex = 1;
  length = 0;
  sortByField = 'createdAt';
  sortDirection = 'desc';
  paginationSize = PAGINATION_SIZE;
  filter = '';

  constructor(
    private router: Router,
    private dialog: MatDialog,
    private activityService: ActivityService,
    private isloadingService: IsLoadingService
  ) { }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.filter = '';
    this.sortByField = 'createdAt';
    this.sortDirection = 'desc';
    this.pageIndex = 1;
    this.pageSize = 10;
    this.loadDataByFunction();
  }

  loadDataByFunction(): void{
    const subscription = this.activityService.getList(this.pageIndex, this.pageSize, this.filter, this.sortDirection, this.sortByField)
    .subscribe((respone) => {
      this.activityList = respone.contents;
      this.length = respone.total;
      this.dataSource = new MatTableDataSource(this.activityList);
    });
    this.isloadingService.add(subscription, { key: 'activity', unique: 'activity' });
  }

  clearFilter(): void {
    this.filter = '';
    this.pageIndex = 1;
    this.loadDataByFunction();
    this.paginator.pageIndex = 0;
  }
  applyFilter(): void {
    this.pageIndex = 1;
    if (this.filter.trim() === null || this.filter.trim() === '') {
      this.loadData();
    }
    if (this.filter.length >= 3) {
      this.loadDataByFunction();
      this.paginator.pageIndex = 0;
    }
  }

  pageEvent(event: PageEvent): void {
    this.pageIndex = event.pageIndex + 1;
    this.pageSize = event.pageSize;
    this.loadDataByFunction();
  }

  sortData(sort: Sort): void {
    this.sortByField = sort.active;
    this.sortDirection = sort.direction;
    if (this.sortByField === 'Date & time') {
      this.sortByField = 'createdAt';
    } else if (this.sortByField === 'Author') {
      this.sortByField = 'userId';
    } else if (this.sortByField === 'Title') {
      this.sortByField = 'title';
    } else if (this.sortByField === 'Description') {
      this.sortByField = 'description';
    } else if (this.sortByField === 'Candidate') {
      this.sortByField = 'firstname';
    } else {
      this.sortByField = 'createdAt';
    }
    this.pageIndex = 1;
    this.loadDataByFunction();
    this.paginator.pageIndex = 0;
  }

  clearSearchBox(): void {
    this.filter = null;
    this.loadData();
  }

  edit(id: number): void {
    const dialogRef = this.dialog.open(EditActivityComponent, {
      maxWidth: '100vw',
      disableClose: true,
      width: '800px',
      data: {
        id
      },
      panelClass: 'overlay-scrollable'
    });
    dialogRef.afterClosed().subscribe(() => {
      this.ngOnInit();
    });
  }

  add(): void {
    this.router.navigate(['/admin/activities/add']);
  }

  view(id: number): void {
   this.dialog.open(ActivityDetailComponent, {
      width: '700px',
      data: {
        id
      },
      panelClass: 'overlay-scrollable'
    });
  }

}
