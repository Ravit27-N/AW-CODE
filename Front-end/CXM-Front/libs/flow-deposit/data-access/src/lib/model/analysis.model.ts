export interface AnalysisModel {
  document?: string | number,
  numberOfPages?: number | string,
  resultAnalysis?: string,
  channel?: string,
  reception?: string,
  docUuid?:string,
  addresses?: Addresses,
  modify?:boolean,
  address?:string,
  numberOfSet?: number | string,
  page?:number
}

export interface Addresses {
  Line1?: string,
  Line2?: string,
  Line3?: string,
  Line4?: string,
  Line5?: string,
  Line6?: string,
  Line7?: string,

  [key: string]: string | undefined;
}
