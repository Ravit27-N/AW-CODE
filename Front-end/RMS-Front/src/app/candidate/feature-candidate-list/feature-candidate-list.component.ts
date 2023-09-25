import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  CandidateCriteria,
  CandidateModel,
  CandidateService,
  DefaultCriteria,
  InterviewService,
  MessageService,
  StatusCandidateModel,
  StatusCandidateService,
} from '../../core';
import { IsLoadingService } from '@service-work/is-loading';
import { Subscription, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { MatSelectChange } from '@angular/material/select';
import { catchError } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { Sort } from '@angular/material/sort';
import {
  AwConfirmMessageService,
  AwPaginationModel,
  AwSnackbarService,
  getAge,
  getAssetPrefix,
} from '../../shared';
import { InterviewResultDialogComponent } from '../../interview/dialog.component';
import { KeyValue } from '@angular/common';
import { UrlUtil } from '../../shared/utils/url.util';

@Component({
  selector: 'app-feature-candidate-list',
  templateUrl: './feature-candidate-list.component.html',
  styleUrls: ['./feature-candidate-list.component.scss'],
})
export class FeatureCandidateListComponent implements OnInit, OnDestroy {
  tableColumnHeader: Array<string> = [
    'no',
    'photo',
    'name',
    'age',
    'phone',
    'university',
    'gpa',
    'experience',
    'priority',
    'status',
    'interview',
    'created',
    'action',
  ];
  statusField = ['Failed', 'Passed', 'In Progress', 'New Request'];
  subscription = new Subscription();
  dataSource: Array<CandidateModel> = [];
  statusCandidates: StatusCandidateModel[] = [];
  candidates: CandidateModel[];
  total: number;
  defaultCriteria: DefaultCriteria = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: 'createdAt',
    sortDirection: 'desc',
  };
  isArchive = false;

  candidateListCriteria: CandidateCriteria = {
    defaultCriteria: { ...this.defaultCriteria },
    filter: '',
    filterReminderOrInterview: '',
    status: '',
    isDeleted: false,
  };

  filterStatuses: Array<KeyValue<string, string>> = [
    { key: '', value: 'All' },
    { key: 'failed', value: 'Failed' },
    { key: 'followingUp', value: 'Following Up' },
    { key: 'inProgress', value: 'In Progress' },
    { key: 'newRequest', value: 'New Request' },
    { key: 'passed', value: 'Passed' },
  ];

  filterGroup: Array<KeyValue<string, string>> = [
    { key: 'interview', value: 'Interview' },
    { key: 'reminder', value: 'Reminder' },
  ];
  iconPrefix = getAssetPrefix();

  constructor(
    public candidateService: CandidateService,
    private statusCandidateService: StatusCandidateService,
    private isLoadingService: IsLoadingService,
    private router: Router,
    private dialog: MatDialog,
    private message: MessageService,
    private awConfirmMessageService: AwConfirmMessageService,
    private awSnackbarService: AwSnackbarService,
    private interviewService: InterviewService,
  ) {}

  ngOnInit(): void {
    // Clean candidate form history.
    localStorage.removeItem('candidate-form');
    localStorage.removeItem('candidate-form-step-1');
    localStorage.removeItem('candidate-form-step-2');
    localStorage.removeItem('candidate-form-step-3');
    localStorage.removeItem('candidate-form-step-4');
    localStorage.removeItem('candidate-form-has-change');
    localStorage.removeItem('submit-form-candidate');
    localStorage.removeItem('currentStep');

    if (localStorage.getItem('candidate-list')) {
      this.candidateListCriteria = JSON.parse(
        localStorage.getItem('candidate-list'),
      );
    }

    const status = UrlUtil.getParamsByKey('status') || '';
    const gender = UrlUtil.getParamsByKey('gender') || '';
    if (status && gender && !this.candidateListCriteria.status) {
      this.candidateListCriteria = {
        ...this.candidateListCriteria,
        status,
        filter: gender,
      };
      localStorage.setItem(
        'candidate-list',
        JSON.stringify(this.candidateListCriteria),
      );
    }
    if (status && !this.candidateListCriteria.status) {
      this.candidateListCriteria = {
        ...this.candidateListCriteria,
        status,
      };
      localStorage.setItem(
        'candidate-list',
        JSON.stringify(this.candidateListCriteria),
      );
    }

    this.fetchCandidates();
    this.loadStatusCandidate();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  fetchCandidates(): void {
    localStorage.setItem(
      'candidate-list',
      JSON.stringify(this.candidateListCriteria),
    );

    this.subscription = this.candidateService
      .getCandidates(this.candidateListCriteria)
      .subscribe((response) => {
        this.candidates = response.contents;
        this.candidates.map((item) => {
          item.arrUniversities = item.universities
            ?.map((university) => university.name)
            ?.join(', ');
        });
        this.total = response.total;
        this.dataSource = this.candidates;
      });
    this.isLoadingService.add(this.subscription, {
      key: 'candidate',
      unique: 'candidate',
    });
  }

  loadStatusCandidate(): void {
    this.statusCandidateService
      .getList(1, 100, '', 'title', 'asc')
      .subscribe((response) => {
        this.statusCandidates = response.contents.filter(
          (x) => x.active && !x.deleted,
        );
      });
  }

  fetchArchive(): void {
    this.isArchive = !this.isArchive;
    this.candidateListCriteria.isDeleted = this.isArchive;
    this.candidateListCriteria.defaultCriteria.pageIndex = 1;
  }

  addCandidate(): void {
    this.router.navigateByUrl('/admin/candidate/add');
  }

  searchCandidate(event: string): void {
    this.resetPagination();
    this.candidateListCriteria.filter = event;
    this.fetchCandidates();
  }

  filterChange(criteria: any) {
    this.resetPagination();
    this.candidateListCriteria.status =
      criteria.status === 'All' ? '' : criteria.status;
    this.candidateListCriteria.filterReminderOrInterview = criteria.filter;
    this.fetchCandidates();
  }

  resetPagination(): void {
    this.candidateListCriteria.defaultCriteria.pageIndex = 1;
    this.candidateListCriteria.defaultCriteria.pageSize = 10;
  }

  getAge(date: string) {
    return getAge(date);
  }

  getCandidateDetailsLink(id: number): string {
    return '/admin/candidate/candidateDetail/'.concat(id.toString());
  }

  sortColumnTable(sort: Sort): void {
    this.candidateListCriteria.defaultCriteria.sortDirection = sort.direction;
    switch (sort.active) {
      case this.tableColumnHeader[2]:
        this.candidateListCriteria.defaultCriteria.sortByField = 'firstname';
        break;
      case this.tableColumnHeader[4]:
        this.candidateListCriteria.defaultCriteria.sortByField = 'telephone';
        break;
      case this.tableColumnHeader[6]:
        this.candidateListCriteria.defaultCriteria.sortByField = 'gpa';
        break;
      case this.tableColumnHeader[8]:
        this.candidateListCriteria.defaultCriteria.sortByField = 'priority';
        break;
      default:
        this.candidateListCriteria.defaultCriteria.sortByField = 'createdAt';
        break;
    }
    if (
      this.candidateListCriteria.defaultCriteria.sortDirection === undefined
    ) {
      this.candidateListCriteria.defaultCriteria.sortDirection = 'asc';
    }
    this.fetchCandidates();
  }

  getCandidateRowNumber(index: number): number {
    return (
      (this.candidateListCriteria.defaultCriteria.pageIndex - 1) *
        this.candidateListCriteria.defaultCriteria.pageSize +
      (index + 1)
    );
  }

  editCandidate(id: number): void {
    this.navigateByUrlAction(id, 'editCandidate');
  }

  async changeStatusConfirmDialog(
    event: MatSelectChange,
    candidateField: CandidateModel,
  ): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Change Status',
        message: `Do you want to change Candidate's status?`,
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();
    if (confirmed) {
      try {
        await this.candidateService
          .updateStatusCandidate(candidateField.id, event.value)
          .pipe(
            catchError((err) => {
              event.source.writeValue(candidateField.candidateStatus.id);
              return throwError(err);
            }),
          )
          .subscribe(() => {
            this.showSuccessMessage(
              'Candidate status was update successfully.',
            );
          });
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchCandidates();
      }
    } else {
      event.source.writeValue(candidateField.candidateStatus.id);
    }
  }

  async deleteDemandPermanent(id: number, fullName: string): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Remove demand?',
        message: `Are you sure to permanently delete this candidate?
        If Okay, this will also permanently delete records that are related to this candidate.
        Full Name: ${fullName}`,
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        await this.candidateService.hardDelete(id).toPromise();
        this.showSuccessMessage('The demands has been delete successfully.');
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchCandidates();
      }
    }
  }

  async restoreFromArchive(id: number, fullName: string): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Restore Candidate',
        message: `Are you sure to restore this candidate?
If Okay, this will also restore records that are related to this candidate.
Full Name: ${fullName}`,
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        await this.candidateService.restore(id, false).toPromise();
        this.showSuccessMessage('The demands has been restored successfully.');
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchCandidates();
      }
    }
  }

  async addToArchive(id: number, fullName: string): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Restore Candidate',
        message: `Are you sure to soft delete this candidate?
If Okay, this will also soft delete records that are related to this candidate.
Full Name: ${fullName}`,
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();
    if (confirmed) {
      try {
        await this.candidateService.delete(id, true).toPromise();
        this.showSuccessMessage('The demands has been restored successfully.');
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchCandidates();
      }
    }
  }

  viewCandidate(id: number): void {
    this.navigateByUrlAction(id);
  }

  setCandidateReminder(id: number): void {
    this.navigateByUrlAction(id, 'reminders');
  }

  setCandidateInterview(id: number): void {
    this.navigateByUrlAction(id, 'interview');
  }

  addCandidateActivityLog(id: number): void {
    this.navigateByUrlAction(id, 'activities');
  }

  navigateByUrlAction(id: number, action: string = ''): void {
    switch (action) {
      case 'activities':
        this.router.navigateByUrl(`/admin/${action}/add/${id}`);
        break;
      case 'interview':
        this.router.navigate([`/admin/${action}/candidate`], {
          queryParams: { candidateId: id },
        });
        break;
      case 'reminders':
        this.router.navigateByUrl(`/admin/${action}/add/${id}/SPECIAL`);
        break;
      case 'editCandidate':
        this.router.navigateByUrl(`/admin/candidate/${action}/${id}`);
        break;
      default:
        this.router.navigateByUrl(this.getCandidateDetailsLink(id));
    }
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  setInterviewResult(candidate: CandidateModel) {
    this.interviewService
      .getById(candidate.interviews.id)
      .subscribe((interview) => {
        interview.candidate.photoUrl = candidate.photoUrl;
        const dailogRef = this.dialog.open(InterviewResultDialogComponent, {
          data: interview,
          width: '800px',
          disableClose: true,
          panelClass: 'overlay-scrollable',
        });

        dailogRef.afterClosed().subscribe((result) => {
          if (result && result.changed) {
            this.fetchCandidates();
            this.message.showSuccess('Success', 'Update interview result');
          }
        });
      });
  }

  checkInterViewResult(candidate: any): boolean {
    return (
      candidate?.interviews &&
      candidate?.interviews.result &&
      candidate?.interviews.result.average
    );
  }

  private alertSnackbarMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  getCandidateRow(candidateField: any) {
    return { row: candidateField };
  }

  getStatusCssClass(candidateStatus: any): string {
    if (candidateStatus.title === this.statusField[0]) {
      return 'failed';
    }
    if (candidateStatus.title === this.statusField[1]) {
      return 'pass';
    }
    if (candidateStatus.title === this.statusField[2]) {
      return 'in-progress';
    }
    if (candidateStatus.title === this.statusField[3]) {
      return 'new-request';
    }
    return 'following';
  }

  getSelectCssClass(candidateStatus: any): string {
    if (candidateStatus.title === this.statusField[0]) {
      return 'mat-select-failed';
    }
    if (candidateStatus.title === this.statusField[1]) {
      return 'mat-select-pass';
    }
    if (candidateStatus.title === this.statusField[2]) {
      return 'mat-select-in-progress';
    }
    if (candidateStatus.title === this.statusField[3]) {
      return 'mat-select-new-request';
    }
    return 'mat-select-following';
  }

  getUrl(row: CandidateModel) {
    return this.candidateService.getFileURL(row.id, row.photoUrl);
  }

  pageChangeEvent(event: AwPaginationModel) {
    this.candidateListCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.candidateListCriteria.defaultCriteria.pageSize = event.pageSize;
    this.fetchCandidates();
  }

  sortName(firstName: string, lastName: string) {
    return firstName.charAt(0).concat(lastName.charAt(0));
  }

  getStatusFilter(candidateFilterStatus: string): string {
    return this.filterStatuses
      .filter((status) => status.value === candidateFilterStatus)
      .map((status) => status.key)
      .map((key) => key)
      .toString();
  }

  getMultipleFilters(candidateFilterStatus: string): string[] {
    return this.filterGroup
      .filter((status) => candidateFilterStatus.split(',').includes(status.key))
      .map((status) => status.key)
      .map((key) => key);
  }
}
