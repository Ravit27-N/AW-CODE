import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import {
  getSmsMetadata,
  initLockableSmsForm,
  selectLoading,
  selectSmsCampaign, selectSMSMetadata, selectSmsNavigation, smsAttempToStep,
  smsInitStep, smsParameterFormChanged, smsSubmitParameter,
  StepOnActivated,
  StepOnCampaign
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { filter, map, take, takeUntil } from 'rxjs/operators';
import { SmsParameterValidator } from './sms-parameter-validator';
import { InputSelectionCriteria } from '@cxm-smartflow/shared/ui/form-input-selection';

@Component({
  selector: 'cxm-smartflow-sms-parameter-new',
  templateUrl: './sms-parameter-new.component.html',
  styleUrls: ['./sms-parameter-new.component.scss']
})
export class SmsParameterNewComponent implements OnInit, OnDestroy{

  navigation: { prev: true, next: false }

  formGroup: FormGroup;
  formHasSubmit = false;
  loading$ = new BehaviorSubject(false);
  metadataResponse$: Observable<InputSelectionCriteria[]>;
  selectSenderLabel$: Observable<string>;
  destroyed$ = new Subject();

  // tooltip properties.
  showTooltipBackground = false;
  errorLabel: any;

  constructor(private translate: TranslateService,
              private fb: FormBuilder,
              private store: Store) {
    this.formGroup = this.fb.group({
      campaignName: new FormControl('', [Validators.required, Validators.maxLength(128)]),
      senderName: new FormControl('', [SmsParameterValidator.fieldSenderLabel(), Validators.maxLength(128)])
    });
  }

  ngOnInit(): void {
    this.translate.get('cxmCampaign.followMyCampaign.settingParameter').pipe(take(1)).subscribe(value => this.errorLabel = value);

    this.store.dispatch(StepOnActivated({ active: true, step: 3, specification: { stepFor: 'SMS' } }));
    this.store.dispatch(StepOnCampaign({  step: 3 }));

    this.store.select(selectSmsCampaign)
      .pipe(takeUntil(this.destroyed$))
      .pipe(filter(x => x !== undefined))
      .subscribe(campaign => {
        this.formGroup.patchValue({
            campaignName: campaign.campaignName,
            senderName: campaign.senderName
          },
          { emitEvent: false });

        // Init step 3.
        this.store.dispatch(smsInitStep({ step: 3 }));
      });

    this.formGroup.valueChanges
      .pipe(takeUntil(this.destroyed$))
      .subscribe(formValue => this.store.dispatch(smsParameterFormChanged(formValue)));

    this.store.select(selectSmsNavigation).subscribe(navigation => this.navigation =  navigation);
    this.store.select(selectLoading).subscribe(v => this.loading$.next(v));
    this.store.select(selectSmsNavigation).pipe(takeUntil(this.destroyed$)).subscribe(navigation => this.navigation =  navigation);
    this.store.dispatch(initLockableSmsForm({isLock: true}));
    this.store.dispatch(getSmsMetadata());
    this.metadataResponse$ = this.store.select(selectSMSMetadata).pipe(
      filter(data => Boolean(data.smsSenderLabel)),
      map(data => (data.smsSenderLabel.map((data) => ({ key: data.value, value: data.value }))))
    );

    this.selectSenderLabel$ = this.store.select(selectSmsCampaign).pipe(
      filter(data => Boolean(data)),
      map(data => data.senderName)
    );
  }

  ngOnDestroy(): void {
    this.store.complete();
    this.destroyed$.complete();
  }

  navigateNext() {
    if(this.formGroup.valid){
      this.store.dispatch(initLockableSmsForm({isLock: false}));
      this.store.dispatch(smsSubmitParameter());
    }else{
      this.formHasSubmit = true;
    }
  }

  navigatePrev() {
    this.store.dispatch(initLockableSmsForm({ isLock: false }));
    if (this.navigation.prev) this.store.dispatch(smsAttempToStep({ step: 2 }));
  }

  get campaignName(){
    return this.formGroup.get('campaignName');
  }

  get campaignNameTooltip(){
    if(this.campaignName?.errors?.required){
      return this.errorLabel?.validation?.required;
    }
    return this.errorLabel?.validation?.maxLength;
  }

  get senderName(){
    return this.formGroup.get('senderName');
  }

  get senderNameTooltip(){
    if(this.senderName?.errors?.required){
      return this.errorLabel?.validation?.required;
    }
    return this.errorLabel?.validation?.maxLength;
  }

  selectSenderLabel(value: string) {
    this.formGroup.controls['senderName'].setValue(value);
  }
}
