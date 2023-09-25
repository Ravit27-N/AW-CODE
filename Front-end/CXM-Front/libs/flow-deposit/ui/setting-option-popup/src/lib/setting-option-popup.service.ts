import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { SettingOptionPopupComponent } from './setting-option-popup.component';
import { Observable } from 'rxjs';
import { SettingOptionCriteriaType } from '@cxm-smartflow/flow-deposit/data-access';

@Injectable({
  providedIn: 'root',
})
export class SettingOptionPopupService {

  private _dialogRef: MatDialogRef<SettingOptionPopupComponent>;

  constructor(private _matDialog: MatDialog) {}

  show(data: SettingOptionCriteriaType): Observable<boolean> {
    this._dialogRef = this._matDialog.open(SettingOptionPopupComponent, {
      width: '1000px',
      height: '630px',
      data,
      panelClass: 'common-pop-up-dialog',
      disableClose: true,
    });

    return <Observable<boolean>>this._dialogRef.afterClosed();
  }

  close(): void {
    this._dialogRef.close();
  }
}
