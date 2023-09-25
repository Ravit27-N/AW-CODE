import jsPDF from 'jspdf';
import 'jspdf-autotable';
import {
  CandidateCriteria,
  CandidateReportModel,
  CandidateService,
  DefaultCriteria,
  PAGINATION_SIZE,
  ReportFormData,
} from '../../core';
import {
  exportToCsv,
  exportToXlsx,
} from '../../core/spreadsheetCandidateReport';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import autoTable from 'jspdf-autotable';
import { IsLoadingService } from '@service-work/is-loading';
import {
  AwDateFormatPipe,
  aWeekFrom,
  firstDayWeeks,
  getAge,
  getAssetPrefix, last7Days,
} from '../../shared';

export interface CalendarOptionModel {
  startDate: Date;
  endDate: Date;
  option: number;
}

@Component({
  selector: 'app-candidate-report',
  templateUrl: './candidate-report.component.html',
  styleUrls: ['./candidate-report.component.scss'],
})
export class CandidateReportComponent implements OnInit {
  buttonExportWidth = '180px';
  filterCalendarSelected: CalendarOptionModel;
  filterCalendarSelectedChange: CalendarOptionModel;
  defaultCriteria: DefaultCriteria = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: 'createdAt',
    sortDirection: 'desc',
  };
  fetchStatus = false;
  total = 0;
  displayedColumns = [
    'No',
    'Full Name',
    'Gender',
    'age',
    'Phone',
    'From',
    'Priority',
    'GPA',
    'Last Interview',
    'Apply For',
    'Quiz',
    'Coding',
    'Average',
    'English',
    'Logical',
    'Flexibility',
    'Oral',
    'Remark',
  ];
  dataSource: MatTableDataSource<CandidateReportModel> =
    new MatTableDataSource();
  candidateList: CandidateReportModel[] = [];
  candidateListPDF: CandidateReportModel[] = [];
  pageIndex = 1;
  pageSize = 10;
  length = 0;
  filter = '';
  sortDirection = 'desc';
  sortByField = 'createdAt';
  pipe = new DatePipe('en-US');
  disButton = true;
  disSearch = true;
  paginationSize = PAGINATION_SIZE;
  isAbleToClick = false;

  isInitialize = true;

  candidateListCriteria: CandidateCriteria = {
    defaultCriteria: { ...this.defaultCriteria },
    filter: '',
    filterReminderOrInterview: '',
    status: '',
    isDeleted: false,
  };

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private candidateService: CandidateService,
    private isloadingService: IsLoadingService,
    private customDateformat: AwDateFormatPipe,
  ) {
  }

  ngOnInit() {
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

    this.calendarLevelChange(this.filterCalendarSelected);
  }

  htmlToPlaintext(text: string): any {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }

  getCandidateReport(): void {
    const from = this.pipe.transform(
      this.filterCalendarSelectedChange.startDate,
      'dd-MM-yyyy',
      'short',
    );
    const to = this.pipe.transform(
      this.filterCalendarSelectedChange.endDate,
      'dd-MM-yyyy',
      'short',
    );
    const subscription = this.candidateService
      .getCandidateReports(
        from,
        to,
        this.pageIndex,
        this.pageSize,
        this.sortDirection,
        this.sortByField,
        this.filter,
      )
      .subscribe((respone) => {
        this.candidateList = respone.contents.map((item) => {
          if (
            item.interviews.result === null ||
            item.interviews.result === undefined
          ) {
            item.interviews.result = {
              average: null,
              oral: '',
              english: '',
              logical: '',
              flexibility: '',
              score: {
                quiz: {
                  score: null,
                  max: null,
                },
                coding: {
                  score: null,
                  max: null,
                },
              },
            };
            item.interviews.description = this.htmlToPlaintext(
              item.interviews.description,
            );
            return item;
          } else if (
            item.interviews.result.score === null ||
            item.interviews.result.score === undefined
          ) {
            item.interviews.result = {
              score: {
                quiz: {
                  score: null,
                  max: null,
                },
                coding: {
                  score: null,
                  max: null,
                },
              },
            };

            item.interviews.description = this.htmlToPlaintext(
              item.interviews.description,
            );
            return item;
          }
          item.interviews.description = this.htmlToPlaintext(
            item.interviews.description,
          );
          return item;
        });
        this.candidateList.forEach((item) => {
          item.arrUniversities = item.universities
            ?.map((x) => x.name)
            ?.join(', ');
        });
        this.length = respone.total;
        this.total = respone.total;
        this.dataSource = new MatTableDataSource(this.candidateList);
        if (this.candidateList.length > 0) {
          this.disButton = false;
        } else {
          this.disButton = true;
        }
      });

    this.isloadingService.add(subscription, {
      key: 'report',
      unique: 'report',
    });
  }

  onSubmit(): void {
    this.fetchStatus = true;
    this.pageIndex = 1;
    this.filter = '';
    this.sortDirection = 'desc';
    this.sortByField = 'createdAt';
    this.getCandidateReport();
    this.disSearch = false;
    this.isAbleToClick = true;
  }

  sortData(sort: Sort): void {
    this.sortDirection = sort.direction;
    this.pageIndex = 1;
    switch (sort.active) {
      case 'Full Name':
        this.sortByField = 'firstname';
        break;
      case 'Gender':
        this.sortByField = 'gender';
        break;
      case 'Phone':
        this.sortByField = 'telephone';
        break;
      case 'Priority':
        this.sortByField = 'priority';
        break;
      case 'GPA':
        this.sortByField = 'gpa';
        break;
      default:
        this.sortByField = 'createdAt';
        break;
    }
    if (this.isAbleToClick) {
      this.getCandidateReport();
      this.paginator.pageIndex = 0;
    }
  }

  pageEvent(event: PageEvent): void {
    this.candidateListCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.candidateListCriteria.defaultCriteria.pageSize = event.pageSize;

    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    if (this.isAbleToClick) {
      this.getCandidateReport();
    }
  }

  clearFilter(): void {
    this.filter = '';
    this.pageIndex = 1;
    this.getCandidateReport();
    this.paginator.pageIndex = 0;
  }

  applyFilter(event): void {
    this.filter = event;
    this.pageIndex = 1;
    if (this.filter.trim() === null || this.filter.trim() === '') {
      this.getCandidateReport();
    }
    if (this.filter.length >= 3) {
      this.getCandidateReport();
    }
  }

  exportToXlsx(): void {
    this.pageIndex = 1;
    const from = this.pipe.transform(
      this.filterCalendarSelectedChange.startDate,
      'dd-MM-yyyy',
      'short',
    );
    const to = this.pipe.transform(
      this.filterCalendarSelectedChange.endDate,
      'dd-MM-yyyy',
      'short',
    );
    this.candidateService
      .getCandidateReports(
        from,
        to,
        this.pageIndex,
        this.length,
        this.sortDirection,
        this.sortByField,
        this.filter,
      )
      .subscribe((respone) => {
        respone.contents.map((item) => {
          item.description = this.htmlToPlaintext(item.description);
        });
        const formatter = (value) => this.customDateformat.transform(value);
        exportToXlsx('Candidate Reports.xlsx', respone.contents, formatter);
      });
  }

  exportToCsv(): void {
    this.pageIndex = 1;
    const from = this.pipe.transform(
      this.filterCalendarSelectedChange.startDate,
      'dd-MM-yyyy',
      'short',
    );
    const to = this.pipe.transform(
      this.filterCalendarSelectedChange.endDate,
      'dd-MM-yyyy',
      'short',
    );
    this.candidateService
      .getCandidateReports(
        from,
        to,
        this.pageIndex,
        this.length,
        this.sortDirection,
        this.sortByField,
        this.filter,
      )
      .subscribe((respone) => {
        const formatter = (value) => this.customDateformat.transform(value);
        exportToCsv('Candidate Reports.csv', respone.contents, formatter);
      });
  }

  getAge(date: string) {
    const age = getAge(date);
    return isNaN(age) || age <= 0 ? 'N/A' : age;
  }

  exportToPDF(): void {
    const getGpa = (gpa) => (!gpa || gpa <= 0 ? 'N/A' : gpa);
    const getExp = (exp) => (!exp ? 'N/A' : exp);

    const data: ReportFormData[] = [];
    let bodyData;
    this.pageIndex = 1;
    const from = this.pipe.transform(
      this.filterCalendarSelectedChange.startDate,
      'dd-MM-yyyy',
      'short',
    );
    const to = this.pipe.transform(
      this.filterCalendarSelectedChange.endDate,
      'dd-MM-yyyy',
      'short',
    );
    this.candidateService
      .getCandidateReports(
        from,
        to,
        this.candidateListCriteria.defaultCriteria.pageIndex,
        this.length,
        this.candidateListCriteria.defaultCriteria.sortDirection,
        this.candidateListCriteria.defaultCriteria.sortByField,
        this.filter,
      )
      .subscribe((respone) => {
        this.candidateListPDF = respone.contents.map((item) => {
          if (
            item.interviews.result === null ||
            item.interviews.result === undefined
          ) {
            item.interviews.result = {
              average: null,
              oral: '',
              english: '',
              logical: '',
              flexibility: '',
              score: {
                quiz: {
                  score: null,
                  max: null,
                },
                coding: {
                  score: null,
                  max: null,
                },
              },
            };
            item.interviews.description = this.htmlToPlaintext(
              item.interviews.description,
            );
            return item;
          } else if (
            item.interviews.result.score === null ||
            item.interviews.result.score === undefined
          ) {
            item.interviews.result = {
              average: null,
              oral: '',
              english: '',
              logical: '',
              flexibility: '',
              score: {
                quiz: {
                  score: null,
                  max: null,
                },
                coding: {
                  score: null,
                  max: null,
                },
              },
            };
            item.interviews.description = this.htmlToPlaintext(
              item.interviews.description,
            );
            return item;
          }
          item.interviews.description = this.htmlToPlaintext(
            item.interviews.description,
          );
          return item;
        });

        let i = 1;
        this.candidateListPDF.forEach((item) => {
          data.push({
            no: i,
            fullname: item.fullName,
            gender: item.gender,
            phone: item.telephone,
            from: item?.universities
              ?.map((x) => (x.name !== undefined ? x.name : ''))
              .join(', '),
            priority: item.priority,
            gpa: item.gpa + '',
            lastInterview: this.customDateformat.transform(
              item.interviews?.dateTime,
            ),
            applyFor: item.interviews?.title,
            quiz: item.interviews?.result?.score?.quiz?.score,
            coding: item.interviews?.result?.score?.coding?.score,
            average: item.interviews?.result?.average,
            english: item.interviews?.result?.english,
            logical: item.interviews?.result?.logical,
            flexibility: item.interviews?.result?.flexibility,
            oral: item.interviews?.result?.oral,
            remark: item.interviews?.description,
            dateOfBirth: item.dateOfBirth,
            yearOfExperience: getExp(item.yearOfExperience),
          });
          i++;
        });

        bodyData = data.map((item) => [
          item.no,
          item.fullname,
          item.gender,
          this.getAge(item.dateOfBirth),
          item.phone,
          item.from,
          item.priority,
          getGpa(item.gpa),
          getExp(item.yearOfExperience),
          item.lastInterview,
          item.applyFor,
          item.quiz,
          item.coding,
          item.average,
          item.english,
          item.logical,
          item.flexibility,
          item.oral,
          item.remark,
        ]);

        const doc = new jsPDF({
          format: 'a4',
          unit: 'mm',
          orientation: 'l',
        });

        const today = this.pipe.transform(new Date(), 'dd/MM/yyyy hh:mm:ss a');
        const title = 'Candidate Report ' + today + '.pdf';
        doc.setProperties({
          title,
          subject: 'Candidate Reports',
          author: 'ALLWEB',
          keywords: '',
          creator: 'ALLWEB',
        });

        // Header
        const img = new Image();
        img.src = `${getAssetPrefix()}/assets/img/all-web-logo.png`;
        doc.addImage(img, 'png', 5, 5, 10, 9);
        doc.setFontSize(7);
        doc.text('Candidate Report', 17, 8);
        doc.setFontSize(6);
        doc.text('Author: Recruiter', 17, 11);
        doc.text('Created At: ' + today, 17, 14);
        const ms1 =
          'From: ' +
          this.pipe.transform(
            this.filterCalendarSelectedChange.startDate,
            'dd/MM/yyyy',
            'short',
          );
        const ms2 =
          'To: ' +
          this.pipe.transform(
            this.filterCalendarSelectedChange.endDate,
            'dd/MM/yyyy',
            'short',
          );
        doc.text(ms1, 256, 14);
        doc.text(ms2, 275, 14);
        // // Footers
        let pageNumber = 1;
        const footer = () => {
          doc.text('Page ' + pageNumber, 280, 205);
          pageNumber++;
        };

        // Body
        autoTable(doc, {
          head: [
            [
              'No.',
              'Full Name',
              'Gender',
              'Age',
              'Phone',
              'University',
              'Priority',
              'GPA',
              'Experience',
              'Interviewed',
              'Apply For',
              'Quiz',
              'Coding',
              'Average',
              'English',
              'Logical',
              'Flexibility',
              'Oral',
              'Remark',
            ],
          ],
          headStyles: { halign: 'left', fontSize: 7, textColor: 'white' },
          bodyStyles: { textColor: 'black', fontSize: 6, halign: 'left' },
          margin: { top: 18, left: 5, bottom: 10, right: 5 },
          styles: { lineWidth: 0.01, lineColor: 'gray' },
          theme: 'striped',
          showHead: 'firstPage',
          rowPageBreak: 'auto',
          body: bodyData,
          didDrawPage: footer,
        });
        doc.output('dataurlnewwindow');
      });
  }

  calendarLevelChange(calendarOption: CalendarOptionModel): void {
    this.filterCalendarSelectedChange = calendarOption;
    this.isInitialize = false;
    this.onSubmit();
  }
}
