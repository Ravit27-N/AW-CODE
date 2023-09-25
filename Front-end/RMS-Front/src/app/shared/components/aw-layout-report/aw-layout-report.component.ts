import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-aw-layout-report',
  templateUrl: './aw-layout-report.component.html',
  styleUrls: ['./aw-layout-report.component.scss']
})
export class AwLayoutReportComponent implements OnInit {

  @Input() pageTitle = '';
  @Input() pageSubtitle = '';

  constructor() {}

  ngOnInit(): void {}
}
