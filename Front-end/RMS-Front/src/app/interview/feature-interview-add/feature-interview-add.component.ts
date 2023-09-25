import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import {
  CandidateModel,
  CandidateService,
  EmployeeModel,
  Interview,
  InterviewService,
  InterviewTemplateModel,
  InterviewTemplateService,
  JobDescription,
  JobDescriptionService,
  JobModel,
} from '../../core';
import { MatDialog } from '@angular/material/dialog';
import {
  AwConfirmMessageService,
  AwSnackbarService,
  formatDate24H,
} from '../../shared';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { distinctUntilChanged, skip } from 'rxjs/operators';
import { FeatureAddJobDescriptionPopupComponent } from '../../setting/project';

@Component({
  selector: 'app-feature-interview-add',
  templateUrl: './feature-interview-add.component.html',
  styleUrls: ['./feature-interview-add.component.scss'],
})
export class FeatureInterviewAddComponent implements OnInit, OnDestroy {
  @Input() isEditableMode: boolean;
  @Input() interview: Interview;
  formGroup: FormGroup;
  candidateFilter = new FormControl('');
  titleDescription = new FormControl('');
  candidates: Array<CandidateModel> = [];
  interviewTemplates: Array<InterviewTemplateModel> = [];
  titleDescriptions: Array<string> = [];
  interviewers = [];
  subscription = new Subscription();
  pageTitle: string;
  pageSubtitle: string;
  candidateId: string;

  constructor(
    private formBuilder: FormBuilder,
    private candidateService: CandidateService,
    private interviewService: InterviewService,
    private interviewTemplateService: InterviewTemplateService,
    private jobDescriptionService: JobDescriptionService,
    public dialog: MatDialog,
    private awSnackbarService: AwSnackbarService,
    private activateRoute: ActivatedRoute,
    private awConfirmMessageService: AwConfirmMessageService,
  ) {
    this.formGroup = this.formBuilder.group({});
  }

