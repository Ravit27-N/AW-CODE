import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { KeyValue } from '@angular/common';

@Component({
  selector: 'app-aw-add-resource-to-demand-popup-fragment',
  templateUrl: './aw-add-resource-to-demand-popup-fragment.component.html',
  styleUrls: ['./aw-add-resource-to-demand-popup-fragment.component.scss'],
})
export class AwAddResourceToDemandPopupFragmentComponent {
  selectedResources: string[];
  isFormError = false;

  constructor(
    private dialogRef: MatDialogRef<AwAddResourceToDemandPopupFragmentComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      resources: KeyValue<string, string>[];
      selectedSources: string[];
      requiredDemand: number;
    },
  ) {
    this.selectedResources = data.selectedSources;
  }

  emitDialog(value: string[] | undefined): void {
    if (value?.length > this.data.requiredDemand) {
      this.isFormError = true;
      return;
    }
    this.dialogRef.close(value);
  }

  selectSources($event: string[]): void {
    this.isFormError = false;
    this.selectedResources = $event;
  }
}
