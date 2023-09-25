import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
// eslint-disable-next-line @nrwl/nx/enforce-module-boundaries
import { modelNameChangeEvent, TemplateService } from '@cxm-smartflow/template/data-access';
import { BehaviorSubject, Subject } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';

@Component({
  selector: 'cxm-smartflow-feature-create-update-template-popup',
  templateUrl: './feature-create-update-template-popup.component.html',
  styleUrls: ['./feature-create-update-template-popup.component.scss']
})
export class FeatureCreateUpdateTemplatePopupComponent
  implements OnInit, OnDestroy {
  destroy$ = new Subject<boolean>();
  formGroup: FormGroup;
  isModelNameDuplicated$ = new BehaviorSubject(false);
  showTooltip$ = new BehaviorSubject(false);

  constructor(
    public readonly dialogRef: MatDialogRef<FeatureCreateUpdateTemplatePopupComponent>,
    private readonly templateService: TemplateService,
    private readonly translate: TranslateService,
    private readonly _router: Router,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private formBuild: FormBuilder,
    private store: Store
  ) {
    this.formGroup = this.formBuild.group({
      modelName: new FormControl('', [
        Validators.required,
        Validators.maxLength(128)
      ]),
      modelType: new FormControl(''), // TODO: dynamic type by param.
      sourceTemplateId: new FormControl(0) // TODO: dynamic source by templateId.
    });

    this.formGroup?.get('modelType')?.setValue(data?.modelType);
  }

  ngOnInit(): void {
    this._router.events.pipe(take(1)).subscribe(() => {
      this.dialogRef.close();
    });

    this.modelName?.valueChanges
      ?.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        value === '' ? this.isModelNameDuplicated$.next(false) : this.templateService
          .validationModelName(value, this.data?.modelType)
          .subscribe((v) => this.isModelNameDuplicated$.next(v));
      });

    this.isModelNameDuplicated$
      .pipe(takeUntil(this.destroy$))
      .subscribe((duplicate) => {
        // Produce value to store.
        this.store.dispatch(
          modelNameChangeEvent({
            value: '',
            formHasChanged: false,
            isDuplicate: duplicate,
            isMaxLength: this.maxLength,
            isRequired: this.required
          })
        );

        // validation to show tooltip.
        const isFormValid = !this.maxLength && !duplicate;
        this.showTooltip$.next(!isFormValid);
      });
  }

  get maxLength() {
    return this.modelName?.errors?.maxlength;
  }

  get required() {
    return this.modelName?.errors?.required;
  }

  get duplicated() {
    return this.isModelNameDuplicated$.value;
  }

  get tooltipMessage() {
    let value = '';
    if (this.required) {
      this.translate
        ?.get('template.popup.errors.modelNameRequired')
        ?.subscribe((v) => (value = v));
    } else if (this.duplicated) {
      this.translate
        ?.get('template.popup.errors.duplicatedModelName')
        ?.subscribe((v) => (value = v));
    } else if (this.maxLength) {
      this.translate
        ?.get('template.popup.errors.maxLength')
        ?.subscribe((v) => (value = v));
    }
    return value;
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
    this.isModelNameDuplicated$.complete();
    this.showTooltip$.complete();
  }

  get modelName() {
    return this.formGroup?.get('modelName');
  }

  submit() {
    if (this.formGroup?.valid && !this.isModelNameDuplicated$.value) {
      this.dialogRef.close(this.formGroup.value);
    } else {
      // Show tooltip.
      this.showTooltip$.next(true);
    }
  }
}
