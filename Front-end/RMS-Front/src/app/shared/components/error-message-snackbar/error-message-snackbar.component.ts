import { Component, Inject, OnDestroy } from '@angular/core';
import { MatSnackBarRef, MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { IErrorMessage } from 'src/app/core';

@Component({
  selector: 'app-error-message-snackbar',
  templateUrl: './error-message-snackbar.component.html',
  styleUrls: ['./error-message-snackbar.component.css']
})
export class ErrorMessageSnackbarComponent implements OnDestroy {

  private actionSubscription: Subscription = null;

  constructor(
    public snackbar: MatSnackBarRef<ErrorMessageSnackbarComponent>,
    @Inject(MAT_SNACK_BAR_DATA) public data: IErrorMessage
  ) {
    if(data.action) {
      this.actionSubscription = this.snackbar.onAction().subscribe(() => {
        this.data.action();
      });
    }
   }

  ngOnDestroy(): void {
    if(this.actionSubscription) {
      this.actionSubscription.unsubscribe();
    }
  }
}
