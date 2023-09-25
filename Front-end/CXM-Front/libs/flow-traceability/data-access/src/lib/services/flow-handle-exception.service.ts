import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';

@Injectable({
  providedIn: 'root',
})
export class FlowHandleExceptionService {
  constructor(
    private route: Router,
    private confirmService: ConfirmationMessageService,
    private snackbar: SnackBarService
  ) {}

  handles(statusCode: number, messages: any, navigateRoute: string) {
    {
      if (statusCode === 403 || statusCode === 401) {
        this.confirmService
          .showConfirmationPopup({
            icon: 'close',
            title: messages.message?.unauthorizedTitle,
            message: messages.message?.unauthorizedMessage,
            cancelButton: messages.cancelButton,
            confirmButton: messages.unauthorizedLeave,
            type: 'Warning',
          })
          .subscribe(() => {
            this.route.navigateByUrl(navigateRoute);
          });
      } else if (statusCode === 404) {
        this.confirmService
          .showConfirmationPopup({
            icon: 'close',
            title: messages.title,
            message: messages.message,
            cancelButton: messages.cancelButton,
            confirmButton: messages.unauthorizedLeave,
            type: 'Warning',
          })
          .subscribe(() => {
            this.route.navigateByUrl(navigateRoute);
          });
      } else if (statusCode >= 500) {
        this.snackbar.openCustomSnackbar({
          message: messages.unknownError,
          icon: 'close',
          type: 'error',
        });
      }
    }
  }
}
