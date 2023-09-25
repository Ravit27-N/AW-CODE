import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  exhaustMap,
  map,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { of } from 'rxjs';
import { Injectable } from '@angular/core';
import { ProfileService, ProfileStorageService } from '../../services';
import * as fromProfileAction from './list-profile-table.action';
import {ConfirmationMessage, ConfirmationMessageService} from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { Router } from '@angular/router';
import { linkTab, ProfileTabs, selectFitler } from '..';
import { Store } from '@ngrx/store';
import {appRoute, SortDirection} from "@cxm-smartflow/shared/data-access/model";

@Injectable({
  providedIn: 'root'
})
export class ListProfileTableEffect {

  deleteConfirmMsg: any;
  btnConfirmMsg: any;
  sortDirection: SortDirection = 'desc';

  constructor(private action$: Actions,
              private service: ProfileService,
              private confirmationService: ConfirmationMessageService,
              private profileStorage: ProfileStorageService,
              private translate: TranslateService,
              private snackBarService: SnackBarService,
              private router: Router,
              private store: Store
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate.get('profile.dialog.delete').subscribe((response) => this.deleteConfirmMsg = response);
    this.translate.get('button').subscribe((response) => this.btnConfirmMsg = response);
  }

  loadProfileList$ = createEffect(
    () => this.action$.pipe(
      ofType(fromProfileAction.loadProfileList),
      withLatestFrom(this.store.select(selectFitler)),
      exhaustMap(([args, filter]) => {
        const storage = this.profileStorage.getProfileListStorage();
        const mapFilter = Object.keys(storage).length > 0 ? {
          ...storage,
          clientIds: storage?.clientIds || filter?.clientIds,
          sortByField: storage?.sortByField || 'lastModified',
          sortDirection: storage?.sortDirection || this.sortDirection
        } : filter;
          return this.service.getProfileList(mapFilter)
          .pipe(
            map((response) => fromProfileAction.loadProfileListSuccess({
              model: response,
              isLoading: false
            })),
            catchError((error) => of(fromProfileAction.loadProfileListFail({
              error: error,
              isLoading: false
            })))
          )
        }
      )
    )
  );

  loadProfileListFailEffect$ = createEffect(() => this.action$.pipe(
    ofType(fromProfileAction.loadProfileListFail),
    tap((args) => {
      const { error } = args.error;
      if(error) {
        const { apierrorhandler } = error;
        if([403, 401].includes(apierrorhandler.statusCode)) {
          Promise.all([
            this.translate.get('template.message').toPromise(),
            this.translate.get('client.messages').toPromise()
          ]).then(([messageProps, clientMessage]) => {
            this.confirmationService.showConfirmationPopup(
              {
                icon: 'close',
                title:  messageProps.unauthorize,
                message:clientMessage.userNotAuthorize,
                cancelButton: messageProps.unauthorizeCancel,
                confirmButton: messageProps.unauthorizeLeave,
                type: 'Warning'
              }
            ).subscribe(() => this.router.navigateByUrl(appRoute.dashboard.baseRoot));
          })
        }
      }

    })
  ), { dispatch: false });



  refreshProfileList = createEffect(
    () => this.action$.pipe(
      ofType(fromProfileAction.refreshProfileList),
      switchMap(args => [fromProfileAction.loadProfileList()])
    )
  );

  filterChanged$ = createEffect(() => this.action$.pipe(
    ofType(fromProfileAction.loadProfileListFilterChangeAction),
    switchMap(args => [fromProfileAction.loadProfileList()])
  ));

  attemptToDeleteProfileEffect = createEffect(() => this.action$.pipe(
    ofType(fromProfileAction.attemptToDeleteProfile),
    exhaustMap((args) => this.service.getListUserOfProfile(args.profileId).pipe(
      map((res) => fromProfileAction.showDeleteDialogPopup({ profileId: args.profileId, name: args.name, user: res })
      )))
  ));


