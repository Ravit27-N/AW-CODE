import { AfterViewInit, Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';
import * as moment from 'moment';
import { DateAdapter } from "@angular/material/core";

@Component({
  selector: 'cxm-smartflow-cxm-date-time-picker',
  templateUrl: './cxm-date-time-picker.component.html',
  styleUrls: ['./cxm-date-time-picker.component.scss']
})
export class CxmDateTimePickerComponent implements OnInit, AfterViewInit {

  @Input() dateTime: Date = new Date();
  @Input() accessTimeSecondPicker: boolean = false;
  @Output() dateTimeChange = new EventEmitter<Date>();

  toggleTime = false;
  toggleTimeSecond = false;
  formGroup: FormGroup;
  timeLabel = new BehaviorSubject('');

  timeSecondLabel = new BehaviorSubject('');

  maxHour = 23;
  maxMinute = 59;
  maxSecond = 59;
  hourTmp = '';
  mmTmp = '';
  ssTmp = '';

  constructor(private fb: FormBuilder, private elementRef: ElementRef, private dateAdapter: DateAdapter<Date>) {
    const localeSelected = localStorage.getItem('locale') || 'fr';
    moment.locale(localeSelected);
    dateAdapter.setLocale(localeSelected);
    this.setSingleCharDayOfWeek();
    this.formGroup = this.fb.group({
      date: new FormControl(new Date()),
      hour: new FormControl(''),
      minute: new FormControl(''),
      second: new FormControl('')
    });
  }

  ngOnInit(): void {
    const hour = parseInt(this.dateTime.getHours().toString(), 10) < 10 ? 0 + this.dateTime.getHours().toString() : this.dateTime.getHours().toString();
    const mm = parseInt(this.dateTime.getMinutes().toString(), 10) < 10 ? 0 + this.dateTime.getMinutes().toString() : this.dateTime.getMinutes().toString();
    const ss = parseInt(this.dateTime.getSeconds().toString(), 10) < 10 ? 0 + this.dateTime.getSeconds().toString() : this.dateTime.getSeconds().toString();

    this.formGroup.patchValue({
      date: this.dateTime,
      hour: hour,
      minute: mm,
      second: ss
    });

    this.timeLabel.next(hour?.concat(' : ')?.concat(mm));
    this.timeSecondLabel.next(hour?.concat(' : ')?.concat(mm).concat(' : ')?.concat(ss));

    this.backupHourMinute();
  }

  ngAfterViewInit(): void {
    this.formGroup?.valueChanges?.subscribe(() => {
      this.backupHourMinute();
      this.dateTimeChangeEvent();
      this.timeLabel.next(this.hourTmp?.concat(' : ')?.concat(this.mmTmp));
      this.timeSecondLabel.next(this.hourTmp?.concat(' : ')?.concat(this.mmTmp)?.concat(' : ')?.concat(this.ssTmp));
    });
  }

  setSingleCharDayOfWeek(): void {
    const singleCharDay = this.dateAdapter.getDayOfWeekNames("narrow").map(day => day.charAt(0).toUpperCase());
    this.dateAdapter.getDayOfWeekNames = () => {
      return singleCharDay;
    }
    const sunday = 0;
    this.setFirstDayOfWeek(sunday);
  }

  setFirstDayOfWeek(firstDay: number): void {
    this.dateAdapter.getFirstDayOfWeek = () => {
      return firstDay;
    }
  }

  private backupHourMinute() {
    this.hourTmp = this.hour?.value?.toString()?.length < 2 ? 0 + this.hour?.value?.toString() : this.hour?.value?.toString();
    this.mmTmp = this.minute?.value?.toString()?.length < 2 ? 0 + this.minute?.value?.toString() : this.minute?.value?.toString();
    this.ssTmp = this.second?.value?.toString()?.length < 2 ? 0 + this.second?.value?.toString() : this.second?.value?.toString();
  }

  private dateTimeChangeEvent() {
    const dateTime = moment(this.date?.value);
    dateTime?.hour(parseInt(this.hour?.value?.toString()));
    dateTime?.minute(parseInt(this.minute?.value?.toString()));
    dateTime?.second(parseInt(this.second?.value?.toString()));
    dateTime?.milliseconds(new Date().getMilliseconds());

    this.dateTimeChange.emit(dateTime?.toDate());
  }

  onToggleTime() {
    this.toggleTime = !this.toggleTime;
    if (this.toggleTime) {
      this.formGroup.patchValue({
        hour: this.hourTmp,
        minute: this.mmTmp
      });
    }
    this.validateHour();
    this.validateMinute();
  }

  validateHour() {
    const hourControl = this.formGroup.get('hour');
    if (hourControl?.status === 'INVALID') {
      if (hourControl?.errors) {
        this.formGroup.patchValue({
          hour: this.getCurrentHour()
        });
      }
    }
  }

  validateMinute() {
    const minuteControl = this.formGroup.get('minute');
    if (minuteControl?.status === 'INVALID') {
      if (minuteControl?.errors) {
        this.formGroup.patchValue({
          minute: this.getCurrentMinute(),
        });
      }
    }
  }

  validateSecond() {
    const socondControl = this.formGroup.get('second');
    if (socondControl?.status === 'INVALID') {
      if (socondControl?.errors) {
        this.formGroup.patchValue({
          second: this.getCurrentSecond(),
        });
      }
    }
  }

  onToggleTimeSecond() {

    this.toggleTimeSecond = !this.toggleTimeSecond;
    if (this.toggleTimeSecond) {
      this.formGroup.patchValue({
        hour: this.hourTmp,
        minute: this.mmTmp,
        second: this.ssTmp
      });
    }
    this.validateHour();
    this.validateMinute();
    this.validateSecond();
  }

  get date() {
    return this.formGroup.get('date');
  }

  get hour() {
    return this.formGroup.get('hour');
  }

  get minute() {
    return this.formGroup.get('minute');
  }

  get second() {
    return this.formGroup.get('second');
  }

  reset() {
    this.formGroup.patchValue({
      hour: new Date().getHours(),
      minute: new Date().getMinutes(),
    });

    this.toggleTime = !this.toggleTime;
  }

  resetTimeSecond() {
    this.toggleTimeSecond = !this.toggleTimeSecond;
  }

  // @HostListener('document:click', ['$event.target'])
  // public onClick(target:any) {
  //   const clickedInside = this.elementRef.nativeElement.contains(target);
  //   if (!clickedInside) {
  //     // this click event from outside
  //     this.toggleTime = !this.toggleTime;
  //   }
  // }

  getCurrentHour() {
    const currentHour = new Date().getHours();
    if (currentHour >= 10) {
      return currentHour.toString(10);
    } else if (currentHour < 10 && currentHour > 0) {
      return '0' + currentHour.toString(10);
    } else {
      return '00';
    }
  }

  getCurrentMinute() {
    const currentMinute = new Date().getMinutes();
    if (currentMinute >= 10) {
      return currentMinute.toString(10);
    } else if (currentMinute < 10 && currentMinute > 0) {
      return '0' + currentMinute.toString(10);
    } else {
      return '00';
    }
  }

  getCurrentSecond() {
    const currentSecond = new Date().getSeconds();
    if (currentSecond >= 10) {
      return currentSecond.toString(10);
    } else if (currentSecond < 10 && currentSecond > 0) {
      return '0' + currentSecond.toString(10);
    } else {
      return '00';
    }
  }
}

