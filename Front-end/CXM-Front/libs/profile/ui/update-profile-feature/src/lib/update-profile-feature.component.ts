import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  attemptToDeleteProfile,
  enqueRoute,
  IDeactivateComponent,
  initFormChange,
  loadClientModule,
  loadProfileForm,
  ProfileTabs,
  selectFormChange,
  selectUserCreatedBy, selectUserOwnerId,
  unloadClientModule,
  unloadProfileForm,
} from '@cxm-smartflow/profile/data-access';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';
import { filter, takeUntil } from 'rxjs/operators';
import {UserFormControlService} from "@cxm-smartflow/user/data-access";
import {CanModificationService, CanVisibilityService} from "@cxm-smartflow/shared/data-access/services";
import {UserManagement} from "@cxm-smartflow/shared/data-access/model";

@Component({
  selector: 'cxm-smartflow-update-profile-feature',
  templateUrl: './update-profile-feature.component.html',
  styleUrls: ['./update-profile-feature.component.scss'],
})
export class UpdateProfileFeatureComponent
  implements OnInit, OnDestroy, IDeactivateComponent
{
  canDeleteProfile$ = new BehaviorSubject<boolean>(false);
  profileId: number;
  label: any;
  formChange$ = new BehaviorSubject(false);
  destroy$ = new Subject<boolean>();

  ngOnInit(): void {
    this.translate
      .get('profile.form.confirmation')
      .subscribe((json) => (this.label = json));

    const { id } = this.activatedRouter.snapshot.params;
    const clientId = this.activatedRouter.snapshot.queryParamMap.get('clientId');
    this.profileId = id || 0;

    this.store.dispatch(enqueRoute(this.router, ProfileTabs.update));
    this.store.dispatch(loadClientModule({ profileId: id, clientId}));
    this.store.dispatch(loadProfileForm({ id }));
  }

  ngOnDestroy(): void {
    this.store.dispatch(unloadProfileForm());
    this.store.dispatch(unloadClientModule());
    this.formChange$.complete();
    this.store.complete();
    this.destroy$.next(true);
  }

  constructor(
    private store: Store,
    private router: Router,
    private activatedRouter: ActivatedRoute,
    private confirmationService: ConfirmationMessageService,
    private translate: TranslateService,
    private userFormControl: UserFormControlService,
    private canModification: CanModificationService
  ) {
    this.checkPrivilege();
    this.store
      .select(selectFormChange)
      .subscribe((value) => this.formChange$.next(value));
  }

  private checkPrivilege(): void {
    this.store
      .select(selectUserOwnerId)
      .pipe(
        takeUntil(this.destroy$),
        filter((e) => e !== undefined)
      )
      .subscribe((ownerId: number) => {
        this.canDeleteProfile$.next(
          this.canModification.hasModify(UserManagement.CXM_USER_MANAGEMENT, UserManagement.DELETE_PROFILE, ownerId, true)
        );
      });
  }

  onDeleteProfile(): void {
    if (this.canDeleteProfile$.value) {
      this.store.dispatch(initFormChange({ formChange: false })); // Clear form change.
      this.store.dispatch(
        attemptToDeleteProfile({
          profileId: this.profileId.toString(),
          name: '',
        })
      );
    }
  }

  canExit(): Observable<boolean> {
    if (this.formChange$.value) {
      return this.confirmationService.showConfirmationPopup({
        icon: 'close',
        title: this.label?.title,
        message: this.label?.message,
        cancelButton: this.label?.cancelButton,
        confirmButton: this.label?.confirmButton,
        type: 'Warning',
      });
    } else {
      return of(true);
    }
  }
}
