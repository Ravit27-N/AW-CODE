import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IsLoadingService } from '@service-work/is-loading';
import { Interview, InterviewResultForm, InterviewService, ResultRankEnum } from '../core';

@Component({
  selector: 'app-result-form',
  templateUrl: './result-form.component.html',
  styleUrls: ['./interview-component.css']
})
export class ResultFormComponent implements OnInit {

  @Input() interview: Interview;
  model: InterviewResultForm;
  validateForm: FormGroup;
  @Output() oncancel = new EventEmitter();
  @Output() onupdateSuccess = new EventEmitter();

  standaloneOption = { standalone: true };

  constructor(private interviewService: InterviewService, private isloadingService: IsLoadingService) { }

  ngOnInit(): void {
    this.preloadForm();

    this.validateForm = new FormGroup({
      quizScore: new FormControl(this.model.quizScore, [Validators.required, Validators.max(this.model.quizMax)]),
      quizMax: new FormControl(this.model.quizMax, [Validators.required]),
      codeScore: new FormControl(this.model.codeScore, [Validators.required, Validators.max(this.model.codeMax)]),
      codeMax: new FormControl(this.model.codeMax, [Validators.required]),
      avarage: new FormControl({ value: this.model.avarage, disabled: true })
    });

    this.validateForm.controls.quizMax.valueChanges.subscribe(x => {
      this.validateForm.controls.quizScore.clearValidators();
      this.validateForm.controls.quizScore.setValidators([Validators.required, Validators.max(x)]);
      this.validateForm.controls.quizScore.updateValueAndValidity();
    });

    this.validateForm.controls.codeMax.valueChanges.subscribe(x => {
      this.validateForm.controls.codeScore.clearValidators();
      this.validateForm.controls.codeScore.setValidators([Validators.required, Validators.max(x)]);
      this.validateForm.controls.codeScore.updateValueAndValidity();
    });
  }

  cancel(): void {
    if (this.oncancel) {
      this.oncancel.emit();
    }
  }

  saveChange(): void {
    if (this.validateForm.invalid) {
      return;
    }
    this.model.avarage = this.calcAverage;
    const subscription = this.interviewService.updateResult(this.interview, this.model).subscribe(() => this.onupdateSuccess.emit());
    this.isloadingService.add(subscription, { key: 'ResultFormComponent', unique: 'ResultFormComponent' });
  }

  get calcAverage(): number {
    return ((this.model.codeScore / this.model.codeMax) + (this.model.quizScore / this.model.quizMax)) * 50;
  }

  preloadForm(): void {
    this.model = {
      quizScore: null,
      quizMax: 50,
      codeScore: null,
      codeMax: 50,
      avarage: 0,
      english: ResultRankEnum.good,
      flexibily: ResultRankEnum.good,
      logical: ResultRankEnum.good,
      qa: ResultRankEnum.good,
      remark: ''
    };

    if (this.interview.hasResult) {
      const subscription = this.interviewService.getResult(this.interview).subscribe(data => {
        this.model = {
          quizScore: data.score.quiz.score,
          quizMax: data.score.quiz.max,
          codeScore: data.score.coding.score,
          codeMax: data.score.coding.max,
          avarage: data.average,
          english: data.english,
          flexibily: data.flexibility,
          logical: data.logical,
          qa: data.oral,
          remark: data.remark
        };
      });
      this.isloadingService.add(subscription, { key: 'ResultFormComponent', unique: 'ResultFormComponent' });
    }
  }
}
