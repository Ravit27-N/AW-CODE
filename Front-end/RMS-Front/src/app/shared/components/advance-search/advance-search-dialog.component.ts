import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-advance-search-dialog',
  templateUrl: './advance-search-dialog.component.html'
})
export class AdvanceSearchDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<AdvanceSearchDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
