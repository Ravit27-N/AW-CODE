import {Component} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'cxm-smartflow-confirm-revert-configuration-version',
  templateUrl: './confirm-revert-configuration-version.component.html',
  styleUrls: ['./confirm-revert-configuration-version.component.scss']
})
export class ConfirmRevertConfigurationVersionComponent {

  constructor(private _dialogRef: MatDialogRef<ConfirmRevertConfigurationVersionComponent>) {}

  closeConfirmRevertPopup(): void {
    this._dialogRef.close(false);
  }

  confirmRevert(): void {
    this._dialogRef.close(true);
  }

}
