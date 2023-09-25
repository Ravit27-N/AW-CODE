import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { createAction, createFeatureSelector, createReducer, createSelector, on, props, Store } from '@ngrx/store';
import { of } from 'rxjs';
import { catchError, exhaustMap, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { ProfileService } from '../../services';
import * as formAction from './manage-profile.action';
import { selectKeepOldProfileData, selectProfileId } from './manage-profile.selector';
import { deepCompare } from '@cxm-smartflow/profile/util';

export const profileFormValidationFeaturekey = 'manage-profile-form-validation';

// Actions
export const validateFormResult = createAction(
  '[manage profile validate form / result]',
  props<{
    atLeastOne?: boolean, empty?: boolean, existed?: boolean, displayName?: boolean,
    profileNameMaxLength?: boolean, displayNameMaxLength?: boolean, formChange?: boolean
  }>()
);

export const validateClientId = createAction('[manage profile validation form / validate client ID]', props<{ clientId: number }>())

export const validateFail = createAction(
  '[manage profile validate form / api fail]'
);

export const clearValidateFormResult = createAction(
  '[manage profile validate form / clear]'
);

export const initFormChange = createAction('[manage profile validation form] / init form change', props<{ formChange: boolean }>());

// initial state
const initialValidateState = {
  atLeastOne: false,
  empty: true,
  existed: false,
  displayName: true,
  profileNameMaxLength: false,
  displayNameMaxLength: false,
  newProfileData: undefined,
  formChange: false,
  clientId: 0,
};

export interface Profile {
  name: string,
  displayName: string,
  privileges: Array<Privilege>
};

export interface Privilege {
    id: number,
    functionalityKey: string,
    privileges: Array<Subprivilege>,
    visibilityLevel: boolean,
    modificationLevel: boolean
}

export interface Subprivilege {
  key: string,
  visibilityLevel: string,
  modificationLevel: string,
  visibility: boolean,
  modification: boolean
}

// Reducer
export const manageProfileValidateFormReducer = createReducer(
  initialValidateState,
  on(validateFormResult, (state, actionProps) => ({
    ...state,
    ...actionProps,
    profileNameMaxLength: actionProps?.profileNameMaxLength || false,
    displayNameMaxLength: actionProps?.displayNameMaxLength || false,
    formChange: actionProps?.formChange || false
  })),
  on(clearValidateFormResult, () => ({ ...initialValidateState })),
  on(validateClientId, (state, props) => ({ ...state, clientId: props.clientId  })),
  on(initFormChange, (state, actionProps) => ({ ...state, formChange: actionProps.formChange }))
);


// Effect
@Injectable({ providedIn: 'root' })
export class ProfileFormValidationEffect {
  isFormUpdate = false;

  validateFormEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formAction.validateForm),
    withLatestFrom(this.store.select(selectKeepOldProfileData)),
    switchMap(([args, selectKeepOldProfileData]) => {
      const { perm, name, displayName } = args;
      const privileges = Array.from(perm)?.filter((x: any) => x.checked);
      const atLeastOneChecked = privileges?.some((x: any) => x.checked);

      const profile: Profile = {
        name: name || '',
        displayName: displayName || '',
        privileges: privileges.map(this.mapToValidate)
          .sort((a, b) => (a.functionalityKey < b.functionalityKey ? -1 : 1))
      };

      const invisibleProfiles: Profile = {
        name: name || '',
        displayName: displayName || '',
        privileges: Array.from(perm)?.filter((x: any) => x.checked == undefined).map(this.mapToUnValidate)
        .sort((a, b) => (a.functionalityKey < b.functionalityKey ? -1 : 1))
      };
      const disableProfiles = Array.from(invisibleProfiles.privileges).filter((invisibleProfile: any) => {
        return Array.from(selectKeepOldProfileData.privileges).some((oldProfile: any) => oldProfile.functionalityKey === invisibleProfile.functionalityKey);
      });

      if (Object.keys(disableProfiles).length) {
        this.addUnmodifyPrivilegeToProfile(disableProfiles, selectKeepOldProfileData, profile);
        const visibleProfiles: Profile = {
          name: name || '',
          displayName: displayName || '',
          privileges: Array.from(perm)?.filter((x: any) => x.checked == undefined).map(this.mapToValidate)
            .sort((a, b) => (a.functionalityKey < b.functionalityKey ? -1 : 1))
        };
        this.addModifyPrivilegeToProfile(visibleProfiles, selectKeepOldProfileData, profile);
      }

      this.preventUnmodifyPrivilegeToUpdate(selectKeepOldProfileData, profile);
      return [validateFormResult({
        atLeastOne: !atLeastOneChecked,
        empty: !name || name?.trim() === '',
        displayName: !displayName || displayName?.trim() === '',
        profileNameMaxLength: name?.length > 128,
        displayNameMaxLength: displayName?.length > 128,
        formChange: !deepCompare(this.sortProfilePrivileges(selectKeepOldProfileData), this.sortProfilePrivileges(profile))
      })];
    })
  ));

  sortProfilePrivileges = (profiles: Profile) => {
    const profile = JSON.parse(JSON.stringify(profiles));
    profile.privileges = Array.from(profile.privileges).map((result: any) => {
      result.privileges = Array.from(result.privileges).map((result: any) => result).sort((a, b) => {
        const keyCompare = a.key && b.key ? a.key < b.key : a.functionalityKey < b.functionalityKey;
        return (keyCompare ? -1 : 1)
      });
      return result;
    }).sort((a, b) => {
      return (a.id < b.id ? -1 : 1)
    });
    return profile;
  }

  sortProfilePrivilegesByKey = (profiles: Profile) => {
    const profile = JSON.parse(JSON.stringify(profiles));
    profile.privileges = Array.from(profile.privileges).map((result: any) => result).sort((a, b) => {
      const keyCompare = a.key && b.key ? a.key < b.key : a.functionalityKey < b.functionalityKey;
      return (keyCompare ? -1 : 1)
    });
    return profile;
  }

  mapToUnValidate = (x: any): any => {
    const privileges = Array.from(x.privileges).filter((y: any) => y.checked == undefined)
      .map((z: any) => ({
        key: z.code,
        visibilityLevel: z.visibility !== undefined ? z.visibility : null,
        modificationLevel: z.modification !== undefined ? z.modification : null,
        visibility: false,
        modification: false
      }));

    return {
      id: x.id,
      functionalityKey: x.code,
      privileges,
      visibilityLevel: x.visibility !== undefined ? x.visibility : null,
      modificationLevel: x.modification !== undefined ? x.modification : null
    };
  };

  mapToValidate = (x: any): any => {
    const privileges = Array.from(x.privileges).filter((y: any) => y.checked)
      .map((z: any) => ({
        key: z.code,
        visibilityLevel: z.visibility !== undefined ? z.visibility : null,
        modificationLevel: z.modification !== undefined ? z.modification : null,
        visibility: false,
        modification: false
      }));

    return {
      id: x.id,
      functionalityKey: x.code,
      privileges,
      visibilityLevel: x.visibility !== undefined ? x.visibility : null,
      modificationLevel: x.modification !== undefined ? x.modification : null
    };
  };

  validateProfileNameExistedEffect = createEffect(() => this.actions$.pipe(
    ofType(formAction.validateProfileName),
    withLatestFrom(this.store.select(selectProfileId), this.store.select(selectValidationProp)),
    exhaustMap(([args, profileId, selectValidationProp]) => {
      const { atLeastOne, empty, displayName, clientId } = selectValidationProp;
      if (profileId === undefined || profileId === null) {
        const isFormChange = (args?.name?.trim()?.length > 0) || (atLeastOne === false) || (empty === false) || (displayName === false);
        return this.profileService.isProfileExist(args?.name, clientId)
          .pipe(map(value => validateFormResult({
              existed: value,
              formChange: isFormChange,
              profileNameMaxLength: args?.name?.length > 128
            })),
            catchError(() => of(validateFormResult({
              existed: false,
              formChange: isFormChange,
              profileNameMaxLength: args?.name?.length > 128
            })))
          );
      } else {
        // Not check duplicate profile name.
        return of(validateFormResult({ existed: false }));
      }
    })
  ));

  constructor(private actions$: Actions, private profileService: ProfileService, private store: Store) {
  }

  preventUnmodifyPrivilegeToUpdate(selectKeepOldProfileData: Profile, profile: Profile) {
    for (const key of Object.keys(this.sortProfilePrivileges(selectKeepOldProfileData))) {
      const originalProfiles = this.sortProfilePrivileges(selectKeepOldProfileData)[key];
      const trackProfiles = this.sortProfilePrivileges(profile)[key];
      Array.from(originalProfiles).forEach((originalProfile: any, index: number) => {
        if (trackProfiles[index] != undefined && trackProfiles[index].id && originalProfile.id) {
          const position = Array.from(profile.privileges).findIndex((priv: any) => priv.functionalityKey == trackProfiles[index].functionalityKey);
          Array.from(originalProfile.privileges).forEach((originalPriv: any) => {
            const privLocation = Array.from(profile.privileges[position].privileges).findIndex((privFindIdx: any) => privFindIdx.key === originalPriv.key);
            const profileDetailPrivilege = profile.privileges[position].privileges[privLocation];
            if (profileDetailPrivilege && profileDetailPrivilege.key == originalPriv.key) {
              if (originalPriv.visibilityLevel == '' && originalPriv.modificationLevel == '') {
                profile.privileges[position].privileges[privLocation].visibilityLevel = '';
                profile.privileges[position].privileges[privLocation].modificationLevel = '';
              } else if (originalPriv.visibilityLevel != '' && originalPriv.modificationLevel == '' && profileDetailPrivilege.modificationLevel != '') {
                profile.privileges[position].privileges[privLocation].modificationLevel = '';
              } else if (originalPriv.visibilityLevel == '' && originalPriv.modificationLevel != '' && profileDetailPrivilege.visibilityLevel != '') {
                profile.privileges[position].privileges[privLocation].visibilityLevel = '';
              }
            }
          });
        }
      });
    }
  }

  addUnmodifyPrivilegeToProfile(disableProfiles: Privilege[], selectKeepOldProfileData: Profile, profile: Profile) {
    Array.from(disableProfiles).forEach((disableProfile: any) => {
      if (disableProfile.modificationLevel == null && disableProfile.visibilityLevel == null) {
        const index = Array.from(selectKeepOldProfileData.privileges).findIndex((oldProfiles: any) => oldProfiles.functionalityKey === disableProfile.functionalityKey);
        disableProfile.modificationLevel = selectKeepOldProfileData.privileges[index].modificationLevel;
        disableProfile.visibilityLevel = selectKeepOldProfileData.privileges[index].visibilityLevel;
        if (disableProfile.privileges.length === selectKeepOldProfileData.privileges[index].privileges.length) {
          disableProfile.privileges = selectKeepOldProfileData.privileges[index].privileges;
          profile.privileges = profile.privileges.concat(this.sortProfilePrivilegesByKey(disableProfile));
        } else {
          Array.from(selectKeepOldProfileData.privileges[index].privileges).forEach((privilegeFilter: any) => {
            const idx = Array.from(disableProfile.privileges).findIndex((priv: any) => priv.key == privilegeFilter.key);
            disableProfile.privileges[idx] = privilegeFilter;
          });
          disableProfile.privileges = Array.from(disableProfile.privileges).filter((filterPrivilege: any) => filterPrivilege.modificationLevel != null && filterPrivilege.visibilityLevel != null);
          profile.privileges = profile.privileges.concat(this.sortProfilePrivilegesByKey(disableProfile));
        }
      }

    });
  }

  addModifyPrivilegeToProfile(visibleProfiles: Profile, selectKeepOldProfileData: Profile, profile: Profile) {
    const modifyProfiles = Array.from(visibleProfiles.privileges).filter((visibleProfile: any) => {
      return Array.from(selectKeepOldProfileData.privileges).some((oldProfile: any) => oldProfile.functionalityKey === visibleProfile.functionalityKey);
    });

    if (modifyProfiles.length) {
      Array.from(modifyProfiles).forEach((modifyProfile: any) => {
        if (modifyProfile.privileges.length) {
          const index = Array.from(profile.privileges).findIndex((p: any) => p.functionalityKey === modifyProfile.functionalityKey);
          profile.privileges[index].privileges = profile.privileges[index].privileges.concat(modifyProfile.privileges);
        }
      });
    }
  }
}


// Selector
const featureProfileValidationForm = createFeatureSelector(profileFormValidationFeaturekey);
export const selectReadyToSubmit = createSelector(featureProfileValidationForm, (state: any) => !(state.atLeastOne || state.empty || state.existed || state?.profileNameMaxLength || state?.displayNameMaxLength));

export const selectValidationError = createSelector(featureProfileValidationForm, (state: any) => state);
export const selectValidationProp = createSelector(featureProfileValidationForm, (state: any) => state);
export const selectNewProfileData = createSelector(featureProfileValidationForm, (state: any) => state?.newProfileData);
export const selectFormChange = createSelector(featureProfileValidationForm, (state: any) => state?.formChange);
