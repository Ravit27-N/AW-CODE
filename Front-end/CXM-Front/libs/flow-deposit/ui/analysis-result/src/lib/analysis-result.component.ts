import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {
  AnalyseFlowResponse,
  cancelFlowDeposit,
  deleteFlowDeposit,
  documentAnalyseResultMessage,
  enqueRoute,
  FlowDepositControlService,
  FlowTabs,
  goBack,
  goBackProductCriterial,
  goBackToPreAnalysis,
  isModelNameConfigurationChanged,
  linkTab, ModifiedFlowDocumentAddress,
  selectAnalyzeResponseState,
  selectDocumentIsKO,
  selectFlowDepositStep, selectIsConfigChanged, selectIsModelNameChanged,
  selectIsNavigateFromFlowTraceability,
  selectPreAnalysisState,
  selectProcessControlResponseState,
  selectSummaryDocument,
  selectUUID,
  stepOnActivated,
  stepOnFlowDeposit,
  stepOnFlowDepositComplete,
  unlockWhenNoDocumentValid
} from '@cxm-smartflow/flow-deposit/data-access';
import {DepositNavControlComponent} from '@cxm-smartflow/flow-deposit/ui/deposit-navigator';
import {Store} from '@ngrx/store';
import {BehaviorSubject, Observable, of, Subject, Subscription} from 'rxjs';
import {ILockableForm, LockableFormGuardService} from '@cxm-smartflow/flow-deposit/guard/pending-change';
import {filter, map, take, takeUntil, takeWhile} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {ConfirmationMessageService} from '@cxm-smartflow/shared/ui/comfirmation-message';
import {CanModificationService} from '@cxm-smartflow/shared/data-access/services';
import {FlowTraceability} from '@cxm-smartflow/shared/data-access/model';
import {StringUtil} from "@cxm-smartflow/shared/utils";
import {PdfViewerComponent} from "../../../pdf-viewer/src/lib/pdf-viewer.component";

export interface Document {
  noOk: false,
  hasKo: false
}

@Component({
  selector: 'cxm-smartflow-analysis-result',
  templateUrl: './analysis-result.component.html',
  styleUrls: ['./analysis-result.component.scss']
})
export class AnalysisResultComponent implements OnInit, OnDestroy, ILockableForm {
  isNavigateFromFlowTraceability = new BehaviorSubject(false);
  subscriptions: Subscription[] = [];
  document: Document;
  message: any;
  uuid: string;
  destroy$ = new Subject<boolean>();
  isShowContinuePopup = new BehaviorSubject(true);

  isLock = false;
  isFlowCancelable = false;
  ownerId = 0;

  filename$: Observable<string>;
  summeryDoc$: Observable<string>;
  isModelConfigChanged: boolean;

  // Lock component properties.
  lockedReasonMessage: [];

  @ViewChild(DepositNavControlComponent, { static: true }) navControl: DepositNavControlComponent;
  @ViewChild("pdfViewerComponent") pdfViewerComponent: PdfViewerComponent;

