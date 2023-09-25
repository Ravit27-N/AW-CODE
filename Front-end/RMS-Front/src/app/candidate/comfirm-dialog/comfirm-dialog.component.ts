import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, Inject } from '@angular/core';
export interface ConfirmDialogData {
  candidateId: number;
  statusId: number;
}
@Component({
  selector: 'app-comfirm-dialog',
  templateUrl: './comfirm-dialog.component.html',
  styleUrls: ['./comfirm-dialog.component.css']
})
export class ComfirmDialogComponent {

  constructor(
    private dialogRef: MatDialogRef<ComfirmDialogComponent>, @Inject(MAT_DIALOG_DATA) public candidate: ConfirmDialogData
  ) { }
}
