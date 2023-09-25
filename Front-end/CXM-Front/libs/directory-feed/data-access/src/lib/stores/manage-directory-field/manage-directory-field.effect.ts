import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import { DirectoryFeedService } from '../../services';
import {
  getDirectoryField,
  getDirectoryFieldFail,
  getDirectoryFieldSuccess,
} from './manage-directory-field.action';

@Injectable({ providedIn: 'root' })
export class ManageDirectoryFieldEffect {
  constructor(
    private actions$: Actions,
    private directoryService: DirectoryFeedService,
    private snackbarService: SnackBarService,
    private translateService: TranslateService
  ) {}

  getDirectoryField$ = createEffect(() =>
    this.actions$.pipe(
      ofType(getDirectoryField),
      exhaustMap((args) => {
        return this.directoryService
          .getDirectoryFeedField(args.directoryId)
          .pipe(
            map((fields) => getDirectoryFieldSuccess({ fields })),
            catchError((httpErrorResponse) => [
              getDirectoryFieldFail({ httpErrorResponse }),
            ])
          );
      })
    )
  );

  getDirectoryFieldFails$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(getDirectoryFieldFail),
        tap(() => {
          this.translateService
            .get('directory.directory_feed_list_fetch_fail')
            .toPromise()
            .then((message) => {
              this.snackbarService.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );
}
