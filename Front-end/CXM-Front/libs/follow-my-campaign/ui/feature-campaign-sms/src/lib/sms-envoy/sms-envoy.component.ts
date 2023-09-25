import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import {
  CampaignModel,
  initLockableSmsForm,
  selectLoading,
  selectSmsCampaign,
  selectSmsCsvRecord,
  selectSmsNavigation,
  smsAttempToStep,
  smsInitStep,
  smsSubmitEnvoy,
  smsSubmitTestSendBat,
  StepOnActivated,
  StepOnCampaign,
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { CampaignConstant } from '@cxm-smartflow/shared/data-access/model';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { BehaviorSubject, Subject } from 'rxjs';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import {
  convertDateTimeToTimeStamp,
  StringUtil,
} from '@cxm-smartflow/shared/utils';
import { CanAccessibilityService } from '@cxm-smartflow/shared/data-access/services';

@Component({
  selector: 'cxm-smartflow-sms-envoy',
  templateUrl: './sms-envoy.component.html',
  styleUrls: ['./sms-envoy.component.scss']
})
export class SmsEnvoyComponent implements OnInit, AfterViewInit, OnDestroy {

  navigation = { next: false, prev: true };
  campaign: CampaignModel;
  previewFirstRecord = '';
  loading$ = new BehaviorSubject(false);
  destroyed$ = new Subject();

  smsFormGroup: FormGroup;

  //send test properties.
  sendTestFormGroup: FormGroup;
  sendTestHasSubmit = false;
  showVariableBgTooltip = true;
  recipientErrorLabel: any;

  recipient: string[] = [];

  canSendTest$ = new BehaviorSubject(false);
  isDuplicate = false;

  constructor(private formBuilder: FormBuilder, private store: Store,
              private translate: TranslateService,
              private canAccessibilityService: CanAccessibilityService) {
    this.smsFormGroup = this.formBuilder.group({
      sendingSchedule: new FormControl(new Date())
    });

    this.sendTestFormGroup = this.formBuilder.group({
      recipients: new FormArray([])
    });

    this.canSendTest$.next(this.canAccessibilityService.canAccessible(CampaignConstant.CXM_CAMPAIGN_SMS, CampaignConstant.TEST_SEND_SMS));
  }

  ngOnInit(): void {
    this.translate.get('cxmCampaign.followMyCampaign.generateEmail.dialog.message').subscribe(value => this.recipientErrorLabel = value);
    this.store.dispatch(StepOnActivated({ active: true, step: 4, specification: { stepFor: 'SMS' } }));
    this.store.dispatch(StepOnCampaign({ step: 4 }));
    this.store.select(selectSmsNavigation).pipe(takeUntil(this.destroyed$)).subscribe(navigation => this.navigation = navigation);
    // Add first default sms recipient.
    this.addRecipient('');
  }

  ngAfterViewInit(): void {
    this.store.select(selectSmsCampaign).pipe(takeUntil(this.destroyed$)).subscribe((campaign: CampaignModel) => {
      this.campaign = campaign;
      // Initialize sending schedule.
      this.sendingSchedule?.setValue(campaign?.sendingSchedule || new Date());

      // Init step 4.
      this.store.dispatch(smsInitStep({ step: 4 }));
    });

    this.store.select(selectSmsCsvRecord).pipe(takeUntil(this.destroyed$)).subscribe(records => {
      if (records && records.length > 0 && this.campaign) {
        const { details } = this.campaign;
        let previewTemplate = details?.htmlTemplate;
        const firstRow = records[0];
        Object.keys(firstRow).forEach(k => {
          previewTemplate = StringUtil.replaceAll(previewTemplate || '', `{${k}}`, firstRow[k]);
        });

        this.previewFirstRecord = previewTemplate || '';
      }
    });

    this.store.select(selectLoading).pipe(takeUntil(this.destroyed$)).subscribe(v => this.loading$.next(v));
    this.store.dispatch(initLockableSmsForm({ isLock: true }));

    this.recipientsCtr.valueChanges.pipe(
      distinctUntilChanged(),
      debounceTime(500))
      .pipe(takeUntil(this.destroyed$))
      .subscribe((recipients) => {
        this.recipient = [];
        this.recipient = recipients;

        // Close error message.
        this.sendTestHasSubmit = false;
      });
  }

  ngOnDestroy(): void {
    this.store.complete();
    this.destroyed$.complete();
  }

  isDuplicateSMS(index: number): boolean {
    const allSMS = this.recipientsCtr.controls.map(sms => sms.value);
    const notDuplicateSMS = Array<string>();
    const duplicateSMS = Array<string>();
    let duplicateSMSIndex = Array<number>();
    if (allSMS.length > 1) {
      allSMS.forEach((sms: string, index: number) => {
        if (!notDuplicateSMS.includes(sms)) {
          notDuplicateSMS.push(sms);
        } else {
          duplicateSMS.push(sms);
          duplicateSMSIndex.push(index);
        }
      });
      if (duplicateSMS.length) {
        duplicateSMS.forEach((sms: string) => {
          if (allSMS.includes(sms)) {
            this.isDuplicate = true;
          }
        });
      } else {
        this.isDuplicate = false;
        duplicateSMSIndex = [];
      }
    }
    const isValidPhoneNumber = allSMS[index].length && testValidPhoneNumber(allSMS[index]);
    this.isDuplicate = this.isDuplicate && duplicateSMSIndex.length > 0 && duplicateSMSIndex.includes(index);
    return this.isDuplicate && isValidPhoneNumber;
  }

  navigateBack() {
    this.store.dispatch(initLockableSmsForm({ isLock: false }));
    this.store.dispatch(smsAttempToStep({ step: 3 }));
  }

  navigateNext() {
    this.store.dispatch(initLockableSmsForm({ isLock: false }));
    this.store.dispatch(smsSubmitEnvoy({ sendingSchedule: convertDateTimeToTimeStamp(this.sendingSchedule?.value) }));
  }

  get sendingSchedule() {
    return this.smsFormGroup.get('sendingSchedule');
  }

  /////////////////// send test //////////////
  submitSentTest() {
    if (this.sendTestFormGroup.valid && !this.isDuplicate) {
      this.store.dispatch(smsSubmitTestSendBat({ recipients: this.recipientsCtr?.value }));
    } else {
      this.sendTestHasSubmit = true;
    }
  }

  get recipientsCtr(): FormArray {
    return this.sendTestFormGroup.get('recipients') as FormArray;
  }

  get isAddBtnInvisible(): boolean {
    return this.recipientsCtr?.controls.length >= 5;
  }

  addRecipient(value = ''): void {
    if (this.isAddBtnInvisible) return;

    this.recipientsCtr.insert(this.recipientsCtr.length, new FormControl(value, [Validators.required, phoneNumberValidator()]));
  }

  removeRecipient(index: number): void {
    this.recipientsCtr.removeAt(index);
  }

  getVariableTooltip(item: any, index: number) {
    if (this.isDuplicateSMS(index)) {
      return this.recipientErrorLabel?.duplicate;
    }
    if (item?.errors?.required) {
      return this.recipientErrorLabel?.phoneRequired;
    }
    return this.recipientErrorLabel?.invalidPhoneNumber;
  }

  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(
      this.recipient,
      event.previousIndex,
      event.currentIndex
    );

    this.recipientsCtr.setValue(this.recipient);
  }

  /////////////////////////////////////////////////

  dateTimeChange(event: any) {
    this.sendingSchedule?.setValue(event);
  }
}

