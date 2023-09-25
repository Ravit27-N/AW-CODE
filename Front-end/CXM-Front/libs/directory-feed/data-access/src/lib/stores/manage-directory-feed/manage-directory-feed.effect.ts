import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  exhaustMap,
  map,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import * as fromActions$ from '../';
import * as fromSelectors$ from './manage-directory-feed.selector';
import { Store } from '@ngrx/store';
import { HttpParams } from '@angular/common/http';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import {
  DirectoryFeedExceptionHandlerService,
  DirectoryFeedService,
} from '../../services';
import { ActivatedRoute } from '@angular/router';
import {
  DebugMessage,
  DirectoryFeedForm,
  FieldDetail,
  InsertedDirectoryFeed,
} from '../../models';
import { Location } from '@angular/common';

@Injectable({ providedIn: 'root' })
export class ManageDirectoryFeedEffect {
  constructor(
    private actions$: Actions,
    private directoryService: DirectoryFeedService,
    private store$: Store,
    private snackbarService: SnackBarService,
    private translateService: TranslateService,
    private _activateRoute: ActivatedRoute,
    private _location: Location
  ) {}

  private directoryFeedExceptionHandler = new DirectoryFeedExceptionHandlerService(
    this.translateService,
    this.snackbarService
  );

  getDirectoryFeedList$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.getDirectoryFeedList),
      exhaustMap((args) => {
        const httpParams = new HttpParams()
          .set('page', args.page)
          .set('pageSize', args.pageSize)
          .set('sortByField', args.sortByField)
          .set('sortDirection', args.sortDirection);

        return this.directoryService
          .getDefinitionDirectoryList(httpParams)
          .pipe(
            map((directoryFeedList) =>
              fromActions$.getDirectoryFeedListSuccess({ directoryFeedList })
            ),
            catchError((httpErrorResponse) => [
              fromActions$.getDirectoryFeedListFails({ httpErrorResponse }),
            ])
          );
      })
    )
  );

  getDirectoryFeedListFails$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromActions$.getDirectoryFeedListFails),
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

  getDirectoryFeedDetail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.getDirectoryFeedDetail),
      exhaustMap((args) =>
        this.directoryService.getDirectoryFeedField(args.id).pipe(
          map((directoryFeedDetail) => {
            return fromActions$.getDirectoryFeedDetailSuccess({
              directoryFeedDetail,
            });
          }),
          catchError((httpErrorResponse) => [
            fromActions$.getDirectoryFeedDetailFail({ httpErrorResponse }),
          ])
        )
      )
    )
  );

  getDirectoryFeedDetailFail$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromActions$.getDirectoryFeedDetailFail),
        tap(() => {
          this.translateService
            .get('directory.directory_feed_details_fetch_fail')
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

  submitImportDirectoryFeed$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.submitImportDirectoryFeed),
      withLatestFrom(
        this.store$.select(fromSelectors$.selectAllManageDirectoryStates)
      ),
      exhaustMap(([args, allStates]) => {
        const importedDetail = allStates.importDirectoryFeedCsvListCriteria;
        return this.directoryService
          .importCsvDirectoryValue(importedDetail.directoryId, {
            ignoreHeader: importedDetail.ignoreHeader,
            fileId: args.fileId,
            removeDuplicate: importedDetail.removeDuplicated,
          })
          .pipe(
            map(() => fromActions$.submitImportDirectoryFeedSuccess()),
            catchError((httpErrorResponse) => [
              fromActions$.submitImportDirectoryFeedFail({
                httpErrorResponse,
              }),
            ])
          );
      })
    )
  );

  submitImportDirectoryFeedSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromActions$.submitImportDirectoryFeedSuccess),
        withLatestFrom(
          this.store$.select(fromSelectors$.selectAllManageDirectoryStates)
        ),
        tap(([args, allStates]) => {
          this.translateService
            .get('directory.directory_feed_imported_successfully')
            .toPromise()
            .then((message) => {
              this.snackbarService.openCustomSnackbar({
                icon: 'close',
                type: 'success',
                message,
              });
            });

          const importedDetail = allStates.importDirectoryFeedCsvListCriteria;
          this.store$.dispatch(
            fromActions$.loadDirectoryFeedDetails({
              directoryId: importedDetail.directoryId,
              page: 1,
              pageSize: 10,
            })
          );
        })
      ),
    { dispatch: false }
  );

  loadImportedDirectoryFeedDataFails$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromActions$.submitImportDirectoryFeedFail),
        tap(() => {
          this.translateService
            .get('directory.directory_feed_load_imported_csv_content_fail')
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

  loadDirectoryFeedDetails$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.loadDirectoryFeedDetails),
      exhaustMap((args) => {
        return this.directoryService
          .getDirectoryFeedValue(args.directoryId, { ...args })
          .pipe(
            map((details) =>
              fromActions$.loadDirectoryFeedDetailsSuccess(details)
            ),
            catchError(() => [fromActions$.loadDirectoryFeedDetailsFail()])
          );
      })
    )
  );

  submitDirectoryFeed$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.submitDirectoryFeed),
      withLatestFrom(
        this.store$.select(fromSelectors$.selectAllManageDirectoryStates),
        this._activateRoute.queryParams
      ),
      exhaustMap(([args, allStates, params]) => {
        const directoryId = params.id;
        const data: FieldDetail[] = allStates.dataDirectoryFeeds;

        const directoryFeed: InsertedDirectoryFeed = {
          lineNumber: 0,
          data: data,
        };
        const inserted: InsertedDirectoryFeed[] = [];
        inserted.push(directoryFeed);

        const payload: DirectoryFeedForm = {
          inserted: inserted,
        };

        return this.directoryService
          .submitDirectoryFeedValue(directoryId, payload)
          .pipe(
            map(() => fromActions$.submitDirectoryFeedSuccess()),
            catchError((httpErrorResponse) => [
              fromActions$.submitDirectoryFeedFail({ httpErrorResponse }),
            ])
          );
      })
    )
  );

  submitDirectoryFeedFail$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromActions$.submitDirectoryFeedFail),
        withLatestFrom(
          this.store$.select(fromSelectors$.selectAllManageDirectoryStates),
          this.translateService.get('directory.directory_feed_details_error'),
          this.translateService.get('directory.insert_directory_feed_message')
        ),
        tap(([args, allState, messageErrorField, messages]) => {
          let errorMessage = '';

          if (allState.errorCode === 4008) {
            errorMessage = messageErrorField.duplicate;
          } else {
            errorMessage = messages.error;
          }

          this.snackbarService.openCustomSnackbar({
            type: 'error',
            message: errorMessage,
            icon: 'close',
          });
        })
      ),
    { dispatch: false }
  );

  submitDirectoryFeedSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromActions$.submitDirectoryFeedSuccess),
        withLatestFrom(
          this.translateService.get('directory.insert_directory_feed_message'),
          this._activateRoute.queryParams
        ),
        tap(([args, messages, params]) => {
          this.store$.dispatch(
            fromActions$.loadDirectoryFeedDetails({
              directoryId: params.id,
              page: 1,
              pageSize: 10,
            })
          );

          this._location.back();
          this.snackbarService.openCustomSnackbar({
            type: 'success',
            message: messages.success,
            icon: 'close',
          });
        })
      ),
    { dispatch: false }
  );

  submitDirectoryFeedValue$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.submitDirectoryFeedValue),
      exhaustMap((args) => {
        const typeMode =
          args.data?.updated && args.data?.updated?.length > 0
            ? 'modify'
            : 'delete';

        return this.directoryService
          .submitDirectoryFeedValue(args.directoryId, args.data)
          .pipe(
            map(() =>
              fromActions$.submitDirectoryFeedValueSuccess({
                directoryId: args.directoryId,
                typeMode,
              })
            ),
            catchError((exception) => [
              fromActions$.submitDirectoryFeedValueFail({
                httpErrorResponse: exception,
                typeMode,
              }),
            ])
          );
      })
    )
  );

  submitDirectoryFeedValueSuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromActions$.submitDirectoryFeedValueSuccess),
        tap((args) => {
          const msgDeleteSuccess = this.translateService
            .get('directory.directory_feed_details_delete_successfully')
            .toPromise();
          const msgModifiedSuccess = this.translateService
            .get('directory.directory_feed_details_modify_successfully')
            .toPromise();

          const messageSuccess =
            args.typeMode === 'modify' ? msgModifiedSuccess : msgDeleteSuccess;

          messageSuccess.then((message) => {
            this.snackbarService.openCustomSnackbar({
              icon: 'close',
              type: 'success',
              message: message,
            });
          });

          this.store$.dispatch(
            fromActions$.loadDirectoryFeedDetails({
              directoryId: args.directoryId,
              page: 1,
              pageSize: 10,
            })
          );
        })
      ),
    { dispatch: false }
  );

  submitDirectoryFeedValueFailure$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromActions$.submitDirectoryFeedValueFail),
        tap((args) => {
          // work on modify mode
          if (args.typeMode === 'delete') {
            this.translateService
              .get('directory.directory_feed_details_delete_failure')
              .toPromise()
              .then((message) => {
                this.snackbarService.openCustomSnackbar({
                  icon: 'close',
                  type: 'error',
                  message: message,
                });
              });
          } else {
            // work on modify mode

            const apierrorhandler =
              args.httpErrorResponse.error.apierrorhandler;
            const statusCode = apierrorhandler?.statusCode;
            const debugMsg = JSON.parse(
              apierrorhandler?.debugMessage || '{}'
            ) as DebugMessage;
            this.directoryFeedExceptionHandler
              .handleError(
                statusCode,
                debugMsg,
                'directory.directory_feed_details_modify_failure'
              )
              .then();
          }
        })
      ),
    { dispatch: false }
  );
}
