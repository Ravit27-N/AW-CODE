import {
  ChangeDetectionStrategy,
  Component,
  HostListener,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { Router } from '@angular/router';
import {
  clearDepositFlow,
  dropFilesAction,
  enqueRoute,
  FlowTabs,
  getLimitUploadFileSize,
  selectFileUploadState,
  selectLimitUploadFileSize,
  stepOnFlowDeposit,
  unloadUploadFileAction,
  validateDocumentFail,
} from '@cxm-smartflow/flow-deposit/data-access';
import { Store } from '@ngrx/store';
import { BehaviorSubject, interval, Subject } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';
import { PDF_FILE_NAME_PATTERN } from '@cxm-smartflow/shared/data-access/model';
import { convertToKB, FileUtils } from '@cxm-smartflow/shared/utils';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-feature-acquisition',
  templateUrl: './feature-acquisition.component.html',
  styleUrls: ['./feature-acquisition.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeatureAcquisitionComponent implements OnInit, OnDestroy {
  // State properties.
  fileUploadState: any;
  errorMessage$ = new BehaviorSubject<string>('');
  limitUploadFileSize$ = new BehaviorSubject<string>('');

  // Animation properties.
  isUploaded$ = new BehaviorSubject<boolean>(false);
  isUploading$ = new BehaviorSubject<boolean>(false);
  isLoadProgress$ = new BehaviorSubject<boolean>(false);
  isUploadFail$ = new BehaviorSubject<boolean>(false);
  dragOpacity = '1';
  buttonOffsetX = '35%';
  titleOffsetX = '55%';
  // Unsubscribe properties.
  destroy$ = new Subject<boolean>();

  // Element properties
  @ViewChild('fileUpload') depositedFile: any;

  ngOnInit(): void {
    // Set uploading layout base on screen.
    this.checkScreen();

    // Emit routes.
    this.store.dispatch(enqueRoute(this.router, FlowTabs.acquisition));
    this.store.dispatch(stepOnFlowDeposit({ step: 1 }));

    // Get Limit upload file size.
    this.store.dispatch(getLimitUploadFileSize());
    this.store
      .select(selectLimitUploadFileSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe((v) => this.limitUploadFileSize$.next(v));

    // Validate state progression.
    this.store
      .select(selectFileUploadState)
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.fileUploadState = value;

        // Set error message.
        switch (value.errorStatusCode) {
          case 406:
            this.errorMessage$.next(
              'flow.deposit.acquisition.label.uploadMessageError406'
            );
            break;
          case 415:
            this.errorMessage$.next(
              'flow.deposit.acquisition.label.uploadMessageError5001'
            );
            break;
          case 5001:
            this.errorMessage$.next(
              'flow.deposit.acquisition.label.uploadMessageError5001'
            );
            break;
          case 5002:
            this.errorMessage$.next(
              'flow.deposit.acquisition.label.uploadMessageError5002'
            );
            break;
          case 5003:
            this.errorMessage$.next(
              'flow.deposit.acquisition.label.uploadMessageError5003'
            );
            break;
          case 5004:
            this.errorMessage$.next(
              'flow.deposit.acquisition.label.uploadMessageError5004'
            );
            break;
          case 5005:
            this.errorMessage$.next(
              'flow.deposit.acquisition.label.uploadMessageError5005'
            );
            break;
          case 5006:
            this.errorMessage$.next(
              'flow.deposit.acquisition.label.uploadMessageError5006'
            );
            break;
          case 5008:
            this.errorMessage$.next(
              'flow.deposit.acquisition.label.uploadMessageError5008'
            );
            break;
          default:
            this.errorMessage$.next('flow.deposit.uploading.invalidPdfFormat');
        }

        // Show error message and clear state.
        if (
          value?.progress === 100 &&
          (value?.error || value?.isCannotIdentify) &&
          !value.isValidateBeforeUpload
        ) {
          interval(1000)
            .pipe(take(1))
            .subscribe(() => {
              this.checkScreen();
              this.isLoadProgress$.next(false);
              this.isUploading$.next(false);
              this.isUploadFail$.next(true);
              this.store.dispatch(unloadUploadFileAction());
              this.store.dispatch(clearDepositFlow());
            });

          if (!value?.isCannotIdentify) {
            this.translate
              .get('flow.deposit.acquisition.label.errorFileUploading')
              .pipe(take(1))
              .subscribe((v) =>
                this.snackbar.openCustomSnackbar({
                  message: v,
                  type: 'error',
                  icon: 'close',
                })
              );
          } else {
            // Alert cannot identify document error message.
            interval(2000)
              .pipe(take(1))
              .subscribe(() => {
                this.translate
                  .get('flow.deposit.acquisition.label.errorFileUploading')
                  .pipe(take(1))
                  .subscribe((message) =>
                    this.snackbar.openCustomSnackbar({
                      message,
                      type: 'error',
                      icon: 'close',
                    })
                  );
              });
          }
        }
      });
  }

  ngOnDestroy(): void {
    // Unsubscribe store.
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  onFileSelected(event: any) {
    this.prepareFile(event.target?.files);
  }

  onFileDrop(files: FileList) {
    this.prepareFile(files);
  }

  setAnimation(): void {
    // Clear old uploading state.
    this.store.dispatch(unloadUploadFileAction());

    // Hide errors, uploaded layout, and show progression bar.
    this.isLoadProgress$.next(false);
    this.isUploading$.next(false);
    this.isUploadFail$.next(false);

    // Show first animation.
    interval(1000)
      .pipe(take(1))
      .subscribe(() => {
        this.isUploading$.next(true);
      });

    // Show progress bar animation.
    interval(2000)
      .pipe(take(1))
      .subscribe(() => {
        this.isLoadProgress$.next(true);
      });
  }

  private prepareFile(files: FileList): void {
    // Validate user upload mono file.
    if (files && files.length === 1) {
      const fileName = files[0].name;

      // Validate file size
      const fileSize = convertToKB(`${files[0].size}B`);
      const limitUploadFileSize = convertToKB(this.limitUploadFileSize$.value);
      const isCorrectFileSize =
        limitUploadFileSize !== 0 &&
        fileSize !== 0 &&
        fileSize <= limitUploadFileSize;

      // Validate PDF file name
      if (fileName.match(PDF_FILE_NAME_PATTERN)) {
        if (isCorrectFileSize) {
          // Validate network connection
          if (navigator.onLine) {
            // Upload file to server.
            this.setAnimation();
            const form = new FormData();
            Array.from(files).forEach((f) => form.append('file', f));
            this.store.dispatch(dropFilesAction({ form, fileName }));
          } else {
            // Internet connection error.
            this.checkScreen();
            this.errorMessage$.next(
              'flow.deposit.acquisition.label.uploadMessageError5007'
            );
            this.isLoadProgress$.next(false);
            this.isUploading$.next(false);
            this.isUploadFail$.next(true);
          }
        } else {
          if (fileSize == 0) {
            this.setErrorAnimation(406);
          } else {
            // File size is too large.
            this.setErrorAnimation(5003);
          }
        }
      } else {
        // File not support.
        this.setErrorAnimation(5001);
      }
    }

    // Validate user upload multiple files.
    if (files && files.length > 1) {
      this.setErrorAnimation(5006);
    }
    this.resetFile();
  }

  private setErrorAnimation(statusCode: number, muteSnackbar?: boolean): void {
    // Hide errors, uploaded layout, and show progression bar.
    this.isLoadProgress$.next(false);
    this.isUploading$.next(false);
    this.isUploadFail$.next(false);

    // Show first animation.
    interval(1000)
      .pipe(take(1))
      .subscribe(() => {
        this.isUploading$.next(true);
      });

    // Show progress bar animation.
    interval(2400)
      .pipe(take(1))
      .subscribe(() => {
        this.isLoadProgress$.next(true);
      });

    // Set validate document fail.
    interval(2200)
      .pipe(take(1))
      .subscribe(() => {
        this.store.dispatch(
          validateDocumentFail({
            done: false,
            error: false,
            isCannotIdentify: null,
            progress: 10,
            isValidateBeforeUpload: true,
            errorStatusCode: statusCode,
          })
        );
        this.isUploading$.next(true);
      });

    // Show first animation.
    interval(2500)
      .pipe(take(1))
      .subscribe(() => {
        this.store.dispatch(
          validateDocumentFail({
            done: false,
            error: false,
            isCannotIdentify: null,
            progress: 50,
            isValidateBeforeUpload: true,
            errorStatusCode: statusCode,
          })
        );
        this.isUploading$.next(true);
      });

    interval(3000)
      .pipe(take(1))
      .subscribe(() => {
        this.store.dispatch(
          validateDocumentFail({
            done: false,
            error: false,
            isCannotIdentify: null,
            progress: 80,
            isValidateBeforeUpload: true,
            errorStatusCode: statusCode,
          })
        );
        this.isUploading$.next(true);
      });

    // Show progress bar animation.
    interval(4000)
      .pipe(take(1))
      .subscribe(() => {
        this.store.dispatch(
          validateDocumentFail({
            done: false,
            error: true,
            isCannotIdentify: null,
            progress: 100,
            isValidateBeforeUpload: true,
            errorStatusCode: statusCode,
          })
        );
        this.isLoadProgress$.next(true);
      });

    // Remove progress bar.
    interval(5200)
      .pipe(take(1))
      .subscribe(() => {
        this.checkScreen();
        this.isLoadProgress$.next(false);
        this.isUploading$.next(false);
        this.isUploadFail$.next(true);
        this.store.dispatch(unloadUploadFileAction());

        // Alert snackbar with error message.
        if (!muteSnackbar) {
          this.translate
            .get('flow.deposit.acquisition.label.errorFileUploading')
            .pipe(take(1))
            .subscribe((v) =>
              this.snackbar.openCustomSnackbar({
                message: v,
                type: 'error',
                icon: 'close',
              })
            );
        }
      });
  }

  private resetFile() {
    this.depositedFile.nativeElement.value = '';
  }

  constructor(
    private store: Store,
    private router: Router,
    private snackbar: SnackBarService,
    private translate: TranslateService
  ) {}

  handleDragover($event: DragEvent, isLeave?: boolean): void {
    if (isLeave) {
      if (screen.availWidth === 1366) {
        this.dragOpacity = '1';
        this.buttonOffsetX = '35%';
        this.titleOffsetX = '55%';
      } else {
        this.dragOpacity = '1';
        this.buttonOffsetX = '42%';
        this.titleOffsetX = '54%';
      }
    } else {
      if ($event.offsetY <= 30 || $event.offsetY >= 160) {
        this.buttonOffsetX = '30%';
        this.titleOffsetX = '53%';
        this.dragOpacity = '0.9';
      } else if ($event.offsetY <= 50 || $event.offsetY >= 150) {
        this.buttonOffsetX = '28%';
        this.dragOpacity = '0.7';
        this.titleOffsetX = '49%';
      } else if ($event.offsetY <= 60 || $event.offsetY >= 140) {
        this.buttonOffsetX = '25%';
        this.dragOpacity = '0.6';
        this.titleOffsetX = '47%';
      } else if ($event.offsetY <= 80 || $event.offsetY >= 100) {
        this.titleOffsetX = '45%';
        this.buttonOffsetX = '10%';
        this.dragOpacity = '0.0';
      }
    }
  }

  private checkScreen() {
    this.dragOpacity = '1';
    if (screen.availWidth === 1366) {
      this.buttonOffsetX = '35%';
      this.titleOffsetX = '55%';
    }
    if (screen.availWidth === 2560) {
      this.buttonOffsetX = '42%';
      this.titleOffsetX = '54%';
    }
  }

  @HostListener('window:resize', ['$event'])
  unloadHandler() {
    this.checkScreen();
  }

  getLimitSize(size: string): string {
    return FileUtils.getLimitSize(size);
  }
}
