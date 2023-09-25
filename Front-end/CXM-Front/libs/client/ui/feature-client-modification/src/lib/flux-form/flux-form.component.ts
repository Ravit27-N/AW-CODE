import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import {
  DepositModesConstant,
  IdentificationModesConstant,
  IClientDepositModePayload,
  IDepositModeForm,
  IdentificationModeForm, CriteriaDistributionFormModel, PreferencePayload
} from '@cxm-smartflow/client/data-access';
import { Router } from '@angular/router';

@Component({
  selector: 'cxm-smartflow-flux-form',
  templateUrl: './flux-form.component.html',
  styleUrls: ['./flux-form.component.scss'],
})
export class FluxFormComponent implements OnInit, OnChanges {
  @Input() fluxDepositModes: Array<IDepositModeForm> = [];
  @Input() distributesCriteria: Array<CriteriaDistributionFormModel> = [];
  @Input() fluxIdentificationMode = false;
  @Output() valueChanged = new EventEmitter<Array<IClientDepositModePayload>>();
  @Output() configurationFileChanged = new EventEmitter<boolean>();
  @Output() manageConfigurationChanged = new EventEmitter<boolean>();
  @Output() criteriaChange = new EventEmitter<PreferencePayload>();
  @Output() manageDistributeCriteria = new EventEmitter<CriteriaDistributionFormModel>();
  formIdentificationModes: IdentificationModeForm = IdentificationModesConstant;
  formDepositModes: Array<IDepositModeForm> = [];
  private _manageable = false;

  constructor(private _router: Router) {
  }

  ngOnInit(): void {
    this._setupIdentificationMode();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this._setupFluxDepositMode(changes);
    this._setupIdentificationMode();
  }

  private _setupFluxDepositMode(changes: SimpleChanges): void {
    if (changes?.fluxDepositModes) {
      this.formDepositModes = DepositModesConstant.map(dm => {
        const founded = this.fluxDepositModes.find(d => d.scanActivation && d.key == dm.key);
        return { ...dm, scanActivation: Boolean(founded), disabled: dm.key == 'flow.traceability.deposit.mode.api' || dm.key == 'flow.traceability.deposit.mode.portal' };
      });
    }
  }

  private _setupIdentificationMode(): void {
    const founded = this.fluxDepositModes.find(d => d.scanActivation && d.key == 'flow.traceability.deposit.mode.portal');
    this._manageable = this.fluxIdentificationMode && Boolean(founded);
    this.formIdentificationModes = { ...this.formIdentificationModes, checked: this._manageable, disabled: !(founded || false) };
  }

  onDepositModesChange(scanActivation: boolean, value: IDepositModeForm) {
    this.formDepositModes = this.formDepositModes.map(dm => {
      return dm.key == value.key? { ...dm, scanActivation } : dm;
    });

    const emitValue = this.formDepositModes.map(dm => {
      const { disabled, ...rest } = dm;
      return rest;
    });
    this.valueChanged.emit(emitValue);
  }

  onIdentificationModesChange(checked: boolean) {
    this.formIdentificationModes = { ...this.formIdentificationModes, checked };
    this.configurationFileChanged.emit(checked);
    this._manageable = checked;
  }

  manageConfigurationFile() {
    this.manageConfigurationChanged.emit(this._manageable);
  }

  criteriaChanged(distributeCriteria: PreferencePayload): void {
    this.criteriaChange.emit(distributeCriteria);
  }

  manageDistribute(distributeCriteria: CriteriaDistributionFormModel): void {
    this.manageDistributeCriteria.emit(distributeCriteria);
  }
}
