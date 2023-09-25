import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {
  ActivityModel,
  CandidateFormModel,
  CandidateService,
  ProfileModel,
} from '../../core';

@Component({
  selector: 'app-feature-activity-detail',
  templateUrl: './feature-activity-detail.component.html',
  styleUrls: ['./feature-activity-detail.component.scss'],
})
export class FeatureActivityDetailComponent implements OnInit {
  candidateDetails: CandidateFormModel;
  profile: ProfileModel;

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public data: ActivityModel,
    public dialogRef: MatDialogRef<FeatureActivityDetailComponent>,
    private candidateService: CandidateService,
  ) {}

  ngOnInit(): void {
    this.fetchCandidateDetails();
  }

  fetchCandidateDetails(): void {
    this.candidateService
      .getById(this.data?.candidate?.id)
      .subscribe((result) => {
        this.candidateDetails = result;
        this.profile = {
          firstName: this.candidateDetails.firstname,
          lastName: this.candidateDetails.lastname,
          fullName: this.candidateDetails.firstname
            .concat(' ')
            .concat(this.candidateDetails.lastname),
          imageURL: this.candidateDetails.photoUrl,
          email: this.candidateDetails.email,
          status: this.candidateDetails.candidateStatus.title,
          details: [],
        };
      });
  }

  getTitleCss(title: string): string {
    if (title.endsWith('Passed')) {
      return 'pass';
    }
    if (title.endsWith('Canceled') || title.endsWith('Failed')) {
      return 'failed';
    }
    if (title.endsWith('In Progress')) {
      return 'in-progress';
    }
    if (title.endsWith('New Request')) {
      return 'new-request';
    }
    if (title.endsWith('Following Up')) {
      return 'following';
    }
  }
}
