import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { InterviewTemplateModel } from '../../../core';

@Component({
  selector: 'app-feature-interview-template-detail',
  templateUrl: './feature-interview-template-detail.component.html',
  styleUrls: ['./feature-interview-template-detail.component.scss'],
})
export class FeatureInterviewTemplateDetailComponent implements OnInit {
  active: string;
  interviewers: Array<string> = [];

  constructor(
    public dialogRef: MatDialogRef<FeatureInterviewTemplateDetailComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: InterviewTemplateModel,
  ) {}

  ngOnInit(): void {
    this.interviewers = this.data?.interviewer
      ? this.data?.interviewer.split(',')
      : [''];
    if (this.data.active === true) {
      this.active = 'Active';
    } else {
      this.active = 'Inactive';
    }
  }

  getStatusCss(status: boolean): string {
    return status ? 'status-active' : 'status-inactive';
  }
}
