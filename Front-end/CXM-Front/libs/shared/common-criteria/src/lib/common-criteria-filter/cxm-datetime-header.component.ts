import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { DateAdapter, MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { MatCalendar } from '@angular/material/datepicker';
import * as moment from 'moment';


@Component({
  selector: 'cxm-smartflow-cxm-datetime-header-component',
  styleUrls: ['./cxm-datetime-header.component.scss'],
  template: `
    <div class="p-4 pb-1">
      <div class="flex flex-row justify-between">
        <label class="header-label">{{'flowTraceability.table.filter.calender'|translate}}</label>
        <label class="header-text px-1">
          <span style="min-width: 50%;">{{ periodStart }}</span>
          <span class="font-thin">|</span>
          <span style="min-width: 50%;">{{ periodEnd }}</span>
        </label>
      </div>
    </div>

    <div class="mb-2">
      <div class="flex flex-row justify-center">
        <mat-icon (click)="prevClick()" class="arrow cursor-pointer">keyboard_arrow_left</mat-icon>
        <label class="header-label w-32 text-center">{{ periodLabel }}</label>
        <mat-icon (click)="nextClick()" class="arrow cursor-pointer">keyboard_arrow_right</mat-icon>
      </div>
    </div>
  `
})
export class CxmDatetimeHeaderComponent<D> implements OnInit, OnDestroy
{


  prevClick() {
    this._calendar.activeDate = this.dateAdapter.addCalendarMonths(this._calendar.activeDate, -1);
  }

  nextClick() {
    this._calendar.activeDate = this.dateAdapter.addCalendarMonths(this._calendar.activeDate, 1);
  }


  get periodLabel() {
    return this.dateAdapter.format(this._calendar.activeDate, this._dateFormats.display.monthYearLabel)
    .toLocaleUpperCase();
  }

  get periodStart() {
    const selectedDate = this._calendar.selected as any;
    return selectedDate?.start ? moment(selectedDate?.start).format('D MMM').replace('.', '') : '';
  }

  get periodEnd() {
    const selectedDate = this._calendar.selected as any;
    return selectedDate?.end ? moment(selectedDate?.end).format('D MMM').replace('.', '') : '';
  }

  ngOnDestroy(): void {
    document.querySelector('body')?.classList.remove('common-cxm-datetime-component');
  }

  ngOnInit(): void {
    document.querySelector('body')?.classList.add('common-cxm-datetime-component');
  }

  constructor(
    private _calendar: MatCalendar<D>, private dateAdapter: DateAdapter<D>,
    @Inject(MAT_DATE_FORMATS) private _dateFormats: MatDateFormats,
    ) {
      const localeSelected = localStorage.getItem('locale') || 'fr';
      moment.locale(localeSelected);
      dateAdapter.setLocale(localeSelected);
    }
}
