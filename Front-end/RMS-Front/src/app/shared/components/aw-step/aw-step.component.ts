import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { AwStepModel } from './aw-step.model';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-aw-step',
  templateUrl: './aw-step.component.html',
  styleUrls: ['./aw-step.component.scss'],
})
export class AwStepComponent implements OnInit {
  @Input() awSteps: AwStepModel[] = [];
  @Output() navigateTo = new EventEmitter<number>();
  totalProgress$ = new BehaviorSubject<string>('0%');

  constructor() {}

  ngOnInit(): void {}

  changeStep(stepNumber: number) {
    if (!stepNumber) {
      this.totalProgress$.next(`0%`);
    } else if (stepNumber > this.awSteps.length) {
      this.totalProgress$.next(`100%`);
    } else {
      this.totalProgress$.next(
        `calc((100% / ${this.awSteps.length / stepNumber}))`,
      );
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes) {
      this.awSteps.forEach((awStep, index) => {
        if (awStep.active) {
          this.changeStep(index + 1);
        }
      });
    }
  }

  updateStep(step: AwStepModel, i: number) {
    if (step.disabled) {
      return;
    }

    this.navigateTo.emit(i + 1);
  }
}
