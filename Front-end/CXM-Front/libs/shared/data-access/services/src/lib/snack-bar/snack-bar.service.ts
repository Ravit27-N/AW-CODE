import { Injectable } from '@angular/core';
import {MatSnackBar, MatSnackBarConfig, MatSnackBarRef, SimpleSnackBar} from '@angular/material/snack-bar';
import { IconSnackbarComponent } from './icon-snackbar.component';
import { SnackbarModel } from './snackbar.model';

@Injectable({
  providedIn: 'root'
})
export class SnackBarService {
  private config: MatSnackBarConfig;
  constructor(private snackBar: MatSnackBar) {
    this.config = new MatSnackBarConfig();
    this.config.duration = 5000;
    this.config.verticalPosition = 'top';
    this.config.horizontalPosition = 'right';
  }

  /**
   *
   * @param message The message of snack bar to show the user.
   * @param duration The length of time in milliseconds to wait before automatically dismissing the snack bar.
   * @param action
   */

  openError(message: string, duration?: number, action?: string) {
    this.config.panelClass = ['snack-bar-error-bg-color', 'classic-style'];
    this.config = duration ? Object.assign(this.config, { 'duration': duration },) : this.config;
    this.snackBar.open(message, action, this.config);

    // this.openCustomSnackbar({message: message, icon: 'close', type: 'error'}, duration);
  }

  openSuccess(message: string, duration?: number, action?: string) {
    this.config.panelClass = ['snack-bar-sucess-bg-color', 'classic-style'];
    this.config = duration ? Object.assign(this.config, { 'duration': duration },) : this.config;
    this.snackBar.open(message, action, this.config);

    // this.openCustomSnackbar({message: message, icon: 'close', type: 'info'}, duration);

  }

  openWarning(message: string, duration?: number, action?: string) {
    this.config.panelClass = ['snack-bar-warning-bg-color', 'classic-style'];
    this.config = duration ? Object.assign(this.config, { 'duration': duration },) : this.config;
    this.snackBar.open(message, action, this.config);

    // this.openCustomSnackbar({message: message, icon: 'close', type: 'info'}, duration);
  }

  openInfo(message: string, duration?: number, action?: string) {
    this.config.panelClass = ['snack-bar-warning-bg-color', 'classic-style'];
    this.config = duration ? Object.assign(this.config, { 'duration': duration },) : this.config;
    this.snackBar.open(message, action, this.config);

    // this.openCustomSnackbar({message: message, icon: 'close', type: 'info'}, duration);
  }

  /**
   * New snackbar message message.
   * @param data - value of {@link SnackbarModel}.
   * @param duration - value of {@link number}.
   */
  openCustomSnackbar(data?: SnackbarModel, duration?: number){
    this.snackBar.openFromComponent(IconSnackbarComponent, {
      duration: (duration || 0) > 0 ? duration : 5000,
      horizontalPosition: 'right',
      verticalPosition: 'bottom',
      panelClass: ['custom-cxm-snackbar', 'cxm-snackbar-bottom-right'],
      data: {
        ...data,
        preClose: () => {this.snackBar.dismiss()}
      }
    })
  }
}
