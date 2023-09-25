import { ActivatedRoute, ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { FlowDepositService } from './flow-deposit.service';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  initAcquisitionFileUploadFeature,
  initAnalyzeResponse,
  initChooseChannel,
  initDefaultBase64PreAnalysis,
  initDefaultConfiguration,
  initIsNavigateFromFlowTraceability,
  initOkDocumentProcessed,
  initPortalStep,
  initPreAnalysis,
  initProcessControlRequest,
  initProcessControlResponse,
  initProductionForm,
  initTreatmentResponse,
  loadDepositParam,
  loadFlowDetails
} from '../stores';
import { DepositedFlowModel, DepositFlowStateModel } from '../model';
import { DepositManagement } from '@cxm-smartflow/shared/data-access/model';

@Injectable({
  providedIn: 'root'
})
export class FlowDepositResolverService implements Resolve<any> {
  constructor(private activatedRoute: ActivatedRoute, private service: FlowDepositService, private store: Store) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any {
    // District params and add it to store.
    const { createdBy, fileId, validation, composedFileId, step, ownerId } = route.queryParams;
    this.store.dispatch(loadDepositParam({
      navigateParams: {
        // createdBy,
        fileId,
        validation: validation || false,
        composedFileId,
        step: step || 0,
        ownerId
      }
    }));
    const param = { step, fileId, composedFileId };
    if (param.step !== undefined && param.fileId !== undefined) {
      this.service.getDepositFlow(param.fileId, param.step).toPromise().then((response: DepositFlowStateModel) => {
        this.store.dispatch(initDefaultConfiguration({ defaultConfig: response.defaultConfiguration }));

        if (response?.processControlRequest) {
          this.store.dispatch(loadFlowDetails({ flowDetails: response?.processControlRequest
          }));
        }
        // initial deposit step
        this.store.dispatch(initPortalStep({ step: Number(param.step) }));
        const _step = Number(param.step);
        if (_step > 1 && _step <= 5) {
          if (_step >= 2) {
            this.initializePreAnalysisStep2(response);
          }
          if (_step >= 3 || _step >= 4) {
            this.initializeAnalysisResultStep3(response, param.composedFileId);
          }
          if (_step === 5) {
            this.initializeFinishedStep5(response, param.composedFileId);
          }
        }
      });
    }
    return undefined;
  }

  /**
   * Initialize preAnalysisStep (step 2).
   * @param response
   * @private
   */
  private initializePreAnalysisStep2(response: any) {
    // Init preAnalysis state.
    const preAnalysisState: DepositedFlowModel = response?.processControlRequest;
    this.store.dispatch(initPreAnalysis(preAnalysisState));

    // Init base64 pdf file.
    this.loadBase64Pdf(preAnalysisState?.fileId, DepositManagement.CXM_FLOW_DEPOSIT, DepositManagement.MODIFY_A_DEPOSIT);

    // Init processControlRequest.
    const processControlRequest = {
      channel: response?.processControlResponse?.Channel || 'Postal',
      flowType: response?.processControlRequest?.flowType,
      fileId: response?.processControlRequest?.fileId,
      uuid: response?.processControlRequest?.uuid,
      idCreator: response?.processControlRequest?.idCreator,
      modelName: response?.processControlResponse?.modeleName
    };
    this.store.dispatch(initProcessControlRequest({ data: processControlRequest }));

    // Init processControlResponse.
    const processControlResponse = {
      data: {
        Channel: response?.processControlResponse?.Channel,
        ModeleName: response?.processControlResponse?.ModeleName,
        ModeleType: response?.processControlResponse?.ModeleType,
        SubChannel: response?.processControlResponse?.SubChannel
      },
      message: 'Finished',
      status: 200,
      timestamp: 1651031464570
    };
    this.store.dispatch(initProcessControlResponse({ data: processControlResponse }));

    // Init choose channel.
    this.store.dispatch(initChooseChannel({ channel: response?.processControlResponse?.Channel }));

    this.store.dispatch(initIsNavigateFromFlowTraceability({ value: true }));

    // Init acquisition file upload feature.
    this.initAcquisitionFileUploadFeature(response);

  }

  public initializeAnalysisResultStep3(response: any, composedFileId: string) {
    // Init processControlResponse.
    const processControlResponse = {
      data: {
        Channel: response?.processControlResponse?.Channel,
        ModeleName: response?.processControlResponse?.ModeleName,
        ModeleType: response?.processControlResponse?.ModeleType,
        SubChannel: response?.processControlResponse?.SubChannel,
        document: {
          DOCUMENT: response?.analyzeResponse?.DOCUMENT,
          NbDocuments: response?.analyzeResponse?.NbDocuments,
          NbDocumentsKO: response?.analyzeResponse?.NbDocumentsKO,
          NbPages: response?.analyzeResponse?.NbPages,
        },
        composedFileId: composedFileId,
        modifiedFlowDocumentAddress: response?.modifiedFlowDocumentAddress
      },
      message: '',
      status: 200,
      timestamp: ''
    };

    this.store.dispatch(initProcessControlResponse({ data: processControlResponse })); //

    // Init AnalyzeResponse Object.
    this.store.dispatch(initAnalyzeResponse({ data: { ...processControlResponse } }));

    // InitOkDocumentProcessed.
    const okDocumentProcessed = this.getProcessOkDocument(response?.analyzeResponse);

    this.store.dispatch(initOkDocumentProcessed({ value: okDocumentProcessed }));

    // Init production form.
    this.initProductionForm(response);
  }


  private initializeFinishedStep5(response: any, composedFileId: string) {
    // Init treatment response object.
    const treatmentData = response?.treatmentResponse;
    this.store.dispatch(initTreatmentResponse({ response: { ...treatmentData, composedFileId: composedFileId } }));
  }

  private loadBase64Pdf(fileId: string, funcKey: string, privKey: string) {
    this.service.loadFileMetaData(fileId, funcKey, privKey).subscribe(response => {
      this.store.dispatch(initDefaultBase64PreAnalysis({ base64: response.content}));
    });
  }

  private getProcessOkDocument(analyzeResponse: any) {
    return analyzeResponse?.DOCUMENT?.filter((x: any) => x.Analysis === 'OK')
      .reduce((prev: any, cur: any) => {
        return { nbDocument: prev.nbDocument + 1, nbPage: prev.nbPage + parseInt(cur.NbPages) };
      }, { nbDocument: 0, nbPage: 0 });
  }

  private initAcquisitionFileUploadFeature(response: any) {
    const acquisitionFileUpload = {
      prepared: true,
      sending: true,
      done: true,
      error: false,
      progress: 100,
      progressFileName: response?.processControlRequest?.fileName,
      response: response?.processControlRequest,
      locked: true,
      files: {}
    };
    this.store.dispatch(initAcquisitionFileUploadFeature(acquisitionFileUpload));
  }

  private initProductionForm(response: any) {
    const productionForm = { ...response?.analyzeResponse?.DOCUMENT?.[0]?.PRODUCTION };
    this.store.dispatch(initProductionForm({ productionForm: productionForm }));
  }
}
