import { Component, Inject } from '@angular/core';
import {
  MAT_SNACK_BAR_DATA,
  MatSnackBarRef,
} from '@angular/material/snack-bar';

@Component({
  selector: 'cxm-smartflow-validation-popup',
  templateUrl: './validation-popup.component.html',
  styleUrls: ['./validation-popup.component.scss'],
})
export class ValidationPopupComponent {
  constructor(
    @Inject(MAT_SNACK_BAR_DATA) public data: any,
    private _snackbarRef: MatSnackBarRef<ValidationPopupComponent>
  ) {}

  dismiss(response: boolean): void {
    this._snackbarRef.dismiss();
    this.data.preClose(response);
  }
}
