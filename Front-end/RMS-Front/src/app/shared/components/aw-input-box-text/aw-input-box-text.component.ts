import { Component, forwardRef, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-aw-input-box-text',
  templateUrl: './aw-input-box-text.component.html',
  styleUrls: ['./aw-input-box-text.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AwInputBoxTextComponent),
      multi: true,
    },
  ],
})
export class AwInputBoxTextComponent implements ControlValueAccessor {
  @Input() awLabel = '';
  @Input() awPlaceholder = '';
  @Input() awErrorMessage = '';
  @Input() awError = false;
  @Input() awRequired = false;

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
