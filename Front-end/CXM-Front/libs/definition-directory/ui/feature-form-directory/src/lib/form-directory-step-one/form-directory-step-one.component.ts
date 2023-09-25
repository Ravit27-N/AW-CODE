import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { DefinitionDirectoryService, FormMode } from '@cxm-smartflow/definition-directory/data-access';
import { FormDirectoryStepOneValidator } from './form-directory-step-one.validator';
import { Subscription } from 'rxjs';
import { FormDirectoryStepOneType } from './form-directory-step-one.type';

@Component({
  selector: 'cxm-smartflow-form-directory-step-one',
  templateUrl: './form-directory-step-one.component.html',
  styleUrls: ['./form-directory-step-one.component.scss'],
})
export class FormDirectoryStepOneComponent implements OnInit, OnChanges, OnDestroy {
  stepOneFormGroup: FormGroup;
  @Input() stepOneMode: FormMode = 'create';
  @Input() stepOneFormValue: FormDirectoryStepOneType | undefined;
  @Output() stepOneNextPageEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() stepOnePreviousPageEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() stepOneValueChangeEvent: EventEmitter<FormDirectoryStepOneType> = new EventEmitter<FormDirectoryStepOneType>();
  #subscription: Subscription = new Subscription();
  showError = false;

  @Input() oldName = "";
  constructor(
    private formBuilder: FormBuilder,
    private directoryService: DefinitionDirectoryService
  ) {}

  ngOnInit(): void {
    this.setupStepOneFormGroup();
    this.subscribeStepOneFormChange();
    this.stepOneFormGroup.patchValue({ ...this.stepOneFormValue }, { emitEvent: false, onlySelf: false });
  }

  ngOnChanges(changes: SimpleChanges) {
    // Setup form.
    if (changes?.stepOneFormValue?.currentValue && (this.stepOneMode === 'modify' || this.stepOneMode === 'view')) {

      this.stepOneFormGroup?.patchValue(
        {...changes.stepOneFormValue.currentValue},
        {emitEvent: false, onlySelf: false}
      );
    }
  }

  ngOnDestroy() {
    this.#subscription.unsubscribe();
  }


  navigateToPreviousPageEvent(): void {
    this.stepOnePreviousPageEvent.next(true);
  }

  async navigateToNextTwoEvent(): Promise<void> {
    if (this.stepOneMode === 'view') {
      this.stepOneNextPageEvent.next(true);
    } else {
      if (await this.validateOneStepForm()) {
        this.showError = true;
        return;
      }
      this.stepOneNextPageEvent.next(true);
    }
  }

  private setupStepOneFormGroup(): void {
    this.stepOneFormGroup = this.formBuilder.group({
      name: new FormControl('', [FormDirectoryStepOneValidator.fieldName()]),
      displayName: new FormControl('', [FormDirectoryStepOneValidator.fieldDisplayName()]),
    });
    if(this.stepOneMode === 'view'){
      this.stepOneFormGroup?.disable();
    }
  }

  private subscribeStepOneFormChange(): void {
    const subscription: Subscription = this.stepOneFormGroup.valueChanges.subscribe(
      (rawValue) => {
        this.stepOneValueChangeEvent.emit(rawValue);
      }
    );

    this.#subscription.add(subscription);
  }

  /**
   * Return true if form invalid.
   */
  private async validateOneStepForm(): Promise<boolean> {
    if (this.stepOneFormGroup.invalid) {
      return Promise.resolve(true);
    }

    const duplicated: boolean = await this.checkUniqueDirectoryName();

    if (duplicated) {
      this.stepOneFormGroup.controls['name'].setErrors({
        incorrect: true,
        message: 'directory.definition_directory_create_step_1_field_display_name_duplicated',
      });
    }

    return Promise.resolve(duplicated);
  }

  private async checkUniqueDirectoryName(): Promise<boolean> {
    const formRawValue = this.stepOneFormGroup.getRawValue();

    if (
      this.stepOneFormValue?.name &&
      formRawValue.name?.trim() === this.oldName &&
      this.stepOneMode === 'modify'
    ) {
      return Promise.resolve(false);
    }

    return this.directoryService
      .checkDefinitionDirectoryNameUnique(formRawValue.name?.trim())
      .toPromise();
  }
}
