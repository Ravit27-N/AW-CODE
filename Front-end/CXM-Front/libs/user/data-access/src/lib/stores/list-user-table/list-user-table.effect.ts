import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, exhaustMap, map, mergeMap, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { UserFormControlService, UserService } from '../../services';
import * as fromUserAction from './list-user-table.action';
import {
  deleteUser,
  deleteUserFail,
  deleteUserSuccess,
  entriesBatchOfModificationUser,
  loadClientCriteria,
  loadClientCriteriaFail,
  loadClientCriteriaSuccess,
  loadClientService,
  loadClientServiceFail,
  loadClientServiceSuccess,
  loadClientServiceInUser,
  loadUserList,
  loadUserListSuccess,
  mapBatchOfModificationUser,
  navigateToUpdateSingleUser,
  exportUsers, 
  exportUsersSuccess, 
  exportUsersFailure
} from './list-user-table.action';
import * as fromUserSelector from './list-user-table.selector';
import { selectSelectionOpened } from './list-user-table.selector';
import { of } from 'rxjs';
import { appRoute, Params } from '@cxm-smartflow/shared/data-access/model';
import { Store } from '@ngrx/store';
import { UserFormUpdateMode } from '@cxm-smartflow/user/util';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { HttpErrorResponse } from '@angular/common/http';
import { Client, ClientResponse, UserList, exportUsersCsv } from '../../models';
import { InputSelectionCriteria } from '@cxm-smartflow/shared/ui/form-input-selection';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';

@Injectable({
  providedIn: 'root'
})

export class ListUserTableEffect {

  constructor(private action$: Actions,
              private service: UserService,
              private store: Store,
              private translate: TranslateService,
              private router: Router,
              private confirmationService: ConfirmationMessageService,
              private userFormControlService: UserFormControlService,
              private snackBar: SnackBarService
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }

  exportUsers$ = createEffect(() =>
  this.action$.pipe(
    ofType(exportUsers),
    mergeMap((action) =>
      this.service.exportUsersToCSV(
        action.services[0].data.profileIds,
        action.services[0].data.userType,
        action.services[0].data.clientIds,
        action.services[0].data.divisionIds,
        action.services[0].data.serviceIds,
        action.services[0].data.filter,
        action.services[0].data.filename
      ).pipe(
        map((response: Blob) => {
          const fileUrl = URL.createObjectURL(response);
          const link = document.createElement('a');
          link.href = fileUrl;
          link.download = `${action.services[0].data.filename}.csv`;
          link.target = '_blank';
          link.click();
          URL.revokeObjectURL(fileUrl);
          this.translate.get('user.export_user_in_csv_success_result').toPromise().then(message => {
            this.snackBar.openCustomSnackbar({ icon: 'close', type: 'success', message })});
          return exportUsersSuccess({ response });
        }),
        catchError((error) => of(exportUsersFailure({ error })))
      )
    )
  )
);



  loadUserList$ = createEffect(() => this.action$.pipe(
    ofType(loadUserList),
    switchMap((arg) => this.service.getUserList(arg.params as Params)
      .pipe(
        map((response: UserList) => loadUserListSuccess({ response: response, isLoading: false })),
        catchError((error) => [fromUserAction.loadUserListFail({ error: error, isLoading: false })])
      )
    )
  ));

  deleteUser$ = createEffect(() => this.action$.pipe(
    ofType(deleteUser),
    switchMap((arg) => {
      return this.service.deleteUsers(arg.userIds)
      .pipe(map(() => deleteUserSuccess({ userIds: arg.userIds })),catchError((httpErrorResponse) => [deleteUserFail({ httpErrorResponse })]))
    })
  ));

  deleteUserSuccess$ = createEffect(() => this.action$.pipe(
    ofType(deleteUserSuccess),
    withLatestFrom(this.store.select(fromUserSelector.selectUserListFilters), this.store.select(fromUserSelector.selectListOfUser)),
    tap(() => {
      this.userFormControlService.alertDeleteSuccess();
    }),
    switchMap(([args, filters, listUser]) => {
      if((listUser.userList || []).length === args.userIds.length) {
        return [loadUserList({ params: { ...filters, page: Math.max(1, filters.page - 1)  } })]
      }

      return [loadUserList({ params: { ...filters  } })]
    })
  ), { dispatch: true });

  deleteUserFail$ = createEffect(() => this.action$.pipe(
    ofType(deleteUserFail),
    tap(() => {
      this.userFormControlService.alertFailToDelete();
    })
  ), { dispatch: false });

  entriesBatchOfModificationUser$ = createEffect(() => this.action$.pipe(
    ofType(entriesBatchOfModificationUser),
    switchMap(args => of(mapBatchOfModificationUser({ filteredModifiedUser: args.modificationBatchUserId })))
  ));


  userSelectionEffect$ = createEffect(() => this.action$.pipe(
    ofType(mapBatchOfModificationUser),
    withLatestFrom(this.store.select(selectSelectionOpened)),
    switchMap(([args, opened]) => {
      if (!opened && args.filteredModifiedUser.length > 0) {
        return of(fromUserAction.openSelectionPanel());
      }

      if (opened && args.filteredModifiedUser.length == 0) {
        return of(fromUserAction.closeSelectionPanel());
      }

      return of(fromUserAction.setSelectionPanel({ active: opened }));
    })
  ));

  navigateToUpdateSingleUser$ = createEffect(() => this.action$.pipe(
    ofType(navigateToUpdateSingleUser),
    tap(args => {
      const data = {
        id: args.updatedUserId,
        mode: UserFormUpdateMode.UPDATE_SINGLE
      };
      this.userFormControlService.navigateToEditUser(data);
    })
  ), { dispatch: false });

