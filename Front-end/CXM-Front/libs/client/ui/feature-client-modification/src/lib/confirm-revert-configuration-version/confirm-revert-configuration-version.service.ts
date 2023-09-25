import {Injectable} from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {Observable} from "rxjs";
import {ConfirmRevertConfigurationVersionComponent} from "./confirm-revert-configuration-version.component";

@Injectable({
  providedIn: 'root'
})
export class ConfirmRevertConfigurationVersionService {
  private _dialogRef: MatDialogRef<ConfirmRevertConfigurationVersionComponent>;

  constructor(private _matDialog: MatDialog) {}

  show(): Observable<any> {
    this._dialogRef = this._matDialog.open(ConfirmRevertConfigurationVersionComponent,
      {
        width: '650px',
        disableClose: true
      },
    );

    return <Observable<boolean>>this._dialogRef.afterClosed();
  }

  close(): void {
    this._dialogRef.close();
  }
}
