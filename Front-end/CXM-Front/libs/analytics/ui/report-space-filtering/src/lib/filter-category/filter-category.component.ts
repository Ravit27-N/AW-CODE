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
import {
  CheckedBoxModel,
  KeyValue,
} from '@cxm-smartflow/analytics/data-access';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Subscription } from 'rxjs';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-filter-category',
  templateUrl: './filter-category.component.html',
  styleUrls: ['./filter-category.component.scss'],
})
export class FilterCategoryComponent implements OnInit, OnChanges, OnDestroy {
  @Input() subChannel: KeyValue[] = [];
  @Input() filterCategorySelected: string[] = [];
  @Input() channelKeys: string[] = ['flow.traceability.sub-channel.postal'];
  @Output() channelCategoryChange = new EventEmitter<string[]>();

  configurationProperties: string[] = [];

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
    categories = Object.keys(categories).filter((key) => categories[key]);
    return categories.length > 0;
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Public methods
  // -----------------------------------------------------------------------------------------------------

  addCustomCssClass(): void {
    document.querySelector('.common-filter-menu-panel')?.classList.add(`cxm-flow-traceability--list`);
  }

  mainMenuOpen(): void {
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(): void {
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Private methods
  // -----------------------------------------------------------------------------------------------------

  private _setupForm(): void {
    this.categoryFormGroup = this._createCategoryFormGroup();
  }

  private _createCategoryFormGroup(): FormGroup {
    return this._formBuilder.group({});
  }

  private _adjustCategoryForm(changes?: SimpleChanges): void {
    // Map categories.
    this.channelCategories = this.subChannel
      .filter(category => this.channelKeys.includes(category.key))
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
      this.categoryFormGroup.addControl(
        category.name,
        new FormControl(category.checked),
        { emitEvent: false }
      );
    });

    this.categoryFormGroup.updateValueAndValidity();
  }

  private _observeFormChange(): void {
    this._observeChannelCategoryForm();
  }

  private _observeChannelCategoryForm(): void {
    // Observe the categories.
    this._subscriptions.add(
      this.categoryFormGroup.valueChanges.subscribe((rarValue) => {
        this.configurationProperties = Object.keys(rarValue).filter((key) => rarValue[key]);

        // Emit data to the smart component.
        this.channelCategoryChange.emit(this.configurationProperties);
      })
    );
  }
}
