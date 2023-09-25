import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { ActivitiesCandidateModel } from '../../core';

@Component({
  selector: 'app-feature-candidate-activity',
  templateUrl: './feature-candidate-activity.component.html',
  styleUrls: ['./feature-candidate-activity.component.scss'],
})
export class FeatureCandidateActivityComponent implements OnChanges {
  @Input() activities: Array<ActivitiesCandidateModel>;
  @Output() editActivity: EventEmitter<number> = new EventEmitter();
  title: string;
  statusField = ['Failed', 'Passed', 'In Progress', 'New Request'];

  ngOnChanges(simpleChanges: SimpleChanges): void {
    if (simpleChanges.activities) {
      this.title = this.getTitle();
    }
  }

  getTitle(): string {
    return this.activities?.length > 1
      ? 'Activities'
          .concat(' ')
          .concat('('.concat(this.activities?.length.toString()).concat(')'))
      : 'Activity'
          .concat(' ')
          .concat('('.concat(this.activities?.length.toString()).concat(')'));
  }

  getActivityTitleCssClass(title: string): string {
    if (title.endsWith(this.statusField[0])) {
      return 'failed';
    }
    if (title.endsWith(this.statusField[1])) {
      return 'pass';
    }
    if (title.endsWith(this.statusField[2])) {
      return 'in-progress';
    }
    if (title.endsWith(this.statusField[3])) {
      return 'new-request';
    }
    return 'following';
  }

  getActivityBorderCssClass(title: string): string {
    if (title.endsWith(this.statusField[1])) {
      return 'pass-border-left';
    }
    if (title.endsWith(this.statusField[0])) {
      return 'failed-border-left';
    }
    if (title.endsWith(this.statusField[2])) {
      return 'in-progress-border-left';
    }
    if (title.endsWith(this.statusField[3])) {
      return 'new-request-border-left';
    }
    return 'following-border-left';
  }
}
