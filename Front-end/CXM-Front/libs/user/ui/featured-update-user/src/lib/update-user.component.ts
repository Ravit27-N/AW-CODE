import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import {
  Client,
  getAllProfileByServiceId,
  loadClientCriteria,
  loadClientService,
  loadClientServiceInUser,
  loadProfileList,
  ProfileAssigned,
  selectAllProfile,
  selectClientCriteria,
  selectClients,
  selectDivServiceWrappers,
  selectDivServiceUserWrappers,
  submitModifyBatchOfUser,
  submitModifySingleUser,
  UserDetail,
  UserFormControlService,
  UserService
} from '@cxm-smartflow/user/data-access';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Observable, Subject, combineLatest } from 'rxjs';
import { filter, map, pluck, take, takeUntil } from 'rxjs/operators';
import { UserFormUpdateMode } from '@cxm-smartflow/user/util';
import { ActivatedRoute } from '@angular/router';
import { InputSelectionCriteria } from '@cxm-smartflow/shared/ui/form-input-selection';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { Location } from '@angular/common';
import { MatPasswordStrengthComponent } from '@angular-material-extensions/password-strength';
import { TitleCaseUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-update-user',
  templateUrl: './update-user.component.html',
  styleUrls: ['./update-user.component.scss'],
  providers: [Location],
})
export class UpdateUserComponent implements OnInit, OnDestroy {
  @ViewChild('passwordComponentWithConfirmation', { static: false })
  passwordComponentWithConfirmation: MatPasswordStrengthComponent = new MatPasswordStrengthComponent();
  // Prefill form.
  clientWrapper$ = new BehaviorSubject<InputSelectionCriteria[]>([]);
  divServiceWrapper$ = new BehaviorSubject<InputSelectionCriteria[]>([]);
  userProfiles$ = new BehaviorSubject<InputSelectionCriteria[]>([]);
  profiles$ = new BehaviorSubject<InputSelectionCriteria[]>([]);
  profilesResponse$: Observable<any>;
  isAdmin$ = new BehaviorSubject(false);
  userDetail: UserDetail;

  // validate property
  mode = this.userFormControlService.userFormUpdateMode();
  userFormUpdateMode = UserFormUpdateMode;
  destroy$ = new Subject<boolean>();

  combinedProfiles$: Observable<InputSelectionCriteria[]> = combineLatest([
    this.userProfiles$,
    this.profiles$
]).pipe(
    map(([userProfiles, otherProfiles]) => {
        // Filtering out otherProfiles that might already be in userProfiles
        const filteredOtherProfiles = otherProfiles.filter(profile => 
            !userProfiles.some(userProfile => userProfile.key === profile.key)
        );
        return [...userProfiles, ...filteredOtherProfiles];
    })
);
  ngOnInit(): void {
    this.initialFormData();
    this.isAdmin$.next(UserUtil.isAdmin());
    // this.isAdmin$.next(UserUtil.isAdmin());
    // this.initialFormData();
    // this.isAdmin$.next(UserUtil.isAdmin());
    // this.initialFormData();
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
    this.userFormControlService.removeModificationUsersCriteriaStorage();
  }
  constructor(
    private store: Store,
    private _activateRoute: ActivatedRoute,
    public userFormControlService: UserFormControlService,
    private userService: UserService
  ) {}

  async update(updateUserModel: any) {
    const mode = this.userFormControlService.userFormUpdateMode();
    if (mode === UserFormUpdateMode.UPDATE_SINGLE) {
      const { firstName, lastName } = updateUserModel;
      updateUserModel = {
        ...updateUserModel,
        firstName: TitleCaseUtil.convert(firstName),
        lastName: lastName.toUpperCase(),
      };
      this.store.dispatch(submitModifySingleUser({ updateUserModel }));
    } else if (mode === UserFormUpdateMode.UPDATE_MULTIPLE) {
      this.store.dispatch(submitModifyBatchOfUser({ ...updateUserModel }));
    }
  }

  cancel(): void {
    this.userFormControlService.navigateToList();
  }

