import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MetaDataPopupComponent } from './meta-data-popup.component';
import { Observable } from 'rxjs';
import { MetadataModel, MetadataPayloadType } from '@cxm-smartflow/client/data-access';
@Injectable({
  providedIn: 'root',
})
export class MetaDataPopupService {

  private _dialogRef: MatDialogRef<MetaDataPopupComponent>;

  constructor(private _matDialog: MatDialog) {}

  show(metadataType: MetadataPayloadType, data: Array<MetadataModel>): Observable<Array<MetadataModel>> {
    this._dialogRef = this._matDialog.open(MetaDataPopupComponent, {
      width: '683px',
      height: 'auto',
      data: { metadataType, data },
      panelClass: 'custom-change-password-pop-up-dialog',
      disableClose: true,
      autoFocus: false,
    });

    return <Observable<Array<MetadataModel>>>this._dialogRef.afterClosed();
  }

  close(): void {
    this._dialogRef.close();
  }

}