  async ngOnInit(): Promise<void> {
    this.setCandidateId();
    this.pageTitle = this.isEditableMode
      ? 'Manage Update Interview'
      : 'Manage Create Interview';
    this.pageSubtitle = this.isEditableMode
      ? 'Update interview'
      : 'Create interview';
    this.fetchCandidates();
    this.fetchInterviewTemplates();
    this.fetchJobTitles();
    this.formCreateInterview();
    this.getInterviews();
    if (this.isEditableMode) {
      this.setInterviewUpdateForm();
    }
    this.searchCandidate();
    this.searchJobTitle();
    this.formChange();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  setCandidateId(): void {
    const { candidateId } = this.activateRoute.snapshot.queryParams;
    this.candidateId = candidateId;
  }

  formChange(): void {
    const formChangeSubscription = this.formGroup.valueChanges
      .pipe(distinctUntilChanged())
      .pipe(skip(1))
      .subscribe((result) => {
        localStorage.setItem('form-interview', JSON.stringify(result));
      });
    this.subscription.add(formChangeSubscription);
  }

  setInterviewUpdateForm(): void {
    const localData = JSON.parse(localStorage.getItem('form-interview'));
    const { status, title, description } = localData || this.interview;
    this.formGroup.patchValue({
      ...{ status, title, description },
      dateTime: localData
        ? new Date(localData.dateTime)
        : new Date(this.interview.dateTime),
      candidateId:
        localData?.candidateId || this.interview.candidate.id.toString(),
    });
  }

  formCreateInterview(): void {
    const localData = JSON.parse(localStorage.getItem('form-interview'));
    this.formGroup.addControl(
      'candidateId',
      new FormControl(
        this.candidateId
          ? this.candidateId.toString()
          : localData?.candidateId || '',
      ),
    );
    this.formGroup.addControl(
      'status',
      new FormControl(localData?.status || ''),
    );
    this.formGroup.addControl(
      'statusId',
      new FormControl(localData?.statusId || ''),
    );
    this.formGroup.addControl(
      'dateTime',
      new FormControl(localData ? new Date(localData.dateTime) : new Date()),
    );
    this.formGroup.addControl(
      'description',
      new FormControl(localData?.description || ''),
    );
    this.formGroup.addControl('title', new FormControl(localData?.title || ''));
    if (!this.isEditableMode) {
      this.formGroup.addControl(
        'reminderTime',
        new FormControl(localData?.reminderTime || 10),
      );
      this.formGroup.addControl(
        'sendInvite',
        new FormControl(localData?.sendInvite || false),
      );
      this.formGroup.addControl(
        'setReminder',
        new FormControl(localData?.setReminder || false),
      );
    }
  }

  fetchCandidates(): void {
    this.candidateService
      .getList(1, 0)
      .toPromise()
      .then((result) => {
        this.candidates = result.contents;
      })
      .catch(() => {
        this.showErrorMessage('Cannot fetch candidates.');
      });
  }

  fetchInterviewTemplates(): void {
    this.interviewTemplateService
      .getList(null, null, 'name', 'asc')
      .toPromise()
      .then((data: any) => {
        this.interviewTemplates = data.contents.filter(
          (result: InterviewTemplateModel) => result?.active,
        );
        if (this.isEditableMode) {
          this.setInterviews(this.interview.status);
        }
      })
      .catch(() => {
        this.showErrorMessage('Cannot fetch interview template.');
      });
  }

  fetchJobTitles(): void {
    this.jobDescriptionService
      .get(1, 0, '')
      .toPromise()
      .then(
        (data: any) =>
          (this.titleDescriptions = data.contents
            .filter((result: JobDescription) => result.active)
            .map((result: JobDescription) => result.title)),
      )
      .catch(() => {
        this.showErrorMessage('Cannot fetch job titles.');
      });
  }

  searchCandidate(): void {
    const candidateFilter = this.candidateFilter.valueChanges.subscribe(() => {
      this.candidateService
        .advanceSearch(
          {
            name: this.candidateFilter.value,
            sortDirection: 'asc',
            sortByField: 'firstname',
          },
          0,
          1,
        )
        .subscribe((data: any) => (this.candidates = data.contents));
    });
    this.subscription.add(candidateFilter);
  }

  searchJobTitle(): void {
    const searchTitleSubscription =
      this.titleDescription.valueChanges.subscribe(() => {
        this.jobDescriptionService
          .get(1, 10, this.titleDescription.value)
          .subscribe(
            (data: any) =>
              (this.titleDescriptions = data.contents
                .filter((result: JobDescription) => result.active)
                .map((result: JobDescription) => result.title)),
          );
      });
    this.subscription.add(searchTitleSubscription);
  }

  openCreateTitleDescription(): void {
    const subscription = this.dialog
      .open(FeatureAddJobDescriptionPopupComponent, {
        width: '40%',
        disableClose: true,
        panelClass: 'custom-confirmation-popup',
        data: {
          dialogMode: true,
        },
      })
      .afterClosed()
      .subscribe((result: JobModel) => {
        if (result?.active) {
          this.titleDescriptions.unshift(result.title);
          this.formGroup.get('title').setValue(result.title);
        }
      });
    this.subscription.add(subscription);
  }

  getInterviews(): void {
    const statusSubscription = this.formGroup
      .get('status')
      .valueChanges.subscribe((name) => {
        this.setInterviews(name);
      });
    this.subscription.add(statusSubscription);
  }

  setInterviews(name: string): void {
    const allInterview = this.interviewTemplates
      .filter((interviewTemplate: InterviewTemplateModel) => {
        if (interviewTemplate.name === name) {
          this.formGroup
            .get('statusId')
            .patchValue(
              { value: interviewTemplate.id.toString() },
              { emitEvent: false },
            );
          return true;
        }
      })
      .map(
        (interviewTemplate: InterviewTemplateModel) =>
          interviewTemplate.employee,
      )
      .map((employeeTemplate: Array<EmployeeModel>) =>
        employeeTemplate.map((employee) => ({
          name: employee.fullName,
          email: employee.email,
        })),
      );
    this.interviewers = allInterview.length ? allInterview[0] : [];
  }

  async save(): Promise<void> {
    if (this.validateFormData()) {
      if (!this.formGroup.get('candidateId').value) {
        this.formGroup.get('candidateId').setErrors({ incorrect: true });
        this.formGroup.get('candidateId').markAsTouched({ onlySelf: true });
      }
      if (!this.formGroup.get('status').value) {
        this.formGroup.get('status').setErrors({ incorrect: true });
        this.formGroup.get('status').markAsTouched({ onlySelf: true });
      }
      if (!this.formGroup.get('dateTime').value) {
        this.formGroup.get('dateTime').setErrors({ incorrect: true });
        this.formGroup.get('dateTime').markAsTouched({ onlySelf: true });
      }
      if (!this.formGroup.get('title').value) {
        this.formGroup.get('title').setErrors({ incorrect: true });
        this.formGroup.get('title').markAsTouched({ onlySelf: true });
      }
      return;
    } else {
      localStorage.removeItem('form-interview');
    }
    if (this.isEditableMode) {
      await this.updateInterview();
    } else {
      await this.addInterview();
    }
  }

  async updateInterview(): Promise<void> {
    try {
      const { value } = this.formGroup.get('statusId').value;
      await this.interviewService
        .update(this.interview, {
          ...this.formGroup.value,
          statusId: value,
          dateTime: formatDate24H(this.formGroup.get('dateTime').value),
        })
        .toPromise();
      this.showSuccessMessage('Update candidate successfully');
      await this.navigateToInterviewList();
    } catch (error) {
      this.showErrorMessage('Update candidate Error');
    }
  }

  async addInterview(): Promise<void> {
    try {
      const { value } = this.formGroup.get('statusId').value;
      await this.interviewService
        .create({
          ...this.formGroup.value,
          statusId: value,
          dateTime: formatDate24H(this.formGroup.get('dateTime').value),
        })
        .toPromise();
      await this.navigateToInterviewList();
      this.showSuccessMessage('Create candidate successfully');
    } catch (error) {
      this.showErrorMessage('Update candidate Error');
    }
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  private showErrorMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  validateFormData(): boolean {
    return (
      this.formGroup.invalid ||
      !this.formGroup.get('candidateId').value ||
      !this.formGroup.get('status').value ||
      !this.formGroup.get('dateTime').value ||
      !this.formGroup.get('title').value
    );
  }

  async navigateToInterviewList(): Promise<void> {
    history.back();
  }

  async back(): Promise<void> {
    history.back();
  }

  async cancel(): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Discard all changes?',
        message: 'Are you sure you want to discard all changes?',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();
    if (confirmed) {
      try {
        window.history.back();
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      }
    }
  }
}