  public async delete() {
    await this.userFormControlService.deleteUser();
  }

  private async initialFormData() {
    if (
      (await this.userFormControlService.userFormUpdateMode()) ===
      UserFormUpdateMode.UPDATE_SINGLE
    ) {
      const id = await this.userFormControlService.getUserId();
      this.userService.getUserById(id).subscribe((userDetail: UserDetail) => {
        this.userDetail = userDetail;

        const userProfilesMapping = userDetail.profiles.map(p => {
          return {
            key: p.id,
            value: p.name,
          };
        });
        this.userProfiles$.next(userProfilesMapping);

        if (UserUtil.isAdmin()) {
          this.store.dispatch(
            loadClientService({ clientId: userDetail.client.id })
          );
        }

        if(!UserUtil.isAdmin()) {
          const userId = typeof userDetail.id === 'number' ? userDetail.id : parseInt(userDetail.id, 10);
          this.store.dispatch(loadClientServiceInUser({ userId }));
        }

      });


      if (UserUtil.isAdmin()) {
        this.store.dispatch(loadClientCriteria({ sortDirection: 'asc' }));
        this.store.select(selectClientCriteria).subscribe((clientCriteria) => {
          const mappingClients = clientCriteria.map((c) => {
            const item: InputSelectionCriteria = {
              key: c.id,
              value: c.name,
            };
            return item;
          });

          this.clientWrapper$.next(mappingClients);
        });
      }

      if (!UserUtil.isAdmin()) {
        this.store.dispatch(loadClientService({ clientId: undefined }));
        const userId = UserUtil.getOwnerId();
        this.store.dispatch(loadClientServiceInUser({ userId }));
      }

      /**
       * Temporary solution.
       * 1. Dynamic profile when change clients.
       * 2. Rollback to old profile, when user select the same old client.
       * */
      this.store
        .select(selectClients)
        .pipe(
          takeUntil(this.destroy$),
          filter((e) => e !== undefined)
        )
        .subscribe((clients: Client[]) => {
          let serId = 0;
          clients?.filter((client) => {
            client?.divisions?.filter((div) => {
              const department = div?.departments?.map((value) => value)[0];
              serId = department?.id;
            });
          });

          if (serId > 0) {
            this.store.dispatch(getAllProfileByServiceId({ serviceId: serId }));
        } else {
            this.profiles$.next([]);
        } 

        });

    if(UserUtil.isAdmin()){
      this.store
        .select(selectDivServiceWrappers)
        .pipe(
          takeUntil(this.destroy$),
          filter((e) => e != undefined)
        )
        .subscribe((divService) => {
          this.divServiceWrapper$.next(divService);
        });
      } 
      else {
        this.store.select(selectDivServiceUserWrappers)
        .pipe(
          takeUntil(this.destroy$),
          filter((e) => e != undefined))
        .subscribe(divService => {
          this.divServiceWrapper$.next(divService);
        });
      }

    }
 //   if (UserUtil.isAdmin() || !UserUtil.isAdmin()) {
      this._activateRoute.queryParams
        .pipe(
          take(1),
          pluck('mode'),
          filter((e) => e === UserFormUpdateMode.UPDATE_SINGLE)
        )
        .subscribe(() =>
          this.store.dispatch(loadProfileList({ page: 0, pageSize: 0 }))
        );

      // Load list of profiles.
      this.store
      .select(selectAllProfile)
      .subscribe((profiles: ProfileAssigned[]) => {
        const profileMapping = profiles.map((p) => {
          const item: InputSelectionCriteria = {
            key: p.id,
            value: p.name,
          };
          return item;
        });

        this.profiles$.next(profileMapping);
      });
  }
  clientChangeEvent(clientId: any) {
    if (UserUtil.isAdmin()) {
      this.store.dispatch(loadClientService({ clientId: clientId }));
    } else {
      const userId = UserUtil.getOwnerId();
      this.store.dispatch(loadClientServiceInUser({ userId: userId })); 
    }
  }

  changeServiceId(serviceId: any) {
    // this.store.dispatch(getAllProfileByServiceId({ serviceId }));
  }
}
