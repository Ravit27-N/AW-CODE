import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import {
  DebugMessage,
  DirectoryFeedExceptionHandlerService,
  DirectoryFeedService,
  importDirectoryFeedCsvFileInSuccess,
  selectDirectoryFieldKeyLabel,
  submitImportDirectoryFeed,
} from '@cxm-smartflow/directory-feed/data-access';
import { FileUtils, URLUtils } from '@cxm-smartflow/shared/utils';
import { Store } from '@ngrx/store';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import { HttpParams } from '@angular/common/http';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-feature-import-directory-feed-csv',
  templateUrl: './feature-import-directory-feed-csv.component.html',
  styleUrls: ['./feature-import-directory-feed-csv.component.scss'],
})
export class FeatureImportDirectoryFeedCsvComponent
  implements OnInit, OnDestroy {
  formGroup: FormGroup;
  fileName = '';
  fileSize = '';
  fileId = '';
  labelFieldKey = '';

  destroyed$ = new Subject<boolean>();
  private directoryFeedExceptionHandler = new DirectoryFeedExceptionHandlerService(
    this.translationService,
    this.snackBarService
  );

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<FeatureImportDirectoryFeedCsvComponent>,
    private directoryService: DirectoryFeedService,
    private store$: Store,
    private snackBarService: SnackBarService,
    private translationService: TranslateService
  ) {}

  ngOnInit(): void {
    this.formGroup = this.formBuilder.group({
      ignoreHeader: new FormControl(true),
      removeDuplicatedField: new FormControl(true),
    });

    this.store$
      .select(selectDirectoryFieldKeyLabel)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((labelFieldKey) => {
        this.labelFieldKey = labelFieldKey;
      });
  }

  ngOnDestroy(): void {
    this.formGroup.reset();
    this.destroyed$.complete();
  }

  closeDialogEvent(): void {
    this.dialogRef.close();
  }

  async uploadFileEvent($event: FileList): Promise<void> {
    const file: File = Array.from($event)[0];
    const id = URLUtils.getQueryParamByKey('id');
    const formData = new FormData();
    const {
      ignoreHeader,
      removeDuplicatedField,
    } = this.formGroup.getRawValue();

    formData.append('csvFile', file);
    const httpParams = new HttpParams()
      .set('directoryId', `${id}`)
      .set('ignoreHeader', ignoreHeader)
      .set('removeDuplicate', removeDuplicatedField);

    const fileForm = formData.get('csvFile') as File;

    if (!id || !formData.has('csvFile')) {
      return;
    }

    if (!(await this.validateUploadMultipleFiles($event))) {
      return;
    }

    try {
      const response = await this.directoryService
        .uploadCsvFile(formData, httpParams)
        .toPromise();
      this.fileName = response.body.fileName;
      this.fileSize = `${FileUtils.convertToKB(`${response.body.size}B`)}KB`;
      this.fileId = response.body.fileId;
      this.store$.dispatch(
        importDirectoryFeedCsvFileInSuccess({
          directoryId: Number(id),
          page: 1,
          pageSize: 10,
          removeDuplicated: removeDuplicatedField,
          ignoreHeader,
        })
      );
    } catch (ex) {
      const statusCode = ex?.error?.apierrorhandler?.statusCode;
      const debugMsg = JSON.parse(
        ex?.error?.apierrorhandler?.debugMessage || '{}'
      ) as DebugMessage;
      await this.handleError(statusCode, debugMsg);
    }
  }

  private async validateUploadMultipleFiles(
    fileList: FileList
  ): Promise<boolean> {
    if (Array.from(fileList).length > 1) {
      const message = await this.translationService
        .get('directory.directory_feed_details_upload_invalid_total_files')
        .toPromise();
      this.snackBarService.openCustomSnackbar({
        icon: 'close',
        type: 'error',
        message,
      });

      return false;
    }
    return true;
  }

  async resetUploadFileEvent(): Promise<void> {
    this.fileSize = '';
    this.fileName = '';
    if (this.fileId) {
      await this.directoryService.removeCsvFile(this.fileId).toPromise();
    }
    this.fileId = '';
  }

  async importEvent(): Promise<void> {
    if (!this.fileId) {
      await this.handleError(4000);
      return;
    }

    this.store$.dispatch(submitImportDirectoryFeed({ fileId: this.fileId }));
    this.dialogRef.close();
  }

  private async handleError(
    statusCode: number,
    debugMsg?: DebugMessage
  ): Promise<void> {
    await this.directoryFeedExceptionHandler.handleError(statusCode, debugMsg);
  }
}
