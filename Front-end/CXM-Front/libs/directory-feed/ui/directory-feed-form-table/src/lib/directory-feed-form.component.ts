import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  attempToSubmitFeedDirectory,
  exportDirectoryData,
  exportDirectorySchema,
  importDirectoryData,
  selectTableSchemas
} from '@cxm-smartflow/directory-feed/data-access';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthenticationConstant, DirectoryManagement } from '@cxm-smartflow/shared/data-access/model';
import { directoryFeedTabNav } from '@cxm-smartflow/directory-feed/ui/feature-directory-feed-navigator';
import {
  CanModificationService,
  CanVisibilityService,
  CheckPrivilegeService
} from '@cxm-smartflow/shared/data-access/services';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'cxm-smartflow-directory-feed-form',
  templateUrl: './directory-feed-form.component.html',
  styleUrls: ['./directory-feed-form.component.scss']
})
export class DirectoryFeedFormComponent implements OnInit, OnDestroy {


  schemas$: Observable<any>;
  destroyed$ = new Subject<boolean>();

  // validate properties
  isCanImport = false;
  isCanExport = false;
  feedingBy: string;
  displayName: string;
  adminUsername: string;

  public importData(event: any) {
    const files = event.target?.files;

    if (files.length > 0) {
      // loadCsvAsJson(files[0]).then((data: any) => this.store.dispatch(importDirectoryData({ json: data })));
      const form = new FormData();
      form.append('file', files[0]);
      this.store.dispatch(importDirectoryData({ form, filename: files[0].name }));
      event.target.value = null;
    }
  }

  public exportData() {
    if (!this.isCanExport) return;
    this.store.dispatch(exportDirectoryData());
  }


  public exportSchema() {
    if (!this.isCanImport) return;
    this.store.dispatch(exportDirectorySchema());
  }

  public submit() {
    // send data to server
    this.store.dispatch(attempToSubmitFeedDirectory());
  }

  validatePrivilege() {
    this.activatedRoute.queryParams.subscribe(v => {
      this.feedingBy = v?.feedingBy || null;
      this.displayName = v?.displayName;
    }).unsubscribe();

  }

  ngOnInit(): void {
    this.checkPrivilege.validateUserRightAndNavigate(DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
      DirectoryManagement.EDIT_DATA_DIRECTORY_FEED,
      directoryFeedTabNav.list.link,
      true);
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  constructor(private store: Store,
              private translateService: TranslateService,
              private checkPrivilege: CheckPrivilegeService,
              private canModify: CanModificationService,
              private canVisibility: CanVisibilityService,
              private activatedRoute: ActivatedRoute) {
    this.schemas$ = this.store.select(selectTableSchemas).pipe(takeUntil(this.destroyed$));
    this.validatePrivilege();
  }


  validateImport(fileUpload: HTMLInputElement) {
    if (!this.isCanImport) return;
    fileUpload.click();
  }
}
