import { AfterContentInit, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import {
  clearValidateFormResult,
  PREDEFINE_PERMISSION_FORM,
  PredefinedFormModel,
  PredefinedFormPrevilege,
  selectProfileForm,
  selectReadyToSubmit,
  selectUserCreatedBy,
  selectValidationError,
  submitProfile,
  validateForm,
  validateProfileName,
  selectClientProfilleForm,
  getAllClient,
  selectClientCriteria,
  selectClientId,
  validateClientId,
  selectProfileLoadingComplete,
  ProfileTablinks, selectUserOwnerId
} from '@cxm-smartflow/profile/data-access';
import { TranslateService } from '@ngx-translate/core';
import { Store } from '@ngrx/store';
import { debounceTime, filter, map, takeUntil, withLatestFrom } from 'rxjs/operators';
import { BehaviorSubject, Observable, ReplaySubject, Subject, Subscription } from 'rxjs';
import {CanModificationService, SnackBarService, UserUtil} from '@cxm-smartflow/shared/data-access/services';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import {UserFormControlService} from "@cxm-smartflow/user/data-access";
import {UserManagement} from "@cxm-smartflow/shared/data-access/model";

@Component({
  selector: 'cxm-smartflow-user-profile-permission-form',
  templateUrl: './user-profile-permission-form.component.html',
  styleUrls: ['./user-profile-permission-form.component.scss']
})
export class UserProfilePermissionFormComponent
  implements OnInit, AfterContentInit, OnDestroy {
  @Input() isEditMode: boolean;

  // from properties.
  form: FormGroup;
  btnSubmitLabel: string;
  name: string;

  // properties.
  formStructure = PREDEFINE_PERMISSION_FORM;
  formloaded = false;


  predefinedForm: PredefinedFormModel[] = [];
  showHidePrivilege: any = {};
  destroyedSubject$ = new Subject();
  isReadyToSubmit: boolean;

  // tooltip properties.
  showTooltipBackground = true;
  showNameTooltip$ = new BehaviorSubject(false);
  showDisplayNameTooltip$ = new BehaviorSubject(false);
  showHideSubmitError = new ReplaySubject<boolean>(1);
  showAtLeastOneProfileTooltip$ = new BehaviorSubject(false);
  currentParentChecked: string;
  currentClientChecked: string;
  isCanModify: boolean;

  clients$: Observable<any[]>;
  clientId$: Observable<any>;
  clientErrorMsg$: Observable<any>;
  isShowError$ = new BehaviorSubject<boolean>(false);
  selectedClientId = 0;
  isAdmin = UserUtil.isAdmin();

  errorType = {
    existed: false,
    displayName: false,
    atLeastOne: false,
    empty: false,
    profileNameMaxLength: false,
    displayNameMaxLength: false
  };

  // Translate properties.
  errorLabel: any;
  destroy$ = new Subject<boolean>();
  checkNameSubscription: Subscription;

  constructor(
    private fb: FormBuilder,
    private translate: TranslateService,
    private store: Store,
    private snackBar: SnackBarService,
    private location: Location,
    private router: Router,
    private userFormControl: UserFormControlService,
    private canModification: CanModificationService
  ) {
    this.form = fb.group({});
  }

  ngOnDestroy(): void {
    this.showNameTooltip$.complete();
    this.showAtLeastOneProfileTooltip$.complete();
    this.store.complete();
    this.destroyedSubject$.next();
    this.destroyedSubject$.complete();
    this.store.dispatch(clearValidateFormResult());
    this.form.reset();
  }

  ngOnInit(): void {
    this.translate.get('profile.form').subscribe((language) => {
      this.errorLabel = language?.errors;
    });
    this.checkPrivilege();

    this.btnSubmitLabel = this.isEditMode ? 'modify' : 'create';

    this.store.select(selectProfileLoadingComplete)
    // .pipe(filter(x => x == true))
    .pipe(takeUntil(this.destroyedSubject$))
    .subscribe((val) => {
      if(val===false) return;
      // setup form on ready
      this.store.select(selectClientProfilleForm)
      .pipe(withLatestFrom(this.store.select(selectProfileForm)))
      .pipe(takeUntil(this.destroyedSubject$))
      .subscribe(([client, response]) => {
        const { form } = client;
        this.formStructure = form;
        this.setupForm();
        this.patchFormData(response)
      })
    })

    // Get form status (Ready to submit) from store.
    this.store
      .select(selectReadyToSubmit)
      .pipe(takeUntil(this.destroyedSubject$))
      .subscribe((ready) => {
        this.isReadyToSubmit = ready;
      });

    // Get validation result from store.
    this.store
      .select(selectValidationError)
      .pipe(takeUntil(this.destroyedSubject$))
      .subscribe((errors) => {
        const { existed, displayName, atLeastOne, empty, profileNameMaxLength, displayNameMaxLength } = errors;
        this.errorType = errors;
        this.showNameTooltip$.next((existed || profileNameMaxLength) && !empty);
        this.showDisplayNameTooltip$.next(displayNameMaxLength);
      });

    // Alert error message.
    this.showAtLeastOneProfileTooltip$.subscribe((isShow) => {
      if (isShow) {
        this.snackBar.openCustomSnackbar({
          message: this.errorLabel?.atLeastOne,
          type: 'error',
          icon: 'close'
        });
      }
    });

    // Initialize data to from.
    if (this.isAdmin) {
      this.store.dispatch(getAllClient());
      this.clients$ = this.store.select(selectClientCriteria).pipe(
        filter(e => e),
        map(e => e.map((e: any) => { return { key: e.id, value: e.name } }))
      );
      this.clientId$ = this.store.select(selectClientId);
      this.clientId$.pipe(takeUntil(this.destroyedSubject$))
        .subscribe((clientId: number) => this.selectedClientId = clientId);
      this.clientErrorMsg$ = this.translate.get('profile.form.errors.clientIsRequired');
    }
  }

  setupForm() {
    this.removeAllControl();

    const controls = this.formStructure.reduce(this.mapCreateFormControl, {});
    this.form.addControl('name', this.fb.control({ value: '', disabled: this.isEditMode}));
    this.form.addControl('displayName', this.fb.control(''));

    // Add dynamic control
    Object.keys(controls).forEach((k) => this.form.addControl(k, controls[k]));


    this.checkvalideName();
  }

  enableFunctional(): void {
    this.formStructure.forEach((item) => {
      this.form.get(`${item.code}Vis`)?.enable();
      this.form.get(`${item.code}Modi`)?.enable();
    });
  }

  removeAllControl() {
    Object.keys(this.form.controls).forEach((k) => this.form.removeControl(k));
  }

  get nameErrorTooltipLabel() {
    if (this.errorType?.existed && !this.errorType?.empty) {
      return this.errorLabel?.nameExisting;
    } else if (this.errorType?.profileNameMaxLength) {
      return this.errorLabel?.nameMaxLength;
    } else {
      return this.errorLabel?.missing;
    }
  }

  get displayNameErrorTooltipLabel(){
    return this.errorLabel?.displayNameMaxLength;
  }

  toggleShowHide(code: string) {
    this.showHidePrivilege[code] = !this.showHidePrivilege[code];
  }

  mapCreateFormControl = (prev: any, cur: any) => {
    Object.assign(this.showHidePrivilege, { [cur.code]: false });

    const functionCtrls = this.createControl(cur);

    const subFunctionsCtls = cur.func.reduce(
      (prevPrevil: any, curPrevil: any) => {
        const a = this.createControl(curPrevil as PredefinedFormModel) as [];
        return [...prevPrevil, ...a];
      },
      []
    );
    return Object.assign(prev, ...functionCtrls, ...subFunctionsCtls);
  };

  createControl(cur: PredefinedFormModel) {
    const ctrCheckbox = { [`${cur.code}Ck`]: this.fb.control({value: cur.checked, disabled: !cur.allowed}) };
    const ctrVisibility = {
      [`${cur.code}Vis`]: this.fb.control({
        value: cur.visibility,
        disabled: !cur.allowed
      })
    };
    const ctrModification = {
      [`${cur.code}Modi`]: this.fb.control({
        value: cur.modification,
        disabled: !cur.allowed
      })
    };
    const ctrHiddenId = {
      [`${cur.code}ID`]: this.fb.control({
        value: cur.id,
        disabled: !cur.allowed
      })
    };

    return [ctrCheckbox, ctrVisibility, ctrModification, ctrHiddenId];
  }

  checkboxChanged($event: any, row: any) {
    this.enableControlsGroupByCode(row.code, $event?.currentTarget?.checked);

    if (row.func && row.func.length > 0) {
      this.subFunctionCheckboxChange($event?.currentTarget?.checked, row.func);
    }
  }

  enableControlsGroupByCode(code: string, checked: boolean) {
    if (checked) {
      this.form.get(`${code}ID`)?.enable();
    } else {
      this.form.get(`${code}ID`)?.disable();
    }
  }

  subFunctionCheckboxChange(
    checked: boolean,
    items: PredefinedFormPrevilege[]
  ) {
    Array.from(items).forEach((item) => {
      // Developer note: set onlySelf, emitEvent so It won't cause form update in loop
      this.form
        .get(`${item.code}Ck`)
        ?.patchValue(checked, { onlySelf: true, emitEvent: false });
      // Developer note: better not to reuse from line 90-98 for flexibility purpose.
      if (checked) {
        this.form.get(`${item.code}ID`)?.enable();
      } else {
        this.form.get(`${item.code}ID`)?.disable();
      }
    });
    // Developer note: validate form value after batch update
    this.form.updateValueAndValidity();
  }

  functionVisibilityChanged($event: any, row: any) {
    if ($event === 'specific') {
      Array.from(row.func).forEach((item: any) => {
        // this.form.get(`${item.code}Vis`)?.enable();
        this.form.get(`${item.code}Vis`)?.setErrors({'customDisable': false});
      });
    } else {
      Array.from(row.func).forEach((item: any) => {
        // this.form.get(`${item.code}Vis`)?.disable();
        this.form.get(`${item.code}Vis`)?.setErrors({'customDisable': true});
      });

      this.subFunctionVisibilityChanged($event, row.func);
    }
  }

  subFunctionVisibilityChanged($event: any, items: PredefinedFormPrevilege[]) {
    Array.from(items).forEach((item) => {
      this.form
        .get(`${item.code}Vis`)
        ?.patchValue($event, { onlySelf: true, emitEvent: false });
    });
    // Developer note: validate form value after batch update
    this.form.updateValueAndValidity();
  }

  functionModificationChange($event: any, row: any) {
    if ($event === 'specific') {
      Array.from(row.func).forEach((item: any) => {
        // this.form.get(`${item.code}Modi`)?.enable();
        this.form.get(`${item.code}Modi`)?.setErrors({'customDisable': false});
      });
    } else {
      Array.from(row.func).forEach((item: any) => {
        // this.form.get(`${item.code}Modi`)?.disable();
        this.form.get(`${item.code}Modi`)?.setErrors({'customDisable': true});
      });
      this.subfunctionModificationChange($event, row.func);
    }
  }

  subfunctionModificationChange($event: any, items: PredefinedFormPrevilege[]) {
    Array.from(items).forEach((item) => {
      this.form
        .get(`${item.code}Modi`)
        ?.patchValue($event, { onlySelf: true, emitEvent: false });
    });

    this.form.updateValueAndValidity();
  }

  shouldUpdateFuncControl(
    $event: any,
    parent: PredefinedFormPrevilege,
    child: PredefinedFormPrevilege
  ) {
    const visParentSelection = this.form.get(`${parent.code}Vis`);
    const modiParentSelection = this.form.get(`${parent.code}Modi`);
    this.currentParentChecked = parent.code;
    this.currentClientChecked =
      parent.code === 'cxm_statistic_report'
        ? 'cxm_statistic_report'
        : parent.code;

    if (visParentSelection?.value === 'specific') {
      this.form.get(`${child.code}Vis`)?.enable();
    }

    if (modiParentSelection?.value === 'specific') {
      this.form.get(`${child.code}Modi`)?.enable();
    }

    this.validateFunctionalChecked(this.form.getRawValue());
  }

  toIntermediateModel(form: any) {
    return this.predefinedForm.map((item) => {
      const checkName = item.code + 'Ck';
      const visName = item.code + 'Vis';
      const modiName = item.code + 'Modi';
      const hiddenId = item.code + 'ID';

      const privileges = item.func.map((subitem) => {
        return {
          name: subitem.name,
          code: subitem.code,
          checked: form[subitem.code + 'Ck'],
          visibility: form[subitem.code + 'Vis'],
          modification: form[subitem.code + 'Modi'],
          id: form[subitem.code + 'ID']
        };
      });

      return {
        name: item.name,
        code: item.code,
        checked: form[checkName],
        visibility: form[visName],
        modification: form[modiName],
        id: form[hiddenId],
        privileges
      };
    });
    // .filter(x => x.checked);
  }

  patchFormData(val: any) {
    const { perms, nameForm } = val;
    // need to use destruction otherwise we can't use check box (readonly state)
    this.predefinedForm = Array.from(perms).map((x: any) => ({
      ...x,
      func: [...x.func]
    }));

    const formdata = this.predefinedForm.reduce((prev, cur) => {
      const a = this.controlNameMapper(cur);
      const b = Array.from(cur.func).reduce((preFunc: any, curFunc: any) => {
        const temp = { ...curFunc };

        if (temp.checked === false && cur.modification && temp.modification) {
          if (cur.modification !== 'specific')
            temp.modification = cur.modification;
        }

        if (temp.checked === false && cur.visibility && temp.visibility) {
          if (cur.visibility !== 'specific') temp.visibility = cur.visibility;
        }

        const g = this.controlNameMapper(temp);
        return [...preFunc, ...g];
      }, []);
      return Object.assign(prev, ...a, ...b);
    }, {});

    this.name = nameForm?.name;

    this.form.patchValue({
      name: nameForm.name,
      displayName: nameForm.displayName,
      ...formdata
    });

    this.form.updateValueAndValidity();
    this.formloaded = true;
  }

  controlNameMapper(cur: any) {
    const ckValue = { [`${cur.code}Ck`]: cur.checked };
    const visValue = { [`${cur.code}Vis`]: cur.visibility };
    const modiValue = { [`${cur.code}Modi`]: cur.modification };
    const idValue = { [`${cur.code}ID`]: cur.id };

    this.enableControlsGroupByCode(cur.code, cur.checked);

    return [ckValue, visValue, modiValue, idValue];
  }

  checkvalideName() {
    // Check duplicate profile name, Only create form.
    if (!(this.router.url.indexOf('update-profile') > -1)) {
      if(this.checkNameSubscription) {
        this.checkNameSubscription.unsubscribe();
      }

      // Name event to validate is duplicate or not duplicate.
      const nameControl = this.form.get('name');
      if(nameControl) {
        this.checkNameSubscription = nameControl.valueChanges.pipe(takeUntil(this.destroyedSubject$), debounceTime(400))
        .subscribe((value) => {
          // hide error tooltip.
          this.showHideSubmitError.next(false);
          if (this.selectedClientId > 0 || !UserUtil.isAdmin()) {
            this.store.dispatch(validateProfileName({ name: value }));
          }
        });
      }

    }
  }

  ngAfterContentInit(): void {
    // Form event.
    this.form.valueChanges
      .pipe(takeUntil(this.destroyedSubject$), debounceTime(400))
      .subscribe((value) => {
        // hide error tooltip.
        this.showHideSubmitError.next(false);
        // Validate form value.
        const { name, displayName } = value;
        this.store.dispatch(
          validateForm({
            perm: this.toIntermediateModel(value),
            displayName,
            name: name || this.name,
          })
        );
      });



    this.showHideSubmitError
      .pipe(takeUntil(this.destroyedSubject$))
      .subscribe((value) => {
        if (value) {
          this.showNameTooltip$.next(
            this.errorType?.existed || this.errorType?.empty || this.errorType?.profileNameMaxLength
          );

          this.showAtLeastOneProfileTooltip$.next(
            this.errorType?.atLeastOne &&
            !this.errorType?.existed &&
            !this.errorType?.empty &&
            !this.errorType?.profileNameMaxLength &&
            !this.errorType?.displayNameMaxLength
          );
          this.isShowError$.next(this.selectedClientId <= 0);
        } else {
          this.isShowError$.next(false);
          this.showNameTooltip$.next(false);
          this.showAtLeastOneProfileTooltip$.next(false);
        }
      });
  }

  validateFunctionalChecked(data: any) {
    if (this.currentParentChecked) {
      const exiting = Object.entries(data)
        .map(([key, value]) => ({
          key,
          value
        }))
        .some(
          (item) =>
            item.key.startsWith(this.currentClientChecked.concat('_')) &&
            item.value === true
        );

      this.form.get(`${this.currentParentChecked}Ck`)?.setValue(exiting);
    }
  }

  submit() {
    if (this.isReadyToSubmit && (this.selectedClientId > 0 || !this.isAdmin)) {
      const profilePerm = this.toIntermediateModel(this.form.getRawValue());
      const { name, displayName } = this.form.getRawValue();
      this.store.dispatch(
        submitProfile({ perm: profilePerm, name, displayName, clientId: this.selectedClientId })
      );
    } else {
      this.showHideSubmitError.next(true);
    }
  }

  cancel() {
    this.router.navigateByUrl(ProfileTablinks.listProfile.link);
  }

  private checkPrivilege(): void {
    this.store.select(selectUserOwnerId)
      .pipe(takeUntil(this.destroy$), filter(e => e !== undefined))
      .subscribe((ownerId: number) => {
        this.isCanModify = this.canModification.hasModify(UserManagement.CXM_USER_MANAGEMENT, UserManagement.MODIFY_PROFILE, ownerId, true);
      })
  }

  selectClient($event: any) {
    this.selectedClientId = JSON.parse($event);
    this.store.dispatch(validateClientId({ clientId: this.selectedClientId }));
    this.showHideSubmitError.next(false);
    this.form.get('name')?.patchValue(this.form.getRawValue().name);
  }
}
