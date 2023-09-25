import { MessageService } from '../../core';
import { StatusCandidateModel } from '../../core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { StatusCandidateService } from '../../core';
import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-updatestatuscandidate',
  templateUrl: './updatestatuscandidate.component.html',
  styleUrls: ['./updatestatuscandidate.component.css']
})
export class UpdatestatuscandidateComponent implements OnInit {
  slidevalue = 'active';
  form: FormGroup;
  id: number;
  activestatus: string;
  action: boolean;
  constructor(private formbuilder: FormBuilder, private message: MessageService, private actRoute: ActivatedRoute, private router: Router
    , private service: StatusCandidateService, public dialogRef: MatDialogRef<UpdatestatuscandidateComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StatusCandidateModel) { }
  btnsave(): void {
    if (this.form.invalid) {
      return;
    }
    this.service.update(this.id, this.form.value).subscribe(() => {
      this.message.showSuccess('Update Sucess', 'Update Candidate Staus');
      this.onNoClick();
    }, () => {
      this.message.showError('Update Fail', 'Update Candidate Status');
    });

  }
  ngOnInit(): void {
    this.form = this.formbuilder.group({
      id: [''],
      title: ['', Validators.required],
      description: ['', Validators.required],
      active: ['', Validators.required],
    });
    this.form.setValue({
      title: this.data.title,
      description: this.data.description,
      id: this.data.id,
      active: this.data.active
    });
    this.action = this.data.active;
    if (this.action) {
      this.activestatus = 'Active';
    } else {
      this.activestatus = 'Inactive';
    }
  }
  onChange(): void {
    if (this.form.get('active').value === false) {
      this.activestatus = 'Active';
    }
    else {
      this.activestatus = 'Inactive';
    }
  }
  clearTitle(): void {
    this.form.controls.title.setValue('');
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
}