  ngOnInit(): void {
    this.translateService.get('flow.deposit').pipe(take(1)).subscribe(v => this.message = v);
    this.store.dispatch(enqueRoute(this.router, FlowTabs.analysisResult));
    this.store.dispatch(stepOnActivated({active: true}));
    this.store.dispatch(stepOnFlowDeposit({step: 3}));
    this.store.select(selectIsModelNameChanged).pipe(take(1), takeUntil(this.destroy$)).subscribe(data => {
      this.isModelConfigChanged = data;
      if (data && this.isShowContinuePopup.value) {
        this.showPopupConfirmModelChanged();
      }
    })

    this.store.select(selectIsConfigChanged).pipe(take(1), takeUntil(this.destroy$)).subscribe(data => {
      if (!this.isModelConfigChanged && data && this.isShowContinuePopup.value) {
        this.showPopupConfirmConfigurationChanged();
      }
    })

    this.navControl.canPrev = true;
    // this.navControl.canPrev = false; // TODO: get value from state.

    this.subscriptions.push(this.store.select(selectDocumentIsKO)
      .subscribe(res => {
        this.document = res;
      }));

    this.subscriptions.push(this.store.select(selectUUID)
      .subscribe(uuid => {
        this.uuid = uuid;
      }));

    this.filename$ = this.store.select(selectPreAnalysisState).pipe(filter(x => x && x.fileName)).pipe(takeUntil(this.destroy$))
      .pipe(map(v => `${v.fileName}.${v.extension}`));

    this.summeryDoc$ = this.store.select(selectSummaryDocument);


    this.subscriptions.push(this.store.select(selectAnalyzeResponseState).subscribe((res: AnalyseFlowResponse) => {

      // Validate lock component
      this.isLock = Object.keys(res).length > 0;

      if (Object.keys(res).length !== 0) {

        let atLeastOneOk = false;

        atLeastOneOk = res?.data?.document?.DOCUMENT?.some((value) => {
          if (value?.Analyse !== undefined) {
            return value?.Analyse?.toLocaleLowerCase() === 'ok';
          } else {
            return value?.Analysis?.toLocaleLowerCase() === 'ok';
          }
        });

        if (!atLeastOneOk) {
          const modifiedFlowDocumentAddress: ModifiedFlowDocumentAddress[] = res?.data?.modifiedFlowDocumentAddress || [];

          if (modifiedFlowDocumentAddress.length > 0) {
            const modify = modifiedFlowDocumentAddress.filter(value => value.modified == true);

            if (modify.length > 0) {
              atLeastOneOk = true;
            }
          }
        }

        this.navControl.canNext = atLeastOneOk;

        if (!atLeastOneOk) {
          this.store.dispatch(documentAnalyseResultMessage());
          this.store.dispatch(unlockWhenNoDocumentValid());
        }
      }
    }));

    this.subscriptions.push(this.store.select(selectIsNavigateFromFlowTraceability).subscribe(v => this.isNavigateFromFlowTraceability.next(v)));

    // Validate cancel flow privilege.
    this.activateRoute.queryParams.pipe(takeUntil(this.destroy$)).subscribe(v => {
      if (v?.ownerId) {
        this.ownerId = v?.ownerId;
        this.isFlowCancelable = this.canModify.hasModify(FlowTraceability.CXM_FLOW_TRACEABILITY, FlowTraceability.CANCEL_FLOW, JSON.parse(v?.ownerId));
      } else {
        // this.createdBy = JSON.parse(localStorage.getItem('userPrivileges') || '')?.name?.trim();
        // TODO can't use createdBy anymore what's todo
        this.isFlowCancelable = true;
      }
    });
    this.flowDepositControlService.updateFlowToFinalize();
  }


  goNext() {
    this.navigateToNextPage();
  }

  navigateToNextPage(): void {
    if (this.document.hasKo && this.isShowContinuePopup.value) {
      this.store.select(selectSummaryDocument).pipe(takeUntil(this.destroy$)).subscribe(v => {
        this.showPopupOnGoNextPage(v?.KO);
      });

    } else {
      this.store.dispatch(linkTab(FlowTabs.analysisResult));

      // Navigate to step 4.
      this.store.dispatch(goBackProductCriterial());
    }
  }

  goPrev() {
    if (this.isNavigateFromFlowTraceability.value) {
      this.store.dispatch(goBackToPreAnalysis());
    } else {
      this.store.dispatch(goBack());
    }
  }

  isLocked(): Observable<boolean> {
    return of(this.isLock);
  }

  getLockedReason(): string[] {
    return this.lockedReasonMessage;
  }

  constructor(private store: Store,
              private router: Router,
              private canModify: CanModificationService,
              private activateRoute: ActivatedRoute,
              private translateService: TranslateService,
              private confirmationMessage: ConfirmationMessageService,
              private readonly flowDepositControlService: FlowDepositControlService,
              private lockableFormGuardService: LockableFormGuardService) {
    // hidden confirmation when click the previous page.
    this.validateContinuePopup();
    // Set lock component messages.
    Promise.all([
      this.translateService.get('flow.deposit.button.back_warning_title').toPromise(),
      this.translateService.get('flow.deposit.button.back_warning_message').toPromise(),
      this.translateService.get('flow.deposit.button.back_warning_okButton').toPromise(),
      this.translateService.get('flow.deposit.button.back_warning_cancelButton').toPromise()
    ]).then(lockedReasonMessage => Object.assign(this, { lockedReasonMessage }));
  }


  ngOnDestroy(): void {
    this.store.dispatch(stepOnActivated({ active: false }));
    this.store.dispatch(stepOnFlowDepositComplete());
    this.destroy$.next(true);
    // this.createdBy = '';
    this.isFlowCancelable = false;

    // unsubscription
    if (this.subscriptions) {
      this.subscriptions.forEach(sub => sub.unsubscribe());
    }
  }

