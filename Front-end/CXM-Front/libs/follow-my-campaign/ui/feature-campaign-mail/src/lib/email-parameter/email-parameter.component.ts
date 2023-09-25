import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { CustomFileModel, TemplateModel } from '@cxm-smartflow/shared/data-access/model';
import {
  clearAttachmentInStore, getEmailMetadata,
  initParameterFormTemporary, MetadataResponseModel,
  previousEmailCampaignParameterStep,
  removeAttachment,
  removeAttachmentOnTemporary,
  selectAttachmentsUploaded,
  selectCampaignDetail, selectEmailMetadata,
  selectTemplateDetails,
  StepOnActivated,
  StepOnCampaign,
  submitEmailCampaignParameterStep,
  unloadParameterForm,
  uploadAttachments
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { BehaviorSubject, Observable, of, Subject, Subscription } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';
import { ILockableForm } from '@cxm-smartflow/flow-deposit/guard/pending-change';
import { FileValidator } from '@cxm-smartflow/shared/common-typo';
import { EmailParameterValidator } from './email-parameter-validator';
import { InputSelectionCriteria } from '@cxm-smartflow/shared/ui/form-input-selection';
import {FileExtensionUtil} from "@cxm-smartflow/shared/utils";

@Component({
  selector: 'cxm-smartflow-email-parameter',
  templateUrl: './email-parameter.component.html',
  styleUrls: ['./email-parameter.component.scss']
})
export class EmailParameterComponent implements OnInit, OnDestroy, ILockableForm {

  campaignSubscription$: Subscription;
  fileUploadedSubscription$: Subscription;
  metadata$: Observable<MetadataResponseModel>;
  senderEmail$: Observable<InputSelectionCriteria[]>;
  selectedSenderMail$: Observable<string>;
  senderName$: Observable<InputSelectionCriteria[]>;
  selectedSenderName$: Observable<string>;
  unsubscribeLink$: Observable<InputSelectionCriteria[]>;
  selectedUnsubscribeLink$: Observable<string>;
  templateDetail$: Subscription;
  destroy$ = new Subject<boolean>();

  formGroup: FormGroup;
  hasSubmit = false;

  variables: string [] = [];

  // tooltip properties.
  showTooltipBackground = false;
  errorLabel: any;

  isLock: boolean;

  // Attachment properties.
  fileValidator: FileValidator = { fileSize: 5, fileLimit: 4, checkDuplicate: true };
  internalAttachment$ = new BehaviorSubject<CustomFileModel[]>([]);
  acceptableType = FileExtensionUtil.getExtensions(
    FileExtensionUtil.PDF_EXTENSIONS
      .concat(FileExtensionUtil.WORD_EXTENSIONS)
      .concat(FileExtensionUtil.EXCEl_EXTENSIONS)
      .concat(FileExtensionUtil.IMAGE_EXTENSIONS)
      .concat(FileExtensionUtil.ZIP_EXTENSIONS));

  constructor(private formBuilder: FormBuilder,
              private store: Store,
              private translate: TranslateService) {

    this.formGroup = this.formBuilder.group({
      id: new FormControl(0),
      campaignName: new FormControl('', [Validators.required, Validators.maxLength(128)]),
      subjectMail: new FormControl('', [Validators.required, Validators.maxLength(128)]),
      senderMail: new FormControl('', [EmailParameterValidator.fieldSenderEmail()]),
      senderName: new FormControl('', [EmailParameterValidator.fieldSenderName()]),
      unsubscribeLink: new FormControl('', [EmailParameterValidator.fieldUnsubscribeLink()]),
      variableTmp: new FormControl(''),
      attachments: new FormControl([] as CustomFileModel[])
    });
  }

  ngOnInit(): void {
    this.translate.get('cxmCampaign.followMyCampaign.settingParameter').subscribe(value => {
      this.errorLabel = value;
      // Label for attachment.
      const validator = value?.attachment?.validation;
      this.fileValidator = {
        ...this.fileValidator,
        fileLimitMessage: validator?.fileLimit,
        fileSizeMessage: validator?.fileSize,
        fileExtensionMessage: validator?.fileExtension,
        duplicateMessage: validator?.fileDuplicate
      };
    });

    this.store.dispatch(StepOnActivated({ active: true, step: 3 }));
    this.store.dispatch(StepOnCampaign({ step: 3 }));
    this.isLock = true;

    // Patch value to from group properties.
    this.campaignSubscription$ = this.store.select(selectCampaignDetail).subscribe((campaign) => {
      if (campaign) {
        this.formGroup.patchValue({
          campaignName: campaign?.campaignName || '',
          subjectMail: campaign?.subjectMail || '',
          senderName: campaign?.senderName,
          senderMail: campaign?.details?.senderMail,
          unsubscribeLink: campaign?.details?.unsubscribeLink,
          attachments: campaign?.attachments || []
        });

        // Initialize internal attachments.
        this.internalAttachment$.next(campaign?.attachments);
      }
    });

    // Subscript file properties from store.
    this.fileUploadedSubscription$ = this.store.select(selectAttachmentsUploaded).subscribe((files: CustomFileModel[]) => {
      this.internalAttachment$.next(files);
    });

    this.templateDetail$ = this.store.select(selectTemplateDetails).pipe(takeUntil(this.destroy$))
      .subscribe((templateDetail: TemplateModel) => {
        this.variables = templateDetail?.variables || [];
      });

    // Pass variable to subject mail property.
    this.variableTmp?.valueChanges?.subscribe((item: string) => {
      if (item.length > 0) {
        const subjectMail: string = this.subjectMail?.value || '';
        this.subjectMail?.setValue(subjectMail + '{' + (item || ' ') + '}');

        // set to default value.
        this.variableTmp?.setValue('');
      }
    });

    // Init attachment of parameter form temporary.
    this.internalAttachment$.subscribe(files => {
      this.store.dispatch(initParameterFormTemporary({ parameter: this.formGroup?.value, attachments: files }));
    });

    // Event form change.
    this.formGroup?.valueChanges?.subscribe(value => {
      this.store.dispatch(initParameterFormTemporary({
        parameter: value,
        attachments: this.internalAttachment$.value
      }));
    });

    this.store.dispatch(getEmailMetadata());

    this.senderEmail$ = this.store.select(selectEmailMetadata).pipe(
      filter(data => Boolean(data.senderMail)),
      map(data => data.senderMail.map(data => ({ key: data.value, value: data.value })))
    );

    this.senderName$ = this.store.select(selectEmailMetadata).pipe(
      filter(data => Boolean(data.senderName)),
      map(data => data.senderName.map(data => ({ key: data.value, value: data.value })))
    );

    this.unsubscribeLink$ = this.store.select(selectEmailMetadata).pipe(
      filter(data => Boolean(data.unsubscribeLink)),
      map(data => data.unsubscribeLink.map(data => ({ key: `mailto:${data.value}`, value: data.value }))),
    );

    this.selectedSenderMail$ = this.store.select(selectCampaignDetail).pipe(
      filter(data => Boolean(data)),
      map(data => data.details.senderMail),
    );

    this.selectedSenderName$ = this.store.select(selectCampaignDetail).pipe(
      filter(data => Boolean(data)),
      map(data => data.senderName),
    );

    this.selectedUnsubscribeLink$ = this.store.select(selectCampaignDetail).pipe(
      filter(data => Boolean(data)),
      map(data => data.details.unsubscribeLink),
    );

  }

  get formGroupValue() {
    return {
      ...this.formGroup.value,
      attachments: this.internalAttachment$.value || []
    };
  }

  submit() {
    this.isLock = false;
    if (this.formGroup?.valid) {
      this.store.dispatch(submitEmailCampaignParameterStep({ parameter: this.formGroupValue }));
    } else {
      this.hasSubmit = true;
    }
  }

  previousStep() {
    this.store.dispatch(previousEmailCampaignParameterStep({}));
    this.isLock = false;
  }

  get campaignName() {
    return this.formGroup.get('campaignName');
  }

  get subjectMail() {
    return this.formGroup.get('subjectMail');
  }

  get senderMail() {
    return this.formGroup.get('senderMail');
  }

  get senderName() {
    return this.formGroup.get('senderName');
  }

  get unsubscribeLink() {
    return this.formGroup.get('unsubscribeLink');
  }

  get variableTmp() {
    return this.formGroup.get('variableTmp');
  }

  get attachments() {
    return this.formGroup.get('attachments');
  }

  get campaignNameTooltip() {
    if (this.campaignName?.errors?.required) {
      return this.errorLabel?.validation?.required;
    }
    return this.errorLabel?.validation?.maxLength;
  }

  get subjectMailTooltip() {
    if (this.subjectMail?.errors?.required) {
      return this.errorLabel?.validation?.required;
    }
    return this.errorLabel?.validation?.maxLength;
  }

  get senderMailTooltip() {
    if (this.senderMail?.errors?.required) {
      return this.errorLabel?.validation?.required;
    } else if (this.senderMail?.errors?.maxLength) {
      return this.errorLabel?.validation?.maxLength;
    } else {
      return this.errorLabel?.senderEmail?.invalid;
    }
  }

  get senderNameTooltip() {
    if (this.senderName?.errors?.required) {
      return this.errorLabel?.validation?.required;
    }
    return this.errorLabel?.validation?.maxLength;
  }

  get unsubscribeLinkTooltip() {
    if (this.unsubscribeLink?.errors?.required) {
      return this.errorLabel?.validation?.required;
    }
    return this.errorLabel?.unsubscribeLink?.invalid;
  }

  ngOnDestroy(): void {
    this.store.dispatch(StepOnActivated({ active: false }));
    this.store.dispatch(clearAttachmentInStore());
    this.store.dispatch(unloadParameterForm());

    this.destroy$.unsubscribe();
    this.store.complete();
    this.campaignSubscription$.unsubscribe();
    this.fileUploadedSubscription$.unsubscribe();
    this.templateDetail$.unsubscribe();
    this.internalAttachment$.unsubscribe();
  }

  getLockedReason(): string[] {
    return [];
  }

  isLocked(): Observable<boolean> {
    return of(this.isLock);
  }

  choosingMultipleFiles(event: any) {
    this.getFormData((event as File[])).subscribe((formData: FormData) => {
      this.store.dispatch(uploadAttachments({ formData: formData }));
    });
  }

  removeFile(event: any) {
    const customFile = event as CustomFileModel;
    // Validate with campaign attachments.
    // True, if it not in campaign attachments.
    if ((this.attachments?.value as CustomFileModel[]).indexOf(customFile) === -1) {
      // delete on temporary.
      this.store.dispatch(removeAttachment({ fileIds: [customFile.fileId || ''] }));
    } else {
      // hard delete.
      this.store.dispatch(removeAttachmentOnTemporary({ fileIds: [customFile.fileId || ''] }));
    }
  }

  private getFormData(files: File[]): Observable<FormData> {
    const formData = new FormData();
    Array.from(files).forEach(file => formData.append('files', file));
    return of(formData);
  }

  selectUnsubscribe(value: string): void {
    this.formGroup.controls['unsubscribeLink'].setValue(value);
  }

  selectSenderMail(value: string): void {
    this.formGroup.controls['senderMail'].setValue(value);
  }

  selectSenderName(value: string) {
    this.formGroup.controls['senderName'].setValue(value);
  }
}

