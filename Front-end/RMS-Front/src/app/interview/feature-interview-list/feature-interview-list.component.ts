import { Component, OnInit } from '@angular/core';
import { DatePipe, KeyValue } from '@angular/common';
import { Router } from '@angular/router';
import {
  DefaultCriteria,
  Interview,
  ProjectCriteria,
  InterviewService,
  InterviewTemplateModel,
  InterviewTemplateService,
} from '../../core';
import {
  AwConfirmMessageService,
  aWeekFrom,
  AwPaginationModel,
  AwSnackbarService,
  firstDayWeeks,
} from '../../shared';
import { Sort } from '@angular/material/sort';
import { IsLoadingService } from '@service-work/is-loading';
import { MatDialog } from '@angular/material/dialog';
import {
  InterviewResultDialogComponent,
  InterviewViewDialogComponent,
} from '../dialog.component';
import { UrlUtil } from '../../shared/utils/url.util';

@Component({
  selector: 'app-feature-interview',
  templateUrl: './feature-interview-list.component.html',
  styleUrls: ['./feature-interview-list.component.scss'],
})
export class FeatureInterviewListComponent implements OnInit {
  dataSource: Array<Interview> = [];
  statusInterview: Array<InterviewTemplateModel> = [];
  total: number;
  dateFilter = UrlUtil.getParamsByKey('date') || '';
  defaultCriteria: DefaultCriteria = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: 'dateTime',
    sortDirection: 'desc',
  };
  interviewListCriteria: ProjectCriteria = {
    defaultCriteria: { ...this.defaultCriteria },
    filter: '',
    startDate: '',
    endDate: '',
    status: [],
    option: 0,
  };
  tableColumnHeader: string[] = [
    'id',
    'title',
    'candidate',
    'dateTime',
    'status',
    'action',
  ];

  filterGroup: Array<KeyValue<string, string>> = [
    { key: 'in processing', value: 'In Processing' },
    { key: 'following up', value: 'Following Up' },
    { key: 'failed', value: 'Failed' },
    { key: 'contacting for interview', value: 'Contacting for Interview' },
    { key: 'new request', value: 'New Request' },
    { key: 'passed', value: 'Passed' },
  ];

  pipe = new DatePipe('en-US');
  isInitialize = true;
  selectedCalendar = {
    startDate: new Date(),
    endDate: new Date(),
    option: 0,
  };
  labelButton = 'Filter';
  constructor(
    private router: Router,
    private awSnackbarService: AwSnackbarService,
    private awConfirmMessageService: AwConfirmMessageService,
    private interviewService: InterviewService,
    private statusInterviewService: InterviewTemplateService,
    private isLoadingService: IsLoadingService,
    public dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    localStorage.removeItem('form-interview');
    if (localStorage.getItem('interview-list')) {
      this.interviewListCriteria = JSON.parse(
        localStorage.getItem('interview-list'),
      );
      this.selectedCalendar = {
        startDate:
          this.interviewListCriteria.originalStartDate != null
            ? new Date(this.interviewListCriteria.originalStartDate)
            : null,
        endDate:
          this.interviewListCriteria.originalEndDate != null
            ? new Date(this.interviewListCriteria.originalEndDate)
            : null,
        option: this.interviewListCriteria.option,
      };
      this.isInitialize = this.selectedCalendar?.startDate === null;
    }

    if (this.dateFilter === 'this_week') {
      const monday = firstDayWeeks(new Date());
      this.selectedCalendar = {
        startDate: monday,
        endDate: aWeekFrom(monday),
        option: 0,
      };
      this.interviewListCriteria.startDate = this.pipe.transform(
        this.selectedCalendar.startDate,
        'dd-MM-yyyy',
        'short',
      );
      this.interviewListCriteria.endDate = this.pipe.transform(
        this.selectedCalendar.endDate,
        'dd-MM-yyyy',
        'short',
      );
      this.isInitialize = false;
    }
    this.fetchStatus();
    this.fetchInterviews();
  }

  addInterview(): void {
    this.router.navigateByUrl('/admin/interview/create');
  }

  filterChangeValue(event: any) {
    this.resetToFirstPaginator();
    this.interviewListCriteria.status = event?.filter;
    this.fetchInterviews();
  }

  searchFilterChange(event: any) {
    this.resetToFirstPaginator();
    this.interviewListCriteria.filter = event;
    this.fetchInterviews();
  }

  pageChangeEvent(event: AwPaginationModel) {
    this.interviewListCriteria.defaultCriteria.pageIndex = event?.pageIndex;
    this.interviewListCriteria.defaultCriteria.pageSize = event?.pageSize;
    this.fetchInterviews();
  }

  sortColumnTable(sort: Sort): void {
    this.interviewListCriteria.defaultCriteria.sortByField = sort?.active;
    this.interviewListCriteria.defaultCriteria.sortDirection = sort?.direction;
    this.fetchInterviews();
  }

  getCandidateRowNumber(index: number): number {
    return (
      (this.interviewListCriteria.defaultCriteria.pageIndex - 1) *
        this.interviewListCriteria.defaultCriteria.pageSize +
      (index + 1)
    );
  }

  getCandidateDetailsLink(id: number): string {
    return '/admin/candidate/candidateDetail/'.concat(id.toString());
  }

  getStatusCssClass(status: any): string {
    if (status === 'Failed') {
      return 'failed';
    }
    if (status === 'Passed') {
      return 'pass';
    }
    if (status === 'In Processing') {
      return 'in-progress';
    }
    if (status === 'New Request') {
      return 'new-request';
    }
    return 'following';
  }

  getCandidateRow(candidateField: any) {
    return { row: candidateField };
  }

  async changeStatus(
    interview: Interview,
    status: InterviewTemplateModel,
  ): Promise<void> {
    if (status.name === interview.status) {
      return;
    }

    const subscription = this.interviewService
      .changeStatus(interview, status.id)
      .subscribe(() => {
        this.fetchInterviews();
        this.showSuccessMessage('Change interview status successfully.');
      });
    this.isLoadingService.add(subscription, {
      key: 'interview',
      unique: 'interview',
    });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  private alertSnackbarMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  fetchStatus(): void {
    this.statusInterviewService.getList().subscribe((data) => {
      this.statusInterview = data.contents.filter((x) => x.active);
    });
  }

  fetchInterviews(): void {
    localStorage.setItem(
      'interview-list',
      JSON.stringify(this.interviewListCriteria),
    );
    this.isLoadingService.add({ key: 'interview', unique: 'interview' });
    this.interviewService
      .getList(
        this.interviewListCriteria.defaultCriteria.pageSize,
        this.interviewListCriteria.defaultCriteria.pageIndex,
        {
          sortByField: this.interviewListCriteria.defaultCriteria?.sortByField,
          sortDirection:
            this.interviewListCriteria.defaultCriteria?.sortDirection,
          filter: this.interviewListCriteria?.filter,
          status: this.interviewListCriteria?.status,
          startDate: this.interviewListCriteria?.startDate,
          endDate: this.interviewListCriteria?.endDate,
        },
      )
      .subscribe((result) => {
        this.isLoadingService.remove({ key: 'interview' });
        this.total = result.total;
        this.dataSource = result.contents;
      });
  }

  view(interview: Interview): void {
    this.dialog.open(InterviewViewDialogComponent, {
      data: interview,
      width: '800px',
      panelClass: 'custom-confirmation-popup',
    });
  }

  edit(interview: Interview): void {
    this.router.navigateByUrl(`/admin/interview/update/${interview.id}`);
  }

  setReminder(id: number): void {
    this.router.navigateByUrl(`/admin/reminders/add/${id}/INTERVIEW`);
  }

  async modifyResult(interview: Interview): Promise<void> {
    const dialogRef = this.dialog.open(InterviewResultDialogComponent, {
      data: interview,
      width: '800px',
      disableClose: true,
      panelClass: 'custom-confirmation-popup',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result && result.changed) {
        this.fetchInterviews();
        this.showSuccessMessage('Update interview result successfully.');
      }
    });
  }

  async delete(interview: Interview): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Confirm delete',
        message: `Are you sure you want to delete ${interview?.title}?`,
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();
    if (confirmed) {
      try {
        const subscription = this.interviewService
          .softDelete(interview, confirmed)
          .subscribe(() => {
            this.showSuccessMessage('Delete interview successfully.');
          });
        this.isLoadingService.add(subscription, {
          key: 'interview',
          unique: 'interview',
        });
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        this.fetchInterviews();
      }
    }
  }

  calendarChange(event: any) {
    this.resetToFirstPaginator();
    this.interviewListCriteria.originalStartDate = event.startDate;
    this.interviewListCriteria.originalEndDate = event.endDate;
    if (event?.startDate && event?.endDate) {
      this.interviewListCriteria.startDate = this.pipe.transform(
        event.startDate,
        'dd-MM-yyyy',
        'short',
      );
      this.interviewListCriteria.endDate = this.pipe.transform(
        event.endDate,
        'dd-MM-yyyy',
        'short',
      );
      this.interviewListCriteria.option = event.option;
      this.isInitialize = false;
    } else {
      this.interviewListCriteria.startDate = '';
      this.interviewListCriteria.endDate = '';
      this.interviewListCriteria.option = 0;
      this.isInitialize = true;
    }
    this.fetchInterviews();
  }

  resetToFirstPaginator(): void {
    this.interviewListCriteria.defaultCriteria.pageIndex = 1;
    this.interviewListCriteria.defaultCriteria.pageSize = 10;
  }

  getMultipleFilters(interviewFilterStatus: string[]): string[] {
    return this.filterGroup
      .filter((status) => interviewFilterStatus.includes(status.key))
      .map((status) => status.key)
      .map((key) => key);
  }
}
