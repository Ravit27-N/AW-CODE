import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import {
  CheckedListKeyValue,
  CriteriaStorage,
  FlowCriteriaSessionService,
  selectCriteriaSubChannelState,
  selectFlowDocumentSubChannelState,
  validateFlowSubChannel,
} from '@cxm-smartflow/flow-traceability/data-access';
import { BehaviorSubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-button-dropdown-faculty',
  templateUrl: './button-dropdown-faculty.component.html',
  styleUrls: ['./button-dropdown-faculty.component.scss'],
})
export class ButtonDropdownFacultyComponent implements OnInit, OnDestroy {
  modeForm: FormGroup;
  channels: string[] = [];
  filterChannel = 'Multiple';
  @Input() isFlowDocument = false;
  @Input() customCssClass = '';

  destroy$ = new Subject<boolean>();
  subChannel$ = new BehaviorSubject([] as CheckedListKeyValue[]);
  _categories?: string[] = [];

  @Output() categoriesChange = new EventEmitter<any>();
  @Input() forComponentType: 'flowTraceability'|'flowDocument'|'viewDocumentShipment';
  isUsingFilter = false;

  constructor(
    private fb: FormBuilder,
    private store: Store,
    private storageService: FlowCriteriaSessionService
  ) {}

  ngOnInit(): void {
    this.initData();
  }

  onCheckPostalAndDigital(): void {
    this.channels = Object.assign([], this.channels);
    const postalValue = this.modeForm.get('postal')?.value;
    const digitalValue = this.modeForm.get('digital')?.value;

    if (postalValue && digitalValue) {
      this.channels = ['Postal', 'Digital'];
      this.filterChannel = 'Multiple';
    } else if (postalValue && !digitalValue) {
      this.filterChannel = 'Postal';
      this.channels = [this.filterChannel];
    } else if (digitalValue && !postalValue) {
      this.filterChannel = 'Digital';
      this.channels = [this.filterChannel];
    } else {
      this.channels = [];
      this.filterChannel = 'Multiple';
    }

    this.setStorage([]);
    this.categoriesChange.emit({ channels: this.channels });
    this.onFilterSubChannel();
  }

  initData() {
    if (this.isFlowDocument) {
      if(this.forComponentType === 'viewDocumentShipment') {
        this.initChannel(this.storageService.getDocumentShipmentCriteria());
      } else {
        this.initChannel(this.storageService.getDocumentCriteria());
      }
    } else {
      this.initChannel(this.storageService.getFlowCriteria());
    }
  }

  initChannel(criteriaStorage?: CriteriaStorage): void {
    this.channels = criteriaStorage?.criteriaParams?.channels || [];
    this._categories = criteriaStorage?.criteriaParams?.categories || [];
    this.filterChannel =
      this.channels.length === 1 ? this.channels?.[0] : 'Multiple';

    this.modeForm = this.fb.group({
      postal: new FormControl(this.channels?.includes('Postal')),
      digital: new FormControl(this.channels?.includes('Digital')),
    });

    this.isUsingFilter = this.channels.length > 0 || this._categories.length > 0;

    this.onFilterSubChannel();
  }

  setStorage(categories?: string[]) {
    this._categories = categories;
    if (this.isFlowDocument) {
      if (this.forComponentType === 'viewDocumentShipment') {
        this.storageService.setDocumentShipmentCriteria(this.mappingStorageData(categories, this.storageService.getDocumentShipmentCriteria()));
      } else {
        this.storageService.setDocumentCriteria(this.mappingStorageData(categories, this.storageService.getDocumentCriteria()));
      }
    } else {
      this.storageService.setFlowCriteria(
        this.mappingStorageData(
          categories,
          this.storageService.getFlowCriteria()
        )
      );
    }

    this.isUsingFilter = this.channels.length > 0 || (!!this._categories && this._categories.length > 0);
  }

  mappingStorageData(categories?: string[], data?: CriteriaStorage) {
    return {
      ...data,
      criteriaParams: {
        ...data?.criteriaParams,
        channels: this.channels,
        categories,
      },
    };
  }

  onFilterSubChannel(): void {
    if (this.isFlowDocument) {
      this.onFilterFlowDocumentSubChannel();
    } else {
      this.onFilterFlowSubChannel();
    }
  }

  onFilterFlowDocumentSubChannel(): void {
    this.store
      .select(selectFlowDocumentSubChannelState)
      .pipe(takeUntil(this.destroy$))
      .subscribe((res) => {
        if (res !== undefined) {
          if (res) {
            const temp = res.filter((item: any) =>
              item?.key.includes(this.filterChannel.toLowerCase())
            );
            const mapped = this.mappingSubChannel(temp[0]?.sendingSubChannel);
            this.subChannel$.next([...mapped]);
          }
        }
      });
  }

  onFilterFlowSubChannel(): void {
    this.store
      .select(selectCriteriaSubChannelState)
      .pipe(takeUntil(this.destroy$))
      .subscribe((res) => {
        if (res) {
          const temp = res
            .filter((item: any) =>
              item?.key.includes(this.filterChannel.toLowerCase())
            )
            .flatMap((item: any) => item?.value.split(','));

          let mapped = [...temp];
          mapped = this.mappingSubChannel(mapped);
          this.subChannel$.next([...mapped]);
        }
      });
  }

  mappingSubChannel(subChannels: string[]): CheckedListKeyValue[] {
    return subChannels.map((item: string) => ({
      key: validateFlowSubChannel(item),
      value: item,
      checked: this._categories && this._categories.includes(item),
    }));
  }

  onReset(): void {
    // reset state when clear
    this.modeForm.reset();
    this.channels = [];
    this.filterChannel = 'Multiple';

    this.clearFacultyStorage();
    this.categoriesChange.emit({ channels: this.channels });
    this.onFilterSubChannel();
  }

  checkedItems(event: string[]): void {
    this.setStorage(event);
    this.categoriesChange.emit({ channels: this.channels, categories: event });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
    this.subChannel$.unsubscribe();
  }

  addCustomCssClass() {
    document
      .querySelector('.faculty-custom-dropdown')
      ?.classList.add(this.customCssClass);
  }

  clearFacultyStorage(): void {
    this.channels = [];
    this.setStorage([]);
  }

  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }
}
