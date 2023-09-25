import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  enqueRoute, fetchWatermark, FlowDepositControlService,
  FlowTabs,
  goBackProductCriterial,
  selectAnalyzeResponseState,
  selectComposedFileIdState,
  selectFilenameAcquisitionFileUpload,
  selectIsNavigateFromFlowTraceability,
  selectOKDocumentProcessed,
  selectProductionForm,
  selectSummaryDocument,
  selectUuid,
  selectUuidProcessControlRequest,
  stepOnActivated,
  stepOnFlowDeposit,
  stepOnFlowDepositComplete,
  switchFlow,
  SwitchFlowModel
} from '@cxm-smartflow/flow-deposit/data-access';
import { ILockableForm, LockableFormGuardService } from '@cxm-smartflow/flow-deposit/guard/pending-change';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { take } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-finished',
  templateUrl: './finished.component.html',
  styleUrls: ['./finished.component.scss']
})
export class FinishedComponent implements OnInit, OnDestroy, ILockableForm {
  propertiesLabel: any;
  filename = new BehaviorSubject('');
  switchFlowModel: SwitchFlowModel;

  color = new BehaviorSubject('');
  recto = new BehaviorSubject('');
  urgency = new BehaviorSubject('');
  wrap = new BehaviorSubject('');

  // Properties setting form.
  composedFileId = new BehaviorSubject('');
  uuid = new BehaviorSubject('');
  selectOKDocumentProcessed = { nbDocument: 0, nbPage: 0 };
  calculatedSheet = new BehaviorSubject(0);
  summeryDoc$: Observable<any>;


  composedFileIdFromUrl = new BehaviorSubject('');
  stepFromUrl = new BehaviorSubject('');
  fileIdFromUrl = new BehaviorSubject('');
  uuidProcessControlRequest = new BehaviorSubject('');

  isNavigateFromFlowTraceability = new BehaviorSubject(false);

  // Lock component properties.
  lockedReasonMessage: [];
  isLock = false;

  ngOnInit() {
    this.store.dispatch(enqueRoute(this.router, FlowTabs.fin));
    this.store.dispatch(stepOnActivated({ active: true }));
    this.store.dispatch(stepOnFlowDeposit({ step: 5 }));

    this.store.select(selectOKDocumentProcessed).subscribe((v: any) => {
      this.selectOKDocumentProcessed = v;
    });

    this.store.select(selectProductionForm).subscribe((v: any) => {
      if(v){
        this.color.next(v.Color);
        this.urgency.next(v.Urgency);
        this.recto.next(v.Recto);
        this.wrap.next(v?.Wrap);

        this.store.select(selectAnalyzeResponseState).subscribe((v) => {

          this.isLock = Object.keys(v).length > 0;

          let nbSheets = 0;
          if (v?.data) {
            const doc = v?.data?.document?.DOCUMENT;
            const documentOk = doc?.filter((value) => {
              if (value?.Analyse) {
                return value?.Analyse === 'OK';
              }

              if (value?.Analysis) {
                return value?.Analysis === 'OK';
              }
              return value;
            });

            if (this.recto.value == 'RV') {
              documentOk?.forEach(data => {
                nbSheets += Math.ceil(parseFloat(data?.NbPages) / 2);
              });
            }
            this.calculatedSheet.next(nbSheets == 0 ? this.selectOKDocumentProcessed.nbPage : nbSheets);
          }
        });
      }
    });

    this.summeryDoc$ = this.store.select(selectSummaryDocument);
    this.store.select(selectComposedFileIdState).subscribe(v => this.composedFileId.next(v));
    this.store.select(selectUuid).subscribe(v => this.uuid.next(v));
    this.store.select(selectUuidProcessControlRequest).subscribe(uuid => this.uuidProcessControlRequest.next(uuid));
    this.store.select(selectIsNavigateFromFlowTraceability).subscribe(v => this.isNavigateFromFlowTraceability.next(v));
    this.store.select(selectFilenameAcquisitionFileUpload).subscribe(v => this.filename.next(v));
    this.flowDepositControlService.updateFlowToFinalize();
  }

  goBackToDeposit() {
    if (this.composedFileIdFromUrl.value !== undefined &&
      this.stepFromUrl.value !== undefined &&
      this.fileIdFromUrl !== undefined) {
      // leave and back form flow traceability (step 5).
      this.activatedRoute.queryParams.pipe(take(1)).subscribe(data => {
        this.store.dispatch(switchFlow({
          request:
            {
              composedFileId: this.composedFileIdFromUrl.value,
              uuid: this.uuidProcessControlRequest.value,
              validation: data?.validation
            }
        }));
      });
    } else {
      // leave and back from flow flow traceability(Step 2, 3, 4).
      this.activatedRoute.queryParams.pipe(take(1)).subscribe(data => {
        this.store.dispatch(switchFlow({
          request:
            {
              composedFileId: this.composedFileId.value,
              uuid: this.uuid.value,
              validation: data?.validation
            }
        }));
      });
    }
  }

  toNumber(value: string) {
    return +value;
  }

  goPrev() {
    this.store.dispatch(goBackProductCriterial());
  }


  isLocked(): Observable<boolean> {
    return of(this.isLock);
  }
  getLockedReason(): string[] {
    return this.lockedReasonMessage;
  }


  constructor(private router: Router,
              private store: Store,
              private translateService: TranslateService,
              private activatedRoute: ActivatedRoute,
              private readonly flowDepositControlService: FlowDepositControlService,
              private lockableFormGuardService: LockableFormGuardService) {

    // Set lock component messages.
    Promise.all([
      this.translateService.get('flow.deposit.button.back_warning_title').toPromise(),
      this.translateService.get('flow.deposit.button.back_warning_message').toPromise(),
      this.translateService.get('flow.deposit.button.back_warning_okButton').toPromise(),
      this.translateService.get('flow.deposit.button.back_warning_cancelButton').toPromise()
    ]).then(lockedReasonMessage => Object.assign(this, { lockedReasonMessage }));

    this.activatedRoute.queryParams.subscribe(params => {
      const param = { step: params['step'], fileId: params['fileId'], composedFileId: params['composedFileId'] };
      this.composedFileIdFromUrl.next(param?.composedFileId);
      this.stepFromUrl.next(param?.step);
      this.fileIdFromUrl.next(param?.fileId);
    });

    this.translateService
      .get('flow.deposit.message')
      .subscribe((v) => (this.propertiesLabel = v));
  }

  ngOnDestroy(): void {
    this.filename.unsubscribe();
    this.store.dispatch(stepOnActivated({ active: false }));
    this.store.dispatch(stepOnFlowDepositComplete());
  }

  displayColor(value: string) {
    switch (value) {
      case '0':
        return 'Noir & Blanc';
      case '1':
        return 'Couleur';
      default:
        return '';
    }
  }

  displayRecto(value: string) {
    switch (value) {
      case 'R':
        return 'Recto uniquement';
      case 'RV':
        return 'Recto & verso';
      default:
        return '';
    }
  }
}
