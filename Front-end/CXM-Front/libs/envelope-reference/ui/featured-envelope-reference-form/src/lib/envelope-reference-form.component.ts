import {
  AfterContentChecked,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import {FormBuilder, FormControl, FormGroup,} from '@angular/forms';
import {takeUntil} from 'rxjs/operators';
import { Subject} from 'rxjs';
import {Store} from '@ngrx/store';
import {TranslateService} from "@ngx-translate/core";
import {
  EnvelopeReferenceFormErrorMessages,
  EnvelopeReferenceFormErrorMessagesModel,
  EnvelopeReferenceFormModel,
  EnvelopeReferenceFormProperties,
  EnvelopeReferenceFormUpdateMode,
  formats
} from "@cxm-smartflow/envelope-reference/util";
import {
  EnvelopeReferenceFormControlService
} from "@cxm-smartflow/envelope-reference/data-access";
import {
  createEnvelopeReference,
  updateEnvelopeReference,
  updateEnvelopeReferences
} from "@cxm-smartflow/envelope-reference/data-access";
import {CreateEnvelopeReference, UpdateEnvelopeReference} from "@cxm-smartflow/envelope-reference/data-access";
import {EnvelopeReferenceService} from "@cxm-smartflow/envelope-reference/data-access";

@Component({
  selector: 'cxm-smartflow-envelope-reference-form',
  templateUrl: './envelope-reference-form.component.html',
  styleUrls: ['./envelope-reference-form.component.scss'],
})
export class EnvelopeReferenceFormComponent
  implements OnInit, OnDestroy, AfterContentChecked, OnChanges {
  @Output() submitForm = new EventEmitter<any>();
  @Output() cancelForm = new EventEmitter<Event>();
  @Input()  mode: EnvelopeReferenceFormUpdateMode = EnvelopeReferenceFormUpdateMode.CREATE;
  EnvelopeReferenceFormUpdateMode = EnvelopeReferenceFormUpdateMode;
  @Input() envelopeReferenceDetail: UpdateEnvelopeReference | null;
  @Input() ids: string[] = [];

  isAdmin = true;
  formGroup: FormGroup;
  errorFormControl: EnvelopeReferenceFormModel[] = [];
  envelopeReferenceFormProperties = EnvelopeReferenceFormProperties;
  referenceErrorMessage: EnvelopeReferenceFormErrorMessagesModel;
  descriptionErrorMessage: EnvelopeReferenceFormErrorMessagesModel;
  formatErrorMessage: EnvelopeReferenceFormErrorMessagesModel;
  formats: any[] = formats;
  destroy$ = new Subject<boolean>();
  constructor(
    private readonly fb: FormBuilder,
    private readonly store: Store,
    private readonly ref: ChangeDetectorRef,
    private _translateService: TranslateService,
    public readonly envelopeReferenceFormControlService: EnvelopeReferenceFormControlService,
    private readonly envelopeReferenceService: EnvelopeReferenceService
  ) {
  }

  ngOnInit(): void {
    this.checkFormMode();
    this.formControl();
  }

  ngAfterContentChecked() {
    this.ref.detectChanges();
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.formGroup.reset();
    this.envelopeReferenceFormControlService.updateEnvelopeReferencePrivilegeInStorage();
  }

  private checkFormMode(): void {
    this.mode = this.envelopeReferenceFormControlService.envelopeReferenceFormUpdateMode() ;
  }
  formControl(): void {
    this.formGroup = this.fb.group({
      id: new FormControl(''),
      format: new FormControl(''),
      reference: new FormControl(''),
      description: new FormControl(''),
      active: new FormControl(true),
    });
    this.formGroup
      .get('reference')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.errorFormControl = this.errorFormControl.filter(
          (property) => property !== EnvelopeReferenceFormProperties.REFERENCE
        );
      });
    this.formGroup
      .get('description')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.errorFormControl = this.errorFormControl.filter(
          (property) => property !== EnvelopeReferenceFormProperties.DESCRIPTION
        );
      });
    this.formGroup
      .get('format')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.errorFormControl = this.errorFormControl.filter(
          (property) => property !== EnvelopeReferenceFormProperties.FORMAT
        );
      });

  }

  async beforeSubmit(): Promise<void> {
    this.validateEnvelopeReferenceForm(this.formGroup.value)
    if(this.errorFormControl.length > 0){
      return;
    }
    if(this.mode == EnvelopeReferenceFormUpdateMode.CREATE){
      this.store.dispatch(createEnvelopeReference({payload: this.formGroup.value as CreateEnvelopeReference}))
    }
    if(this.mode == EnvelopeReferenceFormUpdateMode.UPDATE_SINGLE){
      this.store.dispatch(updateEnvelopeReference({payload: this.formGroup.value as UpdateEnvelopeReference}))
    }
    if(this.mode == EnvelopeReferenceFormUpdateMode.UPDATE_MULTIPLE){
      this.store.dispatch(updateEnvelopeReferences({payload: this.formGroup.value as UpdateEnvelopeReference, ids: this.ids }))
    }
  }


  validateEnvelopeReferenceForm(formValue: any): void {
    const isSingleUpdateOrCreate =  formValue && (this.mode == EnvelopeReferenceFormUpdateMode.CREATE || this.mode == EnvelopeReferenceFormUpdateMode.UPDATE_SINGLE)

    if ( isSingleUpdateOrCreate) {
      for (const formValueKey in formValue) {
        switch (formValueKey) {
          case EnvelopeReferenceFormProperties.REFERENCE: {
            if ( this.mode == EnvelopeReferenceFormUpdateMode.CREATE ) {
              const isNotBlank = formValue[formValueKey]?.trim()?.length > 0;
              if(isNotBlank) {
                this.envelopeReferenceService.checkDuplicate(formValue[formValueKey])
                  .subscribe(isDuplicate=>{

                    const isValidLength =
                      formValue[formValueKey]?.trim()?.length <= 30;

                    if (!isValidLength) {
                      this.referenceErrorMessage =
                        EnvelopeReferenceFormErrorMessages.REFERENCE_INVALID_LENGTH;
                    }else if(isDuplicate){
                      this.referenceErrorMessage =
                        EnvelopeReferenceFormErrorMessages.REFERENCE_DUPLICATE;
                    }
                    isNotBlank && isValidLength && !isDuplicate
                      ? this.errorFormControl.filter(
                        (e) => e !== EnvelopeReferenceFormProperties.REFERENCE
                      )
                      : this.errorFormControl.push(EnvelopeReferenceFormProperties.REFERENCE) || [];
                  })
              } else {
                this.referenceErrorMessage =
                  EnvelopeReferenceFormErrorMessages.REFERENCE_REQUIRED;
                this.errorFormControl.push(EnvelopeReferenceFormProperties.REFERENCE);
              }

            }
            break;
          }
          case EnvelopeReferenceFormProperties.DESCRIPTION: {
            const isValidLength =
              formValue[formValueKey]?.trim()?.length <= 100;
            if (!isValidLength) {
              this.descriptionErrorMessage =
                EnvelopeReferenceFormErrorMessages.DESCRIPTION_INVALID_LENGTH;
            }
             isValidLength
              ? this.errorFormControl.filter(
                (e) => e !== EnvelopeReferenceFormProperties.DESCRIPTION
              )
              : this.errorFormControl.push(EnvelopeReferenceFormProperties.DESCRIPTION) || [];
            break;
          }
          case EnvelopeReferenceFormProperties.FORMAT: {
            this.checkFormatValidation(formValue, formValueKey);
            break;
          }
        }
      }
    }

  }

  private checkFormatValidation(formValue: any, formValueKey: any){
    const isNotBlank = formValue[formValueKey]?.trim()?.length > 0;
    if (!isNotBlank) {
      this.formatErrorMessage =
        EnvelopeReferenceFormErrorMessages.FORMAT_REQUIRED;
    }
    isNotBlank
      ? this.errorFormControl.filter(
        (e) => e !== EnvelopeReferenceFormProperties.FORMAT
      )
      : this.errorFormControl.push(EnvelopeReferenceFormProperties.FORMAT) || [];
  }
  async ngOnChanges(changes: SimpleChanges): Promise<void> {
    if (changes?.envelopeReferenceDetail) {
      if (
        (await this.envelopeReferenceFormControlService.envelopeReferenceFormUpdateMode()) ===
        EnvelopeReferenceFormUpdateMode.UPDATE_SINGLE
      ) {
        if (this.envelopeReferenceDetail) {
          this.formGroup.setValue({
            id: this.envelopeReferenceDetail.id,
            format: this.envelopeReferenceDetail.format,
            active: this.envelopeReferenceDetail.active,
            description: this.envelopeReferenceDetail.description,
            reference: this.envelopeReferenceDetail.reference,
          });
        }
      }

      if (
        (await this.envelopeReferenceFormControlService.envelopeReferenceFormUpdateMode()) ===
        EnvelopeReferenceFormUpdateMode.UPDATE_MULTIPLE
      ) {
        if (this.envelopeReferenceDetail) {
          this.formGroup.setValue({
            format: this.envelopeReferenceDetail.format,
            active: this.envelopeReferenceDetail.active,
          });
        }
      }
    }
  }

  returnFormatChanged(event: string): void {
   this.formGroup.setValue({...this.formGroup.value,format: event})
  }

}
