import { MessageService } from './../../core/service/message.service';
import { StatusCandidateModel } from 'src/app/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { StatusCandidateService } from './../../core/service/status-candidate.service';
import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-addstatuscandidate',
  templateUrl: './addstatuscandidate.component.html',
  styleUrls: ['./addstatuscandidate.component.css']
})
export class AddstatuscandidateComponent implements OnInit {
  public form: FormGroup;
  slidevalue = 'Active';
  constructor(private formbuilder: FormBuilder, private message: MessageService,
    private service: StatusCandidateService,
    public dialogRef: MatDialogRef<AddstatuscandidateComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: StatusCandidateModel) { }
  btnsave(): void {
    if (this.form.invalid) {
      return;
    }
    this.service.create(this.form.value).subscribe(() => {
      this.message.showSuccess('Add Sucess', 'Add Status Candidate');
      this.onNoClick();
    }, () => {
      this.message.showError('Error Add', 'Add Status Candidate');
    });
  }
  onChange(): void {
    if (this.form.get('active').value === false) {
      this.slidevalue = 'Active';
    }
    else {
      this.slidevalue = 'Inactive';
    }
  }
  ngOnInit(): void {
    this.form = this.formbuilder.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      active: [true, Validators.required],
      isAbleDelete: [''],
    });
  }
  clearTitle(): void {
    this.form.controls.title.setValue('');
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
}
