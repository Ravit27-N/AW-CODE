import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { PopupReturnAddressComponent } from "./popup-return-address.component";
import { Observable } from "rxjs";
import { FragmentReturnAddressType } from "@cxm-smartflow/shared/fragments/return-address";

@Injectable({
  providedIn: 'root'
})
export class PopupReturnAddressService {

  private _dialogRef: MatDialogRef<PopupReturnAddressComponent>;

  constructor(private _matDialog: MatDialog) {}

  show(fragmentReturnAddressType: FragmentReturnAddressType | null): Observable<FragmentReturnAddressType | null | undefined> {
    this._dialogRef = this._matDialog.open(PopupReturnAddressComponent, {
      width: '900px',
      height: 'auto',
      data: { fragmentReturnAddressType: fragmentReturnAddressType },
      disableClose: true,
      autoFocus: false,
    });

    return <Observable<FragmentReturnAddressType | null | undefined>>this._dialogRef.afterClosed();
  }

  close(): void {
    this._dialogRef.close();
  }

}
