import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ManageResourceParams } from './manage-resource-popup.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map } from 'rxjs/operators';
import {
  checkDuplicateLabel,
  deleteResourceOrTemptFile,
  fileUploadChange,
  getTranslationMsg,
  resetUploadFile,
  resourcePopupValueChange,
  ResourceTypeConstant,
  selectIsLabelDuplicate,
  selectResourceCriteria,
  selectUploadPopupFileName,
  selectUploadPopupFileSize,
  validateCreateResourceForm
} from '@cxm-smartflow/setting/data-access';
import { Store } from '@ngrx/store';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-feature-manage-resource-popup',
  templateUrl: './feature-manage-resource-popup.component.html',
  styleUrls: ['./feature-manage-resource-popup.component.scss']
})
export class FeatureManageResourcePopupComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  labelError$: Observable<any>;
  typeError$: Observable<any>;
  fileSize$: Observable<any>;
  fileName$: Observable<any>;
  acceptExtension$: string;
  isLabelDuplicate$: Observable<any>;
  resourceCriteria$: Observable<any>;
  description$ = new BehaviorSubject('');
  isSelectTypeDisabled$ = new BehaviorSubject(false);
  isUploadedDisable$ = new BehaviorSubject(true);
  private _subscription: Subscription;
  private _checkLabelSubscription: Subscription;
  updatedIcon = 'assets/images/pdf2.png';
  icon = 'assets/images/pdf2.png';

  constructor(
    private _fb: FormBuilder,
    private _store$: Store,
    private _dialogRef: MatDialogRef<FeatureManageResourcePopupComponent>,
    private _translate: TranslateService,
    private _snackbar: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public userInfo: ManageResourceParams
  ) {
    this.formGroup = this._fb.group({
      label: new FormControl(''),
      type: new FormControl(''),
      file: new FormControl(undefined),
      isShowErrorMsg: new FormControl(false)
    });
  }

  ngOnInit(): void {
    this._store$.dispatch(getTranslationMsg());
    this.labelError$ = this.formGroup.valueChanges.pipe(map(raw => (raw.label?.trim().length === 0 && raw.isShowErrorMsg)));
    this.typeError$ = this.formGroup.valueChanges.pipe(map(raw => (raw.type?.trim().length === 0 && raw.isShowErrorMsg)));
    this.resourceCriteria$ = this._store$.select(selectResourceCriteria).pipe(
      map(rcs => rcs.filter(kv => kv.id == ResourceTypeConstant.background || kv.id == ResourceTypeConstant.attachment || kv.id == ResourceTypeConstant.signature)
        .map(kv => ({
          key: kv.id,
          value: kv.name
        }))));
    this.fileSize$ = this._store$.select(selectUploadPopupFileSize);
    this.fileName$ = this._store$.select(selectUploadPopupFileName);
    this.isLabelDuplicate$ = this._store$.select(selectIsLabelDuplicate);


    this._subscription = this.formGroup.valueChanges
      .pipe(distinctUntilChanged((prev, curr) => JSON.stringify(prev) === JSON.stringify(curr)))
      .subscribe((data) => {
        this.formGroup.controls['isShowErrorMsg'].patchValue(false);
        this._store$.dispatch(resourcePopupValueChange({ resourceType: data.type, label: data.label }));
      });

    // Check duplicate resource by event of label control.
    this._checkLabelSubscription = this.formGroup.controls['label'].valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged((prev, curr) => JSON.stringify(prev) === JSON.stringify(curr)),
        filter(e => (e.trim().length > 0) && (this.typeControl()?.value as string)?.trim()?.length > 0)
      )
      .subscribe(name => {
        this._store$.dispatch(checkDuplicateLabel({ name, resourceType: (this.typeControl()?.value as string) }));
      });

    // Check duplicate resource by event of type control.
    this.typeControl().valueChanges.pipe(
      distinctUntilChanged(),
      filter(e => (e as string)?.trim().length > 0 && (this.labelControl()?.value as string)?.trim()?.length > 0)
    ).subscribe((value) => {
      this._store$.dispatch(checkDuplicateLabel({ name: (this.labelControl()?.value as string), resourceType: value }));
    });

    // Switch description by using value change event of type control.
    this.typeControl().valueChanges.pipe(distinctUntilChanged())
      .subscribe(value =>
        this.switchDescription(value)
      );

    // Disabled combo box when file has select or dropped.
    this.fileSize$.subscribe((value: string) => {
      this.isSelectTypeDisabled$.next(value.length > 0);
    });

    // Enabled uploaded component.
    this.typeControl().valueChanges.subscribe(() => this.isUploadedDisable$.next(false));

    //Enabled uploaded pdf.
    this.typeControl().valueChanges.subscribe((value) => {

      if( !this.isUploadedDisable$.getValue() && (ResourceTypeConstant.background==value || ResourceTypeConstant.attachment==value)){
        this.acceptExtension$=".pdf";
        this.updatedIcon = 'assets/images/pdf2.png';
        this.icon = 'assets/icons/icon-file-pdf.png';
      }else if(!this.isUploadedDisable$.getValue() && ResourceTypeConstant.signature==value){
        this.acceptExtension$=".png";
        this.updatedIcon = 'assets/icons/png.png';
        this.icon="assets/icons/icon-file-png.png";
      }
    });
  }

  ngOnDestroy(): void {
    this._subscription.unsubscribe();
    this._checkLabelSubscription.unsubscribe();
    this.description$.unsubscribe();
    this.isSelectTypeDisabled$.unsubscribe();
    this.isUploadedDisable$.unsubscribe();
  }

  closeModal(): void {
    this.formGroup.reset();
    this._dialogRef.close(false);
  }

  submit() {
    this.formGroup.controls['isShowErrorMsg'].patchValue(true);
    this._store$.dispatch(validateCreateResourceForm({ formType: 'create' }));
  }

  typeValueChange($event: any) {
    this.formGroup.controls['type'].patchValue($event);
  }

  fileChange(fileList: FileList) {
    this._store$.dispatch(fileUploadChange({ files: [...Array.from(fileList)], resourceType: this.formGroup.get('type')?.value}));
  }

  resetFile() {
    this._store$.dispatch(resetUploadFile());
    this._store$.dispatch(deleteResourceOrTemptFile({ deleteType: 'temp' }));
  }

  /**
   * Get property (label) of form control.
   * @return value of {@link AbstractControl}.
   */
  labelControl(): AbstractControl {
    return this.formGroup.controls['label'];
  }

  /**
   * Get property (type) of form control.
   * @return value of {@link AbstractControl}.
   */
  typeControl(): AbstractControl {
    return this.formGroup.controls['type'];
  }

  /**
   * Method used to switch description by resource type.
   * @param type - resource type {@link string}.
   */
  switchDescription(type: string): void {
    this._translate.get('cxm_setting').subscribe(jsonObject => {
      if (ResourceTypeConstant.background === type) {
        this.description$.next(jsonObject?.backgroundDescription);
      }

      if (ResourceTypeConstant.attachment === type) {
        this.description$.next(jsonObject?.attachmentDescription);
      }

      if (ResourceTypeConstant.signature === type) {
        this.description$.next(jsonObject?.signatureDescription);
      }
    });
  }
}
