import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IsLoadingService } from '@service-work/is-loading';
import { Interview, InterviewResult, InterviewService } from '../core';

@Component({
  selector: 'app-result-card',
  templateUrl: './result-card.component.html',
  styleUrls: ['./interview-component.css']
})
export class ResultCardComponent implements OnInit {

  @Input() interview: Interview;
  result: InterviewResult = null;

  @Input() enableEdit = false;
  @Output() onedit = new EventEmitter();

  constructor(private interviewService: InterviewService, private isloadingService: IsLoadingService) { }

  ngOnInit(): void {
    const subscription = this.interviewService.getResult(this.interview).subscribe(data => {
      this.result = data;
    });
    this.isloadingService.add(subscription, { key:'ResultCardComponent', unique: 'ResultCardComponent' });
  }

  editClick(): void {
    this.onedit.emit();
  }

}
