import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { Store } from '@ngrx/store';
import {
  clearRecord,
  getMaxFileSizeUpload, loadCampaignSmsDetail,
  prepareUploadCsv,
  resetSmsCsv,
  selectCampaignSmsTemplate, selectCsvUploadMaxFileSize,
  selectLoading, selectSmsCampaign,
  selectSmsCsvData,
  selectSmsCsvError,
  selectSmsCsvFilter,
  selectSmsCsvRecord,
  selectSmsFormValue,
  selectSmsNavigation,
  selectSmsUploadingProgresssion,
  selectSmsUploadingState,
  setSmsUploadingBar,
  smsFilterCsvFilterChanged,
  smsFormChanged,
  smsInitStep,
  smsSubmitDestination,
  StepOnActivated,
  StepOnCampaign
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { BehaviorSubject, interval, Observable, Subject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import * as XLSX from 'xlsx';
import { appRoute, TemplateModel } from '@cxm-smartflow/template/data-access';
import { getTableContentMeta2, isValidCsvFile } from '@cxm-smartflow/follow-my-campaign/ui/feature-campaign-mail';
import { delay, filter, map, pluck, skip, take, takeUntil } from 'rxjs/operators';
import { TooltipDirective } from '@cxm-smartflow/shared/directives/tooltip';
import { PaginatorComponent } from '@cxm-smartflow/shared/ui/paginator';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { ActivatedRoute, Router } from '@angular/router';
import { FileUtils } from '@cxm-smartflow/shared/utils';

enum ProgressColor {
  // '#E50A0A'| '#3B82F6'| '#04C60B' = '#04C60B'
  Red = '#E50A0A',
  Blue = '#3B82F6',
  Green = '#04C60B'
}

@Component({
  selector: 'cxm-smartflow-sms-destination',
  templateUrl: './campaign-sms-destination.component.html',
  styleUrls: ['./sms-destination.component.scss'],
})
export class SmsDestinationComponent
  implements OnInit, OnDestroy {
  isLoading$ = new BehaviorSubject<boolean>(true);
  formGroup: FormGroup;
  template: TemplateModel;
  csvData: any;

  displayedColumns: string[] = [];
  columns: any[] = [];
  dataSource$ = new BehaviorSubject<any>(1);
  navigation = { prev: false, next: false };

  @ViewChild('fileupload') fileUpload: ElementRef<HTMLInputElement>;
  @ViewChild('csvInfoMsg', { static: true }) csvInfoMsg: TooltipDirective;
  csvInfoMsgActive = false;
  infoMessage: string;

  @ViewChild(PaginatorComponent, { static: true })
  paginator: PaginatorComponent;
  destoryed$ = new Subject();

  paginatinFilters$: Observable<any>;
  uploadingProgression$: Observable<string>;
  uploadingState$: Observable<any>;
  componentStage: 'prepared' | 'uploading' | 'uploaded' | undefined = undefined;
  errorDownloadMessage: string;
  uploadColorFail: ProgressColor = ProgressColor.Blue;
  maxLimitUploadSize: string;
  isNextBtnClickable = false;

  ngOnInit(): void {
    this.initEditForm();
    this.store.dispatch(smsInitStep({ step: 2 }));

    this.store.dispatch(
      StepOnActivated({ active: true, step: 2, specification: { stepFor: 'SMS' } })
    );
    this.store.dispatch(StepOnCampaign({ step: 2 }));
    this.store.dispatch(getMaxFileSizeUpload());

    this.store.select(selectCsvUploadMaxFileSize).pipe(takeUntil(this.destoryed$)).subscribe(
      data => {
        this.maxLimitUploadSize = data
      });
  }

  ngOnDestroy(): void {
    if (!this.csvData.count) {
      this.store.dispatch(StepOnActivated({ active: false, leave: true }));
    }
    this.destoryed$.next();
    this.destoryed$.complete();
    this.store.dispatch(clearRecord());
    // TODO: unload sms form
  }

  selectFile(event: any) {
    if (event && event.target) {
      const file = event.target.files[0];
      this.processCsvFile(file);
    }
    this.fileUpload.nativeElement.value = '';
  }

  handleDropFile(event: any) {
    if(event.type === 'drop') {
      const firstfile = event.files[0];
      this.store.dispatch(setSmsUploadingBar({ progression: 10, mode: 'sending', errorName: ''}));

      if(!isValidCsvFile(firstfile)) {
        // If the extension of file isn't type of CSV, alert error message to user.
        this.alertErrorMsg('invalidExtension');
      } else {
        this.errorDownloadMessage = '';
        this.processCsvFile(firstfile);
      }

    }
  }

  processCsvFile(file: File) {
    const { variables } = this.template;

    const reader = new FileReader();
    reader.onload = (e) => {
      this.processCsvBlob(
        e.target?.result,
        variables || []
      ).then(({ firstrow, matchLength }) => {
        if(firstrow.length === 0) {
          // If the CSV record is empty, alert error message to user.
          this.alertErrorMsg('emptyRecord');
        } else {
          // If the CSV record isn't empty, process uploading file.
          interval(600).pipe(take(1)).subscribe(() =>
            this.store.dispatch(prepareUploadCsv({ file, matchLength, firstrow })));
          }
        }
      );
    };
    reader.readAsBinaryString(file);
  }

  processCsvBlob(bufferedArray: any, header: string[]) {
    const wb = XLSX.read(bufferedArray, { type: 'binary' });
    const sheet = wb.SheetNames[0];
    const result = wb.SheetNames.map((sheetname) =>
      XLSX.utils.sheet_to_json(wb.Sheets[sheetname], {
        header: 'A',
        raw: false,
      })
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

  alertErrorMsg(errorType: 'invalidExtension' | 'emptyRecord'): void {
    this.translate.get('cxmCampaign.followMyCampaign')
      .pipe(take(1), delay(600))
      .subscribe(msg => {
        // Process to get error messages.
        this.errorDownloadMessage = `${errorType === 'invalidExtension'? msg.import_file_in_csv_format_message : msg.import_file_in_csv_empty_records} ${msg.import_file_upload_fail}`;
        // Process to show the progression of uploading error.
        this.store.dispatch(setSmsUploadingBar({ progression: 100, mode: 'fail', errorName: 'error_'  }));
        // Process to show error snackbar.
        this.snackbar.openCustomSnackbar({ type: 'error', icon: 'close', message: msg.csv_fail_reupload });
      });
  }

  navigateBack() {
    // if (this.navigation.prev) this.store.dispatch(smsAttempToStep({ step: 1 }));
    // this.componentStage = 'prepared';
    // this.store.dispatch(resetSmsCsv());
    this.requestToRemoveCsv();
  }

  navigateNext() {
    // if (this.navigation.next) this.store.dispatch(smsSubmitDestination());
    if(this.csvData.invalidCount > 0) {
      Promise.all([
        this.translate.get('cxmCampaign.followMyCampaign.destinationEmailCampaignPopUp.action_continue').toPromise(),
        this.translate.get('cxmCampaign.followMyCampaign.form_button_back_download').toPromise(),
        this.translate.get('cxmCampaign.followMyCampaign.destinationEmailCampaignPopUp.message_file_container_error').toPromise(),
        this.translate.get('cxmCampaign.followMyCampaign.destinationEmailCampaignPopUp.title_file_container_error', this.csvData).toPromise()
      ])
      .then(msg => {
        this.alert.showConfirmationPopup({ icon: 'info',
        title: msg[3],
        message: msg[2],
        cancelButton: msg[1],
        confirmButton: msg[0], type: 'Active',
        isNoEventBtnCancel: true
      }).subscribe(ok => {
        if(ok === true) { this.requestToSubmit(); }
        // TODO: Need to validate specification
        else if(ok === false) { this.store.dispatch(resetSmsCsv());};
      });
      });
    } else {
      this.requestToSubmit();
    }
  }

  requestToSubmit() {
    this.store.select(selectSmsCampaign).pipe(take(1)).subscribe(campaign => {
      this.router.navigateByUrl(`${appRoute.cxmCampaign.followMyCampaign.smsCampaignParameter}/${campaign.id}`);
    });
    this.store.dispatch(smsSubmitDestination({ isUpdate: true}));
  }

  setupTranslation() {
    Promise.all([
      this.translate
        .get('cxmCampaign.followMyCampaign.tooltip.must_format_csv')
        .toPromise(),
      this.translate
        .get('cxmCampaign.followMyCampaign.tooltip.must_match_size_csv')
        .toPromise(),
      this.translate
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

  toggleInfoMessage() {
    if (this.csvInfoMsg.getIsActive) {
      this.csvInfoMsg.disabledMouseEvent = false;
      this.csvInfoMsgActive = false;
    } else {
      this.csvInfoMsg.disabledMouseEvent = true;
      this.csvInfoMsgActive = true;
    }
    this.csvInfoMsg.toggle(this.csvInfoMsgActive);
  }


  paginationUpdated(page: PageEvent): void {
    this.store.dispatch(
      smsFilterCsvFilterChanged({
        filter: { page: page.pageIndex, pageSize: page.pageSize },
      })
    );
  }

  get hasHeader() {
    return this.formGroup.get('hasHeader');
  }

  get checkSameNumber() {
    return this.formGroup.get('checkSameNumber');
  }

  sortby($event: Sort) {

    this.store.dispatch(smsFilterCsvFilterChanged({ filter: { page: 1, pageSize: 10, sortByField: $event.active, sortDirection: $event.direction },
      })
    );
  }

  requestToRemoveCsv() {
    this.translate.get('cxmCampaign.followMyCampaign.destinationEmailCampaignPopUp').toPromise().then(msg => {
      this.alert.showConfirmationPopup({ icon: 'Warning',
        title: msg.remove_campaign_file, message: msg.remove_campaign_info,
        cancelButton: msg.remove_campaign_cancel,
        confirmButton: msg.remove_campaign_ok, type: 'Warning'
      }).subscribe(ok => {
        if(ok) {
          this.store.dispatch(resetSmsCsv());
        }
      });
    })

  }

  constructor(
    private store: Store,
    private translate: TranslateService,
    private fb: FormBuilder,
    private alert: ConfirmationMessageService,
    private router: Router,
    private snackbar: SnackBarService,
    private activateRoute: ActivatedRoute,
  ) {
    this.formGroup = fb.group({
      hasHeader: new FormControl(true),
      checkSameNumber: new FormControl(true),
    });

    this.formGroup.valueChanges
      .pipe(takeUntil(this.destoryed$))
      .subscribe((form) =>
        this.store.dispatch(
          smsFormChanged({
            hasHeader: form.hasHeader,
            checkSameNumber: form.checkSameNumber,
          })
        )
      );

    this.store
      .select(selectSmsFormValue)
      .pipe(takeUntil(this.destoryed$))
      .subscribe((formvalue) =>
        this.formGroup.patchValue(formvalue, { emitEvent: false })
      );

    // TODO: unsubscipt
    this.store
      .select(selectCampaignSmsTemplate)
      .pipe(takeUntil(this.destoryed$))
      .pipe(filter((x) => x !== undefined))
      .subscribe((t) => {
        this.template = t;
        const { variables } = this.template;
        if (variables) {
          this.displayedColumns = ['lineNumber', ...variables, 'valid'];
          this.columns = getTableContentMeta2(variables);
        }
      });

    this.store
      .select(selectLoading)
      .pipe(takeUntil(this.destoryed$))
      .subscribe((value) => this.isLoading$.next(value));

    this.store
      .select(selectSmsCsvData)
      .pipe(takeUntil(this.destoryed$))
      .subscribe((csv) => {
        this.csvData = csv;
      });

    this.store
      .select(selectSmsCampaign)
      .pipe(takeUntil(this.destoryed$))
      .subscribe(campaign => {
        if (!campaign) return;
        this.isNextBtnClickable = campaign?.details.errorCount !== campaign?.details.csvRecordCount;
      });

    this.store.select(selectSmsCsvError).pipe(takeUntil(this.destoryed$))
    .subscribe((csv) => {
      if(csv.csvOK === false && csv.matchLength !== 0) {

        Promise.all([
          this.translate.get('cxmCampaign.followMyCampaign.csv_container_column_not_expected').toPromise(),
          this.translate.get('cxmCampaign.followMyCampaign.csv_fail_reupload').toPromise()
        ])
        .then(msg => this.errorDownloadMessage = `${msg[0]} ${msg[1]}`);
      }
    });

    this.store
      .select(selectSmsCsvRecord)
      .pipe(takeUntil(this.destoryed$))
      .pipe(filter((x) => x !== undefined))
      .subscribe((record) => {
        if (this.template === undefined) return;
        this.dataSource$.next(record);
        this.componentStage = 'uploaded';
      });

    this.store
      .select(selectSmsNavigation)
      .pipe(takeUntil(this.destoryed$))
      .subscribe((nav) => (this.navigation = nav));

    this.paginatinFilters$ = this.store.select(selectSmsCsvFilter).pipe(takeUntil(this.destoryed$));

    // setup uploader
    this.uploadingProgression$ =
    this.store.select(selectSmsUploadingProgresssion).pipe(takeUntil(this.destoryed$)).pipe(map(value => value+'%'));

    this.store.select(selectSmsUploadingState).pipe(takeUntil(this.destoryed$), skip(1)).subscribe(state => {
      if(state.mode === 'idle') {
        this.componentStage = 'prepared';
        this.uploadColorFail = ProgressColor.Blue;
      } else if(state.mode === 'sending') {
        this.componentStage = 'uploading';
      } else if(state.mode === 'uploaded') {
        this.componentStage = 'uploading';
        interval(300).pipe(take(1)).toPromise().then(_ => this.uploadColorFail = ProgressColor.Green);
        interval(600).pipe(take(1)).toPromise().then(_ => this.componentStage = 'uploaded');


      } else if(state.mode === 'fail') {
        this.componentStage = 'uploading';
        interval(300).pipe(take(1)).toPromise().then(_ => this.uploadColorFail = ProgressColor.Red);
        interval(600).pipe(take(1)).toPromise().then(_ => {this.componentStage = 'prepared'; this.uploadColorFail = ProgressColor.Blue});
      } else if(state.mode === 'noloading') {
        this.componentStage = 'uploaded'
      }
    })
    this.setupTranslation();
  }

  getLimitSize(size: string): string {
    return FileUtils.getLimitSize(size);
  }

  initEditForm(): void {
    this.activateRoute.queryParams.pipe(take(1)).subscribe((params) => {
      if(params.id) {
        this.store.dispatch(loadCampaignSmsDetail({ campaignId: params.id }));
      } else {
        this.componentStage = 'prepared';
      }
    });
  }
}
