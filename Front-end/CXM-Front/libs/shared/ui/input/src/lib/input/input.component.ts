import {
  Component,
  forwardRef,
  Input,
  Output,
  ViewChild,
  EventEmitter,
} from '@angular/core';

import {
  ControlContainer,
  ControlValueAccessor,
  FormControl,
  FormControlDirective,
  NG_VALUE_ACCESSOR,
} from '@angular/forms';

@Component({
  selector: 'cxm-smartflow-input',
  template: `
    <input
      type="{{ type }}"
      (keyup)="onUp.emit($event)"
      (mouseleave)="onLeave.emit($event)"
      (mouseenter)="onEnter.emit($event)"
      (click)="onClick.emit($event)"
      [formControl]="control"
      [ngClass]="classes"
      [placeholder]="placeHolder"
      [readonly]="readOnly"
      [ngStyle]="{
        'background-color': readOnly ? '#f6f6f6' : backgroundColor,
        'border-radius': borderRadius,
        color: color,
        'font-size': fontSize,
        padding: padding
      }"
    />
  `,

  styleUrls: ['./input.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputComponent),
      multi: true,
    },
  ],
})
export class InputComponent implements ControlValueAccessor {
  constructor(private controlContainer: ControlContainer) {}

  @ViewChild(FormControlDirective, { static: true })
  formControlDirective: FormControlDirective;

  @Input()
  formControl: FormControl;

  @Input()
  formControlName: string;

  @Input()
  placeHolder: string;

  @Input()
  type = '';

  @Input()
  primary = false;

  @Input()
  size = '';

  @Input()
  backgroundColor = '';

  @Input()
  color = '';

  @Input()
  fontSize = '';

  @Input()
  borderRadius = '';

  @Input()
  padding = '';

  @Input()
  readOnly = false;

  @Input()
  isDisabled = false;

  @Input() cxmClass = [''];

  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onUp = new EventEmitter<Event>();

  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onLeave = new EventEmitter<Event>();

  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onEnter = new EventEmitter<Event>();

  // eslint-disable-next-line @angular-eslint/no-output-on-prefix
  @Output()
  onClick = new EventEmitter<Event>();

  public get classes(): string[] {
    return [
      'storybook-input',
      this.readOnly ? 'cursor-not-allowed' : '',
    ].concat(this.cxmClass);
  }

  get control() {
    return (
      this.formControl ||
      this.controlContainer.control?.get(this.formControlName)
    );
  }

  registerOnTouched(fn: any): void {
    this.formControlDirective.valueAccessor?.registerOnTouched(fn);
  }

  registerOnChange(fn: any): void {
    this.formControlDirective.valueAccessor?.registerOnChange(fn);
  }

  writeValue(obj: any): void {
    this.formControlDirective.valueAccessor?.writeValue(obj);
  }
}
