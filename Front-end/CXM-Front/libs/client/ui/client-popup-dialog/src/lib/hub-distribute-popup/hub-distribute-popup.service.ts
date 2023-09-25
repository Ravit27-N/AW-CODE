import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { HubDistributePopupComponent } from './hub-distribute-popup.component';
import { HubDistributePopupModel } from '@cxm-smartflow/client/data-access';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class HubDistributePopupService {
  private _dialogRef: MatDialogRef<HubDistributePopupComponent>;

  constructor(private _matDialog: MatDialog) {}

  show(
    data: HubDistributePopupModel
  ): Observable<HubDistributePopupModel> {
    this._dialogRef = this._matDialog.open(HubDistributePopupComponent, {
      width: '683px',
      height: 'auto',
      data,
      panelClass: 'custom-change-password-pop-up-dialog',
      disableClose: true,
    });

    return <Observable<HubDistributePopupModel>>this._dialogRef.afterClosed();
  }

  close(): void {
    this._dialogRef.close();
  }
}