  loadClientCriteria$ = createEffect(() => this.action$.pipe(
    ofType(loadClientCriteria),
    exhaustMap(args => this.service.getClientCriteria(args.sortDirection).pipe(
      map((response) => loadClientCriteriaSuccess({ clientCriteria: response })),
      catchError((error: HttpErrorResponse) => [loadClientCriteriaFail({ error: error })])
    ))
  ));

  loadClientServiceInUser$ = createEffect(() => this.action$.pipe(
    ofType(loadClientServiceInUser),
    exhaustMap(args => this.service.getUserService(args.userId).pipe(
      map((response: ClientResponse) => {
        console.log("user Id in effect :",args.userId)
        return loadClientServiceSuccess({
          clients: response.clients,
          clientWrappers: this.wrapClients(response.clients),
          divServiceWrappers: this.wrapDivServices(response.clients)
        });
      }),
      catchError((error: HttpErrorResponse) => [loadClientServiceFail({ error: error })])
    ))
  ));

  loadClientCriteriaFail$ = createEffect(() => this.action$.pipe(
    ofType(loadClientCriteriaFail),
    tap((args) => {
      const httpError = args.error as HttpErrorResponse;
      if (httpError) {
        if ([403, 401].includes(httpError.status)) {
          Promise.all([
            this.translate.get('template.message').toPromise(),
            this.translate.get('client.messages').toPromise()
          ]).then(([messageProps, clientMessage]) => {
            this.confirmationService.showConfirmationPopup(
              {
                icon: 'close',
                title: messageProps.unauthorize,
                message: clientMessage.userNotAuthorize,
                cancelButton: messageProps.unauthorizeCancel,
                confirmButton: messageProps.unauthorizeLeave,
                type: 'Warning'
              }
            ).subscribe(() => this.router.navigateByUrl(appRoute.dashboard.baseRoot));
          });
        }
      }
    })
  ), { dispatch: false });

  loadClientService$ = createEffect(() => this.action$.pipe(
    ofType(loadClientService),
    exhaustMap(args => this.service.getClientService(args.clientId).pipe(
      map((response: ClientResponse) => {
        return loadClientServiceSuccess({
          clients: response.clients,
          clientWrappers: this.wrapClients(response.clients),
          divServiceWrappers: this.wrapDivServices(response.clients)
        });
      }),
      catchError((error: HttpErrorResponse) => [loadClientServiceFail({ error: error })])
    ))
  ));
  
  /*loadClientServiceInUser$ = createEffect(() => this.action$.pipe(
    ofType(loadClientServiceInUser),
    exhaustMap(args => this.service.getUserService(args.userId).pipe(
      map((response: ClientResponse) => {
        console.log("user Id in effect :",args.userId)
        return loadClientServiceSuccess({
          clients: response.clients,
          clientWrappers: this.wrapClients(response.clients),
          divServiceWrappers: this.wrapDivServices(response.clients)
        });
      }),
      catchError((error: HttpErrorResponse) => [loadClientServiceFail({ error: error })])
    ))
  ));*/


  loadClientServiceFail$ = createEffect(() => this.action$.pipe(
    ofType(loadClientServiceFail),
    tap((args) => {
      const httpError = args.error as HttpErrorResponse;
      if (httpError) {
        if ([403, 401].includes(httpError.status)) {
          Promise.all([
            this.translate.get('template.message').toPromise(),
            this.translate.get('client.messages').toPromise()
          ]).then(([messageProps, clientMessage]) => {
            this.confirmationService.showConfirmationPopup(
              {
                icon: 'close',
                title: messageProps.unauthorize,
                message: clientMessage.userNotAuthorize,
                cancelButton: messageProps.unauthorizeCancel,
                confirmButton: messageProps.unauthorizeLeave,
                type: 'Warning'
              }
            ).subscribe(() => this.router.navigateByUrl(appRoute.dashboard.baseRoot));
          });
        }
      }
    })
  ), { dispatch: false });

  wrapClients = (clients: Client[]): InputSelectionCriteria [] => {
    return clients?.map(client => {
      return {
        key: client.id,
        value: client.name
      };
    });
  };

  wrapDivServices = (clients: Client[]): InputSelectionCriteria [] => {
    const divServices: InputSelectionCriteria [] = [];
    clients.filter(client => {
      client.divisions.filter(div => {
        div.departments.filter(ser => {
          divServices.push({
            key: ser.id,
            value: (div.name + ' / ' + ser.name)
          });
        });
      });
    });
    return divServices;
  };
//add new
  // eslint-disable-next-line @typescript-eslint/member-ordering
  loadClientDivision$ = createEffect(() => this.action$.pipe(
    ofType(fromUserAction.loadClientDivision),
    exhaustMap(args => {
      return this.service.getClientDivision(args.clientIds).pipe(
        map((divisions) => fromUserAction.setOrganizationDivision({ divisions }))
      );
    })
  ));

  // eslint-disable-next-line @typescript-eslint/member-ordering
  loadServices$ = createEffect(() => this.action$.pipe(
    ofType(fromUserAction.loadServices),
    exhaustMap(args => {
      return this.service.getServiceListClient(args.clientIds,args.divisionIds).pipe(
        map((services) => fromUserAction.setOrganizationService({ services }))
      );
    })
  ));

  // eslint-disable-next-line @typescript-eslint/member-ordering
  loadOrganizationProfile$ = createEffect(() => this.action$.pipe(
    ofType(fromUserAction.loadOrganizationProfile),
    exhaustMap(args => {
      return this.service.getOrganizationProfiles(args.clientIds).pipe(
        map((profiles) => fromUserAction.setOrganizationProfile({ profiles }))
      );
    })
  ));
  

}

