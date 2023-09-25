import { Location } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { appRoute, TemplateModel } from '@cxm-smartflow/template/data-access';
import {
  CsvFileData,
  csvFormValueChange,
  emailFilterCsvFilterChanged,
  getMaxFileSizeUpload,
  loadDestinationForm,
  loadEmailCampaignDetail,
  loadTemplateDetail,
  prepareEmailCsvUpload,
  resetCampaignCsv,
  select2Lockable,
  selectCampaignDetail,
  selectCsvError,
  selectCsvState,
  selectCsvUploadMaxFileSize,
  selectEmailCsvFilter,
  selectEmailCsvForm,
  selectEmailCsvRecord,
  selectTemplateDetails,
  selectUploadingProgresssion,
  selectUploadingState,
  setIsHeaderChange,
  setUploadingBar,
  StepOnActivated,
  StepOnCampaign,
  unloadDestinationForm,
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { interval, Observable, ReplaySubject, Subject } from 'rxjs';
import {
  delay,
  distinctUntilChanged,
  filter,
  map,
  pluck,
  skip,
  take,
  takeUntil,
} from 'rxjs/operators';
import * as XLSX from 'xlsx';
import { getTableContentMeta2, isValidCsvFile } from './destination-csv.utils';
import { RejectedMailService } from './rejected-mail/rejected-mail.service';
import { PageEvent } from '@angular/material/paginator';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { Sort } from '@angular/material/sort';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { ILockableForm } from '@cxm-smartflow/flow-deposit/guard/pending-change';
import { FileUtils } from '@cxm-smartflow/shared/utils';

enum CsvErrorType {
  MORE_TOOLTIP = 'cxmCampaign.followMyCampaign.csv_contain_more_column_tooltip',
  FEWER_TOOLTIP = 'cxmCampaign.followMyCampaign.csv_contain_fewer_column_tooltip',
  COMPLIANT_FILE_MESSAGE = 'cxmCampaign.followMyCampaign.import_compliant_file_message',
  NOT_CSV_FILE_TOOLTIP = 'cxmCampaign.followMyCampaign.file_not_csv_tooltip',
  CSV_FILE_FORMAT_MESSAGE = 'cxmCampaign.followMyCampaign.import_file_in_csv_format_message',

  NO_VALID_LINE_TOOLTIP = 'cxmCampaign.followMyCampaign.csv_file_no_valid_line_tooltip',
  NO_VALID_LINE_MESSAGE = 'cxmCampaign.followMyCampaign.import_file_at_least_one_recipient_message',
}

enum ProgressColor {
  Red = '#E50A0A',
  Blue = '#3B82F6',
  Green = '#04C60B',
}

@Component({
  selector: 'cxm-smartflow-email-destination',
  templateUrl: './campaign-email-destination.component.html',
  styleUrls: ['./email-destination.component.scss'],
})
export class EmailDestinationComponent
  implements OnInit, OnDestroy, ILockableForm
{
  // Template properties.
  dataSource$ = new ReplaySubject<any>(1);
  columns: any = [];
  displayedColumns: any = [];
  emailTemplateModel: TemplateModel;
  paginationFilters$: Observable<any>;
  infoMessage = '';
  csvFileData: CsvFileData;
  formGroup: FormGroup;

  // Validation properties.
  componentStage: 'prepared' | 'uploading' | 'uploaded' |  undefined = undefined;
  errorDownloadMessage: string;
  maxLimitUploadSize: string;
  uploadColorFail: ProgressColor = ProgressColor.Blue;
  uploadingProgression$: Observable<string>;
  canProceedNext = false;
  private _queryParams: any;
  private _isEditMode: boolean;

  // Unsubscribe properties.
  private _destroy$ = new Subject<boolean>();

  /**
   * Constructor
   */
  constructor(
    private _fb: FormBuilder,
    private _store: Store,
    private _translateService: TranslateService,
    private _activatedRoute: ActivatedRoute,
    private _location: Location,
    private _rejectedMailDialog: RejectedMailService,
    private _snackBar: SnackBarService,
    private _confirmMsgService: ConfirmationMessageService,
    private _router: Router,
    private _activateRoute: ActivatedRoute
  ) {
    this.formGroup = _fb.group({
      hasHeader: new FormControl(false),
      checkSameMail: new FormControl(false),
    });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  /**
   * On init
   */
  ngOnInit(): void {
    this._trackingRoute().then();
    // Initial state to edit form.
    this._initEditForm();

    // Get the maximum file size for presenting the maximum file size user can upload.
    this._store.dispatch(getMaxFileSizeUpload());
    this._store.select(selectCsvUploadMaxFileSize).pipe(takeUntil(this._destroy$)).subscribe((data) => {
        this.maxLimitUploadSize = data;
    });

    // Activate step.
    this._store.dispatch(StepOnActivated({ active: true, step: 2 }));

    // Tracking form.
    this.formGroup.valueChanges.pipe(takeUntil(this._destroy$)).subscribe((form) => {
        this._store.dispatch(csvFormValueChange({ form }));
    });

    // Check error CSV record and setup error message.
    this._store.select(selectCsvError).pipe(takeUntil(this._destroy$)).subscribe((value) => {
        if (value.csvOK === false && value.matchLength !== 0) {
          Promise.all([
            this._translateService
              .get(
                'cxmCampaign.followMyCampaign.csv_container_column_not_expected'
              )
              .toPromise(),
            this._translateService
              .get('cxmCampaign.followMyCampaign.csv_fail_reupload')
              .toPromise(),
          ]).then((msg) => (this.errorDownloadMessage = `${msg[0]} ${msg[1]}`));
        }
    });

    this._store.select(selectCsvState).pipe(takeUntil(this._destroy$)).subscribe((value) => {
      if(value?.count) {
        this.dataSource$.next([]);
      }
      this.csvFileData = value;
    });

    this._store.select(selectCampaignDetail).pipe(takeUntil(this._destroy$), skip(1)).subscribe(state => {
      const { details } = state;
      this.canProceedNext = (details.csvRecordCount - details.errorCount > 0);
    });

    const { templateId } = this._activatedRoute.snapshot.queryParams;
    this._store.dispatch(loadDestinationForm({ templateId }));
    this._setupTranslate();

    // load template detail
    this._store.dispatch(loadTemplateDetail({ templateId }));
    this._store
      .select(selectTemplateDetails)
      .pipe(takeUntil(this._destroy$))
      .pipe(filter((x) => x !== undefined))
      .subscribe((v) => {
        this.emailTemplateModel = v;
        const { variables } = this.emailTemplateModel;
        if (variables) {
          this.displayedColumns = ['lineNumber', ...variables, 'valid'];
          this.columns = getTableContentMeta2(variables);
        }
      });

    this._store.dispatch(StepOnCampaign({ step: 2 }));

    this._store
      .select(selectEmailCsvRecord)
      .pipe(takeUntil(this._destroy$))
      .subscribe((csvRecord) => {
        this.dataSource$.next(csvRecord);
      });

    this.paginationFilters$ = this._store
      .select(selectEmailCsvFilter)
      .pipe(takeUntil(this._destroy$));

    this._store
      .select(selectEmailCsvForm)
      .pipe(takeUntil(this._destroy$))
      .subscribe((form) =>
        this.formGroup.patchValue(form, { emitEvent: false })
      );

    this.uploadingProgression$ = this._store
      .select(selectUploadingProgresssion)
      .pipe(takeUntil(this._destroy$))
      .pipe(map((value) => value + '%'));

    this._store
      .select(selectUploadingState)
      .pipe(distinctUntilChanged(), skip(1))
      .pipe(takeUntil(this._destroy$))
      .subscribe((state) => {
        if (state.mode === 'idle') {
          this.componentStage = 'prepared';
          this.uploadColorFail = ProgressColor.Blue;
        } else if (state.mode === 'sending') {
          this.componentStage = 'uploading';
        } else if (state.mode === 'uploaded') {
          this.componentStage = 'uploading';
          interval(300)
            .pipe(take(1))
            .toPromise()
            .then((_) => (this.uploadColorFail = ProgressColor.Green));
          interval(600)
            .pipe(take(1))
            .toPromise()
            .then((_) => (this.componentStage = 'uploaded'));
        } else if (state.mode === 'fail') {
          this.componentStage = 'uploading';
          interval(300)
            .pipe(take(1))
            .toPromise()
            .then((_) => (this.uploadColorFail = ProgressColor.Red));
          interval(600)
            .pipe(take(1))
            .toPromise()
            .then((_) => {
              this.componentStage = 'prepared';
              this.uploadColorFail = ProgressColor.Blue;
            });
        } else if (state.mode === 'noloading') {
          this.componentStage = 'uploaded';
        }
      });
  }

  /**
   * On destroy
   */
  ngOnDestroy(): void {
    this._store.dispatch(setIsHeaderChange({ isCheckHeaderChange: false }));
    this._destroy$.next(true);
    this._store.dispatch(unloadDestinationForm());
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Accessors
  // -----------------------------------------------------------------------------------------------------

  get hasHeader() {
    return this.formGroup.get('hasHeader');
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  /**
   * Handle drop CSV file.
   * @param event
   */
  handleDropFile(event: any) {
    if (event.type === 'drop') {
      const firstFile = event.files[0];

      this._store.dispatch(
        setUploadingBar({ progression: 10, mode: 'sending', errorName: '' })
      );

      if (!isValidCsvFile(firstFile)) {
        // If the extension of file isn't type of CSV, alert error message to user.
        this._alertErrorMsg('invalidExtension');
      } else {
        this.errorDownloadMessage = '';
        this._processCsvFile(firstFile);
      }
    }
  }

  /**
   * Sort data.
   * @param $event
   */
  sortBy($event: Sort) {
    this._store.dispatch(
      emailFilterCsvFilterChanged({
        filter: {
          page: 1,
          pageSize: 10,
          sortByField: $event.active,
          sortDirection: $event.direction,
        },
      })
    );
  }

  /**
   * Change pagination.
   * @param page
   */
  changePagination(page: PageEvent): void {
    this._store.dispatch(
      emailFilterCsvFilterChanged({
        filter: { page: page.pageIndex, pageSize: page.pageSize },
      })
    );
  }

  /**
   * Request navigate to next step.
   */
  next(): void {
    if (this.csvFileData.invalidCount > 0) {
      Promise.all([
        this._translateService
          .get(
            'cxmCampaign.followMyCampaign.destinationEmailCampaignPopUp.action_continue'
          )
          .toPromise(),
        this._translateService
          .get('cxmCampaign.followMyCampaign.form_button_back_download')
          .toPromise(),
        this._translateService
          .get(
            'cxmCampaign.followMyCampaign.destinationEmailCampaignPopUp.message_file_container_error'
          )
          .toPromise(),
        this._translateService
          .get(
            'cxmCampaign.followMyCampaign.destinationEmailCampaignPopUp.title_file_container_error',
            this.csvFileData
          )
          .toPromise(),
      ]).then((msg) => {
        this._confirmMsgService
          .showConfirmationPopup({
            icon: 'info',
            title: msg[3],
            message: msg[2],
            cancelButton: msg[1],
            confirmButton: msg[0],
            type: 'Active',
            isNoEventBtnCancel: true,
          })
          .pipe(take(1))
          .subscribe((ok) => {
            if (ok === true) {
              this._requestToSubmit().then();
            } else if (ok === false) {
              this._store.dispatch(resetCampaignCsv());
            }
          });
      });
    } else {
      this._requestToSubmit().then();
    }
  }

  /**
   * Request navigate to previous step.
   */
  previous(): void {
    this.requestToRemoveCsv();
  }

  /**
   * Hide CSV table and show the form for uploading new CSV.
   */
  requestToRemoveCsv() {
    this._translateService
      .get('cxmCampaign.followMyCampaign.destinationEmailCampaignPopUp')
      .toPromise()
      .then((msg) => {
        this._confirmMsgService
          .showConfirmationPopup({
            icon: 'Warning',
            title: msg.remove_campaign_file,
            message: msg.remove_campaign_info,
            cancelButton: msg.remove_campaign_cancel,
            confirmButton: msg.remove_campaign_ok,
            type: 'Warning',
          })
          .subscribe((ok) => {
            if (ok) {
              this._store.dispatch(resetCampaignCsv());
            }
          });
      });
  }

  /**
   * Transform a file size label.
   * @param size
   */
  getLimitSize(size: string): string {
    return FileUtils.getLimitSize(size);
  }

  /**
   * Get all confirm leave messages dialog.
   */
  getLockedReason(): string[] {
    return [];
  }

  /**
   * Get component should be locked.
   */
  isLocked(): Observable<boolean> {
    return this._store.select(select2Lockable).pipe(take(1));
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  /**
   * Fill the campaign-destination form.
   *
   * - Request to get campaign detail.
   * - Request to get CSV records.
   */
  private _initEditForm(): void {
    // Get query params.
    this._activateRoute.queryParams
      .pipe(take(1), pluck('id'))
      .subscribe((campaignId) => {
        if (campaignId) {
          this._store.dispatch(loadEmailCampaignDetail({ campaignId }));
        } else {
          this.componentStage = 'prepared';
        }
      });
  }

  /**
   * Get translation messages.
   */
  private _setupTranslate() {
    Promise.all([
      this._translateService
        .get('cxmCampaign.followMyCampaign.tooltip.must_format_csv')
        .toPromise(),
      this._translateService
        .get('cxmCampaign.followMyCampaign.tooltip.must_match_size_csv')
        .toPromise(),
      this._translateService
        .get('cxmCampaign.followMyCampaign.tooltip.import_suggestion')
        .toPromise(),
    ]).then((array: string[]) => {
      this.infoMessage =
        '<ul class="list-disc">' +
        array
          .map((x) => `<li>${x}</li>`)
          .reduce((cur, prev) => cur + prev, '') +
        '</ul>';
    });
  }

  /**
   * Process CSV file.
   * @param file
   */
  private _processCsvFile(file: File) {
    const { variables } = this.emailTemplateModel;
    const reader = new FileReader();
    reader.onload = (e) => {
      this._processCsvBlob(e.target?.result, variables || []).then(
        ({ firstrow, matchLength }) => {
          if (firstrow.length === 0) {
            // If the CSV record is empty, alert error message to user.
            this._alertErrorMsg('emptyRecord');
          } else {
            // If the CSV record isn't empty, process uploading file.
            interval(600)
              .pipe(take(1))
              .subscribe(() => {
                this._store.dispatch(
                  prepareEmailCsvUpload({ file, matchLength, firstrow })
                );
              });
          }
        }
      );
    };
    reader.readAsBinaryString(file);
  }

  /**
   * Process CSV blob.
   * @param bufferedArray
   * @param header
   */
  private _processCsvBlob(bufferedArray: any, header: string[]) {
    const wb = XLSX.read(bufferedArray, { type: 'binary' });
    const result = wb.SheetNames.map((sheetname) =>
      XLSX.utils.sheet_to_json(wb.Sheets[sheetname], { header: 'A' })
    );
    let firstrow = this.hasHeader?.value
      ? (result[0][1] as any)
      : (result[0][0] as any);

    if (!firstrow) {
      return Promise.resolve({ firstrow: [], matchLength: 0 });
    }

    firstrow = Object.keys(firstrow).map((k, index) => firstrow[k]);
    const matchLength = header.length - firstrow.length;

    return Promise.resolve({ firstrow, matchLength });
  }

  /**
   * Alert Error messages.
   * @param errorType
   */
  private _alertErrorMsg(errorType: 'invalidExtension' | 'emptyRecord'): void {
    this._translateService.get('cxmCampaign.followMyCampaign').pipe(take(1), delay(600))
      .subscribe((msg) => {
        // Process to get error messages.
        this.errorDownloadMessage = `${
          errorType === 'invalidExtension'
            ? msg.import_file_in_csv_format_message
            : msg.import_file_in_csv_empty_records
        } ${msg.import_file_upload_fail}`;
        // Process to show the progression of uploading error.
        this._store.dispatch(setUploadingBar({ progression: 100, mode: 'fail', errorName: 'error_' }));
        // Process to show error snackbar.
        this._snackBar.openCustomSnackbar({ type: 'error', icon: 'close', message: msg.csv_fail_reupload });
      });
  }

  /**
   * Request to submit campaign destination.
   */
  private async _requestToSubmit() {
    this._activateRoute.queryParams.pipe(take(1), pluck('id'), filter(e => e)).subscribe(id => {
        this._router.navigate([appRoute.cxmCampaign.followMyCampaign.emailCampaignParameter, id]);
    });
  }

  private async _trackingRoute(): Promise<void> {
    this._activatedRoute.queryParams.pipe(takeUntil(this._destroy$)).subscribe(async (params) => {

      if (params?.id) {
        this._isEditMode = true;
        this._queryParams = this._queryParams?.id? this._queryParams: params;
      }

      if (this._isEditMode) {
        this._router.navigate([appRoute.cxmCampaign.followMyCampaign.emailCampaignDestination], { queryParams: this._queryParams }).then();
        this._store.dispatch(loadEmailCampaignDetail({ campaignId: this._queryParams.id }))
      }
    });
  }
}
