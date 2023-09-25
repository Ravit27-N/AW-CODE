import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DateRange } from '@angular/material/datepicker';
import { ComponentType } from '@angular/cdk/portal';

@Component({
  selector: 'app-aw-calendar-content',
  templateUrl: './aw-calendar-content.component.html',
  styleUrls: ['./aw-calendar-content.component.scss']
})
export class AwCalendarContentComponent implements OnInit {

  @Input() selectedRangeValue: DateRange<Date> | null;
  @Output() selectedRangeValueChange = new EventEmitter<DateRange<Date>>();
  @Input() headerComponent: ComponentType<any>;

  selectedChange(date: any): void {
    if (!this.selectedRangeValue?.start || this.selectedRangeValue?.end) {
      this.selectedRangeValue = new DateRange<Date>(date, null);
    } else {
      const start = this.selectedRangeValue.start;
      const end = date;
      if (end < start) {
        this.selectedRangeValue = new DateRange<Date>(end, start);
      } else {
        this.selectedRangeValue = new DateRange<Date>(start, end);
      }
    }
    this.selectedRangeValueChange.emit(this.selectedRangeValue);
  }


  ngOnInit(): void {
    // Init.
  }


}
