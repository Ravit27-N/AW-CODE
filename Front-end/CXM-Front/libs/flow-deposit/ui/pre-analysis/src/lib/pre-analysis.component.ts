import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { BehaviorSubject, Observable, of, Subject, Subscription } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  analyseFlow,
  AnalyseFlowModel,
  chooseChannel,
  DepositedFlowModel,
  enqueRoute, FlowDepositControlService,
  FlowTabs,
  goBack,
  goBackAcquisition,
  goBackToAnalysisResult,
  linkTab,
  selectAnalyzeResponseState,
  selectChooseChannelState,
  selectDocumentIdentifiable,
  selectIsNavigateFromFlowTraceability,
  selectProcessControlRequestState,
  selectProcessControlResponseState,
  stepOnActivated,
  stepOnFlowDeposit,
  stepOnFlowDepositComplete,
  validateIsCanIdentify
} from '@cxm-smartflow/flow-deposit/data-access';
import { DepositNavControlComponent } from '@cxm-smartflow/flow-deposit/ui/deposit-navigator';
import { filter, takeUntil } from 'rxjs/operators';
import { ILockableForm, LockableFormGuardService } from '@cxm-smartflow/flow-deposit/guard/pending-change';

@Component({
  selector: 'cxm-smartflow-pre-analysis',
  templateUrl: './pre-analysis.component.html',
  styleUrls: ['./pre-analysis.component.scss']
})
export class PreAnalysisComponent implements OnInit, OnDestroy, ILockableForm {
  selectType = new BehaviorSubject<string>('');
  channelType: string [] = [];
  indexSelected: number;
  processCtrlResState: any;
  processCtrlReqState: DepositedFlowModel;
  subscriptions: Subscription[] = [];
  selectedChannel = '';
  allowNext = false;
  lockedReasonMessage: [];
  isLock = false;

  isCanIdentifyDocument$ = new BehaviorSubject<boolean>(false);
  destroy$ = new Subject<boolean>();
  isNavigateFromFlowTraceability = new BehaviorSubject(false);

  @ViewChild(DepositNavControlComponent, { static: true }) navControl: DepositNavControlComponent;

