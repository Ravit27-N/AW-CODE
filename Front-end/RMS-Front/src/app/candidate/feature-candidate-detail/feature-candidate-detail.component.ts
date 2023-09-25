import { Component, OnInit } from '@angular/core';
import {
  CandidateModel,
  CandidateService,
  InterviewsCandidateModel,
  InterviewService,
  NavigationItemModel,
  ProfileModel,
  ResultCandidateModel,
} from '../../core';
import { ActivatedRoute, Router } from '@angular/router';
import { InterviewResultDialogComponent } from '../../interview/dialog.component';
import { MatDialog } from '@angular/material/dialog';
import {
  AwConfirmMessageService,
  AwSnackbarService,
  getAssetPrefix,
} from '../../shared';
import { EditActivityComponent } from '../../activities';

export interface CandidateExperiences {
  companyName: string;
  position: string;
  startDate: Date;
  endDate: Date;
  level: string;
  projectType: string;
  technology: string;
  remarks: string;
}

export interface CandidateUniversities {
  id: number;
  universityId: number;
  university: {
    id: number;
    name: string;
    address: string;
    createdAt: Date;
    updatedAt: Date;
  };
  degree: string;
  major: string;
  startDate: Date;
  endDate: Date;
  graduate: boolean;
  gpa: number;
  remarks: string;
};

@Component({
  selector: 'app-feature-candidate-detail',
  templateUrl: './feature-candidate-detail.component.html',
  styleUrls: ['./feature-candidate-detail.component.scss'],
})
export class FeatureCandidateDetailComponent implements OnInit {
  candidateModel: CandidateModel;
  profile: ProfileModel;
  navigation: Array<NavigationItemModel> = [];
  interviews: Array<InterviewsCandidateModel> = [];
  listInterviewsStatus: Array<string>;
  iconPrefix = getAssetPrefix();
  candidateExperience: CandidateExperiences[] = [];
  candidateUniversities: CandidateUniversities[] = [];
  constructor(
    private candidateService: CandidateService,
    private route: ActivatedRoute,
    private router: Router,
    private interviewService: InterviewService,
    private awSnackbarService: AwSnackbarService,
    private dialog: MatDialog,
    private awConfirmMessageService: AwConfirmMessageService,
  ) {}

  ngOnInit(): void {
    const id =
      this.route.snapshot.paramMap.get('id') !== null
        ? this.route.snapshot.paramMap.get('id')
        : '';
    this.fetchCandidateDetails(id);
  }

  fetchCandidateDetails(id: string | number): void {
    this.candidateService.getCandidateDetailById(id).subscribe((result) => {
      const universities = result?.universities as any;
      this.candidateModel = {
        ...result,
        arrUniversities: universities?.json_agg
          .map((item: any) => item.name)
          .join(', '),
      };
      this.setInterviews(this.candidateModel);
      this.setListInterviewsStatus(this.candidateModel);
      this.setProfile(this.candidateModel);
      this.setNavigation(this.candidateModel?.id);
    });
  }

  scoreResults(item: ResultCandidateModel): string {
    return (
      item.average?.toString() +
      '(Q&A: '
        .concat(item.quizScore?.toString())
        .concat(', Coding: ')
        .concat(item.codingScore?.toString())
        .concat(')')
    );
  }

  setListInterviewsStatus(candidateModel: CandidateModel) {
    this.interviews = candidateModel?.interviews as any;
    const status = [];
    this.interviews?.forEach((interview) => {
      this.interviewService.getById(interview?.id).subscribe((result) => {
        status.push(result.status);
      });
    });
    this.listInterviewsStatus = status;
  }

  setInterviews(candidateModel: CandidateModel): void {
    this.interviews = candidateModel?.interviews as any;
    this.interviews?.forEach((interview) => {
      interview.result.score = this.scoreResults(interview?.result) as any;
    });
  }

  back(): void {
    this.router.navigateByUrl('admin/candidate');
  }

