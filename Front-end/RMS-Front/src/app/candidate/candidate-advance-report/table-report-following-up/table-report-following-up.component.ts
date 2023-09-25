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
  selector: 'app-table-report-following-up',
  templateUrl: './table-report-following-up.component.html',
  styleUrls: ['./table-report-following-up.component.scss'],
})
export class TableReportFollowingUpComponent implements OnInit {
  //following-up-candidates
  displayedColumnsFollowingUp: string[] = [
    'No',
    'Full Name',
    'Gender',
    'Age',
    'School',
    'Year',
    'GPA',
    'Experience',
    'Apply For',
    'Current Company',
    'Contact',
    '1st Recruit',
    '2nd NCH HR TL',
    'QA',
    'Write Coding',
    'Grade',
    'Remarks',
  ];
  dataSourceFollowingUp: MatTableDataSource<CandidateAdvanceReportModel>;

  //staff
  reportFormStaff: ReportForm;
  sortDirection = 'desc';
  sortByField = 'created_at';
  //intern
  reportFormIntern: ReportForm;
  //following up
  reportFormFollowingUp: ReportForm;
  pageIndex = 1;
  pageSize = 50;
  lengthFollowingUp = 0;
  paginationSize = [50, 100, 200];
  filter = '';
  pipe = new DatePipe('en-US');
  candidateAdvanceReportModel: CandidateAdvanceReportModel[] = [];

  isInitializeFollowingup = true;
  canExport = false;
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
  total = 0;
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

    this.calendarLevelChangeFollowingUp(this.filterCalendarSelected);
  }
  htmlToPlaintext(text: string): any {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }
  //staff following up
  getFollowingUpReport(): void {
    const from = this.pipe.transform(
      this.reportFormFollowingUp.from,
      'yyyy-MM-dd',
      'short',
    );
    const to = this.pipe.transform(
      this.reportFormFollowingUp.to,
      'yyyy-MM-dd',
      'short',
    );
    const subscription = this.candidateService
      .getAdvanceReports(
        from,
        to,
        'Following Up',
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
        this.lengthFollowingUp = response.total;
        this.dataSourceFollowingUp = new MatTableDataSource(
          this.candidateAdvanceReportModel,
        );
      });
    this.isloadingService.add(subscription, {
      key: 'report',
      unique: 'report',
    });
  }

  onSubmitFollowingUp(): void {
    this.pageIndex = this.filterListCriteria.defaultCriteria.pageIndex;
    this.pageSize = this.filterListCriteria.defaultCriteria.pageSize;
    this.filter = '';
    this.getFollowingUpReport();
  }

  pageEventFollowingUp(event: PageEvent): void {
    this.pageIndex = event.pageIndex + 1;
    this.pageSize = event.pageSize;
    this.getFollowingUpReport();
  }

  // find grand all staff, intern, following up
  getGrade(grade: number): string {
    if (grade >= 0 && grade <= 100) {
      if (grade > 0 && grade < 50) {
        return 'F';
      } else if (grade >= 50 && grade < 60) {
        return 'E';
      } else if (grade >= 60 && grade < 70) {
        return 'D';
      } else if (grade >= 70 && grade < 80) {
        return 'C';
      } else if (grade >= 80 && grade < 90) {
        return 'B';
      } else if (grade >= 90 && grade <= 100) {
        return 'A';
      }
    }
    return 'N/A';
  }

  //search following up
  applyFilterFollowingUp(event: string) {
    this.dataSourceFollowingUp.filter = event.trim().toLowerCase();

    if (this.dataSourceFollowingUp.paginator) {
      this.dataSourceFollowingUp.paginator.firstPage();
    }
  }

  sortDataFollowingUp(sort: Sort): void {
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
    this.getFollowingUpReport();
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
  //---following-up---
  getColorByExperience(experience: string): string {
    switch (experience) {
      case 'N/A':
        return 'red';
    }
  }

  getColorByCurrentCompany(currentCompany: string): string {
    switch (currentCompany) {
      case 'N/A':
        return 'red';
    }
  }

  calendarLevelChangeFollowingUp(calendarOption: CalendarOptionModel): void {
    this.canExport = true;
    this.reportFormFollowingUp.from = calendarOption.startDate;
    this.reportFormFollowingUp.to = calendarOption.endDate;
    this.isInitializeFollowingup = false;
    this.onSubmitFollowingUp();
  }

  async exportFollowingUpReport(): Promise<void> {
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
        'Following Up',
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

  pageEvent(event: PageEvent): void {
    this.filterListCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.filterListCriteria.defaultCriteria.pageSize = event.pageSize;

    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.onSubmitFollowingUp();
  }
}
