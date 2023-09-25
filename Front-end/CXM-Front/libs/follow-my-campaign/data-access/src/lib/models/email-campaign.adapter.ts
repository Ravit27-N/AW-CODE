import { TemplateModel } from '@cxm-smartflow/shared/data-access/model';
import { CsvFileData, CampaignModel } from '@cxm-smartflow/follow-my-campaign/data-access';

export const getEmailCampaign = (templateDetails: TemplateModel, csvData: CsvFileData, step = 2, hasHeader?: boolean) => {
  const emailCampaign: CampaignModel = {
    templateId: templateDetails?.id || 0,
    modelName: templateDetails?.modelName,
    campaignName: getCampaignName(),
    // subjectMail: "XXXX",
    // senderName: "XXXX",
    // validate: false,
    // sendingSchedule: new Date(),
    details: {
      variables: templateDetails?.variables,
      htmlTemplate: templateDetails?.htmlFile,
      csvPath: csvData?.filePath,
      csvTmpPath: 'tmp',
      csvName: csvData?.fileName,
      csvOriginalName: csvData?.originalName,
      // unsubscribeLink: 'www.tessi.com',
      // senderMail: 'tessi@gmail.com',
      csvRecordCount: csvData?.count,
      // errorCount: 0,
      // csvRecordProcessed: 0,
      csvHasHeader: hasHeader
    },
    // createdBy: '',
    // status: '',
    // percentage: 0,
    // percentageOfEmailSent: 0,
    // percentageOfEmailError: 0,
    // dateOfStatus: new Date(),
    // recipients: 0,
    // createdAt: new Date(),
    // updatedAt: new Date(),
    // campaignStatus: {
    //   status: '',
    //   statusLabel: ''
    // },
    // recipientAddress: [''],
    // value: null
    step: step,
    type: 'EMAIL'
  };
  return emailCampaign;
};

export const getCampaignName = () => 'Emailing'.concat('_').concat(new Date().toLocaleString())