  showDeleteDialogPopup = createEffect(
    () => this.action$.pipe(
      ofType(fromProfileAction.showDeleteDialogPopup),
      exhaustMap((args) => {
          let message;
          let title;
          const objectConfirmation: any = {};
          if (args.user && args.user?.length > 0) {
            message = this.deleteConfirmMsg?.deleteNotValid?.message.replace('{{numUsers}}', args.user?.length);
            title = this.deleteConfirmMsg?.deleteNotValid?.title;
          } else {
            message = this.deleteConfirmMsg.message.replace('{{numUsers}}', args.user?.length);;
            title = this.deleteConfirmMsg.title;
            objectConfirmation.confirmButton = this.btnConfirmMsg.delete;
            objectConfirmation.paragraph= this.deleteConfirmMsg.description;
          }

          return this.confirmationService.showConfirmationPopup(
            {
              ...objectConfirmation,
              type: 'Warning',
              icon: 'Warning',
              title,
              message,
              cancelButton: this.btnConfirmMsg.cancel
            }
          )
            .pipe(
              map((response) => response ? fromProfileAction.deleteProfile({ id: args.profileId }) : fromProfileAction.cancelledDeleteProfile({ error: 'Not call to delete profile.' })
              )
            );
        }
      )
    )
  );

  deleteProfile = createEffect(
    () => this.action$.pipe(
      ofType(fromProfileAction.deleteProfile),
      exhaustMap((args) => this.service.deleteProfile(args.id)
        .pipe(
          map(() => fromProfileAction.deleteProfileSuccess()),
          catchError((error) => {
            if(error?.status === 403) {
              this.translate.get('cannotAccess').subscribe(value => this.snackBarService.openWarning(value));
            } else {
              this.translate.get('profile.events.failDelete').subscribe(value => this.snackBarService.openError(value));
            }
           return of(fromProfileAction.deleteProfileFail({ error: error }))
          })
        )
      )
    )
  );

  deleteProfileSuccess = createEffect(
    () => this.action$.pipe(
      ofType(fromProfileAction.deleteProfileSuccess),
      tap(() => {
        this.router.navigateByUrl(appRoute.cxmProfile.navigateToList);
      }),
      switchMap(() => [fromProfileAction.loadProfileList(), fromProfileAction.showDeleteSuccessMessage()])
    )
  );

  showDeleteSuccessMessage = createEffect(
    () => this.action$.pipe(
      ofType(fromProfileAction.showDeleteSuccessMessage),
      tap(() => this.snackBarService.openCustomSnackbar({
        icon: 'close',
        message: this.deleteConfirmMsg?.success,
        type: 'success'
      }))
    ),
    { dispatch: false }
  );

  deleteProfileFail = createEffect(
    () => this.action$.pipe(
      ofType(fromProfileAction.deleteProfileFail),
      tap(() => {})
    ),
    { dispatch: false }
  );

  redirectToUpdateProfile = createEffect(
    () => this.action$.pipe(
      ofType(fromProfileAction.redirectToUpdateProfile),
      tap((args) => {
        this.router.navigateByUrl(`${appRoute.cxmProfile.navigateToModify}/${args.id}?clientId=${args?.clientId}`);
      }),
      switchMap(() => [linkTab(ProfileTabs.update)])
    )
  );

  redirectToCreateProfile = createEffect(
    () => this.action$.pipe(
      ofType(fromProfileAction.redirectCreateProfile),
      switchMap(() => [linkTab(ProfileTabs.create)]),
      tap(() => {
        this.router.navigateByUrl(appRoute.cxmProfile.navigateToCreate);
      })
    )
  );

  getAllClient$ = createEffect(() =>
    this.action$.pipe(
      ofType(fromProfileAction.getAllClient),
      exhaustMap(() =>
        this.service.getClientCriteria().pipe(
          map((listClientCriteria) => fromProfileAction.getAllClientSuccess({ listClientCriteria })),
          catchError((httpErrorResponse) => [fromProfileAction.getAllClientFail({ httpErrorResponse }),])
        )))
  );

  searchTermChange$ = createEffect(() => this.action$.pipe(
    ofType(fromProfileAction.searchTermChange),
    switchMap(() => [fromProfileAction.loadProfileList()])
  ));

  filterClientBoxChange$ = createEffect(() => this.action$.pipe(
    ofType(fromProfileAction.filterClientBoxChange),
    switchMap(() => [fromProfileAction.loadProfileList()])
  ));
}

