import {
  ResponseModel as Response,
  TemplateModel,
} from '@cxm-smartflow/shared/data-access/model';
import { GrapeJs } from './grapejs.model';

export interface Validator {
  maxLength?: string;
  required?: string;
  email?: string;
}

export const requestBody: TemplateModel = {
  modelName: 'MAIL_Template2021',
  subjectMail: '{nom}',
  senderMail: 'test',
  senderName: 'test',
  unsubscribeLink: '',
  variables: ['nom'],
};

export const responseBody = requestBody;
export const mockErrorResponse: Response = {
  status: 400,
  statusText: 'Bad Request',
  response: null,
  message: 'The Model name field can not be empty',
};

export const mockCreateSuccessResponse: Response = {
  status: 200,
  statusText: 'Success',
  response: requestBody,
};

export const mockUpdateSuccessResponse: Response = {
  status: 200,
  statusText: 'OK',
  response: requestBody,
};

export const mockUpdateFailResponse: Response = {
  status: 400,
  statusText: 'Bad Request',
  response: null,
};

export const maxLengthValidator: Validator = {
  maxLength: 'Maximum length must be smaller or equal to 128 character !',
};

export const requiredValidator: Validator = {
  required: 'This field is required!',
};

export const emailValidator: Validator = {
  email: '',
};

export const mockDeleteSuccessResponse: Response = {
  status: 200,
  statusText: 'OK',
};

const mockEmailTemplateCompositionResponse: GrapeJs = {
  html: '<h1>Test</h1>',
  css: '',
  assets: null,
  styles: '',
  components: null,
  htmlFile: '<html> <body> <h1>Test</h1> </body> </html>',
};

export const mockCreateEmailTemplateCompositionSuccessResponse: Response = {
  status: 200,
  statusText: 'OK',
  response: mockEmailTemplateCompositionResponse,
};

export const mockCreateEmailTemplateCompositionFailResponse: Response = {
  status: 404,
  statusText: 'Not Found',
  response: null,
};

export const mockUpdateEmailTemplateCompositionSuccessResponse: Response = {
  status: 200,
  statusText: 'OK',
  response: mockEmailTemplateCompositionResponse,
};

export const mockUpdateEmailTemplateCompositionFailResponse: Response = {
  status: 404,
  statusText: 'Not Found',
  response: null,
};

export const mockGetEmailTemplateCompositionByIdSuccess: Response = {
  status: 200,
  statusText: 'OK',
  response: mockEmailTemplateCompositionResponse,
};

export const mockGetEmailTemplateCompositionByIdFail: Response = {
  status: 404,
  statusText: 'Not Found',
  response: null,
};