  setNavigation(id: string | number): void {
    this.navigation = [
      {
        icon: this.iconPrefix.concat('assets/icons/activity.svg'),
        title: 'Add activity',
        link: `/admin/activities/add/${id}`,
        function: () => {},
      },
      {
        icon: this.iconPrefix.concat('assets/icons/interview.svg'),
        title: 'Set interview',
        link: `/admin/interview/candidate`,
        queryParams: { candidateId: id },
        function: () => {},
      },
      {
        icon: this.iconPrefix.concat('assets/icons/reminder.svg'),
        title: 'Set reminder',
        link: `/admin/reminders/add/${id}/SPECIAL`,
        function: () => {},
      },
      {
        icon: this.iconPrefix.concat('assets/icons/editing.svg'),
        title: 'Edit',
        link: `/admin/candidate/editCandidate/${id}`,
        function: () => {},
      },
      {
        icon: this.iconPrefix.concat('assets/icons/result.svg'),
        title: 'Interview result',
        link: '',
        function: () => {
          const lastIndex = this.interviews.length - 1;
          this.editInterviewResult(this.interviews[lastIndex]);
        },
      },
    ];
  }

  editInterviewResult(interview: InterviewsCandidateModel) {
    this.interviewService.getById(interview?.id).subscribe((interviewData) => {
      interviewData.candidate.photoUrl = this.candidateModel.photoUrl;
      const dialogRef = this.dialog.open(InterviewResultDialogComponent, {
        data: interviewData,
        width: '800px',
        height: '900px',
        disableClose: true,
        panelClass: 'custom-confirmation-popup',
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (result && result?.changed) {
          this.fetchCandidateData();
          this.showSuccessMessage('Success update interview result');
        }
      });
    });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  fetchCandidateData(): void {
    this.candidateService
      .getCandidateDetails(this.candidateModel.id)
      .subscribe((response) => {
        this.candidateModel = response;
        this.setInterviews(this.candidateModel);
      });
  }

  editInterview(event: any): void {
    this.editInterviewResult(event);
  }

  async removeInterview(event: any): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Delete Interview',
        message: `Are you sure you want to delete ${event.title}`,
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();
    if (confirmed) {
      try {
        await this.interviewService.softDelete(event, confirmed).toPromise();
        this.showSuccessMessage('Success delete interview');
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        this.fetchCandidateData();
      }
    }
  }

  private alertSnackbarMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  gotoInterviewDetail(id: number): void {
    this.router.navigate(['/admin/interview/update/', id]);
  }

  editActivity(id: number): void {
    const dialogRef = this.dialog.open(EditActivityComponent, {
      maxWidth: '100vw',
      disableClose: true,
      width: '800px',
      data: {
        id,
      },
      panelClass: 'custom-confirmation-popup',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.fetchCandidateData();
    });
  }

  setProfile(candidateDetail: CandidateModel): void {

    this.candidateExperience = candidateDetail?.candidateExperiences;
    this.candidateUniversities = candidateDetail?.candidateUniversities;
    this.profile = {
      imageURL: candidateDetail?.photoUrl,
      firstName: candidateDetail?.firstname,
      lastName: candidateDetail?.lastname,
      fullName: candidateDetail.fullName,
      email: candidateDetail?.email,
      gender: candidateDetail?.gender,
      createdBy: candidateDetail.createdBy,
      createdAt: candidateDetail.createdAt,
      status: candidateDetail?.candidateStatus?.title,
      details: [
        {
          title: 'Gender',
          value: candidateDetail?.gender,
        },
        {
          title: 'University',
          value: candidateDetail?.arrUniversities,
        },
        {
          title: 'Created By',
          value: candidateDetail?.createdBy,
        },
        {
          title: 'Date of birth',
          value: candidateDetail?.dateOfBirth,
        },
        {
          title: 'GPA',
          value: candidateDetail?.gpa,
        },
        {
          title: 'Last Modify',
          value: candidateDetail?.updatedAt,
        },
        {
          title: 'Telephone',
          value: candidateDetail?.telephone,
        },
        {
          title: 'Year of experience',
          value: candidateDetail?.yearOfExperience,
        },
        {
          title: 'Created At',
          value: candidateDetail?.createdAt,
        },
        {
          title: 'Priority',
          value: candidateDetail?.priority,
        },
        {
          title: 'Description',
          value: candidateDetail?.description,
        },
      ],
    };
  }
}
