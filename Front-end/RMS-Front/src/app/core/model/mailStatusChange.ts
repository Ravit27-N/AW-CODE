export interface MailStatuschangeModel{
  id?: number;
  title: string;
  from: string;
  to?: string[];
  cc?: string[];
  candidateStatusId: number;
  mailTemplateId: number;
  active: boolean;
  deleted?: boolean;
}
export interface MailStatuschangelist{
  contents: MailStatuschangeformModel[];
  total: number;
  page: number;
  pageSize: number;
}
export interface MailStatuschangeformModel{
  id: number;
  title: string;
  from: string;
  to: [];
  cc: [];
  candidateStatusId: number;
  mailTemplateId: number;
  mailId: number;
  active: boolean;
  deleted?: boolean;
  link: [
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
