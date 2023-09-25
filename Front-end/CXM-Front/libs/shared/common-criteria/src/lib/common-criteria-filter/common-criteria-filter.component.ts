import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import {
  formatDateToRequest,
  getDateRangeLast7Days,
} from '@cxm-smartflow/flow-traceability/data-access';
import * as moment from 'moment';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CxmDatetimeHeaderComponent } from './cxm-datetime-header.component';
import { CustomAngularMaterialUtil, Sort } from '@cxm-smartflow/shared/utils';

export interface IEspaceListFilterConfig {
  category: string[];
  users: any[];
}

export interface ICriteriaFiltering {
  users: string[];
  categories: string[]
  start: string;
  end: string;
  resetType?: CommonFilterCriteria;
  [key: string]: any;
}

export interface DateCriteriaType {
  start: string;
  end: string;
}

export enum CommonFilterCriteria {
  "USER",
  "DATE_PICKER",
  "CATEGORY"
}

export interface RestoreCriteriaState {
  page?: number
  pageSize?: number
  filter?: string
  sortByField?: string
  sortDirection?: string
  start?: string
  end?: string
  categories?: string[]
  isDateChange?: boolean
  channels?: string[]
  users?: User[]
  [key: string]: any;
}

export interface User {
  id?: number
  username?: string
  firstName?: string
  lastName?: string
  email?: string
  technicalRef?: string
  key?: string
}

@Component({
  selector: 'cxm-smartflow-common-criteria-filter-component',
  templateUrl: './common-criteria-filter.component.html',
  styleUrls: ['./common-criteria-filter.component.scss']
})
export class CommonCriteriaFilterComponent implements OnInit, OnChanges, OnDestroy  {

  @Input() config: IEspaceListFilterConfig;
  @Output() criteriaChanged = new EventEmitter<ICriteriaFiltering>();
  @Input() restoreFilterCriteria: RestoreCriteriaState;
  @Input() dateCriteria: DateCriteriaType;
  @Output() resetCriteria = new EventEmitter<CommonFilterCriteria>();

  private _initialized = false;

  public _useUserFilter = false;
  public _useChannelFilter = false;
  public _useDateFilter = true;

  destroyed$ = new Subject<boolean>();
  users: any[] = [];
  allUsers: any[] = [];

  category = [];
  postalUI = [];
  digitalUI = [];

  checkPostal = false;
  checkDigital = false;

  channelFilterForm: FormGroup;
  channelForm: FormGroup;
  datetimeForm: FormGroup;
  usersForm: FormGroup;

  userDirections: Sort.ASC | Sort.DESC = Sort.DESC;

  customCxmDatetimeHeaderComponent = CxmDatetimeHeaderComponent;

  startDateLabel = '';
  endDateLabel = '';
  isDateChange = false;
  isOpening = false;

  _filters: any;

  private setUpChannelForm() {
    const { category } = this.config;

    const { digital, multiple, postal } = this.aggregateCategory(category);
    Object.assign(this, { digitalUI: digital , postalUI: postal, category: multiple });

    this.generateCategoryCheckbox();

    // tracking changed
    this.channelFilterForm.valueChanges.pipe(takeUntil(this.destroyed$)).subscribe(form => {
      Object.assign(this, { checkPostal: form.postal, checkDigital: form.digital  });
      this.channelForm.reset({ }, { emitEvent: true });
    })
  }

  restFilter() {
    this.channelFilterForm.reset();
    this.checkDigital = false;
    this.checkPostal = false;
    this._filters = {...this._filters, resetType: CommonFilterCriteria.CATEGORY};
  }

  resetCalendar() {
    this.isDateChange = false;
    this._filters = {...this._filters, resetType: CommonFilterCriteria.DATE_PICKER};
    this.resetCriteria.emit(CommonFilterCriteria.DATE_PICKER);
  }

  applyCalendar() {
    let selectedDates = this.datetimeForm.getRawValue();
    selectedDates = this.prepareDatetime(selectedDates);
    this.startDateLabel = this.replaceDateLabel(selectedDates.start);
    this.endDateLabel = this.replaceDateLabel(selectedDates.end);
    this.isDateChange = true;
    this.datetimeForm.setValue(selectedDates, { emitEvent: false, onlySelf: true });

    this._filters = { ...this._filters, start: selectedDates.start, end: selectedDates.end }
    this.aggregateFilterCriteria({ ...this._filters, resetType: CommonFilterCriteria.DATE_PICKER });
  }

