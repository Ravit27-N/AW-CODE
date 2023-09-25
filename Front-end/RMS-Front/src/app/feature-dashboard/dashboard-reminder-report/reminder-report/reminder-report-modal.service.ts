import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { ReminderReportModalComponent } from './reminder-report-modal.component';
import { DashboardReportReminderModel } from '../../../core';

@Injectable({
  providedIn: 'root',
})
export class ReminderReportService {
  private dialogRef: MatDialogRef<ReminderReportModalComponent>;

  constructor(private dialog: MatDialog) {}

  previewReminder(reminder: DashboardReportReminderModel): Observable<void> {
    this.dialogRef = this.dialog.open(ReminderReportModalComponent, {
      width: '550px',
      data: {
        reminder,
      },
      panelClass: 'custom-confirmation-popup',
    });

    return this.dialogRef.afterClosed();
  }
}
