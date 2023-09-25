import { MessageService } from './../../../core/service/message.service';
import { InterviewTemplateModel } from '../../../core/model/interview-template';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { InterviewTemplateService } from '../../../core/service/interview-template.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';

@Component({
  selector: 'app-addstatusinterview',
  templateUrl: './addstatusinterview.component.html',
  styleUrls: ['./addstatusinterview.component.css'],
})
export class AddstatusinterviewComponent implements OnInit {
  public form: FormGroup;
  slidevalue = 'Active';

  constructor(
    private formbuilder: FormBuilder,
    private service: InterviewTemplateService,
    private message: MessageService,
    public dialogRef: MatDialogRef<AddstatusinterviewComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: InterviewTemplateModel,
  ) {}
  btnsave(): void {
    if (this.form.invalid) {
      return;
    }
    this.service.create(this.form.value).subscribe(
      () => {
        this.message.showSuccess('Add Sucess', 'Add Status Candidate');
        this.dialogRef.close();
      },
      () => {
        this.message.showError('Add Fail', 'Add Status Candidate');
      },
    );
  }
  onChange(): void {
    if (this.form.get('active').value === false) {
      this.slidevalue = 'Active';
    } else {
      this.slidevalue = 'Inactive';
    }
  }
  ngOnInit(): void {
    this.form = this.formbuilder.group({
      name: ['', Validators.required],
      active: [true],
    });
  }

  clearName(): void {
    this.form.controls.name.setValue('');
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
}
