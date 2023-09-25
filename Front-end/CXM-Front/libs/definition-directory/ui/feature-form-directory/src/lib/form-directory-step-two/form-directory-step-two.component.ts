import {Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {FormDirectoryStepTwoValidator} from './form-directory-step-two.validator';
import {forkJoin, Subscription} from 'rxjs';
import {
  DefinitionDirectoryService,
  DirectoryField,
  FieldData,
  FormMode,
  MaskAndTypeValidationResponse
} from '@cxm-smartflow/definition-directory/data-access';
import {KeyValue} from '@angular/common';
import {TranslateService} from '@ngx-translate/core';
import {SnackBarService} from "@cxm-smartflow/shared/data-access/services";
import {ActivatedRoute} from "@angular/router";

export interface FieldListItem extends DirectoryField {
  _selected: boolean;
  _deletable: boolean;
  _draggable: boolean;
}

interface FieldListItemProperty {
  id: number;
  displayName: string;
  dataType: 'Boolean' | 'Number' | 'Integer' | 'Date' | 'String';
  key: boolean;
  data:
    | 'directory_field_data_field_specific'
    | 'directory_field_data_company_name'
    | 'directory_field_data_telephone'
    | 'directory_field_data_email'
    | 'directory_field_data_address_line'
    | 'directory_field_data_postal_code'
    | 'directory_field_data_commune';
  presence: 'optional' | 'mandatory';
  maxLength: '';
  mask: '';
  canModifyToRequire: boolean,
  canModifyToKey: boolean,
  minLength: number
}

const DataKeyPair: KeyValue<string, string>[] = [
  {
    value: "Field specific",
    key: "directory_field_data_field_specific"
  },
  {
    value: "Company name",
    key: "directory_field_data_company_name"
  },
  {
    value: "Telephone",
    key: "directory_field_data_telephone"
  },
  {
    value: "Email",
    key: "directory_field_data_email"
  },
  {
    value: "Address line",
    key: "directory_field_data_address_line"
  },
  {
    value: "Postal code",
    key: "directory_field_data_postal_code"
  },
  {
    value: "Commune",
    key: "directory_field_data_commune"
  }
];

@Component({
  selector: 'cxm-smartflow-form-directory-step-two',
  templateUrl: './form-directory-step-two.component.html',
  styleUrls: ['./form-directory-step-two.component.scss'],
})
export class FormDirectoryStepTwoComponent implements OnInit, OnChanges, OnDestroy {
  fieldListItems: FieldListItem[] = [];
  fieldPropertyDataType: KeyValue<string, string>[] = [];
  fieldPropertyData: FieldData[] = [];
  fieldPropertyPresence: KeyValue<string, string>[] = [];
  fieldKeyVisible = false;

  @Input() fieldValues: DirectoryField[] = [];
  @Input() formMode: FormMode = 'create';
  @Input() alreadyFeed: boolean = false;
  @Input() formStep2Editor: any = {};
  @Output() stepTwoNextPageEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() stepTwoPreviousPageEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  @Output() fieldListItemValueChangesEvent: EventEmitter<DirectoryField[]> = new EventEmitter<DirectoryField[]>();
  @Output() formStep2EditorChangeEvent: EventEmitter<any> = new EventEmitter<any>();

  warningAddNewField = false;
  stepTwoFormPropertiesFormGroup: FormGroup;
  formDirectoryFieldPropertiesSelected = false;
  preFillsFormValue: FieldListItemProperty = {
    displayName: '',
    data: 'directory_field_data_field_specific',
    dataType: 'String',
    key: true,
    maxLength: '',
    presence: 'optional',
    mask: '',
    id: 0,
    canModifyToRequire: false,
    canModifyToKey: false,
    minLength: 0
  };
  showError = false;
  #subscription: Subscription = new Subscription();

  constructor(
    private formBuilder: FormBuilder,
    private translateService: TranslateService,
    private snackbar: SnackBarService,
    private directoryService: DefinitionDirectoryService,
    private activatedRoute: ActivatedRoute
  ) {
  }

  async ngOnInit(): Promise<void> {
    await this.fetchDirectoryDefinitionDataType();
    await this.fetchDirectoryDefinitionData();
    if (!this.stepTwoFormPropertiesFormGroup) {
      this.setupFieldFormDirectoryStepTwo();
    }
    await this.subscribeStepTwoFieldPropertiesForm();

    //set key back
    if (this.stepTwoFormPropertiesFormGroup.get('key')?.value) {
      this.fieldKeyVisible = true;
    }

    if (this.fieldListItems.length > 0 && !this.fieldListItems.find(field => field.key)) {
      this.fieldKeyVisible = true;
    }

    //set mask or length
    if (this.stepTwoFormPropertiesFormGroup.get('maskEnabled')?.value) {
      this.stepTwoFormPropertiesFormGroup.patchValue({
        maxLengthEnabled: false
      });
    }

  }

  ngOnChanges(changes: SimpleChanges): void {
    this.fieldListItems = this.fieldValues.map((field, index) => {
      return {
        ...field,
        _selected: this.stepTwoFormPropertiesFormGroup?.getRawValue()?.orderId === index,
        _draggable: this.formMode !== 'view',
        _deletable: this.formMode !== 'view'
      }
    });

    this.patchForm(changes);

    if (changes?.formMode) {
      if (this.formMode === 'view') {
        this.stepTwoFormPropertiesFormGroup?.disable();
      }
    }
  }

  ngOnDestroy(): void {
    this.#subscription.unsubscribe();
  }


  private patchForm(changes: SimpleChanges): void {
    const item = changes?.formStep2Editor?.currentValue || {};
    if (Object.keys(item).length === 0 || !changes?.formStep2Editor?.firstChange) {
      return;
    }

    if (!this.stepTwoFormPropertiesFormGroup) {
      this.setupFieldFormDirectoryStepTwo();
    }
    this.resetForm();

    this.formDirectoryFieldPropertiesSelected = true;
    this.preFillsFormValue = {
      id: item?.id,
      displayName: item?.displayName,
      dataType: item?.dataType,
      data: item?.data,
      mask: item?.mask,
      maxLength: item?.maxLength,
      key: item?.key,
      presence: item?.presence,
      canModifyToRequire: item?.canModifyToRequire,
      canModifyToKey: item?.canModifyToKey,
      minLength: item?.minLength
    }

    const isSetDefaultValue = (this.preFillsFormValue.dataType !== 'Boolean') && !this.preFillsFormValue.maxLength && !this.preFillsFormValue.mask;

    this.stepTwoFormPropertiesFormGroup?.patchValue({
      displayName: this.preFillsFormValue.displayName,
      dataType: this.preFillsFormValue.dataType,
      key: this.preFillsFormValue.key,
      data: this.preFillsFormValue.data,
      presence: this.preFillsFormValue.presence,
      maxLengthEnabled: isSetDefaultValue ? true : Boolean(this.preFillsFormValue.maxLength),
      maskEnabled: Boolean(this.preFillsFormValue.mask),
      maxLength: isSetDefaultValue ? 255 : this.preFillsFormValue.maxLength,
      mask: this.preFillsFormValue.mask,
      id: this.preFillsFormValue.id,
      orderId: item?.orderId,
      canModifyToRequire: this.preFillsFormValue?.canModifyToRequire,
      canModifyToKey: this.preFillsFormValue?.canModifyToKey
    });

    if (this.formMode !== 'view') {
      if (this.preFillsFormValue.dataType !== 'String') {
        this.stepTwoFormPropertiesFormGroup?.controls['data']?.disable({emitEvent: false, onlySelf: false});
      }

      if (this.preFillsFormValue.data !== 'directory_field_data_field_specific') {
        this.stepTwoFormPropertiesFormGroup?.controls['dataType']?.disable({emitEvent: false, onlySelf: false});
      }


    } else {
      this.stepTwoFormPropertiesFormGroup?.controls['data']?.disable();
      this.stepTwoFormPropertiesFormGroup?.controls['dataType']?.disable();
    }
  }

  /**
   * Select field.
   */
  selectFieldEvent(order: number): void {
    if (this.stepTwoFormPropertiesFormGroup.getRawValue()?.orderId === order) {
      return;
    }

    this.resetForm();
    this.stepTwoFormPropertiesFormGroup.patchValue({orderId: order}, {emitEvent: false, onlySelf: false});
    this.formDirectoryFieldPropertiesSelected = true;

    this.fieldListItems = this.fieldListItems.map((fieldListItem, index) => {
      return {...fieldListItem, _selected: order === index};
    });

    const selectedField: any = this.fieldValues.find((field, index) => index === order);
    this.fieldKeyVisible = selectedField.key || !this.fieldListItems.find(field => field.key);


    if (selectedField.id === null && this.alreadyFeed) {
      this.warningAddNewField = true;
    } else {
      this.warningAddNewField = false;
    }

    const data: any = DataKeyPair.find(field => field.value === selectedField.properties?.data);

    this.preFillsFormValue = {
      displayName: selectedField?.field,
      dataType: selectedField?.type,
      key: selectedField?.key,
      data: data?.key,
      presence: selectedField.properties.required ? 'mandatory' : 'optional',
      mask: selectedField.properties.option.mask,
      maxLength: selectedField.properties.option.length,
      id: selectedField.id || null,
      canModifyToRequire: selectedField?.canModifyToRequire,
      canModifyToKey: selectedField?.canModifyToKey,
      minLength: selectedField?.minLength,
    };

    const isSetDefaultValue = (this.preFillsFormValue.dataType !== 'Boolean') && !this.preFillsFormValue.maxLength && !this.preFillsFormValue.mask;

    this.stepTwoFormPropertiesFormGroup.patchValue({
      displayName: this.preFillsFormValue.displayName,
      dataType: this.preFillsFormValue.dataType,
      key: this.preFillsFormValue.key,
      data: this.preFillsFormValue.data,
      presence: this.preFillsFormValue.presence,
      maxLengthEnabled: isSetDefaultValue ? true : Boolean(this.preFillsFormValue.maxLength),
      maskEnabled: Boolean(this.preFillsFormValue.mask),
      maxLength: isSetDefaultValue ? 255 : this.preFillsFormValue.maxLength,
      mask: this.preFillsFormValue.mask,
      id: this.preFillsFormValue.id,
      canModifyToRequire: this.preFillsFormValue.canModifyToRequire,
      canModifyToKey: this.preFillsFormValue.canModifyToKey,
      minLength: this.preFillsFormValue?.minLength
    });


  }

  /**
   * Delete field by index.
   */
  deleteFieldEvent(order: number): void {
    this.fieldListItems = this.fieldListItems
      .filter((field, index) => index !== order)
      .map((field, index) => {
        const data = DataKeyPair.find(element => element.key === field.properties?.data)?.value;
        return {...field, fieldOrder: index + 1, properties: {...field.properties, data}};
      });
    this.fieldListItemValueChangesEvent.emit(this.fieldListItems);
    this.formStep2EditorChangeEvent.emit({});
    this.resetForm();
  }

  /**
   * Add field.
   */
  addMoreFieldEvent(): void {
    this.resetForm();
    this.formStep2EditorChangeEvent.emit({});
    this.fieldListItems = this.fieldListItems.map(field => ({...field, _selected: false}));
    this.formDirectoryFieldPropertiesSelected = true;
    this.stepTwoFormPropertiesFormGroup.patchValue({orderId: -1}, {emitEvent: false, onlySelf: false});
    this.fieldKeyVisible = !this.fieldListItems.find(field => field.key);

    if (this.showWarning) {
      this.fieldKeyVisible = false;
    }

    if (this.formMode == "modify" && this.alreadyFeed) {
      this.warningAddNewField = true;
      this.preFillsFormValue = {
        displayName: '',
        dataType: 'String',
        key: this.fieldKeyVisible,
        data: 'directory_field_data_field_specific',
        presence: 'optional',
        mask: '',
        maxLength: '',
        id: 0,
        canModifyToRequire: false,
        canModifyToKey: false,
        minLength: 0
      };
    } else {
      this.defaultPreFillsFormValue();
    }


    this.stepTwoFormPropertiesFormGroup.patchValue({
      displayName: this.preFillsFormValue.displayName,
      dataType: this.preFillsFormValue.dataType,
      key: this.preFillsFormValue.key,
      data: this.preFillsFormValue.data,
      presence: this.preFillsFormValue.presence,
      maxLengthEnabled: true,
      maskEnabled: false,
      maxLength: 255,
      mask: '',
    });
  }

  /**
   * Order field.
   */
  fieldListItemsOrdersEvent($event: CdkDragDrop<any, any>): void {
    const {currentIndex, previousIndex} = $event;
    moveItemInArray(this.fieldListItems, previousIndex, currentIndex);
    this.fieldListItems = this.fieldListItems.map((fieldListItem, index) => ({
      ...fieldListItem,
      fieldOrder: index + 1,
    }));

    const fields: DirectoryField[] = this.fieldListItems.map((field, index) => {
      const data = DataKeyPair.find(element => element.value === field.properties?.data)?.value;
      return {
        id: field.id,
        field: field.field,
        key: field.key,
        type: field.type,
        properties: {
          ...field.properties,
          data,
        },
        fieldOrder: index + 1,
      }
    });

    this.fieldListItemValueChangesEvent.emit(fields);
    this.resetForm();
    this.formStep2EditorChangeEvent.emit({});
  }

  /**
   * Fetch API definition data type.
   */
  private async fetchDirectoryDefinitionDataType(): Promise<void> {
    const subscription: Subscription = forkJoin([
      this.translateService.get(
        'directory.definition_directory_create_step_2_section_field_properties_data_types'
      ),
      this.directoryService.getDirectoryDefinitionDataType(),
    ]).subscribe(([messages, dataTypes]): void => {
      this.fieldPropertyDataType = dataTypes.map((dataType) => ({
        key: dataType.value,
        value: messages[`${dataType.key}`],
      }));
      this.fieldPropertyPresence = [
        {key: 'optional', value: messages['directory_field_type_optional']},
        {key: 'mandatory', value: messages['directory_field_type_mandatory']},
      ];
    });

    this.#subscription.add(subscription);
  }

  /**
   * Get definition data.
   */
  private async fetchDirectoryDefinitionData(): Promise<void> {
    const locale: string = localStorage.getItem('locale') || 'fr';
    const subscription: Subscription = this.directoryService
      .getDirectoryDefinitionFieldData(locale)
      .subscribe((response) => {
        this.fieldPropertyData = response;
      });
    this.#subscription.add(subscription);
  }

  /**
   * Setup form.
   */
  private setupFieldFormDirectoryStepTwo(): void {
    this.stepTwoFormPropertiesFormGroup = this.formBuilder.group({
      id: new FormControl(0),
      displayName: new FormControl('', [
        FormDirectoryStepTwoValidator.fieldDisplayName(),
      ]),
      dataType: new FormControl('', [
        FormDirectoryStepTwoValidator.fieldDataType(),
      ]),
      key: new FormControl(false, [FormDirectoryStepTwoValidator.fieldKey()]),
      data: new FormControl('', [FormDirectoryStepTwoValidator.fieldData()]),
      presence: new FormControl('', [
        FormDirectoryStepTwoValidator.fieldPresence(),
      ]),
      maxLengthEnabled: new FormControl(false),
      maxLength: new FormControl(255, [
        FormDirectoryStepTwoValidator.fieldMaxLength(),
      ]),
      maskEnabled: new FormControl(false),
      mask: new FormControl('', [FormDirectoryStepTwoValidator.fieldMask()]),
      orderId: new FormControl(-1),
      canModifyToRequire: new FormControl(false),
      canModifyToKey: new FormControl(false),
      minLength: new FormControl(0),
    });
    if (this.formMode === 'view') {
      this.stepTwoFormPropertiesFormGroup?.disable();
    }
  }

  private async subscribeStepTwoFieldPropertiesForm(): Promise<void> {
    // Hide error.
    const subscription: Subscription = this.stepTwoFormPropertiesFormGroup.valueChanges.subscribe((rawValue) => {
        this.showError = false;
        this.formStep2EditorChangeEvent.emit({...this.stepTwoFormPropertiesFormGroup.getRawValue()});
      }
    );
    this.#subscription.add(subscription);

    const messages = await this.translateService
      .get('directory.definition_directory_create_step_2_section_field_properties_data_types')
      .toPromise();

    // Hide show presence field base on key.
    const subscription1: Subscription = this.stepTwoFormPropertiesFormGroup.controls['key'].valueChanges.subscribe((value) => {
      if (value) {
        this.fieldPropertyPresence = [
          {
            key: 'mandatory',
            value: messages['directory_field_type_mandatory'],
          },
        ];

        this.stepTwoFormPropertiesFormGroup.patchValue({
          presence: 'mandatory',
        });
        this.preFillsFormValue.presence = 'mandatory';
      } else {
        this.fieldPropertyPresence = [
          {key: 'optional', value: messages['directory_field_type_optional']},
          {
            key: 'mandatory',
            value: messages['directory_field_type_mandatory'],
          },
        ];
      }
    });
    this.#subscription.add(subscription1);

    // Hide show presence field base on key.
    const subscription2: Subscription = this.stepTwoFormPropertiesFormGroup.controls['data'].valueChanges.subscribe((value) => {
      this.stepTwoFormPropertiesFormGroup.patchValue({
        mask: this.fieldPropertyData.find(mask => mask.key === value)?.options.value || ''
      }, {emitEvent: false, onlySelf: false})

      if (this.formMode !== 'view') {
        if (value !== 'directory_field_data_field_specific') {
          this.stepTwoFormPropertiesFormGroup.patchValue({dataType: 'String'});
          this.stepTwoFormPropertiesFormGroup.controls['dataType'].disable({emitEvent: false, onlySelf: false});
        } else {
          this.stepTwoFormPropertiesFormGroup.controls['dataType'].enable({emitEvent: false, onlySelf: false});
        }
      } else {
        this.stepTwoFormPropertiesFormGroup.controls['dataType']?.disable();
      }
    });
    this.#subscription.add(subscription2);

    const subscription3: Subscription = this.stepTwoFormPropertiesFormGroup.controls['dataType'].valueChanges.subscribe((value) => {
      if (this.formMode !== 'view') {
        if (value !== 'String') {
          this.stepTwoFormPropertiesFormGroup.controls['data'].disable({emitEvent: false, onlySelf: false});
        } else {
          this.stepTwoFormPropertiesFormGroup.controls['data'].enable({emitEvent: false, onlySelf: false});
        }
      } else {
        this.stepTwoFormPropertiesFormGroup.controls['data']?.disable();
      }
    });
    this.#subscription.add(subscription3);

    const subscription4: Subscription = this.stepTwoFormPropertiesFormGroup.controls['displayName'].valueChanges.subscribe((value) => {
      const fields = this.fieldListItems.filter((field, index) => index !== this.stepTwoFormPropertiesFormGroup.getRawValue()?.orderId);
      const fieldNameExisted = fields.some(field => {
        return field.field?.trim() === value.trim();
      });

      if (fieldNameExisted) {
        this.stepTwoFormPropertiesFormGroup.controls['displayName'].setErrors({
          incorrect: true,
          message: 'directory.definition.edit.message.fieldNotUnique',
        })
      }
    });
    this.#subscription.add(subscription4);

    const subscription5: Subscription = this.stepTwoFormPropertiesFormGroup.controls['maxLengthEnabled'].valueChanges.subscribe((value) => {
      if (value) {
        if (this.formMode !== 'view') {
          this.stepTwoFormPropertiesFormGroup.controls['mask'].disable({emitEvent: false, onlySelf: false});
          this.stepTwoFormPropertiesFormGroup.controls['maxLength'].enable({emitEvent: false, onlySelf: false});
        } else {
          this.stepTwoFormPropertiesFormGroup.controls['mask']?.disable();
          this.stepTwoFormPropertiesFormGroup.controls['maxLength']?.disable();
        }
        this.stepTwoFormPropertiesFormGroup.patchValue({maskEnabled: false}, {emitEvent: false, onlySelf: false});
      }
    });
    this.#subscription.add(subscription5);

    const subscription6: Subscription = this.stepTwoFormPropertiesFormGroup.controls['maskEnabled'].valueChanges.subscribe((value) => {
      if (value) {
        if (this.formMode !== 'view') {
          this.stepTwoFormPropertiesFormGroup.controls['mask'].enable({emitEvent: false, onlySelf: false});
          this.stepTwoFormPropertiesFormGroup.controls['maxLength'].disable({emitEvent: false, onlySelf: false});
        } else {
          this.stepTwoFormPropertiesFormGroup.controls['mask']?.disable();
          this.stepTwoFormPropertiesFormGroup.controls['maxLength']?.disable();
        }
        this.stepTwoFormPropertiesFormGroup.patchValue({maxLengthEnabled: false}, {emitEvent: false, onlySelf: false});
      }
    });
    this.#subscription.add(subscription6);
  }

  get MaxLengthVisible(): boolean {
    return (
      this.stepTwoFormPropertiesFormGroup.get('dataType')?.value === 'String' ||
      this.stepTwoFormPropertiesFormGroup.get('dataType')?.value === 'Number' ||
      this.stepTwoFormPropertiesFormGroup.get('dataType')?.value === 'Integer' ||
      this.stepTwoFormPropertiesFormGroup.get('dataType')?.value === 'Date'
    );
  }

  get MaskDateVisible(): boolean {
    return (
      this.stepTwoFormPropertiesFormGroup.get('dataType')?.value === 'String' ||
      this.stepTwoFormPropertiesFormGroup.get('dataType')?.value === 'Date'
    );
  }

  /**
   * Validate, add, or update a field.
   */
   validateFieldPropertiesEvent(): void {

    const mask = this.stepTwoFormPropertiesFormGroup.controls['mask'];
    // mask.setErrors(null)

    if (this.stepTwoFormPropertiesFormGroup.invalid) {
      this.showError = true;
      return;
    }
    const rawValue = this.stepTwoFormPropertiesFormGroup.getRawValue();

    if (rawValue.dataType == "String" || rawValue.dataType == "Date") {
      if (rawValue.maskEnabled && !rawValue.mask && rawValue.get) {
        mask.setErrors({
          incorrect: true,
          message: 'directory.definition_directory_modify_step_2_error.mask_require',
        });
        this.showError = true
        return;
      }
    }

    if (this.formMode == "modify" && rawValue.id != null) {

      if (rawValue.presence === "mandatory") {
        if (rawValue.canModifyToRequire != undefined && !rawValue.canModifyToRequire) {
          this.translateService.get('directory.definition_directory_modify_step_2_error.cannot_set_to_require').subscribe(value => {
            this.snackbar.openCustomSnackbar({
              icon: 'close',
              type: 'error',
              message: value
            });
          });
          return;
        }
      }

      if (rawValue.key) {
        if (rawValue.canModifyToKey != undefined && !rawValue.canModifyToKey) {
          this.translateService.get('directory.definition_directory_modify_step_2_error.cannot_set_to_key').subscribe(value => {
            this.snackbar.openCustomSnackbar({
              icon: 'close',
              type: 'error',
              message: value
            });
          });
          return;
        }
      }


      if (rawValue.maxLengthEnabled && rawValue.minLength != null && rawValue.minLength != 0) {
        if (rawValue.maxLength < rawValue.minLength) {
          this.translateService.get('directory.definition_directory_modify_step_2_error.cannot_change_length').subscribe(value => {
            this.snackbar.openCustomSnackbar({
              icon: 'close',
              type: 'error',
              message: value
            });
          });
          return;
        }
      }

      if (rawValue.id != 0) {
        const {id} = this.activatedRoute.snapshot.queryParams;
        this.directoryService.getValidateType(id, rawValue.id, rawValue?.dataType, rawValue?.mask || "").subscribe((value: MaskAndTypeValidationResponse) => {
          if (value != undefined) {
            //in progress
            if (!value.type) {
              this.translateService.get('directory.definition_directory_modify_step_2_error.cannot_change_type').subscribe(value => {
                this.snackbar.openCustomSnackbar({
                  icon: 'close',
                  type: 'error',
                  message: value
                });
              });
            }
            if (!value.mask) {
              this.translateService.get('directory.definition_directory_modify_step_2_error.cannot_set_new_mask').subscribe(value => {
                this.snackbar.openCustomSnackbar({
                  icon: 'close',
                  type: 'error',
                  message: value
                });
              });
            }
          }
          if (value.mask && value.type) {
            this.processField();
          } else {
            this.resetForm();
          }
        });
      }
    } else {
      this.processField();
    }

  }
  private processField() {
      this.fieldListItemValueChangesEvent.emit(this.getUpdateFields());
      this.resetForm();
      this.formStep2EditorChangeEvent.emit({});
  }
  /**
   * Get the updated fields.
   */
  private getUpdateFields(): DirectoryField[] {
    const rawValue = this.stepTwoFormPropertiesFormGroup.getRawValue();
    const data = DataKeyPair.find(element => element.key === rawValue.data)?.value;

    const adjustField: DirectoryField = {
      key: rawValue.key,
      field: rawValue.displayName.trim(),
      type: rawValue.dataType,
      id: rawValue.id,
      canModifyToRequire: rawValue.canModifyToRequire,
      canModifyToKey: rawValue.canModifyToKey,
      properties: {
        data,
        displayName: rawValue.displayName.trim(),
        option: {
          length: rawValue?.maxLengthEnabled ? rawValue.maxLength : '',
          mask: rawValue?.maskEnabled ? rawValue.mask : '',
        },
        required: rawValue.presence !== 'optional',
      },
      fieldOrder: this.fieldValues.length + 1,
    };

    let items: DirectoryField[] = [];

    const orderId = this.stepTwoFormPropertiesFormGroup.getRawValue().orderId;
    if (orderId !== -1) {
      items = this.fieldValues.map((field, index) => {
        if (orderId === index) {
          return adjustField;
        } else {
          return field;
        }
      })
    } else {
      items = [
        ...this.fieldValues,
        adjustField,
      ];
    }

    items = items.map((item, index) => ({...item, fieldOrder: index + 1}));

    return items;
  }

  /**
   * Reset form.
   */
  private resetForm(): void {
    this.stepTwoFormPropertiesFormGroup.reset({}, {emitEvent: false, onlySelf: false});
    this.formDirectoryFieldPropertiesSelected = false;
  }

  /**
   * Select a data type.
   * @param dataType
   */
  selectDataTypeEvent(dataType: any): void {

    if (dataType == "Number" || dataType == "Integer" || dataType == "String" || dataType == "Date") {
      this.stepTwoFormPropertiesFormGroup.patchValue({
        maxLengthEnabled: true,
        maskEnabled: false,
        maxLength: 255,
      });
    } else if (dataType == "Boolean") {
      this.stepTwoFormPropertiesFormGroup.patchValue({
        maxLengthEnabled: false,
        maskEnabled: false,
        maxLength: 255,
      });
    } else {
      this.stepTwoFormPropertiesFormGroup.patchValue({
        maxLengthEnabled: false,
        maskEnabled: true,
        maxLength: 255
      });
    }

    this.stepTwoFormPropertiesFormGroup.patchValue({dataType});
  }

  /**
   * Select a data.
   * @param data
   */
  selectDataEvent(data: any): void {
    if (data != "directory_field_data_field_specific") {
      this.stepTwoFormPropertiesFormGroup.patchValue({
        maxLengthEnabled: false,
        maskEnabled: true,
      });
    }else{
      this.stepTwoFormPropertiesFormGroup.patchValue({
        maxLengthEnabled: true,
        maskEnabled: false,
        maxLength: 255,
      });
    }
    this.stepTwoFormPropertiesFormGroup.patchValue({data});
  }

  /**
   * Select presence as optional or mandatory.
   * @param presence
   */
  selectPresenceEvent(presence: any): void {
    this.stepTwoFormPropertiesFormGroup.patchValue({presence});
  }

  /**
   * Navigate to previous step.
   */
  goPreviousStep(): void {
    this.stepTwoPreviousPageEvent.next(true);
  }

  /**
   * Navigate to next step.
   */
  goNextStep(): void {
    if (this.formMode !== 'view') {
      if (this.fieldValues.length === 0) {
        this.translateService.get('directory.definition_directory_create_step_2_is_required').toPromise().then(message => {
          this.snackbar.openCustomSnackbar({
            type: 'error',
            message,
            icon: 'close',
          })
        });

        return;
      } else if (!this.fieldValues.find(field => field.key)) {
        this.translateService.get('directory.definition_directory_create_step_2_key_is_required').toPromise().then(message => {
          this.snackbar.openCustomSnackbar({
            type: 'error',
            message,
            icon: 'close',
          })
        });

        return;
      }

      this.stepTwoNextPageEvent.next(true);
    } else {
      this.stepTwoNextPageEvent.next(true);
    }
  }

  defaultPreFillsFormValue() {
    this.preFillsFormValue = {
      displayName: '',
      dataType: 'String',
      key: this.fieldKeyVisible,
      data: 'directory_field_data_field_specific',
      presence: 'mandatory',
      mask: '',
      maxLength: '',
      id: 0,
      canModifyToRequire: false,
      canModifyToKey: false,
      minLength: 0
    };
  }

  get disablePresent(): boolean {
    return (
      this.stepTwoFormPropertiesFormGroup.controls["presence"].disabled ||
      this.warningAddNewField ||
      this.stepTwoFormPropertiesFormGroup.get('id')?.value === null && this.alreadyFeed
    );
  }

  get showWarning(): boolean {
    return (
      this.warningAddNewField && this.formMode === "modify" ||
      this.stepTwoFormPropertiesFormGroup.get('id')?.value === null && this.alreadyFeed
    );
  }


}