  constructor(private translateService: TranslateService,
              private router: Router,
              private store: Store,
              private readonly flowDepositControlService: FlowDepositControlService,
              private lockableFormGuardService: LockableFormGuardService) {

    // Set translate message for locked pre-analysis form.
    Promise.all([
      this.translateService.get('flow.deposit.button.back_warning_title').toPromise(),
      this.translateService.get('flow.deposit.button.back_warning_message').toPromise(),
      this.translateService.get('flow.deposit.button.back_warning_okButton').toPromise(),
      this.translateService.get('flow.deposit.button.back_warning_cancelButton').toPromise()
    ]).then(lockedReasonMessage => Object.assign(this, { lockedReasonMessage }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.store.dispatch(stepOnActivated({ active: false }));
    this.store.dispatch(stepOnFlowDepositComplete());
  }

  ngOnInit() {
    this.store.dispatch(enqueRoute(this.router, FlowTabs.preAnalysis));
    this.store.dispatch(stepOnActivated({ active: true }));
    this.store.dispatch(stepOnFlowDeposit({ step: 2 }));
    this.subscriptions.push(this.store.select(selectProcessControlRequestState).subscribe(res => {
      if (res) {
        this.processCtrlReqState = res;
        this.isLock = Object.keys(res).length > 0;
      }
    }));

    this.subscriptions.push(this.store.select(selectProcessControlResponseState).subscribe(res => {
      if (res) {
        this.processCtrlResState = res;

        // if(!res.data.ModeleName?.trim()) {
        //   // unidentified model
        //   this.allowNext = false;
        // } else {
        //   this.allowNext = true;
        // }
        this.allowNext = true;
      }
    }));

    this.subscriptions.push(this.store.select(selectChooseChannelState).subscribe(res => {
      if (res) {
        this.selectedChannel = res;
        this.navControl.canNext=true && this.allowNext;
        this.selectType.next(res);
      }
    }));

    this.initializeChannelType();

    this.navControl.canPrev = true;
    this.checkIsCanIdentifyDocument();
    this.store.select(selectIsNavigateFromFlowTraceability).subscribe(v => this.isNavigateFromFlowTraceability.next(v));
  }

  checkIsCanIdentifyDocument(): void {
    this.store.select(selectDocumentIdentifiable).pipe(
      takeUntil(this.destroy$),
      filter(e => e !== undefined)
    ).subscribe(isDocumentIdentifiable => {
      this.isCanIdentifyDocument$.next(isDocumentIdentifiable);
      if(isDocumentIdentifiable) {
        this.flowDepositControlService.updateFlowToFinalize();
        this.store.dispatch(validateIsCanIdentify({ isCanIdentify: this.isCanIdentifyDocument$.value }));
      }
    })
    /*
    this.store.select(selectProcessControlResponseState)
      .pipe(takeUntil(this.destroy$))
      .subscribe(v => {
        this.isCanIdentifyDocument$.next(v?.data?.ModeleName?.trim() !== '');
        if (this.isCanIdentifyDocument$.value) {
          this.lockableFormGuardService.updateToFinalizeStatus();
          this.store.dispatch(validateIsCanIdentify({isCanIdentify: this.isCanIdentifyDocument$.value}));
        }
      });
    */
  }

  initializeChannelType(): void {
    this.channelType = [
      'flow.deposit.preAnalysis.digitalType.item1',
      'flow.deposit.preAnalysis.digitalType.item2',
      'flow.deposit.preAnalysis.digitalType.item3',
      'flow.deposit.preAnalysis.digitalType.item4'
    ];
  }

  onSelectChannelType(index: number): void {
    if (index === this.indexSelected) {
      this.indexSelected = -1;
    } else {
      this.indexSelected = index;
    }
    this.validateNextButton(this.selectType.value, this.indexSelected);
  }

  onCheckbookChangeEvent(channel: string): void {
    this.selectedChannel = channel;
    this.store.dispatch(chooseChannel({ channel: channel }));
    this.selectType.next(channel);
    this.validateNextButton(this.selectType.value, this.indexSelected);
    if (channel.includes('digital')) return;
    this.goNext();
  }

  validateNextButton(type: string, indexSelect: number): void{
    if(type === 'digital' && indexSelect >= 0){
        this.navControl.canNext=true && this.allowNext;
    }
    else if(type === 'Postal'){
      this.navControl.canNext=true && this.allowNext;
    }else{
      this.navControl.canNext=false;
    }
  }

  goNext() {
    if (!this.isCanIdentifyDocument$.value) return;
    this.store.dispatch(linkTab(FlowTabs.preAnalysis));
    this.subscriptions.push(this.store.select(selectAnalyzeResponseState).subscribe(state => {
      if (Object?.keys(state)?.length == 0) {
        this.store.dispatch(analyseFlow({ request: this.mapDataAnalyzeRequest() }));
      } else {

        // Navigate to step 3.
        this.store.dispatch(goBackToAnalysisResult());
      }
    }));
  }

  goPrev() {
    if(this.isNavigateFromFlowTraceability.value){
      this.store.dispatch(goBackAcquisition());
    }else{
      this.store.dispatch(goBack());
    }
  }

  mapDataAnalyzeRequest(): AnalyseFlowModel {
    return {
      channel: this.selectType.value,
      flowType: this.processCtrlReqState.flowType,
      fileId: this.processCtrlReqState.fileId,
      uuid: this.processCtrlReqState.uuid,
      idCreator: this.processCtrlReqState.idCreator,
      modelName: this.processCtrlResState.data?.ModeleName
    };
  }


  isLocked(): Observable<boolean> {
    return of(this.isLock);
  }

  getLockedReason(): string[] {
    return this.lockedReasonMessage;
  }
}
