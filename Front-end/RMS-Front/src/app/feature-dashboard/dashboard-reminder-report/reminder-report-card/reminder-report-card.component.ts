import { Component, Input, OnInit } from '@angular/core';
import { DashboardReportReminderModel } from '../../../core';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-reminder-report-card',
  templateUrl: './reminder-report-card.component.html',
  styleUrls: ['./reminder-report-card.component.scss'],
})
export class ReminderReportCardComponent implements OnInit {
  @Input() reminder: DashboardReportReminderModel;

  constructor(private domSanitizer: DomSanitizer) {}

  ngOnInit(): void {}

  transform(text: string) {
    return this.domSanitizer.bypassSecurityTrustHtml(text);
  }
}
