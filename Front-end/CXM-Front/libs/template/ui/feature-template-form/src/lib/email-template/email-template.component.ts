import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  loadTemplate,
  selectFormHasChanged,
  selectShowSpinner,
  unloadSmsTemplate,
} from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ILockableForm } from '../LockableFormGuard.service';

@Component({
  selector: 'cxm-smartflow-email-template',
  templateUrl: './email-template.component.html',
  styleUrls: ['./email-template.component.scss'],
})
export class EmailTemplateComponent
  implements OnInit, OnDestroy, ILockableForm {
  lockedReasonMessage: string[];
  isLoading$ = new BehaviorSubject(false);

  ngOnInit(): void {

    const {
      modelName,
      mode,
      sourceTemplateId,
    } = this.activatedRoute.snapshot.queryParams;
    const { id } = this.activatedRoute.snapshot.params;
    this.store.dispatch(
      loadTemplate({
        modelName,
        mode,
        id,
        templateType: 'EMAILING',
        sourceTemplateId,
      })
    );

    Promise.all([
      this.translate
        .get('template.sms.sms_template_back_warning_title')
        .toPromise(),
      this.translate
        .get('template.sms.sms_template_back_warning_message')
        .toPromise(),
      this.translate
        .get('template.sms.sms_template_back_warning_okButton')
        .toPromise(),
      this.translate
        .get('template.sms.sms_template_back_warning_cancelButton')
        .toPromise(),
    ]).then((lockedReasonMessage) =>
      Object.assign(this, { lockedReasonMessage })
    );

    this.store
      .select(selectShowSpinner)
      .subscribe((v) => this.isLoading$.next(v));
  }

  isLocked(): Observable<boolean> {
    return this.store.select(selectFormHasChanged);
  }

  getLockedReason(): string[] {
    return this.lockedReasonMessage;
  }

  ngOnDestroy(): void {
    this.store.dispatch(unloadSmsTemplate());
  }

  constructor(
    private translate: TranslateService,
    private store: Store,
    private activatedRoute: ActivatedRoute
  ) {
    this.store.select(selectShowSpinner).subscribe((v) => {
      this.isLoading$.next(v);
    });
  }
}
