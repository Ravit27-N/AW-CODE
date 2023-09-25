import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  Client,
  CreateUserRequestModel,
  getAllProfileByServiceId,
  loadClientCriteria,
  loadClientService,
  ProfileAssigned,
  selectAllProfile,
  selectClientCriteria,
  selectClients,
  selectDivServiceWrappers,
  submitCreateUserFrom,
  unloadUserForm,
  UserFormControlService,
} from '@cxm-smartflow/user/data-access';
import { BehaviorSubject, Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { InputSelectionCriteria } from '@cxm-smartflow/shared/ui/form-input-selection';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { MatPasswordStrengthComponent } from '@angular-material-extensions/password-strength';
import { TitleCaseUtil } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss'],
})
export class CreateUserComponent implements OnInit, OnDestroy {
  @ViewChild('passwordComponentWithConfirmation', { static: false })
  passwordComponentWithConfirmation: MatPasswordStrengthComponent = new MatPasswordStrengthComponent();
  // Prefill form.
  clientWrapper$ = new BehaviorSubject<InputSelectionCriteria[]>([]);
  divServiceWrapper$ = new BehaviorSubject<InputSelectionCriteria[]>([]);
  profiles$ = new BehaviorSubject<InputSelectionCriteria[]>([]);
  isAdmin$ = new BehaviorSubject(false);

  // Validation properties.
  destroy$ = new Subject<boolean>();

  ngOnInit(): void {
    this.isAdmin$.next(UserUtil.isAdmin());

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
        this.clientWrapper$.next(mappingClients || []);
      });
    }

    if (!UserUtil.isAdmin()) {
      this.store.dispatch(loadClientService({ clientId: undefined }));
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

        this.userFormControlService.getUserId().then(id => {
          if (serId > 0) {
              this.store.dispatch(getAllProfileByServiceId({ serviceId: serId }));
          } else {
              this.profiles$.next([]);
          }
      });
      });

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

  ngOnDestroy(): void {
    this.store.dispatch(unloadUserForm({}));
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  constructor(
    private store: Store,
    private userFormControlService: UserFormControlService
  ) {}

  create(createUserModel: CreateUserRequestModel) {
    const { firstName, lastName } = createUserModel;
    createUserModel = {
      ...createUserModel,
      firstName: TitleCaseUtil.convert(firstName),
      lastName: lastName.toUpperCase(),
    };
    this.store.dispatch(submitCreateUserFrom({ createUserModel }));
  }

  cancelForm() {
    this.userFormControlService.navigateToList();
  }

  clientChangeEvent(clientId: any) {
    this.store.dispatch(loadClientService({ clientId: clientId }));
  }

  changeServiceId(serviceId: any) {
    // this.store.dispatch(getAllProfileByServiceId({ serviceId }));
  }
}
