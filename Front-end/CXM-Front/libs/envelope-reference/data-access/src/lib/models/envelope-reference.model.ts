export interface BaseEnvelopeReference {
  format: Format;
  description: string;
  active: boolean
}

export interface CreateEnvelopeReference  extends  BaseEnvelopeReference{
  reference: string;
}
export interface UpdateEnvelopeReference extends BaseEnvelopeReference{
  id: number;
  reference?: string;
}

export interface ResponseEnvelopeReference extends BaseEnvelopeReference{
  id: number;
  ownerId: number;
  createdAt: Date;
  createdBy: string;
  lastModified: Date;
  lastModifiedBy: string;
  reference: string;
}

export interface PageEnvelopeReference {
  content: Array<ResponseEnvelopeReference>;
  totalElements: number;
  size: number;
}

export interface SearchPageEnvelopeReference{
  keyword: string;
  page:number;
  size: number;
  sort: string[];
}

export enum Format {
  C4 = 'C4',
  C5 = 'C5',
  C6 = 'C6'
}
