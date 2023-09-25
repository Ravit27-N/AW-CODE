import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import * as fromDirectoryFeedAction from '@cxm-smartflow/directory-feed/data-access';
import {
  defaultDirectoryFeedPagination,
  loadDirectoryFeedList,
  selectDirectoryFeedList,
  selectSelectedFeedRow
} from '@cxm-smartflow/directory-feed/data-access';
import { Store } from '@ngrx/store';
import { AuthenticationConstant, DirectoryManagement, Params } from '@cxm-smartflow/shared/data-access/model';
import { CanVisibilityService, CheckPrivilegeService } from '@cxm-smartflow/shared/data-access/services';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-feature-list-directory-feed',
  templateUrl: './feature-list-directory-feed.component.html',
  styleUrls: ['./feature-list-directory-feed.component.scss']
})
export class FeatureListDirectoryFeedComponent implements OnInit, OnDestroy {

  // prefill property
  refreshedDate$ = new BehaviorSubject<Date>(new Date());
  clickable = false;
  params: Params;
  isCanOpen = false;
  // validate properties
  destroyed$ = new Subject<boolean>();
  adminUsername: string;

  constructor(private store: Store,
              private canVisibility: CanVisibilityService,
              private checkPrivilege: CheckPrivilegeService) {
  }

  ngOnInit(): void {
    this.store.dispatch(loadDirectoryFeedList({ params: defaultDirectoryFeedPagination }));
    this.fillListDefinitionFeed();
  }

  ngOnDestroy(): void {
    this.store.dispatch(fromDirectoryFeedAction.unloadDirectoryFeedList({}));
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  fillListDefinitionFeed() {


    this.store.select(selectDirectoryFeedList)
      .pipe(takeUntil(this.destroyed$))
      .subscribe(v => {
        this.clickable = v?.clickable;
        this.refreshedDate$.next(v?.refreshDate);
        this.params = v?.params;

        this.isCanOpen = this.validateVisibility(v?.directoryCreatedBy, v?.directoryFeedingBy);
      });
  }

  refreshEvent() {
    this.store.dispatch(fromDirectoryFeedAction.refreshDirectoryFeedList({ params: this.params }));
  }

  navigateToFeed() {
    if (!this.isCanOpen) return;

    this.store.select(selectSelectedFeedRow)
      .subscribe(selectedRow => this.store
        .dispatch(fromDirectoryFeedAction.navigateToFeed({ row: selectedRow })))
      .unsubscribe();
  }

  validateVisibility(createdBy: string, feedingBy: string): boolean {
    const profile = JSON.parse(<string>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));

    if(!createdBy) return false;

    // Todo: created by admin
    if (createdBy === this.adminUsername) {
      return profile?.admin || feedingBy === null;
    } else {
      // Todo: not created by admin
      if (feedingBy === null) {
        return this.checkPrivilege.getUserRight(DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
          DirectoryManagement.EDIT_DATA_DIRECTORY_FEED,
          true);
      } else {
        return this.canVisibility.getUserRight(DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
          DirectoryManagement.EDIT_DATA_DIRECTORY_FEED,
          feedingBy || '',
          true,
          true);
      }
    }
  }
}
