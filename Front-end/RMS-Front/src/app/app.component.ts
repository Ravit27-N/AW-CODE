import {Component} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router} from '@angular/router';
import {filter} from 'rxjs/operators';
import {UserService} from './auth';
import {ErrorMessageHandler, IErrorMessage} from './core';
import {ErrorMessageSnackbarComponent} from './shared';
import {IsLoadingService} from '@service-work/is-loading';
import {BehaviorSubject, Observable} from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'admin-dashboard-angular';
  isLoading$: Observable<boolean>;
  isReadyToStart = new BehaviorSubject<boolean>(false);

  message: any = {};

  constructor(
    private userService: UserService,
    router: Router,
    private errorHandler: ErrorMessageHandler,
    private dialog: MatDialog,
    private snackbar: MatSnackBar,
    private isLoadingService: IsLoadingService) {

    this.userService.isRoleloaded$.subscribe(value => this.isReadyToStart.next(value));

    // initial authentication
    this.userService.runInitialLoginSequence();

    // Clear any dialog when change routes
    router.events.pipe(
      filter(
        (event) =>
          event instanceof NavigationStart ||
          event instanceof NavigationEnd ||
          event instanceof NavigationCancel ||
          event instanceof NavigationError
      )
    ).subscribe((val) => {
      if (val instanceof NavigationEnd) {
        if (this.dialog.openDialogs.length > 0) {
          this.dialog.closeAll();
        }

        this.isLoadingService.remove({key: 'on-route'});
      }

      if (val instanceof NavigationStart) {
        this.isLoadingService.add({key: 'on-route', unique: 'on-route'});
      }
    });

    this.errorHandler.errorMessages.subscribe((errorMessage: IErrorMessage) => {
      // Custom code auto logout when 401
      if (errorMessage.code === 401) {
        errorMessage.action = () => this.userService.logout();
        errorMessage.actionName = 'Logout';
      }else {
        this.snackbar.openFromComponent(ErrorMessageSnackbarComponent, {
          duration: 6000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
          data: errorMessage,
        });
      }
    });
  }
}
