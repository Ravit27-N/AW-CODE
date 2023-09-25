import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { FeatureImportDirectoryFeedCsvComponent } from './feature-import-directory-feed-csv.component';

@Injectable({
  providedIn: 'root',
})
export class ImportDirectoryFeedCsvService {
  private dialogRef: MatDialogRef<FeatureImportDirectoryFeedCsvComponent>;

  constructor(private matDialog: MatDialog) {}

  show(): Observable<any> {
    this.dialogRef = this.matDialog.open(
      FeatureImportDirectoryFeedCsvComponent,
      {
        width: '1010px',
        panelClass: 'custom-change-password-pop-up-dialog',
        disableClose: true,
      }
    );

    return <Observable<any>>this.dialogRef.afterClosed();
  }

  close(): void {
    this.dialogRef.close();
  }
}
