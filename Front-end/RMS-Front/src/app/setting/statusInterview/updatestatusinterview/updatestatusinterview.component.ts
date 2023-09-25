import { MessageService } from './../../../core/service/message.service';
import { InterviewTemplateModel } from '../../../core/model/interview-template';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { InterviewTemplateService } from '../../../core/service/interview-template.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';

@Component({
  selector: 'app-updatestatusinterview',
  templateUrl: './updatestatusinterview.component.html',
  styleUrls: ['./updatestatusinterview.component.css'],
})
export class UpdatestatusinterviewComponent implements OnInit {
  public form: FormGroup;
  slidevalue: string;
  id: number;
  constructor(
    private formbuilder: FormBuilder,
    private message: MessageService,
    private service: InterviewTemplateService,
    public dialogRef: MatDialogRef<UpdatestatusinterviewComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: InterviewTemplateModel,
  ) {}

  btnsave(): void {
    if (this.form.invalid) {
      return;
    }
    this.service.update(this.id, this.form.value).subscribe(
      () => {
        this.message.showSuccess('Update Sucess', 'Update Status Interview');
        this.onNoClick();
      },
      () => {},
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
      id: [' '],
      name: [' ', Validators.required],
      active: [' '],
    });
    this.form.patchValue({
      id: this.data.id,
      name: this.data.name,
      active: this.data.active,
    });
    if (this.data.active === true) {
      this.slidevalue = 'Active';
    } else {
      this.slidevalue = 'Inactive';
    }
  }
  clearName(): void {
    this.form.controls.name.setValue('');
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
}
