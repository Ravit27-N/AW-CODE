import { MessageService } from './../../../core/service/message.service';
import { UniversityModel } from './../../../core/model/university';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UniversityService } from './../../../core/service/university.service';
import { Validators, FormBuilder, FormGroup } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';

@Component({
  selector: 'app-adduniversity',
  templateUrl: './adduniversity.component.html',
  styleUrls: ['./adduniversity.component.css']
})
export class AdduniversityComponent implements OnInit {

  public form: FormGroup;
  slidevalue = 'Inactive';

  constructor(private formbuilder: FormBuilder, private message: MessageService,
    private service: UniversityService,
    public dialogRef: MatDialogRef<AdduniversityComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: UniversityModel) { }

  btnsave(): void {
    if (this.form.invalid) {
      return;
    }
    this.service.create(this.form.value).subscribe(() => {
      this.message.showSuccess('Add Sucess', 'Add University');
      this.dialogRef.close();
    }, () => {
      this.message.showError('Add Fail', 'Add University');
    });
  }
  ngOnInit(): void {
    this.form = this.formbuilder.group({
      name: ['', Validators.required],
      address: ['']
    });
  }
  clearName(): void {
    this.form.controls.name.setValue('');
  }
  clearAddress(): void {
    this.form.controls.address.setValue('');
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
}
