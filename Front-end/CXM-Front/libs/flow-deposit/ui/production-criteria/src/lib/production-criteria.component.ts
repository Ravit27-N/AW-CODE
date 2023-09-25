import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import {
  AnalyseFlowModel,
  AnalyseFlowResponse,
  clearAllStateInSettingOption, DefaultConfiguration,
  DepositedFlowModel,
  DepositFlowStateModel,
  enqueRoute,
  FlowDepositControlService,
  FlowTabs,
  goBack,
  goBackToAnalysisResult,
  linkTab,
  loadReco,
  Production, selectDefaultConfiguration,
  selectFlowDepositState,
  selectIsNavigateFromFlowTraceability,
  stepOnActivated,
  stepOnFlowDeposit,
  stepOnFlowDepositComplete,
  treatmentFlow,
  TreatmentFlowModel,
  TreatmentFlowResponse
} from '@cxm-smartflow/flow-deposit/data-access';
import { DepositNavControlComponent } from '@cxm-smartflow/flow-deposit/ui/deposit-navigator';
import { CriteriaFormComponent } from '@cxm-smartflow/flow-deposit/ui/product-criteria-form';
import { ILockableForm, LockableFormGuardService } from '@cxm-smartflow/flow-deposit/guard/pending-change';
import { Store } from '@ngrx/store';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-production-criteria',
  templateUrl: './production-criteria.component.html',
  styleUrls: ['./production-criteria.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductionCriteriaComponent implements OnInit, OnDestroy, ILockableForm {


  @ViewChild(DepositNavControlComponent, {static: true}) navControl: DepositNavControlComponent;
  @ViewChild(CriteriaFormComponent, {static: true}) criteriaForm: CriteriaFormComponent;

  isOption = false;
  isReadonly = false;
  subscriptions: Subscription[] = [];
  analysisResState: AnalyseFlowResponse;
  analysisReqState: AnalyseFlowModel;
  processCtrlReqState: DepositedFlowModel;
  treatmentResState: TreatmentFlowResponse;
  treatmentForm = new BehaviorSubject<any>({});
  isNavigateFromFlowTraceability = new BehaviorSubject(false);

  // Lock component properties.
  lockedReasonMessage: [];
  isLock = false;
  destroy$ = new BehaviorSubject<boolean>(false);
  defaultConfiguration: DefaultConfiguration;

  ngOnInit() {
    this.store.dispatch(enqueRoute(this.router, FlowTabs.productionCriteria));
    this.store.dispatch(stepOnActivated({active: true}));
    this.store.dispatch(stepOnFlowDeposit({step: 4}));

    this.subscriptions.push(
      this.store.select(selectFlowDepositState).subscribe((response: DepositFlowStateModel) => {

        this.isLock = Object.keys(response.analyzeResponse).length > 0;

        this.analysisReqState = response.analyzeRequest;
        this.analysisResState = response.analyzeResponse;
        this.processCtrlReqState = response.processControlRequest;
        this.treatmentResState = response.treatmentResponse;

        const treatmentInit = {
          composedFileId: response?.processControlResponse?.data?.composedFileId,
          idCreator: response?.processControlRequest?.idCreator,
          production: {
            Archiving: "0",
            Color: "0",
            Recto: "R",
            Urgency: "Letter"
          },
          uuid: response?.processControlRequest?.uuid
        }
        this.treatmentForm.next(treatmentInit);
      })
    );

    this.navControl.canNext = true;
    this.navControl.canPrev = true;

    this.store.select(selectIsNavigateFromFlowTraceability).subscribe(v => this.isNavigateFromFlowTraceability.next(v))
    this.flowDepositControlService.updateFlowToFinalize();

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.store.dispatch(stepOnActivated({active: false}));
    this.store.dispatch(stepOnFlowDepositComplete());
    this.destroy$.next(true);
    this.store.dispatch(clearAllStateInSettingOption());
  }

  goPrev() {
    if(this.isNavigateFromFlowTraceability.value){
      this.store.dispatch(goBackToAnalysisResult());
    }else{
      this.store.dispatch(goBack());
    }
  }

  goNext() {
    this.store.dispatch(linkTab(FlowTabs.productionCriteria));
    this.store.dispatch(treatmentFlow({request: this.initDataTreatmentReq()}));
    this.store.dispatch(loadReco({recto: this.criteriaForm.criteriaForm.value.recto || ''}))
  }

  openOption(option: boolean) {
    this.isOption = option;
  }

  initDataTreatmentReq(): TreatmentFlowModel {

    const criteriaFormValue = this.criteriaForm.criteriaForm.value;
    const production: Production = {
      Recto: criteriaFormValue?.recto,
      Archiving: '0',
      Color: criteriaFormValue?.color,
      Urgency: criteriaFormValue?.urgency,
      Wrap: criteriaFormValue?.wrap
    }

    const value = {
      uuid: this.processCtrlReqState.uuid,
      composedFileId: this.treatmentResState?.data?.composedFileId ? this.treatmentResState.data.composedFileId : this.analysisResState.data.composedFileId,
      idCreator: this.processCtrlReqState.idCreator,
      production: production
    }

     if (value){
       return value;
     }else {
       return this.treatmentForm.value;
     }
  }

  isLocked(): Observable<boolean> {
    return of(this.isLock);
  }
  getLockedReason(): string[] {
    return this.lockedReasonMessage;
  }

  constructor(private router: Router,
              private store: Store,
              private translate: TranslateService,
              private readonly flowDepositControlService: FlowDepositControlService,
              private lockableFormGuardService: LockableFormGuardService) {

    // Set lock component messages.
    Promise.all([
      this.translate.get('flow.deposit.button.back_warning_title').toPromise(),
      this.translate.get('flow.deposit.button.back_warning_message').toPromise(),
      this.translate.get('flow.deposit.button.back_warning_okButton').toPromise(),
      this.translate.get('flow.deposit.button.back_warning_cancelButton').toPromise()
    ]).then(lockedReasonMessage => Object.assign(this, { lockedReasonMessage }));

    // Subscribe default configuration from store.
    this.store.select(selectDefaultConfiguration).subscribe((configuration: DefaultConfiguration) => {
      this.defaultConfiguration = configuration;
    });
  }
}
