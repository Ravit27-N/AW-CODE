import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ReplaySubject, Subscription } from 'rxjs';
import { PaginationUtils } from './aw-pagination.control';
import { AwPaginationModel } from './aw-pagination.model';
import { AwPaginationValidator } from './aw-pagination.validator';

@Component({
  selector: 'app-aw-pagination',
  templateUrl: './aw-pagination.component.html',
  styleUrls: ['./aw-pagination.component.scss'],
})
export class AwPaginationComponent implements OnInit, OnChanges, OnDestroy {
  @Input() pageSize = 0;
  @Input() pageIndex = 0;
  @Input() length = 0;
  @Input() hasFilter = false;
  @Output() pageChanged = new EventEmitter<AwPaginationModel>();

  totalPageCount: number;
  isShowPageInputBox = false;
  isSubmitted = false;
  formGroup: FormGroup;
  paginationStream$ = new ReplaySubject<AwPaginationModel>(1);
  paginationStreamSubscription: Subscription;

  constructor(private formBuilder: FormBuilder) {
    this.formGroup = formBuilder.group({
      gotoPage: new FormControl(),
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      changes.pageSize?.currentValue ||
      changes.pageIndex?.currentValue ||
      changes.length?.currentValue >= 0
    ) {
      this.totalPageCount = Math.ceil(this.length / this.pageSize);
      this.formGroup.clearValidators();
      this.formGroup.controls['gotoPage'].setValidators(
        AwPaginationValidator.mustBeNumberInRange(this.totalPageCount),
      );
      this.formGroup.controls['gotoPage'].updateValueAndValidity();
    }
  }

  ngOnInit(): void {
    this.paginationStreamSubscription = this.paginationStream$.subscribe(
      (input) => this.emitPageChangedEvent(input),
    );
  }

  ngOnDestroy(): void {
    this.paginationStreamSubscription.unsubscribe();
  }

  moveToPage(targetPageIndex: number) {
    if (targetPageIndex === this.pageIndex) {
      return; // Do nothing if same page
    }

    this.paginationStream$.next({
      pageSize: this.pageSize,
      pageIndex: targetPageIndex,
      length: this.length,
    });
    this.pageIndex = targetPageIndex;
  }

  tryToMove(targetPageIndexOrString: number | string) {
    if (Number.isInteger(targetPageIndexOrString)) {
      this.moveToPage(targetPageIndexOrString as number);
    } else {
      this.isShowPageInputBox = true;
    }
  }

  getPageRange() {
    return this.totalPageCount > 1
      ? PaginationUtils.pagination(this.pageIndex, this.totalPageCount)
      : [];
  }

  navigateToNextPage() {
    this.moveToPage(Math.min(this.totalPageCount, this.pageIndex + 1));
  }

  navigateToPreviousPage() {
    this.moveToPage(Math.max(1, this.pageIndex - 1));
  }

  submitPageInput() {
    this.isSubmitted = true;
    const inputValue = JSON.parse(
      `${this.formGroup.controls['gotoPage'].value}`,
    );
    const targetPageIndex = Math.max(
      1,
      Math.min(
        isNaN(inputValue) ? this.totalPageCount : inputValue,
        this.totalPageCount,
      ),
    );

    this.moveToPage(targetPageIndex);
    this.isSubmitted = false;
    this.isShowPageInputBox = false;
    this.formGroup.controls['gotoPage'].patchValue('');
  }

  private emitPageChangedEvent(input: AwPaginationModel) {
    this.pageChanged.emit({
      pageSize: input.pageSize,
      pageIndex: input.pageIndex,
      length: input.length,
    });
  }
}
