import { Component, EventEmitter, InjectionToken, Input, OnChanges, OnInit, Output, SimpleChanges } from "@angular/core";
import { DateAdapter } from "@angular/material/core";
import { DateRange } from "@angular/material/datepicker";
import { CxmDatetimeHeaderComponent } from "@cxm-smartflow/shared/common-criteria";
import { createCustomDaterange, DEFAULT_DATES_RANGES } from '../default-ranges';

export interface CustomRange<D> {
	name: string
	startDate: D | Date
	endDate: D | Date
}

export interface CalendarChangedEvent {
  option: number;
  start: Date;
  end: Date;
}

export const CXM_DEFAULT_DATES_RANGES = new InjectionToken<CustomRange<any>[]>('Custom Ranges')

@Component({
  selector: 'cxm-smartflow-calendar-date-selector',
  templateUrl: './calendar-date-selector.component.html',
  styleUrls: ['./calendar-date-selector.component.scss']
})
export class CalendarDateSelectorComponent implements OnInit, OnChanges {

  customCxmDatetimeHeaderComponent = CxmDatetimeHeaderComponent;

  selectedDateranged = new DateRange<Date>(null, null);
  customRanges: any[] = [];

  @Output() calenderChanged = new EventEmitter<any>();
  @Input() calendarOptions: CalendarChangedEvent;

  applyCalendar( ){
    const index = this.getOptionsIndex();
    this.calenderChanged.emit({ option: index, start: this.selectedDateranged.start, end: this.selectedDateranged.end || this.selectedDateranged.start })
  }

  dateOptionsChaneged($event: any, index: number) {
    this.setActiveDate($event.startDate, $event.endDate);
  }

  private _setCustomRanges(){
    this.customRanges = createCustomDaterange(this.dateadapter);
	}

  setActiveDate(from: any, to: any){
    this.selectedDateranged = new DateRange(from, to);
	}

  isSameRange(range: any) {
    if(!this.selectedDateranged.start || !this.selectedDateranged.end) return false;

    return this.dateadapter.compareDate(range.startDate, this.selectedDateranged.start) == 0 &&
    this.dateadapter.compareDate(range.endDate, this.selectedDateranged.end) == 0;
  }

  isSomeRange() {
    if(!this.selectedDateranged.start || !this.selectedDateranged.end) return;
    return this.customRanges.some(range => this.isSameRange(range));
  }

  ngOnInit(): void {
  }

  getOptionsIndex() {
    const foundIndex = this.customRanges.findIndex(x => this.isSameRange(x));
    if(foundIndex < 0) {
      return this.customRanges.length; // custom range
    }

    return foundIndex;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.calendarOptions && changes.calendarOptions.currentValue) {
      const filter = changes.calendarOptions.currentValue as CalendarChangedEvent;
      if(filter.option === this.customRanges.length) {
        const { end, start } = filter;
        this.setActiveDate(new Date(start), new Date(end));
      } else {
        const selectedRange = this.customRanges[filter.option];
        this.setActiveDate(selectedRange.startDate, selectedRange.endDate);
      }

    }
  }

  constructor(private dateadapter: DateAdapter<Date>) {
    this._setCustomRanges();
   }
}
