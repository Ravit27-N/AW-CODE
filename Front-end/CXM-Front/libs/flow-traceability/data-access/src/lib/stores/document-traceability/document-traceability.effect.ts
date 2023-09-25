/* eslint-disable @typescript-eslint/member-ordering */
import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, exhaustMap, map, switchMap, tap } from 'rxjs/operators';
import { FlowTraceabilityService } from '../../services';
import * as actions from './document-traceability.action';
import { FileSaverUtil } from '@cxm-smartflow/shared/utils';
import {
  exportSuivi,
  exportSuiviSuccess,
  exportSuiviFailure,
} from './document-traceability.action';
import {
  appRoute,
  DocumentChannelModel,
  FlowTraceability,
  PrivilegeModel
} from '@cxm-smartflow/shared/data-access/model';
import { Store } from '@ngrx/store';
import { clearFlowTraceabilityState } from '../flow-traceability';
// eslint-disable-next-line @nrwl/nx/enforce-module-boundaries
import { CanModificationService, CanVisibilityService, SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import {
  canDownloadFileOfFlowDocumentDigitalInStatuses,
  canDownloadFileOfFlowDocumentPostalInStatuses,
  DocumentNotificationStatus,
  replaceStatusLabelByDash
} from '@cxm-smartflow/flow-traceability/util';
import { validateFlowSubChannel } from '../../models';
// eslint-disable-next-line @nrwl/nx/enforce-module-boundaries
import { CampaignType } from '@cxm-smartflow/follow-my-campaign/data-access';
import { formatDate } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
@Injectable({
  providedIn: 'root'
})
export class DocumentTraceabilityEffect {

  readonly completeStatusList = ['Sent', 'Completed', 'In progress'];

  transformFlowDocumentList = (response: any) => {
    const finalContent = response?.contents?.map((item: any) => {
      return {
        ...item,
        canShowDateStatus: this.shouldShowDateStatus(item),
        category: this.getCategoryBySubChannel(item?.subChannel),
        statusLabelReplacement: this.getStatusLabelReplacement(item?.status),
        documentStatus: this.transformDocumentStatus(item?.documentStatus),
        privilege: this.getPrivilege(item)
      };
    });

    return {
      contents: finalContent,
      page: response?.page,
      pageSize: response?.pageSize,
      total: response?.total
    };
  };

  transformDocumentStatus = (documentStatus: any) => {
    return {
      status: documentStatus?.status,
      statusLabel: this.getDocumentStatusLabel(documentStatus?.statusLabel)
    };
  };

  getDocumentStatusLabel = (statusLabel: string): string => {
    if (statusLabel.includes('sent')) {
      return 'flow.document.status.sent_list';
    }

    const splitStatus = statusLabel?.split('.').slice(-1)[0];
    if (DocumentNotificationStatus.includes(splitStatus)) {
      return 'flow.document.status.sent_list';
    }
    return statusLabel;
  };

  getStatusLabelReplacement = (status: string): string => {
    return replaceStatusLabelByDash(status);
  };

  getCategoryBySubChannel = (subChannel: string): string => {
    return validateFlowSubChannel(subChannel);
  };

  shouldShowDateStatus = (row: any): boolean => {
    return this.completeStatusList.includes(row?.status);
  };

  getPrivilege = (row: any): PrivilegeModel => {
    const privilege = {
      canDownload: this.canDownload(row)
    };

    return {
      ...privilege,
      canShowToggleButton: privilege.canDownload
    };
  };

  canDownload = (row: any): boolean => {
    let statusesCanDownload = canDownloadFileOfFlowDocumentDigitalInStatuses;

    if(row?.documentChannel?.status === DocumentChannelModel.Postal) {
      statusesCanDownload = canDownloadFileOfFlowDocumentPostalInStatuses;
    }

    return this.canVisibilityService.hasVisibility(FlowTraceability.CXM_FLOW_TRACEABILITY, FlowTraceability.DOWNLOAD_DOCUMENT, row?.ownerId, true)
      && statusesCanDownload.includes(row?.status?.toLowerCase());
  };

  loadDocumentTraceabilityList$ = createEffect(() =>
    this.actions$.pipe(
      ofType(actions.loadDocumentTraceabilityList),
      exhaustMap((args) => {
        let params = {};

        if (args.params) {
          const { isDisplayLabel, ...res } = args.params;
          params = res;
        }
        return this.service
          .getFlowDocumentPagination(
            args.flowTraceabilityId || 0,
            args.page,
            args.pageSize,
            {
              ...params
            }
          )
          .pipe(
            map((response) =>
              actions.loadDocumentTraceabilityListSuccess({
                response: this.transformFlowDocumentList(response),
                flowTraceabilityId: args.flowTraceabilityId || 0,
                params: args.params
              })
            ),
            catchError(() => of(actions.loadDocumentTraceabilityListFail()))
          );
      })
    )
  );

  documentTraceabilityListFilterChangeEffect$ = createEffect(() =>
    this.actions$.pipe(
      ofType(actions.documentTraceabilityListFilterChangeAction),
      switchMap((args) => [actions.loadDocumentTraceabilityList(args as any)])
    )
  );

  downloadDocDocumentTraceabilityActionEffect$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(actions.downloadDocDocumentTraceabilityAction),
        tap((args: any) => {
          if (args.flow.fileUrl) {
            window.open(args.flow.fileUrl);
          } else if (args.flow.fileId) {
            this.service.getBase64File(args.flow.fileId, args._type).subscribe((data) => {
              this.downloadBase64File(
                data,
                args.flow
              );
            });
          }
        })
      ),
    { dispatch: false }
  );

  navigateToDetailActionEffect = createEffect(() =>
    this.actions$.pipe(
      ofType(actions.navigateToDetailAction),
      tap((args) => {
        if (this.router.url.split('/').indexOf('view-shipment') !== -1) {
          this.router.navigateByUrl(
            [
              '/cxm-flow-traceability/document-detail-from-flow',
              args.flow.id
            ].join('/')
          );
        } else {
          this.router.navigate(
            ['/cxm-flow-traceability/flow-document-detail'],
            { queryParams: { id: args.flow.id } }
          );
        }
      })
    ), { dispatch: false });

  navigateToListFlowTraceability$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(actions.navigateToPreviousUrl),
        tap((args) => {
          this.store.dispatch(clearFlowTraceabilityState());
          if (args.isBackToFlow) {
            this.router.navigate([appRoute.cxmFlowTraceability.list]);
          } else {
            history.back();
          }
        })
      ),
    { dispatch: false }
  );

   getFilename = (flowDocumentDetail: any): string => {
      if(flowDocumentDetail?.docName !== null && flowDocumentDetail?.docName !== ''){
        return flowDocumentDetail?.docName;
      }

      if(flowDocumentDetail?.subChannel?.toUpperCase() === CampaignType.SMS){
        return flowDocumentDetail?.document?.concat('.txt');
      }else {
        return flowDocumentDetail?.document?.concat('.eml');
      }
   }

  downloadBase64File(base64: string, flowDocumentDetail: any) {
    this.base64Converter.downloadBase64(base64, this.getFilename(flowDocumentDetail));
  }

  constructor(
    private actions$: Actions,
    private service: FlowTraceabilityService,
    private router: Router,
    private base64Converter: FileSaverUtil,
    private activatedRoute: ActivatedRoute,
    private store: Store,
    private canVisibilityService: CanVisibilityService,
    private canModifyService: CanModificationService,
    private translate: TranslateService,
    private snackBar: SnackBarService
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }

  exportSuivi = createEffect(() =>
  this.actions$.pipe(
      ofType(exportSuivi),
      exhaustMap((action) =>
          this.service.exportSuiviToCSV(
              action.services[0].data.channels,
              action.services[0].data.categories,
              action.services[0].data.fillers,
              action.services[0].data.status, 
              action.services[0].data.filter,
              action.services[0].data.startDate instanceof Date
                  ? action.services[0].data.startDate.toISOString()
                  : action.services[0].data.startDate,
              action.services[0].data.endDate instanceof Date
                  ? action.services[0].data.endDate.toISOString()
                  : action.services[0].data.endDate,
                  action.services[0].data.sortByField,
                  action.services[0].data.sortDirection,
                  action.services[0].data.page,
                  action.services[0].data.pageSize,
          ).pipe(
              map((response: any) => {
                  let fileName = response?.filename;
                  if(!fileName) {
                    fileName = "Exp_suivi_"+formatDate(new Date(), 'yyyyMMddhhmmss', 'fr').toString()+".csv";
                  }
                  this.base64Converter.saveCsvFile(response?.file, fileName);
                  this.translate.get('flow.history.export_csv_success_result').toPromise().then(message => {
                    this.snackBar.openCustomSnackbar({ icon: 'close', type: 'success', message })});
                  return exportSuiviSuccess({ response });
              }),
              catchError((error) => of(exportSuiviFailure({ error })))
          )
      )
  )
);

}
