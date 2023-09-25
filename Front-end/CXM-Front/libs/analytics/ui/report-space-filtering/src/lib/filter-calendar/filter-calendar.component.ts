import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { CxmDatetimeHeaderComponent } from '@cxm-smartflow/shared/common-criteria';
import { Subscription } from 'rxjs';
import { CalendarModel } from '@cxm-smartflow/analytics/data-access';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';
import { DateRangeBuilder } from '@cxm-smartflow/analytics/util';


@Component({
  selector: 'cxm-smartflow-filter-calendar',
  templateUrl: './filter-calendar.component.html',
  styleUrls: ['./filter-calendar.component.scss']
})
export class FilterCalendarComponent implements OnInit, OnChanges, OnDestroy {

  @Input() calendar: CalendarModel | null;
  @Output() calendarChange = new EventEmitter<CalendarModel>();

  calendarFormGroup: FormGroup;
  dateFormGroup: FormGroup;
  customCxmDatetimeHeaderComponent = CxmDatetimeHeaderComponent;

  private _subscriptions = new Subscription();

  constructor(private _formBuilder: FormBuilder) {}

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  ngOnInit(): void {
    this._setupForm();
    this._observeFormChange();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this._setupForm();
  }

  ngOnDestroy(): void {
    this._subscriptions.unsubscribe();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  mainMenuOpen(): void {
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }


  mainMenuClose(): void {
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }


  openCalendar(): void {
    this.calendarFormGroup.patchValue({ isOpening: true }, { emitEvent: false });
  }


  closeCalendar(): void {
    this.calendarFormGroup.patchValue({ isOpening: false, }, { emitEvent: false, onlySelf: false });
  }


  applyCalendar(): void {
    // Get the start date and end date from the form.
    let { startDate, endDate } = this.dateFormGroup.getRawValue();
    // Get the current date.
    const currentDate = new Date(Date.now());

    // If start date is greater than current date, set start date to current date.
    if (startDate > currentDate) {
      startDate = currentDate;
    }

    // If end date is falsy, set it to the current date.
    if (!endDate) {
      endDate = currentDate;
    }

    // If start date is less than current date and end date is falsy, set end date to current date.
    if (startDate < currentDate && !endDate) {
      endDate = currentDate;
    }

    // Build a date range object with the start date and end date.
    const dateRange = new DateRangeBuilder()
      .setStartDate(startDate)
      .setEndDate(endDate)
      .setLanguage(localStorage.getItem('locale') || 'fr')
      .build();

    // Update the form with the start date and end date.
    this.dateFormGroup.patchValue({
      startDate,
      endDate,
    });

    // Update the calendar form with the start date, end date, and date labels.
    this.calendarFormGroup.patchValue({
      isOpening: false,
      startDate,
      endDate,
      startDateLabel: dateRange.formattedStartDate,
      endDateLabel: dateRange.formattedEndDate,
    });
  }


  resetCalendar(): void {
    const dateRange = new DateRangeBuilder()
      .setLanguage(localStorage.getItem('locale') || 'fr')
      .build();

    this.dateFormGroup.patchValue({
      startDate: dateRange.startDate,
      endDate: dateRange.endDate,
    });

    this.calendarFormGroup.patchValue({
      isOpening: false,
      startDate: dateRange.startDate,
      endDate: dateRange.endDate,
      startDateLabel: dateRange.formattedStartDate,
      endDateLabel: dateRange.formattedEndDate,
    });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  private _setupForm(): void {
    this.calendarFormGroup = this._createCalendarFormGroup();
    this.dateFormGroup = this._createDateFormGroup();
  }


  private _createCalendarFormGroup(): FormGroup {
    const dateRange = new DateRangeBuilder()
      .setStartDate(this.calendar?.startDate? new Date(this.calendar?.startDate) : undefined)
      .setEndDate(this.calendar?.endDate? new Date(this.calendar?.endDate) : undefined)
      .setLanguage(localStorage.getItem('locale') || 'fr')
      .build();


    return this._formBuilder.group({
      startDate: new FormControl(dateRange.startDate),
      endDate: new FormControl(dateRange.endDate),
      startDateLabel: new FormControl(dateRange.formattedStartDate),
      endDateLabel: new FormControl(dateRange.formattedEndDate),
      isOpening: new FormControl(false),
    });
  }


  private _createDateFormGroup() {
    const rawValue = this.calendarFormGroup.getRawValue();
    return this._formBuilder.group({
      startDate: new FormControl(rawValue.startDate),
      endDate: new FormControl(rawValue.endDate),
    });
  }


  private _observeFormChange(): void {
    this._subscriptions.add(
      this.calendarFormGroup.valueChanges
        .subscribe(({ startDate, endDate }) => {
          this.calendarChange.emit({
            startDate,
            endDate
          });
        })
    );
  }

}
