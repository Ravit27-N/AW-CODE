import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { AuthenticationConstant, DirectoryManagement, Params } from '@cxm-smartflow/shared/data-access/model';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { Store } from '@ngrx/store';
import * as fromDirectoryFeedAction from '@cxm-smartflow/directory-feed/data-access';
import {
  DirectoryFeedModel,
  loadDirectoryFeedList,
  navigateToFeed,
  selectDirectoryFeedList
} from '@cxm-smartflow/directory-feed/data-access';
import { CanVisibilityService, CheckPrivilegeService } from '@cxm-smartflow/shared/data-access/services';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-feature-directory-feed-table',
  templateUrl: './feature-directory-feed-table.component.html',
  styleUrls: ['./feature-directory-feed-table.component.scss']
})
export class FeatureDirectoryFeedTableComponent implements OnInit, OnDestroy {

  // prefill properties
  directoryFeedTable: any = new MatTableDataSource();
  directoryFeedTableColumn = ['name', 'createdAt', 'updatedAt'];
  selectRow = -1;
  // pagination
  totalDirectoryFeed = 0;
  sortByField = 'createdAt';
  sortDirection = 'desc';
  pageIndex = 1;
  pageSize = 15;
  param: Params;
  // validate properties
  directoryFeed: DirectoryFeedModel;
  isUnderline = false;
  destroyed$ = new Subject<boolean>();
  adminUsername: string;

  constructor(private store: Store,
              private canVisibility: CanVisibilityService,
              private checkPrivilege: CheckPrivilegeService) {
  }

  ngOnInit(): void {
    this.fillListDirectoryFeedTable();
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  fillListDirectoryFeedTable() {
    this.store.select(selectDirectoryFeedList).pipe(takeUntil(this.destroyed$)).subscribe(v => {
      this.directoryFeedTable = v?.directoryFeedList?.contents;
      this.totalDirectoryFeed = v?.directoryFeedList?.total;
      if (!v?.clickable) this.selectRow = -1;
      this.param = v?.params;
      if (v?.params?.pageIndex) this.pageIndex = v?.params.pageIndex;
      if (v?.params?.pageSize) this.pageSize = v?.params.pageSize;
      if (v?.params?.sortDirection) this.sortDirection = v?.params.sortDirection;
      if (v?.params?.sortByField) this.sortByField = v?.params.sortByField;
      this.adminUsername = v?.userDetail?.adminUsername;
    });
  }

  selectDirectoryFeed(i: number, row: DirectoryFeedModel) {

    this.selectRow = this.selectRow === -1 || this.selectRow !== i ? i : -1;
    this.directoryFeed = row;

    this.store.dispatch(fromDirectoryFeedAction.validateModifiedAndDelete({
      clickable: this.selectRow !== -1,
      directoryFeed: this.directoryFeed
    }));

    if (this.selectRow === -1) {
      this.store.dispatch(fromDirectoryFeedAction.resetValidatedDirectoryFeedingBy({}));
    } else {
      // this.store.dispatch(fromDirectoryFeedAction.validateDirectoryFeedingBy({
      //   directoryFeedingBy: row?.feedingBy,
      //   directoryCreatedBy: row?.createdBy
      // }));
    }
  }

  onPageChange(pageEvent: PageEvent) {
    this.pageIndex = pageEvent.pageIndex + 1;
    this.pageSize = pageEvent.pageSize;
    this.loadDirectoryDefinition();
  }

  sortDefinition(sort: Sort) {
    if (sort.direction === '') {
      this.sortDirection = 'desc';
      this.sortByField = 'createdAt';
    } else {
      this.sortDirection = sort.direction;
      this.sortByField = sort.active;
    }


    this.loadDirectoryDefinition();
  }

  loadDirectoryDefinition() {
    this.param = {
      page: this.pageIndex,
      pageSize: this.pageSize,
      sortDirection: this.sortDirection,
      sortByField: this.sortByField
    };
    this.store.dispatch(loadDirectoryFeedList({ params: this.param }));
  }

  validateModification(row: DirectoryFeedModel): boolean {
    // const profile = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
    // // Todo: created by admin
    // if (row?.createdBy === this.adminUsername) {
    //   this.isUnderline = profile?.admin || row.feedingBy === null;
    // } else {
    //   // Todo: not created by admin
    //   if (row.feedingBy === null) {
    //     this.isUnderline = this.checkPrivilege.getUserRight(DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    //       DirectoryManagement.EDIT_DATA_DIRECTORY_FEED,
    //       true);
    //   } else {
    //     this.isUnderline = this.canVisibility.getUserRight(DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    //       DirectoryManagement.EDIT_DATA_DIRECTORY_FEED,
    //       row?.feedingBy || '',
    //       true,
    //       true);
    //   }
    // }
    return this.isUnderline;
  }

  navigateTo(row: DirectoryFeedModel) {

    if (this.validateModification(row)) {
      this.store.dispatch(navigateToFeed({ row }));
      return;
    }

    // if (!this.canVisibility.getUserRight(DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    //   DirectoryManagement.EDIT_DATA_DIRECTORY_FEED,
    //   row?.feedingBy || '',
    //   true,
    //   true)) return;

    this.store.dispatch(navigateToFeed({ row }));
  }
}
