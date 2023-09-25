import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  forwardRef,
  Input,
  Output,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-aw-checkbox',
  templateUrl: './aw-checkbox.component.html',
  styleUrls: ['./aw-checkbox.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: forwardRef(() => AwCheckboxComponent),
    },
  ],
})
export class AwCheckboxComponent implements ControlValueAccessor {
  @Input() disabled = false;
  @Input() formControlName: string;
  @Input() value: string | boolean;
  @Output() checkValueChanged = new EventEmitter<boolean>();

  isChecked = false;
  blurEvent = (temp: any) => {};

  onChange: any = () => {};

  writeValue(obj: any): void {
    this.isChecked = obj;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.blurEvent = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  toggleEvent(value: boolean): void {
    if (this.disabled) {
      return;
    }

    this.isChecked = value;
    this.onChange(this.isChecked);
    this.checkValueChanged.emit(this.isChecked);
  }
}
