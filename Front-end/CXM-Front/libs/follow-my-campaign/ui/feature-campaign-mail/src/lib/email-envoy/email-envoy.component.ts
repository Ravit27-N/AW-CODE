import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Location } from '@angular/common';
import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ILockableForm } from '@cxm-smartflow/flow-deposit/guard/pending-change';
import {
  CampaignModel,
  previousEmailCampaignEnvoiStep,
  selectCampaignDetail,
  selectEmailTemplateSources,
  selectIsLoading,
  selectTemplateDetails,
  sendMailTest,
  StepOnActivated,
  StepOnCampaign,
  submitEmailCampaignSummaryStep,
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { IAppSettings } from '@cxm-smartflow/shared/app-config';
import { ConfigurationService } from '@cxm-smartflow/shared/data-access/api';
import {
  CampaignConstant,
  EMAIL_PATTERN,
} from '@cxm-smartflow/shared/data-access/model';
import { CanAccessibilityService } from '@cxm-smartflow/shared/data-access/services';
import {
  convertDateTimeToTimeStamp,
  StringUtil,
} from '@cxm-smartflow/shared/utils';
import { templateEnv as cxmTemplateEnvironment } from '@env-cxm-template';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable, of, Subject, Subscription } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  takeUntil,
} from 'rxjs/operators';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'cxm-smartflow-email-envoy',
  templateUrl: './email-envoy.component.html',
  styleUrls: ['./email-envoy.component.scss'],
})
export class EmailEnvoyComponent implements OnInit, AfterViewInit, OnDestroy, ILockableForm {

  //email campaign properties.
  emailCampaignFormGroup: FormGroup;
  emailCampaign: CampaignModel;
  fileUrl$ = new BehaviorSubject('');
  isLoading$ = new BehaviorSubject(false);
  destroyed$ = new Subject();
  campaignSubscription$: Subscription;

  //send test properties.
  sendTestFormGroup: FormGroup;
  sendTestHasSubmit = false;
  showVariableBgTooltip = true;
  variableErrorLabel: any;

  emailFields: string [] = [];
  isLock: boolean;
  settings: IAppSettings;
 emailTemplateSources$: Observable<string>;

  canSendTest$ = new BehaviorSubject(false);
  isDuplicate = false;

  constructor(private formBuilder: FormBuilder, private store: Store, private location: Location,
              private translate: TranslateService, private changeDetectionRef: ChangeDetectorRef,
              configuration: ConfigurationService,
              private canAccessibilityService: CanAccessibilityService,
              private sanitizer: DomSanitizer
              ) {

    Object.assign(this, { settings: configuration.getAppSettings() });

    this.emailCampaignFormGroup = this.formBuilder.group({
      sendingSchedule: new FormControl(new Date, [Validators.required])
    });

    this.sendTestFormGroup = this.formBuilder.group({
      emails: new FormArray([])
    });

    this.canSendTest$.next(this.canAccessibilityService.canAccessible(CampaignConstant.CXM_CAMPAIGN, CampaignConstant.TEST_SEND_MAIL));
  }

  ngOnInit(): void {
    this.store.dispatch(StepOnActivated({ active: true, step: 4 }));
    this.translate.get('cxmCampaign.followMyCampaign.generateEmail.dialog.message')
      .subscribe(value => this.variableErrorLabel = value);
    this.isLock = true;
    // Add first default email.
    this.addEmail('');
  }

  ngAfterViewInit(): void {
    this.changeDetectionRef.detectChanges();

    this.campaignSubscription$ = this.store.select(selectCampaignDetail).pipe(takeUntil(this.destroyed$))
      .subscribe(campaign => {
        this.emailCampaign = campaign;

        // initialize to sendingSchedule.
        this.sendingSchedule?.setValue(campaign?.sendingSchedule || new Date());
      });

    // Init step 4.
    this.store.dispatch(StepOnCampaign({ step: 4 }));

    this.store.select(selectTemplateDetails).subscribe(v => this.fileUrl$.next(`${this.settings.apiGateway}${cxmTemplateEnvironment.templateContext}/templates/composition/load-file/${v?.fileName}`));
    this.store.select(selectIsLoading).subscribe(v => this.isLoading$.next(v));

    this.emails.valueChanges.pipe(
      distinctUntilChanged(),
      debounceTime(500))
      .pipe(takeUntil(this.destroyed$))
      .subscribe((emails) => {
        this.emailFields = [];
        this.emailFields = emails;

        // close error message;
        this.sendTestHasSubmit = false;
      });


    this._getEmailTemplate();
  }

  ngOnDestroy(): void {
    this.store.complete();
    this.fileUrl$.complete();
    this.isLock = false;
    this.campaignSubscription$.unsubscribe();
    this.canSendTest$.complete();
  }

