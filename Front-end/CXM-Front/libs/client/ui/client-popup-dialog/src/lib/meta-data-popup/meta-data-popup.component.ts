import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MetadataModel, MetadataPayloadType, MetadataType } from '@cxm-smartflow/client/data-access';
import { moveItemInArray } from '@angular/cdk/drag-drop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { BehaviorSubject, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { MetaDataPopupValidator } from './meta-data-popup-validator';

@Component({
  selector: 'cxm-smartflow-meta-data-popup',
  templateUrl: './meta-data-popup.component.html',
  styleUrls: ['./meta-data-popup.component.scss'],
})
export class MetaDataPopupComponent implements OnInit, OnDestroy {

  metadataCollection: Array<MetadataModel> = [];
  formGroup: FormGroup = this._formBuilder.group({});
  #unsubscribe: Subscription;
  validateType: 'email' | 'sms' | 'sender_name' = 'sender_name';
  #responseMetadataCollection: Array<MetadataModel> = [];
  showError$ = new BehaviorSubject<boolean>(false);
  translationKey = '';

  constructor(
    @Inject(MAT_DIALOG_DATA)
    public config: { metadataType: MetadataPayloadType, data: Array<MetadataModel> },
    private _dialogRef: MatDialogRef<MetaDataPopupComponent>,
    private _formBuilder: FormBuilder,
  ) {
    this.#responseMetadataCollection = config.data.map((data, order) => ({ ...data, order }));
    this.metadataCollection = this.#responseMetadataCollection;

    if (config.metadataType === 'sender_mail' || config.metadataType === 'unsubscribe_link') {
      this.validateType = 'email';
    } else if (config.metadataType === 'sender_label') {
      this.validateType = 'sms';
    } else if (config.metadataType === 'sender_name') {
      this.validateType = 'sender_name';
    }

    this.translationKey = this.mapTranslationKey(config.metadataType);
  }

  ngOnInit(): void {
    this.config.data.forEach((metadata, index) => {
      this.formGroup.addControl(`${index}`, new FormControl({ metadata: metadata.value },
        [MetaDataPopupValidator.fields(this.config.metadataType), MetaDataPopupValidator.fieldDuplicated()]), { emitEvent: false });
    });
    this.formGroup.patchValue(this.formGroup.getRawValue());


    this.#unsubscribe = this.formGroup.valueChanges
      .pipe(
        map((form) => Object.entries(form).map((kv) => kv[1])),
      )
      .subscribe((data: Array<any>) => {
        this.#responseMetadataCollection = data.map((value, index): MetadataModel => {
          const found = this.#responseMetadataCollection[index];
          if (!found) {
            return { value: value.metadata, order: index + 1, id: 0 };
          }

          return { value: value.metadata, order: index + 1, id: found.id };
        });
        this.showError$.next(false);
    });
  }

  ngOnDestroy(): void {
    this.#unsubscribe.unsubscribe();
  }

  closeModal() {
    this._dialogRef.close();
  }

  add(): void {
    this.formGroup.addControl(
      `${ this.metadataCollection.length }`, new FormControl({ metadata: '' },
        [MetaDataPopupValidator.fields(this.config.metadataType), MetaDataPopupValidator.fieldDuplicated()])
    );
    this.metadataCollection = [...this.metadataCollection, { id: 0, value: '', order: this.metadataCollection.length }];
  }

  order($event: any): void {
    const { previousIndex, currentIndex } = $event;
    const metadata = [...this.#responseMetadataCollection];
    moveItemInArray(
      metadata,
      previousIndex,
      currentIndex,
    );

    // Reorder form value.
    this.metadataCollection = metadata.map((data, order) => ({ ...data, order }));

    // Reinitialize form.
    const hasError = this.showError$.value;
    this._reinitializeForm();
    if (hasError) {
      this.showError$.next(hasError);
    }
  }

  removeMetadata(i: number): void {
    this.metadataCollection = this.#responseMetadataCollection;
    this.metadataCollection.splice(i, 1);
    this.metadataCollection = this.metadataCollection.map((data, order) => ({ ...data, order }));
    this._reinitializeForm();
    this.formGroup.removeControl(`${this.#responseMetadataCollection.length - 1}`);
  }

  private _reinitializeForm(): void {
    this.metadataCollection.forEach((metadata, index) => {
      this.formGroup.patchValue({[`${index}`]: { metadata: metadata.value } }, { emitEvent: false });
    });

    this.formGroup.patchValue(this.formGroup.getRawValue());
  }

  register(): void {
    if (this.formGroup.invalid) {

      this.#responseMetadataCollection.forEach((data, index) => {
        this.formGroup.get(`${index}`)?.updateValueAndValidity();
      });

      this.showError$.next(true);
      return;
    }

    let response: Array<MetadataModel> = this.#responseMetadataCollection;

    if (this.config.metadataType === 'sender_mail') {
      response = response.map(metadata => ({ ...metadata, value: metadata.value.toLowerCase() }));
    }

    response = response.map((metadata, order): MetadataModel => {
      return { ...metadata, value: metadata.value.trim(), order: order + 1 };
    });

    this._dialogRef.close(response);
  }

  mapTranslationKey(key: MetadataPayloadType): MetadataType {
    switch (key) {
      case 'sender_name': return 'senderNameMetadata';
      case 'sender_mail': return 'senderEmailMetadata';
      case 'unsubscribe_link': return 'unsubscribeMetadata';
      case 'sender_label': return 'senderLabelMetadata';
    }
  }
}
