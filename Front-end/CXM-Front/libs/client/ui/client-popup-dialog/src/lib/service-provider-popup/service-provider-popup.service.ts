import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ServiceProviderFormModel } from '@cxm-smartflow/client/data-access';
import { Observable } from 'rxjs';
import { ServiceProviderPopupComponent } from './service-provider-popup.component';

@Injectable({
  providedIn: 'root',
})
export class ServiceProviderPopupService {
  private _dialogRef: MatDialogRef<ServiceProviderPopupComponent>;

  constructor(private _matDialog: MatDialog) {}

  show(data: { selected: ServiceProviderFormModel, initial: ServiceProviderFormModel }): Observable<ServiceProviderFormModel> {
    this._dialogRef = this._matDialog.open(ServiceProviderPopupComponent, {
      width: '683px',
      height: 'auto',
      data,
      panelClass: 'custom-change-password-pop-up-dialog',
      disableClose: true,
    });

    return <Observable<ServiceProviderFormModel>>this._dialogRef.afterClosed();
  }

  close(): void {
    this._dialogRef.close();
  }
}
