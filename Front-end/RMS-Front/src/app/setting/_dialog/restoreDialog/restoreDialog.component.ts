import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Component, Inject } from '@angular/core';

@Component({
  templateUrl: './restoreDialog.component.html',
  styleUrls: ['./restoreDialog.component.css']
})
export class RestoreDialogComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public data,
    private dailogRef: MatDialogRef<RestoreDialogComponent>) { }

  closeDailog(): void {
    this.dailogRef.close(false);
  }
}
