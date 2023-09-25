import { Component, forwardRef, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-aw-input-box-text-area',
  templateUrl: './aw-input-box-text-area.component.html',
  styleUrls: ['./aw-input-box-text-area.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AwInputBoxTextAreaComponent),
      multi: true,
    },
  ],
})
export class AwInputBoxTextAreaComponent implements ControlValueAccessor {
  @Input() awLabel = '';
  @Input() awPlaceholder = '';
  @Input() awErrorMessage = '';
  @Input() awError = false;
  @Input() awMaxLength = 0;
  color: 'primary' | 'danger' = 'primary';

  value: string;

  writeValue(value: any): void {
    if (value !== undefined) {
      this.value = value;
      this.color = this.awMaxLength < value?.length ? 'danger' : 'primary';
    }
  }

  onInputChange(value: string): void {
    this.value = value;
    this.onChange(value);
    this.onTouched();
    this.color = this.awMaxLength < value?.length ? 'danger' : 'primary';
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
