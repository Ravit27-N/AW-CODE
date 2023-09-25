
import { CampaignModel, CampaignList} from './campaign.model';
import {ResponseModel as Response} from '@cxm-smartflow/shared/data-access/model';

interface FileModel{
  fileName: string,
  originalName: string,
  filePath: string,
  fileSize: number,
  message: string
}

const fileResponse: FileModel = {
  fileName: '976989.csv',
  originalName: 'test.csv',
  filePath: 'tmp',
  fileSize: 127,
  message: 'success'
}

const campaignLists: CampaignList = {
  contents: [
    {
      id: 1,
      modelName: 'MODEL TEST',
      subjectMail: '{nom}',
      senderName: 'Tessi Communication',
      type: 'EMAIL'
    }
  ]
}

export const mockGetAllCampaignsSuccess: Response = {
  status: 200,
  statusText: 'OK',
  response: campaignLists
}

export const mockGetAllCampaignsFail: Response = {
  status: 400,
  statusText: 'Bad Request',
  response: null
}

export  const mockGetListCampaignSuccess: Response = {
  status: 200,
  statusText: 'OK',
  response: campaignLists
}

export  const mockGetListCampaignFail: Response = {
  status: 400,
  statusText: 'Bad Request',
  response: null
}




export const emailCampaign: CampaignModel = {
  templateId: 100,
  modelName: 'Tessi',
  campaignName: 'Testing',
  subjectMail: 'Hello',
  senderName: 'Tessi Communication',
  sendingSchedule: new Date(),
  details: {
    variables: ['email', 'nom'],
    htmlTemplate: '<html> <body> Hello </body> </html>',
    csvPath: 'tmp',
    csvName: 'testing.csv',
    csvOriginalName: 'Testing.csv',
    unsubscribeLink: 'www.testing.com',
    senderMail: 'tessi@gmail.com'
  },
  type: 'EMAIL'
}

export const mockAddEmailCampaignSuccess: Response = {
  status: 200,
  statusText: 'OK',
  response: emailCampaign
}

export const mockAddEmailCampaignFail: Response = {
  status: 400,
  statusText: 'Bad Request',
  response: null
}

export const mockUpdateEmailCampaignSuccess: Response = {
  status: 200,
  statusText: 'OK',
  response: emailCampaign
}

export const mockUpdateEmailCampaignFail: Response = {
  status: 404,
  statusText: 'Not Found',
  response: null
}

export const emailCampaignList: CampaignList = {
  contents: [
    {
    id: 1,
    templateId: 100,
    modelName: 'Testing',
    campaignName: 'Testing',
    subjectMail: 'Hello World',
    senderName: 'Tessi',
    sendingSchedule: new Date(),
    details: {
      variables: ['nom', 'email'],
      htmlTemplate: '<html> <body> Hello World </body> </html>',
      csvName: 'Testing.csv',
      csvPath: 'tmp',
      csvOriginalName: 'Testing.csv',
      unsubscribeLink: 'www.testing.com',
      senderMail: 'Tessi',
    },
    type: 'EMAIL'
  }
],
  page: 1,
  pageSize: 10
}

export const mockGetAllEmailCampaignSuccess: Response = {
  status: 200,
  statusText: 'OK',
  response: emailCampaignList
}

export const mockGetAllEmailCampaignFail: Response = {
  status: 400,
  statusText: 'Bad Request',
  response: null
}

export const mockDeleteEmailCampaignSuccess: Response = {
  status: 200,
  statusText: 'OK'
}

export const mockDeleteEmailCampaignFail: Response = {
  status: 404,
  statusText: 'Not Found'
}

export const mockGetEmailCampaignByIdSuccess: Response = {
  status: 200,
  statusText: 'OK',
  response: emailCampaign
}

export const mockGetEmailCampaignByIdFail: Response = {
  status: 404,
  statusText: 'Not Found',
  response: null
}

export const mockUploadFileSuccess: Response = {
  status: 200,
  statusText: 'OK',
  response: fileResponse
}

export const mockUploadFileFail: Response = {
  status: 400,
  statusText: 'Bad Request',
  response: null
}
