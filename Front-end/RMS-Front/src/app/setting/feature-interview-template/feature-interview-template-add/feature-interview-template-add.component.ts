import { Component, Inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { KeyValue } from '@angular/common';
import {
  EmployeeModel,
  InterviewTemplateModel,
  InterviewTemplateService,
} from '../../../core';
import { AwSnackbarService } from '../../../shared';

@Component({
  selector: 'app-feature-interview-template-add',
  templateUrl: './feature-interview-template-add.component.html',
  styleUrls: ['./feature-interview-template-add.component.scss'],
})
export class FeatureInterviewTemplateAddComponent implements OnInit, OnDestroy {
  @Input() interviewTemplateTitle?: string = 'Add Interview Template';
  @Input() selectedId: number;
  @Input() selectedName: string;
  @Input() selectedType: string;
  @Input() selectedEmployee: Array<number> = [];
  @Input() selectedStatus?: boolean = true;
  @Input() providedRemark: string;
  @Input() optionEmployees: Array<KeyValue<number, string>> = [];
  @Input() optionTypes = ['First Interview', 'Second Interview'];
  form: FormGroup;
  slideValue = 'Active';
  validateName = false;
  #subscription = new Subscription();

  constructor(
    private formBuilder: FormBuilder,
    private interviewTemplateService: InterviewTemplateService,
    private awSnackbarService: AwSnackbarService,
    public dialogRef: MatDialogRef<FeatureInterviewTemplateAddComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: InterviewTemplateModel,
  ) {
    this.form = this.formBuilder.group({});
  }

  ngOnInit(): void {
    this.fetchEmployees();
    this.initInterviewForm();
    const subscription = this.form.valueChanges.subscribe(() => {});
    this.#subscription.add(subscription);
  }

  initInterviewForm(): void {
    if (this.form) {
      this.form = this.formBuilder.group({});
      this.form?.addControl('id', new FormControl(0));
      this.form?.addControl('name', new FormControl(''));
      this.form?.addControl(
        'active',
        new FormControl(this.data?.isUpdated ? this.selectedStatus : true),
      );
      this.form?.addControl('type', new FormControl('First Interview'));
      this.form?.addControl('remark', new FormControl(''));
      this.form?.addControl('employees', new FormControl([]));
    }
  }

  updateInterviewTemplate(): void {
    this.selectedId = this.data?.id;
    this.selectedEmployee = this.data?.employees;
    this.selectedStatus = this.data?.active;
    this.selectedName = this.data?.name;
    this.providedRemark = this.data?.remark;
    this.selectedType = this.data?.type;
  }

  fetchEmployees(): void {
    this.interviewTemplateService.getEmployees().subscribe((result) => {
      this.optionEmployees = result.contents.map((employee: EmployeeModel) => ({
        key: employee.id,
        value: employee.fullName,
      }));
      if (this.data?.isUpdated) {
        this.interviewTemplateTitle = 'Update Interview Template';
        this.updateInterviewTemplate();
        this.setFormUpdate();
      }
    });
  }

  ngOnDestroy(): void {
    this.#subscription.unsubscribe();
  }

  setFormUpdate(): void {
    this.form?.get('id').setValue(this.selectedId);
    this.form?.get('name').setValue(this.selectedName);
    this.form?.get('active').setValue(this.selectedStatus);
    this.form?.get('type').setValue(this.selectedType);
    this.form?.get('remark').setValue(this.providedRemark);
    this.form?.get('employees').setValue(this.getSelectedEmployees());
  }

  getSelectedEmployees(): Array<number> {
    return this.optionEmployees
      .filter((keyValue: KeyValue<number, string>) =>
        this.selectedEmployee.includes(keyValue.key),
      )
      .map((result) => result.key)
      .map((value) => value);
  }

  get status() {
    return this.form?.get('active')?.value;
  }

  get name() {
    return this.form?.get('name')?.value;
  }

  get type() {
    return this.form?.get('type')?.value;
  }

  get remark() {
    return this.form?.get('remark')?.value;
  }

  get employee() {
    return this.form?.get('employees')?.value;
  }

  save(): void {
    const fieldName = this.form?.get('name')?.value;
    if (this.form?.invalid || !fieldName) {
      if (!fieldName) {
        this.form?.controls?.name?.setErrors({
          incorrect: true,
        });
      }
      return;
    } else {
      if (this.data?.isUpdated) {
        this.updateTemplate(fieldName);
      } else {
        this.createTemplate(fieldName);
      }
    }
  }

  updateTemplate(fieldName: string): void {
    this.interviewTemplateService
      .validateInterviewTemplate(fieldName, this.selectedId)
      .subscribe((result) => {
        if (result) {
          this.form?.controls?.name?.setErrors({
            incorrect: true,
          });
          this.validateName = result;
        } else {
          if (this.selectedId) {
            this.interviewTemplateService
              .update(this.selectedId, this.form.value)
              .subscribe(
                () => {
                  this.showSuccessMessage(
                    'Add interview template successfully',
                  );
                  this.dialogRef.close({ updated: true });
                },
                () => {
                  this.alertSnackbarMessage(
                    'Error to update interview template',
                  );
                },
              );
          }
        }
      });
  }

  createTemplate(fieldName: string): void {
    this.interviewTemplateService
      .validateInterviewTemplate(fieldName, null)
      .subscribe((result) => {
        if (result) {
          this.form?.controls?.name?.setErrors({ incorrect: true });
          this.validateName = result;
        } else {
          this.interviewTemplateService.create(this.form.value).subscribe(
            () => {
              this.showSuccessMessage('Add interview template successfully');
              this.dialogRef.close({ created: true });
            },
            () => {
              this.alertSnackbarMessage('Error to add interview template');
            },
          );
        }
      });
  }

  private alertSnackbarMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  changeToggle(): void {
    if (this.form.get('active').value === false) {
      this.slideValue = 'Active';
    } else {
      this.slideValue = 'Inactive';
    }
  }

  clear(): void {
    this.form.controls.name.setValue('');
  }

  closeDialog(): void {
    this.dialogRef.close();
  }
}
