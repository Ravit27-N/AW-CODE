import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import {MatSelectionList} from '@angular/material/list';
import {REGEXP} from '@cxm-smartflow/shared/utils';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {takeUntil} from 'rxjs/operators';
import {Subject} from 'rxjs';
import {ConfirmationMessageService} from '@cxm-smartflow/shared/ui/comfirmation-message';
import {TranslateService} from '@ngx-translate/core';
import {InlineInputComponent} from './inline-input';
import {PopupReturnAddressService} from "@cxm-smartflow/client/ui/client-popup-dialog";
import {FragmentReturnAddressType} from "@cxm-smartflow/shared/fragments/return-address";

@Component({
  selector: 'cxm-smartflow-associate-list',
  templateUrl: './associate-list.component.html',
  styleUrls: ['./associate-list.component.scss'],
})
export class AssociateListComponent implements OnInit, OnChanges, OnDestroy {
  @Input() isReadyState: boolean;
  @Input() clientDivision: any;
  @Output() divisionUpdated = new EventEmitter<any>();
  errorDivision = false;
  errorService = false;
  divisionErrorMsg = '';
  serviceErrorMsg = '';
  formGroup: FormGroup;
  destroy$ = new Subject<boolean>();
  tempDivisionServiceErrorName: string;

  @ViewChild('service') serviceSelectionList: MatSelectionList;
  @ViewChild('division') divisionSelectionList: MatSelectionList;

  divisions: any[] = [];

  isShowing = false;
  isShowingService = true;
  initialized = false;

  selectedDivision: any = null;
  selectedServiceName = '';
  selectedDivisionName = '';

  @ViewChild('editPortalContent') editPortalContent: TemplateRef<unknown>;

  handleAddNewItem() {
    this.isShowing = true;
  }

  onClick(value: string, itemName: string, inlineComp: InlineInputComponent, isDivision?: boolean): void {
    isDivision ? this.editDivision(value, itemName, inlineComp) : this.handleEditServices(value, itemName, inlineComp);
  }

  handleAddDivisionClick(input: any) {
    if (this.errorDivision) return;
    const value = input.value as string;
    this.validateDivision(value);
    if (this.errorDivision) return;
    this.enterNewItem(value);
    input.value = '';
  }

  handleDivisionServiceError(selectDivisionName: string, $event: any) {
    if (selectDivisionName !== this.tempDivisionServiceErrorName) {
      this.errorService = false;
    }
    $event.stopPropagation();
  }

  handleAddDivisionEnter(event: any) {
    const value = event.target?.value as string;
    this.validateDivision(value);
    if (this.errorDivision) return;
    this.enterNewItem(value);
    event.target.value = '';
  }

  handleAddServiceClick(input: any, division: any) {
    const service = this.formGroup.getRawValue().service;
    this.validateService(service, division);
    if (this.errorService) return;
    this.enterNewService(input.value, division);
    input.value = '';
  }

  handleAddServiceEnter(event: any, division: any){
    const service = this.formGroup.getRawValue().service;
    this.validateService(service, division);
    if (this.errorService) return;
    this.enterNewService(service, division);
    event.target.value = '';
  }

  validateDivision(value: string): void {
    if (this.validateNameRequired(value, true)) return;
    const found = this.divisions.find(
      (x) => x.name.toLowerCase().trim() === value.toLowerCase().trim()
    );
    if (found) {
      this.setDivisionErrorMsg('duplicatedDivisionName');
    } else if (!value.match(REGEXP.alphaNumeric)) {
      this.setDivisionErrorMsg('incorrectDivisionFormat');
    } else {
      this.errorDivision = false;
    }
  }

  validateService(value: string, division: any): void {
    if (this.validateNameRequired(value)) return;
    const divisionIndex = this.divisions.findIndex(
      (x) => x.name === division.name
    );
    const found = this.divisions[divisionIndex].services.find(
      (x: any) => x.name.toLowerCase().trim() === value.toLowerCase().trim()
    );

    if (found) {
      this.tempDivisionServiceErrorName = division.name;
      this.setServiceErrorMsg('duplicatedServiceName');
    } else if (!value.match(REGEXP.alphaNumeric)) {
      this.tempDivisionServiceErrorName = division.name;
      this.setServiceErrorMsg('incorrectService');
    } else {
      this.errorService = false;
    }
  }

  validateNameRequired(inputValue: string, isDivision?: boolean): boolean {
    if (!inputValue.length) {
      isDivision ? this.setDivisionErrorMsg('requiredDivision') : this.setServiceErrorMsg('requiredService');
      return true;
    }
    return false;
  }

  private enterNewItem(value: string) {

    if(value.trim().length === 0) return;

    this.divisions.push({
      name: value,
      _editable: true,
      _deletable: false,
      services: [],
    });

    this.shouldShowHide();
    this.divisionChanged();
  }

  handleAddNewService() {
    this.isShowingService = true;
  }

