import { MessageService } from './../../../core/service/message.service';
import { UniversityModel } from 'src/app/core/model/university';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';
import { UniversityService } from 'src/app/core/service/university.service';

@Component({
  selector: 'app-updateuniversity',
  templateUrl: './updateuniversity.component.html',
  styleUrls: ['./updateuniversity.component.css']
})
export class UpdateuniversityComponent implements OnInit {

  public form: FormGroup;
  id: number;
  constructor(private formbuilder: FormBuilder, private message: MessageService,
    private service: UniversityService,
    public dialogRef: MatDialogRef<UpdateuniversityComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: UniversityModel
  ) { }

  btnsave(): void {
    if (this.form.invalid) {
      return;
    }
    this.service.update(this.id, this.form.value).subscribe(() => {
      this.message.showSuccess('Update Sucess', 'Update University');
      this.dialogRef.close();
    }, () => {
      this.message.showError('Update Fail', 'Update University');
    });
  }

  ngOnInit(): void {
    this.form = this.formbuilder.group({
      id: [''],
      name: ['', Validators.required],
      address: [''],
    });
    this.form.patchValue({
      id: this.data.id,
      name: this.data.name,
      address: this.data.address,
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
