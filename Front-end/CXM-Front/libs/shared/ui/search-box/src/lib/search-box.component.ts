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
import { BehaviorSubject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'cxm-smartflow-search-box',
  templateUrl: './search-box.component.html',
  styleUrls: ['./search-box.component.scss'],
})
export class SearchBoxComponent implements OnInit, OnDestroy, OnChanges {
  @Input() value = '';
  @Input() isError = false;
  @Input() errorMsg = 'input.tooltip.searchNotFound';
  @Input() enableBgTooltip = false;
  @Input() placeholder = 'input.placeholder.search';
  @Output() searchBoxChange = new EventEmitter<string>();

  isSearch$ = new BehaviorSubject(false);

  private _subscription = new Subscription();
  fb: FormGroup;

  constructor(private _fb: FormBuilder) {
    this.fb = this._fb.group({ searchValue: '' });
    this._subscription = this.fb.valueChanges
      .pipe(debounceTime(400), distinctUntilChanged())
      .subscribe((value) => {
        this.searchBoxChange.next(value.searchValue);
      });
  }

  ngOnInit(): void {
    this.fb.controls['searchValue'].patchValue(this.value, {
      emitEvent: false,
    });
  }

  ngOnDestroy(): void {
    this._subscription.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.isSearch$.next((this.value && !this.isError) || false);
  }
}
