import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FileConfigEditorComponent } from './file-config-editor.component';
import { Observable } from 'rxjs';
import { ConfigurationForm } from '@cxm-smartflow/client/data-access';

export enum ConfigEditorMode {
  ADD = 'Add',
  MODIFY = 'Modify',
  VIEW = 'View'
}

@Injectable({
  providedIn: 'root'
})
export class FileConfigEditorService {
  private _dialogRef: MatDialogRef<FileConfigEditorComponent>;

  constructor(private _matDialog: MatDialog) {}

  show(data: ConfigurationForm, modelNames: string[], editMode: ConfigEditorMode): Observable<any> {
    this._dialogRef = this._matDialog.open(FileConfigEditorComponent,
      {
        width: '1010px',
        data: { data, modelNames, editMode },
        panelClass: 'custom-change-password-pop-up-dialog',
        disableClose: true
      }
    );

    return <Observable<boolean>>this._dialogRef.afterClosed();
  }

  close(): void {
    this._dialogRef.close();
  }
}
