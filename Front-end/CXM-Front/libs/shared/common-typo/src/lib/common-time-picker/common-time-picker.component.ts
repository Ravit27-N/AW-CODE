import { Component, EventEmitter, OnDestroy, Output } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";
import { Subscription } from "rxjs";
const between = (num: number, min: number, max: number) => (num < min ? min : num > max ? max : num);

interface ICommonTimePickerComponentValue {
  minute: number;
  hour: number;
}

@Component({
  selector: 'cxm-smartflow-common-time-picker',
  templateUrl: './common-time-picker.component.html',
  styleUrls: ['./common-time-picker.component.scss']
})
export class CommonTimePickerComponent implements OnDestroy {

  changeTimeForm: FormGroup;
  @Output() valueChanged = new EventEmitter<ICommonTimePickerComponentValue>();
  @Output() onvalidate = new EventEmitter<ICommonTimePickerComponentValue>();
  @Output() oncloseRequest = new EventEmitter();

  subscriptions: Subscription;

  oncloseChangeTime() {
    this.changeTimeForm.reset({ hour: 0, minute: 0 }, { emitEvent: false });
  }

  closeChangeTime() {
    this.oncloseChangeTime();
    this.oncloseRequest.emit();
  }

  onValidate() {
    const value = this.getValue();
    this.onvalidate.emit({ hour: value.hour, minute: value.minute });
  }

  increaseH(value: any) {
    this.changeTimeForm.patchValue({ hour: between(parseInt(value) + 1, 0, 23) })
  }

  decreaseH(value: any) {
    this.changeTimeForm.patchValue({ hour: between(parseInt(value) - 1, 0, 23) })
  }

  increaseM(value: any) {
    this.changeTimeForm.patchValue({ minute: between(parseInt(value) + 1, 0, 59) })
  }

  decreaseM(value: any) {
    this.changeTimeForm.patchValue({ minute: between(parseInt(value) - 1, 0, 59) })
  }

  private getValue() {
    const value = this.changeTimeForm.getRawValue();
    value.hour = between(value.hour, 0, 23);
    value.minute = between(value.minute, 0, 59);
    return value;
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  hourChanged($event: any) {
    this.changeTimeForm.patchValue({ hour: between($event, 0, 23) });
  }

  minutesChange($event: any) {
    this.changeTimeForm.patchValue({ minute: between($event, 0, 59) });
  }

  constructor(private fb: FormBuilder) {
    this.changeTimeForm = this.fb.group({
      hour: new FormControl(0),
      minute: new FormControl(0)
    });

    this.subscriptions = this.changeTimeForm.valueChanges.subscribe(() => {
      const value = this.getValue();
      this.valueChanged.emit({ hour: value.hour, minute: value.minute });
    })
  }
}


