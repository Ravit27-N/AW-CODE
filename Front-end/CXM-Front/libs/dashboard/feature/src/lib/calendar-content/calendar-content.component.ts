import { ComponentType } from "@angular/cdk/portal";
import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { DateRange } from "@angular/material/datepicker";


@Component({
  selector: 'cxm-smartflow-calendar-content',
  templateUrl: './calendar-content.component.html',
  styleUrls: ['./calendar-content.component.scss']
})
export class CalendarContentComponent implements OnInit {

  @Input() selectedRangeValue: DateRange<Date> | undefined;
  @Output() selectedRangeValueChange = new EventEmitter<DateRange<Date>>();
  @Input() headerComponent: ComponentType<any>;

  selectedChange(m: any) {
    if (!this.selectedRangeValue?.start || this.selectedRangeValue?.end) {
        this.selectedRangeValue = new DateRange<Date>(m, null);
    } else {
        const start = this.selectedRangeValue.start;
        const end = m;
        if (end < start) {
            this.selectedRangeValue = new DateRange<Date>(end, start);
        } else {
            this.selectedRangeValue = new DateRange<Date>(start, end);
        }
    }
    this.selectedRangeValueChange.emit(this.selectedRangeValue);
}


  ngOnInit(): void {
  }


  constructor() {
    //
   }
}
