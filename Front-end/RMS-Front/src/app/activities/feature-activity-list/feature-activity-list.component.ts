import { Component, OnInit } from '@angular/core';
import { Sort } from '@angular/material/sort';
import {
  ActivityCriteriaModel,
  ActivityModel,
  ActivityService,
  DefaultActivity,
} from '../../core';
import { IsLoadingService } from '@service-work/is-loading';
import { Router } from '@angular/router';
import { EditActivityComponent } from '../edit-activity/edit-activity.component';
import { MatDialog } from '@angular/material/dialog';
import { FeatureActivityDetailComponent } from '../feature-activity-detail';

@Component({
  selector: 'app-feature-activity-list',
  templateUrl: './feature-activity-list.component.html',
  styleUrls: ['./feature-activity-list.component.scss'],
})
export class FeatureActivityListComponent implements OnInit {
  tableColumnHeader: string[] = [
    'no',
    'candidate',
    'title',
    'dateTime',
    'author',
    'action',
  ];
  defaultCriteria: DefaultActivity = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: 'createdAt',
    sortDirection: 'desc',
  };

  total: number;

  activityCriteria: ActivityCriteriaModel = {
    defaultCriteria: { ...this.defaultCriteria },
    filter: '',
  };

  dataSource: Array<ActivityModel> = [];

  constructor(
    private router: Router,
    private dialog: MatDialog,
    private activityService: ActivityService,
    private isLoadingService: IsLoadingService,
  ) {}

  ngOnInit(): void {
    if (localStorage.getItem('activity-list')) {
      this.activityCriteria = JSON.parse(localStorage.getItem('activity-list'));
    }
    this.fetchActivityList();
  }

  addActivity(): void {
    this.router.navigateByUrl('/admin/activities/add');
  }

  edit(id: number): void {
    const dialogRef = this.dialog.open(EditActivityComponent, {
      disableClose: true,
      width: '800px',
      height: '520px',
      data: {
        id,
      },
      panelClass: 'custom-confirmation-popup',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.ngOnInit();
    });
  }

  view(activityModel: ActivityModel): void {
    this.dialog.open(FeatureActivityDetailComponent, {
      width: '600px',
      data: activityModel,
      panelClass: ['custom-confirmation-popup', 'hidden-overflow-custom'],
    });
  }

  fetchActivityList(): void {
    localStorage.setItem(
      'activity-list',
      JSON.stringify(this.activityCriteria),
    );
    const subscription = this.activityService
      .getList(
        this.activityCriteria.defaultCriteria.pageIndex,
        this.activityCriteria.defaultCriteria.pageSize,
        this.activityCriteria.filter,
        this.activityCriteria.defaultCriteria.sortDirection,
        this.activityCriteria.defaultCriteria.sortByField,
      )
      .subscribe((result) => {
        this.dataSource = result.contents;
        this.total = result.total;
      });
    this.isLoadingService.add(subscription, {
      key: 'activity',
      unique: 'activity',
    });
  }

  sortColumnTable(sort: Sort): void {
    this.activityCriteria.defaultCriteria.sortByField = sort.active;
    this.activityCriteria.defaultCriteria.sortDirection = sort.direction;
    if (this.activityCriteria.defaultCriteria.sortByField === 'candidate') {
      this.activityCriteria.defaultCriteria.sortByField = 'firstname';
    } else if (this.activityCriteria.defaultCriteria.sortByField === 'author') {
      this.activityCriteria.defaultCriteria.sortByField = 'userId';
    } else if (this.activityCriteria.defaultCriteria.sortByField === 'title') {
      this.activityCriteria.defaultCriteria.sortByField = 'title';
    } else {
      this.activityCriteria.defaultCriteria.sortByField = 'createdAt';
    }
    this.fetchActivityList();
  }

  getCandidateRowNumber(index: number): number {
    return (
      (this.activityCriteria.defaultCriteria.pageIndex - 1) *
        this.activityCriteria.defaultCriteria.pageSize +
      (index + 1)
    );
  }

  pageChangeEvent(event: any): void {
    this.activityCriteria.defaultCriteria.pageIndex = event?.pageIndex;
    this.activityCriteria.defaultCriteria.pageSize = event?.pageSize;
    this.fetchActivityList();
  }

  getCandidateRow(candidateField: ActivityModel) {
    return { row: candidateField };
  }

  searchValueChange(event: string) {
    this.activityCriteria.filter = event;
    this.activityCriteria.defaultCriteria.pageIndex = 1;
    this.activityCriteria.defaultCriteria.pageSize = 10;
    this.fetchActivityList();
  }

  getTitleCss(title: string): string {
    if (title.endsWith('Passed')) {
      return 'pass';
    }
    if (title.endsWith('Canceled') || title.endsWith('Failed')) {
      return 'failed';
    }
    if (title.endsWith('In Progress')) {
      return 'in-progress';
    }
    if (title.endsWith('New Request')) {
      return 'new-request';
    }
    if (title.endsWith('Following Up')) {
      return 'following';
    }
  }
}
