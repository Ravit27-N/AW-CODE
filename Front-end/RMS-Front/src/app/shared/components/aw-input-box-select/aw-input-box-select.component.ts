import {
  Component,
  EventEmitter,
  forwardRef,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { KeyValue } from '@angular/common';

@Component({
  selector: 'app-aw-input-box-select',
  templateUrl: './aw-input-box-select.component.html',
  styleUrls: ['./aw-input-box-select.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AwInputBoxSelectComponent),
      multi: true,
    },
  ],
})
export class AwInputBoxSelectComponent
  implements ControlValueAccessor, OnChanges
{
  @Input() awLabel = '';
  @Input() placeholder = '';
  @Input() awRequired = false;
  @Input() disabled = false;
  @Input() awErrorMessage = '';
  @Input() awOptions: KeyValue<string, any>[] = [];
  @Input() awError = false;
  @Output() change = new EventEmitter<string>();

  selectedValue: string;
  selectedText: string;
  isDisabled: boolean;

  ngOnChanges(changes: SimpleChanges) {
    if (changes?.awOptions?.currentValue) {
      this.writeValue(this.selectedValue);
    }
  }

  onChange: any = (value: any) => {};
  onTouched: any = () => {};

  writeValue(value: string): void {
    this.selectedValue = value;
    this.selectedText =
      this.awOptions.find((item) => item.key === this.selectedValue)?.value ||
      '';
  }

  registerOnChange(onChangeFn: any): void {
    this.onChange = onChangeFn;
  }

  registerOnTouched(onTouchedFn: any): void {
    this.onTouched = onTouchedFn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

  onSelectChange(): void {
    this.onChange(this.selectedValue);
    this.onTouched();
    this.change.emit(this.selectedValue);
  }

  selectOption(awOption: KeyValue<string, any>): void {
    if (this.disabled) {
      return;
    }

    this.selectedValue = awOption.key;
    this.selectedText = awOption.value;
    this.onSelectChange();
  }
}
