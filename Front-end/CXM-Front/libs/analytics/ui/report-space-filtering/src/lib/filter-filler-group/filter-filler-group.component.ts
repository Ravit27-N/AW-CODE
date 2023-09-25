import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FillerGroupsModel, FilterListModel } from '@cxm-smartflow/analytics/data-access';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-filter-filler-group',
  templateUrl: './filter-filler-group.component.html',
  styleUrls: ['./filter-filler-group.component.scss'],
})
export class FilterFillerGroupComponent implements OnInit, OnChanges {


  @Input() fillerGroup: FillerGroupsModel[] = [];
  @Input() fillers: FilterListModel[] = [];
  @Input() fillerGroupSelected: FillerGroupsModel[] = [];


  @Output() fillerGroupChanges = new EventEmitter<{
    data: FillerGroupsModel[],
    fillerIndex: number,
    filerBySearch: boolean,
  }>();


  fillerGroupDisplay: FillerGroupsModel[] = [];


  constructor(private _snackbar: SnackBarService, private _translate: TranslateService) {}


  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------


  ngOnInit(): void {}


  ngOnChanges(changes: SimpleChanges): void {
    this.fillerGroupDisplay = this.fillerGroup.reduce((prev: FillerGroupsModel[], curr: FillerGroupsModel, index) => {
      if (this.fillerGroupSelected) {
        const selectedGroup = this.fillerGroupSelected[prev.length];
        curr.fillerSelectedItem = selectedGroup?.fillerSelectedItem || '';
        curr.fillerDisabledItems = selectedGroup?.fillerDisabledItems || [];
        curr.fillerHiddenItems = selectedGroup?.fillerHiddenItems || [];
        curr.fillerSearchTerms = selectedGroup?.fillerSearchTerms || '';
        curr.fillerName = selectedGroup?.fillerName || '---';
      }


      curr.isDisabled = index === 0? false : prev[0]?.isDisabled || !prev[0]?.fillerSelectedItem;
      prev.push(curr);
      return prev;
    }, []);
  }


  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------


  filterBoxChanges(fillerIndex: number, selectedKey: { reset: boolean, selectedFiller: string }): void {
    this._updateFiller(fillerIndex, selectedKey);
    if (selectedKey.reset) {
      this._updateFiller(fillerIndex, selectedKey);
    }

    this.fillerGroupChanges.emit({ fillerIndex, data: this.fillerGroupDisplay, filerBySearch: false });
  }


