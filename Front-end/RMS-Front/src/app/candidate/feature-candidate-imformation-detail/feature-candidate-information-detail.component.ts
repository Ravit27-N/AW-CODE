import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {CandidateDetail, CandidateModel, ProfileDetailsModel, ProfileModel} from '../../core';

export interface ProfileDisplay {
  gender: string;
  dateOfBirth: string;
  telephone1: string;
  telephone2: string;
  createdBy: string;
  lastModifyBy: string;
  priority: string;
  description: string;
}
@Component({
  selector: 'app-feature-candidate-imformation-detail',
  templateUrl: './feature-candidate-information-detail.component.html',
  styleUrls: ['./feature-candidate-information-detail.component.scss'],
})
export class FeatureCandidateInformationDetailComponent implements OnChanges {
  statusField = ['Failed', 'Passed', 'In Progress', 'New Request'];

  profileInformation: ProfileDisplay = null;
  telephone1 = '--';
  telephone2 = '--';
  telephones = [];

  @Input() profile: ProfileModel;
  @Input() candidateDetail: CandidateModel;
  constructor() {}
  ngOnChanges(changes: SimpleChanges) {
    if (changes) {
      this.telephones = this.candidateDetail?.telephones;
      this.telephone1 = this.telephones?.[0] !== undefined ? this.telephones?.[0] : '--';
      this.telephone2 = this.telephones?.[1] !== undefined  ? this.telephones?.[1]  : '--';
    }
  }

  getStatusCssClass(status: any): string {
    if (status === this.statusField[0]) {
      return 'failed';
    }
    if (status === this.statusField[1]) {
      return 'pass';
    }
    if (status === this.statusField[2]) {
      return 'in-progress';
    }
    if (status === this.statusField[3]) {
      return 'new-request';
    }
    return 'following';
  }

  sortName(profile: ProfileModel): string {
    return profile?.firstName
      .charAt(0)
      .concat(profile?.lastName.charAt(0))
      .toUpperCase();
  }
}
