import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface IChooseFileDialog {
  header: string;
  width: string;
  height: string;
  closeOnSelect: boolean;
  start?: string;
}

@Component({
  selector: 'app-choose-file-dialog',
  templateUrl: './choose-file-dialog.component.html'
})
export class ChooseFileDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ChooseFileDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: IChooseFileDialog) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  recieveFile(event: string): void {
    if (this.data.closeOnSelect) {
      this.dialogRef.close({
        ok: true,
        data: event
      });
    }
  }
}
