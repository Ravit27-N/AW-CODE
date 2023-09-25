import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import {
  CheckPrivilegeService,
  SnackBarService,
  UserUtil
} from '@cxm-smartflow/shared/data-access/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';

import { UserManagement } from '@cxm-smartflow/shared/data-access/model';
import { catchError, exhaustMap, filter, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { PREDEFINE_PERMISSION_FORM, UserProfileModel } from '../../models';
import { ProfileService } from '../../services';
import { initFormChange } from './manage-profile-validation';
import * as formAction from './manage-profile.action';
import * as fromValidateAction from './manage-profile-validation';
import { selectClientProfilleForm, selectProfileId } from './manage-profile.selector';

const clientAllowModuleForm = (permissionForm: any, alloweModule: any[]) => {
  return Array.from(permissionForm).filter((x: any) => alloweModule.includes(x.code));
}

@Injectable({ providedIn: 'root' })
export class ProfileFormEffect {
  submitProfileEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formAction.submitProfile),
    withLatestFrom(this.store$.select(selectProfileId)),
    exhaustMap(([args, profileId]) => {
      const profile: UserProfileModel = {
        name: args.name,
        displayName: args.displayName,
        clientId: args.clientId,
        functionalities: Array.from(args.perm).filter((x: any) => x.checked).map(this.mapToAPIModel)
      }

      if (profileId) {
        if (this.checkPrivilege.getUserRight(UserManagement.CXM_USER_MANAGEMENT, UserManagement.MODIFY_PROFILE)) {
          return this.profileService.updateProfile(profileId, profile).pipe(
            exhaustMap(res => of(initFormChange({ formChange: false }), formAction.submitProfileSuccess())),
            catchError(e => {
              if (e?.status === 403) {
                this.translateService.get('cannotAccess').subscribe(value => this.snackbar.openCustomSnackbar({ message: value, type: 'error', icon: 'close' }));
              } else {
                this.translateService.get("profile.events.failModified").subscribe(text => this.snackbar.openCustomSnackbar({ message: text, type: 'error', icon: 'close' })).unsubscribe();
              }
              return of(formAction.submitProfileFail());
            })
          )
        } else {
          this.translateService.get('cannotAccess').subscribe(value => this.snackbar.openCustomSnackbar({ message: value, type: 'error', icon: 'close' }));
          return of(formAction.submitProfileFail());
        }
      } else {
        return this.profileService.createProfile(profile)
          .pipe(
            exhaustMap(res => of(initFormChange({ formChange: false }), formAction.submitProfileSuccess())),
            catchError(e => {
              if (e?.status === 403) {
                this.translateService.get('cannotAccess').subscribe(value => this.snackbar.openCustomSnackbar({ message: value, type: 'error', icon: 'close' }));
              } else {
                this.translateService.get("profile.events.failCreate").subscribe(text => this.snackbar.openCustomSnackbar({ message: text, type: 'error', icon: 'close' })).unsubscribe();
              }
              return of(formAction.submitProfileFail());
            })
          )
      }
    })
  ))

  afterSubmitSuccessEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formAction.submitProfileSuccess),
    tap(() => {
      this.translateService.get("profile.events.success").subscribe(text => this.snackbar.openCustomSnackbar({ message: text, type: 'success', icon: 'close' })).unsubscribe();
      this.router.navigateByUrl('/cxm-profile/list-profiles');
    })
  ), { dispatch: false })


  afterSubmitFailEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formAction.submitProfileFail),
    tap(() => {
      this.translateService.get("profile.events.fail").subscribe(text => this.snackbar.openCustomSnackbar({ message: text, type: 'error', icon: 'close' })).unsubscribe();
    })
  ), { dispatch: false })


  loadProfileFormEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formAction.loadProfileForm),
    withLatestFrom(this.store$.select(selectClientProfilleForm)),
    switchMap(([args, client]) => {
      const { id } = args;
      return this.profileService.getProfileById(id || '').pipe(
        exhaustMap(res => {
          const { name, displayName, functionalities, createdBy, clientId, ownerId } = res;
          const nameForm = { name, displayName, clientId };
          const perm = functionalities.map(this.mapToNgrxModel);

          const keepOldProfileData: any = {
            name: name,
            displayName: displayName,
            privileges: functionalities
              .sort((a, b) => (a.functionalityKey < b.functionalityKey ? -1 : 1))
          }

          const targetProfilePermission = UserUtil.getTargetPermiisionlevel(functionalities);

          return of(formAction.loadProfileFormSuccess({ nameForm, perm, keepOldProfileData: keepOldProfileData, createdBy: createdBy, targetPermission: targetProfilePermission, ownerId }));
        }),
        catchError(e => of(formAction.loadProfileFormFail()))
      )
    })
  ));


  loadClientModuleEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formAction.loadClientModule),
    withLatestFrom(this.translateService.get('profile.form.label')),
    exhaustMap(([args, names]) => this.profileService.getClientModule({ clientid: args.clientId, profileId: args.profileId }).pipe(
      map((res) => {
        let form = clientAllowModuleForm(PREDEFINE_PERMISSION_FORM, res);
        form = form.map((f: any) => ({ ...f, t: names[f.name.substring("profile.form.label.".length)] }))
          .sort((a: any, b: any) => a.t.localeCompare(b.t));


        form = this.aggregateProfileFormBaseOnUser(form);


        return formAction.loadClientModuleSuccess({ module: res, form, creation: args.profileId === '0' });
      }),
      catchError(() => {
        return of(formAction.loadClientModuleFail());
      })
    ))
  ))

  loadClientModuleSuccessEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formAction.loadClientModuleSuccess),
    filter(args => args.creation === true),
    switchMap((args) => {
      return [formAction.setLoadingFormComplete()]
    })
  ))


  validateClientIdEffect$ = createEffect(() => this.actions$.pipe(
    ofType(fromValidateAction.validateClientId),
    switchMap(args => [formAction.loadClientModule({ clientId: args.clientId })])
  ))

  mapToAPIModel = (x: any): any => {
    const privileges = Array.from(x.privileges).filter((y: any) => y.checked)
      .map((z: any) => ({
        key: z.code,
        modificationLevel: z.modification,
        visibilityLevel: z.visibility,
        id: z.id,
      }));

    return {
      functionalityKey: x.code,
      modificationLevel: x.modification,
      visibilityLevel: x.visibility,
      id: x.id,
      // privileges: [] // TODO: Implement sub functionality
      privileges
    }
  }


  mapToNgrxModel = (x: any) => {
    const privileges = Array.from(x.privileges).map((y: any) => ({
      code: y.key,
      modification: y.modificationLevel,
      visibility: y.visibilityLevel,
      id: y.id
    }))

    return {
      code: x.functionalityKey,
      modification: x.modificationLevel,
      visibility: x.visibilityLevel,
      id: x.id,
      privileges
    }
  }

  aggregateProfileFormBaseOnUser = (form: any) => {
    const isAdmin = UserUtil.isAdmin();
    if (isAdmin) {
      return Array.from(form)
      .map((item: any) => {
        const func = Array.from(item.func).map((f: any) => {
          return { ...f, v: 4, m: 4, allowed: true }
        })
        return { ...item, v: 4, m: 4, func, allowed: true }
      });

    } else {
      const authzPermission = UserUtil.getUserPermissionLevel();

      const result = Array.from(form)
        .map((item: any) => {

          const lv = authzPermission.get(item.code);
          const isFunctionAllowed = lv !== undefined && UserUtil.isLesserLvThanProfile(lv, item);

          const func = Array.from(item.func).map((f: any) => {
            const flv = authzPermission.get(f.code);
            const isPrevilegeAllowed = !!flv && UserUtil.isLesserLvThanProfile(flv, f);

              return isPrevilegeAllowed ?
              { ...f, v: flv?.v, m: flv?.m, allowed: true } :
              { ...f, v:4, m: 4, allowed: false }
            });

          return isFunctionAllowed ?
          { ...item, v: lv?.v, m: lv?.m, func, allowed: true } :
          { ...item, func, v:4, m: 4, allowed: false }
        });

        return result;
    }
  }

  constructor(private actions$: Actions,
              private store$: Store,
              private profileService: ProfileService,
              private router: Router,
              private snackbar: SnackBarService,
              private translateService: TranslateService,
              private checkPrivilege: CheckPrivilegeService) {
    this.translateService.use(localStorage.getItem('locale') || 'fr');
  }

}
