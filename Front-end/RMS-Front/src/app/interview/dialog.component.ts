import { Interview } from './../core/model/Interview';
import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-interview-form-dialog',
  templateUrl: './inerview-dialog.component.html'
})
export class InterviewDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<InterviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Interview
  ) { }

  onNoClick(): void {
    this.dialogRef.close();
  }
}

@Component({
  selector: 'app-interview-view-dialog',
  templateUrl: './inerview-view-dialog.component.html'
})
export class InterviewViewDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<InterviewViewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Interview
  ) { }

  onNoClick(): void {
    this.dialogRef.close();
  }
}

@Component({
  selector: 'app-interview-result-form-dialog',
  templateUrl: './inerview-result-dialog.component.html'
})
export class InterviewResultDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<InterviewResultDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Interview
  ) { }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
