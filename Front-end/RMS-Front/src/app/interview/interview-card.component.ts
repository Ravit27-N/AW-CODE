import { Component, Input, OnInit } from '@angular/core';
import { Interview, InterviewService } from '../core';
import { getAssetPrefix } from '../shared';

@Component({
  selector: 'app-interview-card',
  templateUrl: './interview-card.component.html',
  styleUrls: ['./interview-component.css'],
})
export class InterviewCardComponent implements OnInit {
  @Input() interview: Interview;
  imgUrl: string;

  assetPrefix = getAssetPrefix();
  statusField = ['Failed', 'Passed', 'In Processing', 'New Request'];

  constructor(private interviewService: InterviewService) {}

  ngOnInit(): void {
    if (this.interview.candidate.photoUrl) {
      this.imgUrl = `/candidate/${this.interview.candidate.id}/view/${this.interview.candidate.photoUrl}`;
    } else {
      this.interviewService.getById(this.interview.id).subscribe((x) => {
        if (x.candidate.photoUrl) {
          this.imgUrl = `/candidate/${this.interview.candidate.id}/view/${x.candidate.photoUrl}`;
        }
      });
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
}
