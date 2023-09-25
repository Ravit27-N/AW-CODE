import { RemoveFileComponent } from './../../../shared/components/remove-file/remove-file.component';
/* eslint-disable max-len */
import { getFileIcon } from '../../../core/file';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { JobService } from './../../../core/service/job.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { MessageService } from 'src/app/core/service/message.service';
import { IsLoadingService } from '@service-work/is-loading';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { TemporaryFileService } from 'src/app/core/service/temporary-file.service';

@Component({
  selector: 'app-add-job',
  templateUrl: './add-job.component.html',
  styleUrls: ['./add-job.component.css']
})
export class AddJobComponent implements OnInit {
  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = true;
  i: number;
  filenames: string;
  fileToUpload: File = null;
  public form: FormGroup;
  slidevalue = 'Active';
  showname: boolean = null;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  constructor(private formbuilder: FormBuilder, private message: MessageService,
    private service: JobService,
    private tempoararyService: TemporaryFileService,
    public dialogRef: MatDialogRef<AddJobComponent>,
    private isloadingService: IsLoadingService,
    public dialog: MatDialog) { }

  btnsave(): void {
    if (this.form.invalid) {
      return;
    }
    const subscription = this.service.create(this.form.value).subscribe((data) => {
      this.message.showSuccess('Add Sucess', 'Add Job');
      this.dialogRef.close(data);
    },
      (error: any) => {
        if (error.message === 'Title already exist') {
          this.message.showError(error.message, 'Add Job');
        } else {
          this.message.showError('Add Error', 'Add Job');
        }
      });

    this.isloadingService.add(subscription, { key: 'AddJobComponent', unique: 'AddJobComponent' });
  }
  ngOnInit(): void {
    this.form = this.formbuilder.group({
      title: ['', Validators.required],
      description: [''],
      filename: [''],
      active: [true]
    });
  }
  clearName(): void {
    this.form.controls.title.setValue('');
  }
  clearDescription(): void {
    this.form.controls.description.setValue('');
  }
  onNoClick(): void {
    if (this.form.controls.filename.value) {
      this.tempoararyService.fileRemove(this.form.controls.filename.value).subscribe(() => {
        this.dialogRef.close();
      }, () => {
      });
    } else {
      this.dialogRef.close();
    }
  }
  handleFileInput(files: FileList): void {
    this.fileToUpload = files.item(0);
  }
  onChange(): void {
    if (this.form.get('active').value === false) {
      this.slidevalue = 'Active';
    }
    else {
      this.slidevalue = 'Inactive';
    }
  }
  fileUpload(event): void {
    this.fileToUpload = event.target.files[0];
    if (event.target.files[0]) {
      const fd = new FormData();
      fd.append('file', this.fileToUpload);
      if (this.fileToUpload.size < 1000000) {
        this.filenames = null;
        const uploadFile = this.service.fileUpload(fd).subscribe(res => {
          this.message.showSuccess('Upload Sucess', 'Add Job');
          this.filenames = res.fileName;
          this.form.controls.filename.setValue(this.filenames);
        }, () => {
          this.message.showError('Error Upload', 'Add Job');
        });
        this.isloadingService.add(uploadFile, { key: 'fileUpload', unique: 'fileUpload' });
      }
      else {
        this.message.showWarning('File upload must not be more than 1MB', 'Add Job');
      }
    }
  }
  remove(): void {
    const dialogRef = this.dialog.open(RemoveFileComponent, {
      width: '450px',
      panelClass: 'overlay-scrollable'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.tempoararyService.fileRemove(this.filenames).subscribe(() => {
          this.message.showSuccess('Sucessful Remove File', 'Add Job');
          this.filenames = null;
          this.form.controls.filename.setValue('');
        }, () => {
          this.message.showError('Fail Remove File', 'Add Job');
        });
      }
    });
  }
  viewFile() {
    this.tempoararyService.fileView(this.filenames).subscribe(res => {
      const blob = new Blob([res], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      window.open(url);
    }, () => { });
  }
  getFileIcon(filename: string): any {
    return getFileIcon(filename);
  }
}
