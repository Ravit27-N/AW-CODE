import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import {
  getFileIcon,
  JobModel,
  JobService,
  TemporaryFileService,
} from '../../../core';
import { AwConfirmMessageService, AwSnackbarService } from '../../../shared';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-feature-add-job-description-popup',
  templateUrl: './feature-add-job-description-popup.component.html',
  styleUrls: ['./feature-add-job-description-popup.component.scss'],
})
export class FeatureAddJobDescriptionPopupComponent implements OnInit {
  form: FormGroup;
  fileToUpload: File = null;
  slideValue = 'Active';
  icon = 'assets/icons/icon-file-pdf.png';
  acceptExtension = '.pdf';
  updatedIcon = 'assets/img/pdf2.png';
  fileName: string;
  fileSize: string;

  constructor(
    private formBuilder: FormBuilder,
    private service: JobService,
    private temporaryFileService: TemporaryFileService,
    private awSnackbarService: AwSnackbarService,
    private awConfirmMessageService: AwConfirmMessageService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public matDialogRef: MatDialogRef<FeatureAddJobDescriptionPopupComponent>,
  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.form = this.formBuilder.group({
      title: '',
      description: '',
      filename: '',
      active: true,
    });
  }

  onChange(): void {
    this.slideValue =
      this.form.get('active').value === false ? 'Active' : 'Inactive';
  }

  onSubmit(): void {
    if (this.form.invalid || !this.form.get('title').value) {
      if (!this.form.get('title').value) {
        this.form.controls.title.setErrors({ incorrect: true });
      }
      return;
    }
    this.service.create(this.form.value).subscribe(
      (jobModel: JobModel) => {
        this.showSuccessMessage('Add Job Success');
        this.matDialogRef.close(jobModel);
      },
      () => {
        this.alertSnackbarMessage('Add Job Error');
      },
    );
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  private alertSnackbarMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  fileUpload(event): void {
    this.fileToUpload = Array.from(event)[0] as File;
    if (this.fileToUpload) {
      const formData = new FormData();
      formData.append('file', this.fileToUpload);
      if (this.fileToUpload.size < 1000000) {
        this.service.fileUpload(formData).subscribe(
          (response) => {
            this.showSuccessMessage('Upload Success');
            this.fileName = response.fileName;
            this.fileSize = this.fileToUpload.size.toString();
            this.form.controls.filename.setValue(this.fileName);
          },
          () => {
            this.alertSnackbarMessage('Error Upload');
          },
        );
      } else {
        this.alertSnackbarMessage('File upload must not be more than 1MB');
      }
    }
  }

  clearName(): void {
    this.form.controls.title.setValue('');
  }

  async remove(): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Remove File',
        message:
          'Are you sure you want to remove this file? Once you comfirm file will be delete forever?',
        cancelButton: 'Cancel',
        confirmButton: 'Confirm',
      })
      .toPromise();
    if (confirmed) {
      try {
        await this.temporaryFileService.fileRemove(this.fileName).subscribe(
          () => {
            this.fileName = null;
            this.fileSize = null;
            this.form.controls.filename.setValue('');
            this.showSuccessMessage('Successful Remove File');
          },
          () => {
            this.alertSnackbarMessage('Fail Remove File');
          },
        );
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      }
    }
  }

  viewFile() {
    this.temporaryFileService.fileView(this.fileName).subscribe(
      (res) => {
        const blob = new Blob([res], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        window.open(url);
      },
      () => {},
    );
  }

  getFileIcon(filename: string): any {
    return getFileIcon(filename);
  }

  cancel(): void {
    this.initForm();
    this.matDialogRef.close();
  }
}
