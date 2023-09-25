import {Injectable} from "@angular/core";
import {EnvelopeReferenceService} from "../services";
import {CanModificationService, SnackBarService} from "@cxm-smartflow/shared/data-access/services";
import {TranslateService} from "@ngx-translate/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {Store} from "@ngrx/store";
import {ConfirmationMessageService} from "@cxm-smartflow/shared/ui/comfirmation-message";
import {catchError, map, switchMap, tap, withLatestFrom} from "rxjs/operators";
import {
  closeSelectionPanel,
  createEnvelopeReference,
  createEnvelopeReferenceFail,
  createEnvelopeReferenceSuccess,
  deleteEnvelopeReference,
  deleteEnvelopeReferenceFail,
  deleteEnvelopeReferences,
  deleteEnvelopeReferenceSuccess,
  entriesBatchOfModification,
  fetchEnvelopeReferenceById,
  fetchEnvelopeReferenceByIdFail,
  fetchEnvelopeReferenceByIdSuccess,
  fetchEnvelopeReferences,
  fetchEnvelopeReferencesFail,
  fetchEnvelopeReferencesSuccess, mapBatchOfModification, openSelectionPanel, setSelectionPanel,
  updateEnvelopeReference,
  updateEnvelopeReferenceFail, updateEnvelopeReferences,
  updateEnvelopeReferenceSuccess
} from "./envelope-reference.action";
import {PageEnvelopeReference, ResponseEnvelopeReference} from "../models";
import {appRoute} from "@cxm-smartflow/shared/data-access/model";
import {Router} from "@angular/router";
import {of} from "rxjs";
import {selectSelectionOpened} from "@cxm-smartflow/envelope-reference/data-access";

@Injectable({
  providedIn:'root'
})
export class EnvelopeReferenceEffect {
  constructor(
    private readonly envelopeReferenceService:EnvelopeReferenceService,
    private readonly canModification: CanModificationService,
    private readonly translate: TranslateService,
    private readonly router: Router,
    private readonly actions: Actions,
    private readonly store: Store,
    private readonly snackbar: SnackBarService
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }


  createEnvelopeReference$ = createEffect(() =>
    this.actions.pipe(
      ofType(createEnvelopeReference),
      switchMap((args) => {
        return this.envelopeReferenceService.createEnvelopeReference( args.payload).pipe(
          map(() => createEnvelopeReferenceSuccess()),
          catchError((httpErrorResponse) => [createEnvelopeReferenceFail({ httpErrorResponse })])
        );
      })
    )
  );

  createEnvelopeReferenceSuccess$ = createEffect(() => this.actions.pipe(
    ofType(createEnvelopeReferenceSuccess),
    tap(() => {
      this.alertSuccess('envelope_reference.event.successCreate');
    })
  ), { dispatch: false });

  createEnvelopeReferenceFail$ = createEffect(() => this.actions.pipe(
    ofType(createEnvelopeReferenceFail),
    tap(() => {
      this.alertError('envelope_reference.event.failCreate', false);
    })
  ), { dispatch: false });



  updateEnvelopeReference$ = createEffect(() =>
    this.actions.pipe(
      ofType(updateEnvelopeReference),
      switchMap((args) => {
        return this.envelopeReferenceService.updateEnvelopeReference(args.payload).pipe(
          map(() => updateEnvelopeReferenceSuccess()),
          catchError((httpErrorResponse) => [updateEnvelopeReferenceFail({ httpErrorResponse })])
        );
      })
    )
  );
  updateEnvelopeReferences$ = createEffect(() =>
    this.actions.pipe(
      ofType(updateEnvelopeReferences),
      switchMap((args) => {
        return this.envelopeReferenceService.updateEnvelopeReferences(args.payload, args.ids).pipe(
          map(() => updateEnvelopeReferenceSuccess()),
          catchError((httpErrorResponse) => [updateEnvelopeReferenceFail({ httpErrorResponse })])
        );
      })
    )
  );

  updateEnvelopeReferenceSuccess$ = createEffect(() => this.actions.pipe(
    ofType(updateEnvelopeReferenceSuccess),
    tap(() => {
      this.alertSuccess('envelope_reference.event.successModify');
    })
  ), { dispatch: false });

  updateEnvelopeReferenceFail$ = createEffect(() => this.actions.pipe(
    ofType(updateEnvelopeReferenceFail),
    tap(() => {
      this.alertError('envelope_reference.event.failModified', true);
    })
  ), { dispatch: false });


  deleteEnvelopeReference$ = createEffect(() =>
    this.actions.pipe(
      ofType(deleteEnvelopeReference),
      switchMap((args) => {
        return this.envelopeReferenceService.deleteEnvelopeReferenceById(args.id).pipe(
          map(() => deleteEnvelopeReferenceSuccess()),
          catchError((httpErrorResponse) => [deleteEnvelopeReferenceFail({ httpErrorResponse })])
        );
      })
    )
  );

