import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { CxmTooltipDirective } from "@cxm-smartflow/shared/directives/tooltip";
import { interval, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, pluck, take, takeUntil } from 'rxjs/operators';


@Component({
  selector: 'cxm-smartflow-inline-input',
  styleUrls: ['./inline-input.scss'],
  template: `
    <div class="relative"*ngIf="editing">

      <div class="relative z-101">
        <form  [formGroup]="form" (ngSubmit)="submitChange($event)">
          <input type="text" formControlName="value"
          [placeholder]="placeholder | translate"
          (keyup.enter)="handleKeyUp($event)"
          cxmSmartflowCxmTooltip
         [showTooltip]='isShowError'
          />
        </form>
      </div>

      <div class="inline-backdrop" (click)="onClick()"></div>

    </div>
    <ng-content *ngIf="!editing"></ng-content>
  `,
})
export class InlineInputComponent implements OnChanges, OnInit, OnDestroy {

  form: FormGroup;
  editing = false;
  isShowError = false;

  @Input() value: string;
  @Input() placeholder = '';
  @Output() valueChanged = new EventEmitter<string>();
  @Output() pressEnter = new EventEmitter<string>();
  @Output() eventBlur = new EventEmitter<string>();
  destroy$ = new Subject<boolean>();

  @ViewChild(CxmTooltipDirective) tooltip: CxmTooltipDirective;
  inputValue: string;

  toggle() {
    this.editing = !this.editing;
    this.form.patchValue({ value: this.value});
  }

  close() {
    this.editing = false;
  }

  onClick() {
    this.editing = false
    this.eventBlur.emit(this.inputValue);
  }

  raiseErrorNameExisted(message: string) {
    this.tooltip.tooltipText = message;
    this.tooltip?.hideMatTooltip();
    this.isShowError = false;
    this.isShowError = true;

    interval(3000)
      .pipe(take(1))
      .subscribe(() => {
        this.isShowError = false;
      });
  }

  submitChange(e: any) {
    const f = this.form.getRawValue();
    if(f.value.trim() !== this.value) {
      this.pressEnter.emit(f.value.trim());
    } else {
      this.close();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.value) {
      this.form.patchValue({ value: changes.value.currentValue });
    }

  }

  handleKeyUp(e: any){
    this.submitChange(e);
 }

  constructor(private fb: FormBuilder) {
    this.form = fb.group({ value: '' })
  }

  ngOnInit(): void {
    this.form.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged(),
        pluck('value')
      )
      .subscribe(data => {
        this.valueChanged.emit(data);
        this.inputValue = data;
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }
}
