import {Injectable} from '@angular/core';
import {
  CxmFlowDepositService,
  CxmFlowTraceabilityService,
  CxmSettingService
} from '@cxm-smartflow/shared/data-access/api';
import {flowDepositEnv} from '@env-flow-deposit';
import {flowTraceabilityEnv} from '@env-flow-traceability';
import {Observable, of} from 'rxjs';
import {
  AnalyseFlowModel,
  AnalyseFlowResponse,
  AttachmentResponse,
  DepositedFlowModel,
  FlowDepositFilterCriteriaModel,
  FlowDepositList, FlowDocumentAddress, FlowDocumentAddressDto,
  PortalConfigurationModel, PostalInfo,
  ReIdentifyFlow,
  ResourceDetail,
  SwitchFlowModel,
  TreatmentFlowModel,
  UpdateOptionAttribute, WatermarkAttribute,
} from '../model';
import {HttpClient, HttpParams} from '@angular/common/http';
import {AuthenticationConstant, FileMetadataModel, Params} from '@cxm-smartflow/shared/data-access/model';
import {settingEnv} from '@env-cxm-setting';


@Injectable({
  providedIn: 'root'
})
export class FlowDepositService {

  constructor(private cxmFlowDeposit: CxmFlowDepositService, private cxmFlowTraceability: CxmFlowTraceabilityService,
              private cxmSettingService: CxmSettingService, private _httpClient: HttpClient) {
  }

  uploadFile(form: FormData) {
    const userPrivileges = JSON.parse(<any>localStorage.getItem(AuthenticationConstant.USER_PRIVILEGES));
    let params = new HttpParams();
    params = params.set('idCreator', userPrivileges['id'] || 0);
    return this.cxmFlowDeposit.uploadFile(`${flowDepositEnv.flowDepositContext}/acquisition/deposit/portal`, form, params);
  }

  launchProcessControlStep(request: DepositedFlowModel): Observable<any> {
    return this.cxmFlowDeposit.post(`${flowDepositEnv.processControlContext}/process-control/identify-flow`, request);
  }

  analyseFlow(request: AnalyseFlowModel): Observable<AnalyseFlowResponse> {
    // @ts-ignore
    return this.cxmFlowDeposit.post(`${flowDepositEnv.processControlContext}/process-control/analyse-flow`, null, new HttpParams({ fromObject: request }));
  }

  treatmentFlowStep(request: TreatmentFlowModel) {
    return this.cxmFlowDeposit.post(`${flowDepositEnv.processControlContext}/process-control/treatment-flow`, request);
  }

  switchFlowStep(request: SwitchFlowModel) {
    let params = new HttpParams();
    if (request?.composedFileId !== undefined && request?.composedFileId !== null) {
      params = params.set('uuid', request.uuid);
      params = params.set('composedFileId', request.composedFileId);
      params = params.set('validation', request.validation || false);
      return this.cxmFlowDeposit.get(`${flowDepositEnv.processControlContext}/process-control/send-flow`, params);
    }
    return of({});
  }

  loadFileMetaData(fileId: string, funcKey?: string, privKey?: string): Observable<FileMetadataModel> {
    let params = new HttpParams();
    if (funcKey && privKey) {
      params = params.set('funcKey', funcKey);
      params = params.set('privKey', privKey);
    }
    return this.cxmFlowDeposit.get(`${flowDepositEnv.fileManagerContext}/file-manager/file/${fileId}`, params);
  }

  updateFlowDepositStep(uuid: string, step: number, isValidation: boolean, composedFileId?: string): Observable<any> {
    let params = new HttpParams();
    params = params.set('validation', isValidation || false);
    if (composedFileId) {
      params = params.set('composedFileId', composedFileId);
    }

    if (uuid && step > 1) {
      return this.cxmFlowDeposit.post(`${flowDepositEnv.processControlContext}/process-control/leave-deposit/${uuid}/${step}`, null, params);
    }
    return of({});
  }

  getDepositFlow(uuid: string, step: number): Observable<any> {
    return this.cxmFlowDeposit.get(`${flowDepositEnv.processControlContext}/process-control/deposit/${uuid}/${step}`);
  }

  cancelFlowDeposit(uuid: string, ownerId: number): Observable<any> {

    const params = new HttpParams()
      .set('ownerId', ownerId);

    return this.cxmFlowDeposit.put(`${flowDepositEnv.processControlContext}/process-control/cancel/${uuid}`, null, params);
  }

  getLimitUploadFileSize(): Observable<any> {
    return this.cxmFlowDeposit.getLimitUploadSize(`${flowDepositEnv.fileManagerContext}/file-manager/max-size-of-file-upload`);
  }

