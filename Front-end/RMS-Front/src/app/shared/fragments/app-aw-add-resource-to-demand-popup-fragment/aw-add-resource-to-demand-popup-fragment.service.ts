import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { AwAddResourceToDemandPopupFragmentComponent } from './aw-add-resource-to-demand-popup-fragment.component';
import { KeyValue } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class AwAddResourceToDemandPopupFragmentService {
  private dialogRef: MatDialogRef<AwAddResourceToDemandPopupFragmentComponent>;

  constructor(private dialog: MatDialog) {}

  chooseResource(
    resources: KeyValue<string, string>[],
    selectedSources: string[],
    requiredDemand: number
  ): Observable<number[] | undefined> {
    this.dialogRef = this.dialog.open(
      AwAddResourceToDemandPopupFragmentComponent,
      {
        width: '683px',
        data: {
          resources,
          selectedSources,
          requiredDemand
        },
        panelClass: 'custom-confirmation-popup',
      },
    );

    return this.dialogRef.afterClosed();
  }
}
