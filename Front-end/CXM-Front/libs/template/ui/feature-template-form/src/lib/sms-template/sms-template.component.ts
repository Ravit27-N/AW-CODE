import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import {
  loadTemplate,
  selectFormHasChanged, selectShowSpinner,
  unloadSmsTemplate
} from '@cxm-smartflow/template/data-access';
import { ActivatedRoute } from '@angular/router';
import { ILockableForm } from '../LockableFormGuard.service';
import { BehaviorSubject, Observable } from 'rxjs';

@Component({
  selector: 'cxm-smartflow-sms-template',
  templateUrl: './sms-template.component.html',
  styleUrls: ['./sms-template.component.scss'],
})
export class SmsTemplateComponent implements OnInit, OnDestroy, ILockableForm {
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
        templateType: 'SMS',
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

  constructor(
    private translate: TranslateService,
    private store: Store,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnDestroy(): void {
    this.store.dispatch(unloadSmsTemplate());
  }
}
