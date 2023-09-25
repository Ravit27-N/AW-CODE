import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { InterviewsCandidateModel } from '../../core';
import { getAssetPrefix } from '../../shared';

@Component({
  selector: 'app-feature-candidate-interview',
  templateUrl: './feature-candidate-interview.component.html',
  styleUrls: ['./feature-candidate-interview.component.scss'],
})
export class FeatureCandidateInterviewComponent implements OnChanges {
  @Input() interviews: Array<InterviewsCandidateModel> = [];
  @Input() listInterviewsStatus: Array<string>;
  @Output() editInterviewModel: EventEmitter<InterviewsCandidateModel> =
    new EventEmitter();
  @Output() removeInterview: EventEmitter<InterviewsCandidateModel> =
    new EventEmitter();
  @Output() gotToViewDetails: EventEmitter<number> = new EventEmitter();
  title: string;
  iconPrefix = getAssetPrefix();
  statusField = ['Failed', 'Passed', 'In Progressing', 'New Request'];

  ngOnChanges(simpleChanges: SimpleChanges): void {
    if (simpleChanges.interviews) {
      this.title = this.getTitle();
    }
  }

  getTitle(): string {
    return this.interviews?.length > 1
      ? 'Interviews'
          .concat(' ')
          .concat('('.concat(this.interviews?.length.toString()).concat(')'))
      : 'Interview'
          .concat(' ')
          .concat('('.concat(this.interviews?.length.toString()).concat(')'));
  }

  edit(interview: InterviewsCandidateModel, event: any): void {
    event?.stopPropagation();
    this.editInterviewModel.emit(interview);
  }

  remove(interview: InterviewsCandidateModel, event: any): void {
    event?.stopPropagation();
    this.removeInterview.emit(interview);
  }

  getInterviewTitleCssClass(title: string): string {
    if (title === this.statusField[0]) {
      return 'failed';
    }
    if (title === this.statusField[1]) {
      return 'pass';
    }
    if (title === this.statusField[2]) {
      return 'in-progress';
    }
    if (title === this.statusField[3]) {
      return 'new-request';
    }
    return 'following';
  }

  getInterviewBorderCssClass(title: string): string {
    if (title === this.statusField[0]) {
      return 'failed-border-left';
    }
    if (title === this.statusField[1]) {
      return 'pass-border-left';
    }
    if (title === this.statusField[2]) {
      return 'in-progress-border-left';
    }
    if (title === this.statusField[3]) {
      return 'new-request-border-left';
    }
    return 'following-border-left';
  }
}