  searchTermChange(fillerIndex: number, searchTerm: string): void {
    this.fillerGroupDisplay = this.fillerGroupDisplay.map((item, itemIndex) => {
      if (itemIndex === fillerIndex) {
        return { ...item, fillerSearchTerms: searchTerm };
      }
      return item;
    });

    this.fillerGroupChanges.emit({ fillerIndex, data: this.fillerGroupDisplay, filerBySearch: true });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  private _updateFiller(fillerIndex: number, fillerSelectedItem: { reset: boolean; selectedFiller: string }): void {
    this.fillerGroupDisplay = this.fillerGroupDisplay.reduce((prev: FillerGroupsModel[], curr: FillerGroupsModel, fillerGroupDisplayIndex) => {

      // Add selection key.
      if (fillerIndex === fillerGroupDisplayIndex) {
        curr = { ...curr, fillerSelectedItem: fillerSelectedItem.selectedFiller };
      }


      // latest fillerGroupDisplay.
      const latestFillerGroupDisplay = this.fillerGroupDisplay.map((latestFillerGroupDisplayItem, latestFillerGroupDisplayItemIndex) => {
        if (fillerIndex === latestFillerGroupDisplayItemIndex) {
          latestFillerGroupDisplayItem = { ...latestFillerGroupDisplayItem, fillerSelectedItem: fillerSelectedItem.selectedFiller };
        }

        return latestFillerGroupDisplayItem;
      });

      // Add disabled item.
      curr = { ...curr, isDisabled: this._getDisabled(latestFillerGroupDisplay, fillerGroupDisplayIndex) };

      // Add all disabled item.
      curr = { ...curr, fillerDisabledItems: this._getAllDisabledItem(latestFillerGroupDisplay, fillerGroupDisplayIndex) };

      // Add hidden item.
      curr = { ...curr, fillerHiddenItems: this._getHiddenItem(latestFillerGroupDisplay, fillerGroupDisplayIndex, fillerSelectedItem.selectedFiller, fillerIndex) };

      // Add filler item.
      curr = { ...curr, fillerName: this._getName(latestFillerGroupDisplay, fillerGroupDisplayIndex) };

      // Reset filler.
      if (!fillerSelectedItem.selectedFiller) {
        const isValid = fillerIndex >= fillerGroupDisplayIndex;

        curr = {
          ...curr,
          fillerHiddenItems: isValid? curr?.fillerHiddenItems : [],
          fillerDisabledItems: isValid? curr?.fillerDisabledItems : [],
          fillerSearchTerms: isValid? curr?.fillerSearchTerms : '',
          fillerSelectedItem: isValid? curr?.fillerSelectedItem : '',
          isDisabled: isValid? curr?.isDisabled : true,
          fillerName: isValid? curr?.fillerName : '---',
        };
      }

      prev.push(curr);
      return prev;
    }, []);
  }

  private _getHiddenItem(fillerGroupDisplay: FillerGroupsModel[], index: number, fillerValue: string, targetIndex: number): Array<string> {
    const selectedFiller1 = fillerGroupDisplay[0].fillerSelectedItem;
    const selectedFiller2 = fillerGroupDisplay[1].fillerSelectedItem;
    const selectedFiller3 = fillerGroupDisplay[2].fillerSelectedItem;

    switch (index) {
      case 0: {
        if (!fillerValue && targetIndex === 0) {
          return [];
        }

        return [selectedFiller2, selectedFiller3];
      }
      case 1: {
        if (!fillerValue) {
          return [selectedFiller1];
        }

        return [selectedFiller1, selectedFiller3];
      }
      case 2: {
        return [selectedFiller1, selectedFiller2];
      }
      default: {
        return [];
      }
    }
  }

  private _getDisabled(fillerGroupDisplay: FillerGroupsModel[], index: number): boolean {
    const selectedFiller1 = fillerGroupDisplay[0].fillerSelectedItem;
    const selectedFiller2 = fillerGroupDisplay[1].fillerSelectedItem;
    const selectedFiller3 = fillerGroupDisplay[2].fillerSelectedItem;

    switch (index) {
      case 0: {
        return false;
      }
      case 1: {
        return !selectedFiller1 && !selectedFiller2;
      }
      case 2: {
        return !selectedFiller2 && !selectedFiller3;
      }
      default: {
        return true;
      }
    }
  }

  private _getAllDisabledItem(fillerGroupDisplay: FillerGroupsModel[], index: number): Array<string> {
    const selectedFiller1 = fillerGroupDisplay[0].fillerSelectedItem;
    const selectedFiller2 = fillerGroupDisplay[1].fillerSelectedItem;
    const selectedFiller3 = fillerGroupDisplay[2].fillerSelectedItem;
    const filler = fillerGroupDisplay[0].fillerItems.map(item => item.key);

    switch (index) {
      case 0: {
        if (selectedFiller1) {
          return filler.filter(item => item !== selectedFiller1);
        }

        return [];
      }
      case 1: {
        if (selectedFiller2) {
          return filler.filter(item => item !== selectedFiller2);
        }

        return [selectedFiller1];
      }
      case 2: {
        if (selectedFiller3) {
          return filler.filter(item => item !== selectedFiller3);
        }

        return [selectedFiller1, selectedFiller2];
      }
      default: {
        return [];
      }
    }
  }

  private _getName(fillerGroupDisplay: FillerGroupsModel[], index: number): string {
    const selectedFiller1 = fillerGroupDisplay[0].fillerSelectedItem;
    const selectedFiller2 = fillerGroupDisplay[1].fillerSelectedItem;
    const selectedFiller3 = fillerGroupDisplay[2].fillerSelectedItem;

    const filler =  fillerGroupDisplay[0].fillerItems;

    switch (index) {
      case 0: {
        return selectedFiller1? filler.filter(item => item.key === selectedFiller1)[0].value: '---';
      }
      case 1: {
        return selectedFiller2? filler.filter(item => item.key === selectedFiller2)[0].value : '---';
      }
      case 2: {
        return selectedFiller3? filler.filter(item => item.key === selectedFiller3)[0].value : '---';
      }
      default: {
        return '---';
      }
    }
  }


  alertAnnounceMessage(isDisable: boolean): void {
    if (!isDisable) {
      return;
    }

    this._translate.get('cxm_analytics.you_mas_select_filler_in_order').toPromise().then(message => {
      this._snackbar.openCustomSnackbar({
        icon: 'close',
        message,
        type: 'error',
      });
    });
  }
}
