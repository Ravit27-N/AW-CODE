import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';
import { Subscription } from 'rxjs';

export interface InputSelectionCriteria {
  key: string | number;
  value: string;
}

@Component({
  selector: 'cxm-smartflow-input-selection',
  templateUrl: './input-selection.component.html',
  styleUrls: ['./input-selection.component.scss'],
})
export class InputSelectionComponent implements OnChanges, OnDestroy {
  @Input() datasource: InputSelectionCriteria[] = [];
  @Input() errorMsg = '';
  @Input() isShowError = false;
  @Input() selectSourceKey: number | string;
  @Input() paddingLeft = '0px';
  @Input() isDisabled = false;
  @Input() lockModifyOverlayContainer = false;
  @Output() selectedSource = new EventEmitter<any>();
  formGroup: FormGroup;
  private _subscription$: Subscription;

  constructor(private _fb: FormBuilder) {
    this.formGroup = this._fb.group({
      selectValue: new FormControl(false)
    });

    this._subscription$ = this.formGroup.valueChanges.subscribe(e => {
      this.selectedSource.emit(e.selectValue);
    });
  }

  ngOnDestroy(): void {
    this._subscription$.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes.selectSourceKey && changes.selectSourceKey.currentValue || (changes?.selectSourceKey?.currentValue === 0)) {
      this.formGroup.patchValue({selectValue: changes.selectSourceKey.currentValue}, {emitEvent: false});
    }
  }

  change(id: number | string) {
    this.formGroup.patchValue({selectValue: id});

  }

  mapLabel(key: any) {
    return this.datasource.find(e => e.key == key)?.value || '';
  }

  setCustomStyle() {
    document
      .querySelector('.mat-menu-panel')
      ?.classList.add('common-mat-menu-panel');
  }

  mainMenuOpen() {
    if (this.lockModifyOverlayContainer) {
      return;
    }

    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose() {
    if (this.lockModifyOverlayContainer) {
      return;
    }

    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }
}
