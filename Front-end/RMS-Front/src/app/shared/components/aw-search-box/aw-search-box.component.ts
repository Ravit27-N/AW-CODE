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
import { FormBuilder, FormGroup } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-aw-search-box',
  templateUrl: './aw-search-box.component.html',
})
export class AwSearchBoxComponent implements OnInit, OnDestroy, OnChanges {
  // Inputs
  @Input() value = '';
  @Input() hasError = false;
  @Input() disableSearch = false;
  @Input() errorMessage = 'Not match found';
  @Input() showBackgroundTooltip = false;
  @Input() inputPlaceholder = 'Search';

  // Outputs
  @Output() valueChange = new EventEmitter<string>();

  // Observables
  private subscriptions = new Subscription();
  isSearching$ = new BehaviorSubject(false);

  // Form group for search input
  formGroup: FormGroup;

  constructor(private formBuilder: FormBuilder) {
    // Initialize the form group
    this.formGroup = this.formBuilder.group({ searchValue: '' });

    // Subscribe to form group changes for debouncing
    this.subscriptions.add(
      this.formGroup.valueChanges
        .pipe(debounceTime(400), distinctUntilChanged())
        .subscribe((value) => {
          this.valueChange.next(value.searchValue);
        }),
    );
  }

  ngOnInit(): void {
    // Set the initial value of the input field
    this.formGroup.get('searchValue').patchValue(this.value, {
      emitEvent: false,
    });
  }

  ngOnDestroy(): void {
    // Unsubscribe from all subscriptions
    this.subscriptions.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.isSearching$.next(
      (changes?.initialValue?.currentValue &&
        !changes?.hasError?.currentValue) ||
        false,
    );
  }
}