  private enterNewService(value: string, division: any) {
    this.validateService(value, division);
    if (this.errorService) {
      return;
    }

    if(value.trim().length === 0) return;

    const divisionIndex = this.divisions.findIndex(
      (x) => x.name === division.name
    );

    this.divisions[divisionIndex].services.push({ name: value });
    this.isShowingService = false;
    this.divisionChanged();
  }

  removeServiceClickHandler(service: any) {
    Promise.all([
      this.translate.get('client.messages').toPromise(),
      this.translate.get('client.delete').toPromise()
    ])
      .then((messags) => {
        this.confirmService.showConfirmationPopup({
          type: 'Warning',
          icon: 'Warning',
          title: messags[0].deleteServiceTitle,
          message: messags[0].deleteServiceDesc,
          cancelButton: messags[1].cancelButton,
          confirmButton: messags[1].confirmButton
        }).subscribe(ok => {
          if(ok) {
            this.removeService(service);
          }
        })
      })
  }

  removeDivisionClickHandler(event: MouseEvent, division: any) {
    Promise.all([
      this.translate.get('client.messages').toPromise(),
      this.translate.get('client.delete').toPromise()
    ])
      .then((messags) => {
        this.confirmService.showConfirmationPopup({
          type: 'Warning',
          icon: 'Warning',
          title: messags[0].deleteDivisionTitle,
          message: messags[0].deleteDivisionDesc,
          cancelButton: messags[1].cancelButton,
          confirmButton: messags[1].confirmButton
        }).subscribe(ok => {
          if(ok) {
            this.removeDivision(event, division);
          }
        });
      })
  }

  removeDivision(event: MouseEvent, division: any) {
    event.stopPropagation();

    if (division.name === this.selectedDivision.name) {
      this.selectedDivision = null;
      this.serviceSelectionList?.deselectAll();
    }

    const afterDelete = this.divisions.filter((x) => x !== division);
    Object.assign(this, { divisions: afterDelete });
    this.divisionChanged();
  }

  removeService(service: any) {
    const afterDelete = this.divisions.map(x => {
      if(x.name.toLowerCase() === this.selectedDivision.name.toLowerCase()) {
        const s = x.services.filter((y: any) => y.name.toLowerCase() !== service.name.toLowerCase());
        return { ...x, services: s }
      }
      return x;
    })

    Object.assign(this, { divisions: afterDelete  });
    this.reselect();
    this.divisionChanged();
    this.selectedServiceName = '';
  }

  handleDivisionChanged(event: any) {
    this.selectedDivision = event.options[0].value;
    // this.isShowingService = this.selectedDivision.services.length === 0;
    this.serviceSelectionList?.deselectAll();
    this.formGroup.patchValue({ service: '' }, { emitEvent: false });
  }


  handleEditServices(value: any, service: any, inlineComp: InlineInputComponent) {
    if(!this.validateModifyService(value, service, inlineComp)) {
      return;
    }

    inlineComp.close();

    const afterEdit = this.divisions.map(x => {
      if(x.name.toLowerCase() === this.selectedDivision.name.toLowerCase()) {
        const s = x.services.map((y: any) => y.name.toLowerCase() === service.name.toLowerCase() ? {...y, name: value } : y );
        return { ...x, services: s }
      }
      return x;
    });

    Object.assign(this, { divisions: afterEdit  });
    this.reselect();
    this.divisionChanged();
  }

  handleEditDivision(event: any, divisionName: string, inlineComp: InlineInputComponent) {
    const value = event.target.value;
    this.editDivision(value, divisionName, inlineComp);
  }

  editDivision(value: string, divisionName: string, inlineComp: InlineInputComponent) {
    if(!this.validateModifyDivision(value, divisionName, inlineComp)) {
      return;
    }

    inlineComp.close();

    const division = this.divisions.find(x => x.name.toLowerCase() === divisionName.trim().toLowerCase());
    if(value.trim() === '' || !division) {
      return;
    }
    const afterEdit = this.divisions.map(d => d.name === division.name ? { ...d, name: value } : d);
    Object.assign(this, { divisions: afterEdit  });
    this.divisionChanged();

  }


