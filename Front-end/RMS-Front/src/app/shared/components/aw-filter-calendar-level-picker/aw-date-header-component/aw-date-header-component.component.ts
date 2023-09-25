import {Component, OnInit, OnDestroy, Inject} from '@angular/core';
import { DateAdapter, MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { MatCalendar } from '@angular/material/datepicker';
import * as moment from 'moment';
@Component({
  selector: 'app-aw-date-header-component',
  templateUrl: './aw-date-header-component.component.html',
  styleUrls: ['./aw-date-header-component.component.scss']
})
export class AwDateHeaderComponentComponent<D> implements OnInit, OnDestroy {

  constructor(
    private calendar: MatCalendar<D>, private dateAdapter: DateAdapter<D>,
    @Inject(MAT_DATE_FORMATS) private dateFormats: MatDateFormats,
  ) {
    const localeSelected = 'en';
    moment.locale(localeSelected);
    dateAdapter.setLocale(localeSelected);
  }

  prevClick() {
    this.calendar.activeDate = this.dateAdapter.addCalendarMonths(this.calendar.activeDate, -1);
  }

  nextClick() {
    this.calendar.activeDate = this.dateAdapter.addCalendarMonths(this.calendar.activeDate, 1);
  }


  get periodLabel() {
    return this.dateAdapter.format(this.calendar.activeDate, this.dateFormats.display.monthYearLabel)
      .toLocaleUpperCase();
  }

  get periodStart() {
    const selectedDate = this.calendar.selected as any;
    return selectedDate?.start ? moment(selectedDate?.start).format('D MMM').replace('.', '') : '';
  }

  get periodEnd() {
    const selectedDate = this.calendar.selected as any;
    return selectedDate?.end ? moment(selectedDate?.end).format('D MMM').replace('.', '') : '';
  }

  ngOnDestroy(): void {
    document.querySelector('body')?.classList.remove('common-cxm-datetime-component');
  }

  ngOnInit(): void {
    document.querySelector('body')?.classList.add('common-cxm-datetime-component');
  }


}
