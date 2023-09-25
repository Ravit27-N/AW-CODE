import { Component, Input } from '@angular/core';
import { ProfileModel } from '../../../core';

@Component({
  selector: 'app-aw-profile',
  templateUrl: './aw-profile.component.html',
  styleUrls: ['./aw-profile.component.scss'],
})
export class AwProfileComponent {
  @Input() profile: ProfileModel;
  @Input() disabledShadow: boolean;
  statusField = ['Failed', 'Passed', 'In Progress', 'New Request'];

  sortName(profile: ProfileModel): string {
    return profile?.firstName
      .charAt(0)
      .concat(profile?.lastName.charAt(0))
      .toUpperCase();
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
}
