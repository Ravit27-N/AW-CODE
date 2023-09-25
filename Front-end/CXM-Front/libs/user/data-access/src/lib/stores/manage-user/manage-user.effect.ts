import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import {
  catchError,
  exhaustMap,
  map,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { UserFormControlService, UserService } from '../../services';
import * as fromAction from './manage-user.action';
import {
  getAllProfileByServiceId,
  getAllProfileByServiceIdFail,
  getAllProfileByServiceIdSuccess,
  navigateToEditBatchUserForm,
  submitCreateUserFails,
  submitCreateUserFrom,
  submitCreateUserSuccess,
  submitModifyBatchOfUser,
  submitModifyBatchOfUserFails,
  submitModifyBatchOfUserSuccess,
  submitModifySingleUser,
  submitModifySingleUserFails,
  submitModifySingleUserSuccess,
} from './manage-user.action';
import * as fromSelect from './manage-user.selector';
import { TranslateService } from '@ngx-translate/core';
import { loadUserList } from '../list-user-table';
import {UserAddressLevel, UserFormUpdateMode} from '@cxm-smartflow/user/util';
import * as selectFromUser from './../list-user-table/list-user-table.selector';
import {CreateUserRequestModel, UpdateUserRequestModel} from "../../models";

@Injectable()
export class ManageUserEffect {

  loadAllProfileListEffect$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.loadProfileList),
    exhaustMap(args => this.userService.getAllProfilePage(args.page, args.pageSize).pipe(
      map(response => fromAction.loadProfileListSuccess({
        allprofiles: response.contents,
        page: response.page,
        pageSize: response.pageSize
      }))
    ))));


  lazyLoadPagingEffect$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.lazyLoadPaging),
    withLatestFrom(this.store.select(fromSelect.selectPageOptions)),
    switchMap(([act, options]) => [fromAction.loadProfileList({ page: options.page + 1, pageSize: options.pageSize })])
  ));

  submitModifySingleUser$ = createEffect(() => this.actions.pipe(
    ofType(submitModifySingleUser),
    withLatestFrom(this.store.select(fromSelect.selectReturnAddressLevel)),
    switchMap(([args, addressLevel]) => {
      const returnAddressLevel = UserAddressLevel.returnAddressLevel.find(e => e.key == addressLevel)?.val || '';
      const payload: UpdateUserRequestModel = {
        ...args.updateUserModel,
        returnAddressLevel: returnAddressLevel
      };
      return this.userService.updateSingleUser(payload).pipe(
        map(() => submitModifySingleUserSuccess()),
        catchError((httpErrorResponse) => [submitModifySingleUserFails(httpErrorResponse)])
      );
    })
  ));

  submitModifySingleUserSuccess$ = createEffect(() => this.actions.pipe(
    ofType(submitModifySingleUserSuccess),
    withLatestFrom(this.store.select(selectFromUser.selectUserListFilters)),
    tap(([args, filters]) => {
      this.userFormControlService.alertSuccess('user.event.successModify');
    }),
    switchMap(([args, filters]) => [loadUserList({ params: { ...filters } })])
  ), { dispatch: false });

  submitModifySingleUserFails$ = createEffect(() => this.actions.pipe(
    ofType(submitModifySingleUserFails),
    tap(() => {
      this.userFormControlService.alertFailToModify();
    })
  ), { dispatch: false });

  submitModifyBatchOfUser$ = createEffect(() => this.actions.pipe(
    ofType(submitModifyBatchOfUser),
    switchMap(({
                 userIds,
                 profiles
               }) => this.userService.updateBatchUser(userIds, profiles).pipe(
      map(() => submitModifyBatchOfUserSuccess()),
      catchError((httpErrorResponse) => [submitModifyBatchOfUserFails(httpErrorResponse)])
    ))
  ));

  submitModifyBatchOfUserSuccess$ = createEffect(() => this.actions.pipe(
    ofType(submitModifyBatchOfUserSuccess),
    tap(() => {
      this.userFormControlService.alertSuccess('user.event.successModify');
      this.userFormControlService.removeModificationUsersCriteriaStorage();
    })
  ), { dispatch: false });

  submitModifyBatchOfUserFails$ = createEffect(() => this.actions.pipe(
    ofType(submitModifyBatchOfUserFails),
    tap(() => {
      this.userFormControlService.alertFailToModify();
    })
  ), { dispatch: false });

  submitCreateUserFrom$ = createEffect(() =>
    this.actions.pipe(
      ofType(submitCreateUserFrom),
      withLatestFrom(this.store.select(fromSelect.selectReturnAddressLevel)),
      exhaustMap(([args, addressLevel]) => {
        const returnAddressLevel = UserAddressLevel.returnAddressLevel.find(e => e.key == addressLevel)?.val || '';
        const payload: CreateUserRequestModel = {
          ...args.createUserModel,
          returnAddressLevel: returnAddressLevel
        };
        return this.userService.createUser(payload).pipe(
          map(() => submitCreateUserSuccess()),
          catchError((httpErrorResponse) => [submitCreateUserFails({ httpErrorResponse })])
        );
      })
    )
  );

  submitCreateUserSuccess$ = createEffect(() => this.actions.pipe(
    ofType(submitCreateUserSuccess),
    tap(() => {
      this.userFormControlService.alertSuccess('user.event.successCreate');
    })
  ), { dispatch: false });

  submitCreateUserFails$ = createEffect(() => this.actions.pipe(
    ofType(submitCreateUserFails),
    tap(() => {
      this.userFormControlService.alertError('user.event.failCreate', true);
    })
  ), { dispatch: false });

  navigateToEditBatchUserForm$ = createEffect(() => this.actions.pipe(
    ofType(navigateToEditBatchUserForm),
    tap(({ editBatchUsers }) => {
      this.userFormControlService.setModificationUsersCriteriaStorage(editBatchUsers);
      this.userFormControlService.navigateToEditUser({ mode: UserFormUpdateMode.UPDATE_MULTIPLE }, true);
    })
  ), { dispatch: false });

  getAllProfileByServiceId$ = createEffect(() => 
  this.actions.pipe(
      ofType(getAllProfileByServiceId),
      exhaustMap(args => 
          this.userService.getAllProfileByServiceId(args.serviceId).pipe(
              map(profiles => getAllProfileByServiceIdSuccess({ profiles })),
              catchError(httpErrorResponse => [getAllProfileByServiceIdFail({ httpErrorResponse })])
          )
      )
  )
);


  constructor(private actions: Actions,
              private userService: UserService,
              private store: Store,
              private translateService: TranslateService,
              private userFormControlService: UserFormControlService) {
    this.translateService.use(localStorage.getItem('locale') || 'fr');
  }
}