  initializeFormValue() {
    if (this._initialized) return;
    // Check if should restore previous filter
    const restoredFilter = this.restoreFilterCriteria;

    if (restoredFilter) {
      const {
        users,
        start,
        end,
        categories,
        isDateChange,
        channels,
      } = restoredFilter;

      // Fill postal and digital
      if (channels) {
        const channelFormChecked = Array.from(channels).reduce<any>(
          (prev: any, cur: any) => {
            if (cur === 'Digital')
              return Object.assign(prev, { digital: true });
            if (cur === 'Postal') return Object.assign(prev, { postal: true });
            return prev;
          },
          { digital: false, postal: false }
        );
        this.channelFilterForm.patchValue(channelFormChecked, {
          emitEvent: false,
        });
      }

      if (categories) {
        // Fill channel checkbox
        const channelCheckedBox = categories
          ?.map((x: any) => ({ [x]: true }))
          .reduce((prev: any, cur: any) => Object.assign(prev, cur), {});
        this.channelForm.patchValue(channelCheckedBox, { emitEvent: false });
      }

      if (users) {
        // Fill users checkbox
        const userCheckBox = users
          ?.map((x: any) => ({ [x.id]: true }))
          .reduce((prev: any, cur: any) => Object.assign(prev, cur), {});
        this.usersForm.patchValue(userCheckBox, { emitEvent: false });
      }

      // Update date time label
      const defaultRange = getDateRangeLast7Days(true);
      const dateRange = {
        start: start || defaultRange.startDate.toString(),
        end: end || defaultRange.endDate.toString(),
      };
      this.datetimeForm.patchValue(dateRange, { emitEvent: false });
      this.startDateLabel = this.replaceDateLabel(dateRange.start);
      this.endDateLabel = this.replaceDateLabel(dateRange.end);
      this.isDateChange = isDateChange || false;
    }
    this.updatelabel();
    this.shouldApplyHightligh(restoredFilter);
    this._initialized = true;
  }

  private updatelabel() {
    let selectedDates = this.datetimeForm.getRawValue();
    selectedDates = this.prepareDatetime(selectedDates);
    this.startDateLabel = this.replaceDateLabel(selectedDates.start);
    this.endDateLabel = this.replaceDateLabel(selectedDates.end);
  }

  private generateCategoryCheckbox() {
    this.category.forEach(x => this.channelForm.addControl(x, new FormControl(false), {emitEvent: false}));
  }


  private removeCategoryCheckbox(arr: Array<any>) {
    arr.forEach(x => this.channelForm.removeControl(x, { emitEvent: false }))
  }

  private initDatetimeForm() {
    if (Object.keys(this.restoreFilterCriteria).length > 0) {
      this._filters = { ...this.restoreFilterCriteria };
    }
    const defaultRange = getDateRangeLast7Days(true);
    const { start, end } = this.dateCriteria;

    const dateRange = {
      start: start || defaultRange.startDate.toString(),
      end: end || defaultRange.endDate.toString(),
    };
    this._filters = { ...this._filters, ...dateRange };
    this.datetimeForm.patchValue(dateRange, { emitEvent: true });
    this.startDateLabel = this.replaceDateLabel(dateRange.start);
    this.endDateLabel = this.replaceDateLabel(dateRange.end);
    this.criteriaChanged.emit(this._filters);
  }


  private setupUsersForm() {
    const { users } = this.config;
    Object.assign(this, { users });

    this.generateUsersCheckbox();
  }


  private generateUsersCheckbox() {
    this.users = this.filterUniqueUsers(this.users);
    this.users.forEach(x => {
      return this.usersForm.addControl(x.id, new FormControl(false), { emitEvent: false })
    });
  }

  filterUniqueUsers(users: any[]): any[] {
    const uniqueUsers = new Map<string, any>();
    this.allUsers = users;
    this.allUsers.forEach(user => {
      if (!uniqueUsers.has(user.username)) {
        uniqueUsers.set(user.username, user);
      }
    })
    return Array.from(uniqueUsers.values());
  }

  private removeUsersCheckbox(arr: Array<any>) {
    arr.forEach(x => this.usersForm.removeControl(x.id));
  }


  private aggregateFilterCriteria(filtering: any) {
    const { cat, usersChecked, resetType } = filtering;
    let criteria = { ...this._filters } as ICriteriaFiltering;
    if(cat) {
      criteria = { ...criteria, categories: Object.keys(cat).filter(x => cat[x] === true).map(x => x) }
    }

    if(usersChecked) {
      const checkedUsersID = Object.keys(usersChecked).filter(x => usersChecked[x] === true).map(x => parseInt(x));
      const chechedUsers = this.config.users.filter((x: any) => checkedUsersID.find((z: any) => x.id === z))
      criteria = { ...criteria, users: chechedUsers  }
    }

    const channels = [];

    if(this.checkDigital) {
      channels.push('Digital');
    }
    if(this.checkPostal) {
      channels.push('Postal');
    }

    delete criteria.dates;
    delete criteria.cat;
    delete criteria.usersChecked;

    const filterUpdated = {
      ...criteria,
      isDateChange: this.isDateChange,
      channels,
      resetType,
      start: formatDateToRequest(filtering.start) || this._filters.start,
      end: formatDateToRequest(filtering.end) || this._filters.end,
    };

    this.shouldApplyHightligh(filterUpdated)
    this.criteriaChanged.emit(filterUpdated);
  }

