import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { DateRangeBuilder } from '@cxm-smartflow/analytics/util';
import { MatMenuTrigger } from '@angular/material/menu';
import { CalendarOptionModel } from '@cxm-smartflow/analytics/data-access';

@Component({
  selector: 'cxm-smartflow-filter-calendar-level-picker',
  templateUrl: './filter-calendar-level-picker.component.html',
  styleUrls: ['./filter-calendar-level-picker.component.scss'],
})
export class FilterCalendarLevelPickerComponent
  implements OnInit, OnChanges, OnDestroy
{
  @Input() calendarOptionConfig: CalendarOptionModel;
  @Output() calendarChange = new EventEmitter<CalendarOptionModel>();

  // FormGroup.
  labelForm: FormGroup;
  calendarForm: FormGroup;

  @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;

  field = new BehaviorSubject<CalendarOptionModel>({
    startDate: new Date(),
    endDate: new Date(),
    option: 7,
  });

  // Subscription.
  private _subscription = new Subscription();

  constructor(private _formBuilder: FormBuilder) {}

  wrapInObject(data: any, name: string) {
    return Object.assign({}, { [name]: data });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  ngOnInit(): void {
    this._setupForm();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.calendarOptionConfig.currentValue) {
      this.field.next(this.calendarOptionConfig);
    } else {
      const date = new DateRangeBuilder()
        .setLanguage(localStorage.getItem('locale') || 'fr')
        .build();

      this.field.next({
        startDate: date.startDate,
        endDate: date.endDate,
        option: 2,
      });
    }
  }

  ngOnDestroy(): void {
    this._subscription.unsubscribe();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  onCalendarChange(calendar: { end: Date; start: Date; option: number }) {
    this.calendarForm.patchValue({
      startDate: calendar.start,
      endDate: calendar.end,
      option: calendar.option,
    });

    const date = new DateRangeBuilder()
      .setStartDate(calendar.start)
      .setEndDate(calendar.end)
      .setLanguage(localStorage.getItem('locale') || 'fr')
      .build();

    this.labelForm.patchValue({
      startDateLabel: date.formattedStartDate,
      endDateLabel: date.formattedEndDate,
    });

    this.trigger.closeMenu();

    // Calendar.
    this.calendarChange.emit({
      startDate: calendar.start,
      endDate: calendar.end,
      option: calendar.option,
    });

    this.field.next({
      startDate: date.startDate,
      endDate: date.endDate,
      option: calendar.option,
    });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  private _setupForm(): void {
    this.calendarForm = this._createCalendarForm();
    this.labelForm = this._createLabelForm();
  }

  private _createLabelForm(): FormGroup {
    const date = new DateRangeBuilder()
      .setStartDate(this.calendarOptionConfig?.startDate? new Date(this.calendarOptionConfig?.startDate) : undefined)
      .setEndDate(this.calendarOptionConfig?.endDate? new Date(this.calendarOptionConfig?.endDate) : undefined)
      .setLanguage(localStorage.getItem('locale') || 'fr')
      .build();

    return this._formBuilder.group({
      startDateLabel: new FormControl(date.formattedStartDate),
      endDateLabel: new FormControl(date.formattedEndDate),
    });
  }

  private _createCalendarForm(): FormGroup {
    const date = new DateRangeBuilder()
      .setStartDate(this.calendarOptionConfig?.startDate? new Date(this.calendarOptionConfig?.startDate) : undefined)
      .setEndDate(this.calendarOptionConfig?.endDate? new Date(this.calendarOptionConfig?.endDate) : undefined)
      .setLanguage(localStorage.getItem('locale') || 'fr')
      .build();

    return this._formBuilder.group({
      start: new FormControl(date.startDate),
      end: new FormControl(date.endDate),
      option: new FormControl(7),
    });
  }
}