export function phoneNumberValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {

    if (control?.value?.trim() === '') {
      return null;
    }

    const isValid = testValidPhoneNumber(control.value);

    return isValid === false ? { invalidPhoneNumber: { value: control.value } } : null;
  };
}

const testValidPhoneNumber = (phoneNumbe: string) => {

  phoneNumbe = phoneNumbe.replace(/[,| |.|-|(|)]+/g, '');

  let firstScan = phoneNumbe.substring(0, 2);

  if (firstScan === '00') {
    phoneNumbe = phoneNumbe.replace(firstScan, '+');
  }

  let isPlus = phoneNumbe.substring(0, 1);
  if (isPlus !== '') {
    isPlus = '';
  } else {
    firstScan = phoneNumbe.substring(1, 1);
    if (firstScan == '0') isPlus = '';
  }

  phoneNumbe = phoneNumbe.replace(/(\s|\D)/, '');
  phoneNumbe = isPlus + phoneNumbe;

  if (/(\+|0){1}[1-9]{1}[0-9]{8,11}/.test(phoneNumbe)) {
    if (isPlus && phoneNumbe.length > 10 && phoneNumbe.length <= 13) return phoneNumbe;
    else if (!isPlus && phoneNumbe.length == 10) return phoneNumbe;
  }

  return false;
};
