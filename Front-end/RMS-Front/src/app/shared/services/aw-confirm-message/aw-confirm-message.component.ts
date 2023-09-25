import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ConfirmationMessage } from './aw-confirm-message.service';

@Component({
  selector: 'app-aw-confirm-message',
  templateUrl: './aw-confirm-message.component.html',
  styleUrls: ['./aw-confirm-message.component.scss'],
})
export class AwConfirmMessageComponent {
  constructor(
    public dialogRef: MatDialogRef<AwConfirmMessageComponent>,
    @Inject(MAT_DIALOG_DATA) public confirmationData: ConfirmationMessage,
  ) {}

  emitDialog(value: boolean | undefined): void {
    this.dialogRef.close(value);
  }
}
