import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import {
  CheckedListKeyValue,
  CriteriaStorage,
  FlowCriteriaSessionService,
  selectFlowDocumentStatusState,
  selectFlowStatusState,
} from '@cxm-smartflow/flow-traceability/data-access';
import { Store } from '@ngrx/store';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { appLocalStorageConstant } from '@cxm-smartflow/shared/data-access/model';
import { TranslateService } from '@ngx-translate/core';
import { CustomAngularMaterialUtil, Sort } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-button-dropdown-status',
  templateUrl: './button-dropdown-status.component.html',
  styleUrls: ['./button-dropdown-status.component.scss'],
})
export class ButtonDropdownStatusComponent implements OnInit, OnDestroy {
  flowStatus: CheckedListKeyValue[] = [];
  destroy$ = new Subject<boolean>();
  sortDirection = Sort.ASC;

  @Output() statusChange = new EventEmitter<string[]>();
  @Input() isFlowDocument = false;
  @Input() customCssClass = '';
  _statusStorage?: string[] = [];
  isUsingFilter = false;

  @Input() forComponentType: 'flowTraceability'|'flowDocument'|'viewDocumentShipment';

  constructor(
    private store: Store,
    private readonly translateService: TranslateService,
    private storageService: FlowCriteriaSessionService
  ) {}

  ngOnInit(): void {
    this.loadStorage();

    if (this.isFlowDocument) {
      this.store
        .select(selectFlowDocumentStatusState)
        .pipe(takeUntil(this.destroy$))
        .subscribe((res) => {
          if (res) {
            this.flowStatus = res;
            this.mappingData();
          }
        });
    } else {
      this.store
        .select(selectFlowStatusState)
        .pipe(takeUntil(this.destroy$))
        .subscribe((res) => {
          if (res) {
            this.flowStatus = res;
            this.mappingData();
          }
        });
    }
  }

  onReset(): void {
    this.sortDirection = Sort.ASC;
    this.setStorage([]);
    this.statusChange.emit([]);
    this.mappingData();
  }

  checkedItems(event: string[]): void {
    this.setStorage(event);
    this.statusChange.emit(event);
  }

  async mappingData(): Promise<void> {
    const flowStatusTranslate = await this.translateService.get('flow.traceability.status').toPromise();
    this.flowStatus = this.flowStatus.map((item) => ({
      ...item,
      key: flowStatusTranslate[item?.value?.toLowerCase().replace(' ', '_')],
      checked: this._statusStorage && this._statusStorage.includes(item.value),
    }));
    this.sortFlowStatus(this.sortDirection);

  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  addCustomCssClass(): void {
    document
      .querySelector('.status-custom-dropdown')
      ?.classList?.add(this.customCssClass);
  }

  setStorage(data: string[]): void {
    this._statusStorage = [];
    if (this.isFlowDocument) {

      if(this.forComponentType === 'viewDocumentShipment') {
        const store = this.mappingStorageData(data, this.storageService.getDocumentShipmentCriteria())
        this.storageService.setDocumentShipmentCriteria(store);
        this.isUsingFilter = store.criteriaParams?.status && store.criteriaParams?.status?.length > 0 || false;
      } else {
        const store = this.mappingStorageData(data, this.storageService.getDocumentCriteria())
        this.storageService.setDocumentCriteria(store);
        this.isUsingFilter = store.criteriaParams?.status && store.criteriaParams?.status?.length > 0 || false;
      }
    } else {
      const store = this.mappingStorageData(data, this.storageService.getFlowCriteria())
      this.storageService.setFlowCriteria(store);
      this.isUsingFilter = store.criteriaParams?.status && store.criteriaParams?.status?.length > 0 || false;
    }

  }

  mappingStorageData(status?: string[], data?: CriteriaStorage) {
    return {
      ...data,
      criteriaParams: {
        ...data?.criteriaParams,
        status,
      },
    };
  }

  loadStorage(): void {
    if (this.isFlowDocument) {
      if(this.forComponentType === 'viewDocumentShipment') {
        this._statusStorage = this.storageService.getDocumentShipmentCriteria()?.criteriaParams?.status;
      } else {
        this._statusStorage = this.storageService.getDocumentCriteria()?.criteriaParams?.status;
      }
    } else {
      this._statusStorage = this.storageService.getFlowCriteria()?.criteriaParams?.status;
    }

    this.isUsingFilter = !!this._statusStorage && this._statusStorage.length > 0;
  }

  changeDirection(): void {
    this.sortDirection = this.sortDirection === Sort.ASC? Sort.DESC : Sort.ASC;
    this.sortFlowStatus(this.sortDirection);
  }

  sortFlowStatus(sortDirection: string): void {
    const sorted = this.flowStatus.sort((a:CheckedListKeyValue, b:CheckedListKeyValue) => a.key?.localeCompare(b.key));
    this.flowStatus = sortDirection === Sort.ASC? sorted : sorted.reverse();
  }

  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }
}
