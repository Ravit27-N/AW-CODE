import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FeatureCreateUpdateTemplatePopupComponent } from './feature-create-update-template-popup.component';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { initFormChange, modelNameChangeEvent } from '@cxm-smartflow/template/data-access';
import { take } from 'rxjs/operators';

@Injectable()
export class CreateUpdateTemplatePopupService {
  dialogRef: MatDialogRef<FeatureCreateUpdateTemplatePopupComponent>;

  constructor(private matDialog: MatDialog, private store: Store) {}

  public showCreateTemplatePopup(
    sourceTemplateId?: number | null,
    modelType?: string
  ): Observable<any> {
    this.dialogRef = this.matDialog.open(
      FeatureCreateUpdateTemplatePopupComponent,
      {
        width: '676px',
        height: '305px',
        panelClass: 'custom-template-popup',
        disableClose: true,
        data: {
          sourceTemplateId,
          modelType,
        },
      }
    );

    // Create modelName required.
    this.dialogRef.afterClosed().subscribe(() => {
      this.store.dispatch(modelNameChangeEvent({isRequired: false}))
    });

    return <Observable<any>>this.dialogRef.afterClosed();
  }
}
