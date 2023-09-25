import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as fileUploadAction from './file-upload.action';
import { uploadFileDoneAction } from './file-upload.action';
import { catchError, delay, exhaustMap, map, tap } from 'rxjs/operators';
import { FlowDepositService } from '../../services/flow-deposit.service';
import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { of } from 'rxjs';
import { launchProcessControl } from '../flow-deposit';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { DepositManagement } from '@cxm-smartflow/shared/data-access/model';


@Injectable({providedIn: 'root'})
export class AcquisitionFileUploadEffect {

  fileDropActionEffect$ = createEffect(() => this.actions.pipe(
    ofType(fileUploadAction.dropFilesAction),
    // switchMap((args) => [fileUploadAction.uploadFileAction()])
    exhaustMap(args => this.flowDepositService.uploadFile(args.form)
      // .pipe(catchError(() => of(fileUploadAction.uploadFileFailAction())))
      .pipe(
        map(event => {
          if(event.type === HttpEventType.Sent) {
            return fileUploadAction.uploadFileAction();
          } else if (event.type === HttpEventType.UploadProgress) {
            if (event.total) {
              const progress = Math.round((event.loaded / event.total) * 100);
              return fileUploadAction.uploadFileProgression({progress});
            }
          } else if (event.type === HttpEventType.Response) {
            if (event.ok) {
              return fileUploadAction.uploadFileDoneAction({response: event.body});
            }
            else return fileUploadAction.uploadFileFailAction({ error: event.body });
          }
          return fileUploadAction.uploadFileAction();
        }),
        catchError((httpError: HttpErrorResponse) => {

          if(httpError.status == 405) {
            // 405 method not allowed cause from fm fail => 5002
            return of(fileUploadAction.uploadFileFailAction({ error: { statusCode: 5002 } }))
          }

          const { apierrorhandler } = httpError.error
          return of(fileUploadAction.uploadFileFailAction({ error: apierrorhandler }));
        }))
    )
  ));

  uploadFileDoneAction$ = createEffect(() => this.actions.pipe(
    ofType(uploadFileDoneAction),
    delay(2500),
    tap((res) => {
      this.store.dispatch(launchProcessControl({ request: res.response, funcKey: DepositManagement.CXM_FLOW_DEPOSIT, privKey: DepositManagement.MODIFY_A_DEPOSIT }));
    })
  ), { dispatch: false })

  constructor(private actions: Actions, private flowDepositService: FlowDepositService, private store: Store, private router: Router) {
  }
}
