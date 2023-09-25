import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ConfirmationMessage } from '../confirmation.model';

@Component({
  selector: 'cxm-smartflow-confirmation-popup',
  templateUrl: './confirmation-popup.component.html',
  styleUrls: ['./confirmation-popup.component.scss']
})
export class ConfirmationPopupComponent {

  constructor(
    public dialogRef: MatDialogRef<ConfirmationPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public confirmationData: ConfirmationMessage
  ) {}

  close(): void {
    if(this.confirmationData?.isNoEventBtnCancel) {
      this.dialogRef.close(undefined);
      return;
    }
    this.dialogRef.close(false);
  }
}
