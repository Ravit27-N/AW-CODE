import { EntityResponseHandler } from '../response-handler-model';
import { TemplatePrivilegeModel } from '../privilege.model';

export interface TemplateModel {
  id?: number | null;
  modelName: string;
  subjectMail: string;
  senderMail: string;
  senderName: string;
  unsubscribeLink: string;
  variables?: string[];
  createdAt?: Date;
  active?: boolean;
  fileName?: string;
  createdBy?: string;
  lastModified?: string;
  templateType?: string;
  htmlFile?: string;
  imgUrl?: string;
  downloadOption?: string [];
  privilege?: TemplatePrivilegeModel;
  ownerId?: number;
}

export type TemplateList = EntityResponseHandler<TemplateModel>;
