import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output
} from '@angular/core';
import {
  CheckedListKeyValue,
  CriteriaStorage,
  FlowCriteriaSessionService,
  selectDepositModeState
} from '@cxm-smartflow/flow-traceability/data-access';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-button-dropdown-deposit-mode',
  templateUrl: './button-dropdown-deposit-mode.component.html',
  styleUrls: ['./button-dropdown-deposit-mode.component.scss'],
})
export class ButtonDropdownDepositModeComponent implements OnInit, OnDestroy {
  destroy$ = new Subject<boolean>();
  depositMode: CheckedListKeyValue[] = [];

  @Input() customCssClass = '';
  @Output() depositModeChange = new EventEmitter<string[]>();
  _depositStorage?: string[] = [];
  usingModeFilter = false;

  constructor(
    private store: Store,
    private storageService: FlowCriteriaSessionService
  ) {
    this.loadStorage();
  }

  ngOnInit(): void {
    this.usingModeFilter = this._depositStorage ? this._depositStorage?.length > 0 : false;

    this.store
      .select(selectDepositModeState)
      .pipe(takeUntil(this.destroy$))
      .subscribe((res) => {
        if (res) {
          this.depositMode = res;
          this.mappingData();
          this.applyFilterActive(this.depositMode.filter(x => x.checked === true));
        }
      });
  }

  onReset() {
    this.setStorage([]);
    this.depositModeChange.emit([]);
    this.mappingData();
  }

  mappingData(): void {
    this.depositMode = this.depositMode.map((item) => ({
      ...item,
      checked:
        this._depositStorage && this._depositStorage.includes(item.value),
    }));
  }

  checkedItems(event: string[]): void {
    this.setStorage(event);
    this.depositModeChange.emit(event);
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  addCustomCssClass(): void {
    document
      .querySelector('.deposit-mode-custom-dropdown')
      ?.classList?.add(this.customCssClass);
  }

  setStorage(data: string[]): void {
    this._depositStorage = [];
    const store = this.mappingStorageData(data, this.storageService.getFlowCriteria());
    this.storageService.setFlowCriteria(store);
    this.applyFilterActive(store.criteriaParams.depositModes || []);
  }

  mappingStorageData(depositModes?: string[], data?: CriteriaStorage) {
    return {
      ...data,
      criteriaParams: {
        ...data?.criteriaParams,
        depositModes,
      },
    };
  }

  loadStorage(): void {
    this._depositStorage = this.storageService.getFlowCriteria()?.criteriaParams?.depositModes;
  }

  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  applyFilterActive(filters: any) {
    this.usingModeFilter = (filters && filters.length > 0);
  }
}
