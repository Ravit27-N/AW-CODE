import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarRef } from '@angular/material/snack-bar';
import { DirectoryFeedSelectionComponent } from './directory-feed-selection.component';
import { Observable } from 'rxjs';

export interface DirectoryFeedSelection {
  isDelete(): Observable<boolean>;

  isValidate(): Observable<boolean>;

  isCancel(): Observable<boolean>;

  enableClose(): Observable<boolean>;

  selectedModified(): Observable<number>;

  selectedDelete(): Observable<number>;

  onDelete(): void;

  onValidate(): void;

  onCancel(): void;
}

@Injectable({
  providedIn: 'root',
})
export class DirectoryFeedSelectionServiceService {
  constructor(private matSnackbar: MatSnackBar) {}

  onOpen(
    data: DirectoryFeedSelection
  ): MatSnackBarRef<DirectoryFeedSelectionComponent> {
    return this.matSnackbar.openFromComponent(DirectoryFeedSelectionComponent, {
      panelClass: 'cxm-selection-snackbar-container',
      data,
    });
  }

  onClose() {
    this.matSnackbar?.dismiss();
  }
}
