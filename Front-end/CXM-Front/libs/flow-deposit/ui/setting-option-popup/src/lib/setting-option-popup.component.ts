import {Component, Inject, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {
  createWaterMark,
  fetchResources,
  fileUploadChange,
  KeyValue,
  PositionSetting,
  resetFormUploading,
  selectFileUploadedFileName,
  selectFileUploadedFileSize,
  selectIsHideUploadingArea,
  selectPdfInBase64,
  selectResourceData,
  selectSelectedAddResourceType,
  selectSelectedResource,
  selectSelectedSettingOptionPosition,
  selectSettingOptionAddResourceType,
  selectSettingOptionAllStates,
  selectSettingOptionPosition,
  selectTranslationMessages,
  selectWatermark,
  selectWatermarkPosition,
  SettingOptionCriteriaType,
  switchAddResourceType,
  switchAttachmentPosition,
  switchResourceLabel,
  switchWaterMarkPosition,
  updateAttachmentSettingOption,
  WatermarkAttribute,
} from '@cxm-smartflow/flow-deposit/data-access';
import {TranslateService} from '@ngx-translate/core';
import {Store} from '@ngrx/store';
import {BehaviorSubject, Observable, Subscription} from 'rxjs';
import {map} from 'rxjs/operators';
import {SettingOptionPopupValidation} from './setting-option-popup-validation';
import {SnackBarService, UserProfileUtil} from '@cxm-smartflow/shared/data-access/services';
import {EnrichmentMailing} from '@cxm-smartflow/shared/data-access/model';
import {WatermarkColorUtil} from "@cxm-smartflow/flow-deposit/util";

@Component({
  selector: 'cxm-smartflow-setting-option-popup',
  templateUrl: './setting-option-popup.component.html',
  styleUrls: ['./setting-option-popup.component.scss'],
})
export class SettingOptionPopupComponent implements OnInit, OnDestroy {

  // Messages.
  selectTranslationMessages$: Observable<any>;

  // Resource type.
  resourceTypes$: Observable<Array<KeyValue>>;
  resources$: Observable<Array<KeyValue>>;
  selectedResourceType$: Observable<string>;
  selectedResource$: Observable<string>;

  // Uploading area.
  isHideUploadingArea$: Observable<boolean>;
  fileName$: Observable<string>;
  fileSize$: Observable<string>;

  // Postions.
  positions$: Observable<Array<PositionSetting>>;
  selectedPosition$: Observable<string>;
  selectWatermarkPosition$: Observable<string>;
  // Preview.
  pdfInBase64$: Observable<string>;

  // Form.
  formGroup: FormGroup;

  // Unsubscribe.
  #unsubscription: Subscription;
  #unsubscriptionWaterMark: Subscription;

  // Privileges.
  isCanUpload = UserProfileUtil.canAccess(EnrichmentMailing.CXM_ENRICHMENT_MAILING, EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE);
  isCanUsingLibrary = UserProfileUtil.canAccess(EnrichmentMailing.CXM_ENRICHMENT_MAILING, EnrichmentMailing.USE_RESOURCE_IN_LIBRARY);

  // Icons.
  icon = 'assets/icons/icon-file-pdf.png';
  acceptExtension = '.pdf';
  updatedIcon = 'assets/images/pdf2.png';

  color: string;

  watermarkFormGroup: FormGroup;
  isColorPicker = false;

  colorPreset: string[] = WatermarkColorUtil.COLOR.map(value => {
    return value.hex
  });

  contextMenuPosition:any;
  error$ = new BehaviorSubject<boolean>(false);
  @ViewChild('colorPicker') colorPicker: any;
  constructor(
    @Inject(MAT_DIALOG_DATA)
    public config: SettingOptionCriteriaType,
    private _dialogRef: MatDialogRef<SettingOptionPopupComponent>,
    private _formBuilder: FormBuilder,
    private _translationService: TranslateService,
    private _snackbar: SnackBarService,
    private _store: Store,
  ) {}

  ngOnInit(): void {
    this._setup();
  }

  ngOnDestroy(): void {
    this.#unsubscription.unsubscribe();
    this.formGroup.reset();
    this.#unsubscriptionWaterMark.unsubscribe();
    this.watermarkFormGroup.reset();
  }

  private _setup(): void {
    // Form.
    this.formGroup = this._formBuilder.group({
      sourceFrom: new FormControl(''),
      choiceOfPage: new FormControl('', [SettingOptionPopupValidation.fieldChoiceOfAttachement()]),
      positionMode: new FormControl(''),
      fileUploadingId: new FormControl('', [SettingOptionPopupValidation.fieldUploading()]),
    });


    this.#unsubscription = this._store.select(selectSettingOptionAllStates).subscribe(state => {
      this.formGroup.patchValue({
          sourceFrom: state.selectedAddResourceType,
          choiceOfPage: state.selectedResource,
          positionMode: state.selectedAttachmentPosition,
          fileUploadingId: state.uploadingFileId,
      });
    });

    // Messages.
    this.selectTranslationMessages$ = this._store.select(selectTranslationMessages);

    // Add resource types.
    this.resourceTypes$ = this._store.select(selectSettingOptionAddResourceType);
    this.selectedResourceType$ = this._store.select(selectSelectedAddResourceType);

    // Resources.
    if(this.isCanUsingLibrary) {
      this._store.dispatch(fetchResources({ popupType: this.config }));
    }
    this.resources$ = this._store.select(selectResourceData).pipe(map(collections => {
      return collections.map((item): KeyValue => ({ value: item.label, key: `${item.label}` }));
    }));
    this.selectedResource$ = this._store.select(selectSelectedResource);
    // Uploading area.
    this.isHideUploadingArea$ = this._store.select(selectIsHideUploadingArea);
    this.fileName$ = this._store.select(selectFileUploadedFileName);
    this.fileSize$ = this._store.select(selectFileUploadedFileSize);
    this.pdfInBase64$ = this._store.select(selectPdfInBase64);
    if (this.config === 'Signature') {
      this.icon = 'assets/icons/icon-file-png.png';
      this.updatedIcon = 'assets/icons/png.png';
      this.acceptExtension = '.png';
    }

    // Position.
    this.positions$ = this._store.select(selectSettingOptionPosition);
    this.selectedPosition$ = this._store.select(selectSelectedSettingOptionPosition);

    this.watermarkFormGroup = this._formBuilder.group({
      text: new FormControl("", [SettingOptionPopupValidation.waterMarkText()]),
      position: new FormControl(''),
      size: new FormControl(1, [SettingOptionPopupValidation.watermarkSize()]),
      rotation: new FormControl(0, [SettingOptionPopupValidation.watermarkRotation()]),
      color: new FormControl("RED"),
    });

    this.#unsubscriptionWaterMark = this._store.select(selectWatermark).subscribe(state => {
      if (state) {
        this.watermarkFormGroup.patchValue({
          text: state.text,
          position: state.position,
          size: state.size,
          rotation: state.rotation,
          color: WatermarkColorUtil.textToColorPicker(state.color)
        });
      }
    });

    this.selectWatermarkPosition$ = this._store.select(selectWatermarkPosition);
  }
  /**
   * Close modal.
   */
  closeModal(): void {
    this._dialogRef.close();
  }

  /**
   * Validate and submit form.
   */
  submitForm(): void {
    if (this.config != "Watermark") {
      if (this.formGroup.getRawValue().sourceFrom === '1' && !this.isCanUsingLibrary) {
        this._dialogRef.close();
        return;
      }

      if (this.formGroup.getRawValue().sourceFrom === '2' && !this.isCanUpload) {
        this._dialogRef.close();
        return;
      }

      if (this._validateForm()) {
        return;
      }
      this._store.dispatch(
        updateAttachmentSettingOption({popupType: this.config})
      );
      this._dialogRef.close();
    } else {

      if (this.watermarkFormGroup.invalid) {
        this.error$.next(true);
        return;
      }

      const {text, size, rotation, color} = this.watermarkFormGroup.getRawValue();

      const watermarkAttribute: WatermarkAttribute = {
        id: 0,
        text: text.trim(),
        position: "",
        size: size,
        rotation: rotation,
        color: color ? color : "#ff0000",
        flowId: "",
      }

      this._store.dispatch(createWaterMark({waterMarkAttribute: watermarkAttribute}));
      this._dialogRef.close();
    }

  }

  private _validateForm(): boolean {
    if (this.formGroup.controls['choiceOfPage'].invalid) {
      if (this.config === 'Attachment') {
        this._translationService.get('flow.deposit.setting_option_popup_background_select_page_attachment').toPromise().then(message => {
          this._snackbar.openCustomSnackbar({ type: 'error', message, icon: 'close' });
        });
      } else if(this.config === 'Background') {
        this._translationService.get('flow.deposit.setting_option_popup_background_select_page_background').toPromise().then(message => {
          this._snackbar.openCustomSnackbar({ type: 'error', message, icon: 'close' });
        });
      }

    } else if(this.formGroup.controls['fileUploadingId'].invalid) {
      this._translationService.get('flow.deposit.setting_option_popup_uploading_file_is_required').toPromise().then(message => {
        this._snackbar.openCustomSnackbar({ type: 'error', message, icon: 'close' });
      });
    }

    return this.formGroup.invalid;
  }

  /**
   * Attachment position changes.
   * @param position
   */
  positionChanged(position: string): void {
    this._store.dispatch(switchAttachmentPosition({ position }));
  }

  /**
   * Selection type changes.
   * @param selectResourceType
   */
  selectionTypeChange(selectResourceType: string): void {
    this._store.dispatch(switchAddResourceType({ selectResourceType, popupType: this.config }));
  }

  /**
   * Handle file changes.
   * @param fileList
   */
  handleFileChange(fileList: FileList): void {
    if (fileList.length > 1) {
      this._translationService.get('cxm_setting.attachment_invalid_total_file').toPromise().then(message => {
        this._snackbar.openCustomSnackbar({ type: 'error', message, icon: 'close' });
      });

      return;
    }
    this._store.dispatch(fileUploadChange({ files: Array.from(fileList), popupType: this.config }));
  }

  /**
   * Select a resource.
   * @param label
   */
  selectResource(label: string): void {
    this._store.dispatch(switchResourceLabel({ label }));
  }

  /**
   * Reset file uploading.
   */
  resetUploadingForm(): void {
    this._store.dispatch(resetFormUploading());
  }

  positionWaterMarkChanged(waterMarkPosition: string): void {
    this._store.dispatch(switchWaterMarkPosition({waterMarkPosition}));
  }

  toggle() {
    this.isColorPicker = !this.isColorPicker;
  }

  colorChange(color: string) {
    this.watermarkFormGroup.patchValue({
      color: color
    });
  }

  onRightClick(event: MouseEvent) {
    event.preventDefault(); // Prevent the default browser context menu
    this.isColorPicker = !this.isColorPicker;
    this.contextMenuPosition = { x: event.clientX, y: event.clientY };
    this.contextMenuPosition.x = this.contextMenuPosition.x - (innerWidth / 2) + 'px';
    this.contextMenuPosition.y = this.contextMenuPosition.y - (innerHeight / 2) + 'px';
  }

}
