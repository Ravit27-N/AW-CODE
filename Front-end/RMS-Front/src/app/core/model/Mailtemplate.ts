export interface MailTemplateModel {
  id?: number;
  subject?: string;
  body?: string;
  active?: boolean;
  deleted?: boolean;
  deletable?: boolean;
}

export interface MailTemplateList {
  contents: MailTemplateFormModel[];
  total: number;
  page: number;
  pageSize: number;
}
export interface MailTemplateFormModel {
  id?: number;
  subject?: string;
  body?: string;
  active?: boolean;
  deleted?: boolean;
  deletable?: boolean;
  link?: [
    {
      href: string;
      hreflang: string;
      title: string;
      type: string;
      deprecation: string;
      profile: string;
      name: string;
      templated: boolean;
    }
  ];
}

export interface MailTemplateBodyModel {
  key?: string;
  value?: string;
}
