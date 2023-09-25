import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { IStep } from '@cxm-smartflow/shared/ui/stepper';

@Component({
  selector: 'cxm-smartflow-step',
  templateUrl: './step.component.html',
  styleUrls: ['./step.component.scss']
})
export class StepComponent implements OnChanges {

  @Input() steps: IStep[] = [];
  totalProgress$ = new BehaviorSubject<string>('0%');
  @Output() stepClick = new EventEmitter<IStep>();

  changeStep(stepNumber: number) {
    if (!stepNumber) {
      this.totalProgress$.next(`0%`);
    } else if (stepNumber > this.steps.length) {
      this.totalProgress$.next(`100%`);
    } else {
      this.totalProgress$.next(`calc((100% / ${this.steps.length / stepNumber}) + 30px)`);
    }
  }

  attemptToNavigate(stepNumber: number) {
    const currentIndex = this.steps.findIndex(x => x.active);

    // Check if target step already completed and not equal to current step, make emitting value.
    if (currentIndex >= 0) {
      if (currentIndex !== stepNumber) {
        if(this.steps[stepNumber].completed) {
          this.stepClick.emit(this.steps[stepNumber]);
        }
      }
    }

  }

  get activeStep(): number {
    return this.steps?.find(element => location.pathname.includes(element.link))?.step || 1;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes) {
      this.steps.forEach((x, index) => {
        if(x.active) {  this.changeStep(index + 1) }
      });
    }
  }
}
