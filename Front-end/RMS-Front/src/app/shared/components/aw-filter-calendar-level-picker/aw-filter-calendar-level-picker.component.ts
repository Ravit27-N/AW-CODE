import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { DateRangeBuilder } from './date-range-buildier.util';
import { MatMenuTrigger } from '@angular/material/menu';

export interface CalendarOptionModel {
  startDate: Date;
  endDate: Date;
  option: number;
}

@Component({
  selector: 'app-aw-filter-calendar-level-picker',
  templateUrl: './aw-filter-calendar-level-picker.component.html',
  styleUrls: ['./aw-filter-calendar-level-picker.component.scss'],
})
export class AwFilterCalendarLevelPickerComponent
  implements OnInit, OnChanges, OnDestroy
{
  @Input() calendarOptionConfig: CalendarOptionModel;
  @Input() labelButton = 'Save';
  @Input() showResetButton = false;
  @Output() calendarChange = new EventEmitter<CalendarOptionModel>();
  @Input() isInitialize = false;
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
  private subscription = new Subscription();

  constructor(private formBuilder: FormBuilder) {}

  wrapInObject(data: any, name: string) {
    return Object.assign({}, { [name]: data });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  ngOnInit(): void {
    this.setupForm();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.calendarOptionConfig?.currentValue) {
      this.field.next(this.calendarOptionConfig);
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
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
      .setLanguage('en')
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

  private setupForm(): void {
    this.calendarForm = this.createCalendarForm();
    this.labelForm = this.createLabelForm();
  }

  private createLabelForm(): FormGroup {
    const date = new DateRangeBuilder()
      .setStartDate(
        this.calendarOptionConfig?.startDate
          ? new Date(this.calendarOptionConfig?.startDate)
          : undefined,
      )
      .setEndDate(
        this.calendarOptionConfig?.endDate
          ? new Date(this.calendarOptionConfig?.endDate)
          : undefined,
      )
      .setLanguage('en')
      .build();

    // eslint-disable-next-line no-underscore-dangle
    return this.formBuilder.group({
      startDateLabel: new FormControl(date.formattedStartDate),
      endDateLabel: new FormControl(date.formattedEndDate),
    });
  }

  private createCalendarForm(): FormGroup {
    const date = new DateRangeBuilder()
      .setStartDate(
        this.calendarOptionConfig?.startDate
          ? new Date(this.calendarOptionConfig?.startDate)
          : undefined,
      )
      .setEndDate(
        this.calendarOptionConfig?.endDate
          ? new Date(this.calendarOptionConfig?.endDate)
          : undefined,
      )
      .setLanguage('en')
      .build();

    return this.formBuilder.group({
      start: new FormControl(date.startDate),
      end: new FormControl(date.endDate),
      option: new FormControl(7),
    });
  }

  openMenu() {
    this.addCustomClass();
  }

  closeMenu() {
    this.removeCustomClass();
  }

  private removeCustomClass(): void {
    const element = document.querySelector('.mat-menu-panel');
    if (element) {
      element.classList.remove('aw-mat-menu-panel');
    }
  }

  private addCustomClass(): void {
    const element = document.querySelector('.mat-menu-panel');
    if (element) {
      element.classList.add('aw-mat-menu-panel');
    }
  }
}