  getDepositList(page: number, pageSize: number, params?: FlowDepositFilterCriteriaModel): Observable<FlowDepositList> {
    let params$ = new HttpParams();
    if (params !== undefined) {
      Object.entries(params).forEach(([key, value]) => {
        params$ = params$.set(key, value);
      });
    }

    return this.cxmFlowTraceability.getFlowDeposit(`${flowTraceabilityEnv.flowTraceabilityContext}/flow-deposit/${page}/${pageSize}`, params$);
  }


  forceDechargement(clientid: string) {
    return this.cxmFlowDeposit.patch(`${flowDepositEnv.processControlContext}/process-control/flow-unloading/${clientid}/force-schedule`)
  }

  storeBackground(flowId: string, type: string, form: any) {
    let params = new HttpParams().set('type', type);
    params = params.append('flowId', flowId);
    return this.cxmFlowDeposit.singleUpload(`${flowDepositEnv.processControlContext}/storage/store/resource`, form, params);
  }

  getOptionAttributeDetail(fileId: string): Observable<ResourceDetail> {
    return this.cxmFlowDeposit.get(`${flowDepositEnv.processControlContext}/resource-file/${fileId}`)
  }

  getResourceFileById(fileId: string) {
    return this.cxmFlowDeposit.base64File(`${flowDepositEnv.resourceContext}/resources/file/${fileId}`)
  }

  UpdateOptionAttribute(payload: UpdateOptionAttribute) {
    return this.cxmFlowDeposit.post(`${flowDepositEnv.processControlContext}/resource-file`, payload);
  }

  fetchAllResourceFile(flowId: string): Observable<AttachmentResponse> {
    return this.cxmFlowDeposit.get(`${flowDepositEnv.processControlContext}/resource-file/${flowId}/all`)
  }

  deleteFlowBackground(fileId: number) {
    return this.cxmFlowDeposit.delete(`${flowDepositEnv.processControlContext}/resource-file/${fileId}`)
  }

  getAllResources(params: Params, types: any): Observable<any> {
    let httpParams = new HttpParams();

    Object.entries(params)
      .forEach(([key, value]) => {
        httpParams = httpParams.set(key, value);
      });

    httpParams = httpParams.set('types', types);
    return this.cxmSettingService.get(`${settingEnv.settingContext}/resources`, httpParams);
  }

  hasPortalSignatureConfig(modelName: string):Observable<boolean> {
    const params = new HttpParams().set('modelName', modelName);
    return this.cxmSettingService.get(`${settingEnv.settingContext}/setting/portal/has-config-signature`, params);
  }


  getLastModifiedPortalConfiguration(): Observable<PortalConfigurationModel> {
    return this.cxmSettingService.get(`${settingEnv.settingContext}/setting/portal/last-modified-configuration`);
  }

  validateModelConfigurationChanged(modelName: string): Observable<boolean> {
    const params = new HttpParams().set('modelName', modelName);
    return this.cxmSettingService.get(`${settingEnv.settingContext}/setting/portal/model-name-changed`, params);
  }

  reAnalyzeModelChanged(uuid: string): Observable<ReIdentifyFlow> {
    return this.cxmSettingService.put(`${flowDepositEnv.processControlContext}/process-control/re-identify-flow/${uuid}`);
  }

  createWaterMark(payload: WatermarkAttribute) {
    return this.cxmFlowDeposit.post(`${flowDepositEnv.processControlContext}/watermark`, payload);
  }

  fetchWaterMark(flowId: string): Observable<WatermarkAttribute> {
    return this.cxmFlowDeposit.get(`${flowDepositEnv.processControlContext}/watermark/${flowId}`);
  }
  updateWaterMark(payload: WatermarkAttribute) {
    return this.cxmFlowDeposit.put(`${flowDepositEnv.processControlContext}/watermark`, payload);
  }
  deleteWaterMark(flowId: number) {
    return this.cxmFlowDeposit.delete(`${flowDepositEnv.processControlContext}/watermark/${flowId}`);
  }

  fetchPostalCode(postalCode: number): Observable<PostalInfo> {
    return this._httpClient.get(`https://apicarto.ign.fr/api/codes-postaux/communes/${postalCode}`);
  }

  fetchFlowDocumentAddress(flowId: string, docId:string): Observable<FlowDocumentAddress[]> {
    return this.cxmFlowDeposit.get(`${flowDepositEnv.processControlContext}/flow-document-address/${flowId}/${docId}`);
  }

  updateFlowDocumentAddress(payload: FlowDocumentAddressDto) {
    return this.cxmFlowDeposit.put(`${flowDepositEnv.processControlContext}/flow-document-address`, payload);
  }
}
