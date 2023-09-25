import { Component, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  cancelFlowDeposit,
  clearDepositFlow,
  selectDocumentIsKO,
  selectFlowDetails,
  selectPreAnalysisState,
  unloadUploadFileAction
} from '@cxm-smartflow/flow-deposit/data-access';
import { take, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CanModificationService } from '@cxm-smartflow/shared/data-access/services';
import { FlowTraceability } from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-file-info',
  templateUrl: './file-info.component.html',
  styleUrls: ['./file-info.component.scss']
})
export class FileInfoComponent implements OnDestroy {

  fileName = '';
  message: any;
  uuid: string;
  documentKO: any;
  isFlowCancelable = false;
  ownerId = 0;

  destroy$ = new Subject<boolean>();

  constructor(
    private store: Store,
    private confirmationMessage: ConfirmationMessageService,
    private translateService: TranslateService,
    private canModify: CanModificationService,
    private activateRoute: ActivatedRoute,
    private router: Router) {

    // Load confirm cancel flow message.
    this.translateService.get('flow.deposit.confirmCancelFlow').pipe(take(1)).subscribe(v => this.message = v);

    // Subscribe document KO.
    this.store.select(selectDocumentIsKO)
      .pipe(takeUntil(this.destroy$))
      .subscribe(res => {
        this.documentKO = res;
      });

    // Subscribe file info.
    this.store.select(selectPreAnalysisState).pipe(takeUntil(this.destroy$)).subscribe(v => {
      if (v?.fileName && v?.extension) this.fileName = `${v.fileName}.${v.extension}`;
      if (v?.uuid) this.uuid = v.uuid;
    });

    // Subscribe file info from flow details.
    this.store.select(selectFlowDetails).pipe(takeUntil(this.destroy$)).subscribe(v => {
      if (v?.fileName && v?.extension && !this.fileName) this.fileName = `${v.fileName}.${v.extension}`;
    });

    // Validate cancel flow privilege.
    this.activateRoute.queryParams.pipe(takeUntil(this.destroy$)).subscribe(v => {
      if (v?.ownerId) {
        this.ownerId = v?.ownerId;
        this.isFlowCancelable = this.canModify.hasModify(FlowTraceability.CXM_FLOW_TRACEABILITY, FlowTraceability.CANCEL_FLOW, v?.ownerId, true);
      } else {
        // this.createdBy = JSON.parse(localStorage.getItem('userPrivileges') || '')?.name?.trim();
        this.isFlowCancelable = true;
      }
    });

  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.destroy$.complete();
    this.isFlowCancelable = false;
    this.ownerId = 0;
  }

  cancelFlow(): void {
    if (this.documentKO.noOk) {
      this.store.dispatch(unloadUploadFileAction());
      this.store.dispatch(clearDepositFlow());
      this.router.navigateByUrl('/cxm-deposit/acquisition');
    } else {
      this.confirmationMessage
        .showConfirmationPopup({
          icon: 'error',
          title: this.message?.title,
          message: this.message?.message,
          confirmButton: this.message?.confirmButton,
          cancelButton: this.message?.cancelButton,
          type: 'Warning'
        })
        .pipe(take(1))
        .subscribe((ok) => {
          if (this.uuid && ok && this.ownerId) {
            this.store.dispatch(cancelFlowDeposit({
              uuid: this.uuid,
              ownerId: this.ownerId
            }));
          }
        });
    }
  }
}
