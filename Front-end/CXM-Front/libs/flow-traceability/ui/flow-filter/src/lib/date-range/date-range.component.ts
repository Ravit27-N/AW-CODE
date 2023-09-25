import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { CustomDateRangeHeaderComponent } from '../custom-date-range-header/custom-date-range-header.component';
import { FormControl, FormGroup } from '@angular/forms';
import * as moment from 'moment';
import {
  CriteriaStorage,
  DateRangeModel,
  DateRangeType,
  FlowCriteriaSessionService,
  formatDateToRequest,
  getDateRangeLast7Days,
} from '@cxm-smartflow/flow-traceability/data-access';
import { ReplaySubject, Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-date-range',
  templateUrl: './date-range.component.html',
  styleUrls: ['./date-range.component.scss'],
})
export class DateRangeComponent implements OnInit, OnDestroy {
  customDateRangeHeader = CustomDateRangeHeaderComponent;

  @Output() dateRangeChange = new EventEmitter<DateRangeModel>();
  @Input() dateRangeType: DateRangeType;
  @Input() customCssClass = '';

  usingDateFilter = false;

  // Localstorage criteria properties.
  startDate: any;
  endDate: any;
  // Presenting properties.
  startDateLabel = '';
  endDateLabel = '';
  // Validation properties.
  isDisplayDateLabel = false;
  resetShipment = false;
  dateRangeChange$ = new ReplaySubject<boolean>();
  // Date range form properties.
  range = new FormGroup({
    start: new FormControl(),
    end: new FormControl(),
  });
  // Destroy subscription properties.
  destroy$ = new Subject<boolean>();

  constructor(private storageService: FlowCriteriaSessionService) {
    // Set date-range language.
    const localeSelected = localStorage.getItem('locale') || 'fr';
    moment.locale(localeSelected);
  }

  ngOnInit(): void {
    // Initial criteria from localstorage if it is not undefined.
    this.initCriteria();

    // Emit data to the parent component.
    this.dateRangeChange$
      .pipe(
        takeUntil(this.destroy$),
        filter((isChange) => isChange)
      )
      .subscribe((isChange) => {
        this.emitDateRangeValue();
      });
  }

  /**
   * Make action when the date-range change value.
   *
   *  - Set the selected date criteria and save in localstorage.
   *  - Set the selected date in the date-range form.
   * @param $event
   */
  dateRangeChanged($event?: any) {
    // Set the formatted start date.
    this.startDate = formatDateToRequest(this.range.value.start);
    const currentDate = new Date();
    const _start = new Date(this.startDate);

    if (_start.getTime() > currentDate.getTime()) {
      // Check if the start date greater than current date, set current date in form.
      this.range.setValue({ start: new Date(), end: new Date() });
      // Set the formatted start-date to the criteria start-date.
      this.startDate = formatDateToRequest(this.range.value.start);
    } else if (!this.range.value.end) {
      // If the end-date form is falsy, set current-date to end-date.
      this.range.setValue({ ...this.range.value, end: new Date() });
    }

    // Set the formatted end-date to the criteria end-date.
    this.endDate = formatDateToRequest(this.range.value.end);

    // Store criteria in localstorage.
    this.setCriteriaInLocalstorage();
  }

