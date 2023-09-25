import {Component, forwardRef, Input, OnChanges, SimpleChanges} from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';

@Component({
  selector: 'app-aw-input-box-multiple',
  templateUrl: './aw-input-box-multiple.component.html',
  styleUrls: ['./aw-input-box-multiple.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AwInputBoxMultipleComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => AwInputBoxMultipleComponent),
      multi: true,
    },
  ],
})
export class AwInputBoxMultipleComponent
  implements ControlValueAccessor, Validator, OnChanges
{
  @Input() awLabel = '';
  @Input() awPlaceholder = '';
  @Input() awValues: string[] = [];
  @Input() shouldShowError = false;
  @Input() disabled = false;
  values: string[] = [];

  get invalid(): boolean {
    const invalidValues = this.values.filter((value) =>
      this.awValidator(value),
    );
    return invalidValues.length > 0;
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  @Input() awValidator: any = (validator: any): any => {
    return null;
  };

  ngOnChanges(changes: SimpleChanges) {
    this.values = this.awValues
  }

  /**
   * Write value to form control.
   *
   * @param value
   */
  writeValue(value: any): void {
    if (value) {
      this.values = value;
    }
  }

  /**
   * Register form control has changed.
   *
   * @param fn
   */
  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  /**
   * Register form control has touched.
   *
   * @param fn
   */
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  /**
   * Changes trigger for form control.
   *
   * @param value
   */
  onChange(value: any) {}

  /**
   * Touches trigger for form control.
   */
  onTouched() {}

  removeField(fieldIndex: number) {
    this.values.splice(fieldIndex, 1);
    this.onChange(this.values);
  }

  addMoreFields(): void {
    this.values.push('');
    this.onChange(this.values);
  }

  valueChange(value: string, index: number) {
    this.values[index] = value;
    this.onChange(this.values);
  }

  trackRow(index: number) {
    return index;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  validate(control: AbstractControl): ValidationErrors | null {
    return this.invalid ? { error: true } : null;
  }
}