  deleteEnvelopeReferences$ = createEffect(() =>
    this.actions.pipe(
      ofType(deleteEnvelopeReferences),
      switchMap((args) => {
        return this.envelopeReferenceService.deleteEnvelopeReferences(args.ids).pipe(

          map(() => deleteEnvelopeReferenceSuccess()),
          catchError((httpErrorResponse) => [deleteEnvelopeReferenceFail({ httpErrorResponse })])
        );
      })
    )
  );
  deleteEnvelopeReferenceSuccess$ = createEffect(() => this.actions.pipe(
    ofType(deleteEnvelopeReferenceSuccess),
    tap(() => {
      this.store.dispatch(fetchEnvelopeReferences({payload: {page: 0, sort: ['createdAt'], size: 10, keyword: ''}}))
      this.alertSuccess('envelope_reference.event.successDelete');
    })
  ), { dispatch: false });

  deleteEnvelopeReferenceFail$ = createEffect(() => this.actions.pipe(
    ofType(deleteEnvelopeReferenceFail),
    tap(() => {
      this.alertError('envelope_reference.event.failDelete', true);
    })
  ), { dispatch: false });

  fetchEnvelopeReferenceById$ = createEffect(() =>
    this.actions.pipe(
      ofType(fetchEnvelopeReferenceById),
      switchMap((args) => {
        return this.envelopeReferenceService.findEnvelopeReferenceById(args.id).pipe(
          map((response: ResponseEnvelopeReference) => fetchEnvelopeReferenceByIdSuccess({payload: response})),
          catchError((httpErrorResponse) => [fetchEnvelopeReferenceByIdFail({ httpErrorResponse })])
        );
      })
    )
  );

  fetchEnvelopeReferenceByIdSuccess$ = createEffect(() => this.actions.pipe(
    ofType(fetchEnvelopeReferenceByIdSuccess),
    tap(() => {
      this.alertSuccess('envelope_reference.event.successCreate');
    })
  ), { dispatch: false });

  fetchEnvelopeReferenceByIdFail$ = createEffect(() => this.actions.pipe(
    ofType(fetchEnvelopeReferenceByIdFail),
    tap(() => {
      this.alertError('envelope_reference.event.failCreate', true);
    })
  ), { dispatch: false });


  fetchEnvelopeReferences$ = createEffect(() =>
    this.actions.pipe(
      ofType(fetchEnvelopeReferences),
      switchMap((args) => {
        return this.envelopeReferenceService.searchEnvelopeReference(args.payload).pipe(
          map((response: PageEnvelopeReference) => fetchEnvelopeReferencesSuccess({payload: response})),
          catchError((httpErrorResponse) => [fetchEnvelopeReferencesFail({ httpErrorResponse })])
        );
      })
    )
  );

  fetchEnvelopeReferencesSuccess$ = createEffect(() => this.actions.pipe(
    ofType(fetchEnvelopeReferencesSuccess),
  ), { dispatch: false });

  fetchEnvelopeReferencesFail$ = createEffect(() => this.actions.pipe(
    ofType(fetchEnvelopeReferencesFail),
  ), { dispatch: false });


  entriesBatchOfModification$ = createEffect(() => this.actions.pipe(
    ofType(entriesBatchOfModification),
    switchMap(args => of(mapBatchOfModification({ filteredModified: args.modificationBatchId })))
  ));

  mapBatchOfModification$ = createEffect(() => this.actions.pipe(
    ofType(mapBatchOfModification),
    withLatestFrom(this.store.select(selectSelectionOpened)),
    switchMap(([args, opened]) => {
      if (!opened && args.filteredModified.length > 0) {
        return of(openSelectionPanel());
      }

      if (opened && args.filteredModified.length == 0) {
        return of(closeSelectionPanel());
      }

      return of(setSelectionPanel({ active: opened }));
    })
  ));
  private alertSuccess(keyTranslation: string): void {
    this.translate.get(keyTranslation).toPromise().then(message => {
      this.snackbar.openCustomSnackbar({
        icon: 'close',
        message,
        type: 'success'
      });

      this.navigateToList();
    });
  }

  private alertError(keyTranslation: string, navigateToList?: boolean): void {
    this.translate.get(keyTranslation).toPromise().then(message => {
      this.snackbar.openCustomSnackbar({
        icon: 'close',
        message,
        type: 'error'
      });

      if (navigateToList) {
        this.navigateToList();
      }
    });
  }
  private navigateToList(): void {
    this.router.navigateByUrl(appRoute.cxmEnvelopeReference.navigateToList);
  }
}
