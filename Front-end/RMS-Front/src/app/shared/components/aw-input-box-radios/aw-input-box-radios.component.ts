import {
  Component,
  EventEmitter,
  forwardRef,
  Input, OnChanges,
  Output, SimpleChanges,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { KeyValue } from '@angular/common';

@Component({
  selector: 'app-aw-input-box-radios',
  templateUrl: './aw-input-box-radios.component.html',
  styleUrls: ['./aw-input-box-radios.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AwInputBoxRadiosComponent),
      multi: true,
    },
  ],
})
export class AwInputBoxRadiosComponent implements ControlValueAccessor {
  @Input() awLabel = '';
  @Input() awRequired = false;
  @Input() awDisabled = false;
  @Input() awErrorMessage = '';
  @Input() awOptions: KeyValue<any, any>[] = [];
  @Input() awError = false;
  @Output() change = new EventEmitter<string>();

  value: string;

  writeValue(value: any): void {
    if (value !== undefined) {
      this.value = value;
    }
  }

  onInputChange(value: string): void {
    this.value = value;
    this.onChange(value);
    this.onTouched();
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  private onChange: (value: any) => void = () => {};
  private onTouched: () => void = () => {};
}
