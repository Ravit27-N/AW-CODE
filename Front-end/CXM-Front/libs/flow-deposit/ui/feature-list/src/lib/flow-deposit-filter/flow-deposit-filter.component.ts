import {
  AfterContentChecked,
  AfterViewInit, ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output
} from '@angular/core';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Subject } from 'rxjs';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import {
  loadAllUserByService,
  loadFilterCriteria,
  selectFilterCriterial,
  selectShowSearchBoxTooltip,
  selectUsers
} from '@cxm-smartflow/flow-deposit/data-access';
import { TranslateService } from '@ngx-translate/core';
import { FilterCriteria } from '@cxm-smartflow/shared/ui/dropdown-filter-criterial';
import { UserModel } from '@cxm-smartflow/shared/data-access/model';
import { CheckedListKeyValue } from '@cxm-smartflow/flow-traceability/data-access';

@Component({
  selector: 'cxm-smartflow-flow-deposit-filter',
  templateUrl: './flow-deposit-filter.component.html',
  styleUrls: ['./flow-deposit-filter.component.scss']
})
export class FlowDepositFilterComponent implements OnInit, OnDestroy, AfterViewInit, AfterContentChecked {

  @Input() isFlowCriteria = true;
  @Output() formFilterChange = new EventEmitter<{ filter: '', channels: [], subChannels: [], users: [], depositModes: [] }>();

  destroy$ = new Subject<boolean>();
  searchTerm$ = new BehaviorSubject<string>('');
  filterFormGroup: FormGroup;

  showTooltip$ = new BehaviorSubject(false);
  showTooltipBackground = false;

  messageLabel: any;

  filterCriteria: FilterCriteria = {};
  users: CheckedListKeyValue [] = [];

  usingChannelFilter = false;
  usingUserFilter = false;
  usingModeFilter = false;

  constructor(private store: Store, private fb: FormBuilder, private translate: TranslateService, private ref: ChangeDetectorRef) {
    this.filterFormGroup = this.fb.group({
      filter: new FormControl(''),
      channels: new FormControl([]),
      subChannels: new FormControl([]),
      users: new FormControl([]),
      depositModes: new FormControl([])
    });

    this.translate.get('flow.deposit.list.message').subscribe(response => this.messageLabel = response);
  }

  ngOnInit(): void {
    this.store.dispatch(loadFilterCriteria());
    this.store.dispatch(loadAllUserByService({ serviceId: 0 }));
  }

  onUsersChange($event: any) {
    this.filterFormGroup?.get('users')?.setValue($event);
  }

  onDepositModeChange(depositModes: string[] = []) {
    this.filterFormGroup?.get('depositModes')?.setValue(depositModes);
  }

  searchTermChanged(searchTerm: any): void {
    this.searchTerm$.next(searchTerm);
  }

  channelWithSubChannelEventChange($event: any) {
    const { channels, subChannels } = $event;
    this.filterFormGroup.patchValue({ 'channels': channels, 'subChannels': subChannels });
  }

  get tooltipMessage() {
    return this.messageLabel?.notFound;
  }

  ngOnDestroy(): void {
    this.formFilterChange.complete();
    this.searchTerm$.complete();
    this.store.complete();
    this.destroy$.complete();
  }

  ngAfterViewInit(): void {
    this.store.select(selectFilterCriterial).pipe(takeUntil(this.destroy$))
      .subscribe(response => this.filterCriteria = response);

    this.store.select(selectUsers).pipe(takeUntil(this.destroy$))
      .subscribe((response: UserModel[]) => {
        this.users = response.map((user: UserModel) => ({
          value: user.id+'',
          key: user.firstName + ' ' + user.lastName,
          other: user.username,
          checked: false
        }));
      });

    this.searchTerm$
      .pipe(distinctUntilChanged(), debounceTime(800))
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => {
        this.filterFormGroup?.get('filter')?.setValue(value)
      });

    this.filterFormGroup.valueChanges.subscribe(formValue => {
      this.formFilterChange.emit(formValue);
      this.applyFilterHightligh(formValue);
    });

    this.store.select(selectShowSearchBoxTooltip).pipe(takeUntil(this.destroy$))
      .subscribe(response => this.showTooltip$.next(response));
  }

  ngAfterContentChecked(): void {
    this.ref.detectChanges();
  }

  private applyFilterHightligh(filters: any) {

    if(!filters) return;

    this.usingChannelFilter = (filters.channels && filters.channels.length > 0) ||
                              (filters.subChannels && filters.subChannels.length > 0);

    this.usingUserFilter = (filters.users && filters.users.length > 0);

    this.usingModeFilter = (filters.depositModes && filters.depositModes.length > 0);
  }
}