  private prepareDatetime(dates: any) {
    if(dates == null) return dates;

    const range = getDateRangeLast7Days();
    if(!dates.start || dates.start > range.endDate) {
      dates = { ...dates, start: range.endDate }
    }
    if(!dates.end) {
      dates = { ...dates, end:  range.endDate  }
    }

    return dates;
  }


  ngOnInit(): void {

    this.setUpChannelForm();
    this.setupUsersForm();
    this.initDatetimeForm();

    // restore data from local storage
    if (Object.keys(this.restoreFilterCriteria).length > 0) {
      this._filters = { ...this.restoreFilterCriteria };
      this.initializeFormValue();
    }


    this.channelForm.valueChanges
      .pipe(takeUntil(this.destroyed$))
      .subscribe((f) => {
        // check value of channel when categories changed
        this.checkPostal = this.channelFilterForm.get('postal')?.value;
        this.checkDigital = this.channelFilterForm.get('digital')?.value;

        this._filters = {
          ...this._filters,
          cat: f,
          resetType: CommonFilterCriteria.CATEGORY,
        };
        this.aggregateFilterCriteria(this._filters);
      });

    this.usersForm.valueChanges
      .pipe(takeUntil(this.destroyed$))
      .subscribe((userSelectCheckedBox) => {
        userSelectCheckedBox = this.getRelateUserById(userSelectCheckedBox);
        this._filters = {
          ...this._filters,
          usersChecked: userSelectCheckedBox,
          resetType: CommonFilterCriteria.USER,
        };
        this.aggregateFilterCriteria(this._filters);
      });
  }

  getRelateUserById(userSelectCheckbox: any): any {
    const userCheckboxIds = Object.keys(userSelectCheckbox)
      .filter((key) => userSelectCheckbox[key])
      .map((key) => Number(key));
    const userNames = this.allUsers
      .filter((user) => userCheckboxIds.includes(user.id))
      .map((user) => user.username);
    const usersIds = this.allUsers
      .filter((user) => userNames.includes(user.username))
      .map((user) => user.id);
    usersIds
      .map((id) => ({ [id]: true }))
      .forEach((id) => {
        userSelectCheckbox = Object.assign(userSelectCheckbox, id);
      });
    return userSelectCheckbox;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.dateCriteria && !changes.dateCriteria.isFirstChange()) {
      this.initDatetimeForm();
    }
  }

  private aggregateCategory(arr: any) {
    // Simplify category model

    const a = arr.find((x: any) => x.key === 'flow.traceability.sub-channel.postal');
    const b = arr.find((x: any) => x.key === 'flow.traceability.sub-channel.digital');
    const c = arr.find((x: any) => x.key === 'flow.traceability.sub-channel.multiple');

    return {
      postal: a ? a.value.split(',') : [],
      digital: b ? b.value.split(',') : [],
      multiple: c ? c.value.split(',') : []
    }
  }

  onChangeDirection() {
    if (this.userDirections === Sort.ASC) this.userDirections = Sort.DESC;
    else this.userDirections = Sort.ASC;

    this.sortUsers(this.userDirections);
  }

  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  ngOnDestroy(): void {
    this.destroyed$.complete();
    this.destroyed$.next(false);

  }

  private replaceDateLabel(date: string): string {
    return moment(date).format('D MMM').replace('.', '');
  }

  private shouldApplyHightligh(filters: any) {
    if(!filters) return;

    this._useChannelFilter = (filters.channels && filters.channels.length > 0) ||
                              (filters.categories && filters.categories.length > 0);

    // this._useDateFilter = filters.isDateChange;

    this._useUserFilter = filters.users && filters.users.length > 0;

  }

  resetUserFilter(): void {
    this.userDirections = Sort.DESC;
    this.sortUsers(this.userDirections);
    this.usersForm.reset();
    this._filters = {...this._filters, resetType: CommonFilterCriteria.USER}
  }

  private sortUsers(direction: string) {
    this.users = this.users.map(x => ({...x, key: x.firstName + ' ' + x.lastName}))
      .sort((a, b) => {
        return (a.key < b.key ? -1 : 1) * (direction === Sort.ASC ? 1 : -1);
      });
  }

  constructor(private fb: FormBuilder) {
    this.datetimeForm = this.fb.group({
      start: new FormControl(),
      end: new FormControl()
    });

    this.channelFilterForm = this.fb.group({
      postal: new FormControl(false),
      digital: new FormControl(false)
    })

    this.channelForm = this.fb.group({ });

    this.usersForm = this.fb.group({ });
  }
}