  cancelFlowDeposit() {
    if (this.document.hasKo) {
      this.showPopupCancelFlow();
    }

    this.backToDepositFile();
  }

  backToDepositFile(): void {
    if (this.document.noOk) {
      // this.store.dispatch(unloadUploadFileAction());
      // this.store.dispatch(clearDepositFlow());
      // this.router.navigateByUrl('/cxm-deposit/acquisition');

      // We call to API for delete flow, and redirect to acquisition path for re-upload pdf file.
      this.store.dispatch(deleteFlowDeposit({ fileId: this.uuid, hideShowMessage: false }));
    }
  }

  showPopupOnGoNextPage(docError: number): void {
    if (docError && docError > 0) {
      this.confirmationMessage
        .showConfirmationPopup({
          icon: 'error',
          title: this.message?.confirmNextPage?.title.replace('{{KO}}', docError),
          message: this.message?.confirmNextPage?.message,
          confirmButton: this.message?.confirmNextPage?.confirmButton,
          cancelButton: this.message?.confirmNextPage?.cancelButton,
          type: 'Active',
          isNoEventBtnCancel: true
        })
        .pipe(take(1))
        .subscribe((res) => {
          if (res) {
            this.store.dispatch(linkTab(FlowTabs.analysisResult));

            // Navigate to step 4.
            this.store.dispatch(goBackProductCriterial());

          } else if (this.uuid && res !== undefined && this.ownerId) {
            this.store.dispatch(cancelFlowDeposit({
              uuid: this.uuid, ownerId: this.ownerId
            }));
          }
        });
    }
  }

  showPopupConfirmModelChanged(): void {
    this.confirmationMessage
      .showConfirmationPopup({
        icon: 'error',
        title: this.message?.document_model_changed?.title,
        message: this.message?.document_model_changed?.message,
        paragraph: this.message?.document_model_changed?.message2,
        confirmButton: this.message?.document_model_changed?.confirmButton,
        type: 'Active',
        isNoEventBtnCancel: true
      })
      .pipe(take(1))
      .subscribe((isConfirm) => {
        if (isConfirm) {
          this.navigateToNextPage();
        }
      });
  }

  showPopupConfirmConfigurationChanged(): void {
    this.store.select(selectProcessControlResponseState).pipe(
      filter((res) => res && Object.keys(res).length !== 0),
      take(1),
      map((res) => res?.data?.ModeleName),
      takeWhile((modelName) => !!modelName, true),
      takeUntil(this.destroy$)
    ).subscribe((modelName) => {
      this.confirmationMessage
        .showConfirmationPopup({
          icon: 'error',
          title: this.message?.document_configuration_changed?.title,
          message: StringUtil.replaceAll(this.message?.document_configuration_changed?.message, '${ModelName}', modelName),
          paragraph: this.message?.document_configuration_changed?.message2,
          confirmButton: this.message?.document_configuration_changed?.confirmButton,
          type: 'Active',
          isNoEventBtnCancel: true
        })
        .pipe(take(1))
        .subscribe((isConfirm) => {
          if (isConfirm) {
            this.navigateToNextPage();
          }
        });
    });
  }

  showPopupCancelFlow(): void {
    this.confirmationMessage
      .showConfirmationPopup({
        icon: 'error',
        title: this.message?.confirmCancelFlow?.title,
        message: this.message?.confirmCancelFlow?.message,
        confirmButton: this.message?.confirmCancelFlow.confirmButton,
        cancelButton: this.message?.confirmCancelFlow?.cancelButton,
        type: 'Warning'
      })
      .pipe(take(1))
      .subscribe((ok) => {
        this.isLock = !ok;
        if (this.uuid && ok && this.ownerId) {
          // We call to API for delete flow, and redirect to acquisition path for re-upload pdf file.
          this.store.dispatch(deleteFlowDeposit({ fileId: this.uuid, hideShowMessage: false }));
        }
      });
  }

  validateContinuePopup(): void {
    this.store.select(selectFlowDepositStep).pipe(takeUntil(this.destroy$)).subscribe(step => {
      const steps = step.filter((item: any) => item.completed).map((item: any) => item.step);
      const maxStep = Math.max(...steps);
      if (maxStep > 3) {
        this.isShowContinuePopup.next(false);
      }
    });
  }

  pageAccessNumberChange(event: number) {
    this.pdfViewerComponent.setPageAccess(event);
  }
}
