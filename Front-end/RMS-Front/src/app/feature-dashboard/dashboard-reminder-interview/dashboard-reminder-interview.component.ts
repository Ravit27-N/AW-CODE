import { Component, OnInit } from '@angular/core';
import { DashboardReportInterviewModel, InterviewService } from '../../core';
import {
  aWeekFrom,
  EntityResponseHandler,
  firstDayWeeks,
  formatDateWithoutTime,
} from '../../shared';

@Component({
  selector: 'app-dashboard-reminder-interview',
  templateUrl: './dashboard-reminder-interview.component.html',
  styleUrls: ['./dashboard-reminder-interview.component.scss'],
})
export class DashboardReminderInterviewComponent implements OnInit {
  interviews: EntityResponseHandler<DashboardReportInterviewModel> = {
    contents: [],
    total: 0,
    page: 1,
    pageSize: 10,
    summary: {},
  };

  constructor(private interviewService: InterviewService) {}

  async ngOnInit(): Promise<void> {
    await this.fetchInterviewReport();
  }

  async fetchInterviewReport(): Promise<void> {

    const monday = firstDayWeeks(new Date());
    const aWeekFromNow = aWeekFrom(monday);

    this.interviews = await this.interviewService
      .getList(3, 1, {
        startDate: formatDateWithoutTime(monday),
        endDate: formatDateWithoutTime(aWeekFromNow),
        sortByField: 'dateTime',
        sortDirection: 'asc',
      })
      .toPromise();
  }
}
