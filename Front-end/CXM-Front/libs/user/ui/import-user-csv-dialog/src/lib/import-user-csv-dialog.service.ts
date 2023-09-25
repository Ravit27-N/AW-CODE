import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { ImportUserCsvDialogComponent } from './import-user-csv-dialog.component';
import { BatchUserResponse } from '@cxm-smartflow/user/data-access';

@Injectable({
  providedIn: 'root',
})
export class ImportUserCsvDialogService {
  private _dialogRef: MatDialogRef<ImportUserCsvDialogComponent>;

  constructor(private _matDialog: MatDialog) {}

  show(data: { status: 'in_progress' | 'done'; content: BatchUserResponse }): Observable<void> {
    this._dialogRef?.close();

    this._dialogRef = this._matDialog.open(ImportUserCsvDialogComponent, {
      width: '580px',
      height: 'auto',
      data,
      panelClass: 'custom-change-password-pop-up-dialog',
      disableClose: true,
    });

    return this._dialogRef.afterClosed();
  }

  close(): void {
    this._dialogRef.close();
  }
}
