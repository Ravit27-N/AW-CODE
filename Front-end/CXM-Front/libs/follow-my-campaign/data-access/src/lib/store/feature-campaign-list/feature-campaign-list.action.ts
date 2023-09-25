import { CampaignList, CampaignModel } from '../../models';
import { createAction, props } from '@ngrx/store';

export const loadFeatureCampaignList = createAction(
  '[follow my campaign/Load feature campaign list]',
  props<{
    page: number,
    pageSize: number,
    sortByField?: string,
    sortDirection?: string,
    filter?: string,
    _type?: string,
    mode?: string
  }>()
);

export const loadFeatureCampaignListSuccess = createAction(
  '[follow my campaign/Load feature campaign list success]',
  props<{
    response: CampaignList,
    isLoading?: boolean;
  }>()
);

export const loadFeatureCampaignListFail = createAction(
  '[follow my campaign/Load feature campaign list fail]',
  props<{
    error?: string;
    isLoading?: boolean;
  }>()
);

export const cancelCampaign = createAction('[Campaign] / cancel campaign', props<{ id: number, status: string }>());
export const cancelCampaignSuccess = createAction('[Campaign] / cancel campaign success');
export const cancelCampaignFail = createAction('[Campaign] / cancel campaign fail');

export const gotoEmailCampaignDetail = createAction('[Campaign] / navigate to email detail', props<{id: number, ownerId: number}>());
export const gotoSmsCampaignDetail = createAction('[Campaign] / navigate to sms detail', props<{id: number, ownerId: number}>());

export const gotoUpdateEmail = createAction('[Campaign] / navigate to update email', props<{ id: number }>());
export const getEmailCampaignDetail = createAction('[Campaign] / get email campaign detail', props<{ id: number }>());
export const getEmailCampaignDetailSuccess = createAction('[Campaign] / get email campaign detail success', props<{ campaign: CampaignModel }>());

export const gotoUpdateSms = createAction('[Campaign] / navigate to update sms', props<{ id: number }>());
export const getSMSCampaignDetail = createAction('[Campaign] / get SMS campaign detail', props<{ id: number }>());
export const getSMSCampaignDetailSuccess = createAction('[Campaign] / get SMS campaign detail success', props<{ campaign: CampaignModel }>());

export const gotoCreateCampaign = createAction('[Campaign] / navigate to crate campaign');

export const downloadCsvFile = createAction('[Campaign] / download csv file', props<{ campaign: CampaignModel }>());
export const downloadCsvFileSuccess = createAction('[Campaign] / download csv file success', props<{ file: any, campaign?: CampaignModel }>());
export const downloadCsvFileFail = createAction('[Campaign] / download csv file fail');

export const doLoadCampaignList = createAction('[Campaign] do load campaign list');
