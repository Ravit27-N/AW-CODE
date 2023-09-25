import { Component, OnInit } from '@angular/core';
import { DashboardReportReminderModel, ReminderService } from '../../core';
import {
  aWeekFrom,
  EntityResponseHandler,
  firstDayWeeks,
  formatDateWithoutTime,
} from '../../shared';
import { ReminderReportService } from './reminder-report';

@Component({
  selector: 'app-dashboard-reminder-report',
  templateUrl: './dashboard-reminder-report.component.html',
  styleUrls: ['./dashboard-reminder-report.component.scss'],
})
export class DashboardReminderReportComponent implements OnInit {
  reminders: EntityResponseHandler<DashboardReportReminderModel> = {
    contents: [],
    page: 1,
    pageSize: 0,
    summary: {},
    total: 0,
  };

  constructor(
    private reminderService: ReminderService,
    private previewReminderModal: ReminderReportService,
  ) {}

  async ngOnInit(): Promise<void> {
    await this.fetchReminderService();
  }

  async fetchReminderService(): Promise<void> {

    const monday = firstDayWeeks(new Date());
    const aWeekFromNow = aWeekFrom(monday);
    const startDate = formatDateWithoutTime(monday);
    const endDate = formatDateWithoutTime(aWeekFromNow);

    this.reminders = await this.reminderService
      .getDashboardList(1, 5, 'asc', 'dateReminder', startDate, endDate, true)
      .toPromise();
  }

  previewReminder(reminder: DashboardReportReminderModel) {
    this.previewReminderModal.previewReminder(reminder);
  }
}
