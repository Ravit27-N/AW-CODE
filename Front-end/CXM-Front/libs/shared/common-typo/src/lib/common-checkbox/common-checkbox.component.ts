import { ChangeDetectionStrategy, Component, EventEmitter, forwardRef, Input, Output } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'cxm-smartflow-common-checkbox',
  templateUrl: './common-checkbox.component.html',
  styleUrls: ['./common-checkbox.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default,
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    multi: true,
    useExisting: forwardRef(() => CommonCheckboxComponent)
  }]
})
export class CommonCheckboxComponent implements ControlValueAccessor {

  @Input() disabled = false;
  @Input() formControlName: string;
  @Input() value: string;
  @Output() checkValueChanged = new EventEmitter<boolean>();

  isChecked = false;

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  onChange: any = () => {};
  // eslint-disable-next-line @typescript-eslint/no-empty-function
  onBlur = ((_: any) => { });

  writeValue(obj: any): void {
    this.isChecked = obj;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onBlur = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  toggleChanged(value: boolean) {
    if(this.disabled) return;

    this.isChecked = value;
    this.onChange(this.isChecked);
    this.checkValueChanged.emit(this.isChecked);
  }

  constructor() {
    //
  }
}