  isDuplicateEmail(index: number): boolean {
    const allEmail = this.emails.controls.map(email => email.value);
    const notDuplicateEmails = Array<string>();
    const duplicateEmails = Array<string>();
    let duplicateEmailIndex = Array<number>();
    if (allEmail.length > 1) {
      allEmail.forEach((email: string, index: number) => {
        if (!notDuplicateEmails.includes(email)) {
          notDuplicateEmails.push(email);
        } else {
          duplicateEmails.push(email);
          duplicateEmailIndex.push(index);
        }
      });
      if (duplicateEmails.length) {
        duplicateEmails.forEach((email: string) => {
          if (allEmail.includes(email)) {
            this.isDuplicate = true;
          }
        });
      } else {
        this.isDuplicate = false;
        duplicateEmailIndex = [];
      }
    }
    const isValidEmail = allEmail[index].length && new FormControl(allEmail[index], [Validators.pattern(EMAIL_PATTERN)]).valid;
    this.isDuplicate = this.isDuplicate && duplicateEmailIndex.length > 0 && duplicateEmailIndex.includes(index);
    return this.isDuplicate && isValidEmail;
  }

  private _getEmailTemplate(): void {
    this.emailTemplateSources$ = this.store.select(selectEmailTemplateSources)
      .pipe(
        filter(e => e.emailRecord.length > 0 && e.templateDetails),
        distinctUntilChanged((prev, curr) => JSON.stringify(prev) === JSON.stringify(curr)),
        map(state => {
          console.log({state})
          const firstRecord = state.emailRecord[0];
          let htmlSource = state?.templateDetails?.htmlFile;

          const exceptVars = ['unsubscribeLink', 'Mirror page'];
          const variables = [...new Set(state?.templateDetails?.variables), ...exceptVars];
          console.log({variables})
          const valuePair = variables.map(e => { return { key: e, value: firstRecord[`${e}`] } });

          const mirrorPage = `<span>Si le message ne s'affiche pas correctement, cliquez ici.</span>`;
          const unsubscribeLink = state.unsubscribeLink?.replace("mailto:", "");

          valuePair
            .forEach(e => {
              if (e.key === exceptVars[0]) {
                e.value = unsubscribeLink;
              }
              if (e.key === exceptVars[1]) {
                e.value = mirrorPage;
              }
              htmlSource = StringUtil.replaceAll(htmlSource, `{${e.key}}`, e.value)
            });
          return htmlSource;
        })
      );
  }

  transformHTML(htmlFile: string | null) {
    // Bypass safe HTML code.
    return this.sanitizer.bypassSecurityTrustHtml(htmlFile || "");
  }

  get sendingSchedule() {
    return this.emailCampaignFormGroup.get('sendingSchedule');
  }

  getSender(senderName?: string, senderMail?: string): string {
    return senderName?.concat('(')?.concat(senderMail || '')?.concat(')') || '';
  }

  submitEmailCampaign(): void {
    this.store.dispatch(submitEmailCampaignSummaryStep({ summary: { sendingSchedule: convertDateTimeToTimeStamp(this.sendingSchedule?.value) } }));
    this.isLock = false;
  }

  previous() {
    this.store.dispatch(previousEmailCampaignEnvoiStep({}));
    this.isLock = false;
  }

  submitSentTest() {
    if (this.sendTestFormGroup.valid && !this.isDuplicate) {
      this.store.dispatch(sendMailTest({ recipientAddress: this.emails?.value }));
    } else {
      this.sendTestHasSubmit = true;
    }
  }

  get emails(): FormArray {
    return this.sendTestFormGroup.get('emails') as FormArray;
  }

  get isAddBtnInvisible(): boolean {
    return this.emails?.controls.length >= 5;
  }

  addEmail(value = ''): void {
    if (this.isAddBtnInvisible) return;

    this.emails.insert(this.emails.length, new FormControl(value, [Validators.required, Validators.pattern(EMAIL_PATTERN)]));
  }

  removeEmail(index: number): void {
    this.emails.removeAt(index);
  }

  getVariableTooltip(item: any, index: number): string {
    if (this.isDuplicateEmail(index)) {
      return this.variableErrorLabel?.duplicate;
    }
    if (item?.errors?.required) {
      return this.variableErrorLabel?.required;
    }
    return this.variableErrorLabel?.inValid;
  }

  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(
      this.emailFields,
      event.previousIndex,
      event.currentIndex
    );

    this.emails.setValue(this.emailFields);
  }

  dateTimeChange(event: any) {
    this.sendingSchedule?.setValue(event);
  }

  getLockedReason(): string[] {
    return [];
  }

  isLocked(): Observable<boolean> {
    return of(this.isLock);
  }
}
