import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import {
  EMAILING,
  initFormChange,
  selectEmailTemplate,
  selectHtmlFile,
  selectModelNameStatus,
  selectSmsForm,
  selectTemplateVariables,
  SMS,
  submitFormVar,
  TemplateService,
  TemplateType
} from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Observable, of, Subject, Subscription } from 'rxjs';
import { distinctUntilChanged, filter, pluck, take, takeUntil } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute } from '@angular/router';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { GrapeJsUtil, keepVariableTemp, removeVariableTemp } from '@cxm-smartflow/template/util';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';

@Component({
  selector: 'cxm-smartflow-template-variable-form',
  templateUrl: './template-variable-form.component.html',
  styleUrls: ['./template-variable-form.component.scss'],
})
export class TemplateVariableFormComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  templateId$ = new BehaviorSubject(0);
  destroy$ = new Subject<boolean>();
  subscription$: Subscription[] = [];

  modelNameValid$ = new BehaviorSubject(false);
  variableFields: string[] = [];
  isSubmit = false;
  hasDefaultVariable$ = new BehaviorSubject(true);
  modelType$ = new BehaviorSubject('');
  formType$ = new BehaviorSubject('');
  disabledForm$ = new BehaviorSubject(false);
  defaultVariable$ = new BehaviorSubject('');
  oldVariables: string[] = [];
  indexDuplicated: number[] = [];
  variableHasDuplicated$ = new BehaviorSubject(false);
  usedVariables$ = new BehaviorSubject<string[]>([]);
  isSMSFormValid = true;

  // variable tooltip properties.
  showVarTooltip$ = new BehaviorSubject(false);
  showVarBackgroundTooltip = false;

  showBtnTooltipWhenValueChange$ = new BehaviorSubject(true);
  showBtnBackgroundTooltip = false;

  templateTranslatedMessage: { [key: string]: string };

  constructor(
    private store: Store,
    private fb: FormBuilder,
    private templateService: TemplateService,
    private readonly translate: TranslateService,
    private activatedRoute: ActivatedRoute,
    private snackBarService: SnackBarService
  ) {
    this.formGroup = this.fb.group({
      modelName: new FormControl('', [
        Validators.required,
        Validators.maxLength(128),
      ]),
      variables: this.fb.array([]),
    });

    this.activatedRoute.queryParams.pipe(take(1)).subscribe((query) => {
      this.templateId$.next(<number>query?.sourceTemplateId);
      this.modelType$.next(<string>query?.modelType);
      this.formType$.next(<string>query?.mode);
    });

    // Check condition to disable form.
    this.formType$.subscribe((v) => this.disabledForm$.next(v === 'edit'));
  }

  ngOnInit(): void {
    this.translate
      .get('template.message')
      .subscribe(
        (templateMessage) => (this.templateTranslatedMessage = templateMessage)
      );

    this.activatedRoute.queryParams.pipe(take(1)).subscribe((v) => {
      if (v?.mode !== 'edit') {
        const VARIABLE_PATTERN = /([{])+([^}]+)+([}])/g;

        if (v?.modelType === TemplateType.EMAILING) {
          // Subscribe all variables used in EMAILING editor
          this.subscription$.push(
            this.store.select(selectEmailTemplate).subscribe((html: string) => {
              this.usedVariables$.next([
                ...new Set(
                  html
                    ?.match(VARIABLE_PATTERN)
                    ?.map((e: string) => e?.slice(1, -1))
                ),
              ]);
            })
          );
        }

        if (v?.modelType === TemplateType.SMS) {
          // Subscribe all variables used in SMS editor
          this.subscription$.push(
            this.store.select(selectHtmlFile).subscribe((html: string) => {
              this.usedVariables$.next([
                ...new Set(
                  html
                    ?.match(VARIABLE_PATTERN)
                    ?.map((e: string) => e?.slice(1, -1))
                ),
              ]);
            })
          );
        }
      }
    });

    // Set default variable.
    this.modelType$?.subscribe((value) =>
      this.defaultVariable$.next(
        value.toUpperCase() === TemplateType.SMS ? SMS.value : EMAILING.value
      )
    );

    // variable formArray event.
    this.variables.valueChanges
      .pipe(distinctUntilChanged())
      .pipe(takeUntil(this.destroy$))
      .subscribe((variables) => {
        this.variableFields = [];
        this.variableFields = variables;

        // Close error of at lease one variable.
        this.hasDefaultVariable$.next(true);

        // Store variableTemp in storage.
        removeVariableTemp().subscribe(() => keepVariableTemp(variables));

        // validate variable duplicate.
        this.validateVariableDuplicate(variables);

        // validate variable form change.
        this.validateVariableChange(variables);
      });

    this.subscription$.push(
      this.store.select(selectTemplateVariables).subscribe((variableState) => {
        // Initialized variable to form with not duplicated.
        const variables = variableState as string[];

        // keep total variable.
        this.oldVariables = variables;

        // add variable.
        variables.filter((item) => {
          if (
            !this.variables?.value?.includes(item) &&
            item?.trim().length > 0
          ) {
            this.addVariable(item);
          }
        });

        // disabled control.
        if (this.disabledForm$.value) {
          this.disableVariableFormControl();
        }
      })
    );

    // validate form.
    if (this.disabledForm$.value) {
      // disable formGroup.
      this.formGroup.disable();
    }

    this.store
      .select(selectModelNameStatus)
      .pipe(takeUntil(this.destroy$))
      .subscribe((args) => {
        const {
          isModelNameDuplicate,
          isModelNameRequired,
          isModelNameMaxLength,
        } = args as any;
        const isValid =
          !isModelNameDuplicate &&
          !isModelNameRequired &&
          !isModelNameMaxLength;
        this.modelNameValid$.next(isValid);
      });

    this.showDefaultVariableErrorMessage();
    this.activatedRoute.queryParams
      .pipe(
        take(1),
        pluck('modelType'),
        filter((e) => e !== undefined)
      )
      .subscribe((type) => {
        if (type !== 'SMS') return;
        this.store
          .select(selectSmsForm)
          .pipe(takeUntil(this.destroy$))
          .subscribe((data) => {
            // @smsSendingLimitSize refers to the amount of characters that SMS campaign can send.
            const smsSendingLimitSize = 612;
            this.isSMSFormValid = smsSendingLimitSize >= data?.trim()?.length;
          });
      });
  }

  private checkDuplicateSmsTemplateVariable(variables: string [], finalVariables: string []){
    let emailDupTotal = 0;
    finalVariables?.filter((item: string, index: number) => {
      // check with default key of sms variable.
      if (
        item?.trim()?.length > 0 &&
        finalVariables?.filter((value) => value === SMS.key)?.length >
        1 &&
        item === SMS.key
      ) {
        emailDupTotal += 1;
        if (emailDupTotal > 1) {
          this.indexDuplicated.push(index);
        }
      }
      // check all variable of sms.
      else {
        variables?.filter((item, index) => {
          if (
            variables.indexOf(item) !== index &&
            item?.trim()?.length > 0 &&
            !this.indexDuplicated.includes(index)
          ) {
            this.indexDuplicated.push(index);
          }
        });
      }
    });
  }

  private checkDuplicateEmailTemplateVariable(variables: string [], finalVariables: string []){
    let emailDupTotal = 0;
    finalVariables?.filter((item: string, index: number) => {
      // check with default key of email template.
      if (
        item?.trim()?.length > 0 &&
        finalVariables?.filter((value) => value === EMAILING.key)?.length >
        1 &&
        item === EMAILING.key
      ) {
        emailDupTotal += 1;
        if (emailDupTotal > 1) {
          this.indexDuplicated.push(index);
        }
      }
      // check all variable of email.
      else {
        variables?.filter((item, index) => {
          if (
            variables.indexOf(item) !== index &&
            item?.trim()?.length > 0 &&
            !this.indexDuplicated.includes(index)
          ) {
            this.indexDuplicated.push(index);
          }
        });
      }
    });
  }

  validateVariableDuplicate(variables: string[]) {
    this.indexDuplicated = [];
    this.transformVariable(variables)?.subscribe((finalVariables) => {
      // Validate variable duplicate for SMS template form.
      if (this.modelType$.value.toUpperCase() === TemplateType.SMS) {
        this.checkDuplicateSmsTemplateVariable(variables, finalVariables);
      }
      // Validate variable duplicate for EMAIL template form.
      else {
        this.checkDuplicateEmailTemplateVariable(variables, finalVariables);
      }

      this.variableHasDuplicated$.next(this.indexDuplicated?.length > 0);
    });
  }

  validateVariableChange(variables: string[]) {
    if (JSON.stringify(this.oldVariables) !== JSON.stringify(variables)) {
      this.store.dispatch(initFormChange({ hasChange: true }));
    } else {
      this.store.dispatch(initFormChange({ hasChange: false }));
    }
  }

  disableVariableFormControl() {
    this.variables?.controls?.forEach((control) => {
      control.disable();
    });
  }

  get variables(): FormArray {
    return this.formGroup.get('variables') as FormArray;
  }

  drop(event: CdkDragDrop<string[]>) {
    if (!this.disabledForm$.value) {
      moveItemInArray(
        this.variableFields,
        event.previousIndex,
        event.currentIndex
      );

      // Set value to variable FormArray after drop.
      this.variables.setValue(this.variableFields);
    }
  }

  addVariable(value: string) {
    this.isSubmit = false;
    this.variables.push(
      new FormControl(value, [Validators.required, Validators.maxLength(128)])
    );
  }

  removeVariable(
    controls: AbstractControl[],
    variable: string | null,
    index: number
  ) {
    if (
      this.usedVariables$.value.includes(variable || '') &&
      !this.indexDuplicated.includes(index)
    ) {
      this.snackBarService.openCustomSnackbar({
        message: this.templateTranslatedMessage?.variableAlreadyUsed ?? '',
        icon: 'close',
        type: 'error',
      });
      return;
    }
    if (!this.checkIsModifiable(controls, variable, index)) return;
    this.variables.removeAt(index);
  }

  submit() {
    if (!this.isSMSFormValid) return;

    this.isSubmit = true;
    if (
      !this.variables.invalid &&
      this.hasDefaultVariable &&
      !this.variableHasDuplicated$.value &&
      this.modelNameValid$.value
    ) {
      this.store.dispatch(submitFormVar({ variables: this.variables.value }));
    }
  }

  transformVariable(variables: string[]): Observable<string[]> {
    if (this.modelType$.value === TemplateType.SMS) {
      return of(
        variables?.map((variable) => {
          if (variable === SMS.value || variable === SMS.key) {
            return SMS.key;
          } else return variable;
        })
      );
    } else {
      return of(
        variables?.map((variable) => {
          if (variable === EMAILING.value || variable === EMAILING.key) {
            return EMAILING.key;
          } else return variable;
        })
      );
    }
  }

  get hasDefaultVariable(): boolean {
    const isHas =
      this.variableFields.filter((item) => item === this.defaultVariable$.value)
        ?.length > 0;
    this.hasDefaultVariable$.next(isHas);
    return isHas;
  }

  getTooltipVariableMessage(control?: any, duplicate?: boolean) {
    const isFormValid = !duplicate && !control?.errors?.maxlength;
    this.showVarTooltip$.next(!isFormValid);

    let message = '';
    if (control?.errors?.maxlength) {
      this.translate
        .get('template.message.maxLength')
        ?.subscribe((v) => (message = v));
    } else if (duplicate) {
      this.translate
        .get('template.message.variableDuplicate')
        ?.subscribe((v) => (message = v));
    } else if (control?.errors?.required && this.isSubmit) {
      this.translate
        .get('template.message.variableEmpty')
        ?.subscribe((v) => (message = v));
      this.showVarTooltip$.next(true);
    }
    return message;
  }

  showDefaultVariableErrorMessage() {
    let message = '';
    if (this.modelType$.value === TemplateType.SMS) {
      this.translate
        .get('template.message.smsRequiredVariable')
        ?.subscribe((v) => (message = (v as string)?.replace('{}', SMS.value)));
    } else {
      this.translate
        .get('template.message.emailingRequiredVariable')
        ?.subscribe(
          (v) => (message = (v as string)?.replace('{}', EMAILING.value))
        );
    }

    this.hasDefaultVariable$?.subscribe((v) => {
      if (!v) {
        this.snackBarService.openCustomSnackbar({
          message: message,
          icon: 'close',
          type: 'error',
        });
      }
    });
  }

  ngOnDestroy(): void {
    this.templateId$.unsubscribe();
    this.hasDefaultVariable$.unsubscribe();
    this.modelType$.unsubscribe();
    this.formType$.unsubscribe();
    this.disabledForm$.unsubscribe();
    this.defaultVariable$.unsubscribe();
    this.variableHasDuplicated$.unsubscribe();
    this.showVarTooltip$.unsubscribe();
    this.showBtnTooltipWhenValueChange$.unsubscribe();

    GrapeJsUtil.removeGrapeJsProperties();
    removeVariableTemp();

    this.subscription$.filter((item) => item?.unsubscribe());
    this.store?.complete();
  }

  checkIsModifiable(
    controls: AbstractControl[],
    variable: string | null,
    index: number
  ): boolean {
    if (!variable) return true;

    if (this.defaultVariable$.value === variable) {
      return this.indexDuplicated.includes(index);
    }

    const duplicatedVariables = controls.filter((e) => e.value === variable);

    return duplicatedVariables.length >= 1;
  }

  get btnSubmitStyle() {
    return this.isSMSFormValid &&
      this.modelNameValid$.value &&
      !this.variableHasDuplicated$.value
      ? 'btn-active'
      : 'btn-disabled';
  }
}
