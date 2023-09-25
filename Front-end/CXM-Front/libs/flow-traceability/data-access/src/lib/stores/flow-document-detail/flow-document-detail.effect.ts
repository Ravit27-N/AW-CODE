import { Injectable } from '@angular/core';
import * as fromActions from './flow-document-detail.action';
import {
  loadFlowDocumentDetail,
  unloadFlowDocumentDetail,
} from './flow-document-detail.action';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { FlowTraceabilityService } from '@cxm-smartflow/flow-traceability/data-access';
import { TranslateService } from '@ngx-translate/core';
import { CanVisibilityService, SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { Store } from '@ngrx/store';
import {
  DocumentChannelModel,
  ExtensionConstant,
  FlowDocumentDetailPrivilegeModel,
  FlowTraceability
} from '@cxm-smartflow/shared/data-access/model';
import {
  canDownloadFileOfFlowDocumentDigitalInStatuses,
  canDownloadFileOfFlowDocumentPostalInStatuses,
  FlowStatusConstant
} from '@cxm-smartflow/flow-traceability/util';

@Injectable({
  providedIn: 'root'
})
export class FlowDocumentDetailEffect {

  showFullNameInStatus = [
    FlowStatusConstant.DEPOSITED,
    FlowStatusConstant.VALIDATED,
    FlowStatusConstant.REFUSED,
    FlowStatusConstant.REFUSED_DOCUMENT,
    FlowStatusConstant.CANCELED
  ];

  loadDocumentDetail$ = createEffect(() => this.actions.pipe(
    ofType(loadFlowDocumentDetail),
    exhaustMap(({ id }) => this.flowService.getFlowDocumentDetail(id).pipe(
      map(documentDetail => fromActions.loadFlowDocumentDetailSuccess({ documentDetail: this.transformFlowDocumentDetail(documentDetail) })),
      catchError(httpErrorResponse => [fromActions.loadFlowDocumentDetailFail({ httpErrorResponse })])
    ))
  ));

  loadFlowDocumentDetailFail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.loadFlowDocumentDetailFail),
    tap(() => {
      this.translate.get('flow.document.errorMessages.unableToLoadDocumentDetail').toPromise().then(message => {
        this.snackbar.openCustomSnackbar({ icon: 'close', type: 'error', message });
      });
    })), { dispatch: false });

  downloadDocumentDetail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.downloadDocumentDetail),
    tap(({ documentId, docName, _type }) => {
      this.flowService.getBase64File(documentId,"", _type).toPromise().then(e => {
        this.downloadBase64File(e, docName);
      });
    })), { dispatch: false });

  backToLastURL$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.backToLastURL),
    tap(() => {
      history.back();
      this.store.dispatch(unloadFlowDocumentDetail());
    })
  ), { dispatch: false });

  downloadAssociateDocument$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.downloadAssociateDocument),
    tap(({ fileId, associateDocKey }) => {
      this.flowService.downloadAssociateDocument(fileId).toPromise().then(res => {
        this.translate.get(associateDocKey)
          .subscribe(data => {
            this.downloadBase64File(res.content, `${data}.${ExtensionConstant.PDF}`);
          }, () => {
            this.translate.get('flow.document.errorMessages.unableToDownloadAssociateDocument').toPromise()
              .then(m => this.snackbar.openCustomSnackbar({ icon: 'close', type: 'error', message: m }));
          });
      });
    })
  ), { dispatch: false });

  loadAssociateDocument$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.loadAssociateDocument),
    exhaustMap(({ flowDocumentId }) => this.flowService.getFlowDocumentOfElementAssociationList(flowDocumentId).pipe(
      map(associateDocument => fromActions.loadAssociateDocumentSuccess({ associateDocument })),
      catchError(httpErrorResponse => [fromActions.loadAssociateDocumentFail({ httpErrorResponse })])
    ))));

  loadAssociateDocumentFail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.loadAssociateDocumentFail),
    tap(() => {
      this.translate.get('flow.document.errorMessages.unableToLoadAssociateDocument').toPromise()
        .then(message => this.snackbar.openCustomSnackbar({ icon: 'close', type: 'error', message }));
    })
  ), { dispatch: false });


  viewStatusInfo$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.viewStatusInfo),
    exhaustMap(({ id, locale }) => this.flowService.getStatusInfo(id, locale).pipe(
      map((eventHistoryInfo) => fromActions.viewStatusInfoSuccess({ eventHistoryInfo })),
      catchError((httpErrorResponse) => [fromActions.viewStatusInfoFail({ httpErrorResponse })])
    ))
  ));

  downloadBase64File(base64: string, filename: string) {
    const source = `data:application/octet-stream;base64,${base64}`;
    const link = document.createElement('a');
    link.href = source;
    link.download = filename;
    link.click();
  }

  transformFlowDocumentDetail = (flowDocument: any) => {
    return {
      ...flowDocument,
      histories: this.transformHistories(flowDocument),
      privilege: this.getFlowDocumentDetailPrivilege(flowDocument)
    };
  };

  getFlowDocumentDetailPrivilege = (flowDocument: any): FlowDocumentDetailPrivilegeModel => {
    return {
      canDownloadFeed: this.canDownloadFeed(flowDocument),
      canDownloadAssociate: this.isCanDownloadAssociate(flowDocument)
    };
  };

  canDownloadFeed = (flowDocument: any): boolean => {
    // Validate download associate document button.
    const histories = flowDocument?.histories?.map((e: any) =>
      e?.historyStatus?.status?.toLowerCase()
    );

    let statusesCanDownload = canDownloadFileOfFlowDocumentDigitalInStatuses;

    if(flowDocument?.documentChannel?.status === DocumentChannelModel.Postal){
      statusesCanDownload = canDownloadFileOfFlowDocumentPostalInStatuses;
    }

    return this.canVisibilityService.hasVisibility(FlowTraceability.CXM_FLOW_TRACEABILITY, FlowTraceability.DOWNLOAD_DOCUMENT, flowDocument?.details?.ownerId, true)
      && statusesCanDownload.some((r: string) => histories?.indexOf(r) >= 0);
  };

  isCanDownloadAssociate = (flowDocument: any): boolean => {
    return this.canVisibilityService.hasVisibility(FlowTraceability.CXM_FLOW_TRACEABILITY,
      FlowTraceability.OPEN_AND_DOWNLOAD_RELATED_ITEM,
      flowDocument?.details?.ownerId,
      true);
  }

  transformHistories = (flowDocument: any): any [] => {
    return flowDocument?.histories?.map((value: any) => {
      return {
        ...value,
        canSeeFullName: this.canSeeFullName(value)
      };
    });
  };

  canSeeFullName = (flow: any): boolean => {
    const { status } = flow?.historyStatus;
    return this.showFullNameInStatus.includes(status);
  };


  constructor(private actions: Actions,
              private translate: TranslateService,
              private snackbar: SnackBarService,
              private store: Store,
              private flowService: FlowTraceabilityService,
              private canVisibilityService: CanVisibilityService) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }
}
