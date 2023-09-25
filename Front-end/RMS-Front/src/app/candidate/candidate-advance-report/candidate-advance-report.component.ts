import {Component, OnInit, ViewChild} from '@angular/core';
import {TableReportStaffComponent} from "./table-report-staff";
import {TableReportInternComponent} from "./table-report-intern";
import {TableReportFollowingUpComponent} from "./table-report-following-up/table-report-following-up.component";

interface TabLabel {
  title: string;
  order: number;
  sort: string;
}

@Component({
  selector: 'app-candidate-advance-report',
  templateUrl: './candidate-advance-report.component.html',
  styleUrls: ['./candidate-advance-report.component.scss'],
})
export class CandidateAdvanceReportComponent implements OnInit {
  tabLabel: TabLabel[] = [];
  sortLabel = 'staff';
  @ViewChild('tableStaffReport')
  tableReportStaffComponent: TableReportStaffComponent;

  @ViewChild('tableInternReport')
  tableReportInternComponent: TableReportInternComponent;

  @ViewChild('tableFollowingUpReport')
  tableReportFollowingUpComponent: TableReportFollowingUpComponent;

  //staff
  constructor() {}
  onSubmit(): void {}

  ngOnInit(): void {
    this.tabLabel = [
      { title: 'Full Staff Report', order: 1, sort: 'staff' },
      { title: 'Intern Report', order: 2, sort: 'intern' },
      { title: 'Following Up Report', order: 3, sort: 'following_up' },
    ];
  }

  exportExcel() {
    if (this.sortLabel === 'staff') {
      this.tableReportStaffComponent.exportStaffReport();
    } else if (this.sortLabel === 'intern') {
      this.tableReportInternComponent.exportInternReport();
    } else if (this.sortLabel === 'following_up') {
      this.tableReportFollowingUpComponent.exportFollowingUpReport();
    }
  }

  tabChanged(label: any) {
    const order = label.index + 1;
    this.sortLabel =
      this.tabLabel.find((value) => value?.order === order)?.sort || '';
  }
}



