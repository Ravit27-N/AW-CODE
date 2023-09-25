import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import {
  CandidateAdvanceReportModel,
  CandidateCriteria,
  CandidateService,
  DefaultCriteria,
} from '../../../core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { DatePipe } from '@angular/common';
import { IsLoadingService } from '@service-work/is-loading';
import {aWeekFrom, BlobFile, firstDayWeeks, last7Days} from '../../../shared';

interface ReportForm {
  from: Date | number | string;
  to: Date | number | string;
}

interface CalendarOptionModel {
  startDate: Date;
  endDate: Date;
  option: number;
}
@Component({
  selector: 'app-table-report-intern',
  templateUrl: './table-report-intern.component.html',
  styleUrls: ['./table-report-intern.component.scss'],
})
export class TableReportInternComponent implements OnInit {
  //intern
  displayedColumnsIntern: string[] = [
    'No',
    'Full Name',
    'Gender',
    'Age',
    'School',
    'Year',
    'Experience',
    'Apply For',
    'Current Company',
    'Result',
    '1st Recruit',
    '2nd NCH HR TL',
    'QA',
    'Write Coding',
    'Availability',
    'Remarks',
  ];
  dataSourceIntern: MatTableDataSource<CandidateAdvanceReportModel>;
  canExport = false;
  //staff
  reportFormStaff: ReportForm;
  sortDirection = 'desc';
  sortByField = 'created_at';
  //intern
  reportFormIntern: ReportForm;
  pageIndex = 1;
  pageSize = 10;
  lengthIntern = 0;
  //following up
  reportFormFollowingUp: ReportForm;
  paginationSize = [50, 100, 200];
  filter = '';
  pipe = new DatePipe('en-US');
  candidateAdvanceReportModel: CandidateAdvanceReportModel[] = [];

  isInitializeIntern = true;

  filterCalendarSelected: CalendarOptionModel;

  defaultCriteria: DefaultCriteria = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: 'createdAt',
    sortDirection: 'desc',
  };
  filterListCriteria: CandidateCriteria = {
    defaultCriteria: { ...this.defaultCriteria },
    filter: '',
    filterReminderOrInterview: '',
    status: '',
    isDeleted: false,
  };
  total=0;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild('staffSort') staffSort: MatSort;
  @ViewChild('internSort') internSort: MatSort;
  @ViewChild('followingUpSort') followingUpSort: MatSort;

  constructor(
    private candidateService: CandidateService,
    private isloadingService: IsLoadingService,
  ) {
    const today = new Date();
    const lastweek = today.getDate() - 30;
    const setFrom = new Date(today.setDate(lastweek));
    // staff
    this.reportFormStaff = {
      from: setFrom,
      to: new Date(),
    };
    // intern
    this.reportFormIntern = {
      from: setFrom,
      to: new Date(),
    };
    // following up
    this.reportFormFollowingUp = {
      from: setFrom,
      to: new Date(),
    };
  }

  onSubmit(): void {}

  ngOnInit(): void {
    this.setUp();
  }

  setUp() {
    const today = new Date();
    const lastSevenDay = last7Days(today);
    this.filterCalendarSelected = {
      startDate: lastSevenDay,
      endDate: today,
      option: 0,
    };
    this.calendarLevelChangeInternship(this.filterCalendarSelected);
  }

  htmlToPlaintext(text: string): any {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }
  // intern
  getInternReport(): void {
    const from = this.pipe.transform(
      this.reportFormIntern.from,
      'yyyy-MM-dd',
      'short',
    );
    const to = this.pipe.transform(
      this.reportFormIntern.to,
      'yyyy-MM-dd',
      'short',
    );
    const subscription = this.candidateService
      .getAdvanceReports(
        from,
        to,
        'Intern',
        this.pageIndex,
        this.pageSize,
        this.sortDirection,
        this.sortByField,
      )
      .subscribe((response) => {
        this.candidateAdvanceReportModel = response.contents.map((item) => {
          item.remark = this.htmlToPlaintext(item.remark);
          return item;
        });
        this.total=response.total;
        this.lengthIntern = response.total;
        this.dataSourceIntern = new MatTableDataSource(
          this.candidateAdvanceReportModel,
        );
      });
    this.isloadingService.add(subscription, {
      key: 'report',
      unique: 'report',
    });
  }

  onSubmitIntern(): void {
    this.pageIndex = this.filterListCriteria.defaultCriteria.pageIndex;
    this.pageSize = this.filterListCriteria.defaultCriteria.pageSize;
    this.filter = '';
    this.getInternReport();
  }

  pageEventIntern(event: PageEvent): void {
    this.pageIndex = event.pageIndex + 1;
    this.pageSize = event.pageSize;
    this.getInternReport();
  }
  //search intern
  applyFilterIntern(value: string) {
    this.dataSourceIntern.filter = value.trim().toLowerCase();

    if (this.dataSourceIntern.paginator) {
      this.dataSourceIntern.paginator.firstPage();
    }
  }

  sortDataIntern(sort: Sort): void {
    this.sortDirection = sort.direction;
    this.pageIndex = 1;
    switch (sort.active) {
      case 'Full Name':
        this.sortByField = 'lastname';
        break;
      case 'Gender':
        this.sortByField = 'gender';
        break;
      case 'Age':
        this.sortByField = 'date_of_birth';
        break;
      case 'GPA':
        this.sortByField = 'gpa';
        break;
      case 'Experience':
        this.sortByField = 'year_of_experience';
        break;
    }
    this.getInternReport();
    this.paginator.pageIndex = 0;
  }

  // find grand all staff, intern, following up
  getAge(age: number): string {
    if (age === 0) {
      return 'N/A';
    } else {
      return age + '';
    }
  }

  //---intership
  getColorByResult(result: string): string {
    switch (result) {
      case 'Passed':
        return 'green';
      case 'In Processing':
        return 'orange';
      case 'Contacting for Interview':
        return 'orange';
      case 'Following Up':
        return 'orange';
      case 'New Request':
        return 'orange';
      case 'Failed':
        return 'red';
    }
  }

  async exportInternReport(): Promise<void> {
    if (!this.canExport) {
      return;
    }
    const from = this.pipe.transform(
      this.reportFormStaff.from,
      'yyyy-MM-dd',
      'short',
    );
    const to = this.pipe.transform(
      this.reportFormStaff.to,
      'yyyy-MM-dd',
      'short',
    );
    const blobFile = await this.candidateService
      .getExportAdvanceReports(
        from,
        to,
        'Intern',
        this.pageIndex,
        1000,
        this.sortDirection,
        this.sortByField,
      )
      .toPromise();
    const file: File = BlobFile.blobToFile(
      blobFile.file,
      `${blobFile.filename}.xlsx`,
    );
    BlobFile.saveFile(file);
  }
  calendarLevelChangeInternship(calendarOption: CalendarOptionModel): void {
    this.canExport = true;
    this.reportFormIntern.from = calendarOption.startDate;
    this.reportFormIntern.to = calendarOption.endDate;
    this.isInitializeIntern = false;
    this.onSubmitIntern();
  }

  pageEvent(event: PageEvent): void {
    this.filterListCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.filterListCriteria.defaultCriteria.pageSize = event.pageSize;

    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.onSubmitIntern();
  }
}
