import { Component, Input, OnInit } from '@angular/core';
import { DashboardReportInterviewModel } from '../../../core';
import { getAssetPrefix } from '../../../shared';

@Component({
  selector: 'app-reminder-interview-card',
  templateUrl: './reminder-interview-card.component.html',
  styleUrls: ['./reminder-interview-card.component.scss'],
})
export class ReminderInterviewCardComponent implements OnInit {
  @Input() interview: DashboardReportInterviewModel;

  assetPrefix = `${getAssetPrefix()}/assets/img/user-icon.png`;

  constructor() {}

  ngOnInit(): void {}
}
