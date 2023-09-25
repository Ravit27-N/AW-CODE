import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import {
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar/snack-bar-config';
import { AwSnackbarComponent } from './aw-snackbar.component';

export interface AwSnackbarModel {
  message?: string;
  icon?: string;
  type?: 'success' | 'info' | 'error';
  details?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AwSnackbarService {
  private config: MatSnackBarConfig;
  private defaultDuration = 5000;
  private defaultVerticalPosition: MatSnackBarVerticalPosition = 'top';
  private defaultHorizontalPosition: MatSnackBarHorizontalPosition = 'right';

  constructor(private snackBar: MatSnackBar) {
    this.config = new MatSnackBarConfig();
    this.setupDefaultConfig();
  }

  private setupDefaultConfig(): void {
    this.config.duration = this.defaultDuration;
    this.config.verticalPosition = this.defaultVerticalPosition;
    this.config.horizontalPosition = this.defaultHorizontalPosition;
  }

  /**
   * New snackbar message.
   *
   * @param data - value of {@link AwSnackbarModel}.
   * @param duration - value of {@link number}.
   */
  openCustomSnackbar(data?: Partial<AwSnackbarModel>, duration?: number): void {
    const finalDuration =
      duration && duration > 0 ? duration : this.defaultDuration;
    const snackbarData = {
      ...data,
      preClose: () => {
        this.snackBar.dismiss();
      },
    };

    this.snackBar.openFromComponent(AwSnackbarComponent, {
      duration: finalDuration,
      horizontalPosition: this.defaultHorizontalPosition,
      verticalPosition: this.defaultVerticalPosition,
      panelClass: ['custom-aw-snackbar', 'aw-snackbar-bottom-right'],
      data: snackbarData,
    });
  }
}
