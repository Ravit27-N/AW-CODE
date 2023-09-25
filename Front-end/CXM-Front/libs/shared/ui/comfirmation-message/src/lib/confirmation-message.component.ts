import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ConfirmationMessage } from './confirmation.model';

@Component({
  selector: 'cxm-smartflow-confirmation-message',
  templateUrl: './confirmation-message.component.html',
  styleUrls: ['./confirmation-message.component.scss']
})
export class ConfirmationMessageComponent {

  constructor(
    public dialogRef: MatDialogRef<ConfirmationMessageComponent>,
    @Inject(MAT_DIALOG_DATA) public confirmationData: ConfirmationMessage
  ) { }

  close(): void{
    this.dialogRef.close(false);
  }

}
