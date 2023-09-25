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
import { FormBuilder } from '@angular/forms';
import {
  CalendarOptionModel,
  ChannelCategoryFilterModel,
  FillerGroupsModel,
  FilterCriteriaModel,
  FilterListModel,
  FilterOptionModel,
} from '@cxm-smartflow/analytics/data-access';
import { filterHistoryManager } from '@cxm-smartflow/analytics/util';

declare type ReportFilterOption = 'channel-category' | 'category' | 'date-picker' | 'fillers' | 'fillers-group';


@Component({
  selector: 'cxm-smartflow-report-space-filtering',
  templateUrl: './report-space-filtering.component.html',
  styleUrls: ['./report-space-filtering.component.scss'],
})
export class ReportSpaceFilteringComponent implements OnInit, OnChanges, OnDestroy {

  @Input() config: FilterCriteriaModel | null;
  @Input() disabledChannel: string[];
  @Input() disabledCategory: string[];
  @Input() fillers: FilterListModel[] = [];
  @Input() type: 'global' | 'Postal' | 'SMS' | 'Email' = 'global';
  @Input() visibilityItem: ReportFilterOption[] = []
  @Output() criteriaOptionChange = new EventEmitter<FilterOptionModel>();

  // Filter selected.
  filterCalendarSelected: CalendarOptionModel;
  filterFillersSelected: string[] = [];
  filterChannelSelected: string[] = [];
  filterCategoriesSelected: string[] = [];
  filterStandAloneCategorySelected: string[] = [];
  fillersSearchTermInput = '';
  fillersCalendarOption: CalendarOptionModel;
  fillerGroup: FillerGroupsModel[] = [];
  fillersGroupSelected: FillerGroupsModel[]

  configurationProperties: FilterOptionModel;

  constructor(private _formBuilder: FormBuilder) {}

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  ngOnInit(): void {
    // Init.
    this._restoreFilterHistory();
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Simple changes.
    this.fillerGroup = Array.from({ length: 3 }, () => ({
      fillerName: '---',
      fillerSearchTerms: '',
      fillerDisabledItems: [],
      fillerHiddenItems: [],
      fillerSelectedItem: '',
      isDisabled: false,
      fillerItems: this.fillers || [],
    }));

  }

  ngOnDestroy(): void {
    // Destroy.
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  addCustomCssClass(): void {
    document
      .querySelector('.common-filter-menu-panel')
      ?.classList.add(`cxm-flow-traceability--list`);
  }

  channelCategoryChanged(channelCategoryFilter: ChannelCategoryFilterModel): void {
    this.configurationProperties = {
      ...this.configurationProperties,
      categories: channelCategoryFilter.categories,
      channels: channelCategoryFilter.channels,
    };

    this._storeFilterHistoryAndEmitValue();
  }

  fillersChange(fillers: string[]): void {
    this.configurationProperties = {
      ...this.configurationProperties,
      fillers,
    }

    filterHistoryManager.storeFilterHistory(this.configurationProperties, this.type);
    if (this.configurationProperties.fillerSearchTerm?.length) {
      this.criteriaOptionChange.emit(this.configurationProperties);
    }
  }

  fillerSearchTermChange(fillerSearchTerm: string) {
    this.configurationProperties = {
      ...this.configurationProperties,
      fillerSearchTerm,
    }

    this._storeFilterHistoryAndEmitValue();
  }


  calendarLevelChange(calendarOption: CalendarOptionModel): void {
    this.configurationProperties = {
      ...this.configurationProperties,
      calendar: {
        startDate: calendarOption.startDate,
        endDate: calendarOption.endDate,
        option: calendarOption.option,
      },
    };

    this._storeFilterHistoryAndEmitValue();
  }

  categoryChange(categories: string[]): void {
    this.configurationProperties = {
      ...this.configurationProperties,
      standAloneCategory: categories,
    };

    this._storeFilterHistoryAndEmitValue();
  }

  fillersGroupChanges(fillersGroup: { data: FillerGroupsModel[], fillerIndex: number, filerBySearch: boolean }): void {
    this.configurationProperties = {
      ...this.configurationProperties,
      fillersGroup: fillersGroup.data,
    };

    filterHistoryManager.storeFilterHistory(this.configurationProperties, this.type);

    if (fillersGroup.filerBySearch) {

      if (this.configurationProperties.fillersGroup[fillersGroup.fillerIndex].fillerSelectedItem) {
        this.criteriaOptionChange.emit(this.configurationProperties);
      }

    } else {
      this.criteriaOptionChange.emit(this.configurationProperties);
    }

  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------


  private _storeFilterHistoryAndEmitValue(): void {
    filterHistoryManager.storeFilterHistory(this.configurationProperties, this.type);
    this.criteriaOptionChange.emit(this.configurationProperties);
  }


  private _restoreFilterHistory(): void {
    const filterHistory = filterHistoryManager.shouldRestoreFilterHistory(this.type);


    if (filterHistory) {
      this.configurationProperties = filterHistory;
      this.filterCalendarSelected = filterHistory.calendar;
      this.filterFillersSelected = filterHistory.fillers;
      this.filterChannelSelected = filterHistory.channels;
      this.filterCategoriesSelected = filterHistory.categories;
      this.fillersSearchTermInput = filterHistory.fillerSearchTerm;
      this.fillersCalendarOption = filterHistory.calendar;
      this.filterStandAloneCategorySelected = filterHistory.standAloneCategory;
      this.fillersGroupSelected = filterHistory.fillersGroup;
    }
  }
}
