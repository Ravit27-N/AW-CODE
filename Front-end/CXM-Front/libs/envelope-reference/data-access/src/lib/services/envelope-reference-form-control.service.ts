import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { pluck, take } from 'rxjs/operators';

import { TranslateService } from '@ngx-translate/core';
import { appLocalStorageConstant, appRoute, EnvelopeReferenceManagement } from '@cxm-smartflow/shared/data-access/model';
import { AuthService } from '@cxm-smartflow/auth/data-access';
import {
  CanAccessibilityService,
  SnackBarService
} from '@cxm-smartflow/shared/data-access/services';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Store } from '@ngrx/store';
import {EnvelopeReferenceService} from "./envelope-reference.service";
import {deleteEnvelopeReference} from "@cxm-smartflow/envelope-reference/data-access";
import {
  EnvelopeReferenceFormUpdateMode,
  EnvelopeReferenceFormUpdateModel
} from "@cxm-smartflow/envelope-reference/util";

@Injectable({
  providedIn: 'any'
})
export class EnvelopeReferenceFormControlService {

  // Validation envelope reference's privileges properties.
  public isCanList = this.canAccessService.getUserRight(EnvelopeReferenceManagement.CXM_REFERENCE_ENVELOPE_MANAGEMENT, EnvelopeReferenceManagement.LIST, true, true);
  public isCanEdit = this.canAccessService.getUserRight(EnvelopeReferenceManagement.CXM_REFERENCE_ENVELOPE_MANAGEMENT, EnvelopeReferenceManagement.EDIT, true, true);
  public isCanModify = this.canAccessService.getUserRight(EnvelopeReferenceManagement.CXM_REFERENCE_ENVELOPE_MANAGEMENT, EnvelopeReferenceManagement.EDIT, true, true);
  public isCanCreate = this.canAccessService.getUserRight(EnvelopeReferenceManagement.CXM_REFERENCE_ENVELOPE_MANAGEMENT, EnvelopeReferenceManagement.CREATE, true, true);
  public isCanDelete = this.canAccessService.getUserRight(EnvelopeReferenceManagement.CXM_REFERENCE_ENVELOPE_MANAGEMENT, EnvelopeReferenceManagement.DELETE, true, true);

  constructor(private readonly activatedRoute: ActivatedRoute,
              private readonly translate: TranslateService,
              private readonly router: Router,
              private readonly envelopeReferenceService: EnvelopeReferenceService,
              private readonly authService: AuthService,
              private readonly snackbar: SnackBarService,
              private readonly store: Store,
              private readonly confirmService: ConfirmationMessageService,
              private readonly canAccessService: CanAccessibilityService) {
    this.translate.use(localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) || appLocalStorageConstant.Common.Locale.Fr);
  }
  envelopeReferenceFormUpdateMode(): EnvelopeReferenceFormUpdateModel {
    const mode = this.activatedRoute.snapshot.queryParams.mode;

    if (mode === EnvelopeReferenceFormUpdateMode.UPDATE_MULTIPLE) {
      return EnvelopeReferenceFormUpdateMode.UPDATE_MULTIPLE;
    } else if (mode === EnvelopeReferenceFormUpdateMode.UPDATE_SINGLE) {
      return EnvelopeReferenceFormUpdateMode.UPDATE_SINGLE;
    }

    return EnvelopeReferenceFormUpdateMode.CREATE;
  }

  public navigateToList(): void {
    this.router.navigateByUrl(appRoute.cxmEnvelopeReference.navigateToList);
  }

  public navigateToCreate(): void {
    this.router.navigateByUrl(appRoute.cxmEnvelopeReference.navigateToCreate);
  }


  public async getEnvelopeReferenceId() {
    return await this.activatedRoute.queryParams.pipe(take(1), pluck('id')).toPromise();
  }




  public async confirmDelete() {
    const messages = await Promise.all([
      this.translate.get('envelope_reference.delete.heading').toPromise(),
      this.translate.get('envelope_reference.delete.message').toPromise(),
      this.translate.get('envelope_reference.delete.cancelButton').toPromise(),
      this.translate.get('envelope_reference.delete.confirmButton').toPromise(),
      this.translate.get('envelope_reference.delete.action_undone').toPromise()
    ]);
    return await this.confirmService.showConfirmationPopup({
      type: 'Warning',
      icon: 'Warning',
      title: messages[0],
      message: messages[1],
      paragraph: messages[4],
      cancelButton: messages[2],
      confirmButton: messages[3]
    }).toPromise();
  }

  public async deleteEnvelopeReference() {
    const id = await this.getEnvelopeReferenceId()
    if (id && await this.confirmDelete()) {
      this.store.dispatch(deleteEnvelopeReference({ id }));
    }
  }


  public async updateEnvelopeReferencePrivilegeInStorage() {
    const rePrivilege = await this.authService.getUserPrivileges().toPromise();
    localStorage.setItem(appLocalStorageConstant.UserManagement.UserPrivilege, JSON.stringify(rePrivilege));
  }

}
