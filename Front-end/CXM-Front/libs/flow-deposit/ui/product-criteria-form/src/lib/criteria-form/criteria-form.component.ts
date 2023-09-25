import { ChangeDetectionStrategy, Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import { BehaviorSubject } from 'rxjs';
import { DefaultConfiguration, selectProductionForm } from '@cxm-smartflow/flow-deposit/data-access';

interface PostageModel {
  value: string;
  key: string;
  selected?: boolean;
}

@Component({
  selector: 'cxm-smartflow-criteria-form',
  templateUrl: './criteria-form.component.html',
  styleUrls: ['./criteria-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CriteriaFormComponent implements OnInit, OnDestroy, OnChanges {

  @Input() defaultConfiguration: DefaultConfiguration = {};

  criteriaForm: FormGroup;

  showMore = false;
  enclosedList = [1];
  selectedValue: string;
  production = new BehaviorSubject<any>({});

  postageModels: PostageModel[] = [
    { value: 'Ecopli', key: 'ecopli', selected: false },
    { value: 'Lettre', key: 'letter', selected: false },
    { value: 'R1 avec AR', key: 'registerLetterWithAR', selected: false },
    { value: '', key: 'registerLetterWithoutAR', selected: false }
  ];

  postageHasDefaultConfig = false;
  colorHasDefaultConfig = false;
  rectoHasDefaultConfig = false;
  wrapHasDefaultConfig = false;
  hasHasAppliedDefaultConfig = false;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.defaultConfiguration) {
      this.store.select(selectProductionForm).subscribe((productionFormState: any) => {
        let urgency = 'Lettre';
        const urgencyDefaultConfig = this.postageModels.map(item => item.value);
        if (productionFormState?.Urgency && urgencyDefaultConfig.some(item => item === productionFormState?.Urgency)) {
          urgency = productionFormState?.Urgency;
        }

        let color = '0';
        const colorDefaultConfig = ['0', '1'];
        if (productionFormState?.Color && colorDefaultConfig.some(item => item === productionFormState?.Color)) {
          color = productionFormState?.Color;
        }

        let recto = 'R';
        const rectoDefaultConfig = ['R', 'RV'];
        if (productionFormState?.Recto && rectoDefaultConfig.some(item => item === productionFormState?.Recto)) {
          recto = productionFormState?.Recto;
        }

        let wrap = 'Auto';
        const wrapDefaultConfig = ['Auto', 'C4', 'C5', 'C6'];
        if(productionFormState?.Wrap && wrapDefaultConfig.some(item => item === productionFormState?.Wrap)){
          wrap = productionFormState?.Wrap;
        }

        this.criteriaForm.patchValue({
            urgency: urgency,
            color: color,
            recto: recto,
            wrap: wrap
          },
          { onlySelf: true, emitEvent: true });
      });
    }
  }

  ngOnInit(): void {
    // Subscribe criteria from to prefill color default configuration.
    this.criteriaForm.valueChanges.subscribe(formValue => {
      // Postage configuration color.
      this.postageHasDefaultConfig = formValue?.urgency === this.defaultConfiguration.Urgency;
      this.postageModels.filter(item => item.value === this.defaultConfiguration.Urgency)
        .forEach(item => item.selected = true);

      // Color configuration.
      this.colorHasDefaultConfig = formValue?.color === this.defaultConfiguration.Color;

      // Recto configuration color.
      this.rectoHasDefaultConfig = formValue?.recto === this.defaultConfiguration.Recto;

      // Wrap configuration color.
      this.wrapHasDefaultConfig = formValue?.wrap === this.defaultConfiguration.Wrap;

      this.hasHasAppliedDefaultConfig = (this.postageHasDefaultConfig || this.colorHasDefaultConfig
        || this.rectoHasDefaultConfig || this.wrapHasDefaultConfig);
    });
  }

  addEnclosed() {
    this.enclosedList.push(this.enclosedList.length);
  }

  constructor(private fb: FormBuilder, private store: Store) {
    this.criteriaForm = this.fb.group({
      urgency: new FormControl(''),
      color: new FormControl(''),
      recto: new FormControl(''),
      wrap: new FormControl(''),
      leaflet: new FormControl({ value: false, disabled: true }),
      signature: new FormControl(''),
      background: new FormControl(''),
      enclosed: new FormControl(''),
      countryCode: new FormControl(''),
      zipCode: new FormControl('')
    });
  }

  ngOnDestroy(): void {
    this.store?.complete();
  }
}
