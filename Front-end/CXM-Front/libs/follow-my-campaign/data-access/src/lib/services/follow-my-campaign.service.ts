import { HttpEvent, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CxmCampaignService, CxmSettingService, CxmTemplateService } from '@cxm-smartflow/shared/data-access/api';
import { campaignEnv as env } from '@env-cxm-campaign';
import { Observable, of } from 'rxjs';
import {
  CAMPAIGN_DESTINATION_LOCAL_STORAGE,
  CampaignDestinationModel,
  CampaignList,
  CampaignModel,
  CampaignSmsSendTestModel, MetadataPayloadType, MetadataResponseModel
} from '../models';
import { templateEnv as envTemplate } from '@env-cxm-template';
import { TemplateList } from '@cxm-smartflow/shared/data-access/model';
import { settingEnv } from '@env-cxm-setting';


interface CsvUploadOptions {
  templateId: string;
  type: 'SMS' | 'EMAIL';
  dirs: string;
  isKeepOriginalName: boolean;
  filename: string;
  hasHeader: boolean;
  removeDuplicate: boolean;
}

@Injectable({
  providedIn: 'root'
})

/**
 * Handle transactional service.
 *
 * @author Pisey CHORN
 */
export class FollowMyCampaignService {
  constructor(
    private cxmCampaignService: CxmCampaignService,
    private cxmSettingService: CxmSettingService,
    private cxmTemplateService: CxmTemplateService) {
  }

  getAllCampaign(): Observable<any> {
    return this.cxmCampaignService.get(`${env.campaignContext}/campaigns`);
  }

  getList(page?: number, pageSize?: number): Observable<any> {
    return this.cxmCampaignService.getList(
      `/campaign/api/v1/campaigns/sendMail`,
      page,
      pageSize
    );
  }

  checkValidateCSVFile(headerArray: string[], id: number , type: 'EMAIL'|'SMS' = 'EMAIL' ): Observable<boolean> {
    let params = new HttpParams();
    params = params.append('columns', headerArray.join(','));
    params = params.append('type', type);
    return this.cxmCampaignService.get(
      `${env.campaignContext}/campaigns/validate/csv/${id}`,
      params
    );
  }



  uploadFile(file: File, options: CsvUploadOptions): Observable<HttpEvent<any>> {

    let params = new HttpParams();

    Object.entries(options).forEach(([key, value]) => {
      if (value !== undefined) params = params.set(key, value.toString());
    });

    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.cxmCampaignService.upload(
      `${env.campaignContext}/storage/store`,
      formData,
      params
    );
  }

  removeFile() {
    const campaignDestinationModel: CampaignDestinationModel = JSON.parse(<string>localStorage.getItem(CAMPAIGN_DESTINATION_LOCAL_STORAGE));
    if (campaignDestinationModel !== null) {
      return this.cxmCampaignService.get(`${env.campaignContext}/storage/remove/${campaignDestinationModel.fileName}`);
    }
    return;
  }

  /**
   * Method used to save object of {@link CampaignModel} to storage.
   * @param emailCampaign
   * return object of {@link CampaignModel}
   */
  addEmailCampaign(emailCampaign: CampaignModel): Observable<CampaignModel> {
    return this.cxmCampaignService.post(`${env.campaignContext}/campaigns`, emailCampaign);
  }

  /**
   * Method used to update object of {@link CampaignModel} in storage.
   * @param emailCampaign
   * return object of {@link CampaignModel}
   */
  updateEmailCampaign(emailCampaign: CampaignModel): Observable<any> {
    return this.cxmCampaignService.put(`${env.campaignContext}/campaigns`, emailCampaign);
  }

  /**
   * Method used to get all email campaign.
   * @param page
   * @param pageSize
   * @param sortByField
   * @param sortDirection
   * @param filter
   * @param type refer to type of campaign (EMAIL, SMS)
   * return object of {@param mode
@link EmailCampaignList}
   */
  getAllEmailCampaign(page: number, pageSize: number, sortByField?: string, sortDirection?: string, filter?: string, type?: string, mode?: string): Observable<CampaignList> {
    return this.cxmCampaignService.getList(`${env.campaignContext}/campaigns`, page, pageSize, sortByField, sortDirection, filter, type, mode);
  }

  /**
   * Method used to delete one record in storage.
   * @param id
   */
  deleteEmailCampaign<T>(id: number): Observable<T> {
    return this.cxmCampaignService.patch(`${env.campaignContext}/campaigns/${id}`);
  }

  /**
   * Method used to get one object of {@link CampaignModel}.
   * @param id
   * return Object of {@link CampaignModel}
   */
  getEmailCampaignById(id: number): Observable<CampaignModel> {
    return this.cxmCampaignService.get(`${env.campaignContext}/campaigns/${id}`);
  }

