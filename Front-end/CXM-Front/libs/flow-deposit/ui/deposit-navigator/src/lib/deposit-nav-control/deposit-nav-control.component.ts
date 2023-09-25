import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { getStepActive, selectProcessControlResponseState } from '@cxm-smartflow/flow-deposit/data-access';
import { pluck, take, takeUntil } from 'rxjs/operators';
import { Store } from '@ngrx/store';

@Component({
  selector: 'cxm-smartflow-deposit-nav-control',
  templateUrl: './deposit-nav-control.component.html',
  styleUrls: ['./deposit-nav-control.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class DepositNavControlComponent implements OnInit, OnDestroy{

  @Input() canNext = false;
  @Input() canPrev = false;
  @Input() disabled = false;
  @Input() positionStart = false;

  @Input() hideCanPrev = false;
  @Input() hideCanNext = false;
  @Input() showCancelFlow = false;
  @Input() hasPartialError = false;

  @Output() nextClick = new EventEmitter();
  @Output() prevClick = new EventEmitter();
  @Output() cancelFlow = new EventEmitter();

  destroyed$ = new Subject<boolean>();
  isCanIdentify$ = new BehaviorSubject<boolean>(true);

  onPrevClick() {
    if(this.canPrev) {
      this.prevClick.emit();
    }
  }

  onNextClick() {
    if (this.canNext && this.isCanIdentify$.value) {
      this.nextClick.emit();
    }
  }

  onCancelFlow() {
    this.cancelFlow.emit();
  }

  ngOnInit(): void {
    this.store.select(selectProcessControlResponseState)
      .pipe(takeUntil(this.destroyed$), pluck('data', 'ModeleName'))
      .subscribe(v => {
        this.store.select(getStepActive).pipe(take(1)).subscribe(stepActive => {
          if (stepActive === 2) this.isCanIdentify$.next(Boolean(v));
        });
      });

    this.store.select(getStepActive).pipe(takeUntil(this.destroyed$)).subscribe(stepActive => {
      if (stepActive !== 2) {
        this.isCanIdentify$.next(true);
      }
    });
  }

  ngOnDestroy(): void {
      this.destroyed$.next(true);
      this.destroyed$.complete();
  }

  constructor(private store: Store) {}
}
