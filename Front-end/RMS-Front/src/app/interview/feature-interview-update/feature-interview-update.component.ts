import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Interview, InterviewService } from '../../core';

@Component({
  selector: 'app-feature-interview-update',
  templateUrl: './feature-interview-update.component.html',
  styleUrls: ['./feature-interview-update.component.scss'],
})
export class FeatureInterviewUpdateComponent implements OnInit {
  interview: Interview;

  constructor(
    private activateRoute: ActivatedRoute,
    private interviewService: InterviewService,
  ) {}

  ngOnInit(): void {
    const id = this.activateRoute.snapshot.params.id;
    this.interviewService
      .getById(id)
      .toPromise()
      .then((result: Interview) => {
        this.interview = result;
      });
  }
}
