import { CustomFileModel, EntityResponseHandler, PrivilegeModel } from '@cxm-smartflow/shared/data-access/model';

export interface CampaignModel {
  id?: number,
  templateId?: number | null,
  modelName?: string,
  campaignName?: string,
  subjectMail?: string,
  senderName?: string,
  validate?: boolean,
  sendingSchedule?: Date | string | number,
  details?: {
    variables?: string[],
    htmlTemplate?: string,
    csvPath?: string,
    csvTmpPath?: string,
    csvName?: string,
    csvOriginalName?: string,
    unsubscribeLink?: string,
    senderMail?: string,
    csvRecordCount?: number,
    errorCount?: number,
    csvRecordProcessed?: number,
    csvHasHeader?: boolean,
    removeDuplicate?: boolean
  },
  createdBy?: string,
  status?: string,
  percentage?: number,
  percentageOfEmailSent?: number,
  percentageOfEmailError?: number,
  dateOfStatus?: Date,
  recipients?: number,
  createdAt?: Date,
  ownerId?: number,
  updatedAt?: Date,
  campaignStatus?: {
    status: string;
    statusLabel: string
  },
  recipientAddress?: string[];
  value?: any;
  step?: number;
  type: 'EMAIL' | 'SMS';
  fileId?: string;
  channel?: string;
  mode?: string;
  campaignMode?: {
    modeLabel?: string;
    mode?: string;
  },
  privilege?: PrivilegeModel,
  attachments?: CustomFileModel[]
}

export interface DestinationEmailCampaignModel {
  emails: string[]
}

export  type CampaignList = EntityResponseHandler<CampaignModel>;

export interface CampaignSmsSendTestModel{
  destinations: string[],
  campaignId: number
}

export interface CampaignListFilter{
  page: number,
  pageSize: number,
  sortByField?: string,
  sortDirection?: string,
  filter?: string,
  _type?: string,
  mode?: string
}

export const CampaignType = {
  EMAIL: 'EMAIL',
  SMS: 'SMS'
}
