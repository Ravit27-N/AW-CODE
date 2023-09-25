import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import {
  catchError,
  exhaustMap,
  map,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { fromClientActions, fromClientSelector } from '.';
import { ClientService } from '../../services/client.service';

import * as fromActions from './client.actions';
import { TranslateService } from '@ngx-translate/core';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { filterHistory } from './client.reducer';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Router } from '@angular/router';
import { appLocalStorageConstant, appRoute } from '@cxm-smartflow/shared/data-access/model';

@Injectable()
export class ClientEffects {
  loadClientListEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.loadClientList),
      switchMap((args) => {
        return this.clientService.getClientList(args.filters).pipe(
          map((res) => {
            // TOOD: Filter user privilege
            const history = JSON.parse(
              <string>localStorage.getItem(filterHistory)
            );
            return fromActions.loadClientListSuccess({
              clients: res.contents,
              pagination: {
                page: history?.page || res.page,
                total: res.total,
                pageSize: history?.pageSize || res.pageSize,
              },
            });
          }),
          catchError((err) => of(fromActions.loadClientListFail({ httpError: err })))
        );
      })
    )
  );

  loadClientListFailEffect$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.loadClientListFail),
    tap(args => {
      const { httpError} = args;
      if(httpError) {
        const { apierrorhandler }  = httpError.error;
        if([403, 401].includes(apierrorhandler.statusCode)) {
          Promise.all([
            this.translate.get('template.message').toPromise(),
            this.translate.get('client.messages').toPromise()
          ]).then(([messageProps, clientMessage]) => {
            this.confirmService.showConfirmationPopup(
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
  ), { dispatch: false })

  filterChangedEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.filterChanged),
      withLatestFrom(this.store.select(fromClientSelector.selectPagination)),
      switchMap(([args, paging]) => {
        const { sortByField, sortDirection, page, pageSize } = args;
        return [
          fromClientActions.loadClientList({
            filters: {
              sortByField,
              sortDirection,
              page: page ? page : paging.page,
              pageSize: pageSize ? pageSize : paging.pageSize,
            },
          }),
        ];
      })
    )
  );

  attempToDeleteClient$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.attempToDeleteClient),
        tap(({ id }) => {
          const messages = Promise.all([
            this.translate.get('client.delete.title').toPromise(),
            this.translate.get('client.delete.message').toPromise(),
            this.translate.get('client.delete.cancelButton').toPromise(),
            this.translate.get('client.delete.confirmButton').toPromise(),
          ]);

          messages.then((m) => {
            this.confirmService
              .showConfirmationPopup({
                type: 'Warning',
                icon: 'Warning',
                title: m[0],
                message: m[1],
                cancelButton: m[2],
                confirmButton: m[3],
              })
              .toPromise()
              .then((ok) => {
                if (!ok) return;
                this.store.dispatch(fromClientActions.deleteClient({ id }));
              });
          });
        })
      ),
    { dispatch: false }
  );

  deleteClient$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.deleteClient),
      exhaustMap(({ id }) =>
        this.clientService.deleteClient(id).pipe(
          map(() => fromActions.deleteClientSuccess()),
          catchError(() => [fromActions.deleteClientFail()])
        )
      )
    )
  );

  deleteClientSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.deleteClientSuccess),
        tap(() => {
          this.translate
            .get('client.messages.deleteSuccess')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                message,
                type: 'success',
              });
            });

          const filteredHistory = JSON.parse(
            <string>localStorage.getItem(filterHistory)
          );

          this.store.dispatch(
            fromActions.filterChanged({
              page: filteredHistory?.page || 1,
              pageSize: filteredHistory?.pageSize || 10,
              sortByField: filteredHistory?.sortByField || 'lastModified',
              sortDirection: filteredHistory?.sortDirection || 'desc',
            })
          );
          this.router
            .navigateByUrl(appRoute.cxmClient.navigateToListClient)
            .then();
        })
      ),
    { dispatch: false }
  );

  deleteClientFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.deleteClientFail),
        tap(() => {
          this.translate
            .get('client.messages.deleteFail')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                message,
                type: 'error',
              });
            });
        })
      ),
    { dispatch: false }
  );

  constructor(
    private actions: Actions,
    private clientService: ClientService,
    private store: Store,
    private translate: TranslateService,
    private snackbar: SnackBarService,
    private router: Router,
    private confirmService: ConfirmationMessageService
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate.use(
      localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) ||
      appLocalStorageConstant.Common.Locale.Fr
    );
  }
}
