import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Inject,
  OnDestroy,
} from '@angular/core';
import { Subject } from 'rxjs';
import { MatCalendar } from '@angular/material/datepicker';
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  MatDateFormats,
} from '@angular/material/core';
import { takeUntil } from 'rxjs/operators';
import * as moment from 'moment';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-custom-date-range-header',
  templateUrl: './custom-date-range-header.component.html',
  styleUrls: ['./custom-date-range-header.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CustomDateRangeHeaderComponent<D> implements OnDestroy {
  private destroyed$ = new Subject<void>();
  formatDateLabel = 'D MMM';

  constructor(
    private _calendar: MatCalendar<D>,
    private _dateAdapter: DateAdapter<D>,
    @Inject(MAT_DATE_FORMATS) private _dateFormats: MatDateFormats,
    cdr: ChangeDetectorRef
  ) {
    const localeSelected = localStorage.getItem('locale') || 'fr';
    moment.locale(localeSelected);
    this._dateAdapter.setLocale(localeSelected);

    _calendar.stateChanges.pipe(takeUntil(this.destroyed$)).subscribe(() => {
      cdr.markForCheck();
    });
  }

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  get periodLabel() {
    return this._dateAdapter
      .format(
        this._calendar.activeDate,
        this._dateFormats.display.monthYearLabel
      )
      .toLocaleUpperCase();
  }

  get periodStartLabel() {
    const selectedDate = this._calendar.selected as any;
    if (selectedDate?.start) {
      return this.replaceDateLabel(selectedDate?.start);
    }
    return '';
  }

  get periodEndLabel() {
    const selectedDate = this._calendar.selected as any;
    if (selectedDate?.end) {
      return this.replaceDateLabel(selectedDate?.end);
    }
    return '';
  }

  previousClicked(mode: 'month' | 'year') {
    this._calendar.activeDate =
      mode === 'month'
        ? this._dateAdapter.addCalendarMonths(this._calendar.activeDate, -1)
        : this._dateAdapter.addCalendarYears(this._calendar.activeDate, -1);
  }

  nextClicked(mode: 'month' | 'year') {
    this._calendar.activeDate =
      mode === 'month'
        ? this._dateAdapter.addCalendarMonths(this._calendar.activeDate, 1)
        : this._dateAdapter.addCalendarYears(this._calendar.activeDate, 1);
  }

  replaceDateLabel(date: string): string {
    return moment(date).format(this.formatDateLabel).replace('.', '');
  }
}
