import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {
  navigateToStep,
  selectFlowDepositStep,
  selectFileUploadState, showStepSeq,
  unloadUploadFileAction,
  selectDepositLocked,
  clearDepositFlow,
  selectDepositLoading
} from '@cxm-smartflow/flow-deposit/data-access';
import { Store } from '@ngrx/store';
import { ILockableForm} from  '@cxm-smartflow/flow-deposit/guard/pending-change';
import { Observable, of, ReplaySubject, Subscription } from 'rxjs';
import {IStep, StepperComponent} from "@cxm-smartflow/shared/ui/stepper";
import { TranslateService } from '@ngx-translate/core';
import {NavigationEnd, Router} from "@angular/router";
import { FlowCriteriaSessionService } from '@cxm-smartflow/flow-traceability/data-access';

@Component({
  selector: 'cxm-smartflow-flow-deposit',
  templateUrl: './flow-deposit.component.html',
  styleUrls: ['./flow-deposit.component.scss']
})
export class FlowDepositComponent implements OnInit, OnDestroy, ILockableForm {
  fileUploadState: any;
  fileUploadStateSubscription: Subscription;

  isLoading$ = new ReplaySubject<boolean>(1);
  loadingUnsubscribe: Subscription;


  steps$: Observable<any>;
  show$: Observable<boolean>;

  @ViewChild("stepper") stepper: StepperComponent;
  unsubscription$: any;
  lockedReasonMessage: [];


  ngOnDestroy(): void {
    this.store.dispatch(unloadUploadFileAction());
    this.store.dispatch(clearDepositFlow());
    if(this.fileUploadStateSubscription) {
      this.fileUploadStateSubscription.unsubscribe();
    }

    this.loadingUnsubscribe.unsubscribe();
    this.storageService.clearFlowCriteria();
  }

  isLocked(): Observable<boolean> {
    return this.store.select(selectDepositLocked);
  }
  getLockedReason(): string[] {
    return this.lockedReasonMessage;
  }

  constructor(private store: Store, private router: Router, private translate: TranslateService, private storageService: FlowCriteriaSessionService) {
    this.steps$ = this.store.select(selectFlowDepositStep);
    this.unsubscription$ = this.show$ = this.store.select(showStepSeq);

    Promise.all([
      this.translate.get('flow.deposit.button.back_warning_title').toPromise(),
      this.translate.get('flow.deposit.button.back_warning_message').toPromise(),
      this.translate.get('flow.deposit.button.back_warning_okButton').toPromise(),
      this.translate.get('flow.deposit.button.back_warning_cancelButton').toPromise()
    ]).then(lockedReasonMessage => Object.assign(this, { lockedReasonMessage }));
  }

  // @HostListener('window:beforeunload')
  // canDeactivate(): Observable<boolean> | boolean {
  //   return !this.fileUploadState.response;
  // }

  navigateTo(step: IStep) {
    this.store.dispatch(navigateToStep({ step }));
  }

  ngOnInit(): void {
    this.fileUploadStateSubscription =
      this.store.select(selectFileUploadState).subscribe(value => {
        this.fileUploadState = value;
      });

      this.loadingUnsubscribe = this.store.select(selectDepositLoading).subscribe(value => this.isLoading$.next(value));
  }

  get isFinished(): boolean {
    return location.pathname.includes('/cxm-deposit/validate-result');
  }

  // Not display, when it in list page.
  get isNotDisplay(): Observable<boolean>{
    return of(!location.pathname.includes('/cxm-deposit/list/postal'));
  }

  onActivate():void {
    this.router.events.subscribe(x => {
      if(x instanceof NavigationEnd)
      {
        window.scroll({
          top: 0,
          left: 0
        });
      }
    });
  }

}
