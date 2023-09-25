import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DashboardReportReminderModel } from '../../../core';

@Component({
  selector: 'app-reminder-report',
  templateUrl: './reminder-report-modal.component.html',
  styleUrls: ['./reminder-report-modal.component.scss'],
})
export class ReminderReportModalComponent {
  constructor(
    private dialogRef: MatDialogRef<ReminderReportModalComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      reminder: DashboardReportReminderModel;
    },
  ) {}

  closeModal(): void {
    this.dialogRef.close();
  }
}
