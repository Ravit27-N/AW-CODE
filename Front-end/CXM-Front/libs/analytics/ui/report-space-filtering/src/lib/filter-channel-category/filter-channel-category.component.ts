import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ChannelCategoryFilterModel, CheckedBoxModel, KeyValue } from '@cxm-smartflow/analytics/data-access';
import { Subscription } from 'rxjs';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-filter-channel-category',
  templateUrl: './filter-channel-category.component.html',
  styleUrls: ['./filter-channel-category.component.scss'],
})
export class FilterChannelCategoryComponent implements OnInit, OnChanges, OnDestroy {

  @Input() subChannel: KeyValue[] = [];
  @Input() filterChannelSelected: string[] = [];
  @Input() filterCategorySelected: string[] = [];
  @Input() disabledChannel: string[];
  @Input() disabledCategory: string[];
  @Output() channelCategoryChange = new EventEmitter<ChannelCategoryFilterModel>();

  configurationProperties: ChannelCategoryFilterModel = {
    categories: [],
    channels: [],
  };

  channelFormGroup: FormGroup;
  categoryFormGroup: FormGroup;
  channelCategories: CheckedBoxModel[] = [];

  private _subscriptions = new Subscription();

  constructor(private _formBuilder: FormBuilder) {}

  // -----------------------------------------------------------------------------------------------------
  // @ Lifecycle hooks
  // -----------------------------------------------------------------------------------------------------

  ngOnInit(): void {
    // Init.
  }


  ngOnChanges(changes: SimpleChanges): void {
    this._subscriptions.unsubscribe();
    this._subscriptions = new Subscription();
    this._setupForm();
    this._adjustCategoryForm(changes);
    this._observeFormChange();
  }


  ngOnDestroy(): void {
    this._subscriptions.unsubscribe();
  }


  // -----------------------------------------------------------------------------------------------------
  // @ Access modifier
  // -----------------------------------------------------------------------------------------------------


  get channelHasFilter(): boolean {
    let categories = this.categoryFormGroup.getRawValue();
    let channels = this.channelFormGroup.getRawValue();
    categories = Object.keys(categories).filter(key => categories[key]);
    channels = Object.keys(channels).filter(key => channels[key]);
    return channels.length > 0 || categories.length > 0;
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------


  addCustomCssClass(): void {
    document
      .querySelector('.common-filter-menu-panel')
      ?.classList.add(`cxm-flow-traceability--list`);
  }


  mainMenuOpen(): void {
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }


  mainMenuClose(): void {
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }


  resetChannel(): void {
    this.channelFormGroup.patchValue({
      Postal: false,
      Digital: false,
    });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  private _setupForm(): void {
    this.channelFormGroup = this._createChannelFormGroup();
    this.categoryFormGroup = this._createCategoryFormGroup();
  }


  private _createChannelFormGroup(): FormGroup {
    return this._formBuilder.group({
      Postal: new FormControl({ value: this.filterChannelSelected?.includes('Postal'), disabled: this.disabledChannel.includes('Postal') }),
      Digital: new FormControl({ value: this.filterChannelSelected?.includes('Digital'), disabled: this.disabledChannel.includes('Digital') }),
    });
  }


  private _createCategoryFormGroup(): FormGroup {
    return this._formBuilder.group({});
  }


  private _adjustCategoryForm(changes?: SimpleChanges): void {
    let rawValue = this.channelFormGroup?.getRawValue();

    // If input properties changes.
    if (changes) {
      rawValue = {
        Postal: this.filterChannelSelected?.includes('Postal'),
        Digital: this.filterChannelSelected?.includes('Digital'),
      };
    }

    if (!this.subChannel || !rawValue) return;
    const channelKeys: string[] = [];

    // Check channel.
    if (rawValue.Postal && rawValue.Digital || !rawValue.Postal && !rawValue.Digital) {
      channelKeys.push('flow.traceability.sub-channel.postal', 'flow.traceability.sub-channel.digital');
    } else if (rawValue.Postal) {
      channelKeys.push('flow.traceability.sub-channel.postal');
    } else if (rawValue.Digital) {
      channelKeys.push('flow.traceability.sub-channel.digital');
    }


    // Map categories.
    this.channelCategories = this.subChannel
      .filter(category => channelKeys.includes(category.key))
      .reduce((prev, curr) => {
        const sub: any = curr.value
          .split(',')
          .map((item) => {
            // If input properties changes.
            if (changes) {
              return { name: item, checked: this.filterCategorySelected?.includes(item) };
            }

            return { name: item, checked: false };
          });
        return prev.concat(sub);
      }, []);

    // Reinitialize form.
    this.categoryFormGroup.reset({}, { emitEvent: false });
    this.categoryFormGroup.clearValidators();
    this.categoryFormGroup.controls = {};
    this.channelCategories.forEach((category) => {
      this.categoryFormGroup.addControl(category.name, new FormControl({ value: category.checked, disabled: this.disabledCategory.includes(category.name) }), { emitEvent: false });
    });

    this.categoryFormGroup.updateValueAndValidity();
  }


  private _observeFormChange(): void {
    this._observeChannelCategoryForm();
  }


  private _observeChannelCategoryForm(): void {
    // Observe the channel.
    this._subscriptions.add(
      this.channelFormGroup.valueChanges.subscribe(() => {
        this._adjustCategoryForm();
      })
    );


    // Observe the categories.
    this._subscriptions.add(
      this.categoryFormGroup.valueChanges.subscribe((rarValue) => {
        const categories = Object.keys(rarValue).filter((key) => rarValue[key]);
        const channels = Object.keys(
          this.channelFormGroup.getRawValue()
        ).filter((key) => this.channelFormGroup.getRawValue()[key]);
        this.configurationProperties = {
          categories,
          channels,
        };


        // Emit data to the smart component.
        this.channelCategoryChange.emit(this.configurationProperties);
      })
    );
  }
}
