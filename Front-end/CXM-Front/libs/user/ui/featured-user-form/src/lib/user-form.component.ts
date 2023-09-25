import {
  AfterContentChecked,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import {
  KeyVal,
  selectReturnAddressLevel,
  switchReturnAddressLevel,
  UserDetail,
  UserFormControlService,
} from '@cxm-smartflow/user/data-access';
import {FormBuilder, FormControl, FormGroup, Validators,} from '@angular/forms';
import {distinctUntilChanged, takeUntil} from 'rxjs/operators';
import {Observable, Subject} from 'rxjs';
import {
  UserAddressLevel,
  UserFormActionType,
  UserFormErrorMessages,
  UserFormErrorMessagesModel,
  UserFormModel,
  UserFormProperties,
  UserFormUpdateMode,
  UserFormUpdateModel,
} from '@cxm-smartflow/user/util';
import {EMAIL_PATTERN} from '@cxm-smartflow/shared/data-access/model';
import {removeFalsyObject} from '@cxm-smartflow/shared/utils';
import {Store} from '@ngrx/store';
import {InputSelectionCriteria} from '@cxm-smartflow/shared/ui/form-input-selection';
import {UserUtil} from '@cxm-smartflow/shared/data-access/services';
import {MatPasswordStrengthComponent} from '@angular-material-extensions/password-strength';
import {
  FragmentReturnAddressComponent,
  FragmentReturnAddressType
} from "@cxm-smartflow/shared/fragments/return-address";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'cxm-smartflow-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.scss'],
})
export class UserFormComponent
  implements OnInit, OnDestroy, AfterContentChecked, OnChanges {
  @ViewChild('passwordComponentWithConfirmation', { static: false })
  passwordComponentWithConfirmation: MatPasswordStrengthComponent = new MatPasswordStrengthComponent();

  pattern = new RegExp(/^(?=.*?[äöüÄÖÜß])/);
  // Event.
  @Output() submitForm = new EventEmitter<any>();
  @Output() cancelForm = new EventEmitter<Event>();
  @Output() clientChange = new EventEmitter<number>();
  @Output() serviceIdChange = new EventEmitter<number>();

  @ViewChild('returnAddressElement') returnAddressElement: FragmentReturnAddressComponent;
  // Validation properties.
  mode: UserFormUpdateModel = UserFormUpdateMode.CREATE;
  userFormUpdateMode = UserFormUpdateMode;
  isAdminUser = this.userFormControlService.isAdminUser();

  // field form properties.
  @Input() userDetail: UserDetail | null;
  @Input() isAdmin = true;
  @Input() clients: InputSelectionCriteria[] = [];
  @Input() divService: InputSelectionCriteria[] = [];
  @Input() profiles: InputSelectionCriteria[] = [];
  @Input() isDisabled: boolean;

  profileIdsSelected: number[] = [];
  serviceIdSelected: number;
  clientIdSelected: number;

  // Form group properties.
  formGroup: FormGroup;
  errorFormControl: UserFormModel[] = [];
  userFormProperties = UserFormProperties;
  firstNameErrorMessage: UserFormErrorMessagesModel;
  lastNameErrorMessage: UserFormErrorMessagesModel;
  emailErrorMessage: UserFormErrorMessagesModel;
  passwordErrorMessage: UserFormErrorMessagesModel;
  confirmedPasswordErrorMessage: UserFormErrorMessagesModel;
  clientIdErrorMessage: UserFormErrorMessagesModel;
  serviceIdErrorMessage: UserFormErrorMessagesModel;
  profileIdsErrorMessage: UserFormErrorMessagesModel;

  // Validation properties.
  isPasswordVisible = false;

  returnAddress: FragmentReturnAddressType = {
    line1: '',
    line2: '',
    line3: '',
    line4: '',
    line5: '',
    line6: '',
    line7: ''
  }

  userAddressLevel: KeyVal[];
  translateUserAddressLevel:string;
  selectUserAddressLevel$: Observable<string>;
  destroy$ = new Subject<boolean>();
  isUserLevel = false;
  constructor(
    private readonly fb: FormBuilder,
    private readonly store: Store,
    private readonly ref: ChangeDetectorRef,
    public readonly userFormControlService: UserFormControlService,
    private _translateService: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.checkFormMode();
    this.formControl();

    this.userAddressLevel = UserAddressLevel.returnAddressLevel.map(data => {
      this._translateService.get(data.value).subscribe(value => this.translateUserAddressLevel = value);
      return {
        ...data,
        value: this.translateUserAddressLevel
      }
    });

    this.selectUserAddressLevel$ = this.store.select(selectReturnAddressLevel);
  }

  ngAfterContentChecked() {
    this.ref.detectChanges();
    /*if(this.passwordComponentWithConfirmation)
    this.formGroup.setControl('confirmPassword', this.passwordComponentWithConfirmation.passwordConfirmationFormControl);*/
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.formGroup.reset();
    this.userFormControlService.updateUserPrivilegeInStorage();
  }

  private checkFormMode(): void {
    this.mode = this.userFormControlService.userFormUpdateMode();
  }

  // private async formControl(): Promise<void>{
  //   // Initial form.
  //   this.formGroup = this.fb.group({
  //     id: new FormControl(''),
  //     email: new FormControl(''),
  //     firstName: new FormControl(''),
  //     lastName: new FormControl(''),
  //     password: new FormControl(''),
  //     confirmedPassword: new FormControl(''),
  //     clientId: new FormControl(),
  //     serviceId: new FormControl(),
  //     profiles: new FormControl([]),
  //     admin: new FormControl(false)
  //   });
  //   console.log("firstName formControl  : "+ this.formGroup.get('firstName'));
  //   // Listen to changes in each form control and remove the corresponding error message
  //   this.formGroup.get('firstName')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
  //     this.errorFormControl = this.errorFormControl.filter((property) => property !== UserFormProperties.FIRST_NAME);

  // });

  // this.formGroup.get('lastName')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
  //  this.errorFormControl = this.errorFormControl.filter((property) => property !== UserFormProperties.LAST_NAME);

  // });
  // this.formGroup.get('email')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
  //   this.errorFormControl = this.errorFormControl.filter((property) => property !== UserFormProperties.EMAIL)
  // });

  // this.formGroup.get('password')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
  //   this.errorFormControl = this.errorFormControl.filter((property) => property !== UserFormProperties.PASSWORD)
  // });

  // this.formGroup.get('confirmedPassword')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
  //  this.errorFormControl = this.errorFormControl.filter((property) => property !== UserFormProperties.CONFIRMED_PASSWORD)
  // });

  // this.formGroup.get('clientId')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
  //   this.errorFormControl = this.errorFormControl.filter((property) => property !== UserFormProperties.CLIENT_ID);
  // });

  // this.formGroup.get('serviceId')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((selectedServiceId) => {
  //  if (!this.formGroup.get('clientId')?.value) {
  //  this.errorFormControl = this.errorFormControl.filter((property) => property !== UserFormProperties.SERVICE_ID);
  //  }
  //  if (selectedServiceId) {
  //   this.errorFormControl = this.errorFormControl.filter((property) => property !== UserFormProperties.SERVICE_ID);
  // }

  // });

  // this.formGroup.get('profiles')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((value) => {
  //   if (value.length > 0) {
  //    this.errorFormControl = this.errorFormControl.filter((property) => property !== UserFormProperties.PROFILES);
  //   }
  // });

  //   if (this.mode !== UserFormUpdateMode.UPDATE_MULTIPLE) {
  //     this.formGroup.controls['serviceId'].valueChanges
  //       .pipe(takeUntil(this.destroy$), distinctUntilChanged())
  //       .subscribe((value) => this.serviceIdChange.next(value));

  //     this.formGroup.controls['clientId'].valueChanges
  //       .pipe(takeUntil(this.destroy$), distinctUntilChanged())
  //       .subscribe((value) => this.clientChange.next(value));
  //   }
  // }
  formControl(): void {
    // Initial form.
    this.formGroup = this.fb.group({
      id: new FormControl(''),
      email: new FormControl(''),
      firstName: new FormControl(''),
      lastName: new FormControl(''),
      password: new FormControl(''),
      confirmedPassword: new FormControl(''),
      clientId: new FormControl(),
      serviceId: new FormControl(),
      profiles: new FormControl([]),
      admin: new FormControl(false),
      passwordComponent: ['', [Validators.required, Validators.pattern]],
      userReturnAddress:[]
    });

    // Listen to changes in each form control and remove the corresponding error message
    this.formGroup
      .get('firstName')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.errorFormControl = this.errorFormControl.filter(
          (property) => property !== UserFormProperties.FIRST_NAME
        );
      });

    this.formGroup
      .get('lastName')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.errorFormControl = this.errorFormControl.filter(
          (property) => property !== UserFormProperties.LAST_NAME
        );
      });

    this.formGroup
      .get('email')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.errorFormControl = this.errorFormControl.filter(
          (property) => property !== UserFormProperties.EMAIL
        );
      });

    /*this.formGroup
      .get('password')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.errorFormControl = this.errorFormControl.filter(
          (property) => property !== UserFormProperties.PASSWORD
        );
      });

    this.formGroup
      .get('confirmedPassword')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.errorFormControl = this.errorFormControl.filter(
          (property) => property !== UserFormProperties.CONFIRMED_PASSWORD
        );
      });*/

    this.formGroup
      .get('clientId')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.errorFormControl = this.errorFormControl.filter(
          (property) => property !== UserFormProperties.CLIENT_ID
        );
      });

    this.formGroup
      .get('serviceId')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((selectedServiceId) => {
        if (!this.formGroup.get('clientId')?.value) {
          this.errorFormControl = this.errorFormControl.filter(
            (property) => property !== UserFormProperties.SERVICE_ID
          );
        }
        if (selectedServiceId) {
          this.errorFormControl = this.errorFormControl.filter(
            (property) => property !== UserFormProperties.SERVICE_ID
          );
        }
      });

    this.formGroup
      .get('profiles')
      ?.valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value.length > 0) {
          this.errorFormControl = this.errorFormControl.filter(
            (property) => property !== UserFormProperties.PROFILES
          );
        }
      });

    if (this.mode !== UserFormUpdateMode.UPDATE_MULTIPLE) {
      this.formGroup.controls['serviceId'].valueChanges
        .pipe(takeUntil(this.destroy$), distinctUntilChanged())
        .subscribe((value) => this.serviceIdChange.next(value));

      this.formGroup.controls['clientId'].valueChanges
        .pipe(takeUntil(this.destroy$), distinctUntilChanged())
        .subscribe((value) => this.clientChange.next(value));
    }
  }

  async beforeSubmit(): Promise<void> {
    this.formGroup.addControl('userAction', new FormControl(UserFormActionType.OTHER));
    /*if(this.passwordComponentWithConfirmation) {
      this.formGroup.setControl('password',this.passwordComponentWithConfirmation.passwordConfirmationFormControl);
      this.formGroup.setControl('confirmedPassword',this.passwordComponentWithConfirmation.passwordConfirmationFormControl);
     }*/
    // await this.checking();
    if (this.isUserLevel && this.checkReturnAddressHaveInput()) {
      const addressDestination: FragmentReturnAddressType | null | undefined = await this.returnAddressElement.getValueAndValidity();
      if (!addressDestination) {
        return;
      }
    }
    switch (this.mode) {
      case UserFormUpdateMode.CREATE:
        this.validateCreatingUserForm();
        this.formGroup.get('userAction')?.setValue(UserFormActionType.CREATE);
        break;
      case UserFormUpdateMode.UPDATE_SINGLE:
        this.validateUpdatingUserForm();
        this.formGroup.get('userAction')?.setValue(UserFormActionType.UPDATE);
        break;
      case UserFormUpdateMode.UPDATE_MULTIPLE:
        this.validateUpdatingBatchUserForm();
        this.formGroup.get('userAction')?.setValue(UserFormActionType.UPDATE);
        break;
    }

    if (
      this.mode === UserFormUpdateMode.CREATE &&
      !this.errorFormControl.includes(UserFormProperties.EMAIL) &&
      this.userFormControlService.isCanCreate
    ) {
      const isAvailableEmail = await this.userFormControlService.checkDuplicatedEmail(
        this.formGroup.getRawValue()?.email?.trim()
      );
      // Validate duplicated email.
      isAvailableEmail
        ? this.errorFormControl.filter((e) => e !== UserFormProperties.EMAIL)
        : this.errorFormControl.push(UserFormProperties.EMAIL) || [];

      if (isAvailableEmail && this.errorFormControl.length === 0) {
        this.formGroup.patchValue({userReturnAddress: this.returnAddress});
        const formvalue = this.formGroup.getRawValue();
        this.submitForm.next({
          admin: formvalue.admin,
          ...removeFalsyObject(formvalue),
        });
      }
    }

    if (this.mode === UserFormUpdateMode.UPDATE_SINGLE && this.errorFormControl.length === 0 && this.userFormControlService.isCanModify) {
      /*const { id, firstName, lastName, password, profiles, serviceId, admin } = this.formGroup.getRawValue();
      this.submitForm.next({admin, ...removeFalsyObject({ id, firstName, lastName, password, profiles, serviceId })});*/
      this.formGroup.patchValue({userReturnAddress: this.returnAddress});
      const {id, firstName, lastName, profiles, serviceId, admin, userReturnAddress} = this.formGroup.getRawValue();
      this.submitForm.next({
        admin, ...removeFalsyObject({
          id,
          firstName,
          lastName,
          profiles,
          serviceId,
          userReturnAddress
        })
      });
    }

    const { profiles } = this.formGroup.getRawValue();
    if (
      this.mode === UserFormUpdateMode.UPDATE_MULTIPLE &&
      this.errorFormControl.length === 0 &&
      this.userFormControlService.isCanModify
    ) {
      this.submitForm.next(
        removeFalsyObject({
          profiles,
          userIds: this.userFormControlService.getModificationUsers()
        })
      );
    }

  }

   private async validateCreatingUserForm(): Promise<void> {
    this.formGroup.removeControl('password');
    this.formGroup.removeControl('confirmedPassword');

    this.validateUserForm(this.formGroup.getRawValue());
  }

  private validateUpdatingUserForm(): void {
    if (this.userFormControlService.isAdminUser()) {
      /*const {clientId, firstName, lastName, password, confirmedPassword, serviceId, profiles } = this.formGroup.getRawValue();
      let params: any = {clientId, firstName, lastName, password, confirmedPassword, serviceId, profiles };*/
      const {clientId, firstName, lastName, serviceId, profiles } = this.formGroup.getRawValue();
      let params: any = {clientId, firstName, lastName, serviceId, profiles };
      //if (!password?.trim() && !confirmedPassword?.trim()) {
      params = {clientId, firstName, lastName, profiles, serviceId };
      //}
      this.validateUserForm(params);
    } else {
      const {
        clientId,
        firstName,
        lastName,
        profiles,
        serviceId,
      } = this.formGroup.getRawValue();
      this.validateUserForm({
        clientId,
        firstName,
        lastName,
        profiles,
        serviceId,
      });
    }
  }

  private validateUpdatingBatchUserForm(): void {
    const { profiles } = this.formGroup.getRawValue();
    this.validateUserForm({ profiles });
  }

  validateUserForm(formValue: any): void {
    if (formValue) {
      for (const formValueKey in formValue) {
        // Normal users.
        switch (formValueKey) {
          case UserFormProperties.FIRST_NAME: {
            const isNotBlank = formValue[formValueKey]?.trim()?.length > 0;
            const isValidLength =
              formValue[formValueKey]?.trim()?.length <= 128;

            // Set error messages.
            if (!isNotBlank) {
              this.firstNameErrorMessage =
                UserFormErrorMessages.FIRST_NAME_REQUIRED;
            } else if (!isValidLength) {
              this.firstNameErrorMessage =
                UserFormErrorMessages.FIRST_NAME_INVALID_LENGTH;
            }

            // Alert error.
            isNotBlank && isValidLength
              ? this.errorFormControl.filter(
                  (e) => e !== UserFormProperties.FIRST_NAME
                )
              : this.errorFormControl.push(UserFormProperties.FIRST_NAME) || [];
            break;
          }

          case UserFormProperties.LAST_NAME: {
            const isNotBlank = formValue[formValueKey]?.trim()?.length > 0;
            const isValidLength =
              formValue[formValueKey]?.trim()?.length <= 128;

            // Set error messages.
            if (!isNotBlank) {
              this.lastNameErrorMessage =
                UserFormErrorMessages.LAST_NAME_REQUIRED;
            } else if (!isValidLength) {
              this.lastNameErrorMessage =
                UserFormErrorMessages.LAST_NAME_INVALID_LENGTH;
            }

            // Alert error.
            isNotBlank && isValidLength
              ? this.errorFormControl.filter(
                  (e) => e !== UserFormProperties.LAST_NAME
                )
              : this.errorFormControl.push(UserFormProperties.LAST_NAME) || [];
            break;
          }

          case UserFormProperties.EMAIL: {
            const isNotBlank = formValue[formValueKey]?.trim()?.length > 0;
            const isPatternMatch =
              formValue[formValueKey]?.trim()?.match(EMAIL_PATTERN)?.length > 0;
            const isValidLength =
              formValue[formValueKey]?.trim()?.length <= 128;

            // Set error messages.
            if (!isNotBlank) {
              this.emailErrorMessage = UserFormErrorMessages.EMAIL_REQUIRED;
            } else if (!isPatternMatch) {
              this.emailErrorMessage =
                UserFormErrorMessages.EMAIL_INVALID_PATTERN;
            } else if (!isValidLength) {
              this.emailErrorMessage =
                UserFormErrorMessages.EMAIL_INVALID_LENGTH;
            } else {
              this.emailErrorMessage = UserFormErrorMessages.EMAIL_DUPLICATED;
            }

            // Alert error.
            isNotBlank && isPatternMatch && isValidLength
              ? this.errorFormControl.filter(
                  (e) => e !== UserFormProperties.EMAIL
                )
              : this.errorFormControl.push(UserFormProperties.EMAIL) || [];
            break;
          }

          /*case UserFormProperties.PASSWORD: {
            const isNotBlank = formValue[formValueKey]?.trim()?.length > 0;
            const isValidLength =
              formValue[formValueKey]?.trim()?.length <= 128;


            if (!isNotBlank) {
              this.passwordErrorMessage =
                UserFormErrorMessages.PASSWORD_REQUIRED;
            } else if (!isValidLength) {
              this.passwordErrorMessage =
                UserFormErrorMessages.PASSWORD_INVALID_LENGTH;
            }

            isNotBlank && isValidLength ?
              this.errorFormControl.filter(e => e !== UserFormProperties.PASSWORD) :
              this.errorFormControl.push(UserFormProperties.PASSWORD) || [];
            break;
          }

          case UserFormProperties.CONFIRMED_PASSWORD: {
            const isValid =
              formValue[formValueKey]?.trim() ===
              this.formGroup.getRawValue()?.password?.trim();
            const isPasswordBlank =
              this.formGroup.getRawValue()?.password?.trim()?.length === 0;
            const isValidLength =
              formValue[formValueKey]?.trim()?.length <= 128;

            // Set error messages.
            if (!isValid) {
              this.confirmedPasswordErrorMessage =
                UserFormErrorMessages.CONFIRMED_PASSWORD_NOT_EQUAL_TO_PASSWORD;
            } else if (!isValidLength) {
              this.confirmedPasswordErrorMessage =
                UserFormErrorMessages.CONFIRMED_PASSWORD_INVALID_LENGTH;
            }

            // Alert error.
            (isValid && isValidLength) || isPasswordBlank
              ? this.errorFormControl.filter(
                  (e) => e !== UserFormProperties.CONFIRMED_PASSWORD
                )
              : this.errorFormControl.push(
                  UserFormProperties.CONFIRMED_PASSWORD
                ) || [];
            break;
          }*/

          case UserFormProperties.SERVICE_ID: {
            // Set error messages.
            this.serviceIdErrorMessage =
              UserFormErrorMessages.SERVICE_ID_REQUIRED;

            // Alert error.
            formValue[formValueKey] > 0 || formValue[formValueKey] === undefined
              ? this.errorFormControl.filter(
                  (e) => e !== UserFormProperties.SERVICE_ID
                )
              : this.errorFormControl.push(UserFormProperties.SERVICE_ID) || [];
            break;
          }

          case UserFormProperties.PROFILES: {
            // Set error messages.
            this.profileIdsErrorMessage =
              UserFormErrorMessages.PROFILE_IDS_REQUIRED;
            formValue[formValueKey]?.length > 0
              ? this.errorFormControl.filter(
                  (e) => e !== UserFormProperties.PROFILES
                )
              : this.errorFormControl.push(UserFormProperties.PROFILES) || [];
            break;
          }
        }

        // Admin.
        if (UserUtil.isAdmin()) {
          if (UserFormProperties.CLIENT_ID === formValueKey) {
            // Set error messages.
            this.clientIdErrorMessage =
              UserFormErrorMessages.CLIENT_ID_REQUIRED;

            // Alert error.
            formValue[formValueKey] > 0
              ? this.errorFormControl.filter(
                  (e) => e !== UserFormProperties.CLIENT_ID
                )
              : this.errorFormControl.push(UserFormProperties.CLIENT_ID) || [];
          }
        }
      }
    }
  }

  selectProfile(profileResponse: any[]) {
    profileResponse = profileResponse.map((e) => JSON.parse(e));
    this.profileIdsSelected = profileResponse;
    this.formGroup.controls['profiles'].setValue(profileResponse);
  }

  onClientSelectEvent(clientId: number) {
    this.formGroup.controls['clientId']?.setValue(clientId);
    this.clientIdSelected = clientId;

    if (clientId === this.userDetail?.client.id) {
      this.serviceIdSelected = this.userDetail.service.id;
      this.profileIdsSelected = this.userDetail.profiles?.map((e) => e.id);

      this.formGroup.patchValue({
        serviceId: this.serviceIdSelected,
        profiles: this.profileIdsSelected,
      });
    } else {
      // Clear serviceId & profileId.
      this.serviceIdSelected = 0;
      this.profileIdsSelected = [];

      this.formGroup.patchValue({
        serviceId: 0,
        profiles: [],
      });
    }
  }

  selectService(selectedService: number) {
    this.serviceIdSelected = selectedService;
    this.formGroup.controls['serviceId']?.setValue(selectedService);
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  get password(): string {
    return this.formGroup?.get('password')?.value;
  }

  get confirmedPassword(): string {
    return this.formGroup?.get('confirmedPassword')?.value;
  }

  async ngOnChanges(changes: SimpleChanges): Promise<void> {
    if (changes?.userDetail) {
      // Set up updated user-form.
      if (
        (await this.userFormControlService.userFormUpdateMode()) ===
        UserFormUpdateMode.UPDATE_SINGLE
      ) {
        if (this.userDetail) {
          this.formGroup.patchValue({
            id: this.userDetail.id,
            email: this.userDetail.email,
            firstName: this.userDetail.firstName,
            lastName: this.userDetail.lastName,
            clientId: this.userDetail.client.id,
            serviceId: this.userDetail.service.id,
            admin: this.userDetail.admin,
            profiles: this.userDetail.profiles?.map((e: any) => e.id),
          });


          const level = UserAddressLevel.returnAddressLevel.find(e => e.val.toLowerCase() == this.userDetail?.returnAddressLevel)?.key || '';
          this.store.dispatch(switchReturnAddressLevel({returnAddressLevel:level}));
          this.checkUserLevel();

          if (this.userDetail.userReturnAddress) {
            const {line1, line2, line3, line4, line5, line6, line7} = this.userDetail.userReturnAddress;
            this.returnAddress = {
              line1: line1 || "",
              line2: line2 || "",
              line3: line3 || "",
              line4: line4 || "",
              line5: line5 || "",
              line6: line6 || "",
              line7: line7 || "",
            }
          }


          this.profileIdsSelected = this.userDetail.profiles?.map(
            (e: any) => e.id
          );
          this.serviceIdSelected = this.userDetail.service.id;
          this.clientIdSelected = this.userDetail.client.id;
        }
      }
    }
  }
  onStrengthChanged(strength: number) {
    //console.log("",strength);
  }

  returnAddressLevelChanged(returnAddressLevel: string): void {
    this.store.dispatch(switchReturnAddressLevel({returnAddressLevel}));
    this.checkUserLevel();
  }

  updateReturnAddress(returnAddress: FragmentReturnAddressType): void {
    this.returnAddress = returnAddress;
  }

  checkUserLevel(): void {
    this.selectUserAddressLevel$.subscribe(value => {
      const level = UserAddressLevel.returnAddressLevel.find(e => e.key == value)?.val || '';
      this.isUserLevel = level.toLowerCase() === "user";
    }).unsubscribe();
  }
  
  checkReturnAddressHaveInput(): boolean {
    const returnAddress = this.returnAddress;
    for (const line in returnAddress) {
      if (returnAddress[line as keyof FragmentReturnAddressType] !== '') {
        return true;
      }
    }
    return false;
  }


}
