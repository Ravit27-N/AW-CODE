import { CandidateDetail } from '../../core';
import {
  checkFileType,
  previewPdfFile,
  downloadDocumentFile,
  getFileIcon,
} from '../../core';
import { RemoveFileDialogComponent } from '../remove-file-dialog';
import { G_USER_ICON, USER_ICON } from '../../core';
import { EditActivityComponent } from '../../activities';
import { MatDialog } from '@angular/material/dialog';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { StatusCandidateModel } from '../../core';
import { Component, OnInit } from '@angular/core';
import { CandidateService, InterviewService } from 'src/app/core';
import { ComfirmDailogComponent, getAge } from 'src/app/shared';
import { MessageService } from 'src/app/core/service/message.service';
import { InterviewResultDialogComponent } from 'src/app/interview/dialog.component';

@Component({
  selector: 'app-candidate-detail',
  templateUrl: './candidate-detail.component.html',
  styleUrls: ['./candidate-detail.component.css'],
})
export class CandidateDetailComponent implements OnInit {
  candidate: CandidateDetail;
  statuses: StatusCandidateModel[];
  defaultProfile = USER_ICON;
  profile: any;
  dataProfile: any;
  bIcon = USER_ICON;
  gIcon = G_USER_ICON;
  filenames: string[] = [];
  slideConfig1 = {
    infinite: true,
    arrows: true,
    slidesToShow: 3,
    slidesToScroll: 3,
    autoplay: false,
  };
  slideConfig2 = {
    infinite: true,
    arrows: true,
    slidesToShow: 3,
    slidesToScroll: 3,
    autoplay: false,
  };
  constructor(
    private activateRoute: ActivatedRoute,
    private router: Router,
    private pageHistory: Location,
    private dialog: MatDialog,
    private candidateService: CandidateService,
    private interviewService: InterviewService,
    private messageService: MessageService,
  ) {
    this.candidate = {
      id: null,
      salutation: '',
      firstname: '',
      lastname: '',
      fullName: '',
      gender: '',
      email: '',
      telephone: '',
      photoUrl: '',
      gpa: null,
      priority: '',
      active: null,
      filesCV: null,
      statusId: null,
      countInterview: null,
      countReminder: null,
      candidateStatus: null,
      interviews: null,
      activities: null,
      arrUniversities: null,
      createdAt: null,
      updatedAt: null,
      createdBy: '',
      dateOfBirth: '',
      yearOfExperience: '',
    };
  }

  ngOnInit(): void {
    this.activateRoute.data.subscribe((data) => {
      this.candidate.id = data?.data?.id;
      this.candidate.salutation = data?.data?.salutation;
      this.candidate.fullName = data?.data?.fullName;
      this.candidate.gender = data?.data?.gender;
      this.candidate.email = data?.data?.email;
      this.candidate.telephone = data?.data?.telephone;
      this.candidate.photoUrl = data?.data?.photoUrl;
      this.candidate.gpa = data?.data?.gpa;
      this.candidate.priority = data?.data?.priority;
      this.candidate.active = data?.data?.active;
      this.candidate.description = data?.data?.description;
      this.candidate.statusId = data?.data?.statusId;
      this.candidate.countInterview = data?.data?.countInterview;
      this.candidate.countReminder = data?.data?.countReminder;
      this.candidate.candidateStatus = data?.data?.candidateStatus;
      this.candidate.interviews = data?.data?.interviews;
      this.candidate.activities = data?.data?.activities;
      this.candidate.arrUniversities = data?.data?.universities
        .map((x) => x.name)
        .join(', ');
      this.candidate.createdAt = data?.data?.createdAt;
      this.candidate.updatedAt = data?.data?.updatedAt;
      this.candidate.createdBy = data?.data?.createdBy;
      this.candidate.dateOfBirth = data?.data?.dateOfBirth;
      this.candidate.yearOfExperience = data?.data?.yearOfExperience;
    });
  }

  refreshData(): void {
    this.candidateService
      .getCandidateDetail(this.candidate.id)
      .subscribe((respone) => {
        this.candidate = respone;
      });
  }

  gotoInterviewDetail(id: number): void {
    this.router.navigate(['/admin/interview/view/', id]);
  }

  setReminder(id: number): void {
    this.router.navigate(['/admin/reminders/add', id, 'SPECIAL']);
  }

  setInterview(id: number | string): void {
    this.router.navigate(['/admin/interview/create', id]);
  }

  addActivity(id: number | string): void {
    this.router.navigate(['/admin/activities/add', id]);
  }

  edit(id: string | number): void {
    this.router.navigate(['/admin/candidate/editCandidate', id]);
  }

  back(): void {
    this.pageHistory.back();
  }

  getResultName(interview: any) {
    return interview.result.average ? 'Result' : 'Add result';
  }

  setLastResult(interview: any, event: any) {
    event?.stopPropagation();

    this.interviewService.getById(interview.id).subscribe((interviewData) => {
      interviewData.candidate.photoUrl = this.candidate.photoUrl;
      const dailogRef = this.dialog.open(InterviewResultDialogComponent, {
        data: interviewData,
        width: '800px',
        disableClose: true,
        panelClass: 'overlay-scrollable',
      });

      dailogRef.afterClosed().subscribe((result) => {
        if (result && result.changed) {
          this.refreshData();
          this.messageService.showSuccess('Success', 'Update interview result');
        }
      });
    });
  }

  editActivity(id: number): void {
    const dialogRef = this.dialog.open(EditActivityComponent, {
      maxWidth: '100vw',
      disableClose: true,
      width: '800px',
      data: {
        id,
      },
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.refreshData();
    });
  }

  getUrl(id: number, photoUrl: any): any {
    return `/candidate/${id}/view/${photoUrl}`;
  }

  removeCandidateFile(candidateId: number | string, filename: string): void {
    const dialogRef = this.dialog.open(RemoveFileDialogComponent, {
      data: {
        candidateId,
        filename,
      },
      width: '500px',
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe();
  }

  previewCandidateFile(candidateId: number | string, filename: string): void {
    this.candidateService
      .previewCandidateFile(candidateId, filename)
      .subscribe((respone) => {
        if (checkFileType(respone) === 'pdf') {
          previewPdfFile(respone);
        } else {
          downloadDocumentFile(respone, filename);
        }
      });
  }

  getAge(date: string) {
    return getAge(date);
  }

  getFileIcon(filename: string): any {
    return getFileIcon(filename);
  }

  requestToRemoveInterview(event: any, interview) {
    event.stopPropagation();

    this.dialog
      .open(ComfirmDailogComponent, {
        data: { title: interview.title },
        width: '550px',
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.interviewService.softDelete(interview, result).subscribe(() => {
            this.messageService.showSuccess('Success', 'Delete interview');
            this.refreshData();
          });
        }
      });
  }
}
