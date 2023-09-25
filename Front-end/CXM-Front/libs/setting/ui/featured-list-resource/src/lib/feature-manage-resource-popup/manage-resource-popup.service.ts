import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FeatureManageResourcePopupComponent } from './feature-manage-resource-popup.component';
import { Observable } from 'rxjs';

export interface ManageResourceParams {
  formType: 'create' | 'update',
}

@Injectable({
  providedIn: 'root',
})
export class ManageResourcePopupService {
  private _dialogRef: MatDialogRef<FeatureManageResourcePopupComponent>;
  constructor(private _matDialog: MatDialog,) {}

  show(data: ManageResourceParams): Observable<any> {
    this._dialogRef = this._matDialog.open(FeatureManageResourcePopupComponent,
      {
        width: '1010px',
        data,
        panelClass: 'custom-change-password-pop-up-dialog',
        disableClose: true
      }
    );

    return <Observable<boolean>>this._dialogRef.afterClosed();
  }

  close(): void {
    this._dialogRef.close();
  }
}
