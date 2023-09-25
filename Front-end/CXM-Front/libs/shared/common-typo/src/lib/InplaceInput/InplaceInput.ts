import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { Subject } from 'rxjs';
import { distinctUntilChanged, pluck, take, takeUntil } from 'rxjs/operators';


@Component({
  selector: 'cxm-smartflow-inplace-input',
  styleUrls: ['./InplaceInput.scss'],
  template: `
    <div class="relative"*ngIf="editing">
      <div class="relative z-101">
        <form  [formGroup]="form" (ngSubmit)="submitChange($event)">
          <input type="number" [max]="max" [min]="min" formControlName="value" (keyup.enter)="handleKeyUp($event)"/>
        </form>
      </div>
      <div class="inline-backdrop" (click)="editing = false"></div>
    </div>
    <ng-content *ngIf="!editing"></ng-content>
  `,
})
export class InlineInputComponent implements OnChanges, OnInit, OnDestroy {

  form: FormGroup;
  editing = false;
  isShowError = false;

  @Input() min: number|string;
  @Input() max: number|string;

  @Input() value: string;
  @Input() placeholder = '';
  @Output() valueChanged = new EventEmitter<string>();
  @Output() pressEnter = new EventEmitter<string>();
  destroy$ = new Subject<boolean>();

  toggle() {
    this.editing = !this.editing;
    this.form.patchValue({ value: this.value});
  }

  close() {
    this.editing = false;
  }

  submitChange(e: any) {
    const f = this.form.getRawValue();
    if(f.value!== this.value) {
      this.pressEnter.emit(f.value);
    } else {
      this.close();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.value) {
      this.form.patchValue({ value: changes.value.currentValue.toString().padStart(2, 0) }, { emitEvent: false });
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
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }
}