  /**
   * Method used to change status of {@link CampaignModel}
   * @param id
   * @param status
   */
  changeEmailCampaignStatus(id: number, status: string): Observable<any> {
    return this.cxmCampaignService.patch(`${env.campaignContext}/campaigns/status-change/${id}/${status}`);
  }

  /**
   * Method used to get csv of email campaign.
   * @param filename
   */
  getEmailCampaignCsv(filename: string, csvPath: string): Observable<Blob> {
    let params$ = new HttpParams();
    params$ = params$.set('dir', csvPath);
    return this.cxmCampaignService.getFile(`${env.campaignContext}/storage/file/${filename}`, params$);
  }

  /**
   * Method used to send email for testing.
   * @param emailCampaignModel
   * @returns
   */
  sendTestMail(emailCampaignModel: CampaignModel): Observable<any> {
    return this.cxmCampaignService.post(`${env.campaignContext}/campaigns/mail/send/test`, emailCampaignModel);
  }


  sendTestSms(payload: CampaignSmsSendTestModel): Observable<any> {
    return this.cxmCampaignService.post(`${env.campaignContext}/campaigns/sms/send/test`, payload);
  }


  getCsvRecord(page: number, pageSize: number, options: {
    dir: string;
    fileName: string;
    templateId: string;
    type: 'SMS' | 'EMAIL';
    sortByField: string;
    sortDirection: 'DESC'|'ASC'|string

    isCount?: boolean;
    countryCode?: string
    removeDuplicate?: boolean;
    hasHeader?: boolean;
  }): Observable<any> {
    let params = new HttpParams();
    Object.entries(options).forEach(([key, value]) => {
      if (value !== undefined) params = params.set(key, value.toString());
    });

    if (params.get('fileName') && params.get('templateId')) {
      pageSize = pageSize > 1 ? pageSize : 15;
      return this.cxmCampaignService.get(`${env.campaignContext}/campaigns/read-csv/${page}/${pageSize}`, params);
    } else {
      return of({});
    }
  }

  updateStepByCampaignProcess(uuid: string, step: number, type: string): Observable<any>{
    let params$ = new HttpParams();
    params$ = params$.set('type', type);
    return this.cxmCampaignService.post(`${env.processCtrContext}/process-control/leave-campaign/${uuid}/${step}`, params$);
  }

  downloadCsvFile(campaignId: number){
    return this.cxmCampaignService.getFile(`${env.campaignContext}/campaigns/download/csv/${campaignId}`, new HttpParams());
  }

  getLimitUploadFileSize(): Observable<any> {
    return this.cxmCampaignService.getLimitUploadSize(`${env.fileManagerContext}/file-manager/max-size-of-file-upload`);
  }

  getMetadata(type: Array<MetadataPayloadType>): Observable<MetadataResponseModel> {

    const params = new HttpParams()
      .set('types', type.join(','));

    return this.cxmSettingService.get(`${settingEnv.settingContext}/setting/channel-metadata`, params);
  }

  /**
   * Get list template both (sms or email).
   * We used this service for choice of model page.
   * @param page
   * @param pageSize
   * @param sortByField
   * @param sortDirection
   * @param filter
   * @param templateType
   * @return object of {@Observable}
   */
  getListTemplate(page?: number,
                  pageSize?: number,
                  sortByField = '',
                  sortDirection = '',
                  filter = '',
                  templateType = ''): Observable<TemplateList>{
    let params$ = new HttpParams();

    params$ = params$
      .set('sortByField', sortByField)
      .set('sortDirection', sortDirection)
      .set('filter', filter)
      .set('templateType', templateType);

    return this.cxmTemplateService.get(
      `${envTemplate.templateContext}/templates/${page}/${pageSize}`,
      params$
    );
  }

  /**
   * Get list template for choose template model.
   * @param page
   * @param pageSize
   * @param sortByField
   * @param sortDirection
   * @param filter
   * @param templateType
   * @return observable of type (@link TemplateList).
   */
  getChooseTemplateModel(page?: number,
                         pageSize?: number,
                         sortByField = '',
                         sortDirection = '',
                         filter = '',
                         templateType = ''): Observable<TemplateList> {
    let params$ = new HttpParams();
    params$ = params$
      .set('sortByField', sortByField)
      .set('sortDirection', sortDirection)
      .set('filter', filter)
      .set('templateType', templateType);

    return this.cxmTemplateService.get(
      `${envTemplate.templateContext}/templates/choose-model/${page}/${pageSize}`,
      params$
    );
  }

  uploadAttachments(formData: FormData): Observable<any>{
    return this.cxmCampaignService.upload(`${env.campaignContext}/storage/attachment/stores`, formData);
  }

  removeAttachment(fileIds: string[]): Observable<any>{
    let param$ = new HttpParams();
    if(fileIds.length > 0){
      param$ = param$.append('fileIds', fileIds.join(', '));
    }

    return this.cxmCampaignService.deleteWithParam(`${env.campaignContext}/storage/attachments`, param$);
  }
}