  private reselect() {
    this.selectedDivision = this.divisions.find(x => x.name.toLowerCase() === this.selectedDivision.name.toLowerCase());
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      changes.clientDivision
      // && !changes.clientDivision.firstChange && !this.initialized
    ) {
      const prepared = changes.clientDivision.currentValue.map((d: any) => ({
        ...d,
        services: [...d.services],
      }));
      this.divisions = [...prepared];
      this.initialized = true;

      if(this.selectedDivision) {
        this.reselect();
      }
    }
  }

  private divisionChanged() {
    // Prepare to prevent readonly change to deeps properties
    const prepared = this.divisions.map((d) => ({
      ...d,
      services: [...d.services],
    }));
    this.divisionUpdated.emit({ divisions: prepared });
  }

  constructor(private confirmService: ConfirmationMessageService,
              private translate: TranslateService,
              private fb: FormBuilder,
              private _popupReturnAddressService: PopupReturnAddressService) {
    this.formGroup = this.fb.group({
      division: new FormControl(''),
      service: new FormControl(''),
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
  }

  ngOnInit(): void {
    this.formGroup.controls['division'].valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => this.validateDivision(data));
    this.formGroup.controls['service'].valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => this.validateService(data, this.divisionSelectionList.selectedOptions.selected[0]?.value));
    this.shouldShowHide();
  }

  shouldShowHide() {
    this.isShowing = this.divisions.length == 0;
  }

  setDivisionErrorMsg(
    errorType:
      | 'requiredDivision'
      | 'incorrectDivisionFormat'
      | 'duplicatedDivisionName'
  ): void {
    this.divisionErrorMsg = `client.formError.${errorType}`;
    this.errorDivision = true;
  }

  setServiceErrorMsg(
    errorType: 'requiredService' | 'incorrectService' | 'duplicatedServiceName'
  ): void {
    this.serviceErrorMsg = `client.formError.${errorType}`;
    this.errorService = true;
  }

  removeDivisionError() {
    const division = this.formGroup.getRawValue().division;
    if (division?.trim()?.length === 0) {
      this.errorDivision = false;
    }
  }

  removeServiceError() {
    const service = this.formGroup.getRawValue().service;
    if (service?.trim()?.length === 0) {
      this.errorService = false;
    }
  }

  validateModifyDivision(value: string, divisionName: string, inlineComp: InlineInputComponent): boolean {
    const isRenaming = value.trim().toLowerCase() == divisionName.toLowerCase();
    const isNameExisted = this.divisions.find(x => x.name.toLowerCase() === value.trim().toLowerCase());
    const isInvalidFormat = !value.match(REGEXP.alphaNumeric);

    if(!isRenaming && isNameExisted) {
      this.translate.get('client.formError.duplicatedDivisionName')
        .toPromise().then(message => inlineComp.raiseErrorNameExisted(message));
      return false;
    }

    if (!isRenaming && value.trim().length === 0) {
      this.translate.get('client.formError.requiredDivision')
        .toPromise().then(message => inlineComp.raiseErrorNameExisted(message));
      return false;
    }

    if (!isRenaming && isInvalidFormat) {
      this.translate.get('client.formError.incorrectDivisionFormat')
        .toPromise().then(message => inlineComp.raiseErrorNameExisted(message));
      return false;
    }

    return true;
  }

  validateModifyService(value: any, service: any, inlineComp: InlineInputComponent): boolean {
    const isRenaming = value.trim().toLowerCase() == service.name.toLowerCase();
    const isNameExisted = this.selectedDivision.services.find((x: any) => x.name.toLowerCase() === value.trim().toLowerCase());
    const isInvalidFormat = !value.match(REGEXP.alphaNumeric);
    if(!isRenaming && isNameExisted) {
      this.translate.get('client.formError.duplicatedServiceName').toPromise().then(message => inlineComp.raiseErrorNameExisted(message));
      return false;
    }

    if(!isRenaming && value?.trim().length === 0) {
      this.translate.get('client.formError.requiredService').toPromise().then(message => inlineComp.raiseErrorNameExisted(message));
      return false;
    }

    if(!isRenaming && isInvalidFormat) {
      this.translate.get('client.formError.incorrectService').toPromise().then(message => inlineComp.raiseErrorNameExisted(message));
      return false;
    }

    return true;
  }


  async modifyServiceReturnAddress(address: FragmentReturnAddressType, serviceName: string): Promise<void> {
    const fragmentReturnAddressTypePromise: FragmentReturnAddressType | null | undefined = await this._popupReturnAddressService.show(address).toPromise();

    if (fragmentReturnAddressTypePromise !== undefined) {
      this.divisions = this.divisions.map(division => {
        if(division.name.toLowerCase() === this.selectedDivision.name.toLowerCase()) {
          const modifiedServices = division.services.map((service: any) => {
            if (service.name.toLowerCase() === serviceName.toLowerCase()) {
              return {
                ...service,
                address: fragmentReturnAddressTypePromise,
              }
            }

            return service;
          });
          return { ...division, services: modifiedServices }
        }
        return division;
      });

      this.divisionChanged();
    }
  }

  async modifyDivisionReturnAddress(address: FragmentReturnAddressType, divisionName: string): Promise<void> {
    const fragmentReturnAddressTypePromise: FragmentReturnAddressType | null | undefined = await this._popupReturnAddressService.show(address).toPromise();
    if (fragmentReturnAddressTypePromise !== undefined) {
      this.divisions = this.divisions.map(division => {
        if (division.name.toLowerCase() === divisionName.toLowerCase()) {
          return {
            ...division,
            address: fragmentReturnAddressTypePromise,
          }
        }

        return division;
      });
      this.divisionChanged();
    }

  }

  selectService(serviceName: string): void {
    this.selectedServiceName = serviceName;
  }

  selectDivision(divisionName: string): void {
    if (divisionName !== this.selectedDivisionName) {
      this.selectedServiceName = '';
    }

    this.selectedDivisionName = divisionName;
  }
}
