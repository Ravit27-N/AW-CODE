import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ReplaySubject, Subscription } from 'rxjs';
import { pagination } from './pagination';


function mustBeNumberInRanged(ranged: number): ValidatorFn {
  return (control: AbstractControl) : ValidationErrors | null => {
    if(control.value === '') return null;

    const value = parseInt(control.value);
    const valid = value > 0 && value <= ranged;
    const x = valid === false ? { notInRanged: true } : null;

    return x;
  }
}


@Component({
  selector: 'cxm-smartflow-list-paginator',
  templateUrl: './list-paginator.component.html',
  styleUrls: ['./list-paginator.component.scss']
})
export class ListPaginatorComponent implements OnInit, OnChanges, OnDestroy {


  @Input() pageSize = 50;
  @Input() pageIndex = 1;
  @Input() length = 0;
  @Input() showLength = true;
  @Input() center = true;

  /** withCriteria: true if result for search, false result for total  */
  @Input() withCriteria = false;
  @Input() withFilter = false;

  _totalPage: number;

  @Output() page = new EventEmitter<{ pageSize: number, pageIndex: number, length: number }>();

  isShowBox = false;
  isSubmitted = false;

  form: FormGroup;

  paginationStream$ = new ReplaySubject<any>(1);
  paginationStreamSubscribe: Subscription;


  ngOnChanges(changes: SimpleChanges): void {
    if(changes.pageSize?.currentValue || changes.pageIndex?.currentValue || changes.length?.currentValue >= 0) {
      this._totalPage = Math.ceil(this.length / this.pageSize);
      this.form.clearValidators();
      this.gotopage.setValidators(mustBeNumberInRanged(this._totalPage));
      this.gotopage.updateValueAndValidity();
    }
  }

  ngOnInit(): void {
    this.paginationStreamSubscribe =
    this.paginationStream$
    // .pipe(distinctUntilChanged((x, y) => x.pageIndex === y.pageIndex))
    .subscribe(value => this.page.emit(value))
  }

  ngOnDestroy(): void {
    this.paginationStreamSubscribe.unsubscribe();
  }

  moveTo(toPage: number) {
    if(toPage === this.pageIndex) return; // do nothing if same page

    this.paginationStream$.next({ pageSize: this.pageSize, pageIndex: toPage as number, length: this.length });
    this.pageIndex = toPage;
  }

  tryToMove(toPage: number | string) {
    if(Number.isInteger(toPage)) {
      this.moveTo(toPage as number);
    } else {
      this.isShowBox = true;
    }
  }

  rangeOfTototal() {
    return this._totalPage > 1 ? pagination(this.pageIndex, this._totalPage) : [];
  }

  next() {
    this.moveTo(Math.min(this._totalPage, this.pageIndex + 1));
  }

  prev() {
    this.moveTo(Math.max(1, this.pageIndex - 1));
  }

  submit() {
    this.isSubmitted = true;
    // if(this.form.valid) {
      const value = parseInt(this.gotopage.value);

      this.moveTo(Math.max(1, Math.min(isNaN(value) ? this._totalPage : value, this._totalPage)));
      this.isSubmitted = false;
      this.isShowBox = false;
      this.gotopage.patchValue('');
    // }
  }

  get gotopage() { return this.form.controls['gotopage'] }

  constructor(private fb: FormBuilder) {
    this.form = fb.group({ gotopage: new FormControl()})
  }

}
