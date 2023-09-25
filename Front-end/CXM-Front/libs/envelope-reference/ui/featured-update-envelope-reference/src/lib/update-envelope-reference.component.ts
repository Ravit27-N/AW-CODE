import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Subject } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { Location } from '@angular/common';
import { MatPasswordStrengthComponent } from '@angular-material-extensions/password-strength';
import {EnvelopeReferenceFormUpdateMode} from "@cxm-smartflow/envelope-reference/util";
import {
  EnvelopeReferenceFormControlService
} from "@cxm-smartflow/envelope-reference/data-access";
import {EnvelopeReferenceService} from "@cxm-smartflow/envelope-reference/data-access";
import {UpdateEnvelopeReference} from "@cxm-smartflow/envelope-reference/data-access";
import {closeSelectionPanel} from "@cxm-smartflow/envelope-reference/data-access";

@Component({
  selector: 'cxm-smartflow-update-envelope-reference',
  templateUrl: './update-envelope-reference.component.html',
  styleUrls: ['./update-envelope-reference.component.scss'],
  providers: [Location],
})
export class UpdateEnvelopeReferenceComponent implements OnInit, OnDestroy {
  @ViewChild('passwordComponentWithConfirmation', { static: false })
  passwordComponentWithConfirmation: MatPasswordStrengthComponent = new MatPasswordStrengthComponent();
  isAdmin$ = new BehaviorSubject(false);
  updateEnvelopeReference: UpdateEnvelopeReference;
  envelopeReferenceFormUpdateMode = EnvelopeReferenceFormUpdateMode;
  mode = this.envelopeReferenceFormControlService.envelopeReferenceFormUpdateMode();
  destroy$ = new Subject<boolean>();
  ids: string[]

  ngOnInit(): void {
    this.initialFormData();
    this.isAdmin$.next(UserUtil.isAdmin());
    this.isAdmin$.next(UserUtil.isAdmin());
    this.initialFormData();
    this.isAdmin$.next(UserUtil.isAdmin());
    this.initialFormData();
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }
  constructor(
    private store: Store,
    private _activateRoute: ActivatedRoute,
    private envelopeReferenceService: EnvelopeReferenceService,
    public envelopeReferenceFormControlService: EnvelopeReferenceFormControlService
  ) {}


  cancel(): void {
    this.envelopeReferenceFormControlService.navigateToList();
    this.store.dispatch(closeSelectionPanel());
  }

  public async delete() {
    await this.envelopeReferenceFormControlService.deleteEnvelopeReference();
  }

  private async initialFormData() {
    if (
      (await this.envelopeReferenceFormControlService.envelopeReferenceFormUpdateMode()) ===
      EnvelopeReferenceFormUpdateMode.UPDATE_SINGLE
    ) {
      const id = await this.envelopeReferenceFormControlService.getEnvelopeReferenceId();
      this.envelopeReferenceService.findEnvelopeReferenceById(id).subscribe((updateEnvelopeReference: any) => {
        this.updateEnvelopeReference = updateEnvelopeReference;
      });
    }
    if (
      (await this.envelopeReferenceFormControlService.envelopeReferenceFormUpdateMode()) ===
      EnvelopeReferenceFormUpdateMode.UPDATE_MULTIPLE
    ) {
      const toSendIds = await this.envelopeReferenceFormControlService.getEnvelopeReferenceId();
      this.ids = toSendIds
    }
  }
}
