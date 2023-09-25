import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Confirmable,
  DirectoryFeedService, getDirectoryField,
  selectDirectoryFeedIsLocked, selectDirectoryFields, selectIsSharedDirectory,
} from '@cxm-smartflow/directory-feed/data-access';
import { BlobFile, URLUtils } from '@cxm-smartflow/shared/utils';
import {
  UserProfileUtil,
  UserUtil,
} from '@cxm-smartflow/shared/data-access/services';
import {
  appRoute,
  DirectoryManagement,
} from '@cxm-smartflow/shared/data-access/model';
import { Store } from '@ngrx/store';
import { ImportDirectoryFeedCsvService } from '@cxm-smartflow/directory-feed/ui/feature-import-directory-feed-csv';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import {filter, take, takeUntil} from 'rxjs/operators';
import {BehaviorSubject, Subject} from "rxjs";

@Component({
  selector: 'cxm-smartflow-feature-directory-feed-details',
  templateUrl: './feature-directory-feed-details.component.html',
  styleUrls: ['./feature-directory-feed-details.component.scss'],
})
export class FeatureDirectoryFeedDetailsComponent
  implements OnInit, Confirmable {
  feedDirectoryName = '';
  _canExportCSV = UserProfileUtil.canAccess(
    DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    DirectoryManagement.EXPORT_DIRECTORY_FEED,
    true
  );
  _canImportCSV = UserProfileUtil.getInstance().canModify({
    func: DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    priv: DirectoryManagement.IMPORT_DATA_DIRECTORY_FEED,
    checkAdmin: true,
    ownerId: UserUtil.getOwnerId(),
  });
  _canAddMoreLine = UserProfileUtil.getInstance().canModify({
    func: DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    priv: DirectoryManagement.MANUAL_POPULATE_DATA_DIRECTORY_FEED,
    checkAdmin: true,
    ownerId: UserUtil.getOwnerId(),
  });

  isAdmin = UserUtil.isAdmin();
  destroy$ = new Subject<boolean>();
  isCanImport$ = new BehaviorSubject(false);
  isCanAddNew$ = new BehaviorSubject(false);

  directoryId: number;

  addRouteLink = '';

  constructor(
    private activatedRoute: ActivatedRoute,
    private directoryFeedService: DirectoryFeedService,
    private importDirectoryFeedCsvService: ImportDirectoryFeedCsvService,
    private store$: Store,
    private router: Router,
    private translateService: TranslateService,
    private confirmationService: ConfirmationMessageService
  ) {
    const { id } = this.activatedRoute.snapshot.queryParams;
    if (id) {
      this.directoryId = id;
    }
  }

  async isLocked(): Promise<boolean> {
    const hasChanged = await this.store$
      .select(selectDirectoryFeedIsLocked)
      .pipe(take(1))
      .toPromise();
    const message = await this.translateService
      .get('directory.definition_directory_confirm_leave')
      .toPromise();

    if (hasChanged) {
      return !(await this.confirmationService
        .showConfirmationPopup({
          type: 'Warning',
          title: message.title,
          message: message.message,
          confirmButton: message.quit,
          cancelButton: message.cancelBtn,
        })
        .toPromise());
    }

    return false;
  }

  ngOnInit(): void {
    this.feedDirectoryName = URLUtils.getQueryParamByKey('name') || '';
    this.addRouteLink =
      '/cxm-directory/directory-feed/add?id=' + this.directoryId;

    this.store$.dispatch(getDirectoryField({directoryId: this.directoryId}));

    this.store$
      .select(selectIsSharedDirectory)
      .pipe(takeUntil(this.destroy$), filter(field => field.isLoaded === true))
      .subscribe((isShared) => {
        if(this.isAdmin) {
          this.isCanImport$.next(true);
          this.isCanAddNew$.next(true);
        }else if(!isShared.isSharedDirectory) {
          this.isCanAddNew$.next(this._canAddMoreLine);
          this.isCanImport$.next(this._canImportCSV);
        }
      });
  }

  async exportFeedCsv(): Promise<void> {
    const id = URLUtils.getQueryParamByKey('id') || '0';
    if (!id) {
      return;
    }

    const blobFile = await this.directoryFeedService
      .downloadDirectoryFeedCsv(JSON.parse(id))
      .toPromise();
    const file: File = BlobFile.blobToFile(
      blobFile.file,
      `${this.feedDirectoryName}.csv`
    );
    BlobFile.saveFile(file);
  }

  async importDirectoryFeedCsv(): Promise<void> {
    await this.importDirectoryFeedCsvService.show().toPromise();
  }

  addDirectory(): void {
    this.router.navigate([appRoute.cxmDirectory.navigateToAddDirectoryFeed], {
      queryParams: { id: this.directoryId },
    });
  }
}
