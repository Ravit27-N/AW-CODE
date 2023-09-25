import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { ConfirmationMessageComponent } from './confirmation-message.component';
import { ConfirmationPopupComponent } from './confirmation-popup/confirmation-popup.component';
import { ConfirmationMessage } from './confirmation.model';
import {InformationPopUpComponent} from "./information-popup/information-pop-up.component";

@Injectable({
  providedIn: 'any'
})
export class ConfirmationMessageService {

  defaultButtonColor = '#C1C1C1';
  dialogRef: MatDialogRef<ConfirmationMessageComponent>;
  informationDialogRef: MatDialogRef<InformationPopUpComponent>;

  constructor(
    private dialog: MatDialog
  ) {
  }

  /**
   * Method used to show confirmation message.
   * @param icon
   * @param heading
   * @param title
   * @param message
   * @param cancelButton
   * @param cancelButtonColor
   * @param confirmButton
   * @param confirmButtonColor
   * return {@param type
@link Observable}
   */
  showConfirmationMessage(icon?: string, heading?: string, title?: string,
                          message?: string,
                          cancelButton?: string,
                          cancelButtonColor = this.defaultButtonColor,
                          confirmButton?: string,
                          confirmButtonColor = this.defaultButtonColor,
                          type?: string): Observable<boolean> {
    this.dialogRef = this.dialog.open(ConfirmationMessageComponent,
      {
        width: '52vh',
        data: {
          icon,
          heading,
          title,
          message,
          cancelButton,
          cancelButtonColor,
          confirmButton,
          confirmButtonColor,
          type
        },
        panelClass: 'custom-confirmation-message'
      }
    );
    return <Observable<boolean>>this.dialogRef.afterClosed();
  }

  /**
   * New confirmation dialog popup.
   * @param confirmationData - object of {@link ConfirmationMessage}.
   */
  showConfirmationPopup(confirmationData: ConfirmationMessage): Observable<boolean> {
    this.dialogRef = this.dialog.open(ConfirmationPopupComponent,
      {
        width: '683px',
        data: {...confirmationData},
        panelClass: 'custom-confirmation-popup'
      }
    );
    return <Observable<boolean>>this.dialogRef.afterClosed();
  }

  showInformationPopup(title?: string, label?: string, message?: string): Observable<boolean> {
    this.informationDialogRef = this.dialog.open(InformationPopUpComponent,
      {
        width: '900px',
        data: {
          title,
          label,
          message,
        },
        panelClass: 'information-pop-up'
      }
    );
    return <Observable<boolean>>this.informationDialogRef.afterClosed();
  }
}
