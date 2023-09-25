import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';


@Component({
  selector: 'cxm-smartflow-search',
  template: `
    <div class='searchComponent' [ngStyle]="{
    'background-color': backgroundColor,
    'border-radius': borderRadius,
    'border': border
    }">
      <input
        type='{{ type }}'
        [ngClass]='classes'
        [placeholder]='placeHolder'
        [ngStyle]="{
        'background-color': backgroundColor,
         'border-radius': borderRadius,
         'color': color,
         'font-size': fontSize ,
         'padding': padding,
         'width': width
      }"
        [disabled]='disable'
        [value]='inputValue'
        (keyup)='search(searchTerm.value)'
        #searchTerm
      >
      <mat-icon (click)='onClickIcon()'>search</mat-icon>
    </div>
  `,
  styleUrls: ['./search.component.scss']
})
export class SearchComponent {

  /**
   * properties
   */

  $searchTerm = new Subject<string>();

  /**
   * input data
   */
  @Input()
  icon = 'search';

  @Input()
  inputValue = '';

  @Input()
  placeHolder = '';

  @Input()
  type = '';

  @Input()
  primary = true;

  @Input()
  size = '';

  @Input()
  backgroundColor = '#f6f6f6';

  @Input()
  color = '';

  @Input()
  fontSize = '';

  @Input()
  borderRadius = '';

  @Input()
  padding = '3px';

  @Input()
  disable = false;

  @Input()
  width: string;

  @Input()
  float: string;

  @Input()
  border: string;

  /**
   * output data
   */
  @Output() valueChange = new EventEmitter<string>();
  @Output() clickIcon = new EventEmitter<boolean>();

  constructor() {
    // delay filtering when searching
    this.$searchTerm.pipe(
      distinctUntilChanged(),
      debounceTime(800)).subscribe((value) => {
      this.valueChange.emit(value);
    });
  }

  onClickIcon(): void {
    this.clickIcon.emit(true);
  }

  public get classes(): string[] {
    const mode = this.primary
      ? 'storybook-input--primary'
      : 'storybook-input--secondary';

    return ['storybook-input', `storybook-input--${this.size}`, mode];
  }

  /**
   * used for manipulate filtering on searching-event
   * @param searchTerm
   */
  search(searchTerm: string): void {
    this.$searchTerm.next(searchTerm.trim());
  }

}
