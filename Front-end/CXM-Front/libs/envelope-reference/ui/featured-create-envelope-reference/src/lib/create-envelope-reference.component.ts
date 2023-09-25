import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  loadClientCriteria,
  loadClientService,
  selectClientCriteria,
} from '@cxm-smartflow/user/data-access';
import { BehaviorSubject, Subject } from 'rxjs';
import { InputSelectionCriteria } from '@cxm-smartflow/shared/ui/form-input-selection';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import {
  EnvelopeReferenceFormControlService
} from "@cxm-smartflow/envelope-reference/data-access";

@Component({
  selector: 'cxm-smartflow-create-envelope-reference',
  templateUrl: './create-envelope-reference.component.html',
  styleUrls: ['./create-envelope-reference.component.scss'],
})
export class CreateEnvelopeReferenceComponent implements OnInit, OnDestroy {
  isAdmin$ = new BehaviorSubject(false);
  destroy$ = new Subject<boolean>();

  ngOnInit(): void {
    this.isAdmin$.next(UserUtil.isAdmin());
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
  }

  constructor(
    private store: Store,
    private envelopeReferenceFormControlService: EnvelopeReferenceFormControlService
  ) {}


  cancelForm() {
    this.envelopeReferenceFormControlService.navigateToList();
  }

}
