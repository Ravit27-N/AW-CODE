import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import {
  CriteriaCategoryFormModel,
  CriteriaDistributionFormModel,
  PreferencePayload,
} from '@cxm-smartflow/client/data-access';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'cxm-smartflow-distribute-criteria',
  templateUrl: './distribute-criteria.component.html',
  styleUrls: ['./distribute-criteria.component.scss'],
})
export class DistributeCriteriaComponent implements OnChanges {

  @Input() distributesCriteria: Array<CriteriaDistributionFormModel> = [];
  @Output() criteriaChanged = new EventEmitter<PreferencePayload>();
  @Output() manageDistributeCriteria = new EventEmitter<CriteriaDistributionFormModel>();
  formGroupCategory: FormGroup;
  datasource: Array<CriteriaDistributionFormModel> = [];

  constructor(private _formBuilder: FormBuilder) {
    this.formGroupCategory = _formBuilder.group({});
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.distributesCriteria.currentValue.length) {
      this._setupForm();
      this.datasource = changes.distributesCriteria.currentValue;
    }
  }

  private _setupForm(): void {
    this.distributesCriteria.forEach(distributeCriteria => {
      distributeCriteria.categories.forEach(category => {
        this.formGroupCategory.addControl(category.key,new FormControl({value: category.active, disabled: !category.enabled}), { emitEvent: false });
      });
    });
  }


  distributeCriteriaChange(active: boolean, distributeCriteria: CriteriaDistributionFormModel) {
    if (!distributeCriteria.enabled) {
      return;
    }

    this.datasource = this.datasource.map(data => data.key === distributeCriteria.key? { ...data, active, manageable: active && data.name === 'Digital' } : data);
    this.criteriaChanged.emit({ name: distributeCriteria.name, active });
  }

  categoryChanged(active: boolean, category: CriteriaCategoryFormModel) {
    if (!category.enabled) {
      return;
    }

    this.formGroupCategory.controls[`${category.key}`].patchValue(active);
    this.criteriaChanged.emit({ name: category.name, active });
  }

  manage(distributeCriteria: CriteriaDistributionFormModel) {
    if (!distributeCriteria.manageable) {
      return;
    }

    this.manageDistributeCriteria.emit(distributeCriteria);
  }
}
