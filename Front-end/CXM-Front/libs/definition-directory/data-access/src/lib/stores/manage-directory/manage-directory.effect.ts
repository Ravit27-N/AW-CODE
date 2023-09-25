import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { DefinitionDirectoryService } from '../../services';
import {
  catchError,
  exhaustMap,
  map,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import * as fromActions$ from './manage-directory.action';
import * as fromSelector$ from './manage-directory.selector';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { Router } from "@angular/router";
import { appRoute } from "@cxm-smartflow/shared/data-access/model";
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';

@Injectable({
  providedIn: 'root',
})
export class ManageDirectoryEffect {
  constructor(
    private actions$: Actions,
    private store$: Store,
    private translateService: TranslateService,
    private snackBarService: SnackBarService,
    private router: Router,
    private definitionDirectoryService: DefinitionDirectoryService,
    private confirmation: ConfirmationMessageService
  ) {}

  fetchDirectoryDefinition$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.fetchDirectoryDefinition),
      withLatestFrom(
        this.store$.select(fromSelector$.selectDefinitionDirectoryAllStates)
      ),
      exhaustMap(([args, allStates]) => {
        const httpParams: HttpParams = new HttpParams()
          .set('page', allStates.listFilteringCriteria.page)
          .set('pageSize', allStates.listFilteringCriteria.pageSize)
          .set('sortByField', allStates.listFilteringCriteria.sortByField)
          .set('sortDirection', allStates.listFilteringCriteria.sortDirection);

        return this.definitionDirectoryService
          .getDefinitionDirectory(httpParams)
          .pipe(
            map((listDefinitionDirectoryResponse) =>
              fromActions$.fetchDirectoryDefinitionSuccess({
                listDefinitionDirectoryResponse,
              })
            ),
            catchError((httpErrorResponse) => [
              fromActions$.fetchDirectoryDefinitionFail({ httpErrorResponse }),
            ])
          );
      })
    )
  );

  deleteDirectory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.deleteDirectory),
      exhaustMap((args) => {
        return this.definitionDirectoryService
          .deleteDefinitionDirectory(args.id)
          .pipe(
            map(() => fromActions$.deleteDirectorySuccess()),
            catchError((httpErrorResponse) => [
              fromActions$.deleteDirectoryFail({ httpErrorResponse }),
            ])
          );
      })
    )
  );

  deleteDirectorySuccess$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromActions$.deleteDirectorySuccess),
        withLatestFrom(
          this.store$.select(fromSelector$.selectDefinitionDirectoryAllStates)
        ),
        tap(([args, allStates]) => {
          this.store$.dispatch(
            fromActions$.fetchDirectoryDefinition({
              page: allStates.listFilteringCriteria.page,
              pageSize: allStates.listFilteringCriteria.pageSize,
              sortByField: allStates.listFilteringCriteria.sortByField,
              sortDirection: allStates.listFilteringCriteria.sortDirection,
            })
          );

          this.translateService
            .get('directory.definition.event.deleteSuccess')
            .toPromise()
            .then((message) =>
              this.snackBarService.openCustomSnackbar({
                type: 'success',
                icon: 'close',
                message,
              })
            );
        })
      ),
    { dispatch: false }
  );

  deleteDirectoryFail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.deleteDirectoryFail),
      tap(() => {
        this.translateService
          .get('directory.definition.event.failDelete')
          .toPromise()
          .then((message) =>
            this.snackBarService.openCustomSnackbar({
              type: 'error',
              icon: 'close',
              message,
            })
          );
      })
    ), { dispatch: false },
  );

  adjustFormStep1$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.adjustFormStep1),
      switchMap((data) => {
        return [fromActions$.validateFormChange()];
      })
    )
  );

  adjustDirectoryFields$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.adjustDirectoryFields),
      switchMap((data) => {
        return [fromActions$.validateFormChange()];
      })
    )
  );

  setupHistoryForm$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.setupHistoryForm),
      switchMap((data) => {
        return [fromActions$.validateFormChange()];
      })
    )
  );

  addClientIds$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.addClientId),
      switchMap((data) => {
        return [fromActions$.validateFormChange()];
      })
    )
  );

  removeClientId$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.removeClientId),
      switchMap((data) => {
        return [fromActions$.validateFormChange()];
      })
    )
  );

  validateFormChange = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.validateFormChange),
      withLatestFrom(
        this.store$.select(fromSelector$.selectDefinitionDirectoryAllStates)
      ),
      switchMap(([args, allStates]) => {
        const formHasChange =
          JSON.stringify(allStates.definitionDirectoryBeforeModify) !==
          JSON.stringify(allStates.definitionDirectoryForm);

        const hasDraftForm = Object.keys(JSON.parse(localStorage.getItem('definitionDirectoryFormEditor') || '{}')).length > 0;

        // Validate creation form.
        if (formHasChange || hasDraftForm) {
          return [fromActions$.updateFormChange({ hasChange: true })];
        }

        return [fromActions$.updateFormChange({ hasChange: false })];
      })
    )
  );

  adjustDefinitionDirectoryFormEditor$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions$.adjustDefinitionDirectoryFormEditor),
    switchMap(() => {
      return [fromActions$.validateFormChange()];
    })
  ));

  submitForm$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromActions$.submitForm),
      withLatestFrom(this.store$.select(fromSelector$.selectDefinitionDirectoryAllStates)),
      exhaustMap(([args, allStates]) => {
        if (args.formType === 'modify') {
          return this.definitionDirectoryService.editDirectoryDefinition(allStates.definitionDirectoryForm).pipe(
            map(res => fromActions$.modifyDirectorySuccess()),
            catchError(httpErrorResponse => [fromActions$.modifyDirectoryFail({ httpErrorResponse })])
          )
        }


        return this.definitionDirectoryService.createDirectoryDefinition(allStates.definitionDirectoryForm).pipe(
          map(res => fromActions$.createDirectorySuccess()),
          catchError(httpErrorResponse => [fromActions$.createDirectoryFail({ httpErrorResponse })])
        )
      }))
  );

  modifyDirectorySuccess$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions$.modifyDirectorySuccess),
    tap(() => {
      this.translateService
        .get('directory.definition.edit.message.editSuccess')
        .toPromise()
        .then((message) =>
          this.snackBarService.openCustomSnackbar({
            type: 'success',
            icon: 'close',
            message,
          })
        );

      this.router.navigateByUrl(appRoute.cxmDirectory.navigateToListDefinitionDirectory);
    })
  ), { dispatch: false });

  modifyDirectoryFail$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions$.modifyDirectoryFail),
    tap(() => {
      this.translateService
        .get('directory.definition.event.failModified')
        .toPromise()
        .then((message) =>
          this.snackBarService.openCustomSnackbar({
            type: 'error',
            icon: 'close',
            message,
          })
        );
    })
  ), { dispatch: false });

  getDefinitionDirectoryDetail$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions$.getDefinitionDirectoryDetail),
    exhaustMap((args) => {
      return this.definitionDirectoryService.getDirectoryDefinitionById(args.id).pipe(
        map(directoryDefinitionForm => fromActions$.getDefinitionDirectoryDetailSuccess({ directoryDefinitionForm })),
        catchError(httpErrorResponse => [fromActions$.getDefinitionDirectoryDetailFail({ httpErrorResponse })])
      )
    })
  ));

  getDefinitionDirectoryDetailFail$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions$.getDefinitionDirectoryDetailFail),
    tap((httpErrorResponse) => {
      const error = (httpErrorResponse as unknown as HttpErrorResponse);
      let translateKey = 'directory.load_directory_fail';
      if(error.status === 401 || error.status === 403){
        translateKey = 'load_directory_unauthorized';
      }
      this.translateService.get(translateKey)
        .toPromise().then((message) => {
        this.confirmation.showConfirmationPopup({
          type: 'Warning',
          title: message?.title,
          message: message?.message,
          confirmButton: message?.confirm,
          cancelButton: message?.cancel
        })
          .toPromise().then(() => {
          this.router.navigateByUrl(appRoute.cxmDirectory.navigateToListDefinitionDirectory);
        })
      })
    })
  ), {dispatch: false});

  createDirectorySuccess$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions$.createDirectorySuccess),
    tap(() => {
      this.translateService
        .get('directory.definition.create.message.createdSuccess')
        .toPromise()
        .then((message) =>
          this.snackBarService.openCustomSnackbar({
            type: 'success',
            icon: 'close',
            message,
          })
        );

      this.router.navigateByUrl(appRoute.cxmDirectory.navigateToListDefinitionDirectory);
    })
  ), { dispatch: false });


  createDirectoryFail$ = createEffect(() => this.actions$.pipe(
    ofType(fromActions$.createDirectoryFail),
    tap(() => {
      this.translateService
        .get('directory.definition.create.message.createdFail')
        .toPromise()
        .then((message) =>
          this.snackBarService.openCustomSnackbar({
            type: 'error',
            icon: 'close',
            message,
          })
        );
    })
  ), { dispatch: false });
}
