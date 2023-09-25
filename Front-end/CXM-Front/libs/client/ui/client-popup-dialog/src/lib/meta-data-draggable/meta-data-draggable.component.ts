import {
  AfterViewInit,
  ChangeDetectorRef,
  Component, ElementRef,
  EventEmitter,
  Input, OnDestroy,
  Output, ViewChild
} from '@angular/core';
import { MetadataModel } from '@cxm-smartflow/client/data-access';
import {
  ControlValueAccessor, FormBuilder, FormGroup,
  NG_VALUE_ACCESSOR,
} from '@angular/forms';
import { Subscription } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-meta-data-draggable',
  templateUrl: './meta-data-draggable.component.html',
  styleUrls: ['./meta-data-draggable.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: MetaDataDraggableComponent,
    },
  ],
})
export class MetaDataDraggableComponent implements ControlValueAccessor, OnDestroy, AfterViewInit {

  @Input() metadata: MetadataModel;
  @Input() errorMessage = '';
  @Input() errorDisplay = false;
  @Input() isLowercase = false;
  dragging = false;
  @Output() metadataRemove = new EventEmitter<MetadataModel>();
  @ViewChild('inputElement') inputElement: ElementRef;
  formGroup: FormGroup = this._formBuilder.group({
    metadata: [''],
  });
  disabled = false;
  onTouched: Function = () => {};
  onChangeSubs: Subscription[] = [];

  constructor(private _changeDetectorRef: ChangeDetectorRef, private _formBuilder: FormBuilder) {}

  ngAfterViewInit(): void {
    this._changeDetectorRef.detectChanges();
  }

  ngOnDestroy(): void {
    this.onChangeSubs.forEach(sub => sub.unsubscribe());
  }

  writeValue(value: any): void {
    if (value) {
      this.formGroup.setValue(value, { emitEvent: false });
    }
  }

  registerOnChange(onChange: any) {
    const sub = this.formGroup.valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub)
  }

  registerOnTouched(onTouched: any): void {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.formGroup.disable();
    } else {
      this.formGroup.enable();
    }
  }

  addTitle(inputElement: HTMLInputElement): string {
    return inputElement.clientWidth < inputElement.scrollWidth ? inputElement.value : '';
  }

  removeMetadata(): void {
    this.metadataRemove.emit(this.metadata);
  }
}
