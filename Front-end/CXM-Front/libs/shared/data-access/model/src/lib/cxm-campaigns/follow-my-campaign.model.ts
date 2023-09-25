export interface campaignModel {
  id?: number;
  firstName?: string;
  lastName?: string;
  date?: Date;
  email?: string;
  modifieddDate?: Date;
  status?: string;
  templateName?: string;
  emailId?: string;
}

export interface campaignList {
  contents?: campaignModel[];
  page?: number;
  pageSize?: number;
  total?: number;
}


export interface ResponseDataAfterUpdateStatus{
  createdAt?: Date,
  lastModified?: Date,
  createdBy?: Date,
  lastModifiedBy?: Date,
  id?: number,
  emailId?: number,
  status?: string,
  template?: {
     name?: string
  },
  email?: {
     isPublished?: number,
     replyToAddress?: string
  },
  contacts?: [
     {
        contact?: {
           firstname?: string,
           lastname?: string
        },
        tokens?: {
           nom?: string,
           prenom?: string,
           email?: string,
           webview_url?: string,
           unsubscribe_url?: string
        }
     }
  ]

}

export interface ParameterModel {
  id?: number;
  modelSelection: string;
  subjectMail: string;
  campaignName: string;
  sender: string;
  datetime: Date;
  fileName?: string
  variables: string[],
  htmlTemplate: string,
  senderMail: string,
  unsubscribeLink: string,
}

