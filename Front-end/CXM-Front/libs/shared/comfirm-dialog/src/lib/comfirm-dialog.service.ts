import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { ComfirmDialogComponent } from './comfirm-dialog.component';

@Injectable({
  providedIn: 'root',
})
export class ComfirmDialogService {
  dialogRef: MatDialogRef<ComfirmDialogComponent>;

  constructor(private dialog: MatDialog) {}

  /**
   * Cancel is text for cancel button.
   * Confrim is text for confirm button.
   * title is title of dialog popUp.
   * message is text for display.
   * @param cancel
   * @param confirm
   * @param title
   * @param message
   * @returns
   */

  public confirm(cancel?: string, confirm?: string ,title?: string, message?: string): Observable<boolean> {
    this.dialogRef = this.dialog.open(ComfirmDialogComponent, {
      width: '400px',
      disableClose: true,
      data: { cancel, confirm, title, message},
    });
    return <Observable<boolean>>this.dialogRef.afterClosed();
  }
}
