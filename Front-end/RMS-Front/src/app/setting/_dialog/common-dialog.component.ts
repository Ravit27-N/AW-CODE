import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, Inject } from '@angular/core';


interface ICommonDialogData {
  id?: string;
  name?: string;
  createdAt?: string;
  updatedAt?: string;
  active?: boolean;
  description?: string;
}

@Component({
  selector: 'app-common-dialog',
  templateUrl: './common-dialog.component.html'
})
export class CommonDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<CommonDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { common: ICommonDialogData; header: string }) { }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
