import { RemoveFileComponent } from './../../../shared/components/remove-file/remove-file.component';
import { JobModel } from './../../../core/model/Job';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { JobService } from './../../../core/service/job.service';
import { Validators, FormGroup, FormBuilder } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';
import { MessageService } from 'src/app/core/service/message.service';
import { IsLoadingService } from '@service-work/is-loading';
import { getFileIcon } from '../../../core/file';
@Component({
  selector: 'app-update-job',
  templateUrl: './update-job.component.html',
  styleUrls: ['./update-job.component.css']
})
export class UpdateJobComponent implements OnInit {
  filename: string;
  public form: FormGroup;
  slideValue = 'Active';
  removable = true;
  afterRemove = false;
  fileToUpload: File;
  newFileUpload = false;
  constructor(public dialog: MatDialog, private formbuilder: FormBuilder, private message: MessageService,
    private service: JobService, public dialogRef: MatDialogRef<UpdateJobComponent>,
    @Inject(MAT_DIALOG_DATA) public data: JobModel,
    private isloadingService: IsLoadingService) { }

  btnsave(): void {
    if (this.form.invalid) {
      return;
    }
    const subscription = this.service.update(this.data.id, this.form.value).subscribe(() => {
      this.message.showSuccess('Update Sucess', 'Update Job');
      this.dialogRef.close();
    }, () => {
      this.message.showError('Update Error', 'Update Job');
    });
    this.isloadingService.add(subscription, { key: 'UpdateJobComponent', unique: 'UpdateJobComponent' });
  }
  ngOnInit(): void {

    this.form = this.formbuilder.group({
      id: [''],
      title: ['', Validators.required],
      description: [''],
      filename: [''],
      active: ['']
    });
    this.form.patchValue({
      title: this.data.title,
      description: this.data.description,
      id: this.data.id,
      filename: this.data.filename,
      active: this.data.active
    });
    if (this.form.get('active').value === true) {
      this.slideValue = 'Active';
    }
    else {
      this.slideValue = 'Inactive';
    }
    if (this.data.filename === '') {
      this.afterRemove = true;
    }
  }
  clearName(): void {
    this.form.controls.title.setValue('');
  }
  clearDescription(): void {
    this.form.controls.description.setValue('');
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
  onChange(): void {
    if (this.form.get('active').value === false) {
      this.slideValue = 'Active';
    }
    else {
      this.slideValue = 'Inactive';
    }
  }
  fileUpload(event): void {
    this.fileToUpload = event.target.files[0];
    if (event.target.files[0]) {
      const fd = new FormData();
      fd.append('filename', this.fileToUpload);
      if (this.fileToUpload.size < 1000000) {
        this.service.fileUpdate(this.form.controls.id.value, fd).subscribe(res => {
          this.message.showSuccess('File Upload Sucess', 'Update Job');
          this.form.controls.filename.setValue(res.fileName);
          this.newFileUpload = true;
        }, () => {
          this.message.showError('File Upload Fail', 'Update Job');
        });
      }
      else {
        this.message.showWarning('File upload must not be more than 1MB', 'Update Job');
      }
    }
  }
  removeFile(): void {
    const dialogRef = this.dialog.open(RemoveFileComponent, {
      data: { title: this.form.controls.filename.value },
      width: '450px',
      panelClass: 'overlay-scrollable'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.service.fileRemove(this.form.controls.id.value, this.form.controls.filename.value).subscribe(() => {
          this.message.showSuccess('Delete File Sucess', 'Update Job');
          this.form.controls.filename.setValue('');
          this.afterRemove = true;
        }, (err) => {
          this.message.showError(err);
        });
      }
    });
  }
  viewFile() {
    this.service.fileView(this.form.controls.id.value,this.form.controls.filename.value).subscribe(res => {
      const blob = new Blob([res], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      window.open(url);
    }, () => { });
  }
  getFileIcon(filename: string): any {
    return getFileIcon(filename);
  }
}
