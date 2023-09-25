import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ComfirmDialog } from './comfirm-dialog.model';

@Component({
  selector: 'cxm-smartflow-comfirm-dialog',
  templateUrl: './comfirm-dialog.component.html',
  styleUrls: ['./comfirm-dialog.component.scss'],
})
export class ComfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ComfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ComfirmDialog
  ) {}
}
