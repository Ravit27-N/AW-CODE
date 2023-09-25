import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatMenuTrigger } from '@angular/material/menu';
import { TranslateService } from '@ngx-translate/core';

interface OffloadByDay {
  label: string;
  check: boolean;
  hours: string[];
}

interface OffloadByHoliday {
  label: string;
  check: boolean;
  id: string;
  day?: string;
  month?: string;
  holidayTooltip?: string;
}

interface IOffloadFormSubmitEvent {
  byDays: any;
  byHolidays: any;
}

@Component({
  selector: 'cxm-smartflow-offloading-form',
  templateUrl: './offloading-form.component.html',
  styleUrls: ['./offloading-form.component.scss'],
})
export class OffloadingFormComponent implements OnInit, OnChanges {
  initialized = false;

  @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;

  @Output() onformChanged = new EventEmitter<IOffloadFormSubmitEvent>();
  @Input() configuration: {
    byDays: OffloadByDay[];
    byHolidays: OffloadByHoliday[];
  };

  form: FormGroup;
  formModel: OffloadByDay[] = [];

  holidayForm: FormGroup;
  holidayFormModel: OffloadByHoliday[] = [];

  ngOnInit(): void {
    this.setupForm();

    // Tracking change
    this.form.valueChanges.subscribe(() => this.formValueChanged());
    this.holidayForm.valueChanges.subscribe(() => this.formValueChanged());
  }

  setupForm() {
    this.removeAllControl();

    const controls = this.formModel.reduce(this.mapCreateFormControl, {});
    Object.keys(controls).forEach((k) =>
      this.form.addControl(k, controls[k], { emitEvent: false })
    );

    const holidayControls = this.holidayFormModel.reduce(
      this.mapCreateFormControl,
      {}
    );
    Object.keys(holidayControls).forEach((k) =>
      this.holidayForm.addControl(k, holidayControls[k], { emitEvent: false })
    );
  }

  private removeAllControl() {
    Object.keys(this.form.controls).forEach((k) => this.form.removeControl(k));
    Object.keys(this.holidayForm.controls).forEach((k) =>
      this.holidayForm.removeControl(k)
    );
  }

  mapCreateFormControl = (prev: any, cur: any) => {
    const control = { [cur.label]: this.fb.control(cur.check) };
    return Object.assign(prev, control);
  };

  oncloseChangeTime() {
    //
  }

  closeChangeTime() {
    this.trigger.closeMenu();
  }

  updateChangeTime(name: string, value: any) {
    this.closeChangeTime();
    this.formModel = this.formModel.map((x) => {
      if (x.label === name) {
        const newHour = `${value.hour
          .toString()
          .padStart(2, 0)}:${value.minute.toString().padStart(2, 0)}`;
        let h = [...x.hours, newHour].sort((a, b) => a.localeCompare(b));
        h = h.filter((x, index) => h.indexOf(x) === index);

        return { ...x, hours: h };
      }
      return x;
    });

    this.formValueChanged();
  }

  removeTime(item: any, index: number) {
    if (item.check === false) return;

    this.formModel = this.formModel.map((x) => {
      if (x.label === item.label) {
        const hs = x.hours.filter((v, i) => i !== index);
        return { ...x, hours: hs };
      }
      return x;
    });

    this.formValueChanged();
  }

  formValueChanged() {
    const daysForm = this.form.getRawValue();
    const holidays = this.holidayForm.getRawValue();
    const byDays = this.formModel.map((x) => ({
      ...x,
      check: daysForm[x.label],
    }));
    const byHolidays = this.holidayFormModel.map((x) => ({
      ...x,
      check: holidays[x.label],
    }));

    this.formModel = byDays;

    this.onformChanged.emit({ byDays, byHolidays });
  }

  get firstHalf() {
    return this.holidayFormModel.slice(
      0,
      Math.ceil(this.holidayFormModel.length / 2)
    );
  }

  get secondHalf() {
    return this.holidayFormModel.slice(
      Math.ceil(this.holidayFormModel.length / 2)
    );
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      changes.configuration &&
      /**!changes.configuration.firstChange && **/ !this.initialized
    ) {
      if (changes.configuration.currentValue.ready == false) return;

      this.formModel = changes.configuration.currentValue.byDays.map(
        (d: any) => ({ ...d })
      );
      this.holidayFormModel = changes.configuration.currentValue.byHolidays.map(
        (d: any) => ({ ...d, check: d.check })
      );

      this.setupForm();

      this.initialized = true;
    }
  }

  constructor(private fb: FormBuilder, private translate: TranslateService) {
    this.form = this.fb.group({});
    this.holidayForm = this.fb.group({});
  }
}
