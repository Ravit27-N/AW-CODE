import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { TranslateService } from '@ngx-translate/core';
import * as fromActions$ from './batch-user.action';
import { catchError, delay, exhaustMap, map, switchMap, tap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { UserService } from '../../services';
import { ImportUserCsvDialogService } from '@cxm-smartflow/user/ui/import-user-csv-dialog';
import { HttpEventType } from '@angular/common/http';
import { BatchUserResponse } from '../../models';
import { FileListValidator } from '@cxm-smartflow/shared/utils';
import { loadUserList } from '../list-user-table';

@Injectable({
  providedIn: 'root',
})
export class BatchUserEffect {
  validateBatchUserCSV$ = createEffect(() =>
    this._action$.pipe(
      ofType(fromActions$.validateBatchUserCSV),
      switchMap((args) => {
        const validator = new FileListValidator(args.files)
          .withAllowedExtensions(['csv'])
          .withAllowedMimeTypes(['text/csv', 'text/plain', 'application/csv', 'application/vnd.ms-excel']);

        const isValid = validator.validate();
        // Validate CSV extension and file type.
        if (isValid) {
          return [
            fromActions$.validateBatchUserCSVSuccess({ files: args.files }),
          ];
        }

        return [fromActions$.validateBatchUserCSVFail({})];
      })
    )
  );

  validateBatchUserCSVFail$ = createEffect(
    () =>
      this._action$.pipe(
        ofType(fromActions$.validateBatchUserCSVFail),
        tap((args) => {
          this._importUserCsvDialogService.close();

          switch (args.httpErrorResponse?.status) {
            case 406: {
              this._translate
                .get('user.import_invalid_csv_format')
                .toPromise()
                .then((message) => {
                  this._snackbar.openCustomSnackbar({
                    icon: 'close',
                    message,
                    type: 'error',
                  });
                });
              break;
            }

            default: {
              this._translate
                .get('user.error_during_import_invalid_csv_format')
                .toPromise()
                .then((message) => {
                  this._snackbar.openCustomSnackbar({
                    icon: 'close',
                    message,
                    type: 'error',
                  });
                });
              break;
            }
          }
        })
      ),
    { dispatch: false }
  );

  validateBatchUserCSVSuccess$ = createEffect(() =>
    this._action$.pipe(
      ofType(fromActions$.validateBatchUserCSVSuccess),
      exhaustMap((action) => {
        this._importUserCsvDialogService.show({ status: 'in_progress', content: { errorCount: 0, successCount: 0, total: 0 } });
        const formData = new FormData();

        if (action.files[0].type === 'application/vnd.ms-excel') {
          formData.append('file', new Blob([action.files[0]], { type: 'text/csv' }), action.files[0].name);
        } else {
          formData.append('file', action.files[0]);
        }

        return this._userService.validateImportedUserCSV(formData).pipe(
          map((content) => {
            if (content.type !== HttpEventType.Response) {
              return fromActions$.waitingBatchUser();
            }

            return fromActions$.createBatchUserSuccess({ batchUserResponse: content.body as BatchUserResponse });
          }),
          catchError((httpErrorResponse) => [fromActions$.validateBatchUserCSVFail({ httpErrorResponse })]),
        );
      }),
    ),
  );

  createBatchUserSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions$.createBatchUserSuccess),
    delay(1000),
    tap((args) => {
      this._importUserCsvDialogService.show({ status: 'done', content: args.batchUserResponse });
      const filteringFromLocalstorage = JSON.parse(localStorage.getItem('rem-list-use-table') || '{}') as any;
      const defaultParams = {
        page: 1,
        pageSize: 10,
        sortDirection: 'asc',
        sortByField: 'email'
      };

      const targetParams = Object.keys(filteringFromLocalstorage).length? filteringFromLocalstorage : defaultParams;

      this._store.dispatch(loadUserList({ params: { ...targetParams } }));
    })
  ), { dispatch: false });

  constructor(
    private _action$: Actions,
    private _translateService: TranslateService,
    private _translate: TranslateService,
    private _store: Store,
    private _snackbar: SnackBarService,
    private _userService: UserService,
    private _importUserCsvDialogService: ImportUserCsvDialogService,
  ) {
    this._translateService.use(localStorage.getItem('locale') || 'fr');
  }
}
