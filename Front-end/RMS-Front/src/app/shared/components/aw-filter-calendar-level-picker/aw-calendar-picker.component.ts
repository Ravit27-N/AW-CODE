import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { DateRange } from '@angular/material/datepicker';
import { DateAdapter } from '@angular/material/core';
import { AwDateHeaderComponentComponent } from './aw-date-header-component';
import { createCustomDaterange } from './default-ranges.util';

export interface CustomRange<D> {
  name: string;
  startDate: D | Date;
  endDate: D | Date;
}

export interface CalendarOptionModel {
  startDate: Date;
  endDate: Date;
  option: number;
}

export interface CalendarChangedEvent {
  option: number;
  startDate: Date;
  endDate: Date;
}

@Component({
  selector: 'app-aw-calendar-picker',
  templateUrl: './aw-calendar-picker.component.html',
  styleUrls: ['./aw-calendar-picker.component.scss'],
})
export class AwCalendarPickerComponent implements OnInit, OnChanges {
  @Input() labelButton = 'Save';
  @Input() showResetButton = false;
  customCxmDatetimeHeaderComponent = AwDateHeaderComponentComponent;

  selectedCalendar = new DateRange<Date>(null, null);
  customRanges: any[] = [];

  @Output() calenderChanged = new EventEmitter<any>();
  @Input() calendarOptions: CalendarOptionModel;

  constructor(private dateDateAdapter: DateAdapter<Date>) {
    // eslint-disable-next-line no-underscore-dangle
    this._setCustomRanges();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  ngOnInit(): void {}

  getOptionsIndex() {
    const foundIndex = this.customRanges.findIndex((x) => this.isSameRange(x));
    if (foundIndex < 0) {
      return this.customRanges.length;
    }

    return foundIndex;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.calendarOptions && changes.calendarOptions.currentValue) {
      const filter = changes.calendarOptions
        .currentValue as CalendarChangedEvent;
      if (filter) {
        const { endDate, startDate } = filter;
        this.setActiveDate(new Date(startDate), new Date(endDate));
      } else {
        const selectedRange = this.customRanges[filter.option];
        this.setActiveDate(
          new Date(selectedRange?.startDate),
          new Date(selectedRange?.endDate),
        );
      }
    }
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  applyCalendar() {
    const index = this.getOptionsIndex();
    this.calenderChanged.emit({
      option: index,
      start: this.selectedCalendar.start,
      end: this.selectedCalendar.end || this.selectedCalendar.start,
    });
  }

  resetCalendar() {
    const index = this.getOptionsIndex();
    this.calenderChanged.emit({
      option: index,
      start: null,
      end: null,
    });
  }

  dateOptionChanged($event: any): void {
    this.setActiveDate($event.startDate, $event.endDate);
  }

  setActiveDate(from: any, to: any): void {
    this.selectedCalendar = new DateRange(from, to);
  }

  isSameRange(range: any): boolean {
    if (!this.selectedCalendar.start || !this.selectedCalendar.end) {
      return false;
    }

    return (
      this.dateDateAdapter.compareDate(
        range.startDate,
        this.selectedCalendar.start,
      ) === 0 &&
      this.dateDateAdapter.compareDate(
        range.endDate,
        this.selectedCalendar.end,
      ) === 0
    );
  }

  isSomeRange(): boolean {
    if (!this.selectedCalendar.start || !this.selectedCalendar.end) {
      return false;
    }
    return this.customRanges.some((range) => this.isSameRange(range));
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  private _setCustomRanges() {
    this.customRanges = createCustomDaterange(this.dateDateAdapter);
  }
}
