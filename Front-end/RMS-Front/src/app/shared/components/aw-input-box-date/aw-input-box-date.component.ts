import {Component, forwardRef, Input, ViewChild} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import {MatDatepicker} from "@angular/material/datepicker";

@Component({
  selector: 'app-aw-input-box-date',
  templateUrl: './aw-input-box-date.component.html',
  styleUrls: ['./aw-input-box-date.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AwInputBoxDateComponent),
      multi: true,
    },
  ],
})
export class AwInputBoxDateComponent implements ControlValueAccessor {
  @Input() awLabel = '';
  @Input() awPlaceholder = '';
  @Input() awErrorMessage = '';
  @Input() awError = false;
  @Input() awRequired = false;
  @ViewChild(MatDatepicker) picker: MatDatepicker<any>;

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

  reset(): void {
    this.picker.select(undefined);
  }
}
