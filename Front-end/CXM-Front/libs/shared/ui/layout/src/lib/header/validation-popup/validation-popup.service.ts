import { Injectable } from '@angular/core';
import { ValidationPopupComponent } from './validation-popup.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, Subject } from 'rxjs';
import { take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ValidationPopupService {
  constructor(private _snackBar: MatSnackBar) {}

  open(): Observable<boolean> {
    const onClose = new Subject<boolean>();
    const data = {
      preClose: (response: boolean) => onClose.next(response),
    };

    this._snackBar.openFromComponent(ValidationPopupComponent, {
      horizontalPosition: 'right',
      verticalPosition: 'bottom',
      panelClass: ['common-notification-popup'],
      data,
    });

    return onClose.asObservable().pipe(take(1));
  }

  dismiss(): void {
    this._snackBar.dismiss();
  }
}
