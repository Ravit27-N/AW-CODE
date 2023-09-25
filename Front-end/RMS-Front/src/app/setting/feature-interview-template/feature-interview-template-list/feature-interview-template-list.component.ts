import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { IsLoadingService } from '@service-work/is-loading';
import { Sort } from '@angular/material/sort';
import { AwConfirmMessageService, AwSnackbarService } from '../../../shared';
import { FeatureInterviewTemplateAddComponent } from '../feature-interview-template-add';
import { FeatureInterviewTemplateDetailComponent } from '../feature-interview-template-detail';
import {
  DefaultCriteria,
  EmployeeModel,
  ProjectCriteria,
  InterviewTemplateModel,
  InterviewTemplateService,
} from '../../../core';

@Component({
  selector: 'app-feature-interview-template-list',
  templateUrl: './feature-interview-template-list.component.html',
  styleUrls: ['./feature-interview-template-list.component.scss'],
})
export class FeatureInterviewTemplateListComponent implements OnInit {
  dataSource: Array<InterviewTemplateModel> = [];
  tableColumnHeader: string[] = [
    'no',
    'name',
    'type',
    'interviewer',
    'active',
    'action',
  ];
  total: number;
  defaultCriteria: DefaultCriteria = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: 'name',
    sortDirection: 'asc',
  };
  interviewTemplateCriteria: ProjectCriteria = {
    defaultCriteria: { ...this.defaultCriteria },
    filter: '',
  };

  constructor(
    public dialog: MatDialog,
    private interviewTemplateService: InterviewTemplateService,
    private isLoadingService: IsLoadingService,
    private awConfirmMessageService: AwConfirmMessageService,
    private awSnackbarService: AwSnackbarService,
  ) {}

  ngOnInit(): void {
    if (localStorage.getItem('interview-template')) {
      this.interviewTemplateCriteria = JSON.parse(
        localStorage.getItem('interview-template'),
      );
    }
    this.fetchInterviewTemplate();
  }

  addInterviewTemplate(): void {
    const matDialogRef = this.dialog.open(
      FeatureInterviewTemplateAddComponent,
      {
        width: '800px',
        height: '520px',
        disableClose: true,
        panelClass: 'custom-confirmation-popup',
      },
    );
    matDialogRef.afterClosed().subscribe((result) => {
      if (result?.created) {
        this.fetchInterviewTemplate();
      }
    });
  }

  pageChangeEvent(event: any): void {
    this.interviewTemplateCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.interviewTemplateCriteria.defaultCriteria.pageSize = event.pageSize;
    this.fetchInterviewTemplate();
  }

  fetchInterviewTemplate(): void {
    localStorage.setItem(
      'interview-template',
      JSON.stringify(this.interviewTemplateCriteria),
    );
    this.isLoadingService.add({
      key: 'interviewtemplate',
      unique: 'interviewtemplate',
    });
    this.interviewTemplateService
      .getList(
        this.interviewTemplateCriteria.defaultCriteria.pageIndex,
        this.interviewTemplateCriteria.defaultCriteria.pageSize,
        this.interviewTemplateCriteria.defaultCriteria.sortByField,
        this.interviewTemplateCriteria.defaultCriteria.sortDirection,
        this.interviewTemplateCriteria.filter,
      )
      .subscribe((result) => {
        this.isLoadingService.remove({ key: 'interviewtemplate' });
        this.dataSource = result?.contents;
        this.dataSource?.map((item: any, index: number) => {
          this.dataSource[index].interviewer = this.getInterviewer(
            item?.employee,
          );
          return item;
        });
        this.total = result?.total;
      });
  }

  searchValueChange(event: string) {
    this.interviewTemplateCriteria.filter = event;
    this.interviewTemplateCriteria.defaultCriteria.pageIndex = 1;
    this.interviewTemplateCriteria.defaultCriteria.pageSize = 10;
    this.fetchInterviewTemplate();
  }

  sortColumnTable(sort: Sort): void {
    this.interviewTemplateCriteria.defaultCriteria.sortByField = sort?.active;
    this.interviewTemplateCriteria.defaultCriteria.sortDirection =
      sort?.direction;
    this.fetchInterviewTemplate();
  }

  getCandidateRowNumber(index: number): number {
    return (
      (this.interviewTemplateCriteria.defaultCriteria.pageIndex - 1) *
        this.interviewTemplateCriteria.defaultCriteria.pageSize +
      (index + 1)
    );
  }

  getCandidateRow(candidateField: any) {
    return { row: candidateField };
  }

  edit(interviewTemplateModel: InterviewTemplateModel): void {
    interviewTemplateModel.isUpdated = true;
    const matDialogRef = this.dialog.open(
      FeatureInterviewTemplateAddComponent,
      {
        data: interviewTemplateModel,
        disableClose: true,
        width: '800px',
        height: '520px',
        panelClass: 'custom-confirmation-popup',
      },
    );
    matDialogRef.afterClosed().subscribe((result) => {
      if (result?.updated) {
        this.fetchInterviewTemplate();
      }
    });
  }

  async delete(interviewTemplateModel: InterviewTemplateModel): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Confirm delete',
        message: `Are you sure you want to delete ${interviewTemplateModel?.name}?`,
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();
    if (confirmed) {
      try {
        const subscription = this.interviewTemplateService
          .delete(interviewTemplateModel.id)
          .subscribe(() => {
            this.fetchInterviewTemplate();
            this.showSuccessMessage('Delete interview template successfully.');
          });
        this.isLoadingService.add(subscription, {
          key: 'interviewtemplate',
          unique: 'interviewtemplate',
        });
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      }
    }
  }

  view(interviewTemplateModel: InterviewTemplateModel): void {
    this.dialog.open(FeatureInterviewTemplateDetailComponent, {
      data: interviewTemplateModel,
      width: '650px',
      height: 'auto',
      panelClass: 'custom-confirmation-popup',
    });
  }

  async slideToggle(id: number, active: any): Promise<void> {
    this.interviewTemplateService.changeStatus(id, !active).subscribe(
      (): void => {
        this.fetchInterviewTemplate();
        this.showSuccessMessage('Update interview template successfully');
      },
      (): void => {
        this.alertSnackbarMessage('Error update interview template.');
      },
    );
  }

  private alertSnackbarMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  getInterviewer(employees: Array<EmployeeModel>): string {
    return employees
      ?.map((employee: EmployeeModel) => employee?.fullName)
      .join(', ');
  }
}