  /**
   * Add custom style to the material UI when date-range component opened.
   */
  dateRangeOpened(): void {
    // Hide the display date label
    this.isDisplayDateLabel = true;
    // Update criteria in localstorage.
    this.setCriteriaInLocalstorage();
    // set z-index of (cdk_overlay_container) to 999.
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  /**
   * Close date-range dialog and display the selected dates.
   */
  dateRangeClosed(): void {
    if (this.startDate && this.endDate) {
      // Format the date label.
      this.isDisplayDateLabel = false;
      this.startDateLabel = this.formatDate(this.startDate);
      this.endDateLabel = this.formatDate(this.endDate);
    } else {
      this.isDisplayDateLabel = true;
      this.startDateLabel = '';
      this.endDateLabel = '';
    }

    // set z-index of (cdk_overlay_container) to default.
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  /**
   * Validate start, end date and emit data to parent component.
   */
  emitDateRangeValue(): void {
    // Set format date to form if start and end date is undefined.
    if ((this.startDate === undefined && this.endDate === undefined) ||
      (!this.startDate && !this.endDate && this.dateRangeType === 'viewDocumentShipment' && !this.resetShipment)) {
      this.startDate = this.endDate = formatDateToRequest(new Date());
      this.range.setValue({start: new Date(), end: new Date()});
    }

    // Emit date to the parent component.
    this.dateRangeChange.emit({
      startDate: this.startDate,
      endDate: this.endDate,
    });
  }

  /**
   * Reset date-range base on type of form.
   *
   *  - flow-traceability
   *  - flow-document
   *  - flow-document-shipment
   *
   *  If type of form is not flow-document-shipment,
   *  set date with the last 7 days date-range.
   */
  resetDateRange() {
    if (this.dateRangeType !== 'viewDocumentShipment') {
      // Reset form.
      this.range.reset();
      // Set date with the last 7 days date-range.
      this.initialLast7DaysInForm();
    } else {
      this.resetDateRangeForViewShipment();
    }

    // Hide the display date label.
    this.isDisplayDateLabel = false;
    this.usingDateFilter = false;
    // Update criteria in localstorage.
    this.setCriteriaInLocalstorage();
  }

  formatDate(date: string): string {
    return moment(date).format('D MMM').replace('.', '');
  }

  /**
   * Initial last 7 days in date-range form.
   */
  initialLast7DaysInForm(): void {
    const last7Days = getDateRangeLast7Days();
    this.range.setValue({ start: last7Days.startDate, end: last7Days.endDate });
    this.dateRangeChanged();
    this.dateRangeChange$.next(true);
  }

  /**
   * Reset date-range for view shipment.
   */
  resetDateRangeForViewShipment(): void {
    this.range.reset();
    this.startDate = '';
    this.endDate = '';
    this.resetShipment = true;
    this.dateRangeClosed();
    this.dateRangeChange$.next(true);
    this.setCriteriaInLocalstorage();
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  /**
   * Add custom class to modify material UI.
   */
  addCustomCssClass() {
    document
      .querySelector('mat-datepicker-content.mat-datepicker-content')
      ?.classList?.add(this.customCssClass);
  }

  /**
   * Select the selected date.
   */
  onApply(): void {
    this.resetShipment = false;
    this.dateRangeChange$.next(true);
    this.setCriteriaInLocalstorage();
    this.usingDateFilter = true;
  }

  /**
   * Set criteria in localstorage base of type of form.
   *
   * Type of form:
   *  - flow-traceability
   *  - flow-document
   *  - flow-document-shipment
   */
  setCriteriaInLocalstorage(): void {
    if (this.dateRangeType == 'flowTraceability') {
      // Set criteria in localstorage if type of form is flow-traceability.
      this.storeDateRangeFlowTraceability(this.startDate, this.endDate);
    } else if (this.dateRangeType === 'flowDocument') {
      // Set criteria in localstorage if type of form is flow-document.
      this.storeDateRangeFlowDocument(this.startDate, this.endDate);
    } else if (this.dateRangeType === 'viewDocumentShipment') {
      // Set criteria in localstorage if type of form is flow-document-shipment.
      this.storeDateRangeFlowDocumentShipment(this.startDate, this.endDate);
    }
  }

  /**
   * Set date-range criteria in localstorage.
   * @param startDate refers to the start date.
   * @param endDate refers to the end date.
   */
  storeDateRangeFlowDocument(startDate: string, endDate: string): void {
    const state = this.storageService.getDocumentCriteria();
    const data = {
      ...state,
      criteriaParams: {
        ...state.criteriaParams,
        startDate,
        endDate,
        isDisplayLabel: this.isDisplayDateLabel,
      },
    } as CriteriaStorage;
    this.storageService.setDocumentCriteria(data);
  }

  storeDateRangeFlowDocumentShipment(startDate: string, endDate: string): void {

    const state = this.storageService.getDocumentShipmentCriteria();

    const data = {
      ...state,
      criteriaParams: {
        ...state.criteriaParams,
        startDate,
        endDate,
        isDisplayLabel: this.isDisplayDateLabel,
      },
    } as CriteriaStorage;
    this.storageService.setDocumentShipmentCriteria(data);
  }

  /**
   * Set the date-range criteria in localstorage.
   *
   * @param startDate refers to the start date
   * @param endDate refers to the end date
   */
  storeDateRangeFlowTraceability(startDate: string, endDate: string): void {
    const state = this.storageService.getFlowCriteria();
    const data = {
      ...state,
      criteriaParams: {
        ...state.criteriaParams,
        startDate,
        endDate,
        isDisplayLabel: this.isDisplayDateLabel,
      },
    } as CriteriaStorage;
    this.storageService.setFlowCriteria(data);
  }

  /**
   * Initial the criteria from localstorage base on type of form.
   *
   * Type of form:
   *  - flow-traceability
   *  - flow-document.
   *  - flow-document-shipment.
   *
   *  If the criteria in localstorage did not initial, set the last 7 days
   *  date-range as default except type of form is flow-document-shipment.
   */
  initCriteria(): void {
    // Get the criteria from localstorage base on type of form.
    const criteriaFromLocalstorage = this.getInitialCriteria(this.dateRangeType);

    this.isDisplayDateLabel = Boolean(criteriaFromLocalstorage.criteriaParams?.isDisplayLabel);

    if (
      criteriaFromLocalstorage &&
      criteriaFromLocalstorage?.criteriaParams?.startDate &&
      criteriaFromLocalstorage?.criteriaParams?.endDate
    ) {

      // Set criteria to date-range form.
      this.setCriteriaToForm(criteriaFromLocalstorage);
      this.usingDateFilter = Boolean(criteriaFromLocalstorage.criteriaParams?.isDisplayLabel);
    } else if (this.dateRangeType !== 'viewDocumentShipment') {
      // Set last 7 days date-range as default except type of form is flow-document-shipment.
      this.initialLast7DaysInForm();
    }

    this.updateLabel();
  }

  getInitialCriteria(dateRangeType: DateRangeType): CriteriaStorage {
    switch (dateRangeType) {
      case 'flowDocument': return this.storageService.getDocumentCriteria();
      case 'viewDocumentShipment': return this.storageService.getDocumentShipmentCriteria();
      case 'flowTraceability': return this.storageService.getFlowCriteria();
    }
  }

  /**
   * Set criteria to date-range form.
   * @param criteriaFromLocalstorage refers to {@link CriteriaStorage}
   */
  setCriteriaToForm(criteriaFromLocalstorage: CriteriaStorage): void {
    // Set the criteria to date-range form.
    this.range.setValue({
      start: new Date(criteriaFromLocalstorage.criteriaParams?.startDate || ''),
      end: new Date(criteriaFromLocalstorage.criteriaParams?.endDate || ''),
    });

    // Change new date-range value in form and save it in localstorage.
    this.dateRangeChanged();
    // Emit value to the parent component.
    this.dateRangeChange$.next(true);
    // Close date-range dialog.
    this.dateRangeClosed();
  }

  updateLabel() {
    this.isDisplayDateLabel = true;

    if(this.dateRangeType !== 'viewDocumentShipment') {
      this.isDisplayDateLabel = false;
      this.startDateLabel = this.formatDate(this.startDate);
      this.endDateLabel = this.formatDate(this.endDate);
    } else {
      if(this.startDate != undefined && this.endDate !== undefined) {
        this.isDisplayDateLabel = false;
      }
    }
  }
}
