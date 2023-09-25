import { Component, Input } from '@angular/core';
import { ProfileDetailsModel } from '../../core';

@Component({
  selector: 'app-feature-candidate-profile-detail',
  templateUrl: './feature-candidate-profile-detail.component.html',
  styleUrls: ['./feature-candidate-profile-detail.component.scss'],
})
export class FeatureCandidateProfileDetailComponent {
  @Input() details: Array<ProfileDetailsModel>;
}
