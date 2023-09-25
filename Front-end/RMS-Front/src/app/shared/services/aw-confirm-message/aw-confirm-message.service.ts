import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { AwConfirmMessageComponent } from './aw-confirm-message.component';

/**
 * Configuration for the confirmation message.
 */
export interface ConfirmationMessage {
  icon?: string;
  title?: string;
  message?: string;
  cancelButton?: string;
  confirmButton?: string;
  type?: 'Warning' | 'Active' | 'Secondary';
}

@Injectable({
  providedIn: 'root',
})
export class AwConfirmMessageService {
  private dialogRef: MatDialogRef<AwConfirmMessageComponent>;

  constructor(private dialog: MatDialog) {}

  /**
   * Displays a confirmation popup dialog.
   *
   * @param confirmationData Configuration for the confirmation popup.
   * @returns An Observable that emits a boolean value when the dialog is closed.
   */
  showConfirmationPopup(
    confirmationData: ConfirmationMessage,
  ): Observable<boolean> {
    this.dialogRef = this.dialog.open(AwConfirmMessageComponent, {
      width: '683px',
      data: { ...confirmationData },
      panelClass: 'custom-confirmation-popup',
    });

    return this.dialogRef.afterClosed();
  }
}
