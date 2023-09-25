import { Injectable } from '@angular/core';
import { RejectedMail, RejectedMailComponent } from './rejected-mail.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RejectedMailService {

  dialogRef: MatDialogRef<RejectedMailComponent>;

  constructor(private dialog: MatDialog) {
  }

  showRejectedMail(data: RejectedMail): Observable<boolean> {
    this.dialogRef = this.dialog.open(RejectedMailComponent,
      {
        width: '90vh',
        panelClass: 'custom-reject-mail-dialog',
        // backdropClass: 'custom-reject-mail-overlay',
        data: {
          value: data
        }
      }
    );
    return <Observable<boolean>>this.dialogRef.afterClosed();
  }

}
