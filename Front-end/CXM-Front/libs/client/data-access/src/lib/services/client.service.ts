import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService, CxmProfileService, CxmSettingService } from '@cxm-smartflow/shared/data-access/api';
import { cxmProfileEnv } from '@env-cxm-profile';
import {Observable, of} from 'rxjs';
import { ClientModel, IClientListResponse } from '../models';
import { settingEnv } from '@env-cxm-setting';
import { PreferenceAPIResponse } from '../models';
import { PublicHolidayModel } from '../models';
import { CritteriaDistributionPayload } from '../models';
import {
  HubAccountDto,
  MetadataRequestModel,
  MetadataPayloadType,
  MetadataResponseModel, HubAccountResponse
} from '../models';
import { ServiceProviderDto, ServiceProviderRequest } from '../models';
import { PostalConfigurationVersion } from '../models/postal-configuration-version.model';

@Injectable()
export class ClientService {
  getClientList(filters: {
    page: number;
    pageSize: number;
    sortDirection: 'asc' | 'desc';
    sortByField: string;
  }): Observable<IClientListResponse> {
    let params = new HttpParams();
    if(filters.page){
      params = params.set('page', filters.page);
    }
    if(filters.pageSize){
      params = params.set('pageSize', filters.pageSize);
    }
    if (filters.sortByField) {
      params = params.set('sortByField', filters?.sortByField);
    }
    if (filters.sortDirection) {
      params = params.set('sortDirection', filters?.sortDirection);
    }

    return this.service.get(
      `${cxmProfileEnv.profileContext}/clients`,
      params
    );
  }

  deleteClient(id: number): Observable<void> {
    return this.service.delete(`${cxmProfileEnv.profileContext}/clients/${id}`);
  }


  uploadClientDocument(form: FormData): Observable<any> {
    return this.service.uploadFile(`${cxmProfileEnv.profileContext}/storage/store`, form);
  }


  createClient(payload: any) {
    return this.service.post(`${cxmProfileEnv.profileContext}/clients`, payload);
  }

  deleteClientPrivacyDoc(id: string): Observable<any> {
    return this.service.delete(`${cxmProfileEnv.profileContext}/storage/delete/${id}`);
  }

  checkDuplicatedClientName(params: {name: string, id?: number}): Observable<boolean> {
    const param = new HttpParams().set('name', params.name);
    if(params?.id) param.set('id', params.id);
    return this.service.get(`${cxmProfileEnv.profileContext}/clients/is-duplicate`, param);
  }

  getClientInfo(clientId: string): Observable<ClientModel> {
    return this.service.get(`${cxmProfileEnv.profileContext}/clients/${clientId}`);
  }

  updateClient(payload: any) {
    return this.service.put(`${cxmProfileEnv.profileContext}/clients`, payload);
  }

  getHoliday(): Observable<Array<PublicHolidayModel>> {
    return this.service.get(`${cxmProfileEnv.profileContext}/clients/public-holiday`)
  }

  getModelsByVersionId(clientName: string, version: number): Observable<any> {
    const params = new HttpParams()
      .set('clientName', clientName)
      .set('version', version);
    return this.service.get(`${cxmProfileEnv.profileContext}/clients/portal/configuration/version`, params);
  }


  getModels(clientName: string): Observable<any> {
    const params = new HttpParams().set('clientName', clientName);
    return this.service.get(`${cxmProfileEnv.profileContext}/clients/portal/configuration`, params);
  }

  downloadINIFile(clientName: string): Observable<any> {
    const params = new HttpParams().set('clientName', clientName);
    return this.apiService.getBase64File(`${cxmProfileEnv.profileContext}/clients/portal/configuration/file`, params);
  }

  switchIdentificationMode(clientName: string, active: boolean): Observable<any> {
    const params = { clientName, active };
    return this.service.put(`${cxmProfileEnv.profileContext}/clients/portal-setting-config`, params);
  }

  registerNewConfiguration(payload: any) {
    return this.service.put(`${cxmProfileEnv.profileContext}/clients/portal/configuration`, payload);
  }

  updateSettingCriteria(criteria: CritteriaDistributionPayload): Observable<PreferenceAPIResponse> {
    return this.cxmSettingService.post(`${settingEnv.settingContext}/setting/criteria-distribution`, criteria);
  }

  getMetadata(customer: string, type: MetadataPayloadType): Observable<MetadataResponseModel> {

    const params = new HttpParams()
      .set('customer', customer)
      .set('types', type);

    return this.cxmSettingService.get(`${settingEnv.settingContext}/setting/channel-metadata`, params);
  }


  updateMetadataByType(metadataDtoModel: MetadataRequestModel) {
    return this.cxmSettingService.post(`${settingEnv.settingContext}/setting/channel-metadata`, metadataDtoModel);
  }

  getHubAccessAccount(client: string): Observable<HubAccountResponse> {
    const params = new HttpParams().set('client', client);
    return this.cxmSettingService.get(`${cxmProfileEnv.profileContext}/user-hub/client-account`, params);
  }

  updateHubAccessAccount(hubAccountDto: HubAccountDto) {
    return this.cxmSettingService.post(`${cxmProfileEnv.profileContext}/user-hub/user-account`, hubAccountDto);
  }

  getServiceProvider(customer: string): Observable<ServiceProviderDto> {
    const params = new HttpParams().set('customer', customer);
    return this.cxmSettingService.get(`${cxmProfileEnv.profileContext}/hub/configuration/customer-service-provider`, params);
  }

  getServiceProviderCriteria(customer: string): Observable<ServiceProviderDto> {
    const params = new HttpParams().set('channel', ['MAIL', 'SMS'].join(','));
    return this.cxmSettingService.get(`${cxmProfileEnv.profileContext}/hub/configuration/service-provider`, params);
  }

  updateServiceProvider(serviceProvider: ServiceProviderRequest) {
    return this.cxmSettingService.post(`${cxmProfileEnv.profileContext}/hub/configuration/customer-service-provider`, serviceProvider);
  }

  getPostalConfigurationVersion(clientName: string): Observable<PostalConfigurationVersion []>{
    const params = new HttpParams().set('clientName', clientName);
    return this.service.get(`${cxmProfileEnv.profileContext}/clients/portal/configuration/versions`, params);
  }

  revertConfiguration(clientName: string, referenceVersion: number): Observable<PostalConfigurationVersion> {
    const params = new HttpParams().set('clientName', clientName)
      .set('referenceVersion', referenceVersion);
    return this.service.post(`${cxmProfileEnv.profileContext}/clients/portal/configuration/version/revert`,{}, params)
  }

  constructor(private readonly service: CxmProfileService, private readonly apiService: ApiService,
              private readonly cxmSettingService: CxmSettingService) { }
}
