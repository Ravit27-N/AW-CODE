import {Injectable} from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {Observable} from "rxjs";
import {AddressDestinationPopupComponent} from "./address-destination-popup.component";
import {Addresses} from "@cxm-smartflow/flow-deposit/data-access";

@Injectable({
  providedIn: 'root'
})
export class AddressDestinationPopupService {

  private _dialogRef: MatDialogRef<AddressDestinationPopupComponent>;

  constructor(private _matDialog: MatDialog) {
  }

  show(docUuid: string, addresses: Addresses): Observable<boolean> {
    this._dialogRef = this._matDialog.open(AddressDestinationPopupComponent, {
      width: '1000px',
      height: '630px',
      data: {docUuid: docUuid, addresses: addresses},
      panelClass: 'address-pop-up-dialog',
      disableClose: true,
    });

    return <Observable<boolean>>this._dialogRef.afterClosed();
  }

  close(): void {
    this._dialogRef.close();
  }
}
