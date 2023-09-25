import {Component, forwardRef, Input, ViewChild} from '@angular/core';
import {ControlContainer, FormControl, FormControlDirective, NG_VALUE_ACCESSOR} from "@angular/forms";

@Component({
  selector: 'cxm-smartflow-date-time-picker',
  templateUrl: './date-time-picker.component.html',
  styleUrls: ['./date-time-picker.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DateTimePickerComponent),
      multi: true
    }
  ]
})
export class DateTimePickerComponent{

  @Input()
  formControl: FormControl;

  @Input()
  formControlName: string;

  @ViewChild(FormControlDirective, { static: true })
  formControlDirective: FormControlDirective;

  constructor(private controlContainer: ControlContainer) {}

  public get control(){
    return this.formControl || this.controlContainer.control?.get(this.formControlName);
  }

  public registerOnTouched(fn: any): void {
    this.formControlDirective.valueAccessor?.registerOnTouched(fn);
  }

  public registerOnChange(fn: any): void {
    this.formControlDirective.valueAccessor?.registerOnChange(fn);
  }

  public writeValue(obj: any): void {
    this.formControlDirective.valueAccessor?.writeValue(obj);
  }

}
