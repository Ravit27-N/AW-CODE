import { StepperSelectionEvent } from '@angular/cdk/stepper';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges, OnDestroy, OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { TranslateService } from '@ngx-translate/core';
import { interval, Subject } from 'rxjs';
import { take, takeUntil } from 'rxjs/operators';

export interface IStep {
  name: string;
  link: string;
  completed: boolean;
  active: boolean;
  step: number;
}

@Component({
  selector: 'cxm-smartflow-stepper',
  templateUrl: './stepper.component.html',
  styleUrls: ['./stepper.component.scss']
})
export class StepperComponent implements OnChanges, OnInit, OnDestroy {

  @Input() steps: IStep[] = [];
  @ViewChild('stepper') stepper: MatStepper;
  destroy$ = new Subject<boolean>();

  @Output() stepClick = new EventEmitter<IStep>();

  public next() {
    this.stepper.next();
  }

  public reset() {
    this.stepper.reset();
  }

  public previous() {
    this.stepper.previous();
  }

  public Jump(stepNumber: number) {
    this.stepper.selectedIndex = Math.max(0, stepNumber - 1);
  }

  public attempToNavigate(event: StepperSelectionEvent) {
    const currentIndex = this.steps.findIndex(x => x.active);
    if(currentIndex >= 0) {
      if(currentIndex !== event.selectedIndex) {
        if(this.steps[event.selectedIndex].completed) {
          this.stepClick?.emit(this.steps[event.selectedIndex]);
        }
      }
    }
  }

  disableStep(): void {
    const checkedTool = interval(1000);
    checkedTool.pipe(takeUntil(this.destroy$))
      .subscribe((e) => {
        const steps = document.querySelectorAll('mat-step-header');
        const lines = document.querySelectorAll('.mat-stepper-horizontal-line');
        if (steps && lines) {
          const activeStep = this.steps?.find(element => location.pathname.includes(element.link))?.step || 1;
          this.steps?.forEach(e => {
            if(e.completed) {
              steps[e.step - 1]?.classList.add('can-move');
            } else {
              steps[e.step - 1]?.classList.remove('can-move');
            }

            if (e.step <= activeStep) {
              steps[e.step - 1]?.classList.add('active-step');
            } else {
              steps[e.step - 1]?.classList.remove('active-step');
            }

            if (e.step < activeStep) {
              lines[e.step - 1]?.classList.add('active-line');
            } else {
              lines[e.step - 1]?.classList.remove('active-line');
            }
          });
        }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes && this.stepper) {
      this.steps.forEach((x, index) => {
        if(x.active) {  this.stepper.selectedIndex = index };
      });
    }
  }

  ngOnInit(): void {
    this.disableStep();
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }


  constructor(private translate: TranslateService) { }

}
