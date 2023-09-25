import { ResultRankEnum } from './constant';

export interface ResultModel{
  id: number;
  oral: ResultRankEnum | string;
  average: number;
  interviewDate: Date | number;
  interview: { id: number | string; title: string};
  candidate: { id: number | string; fullname: string};
}

export interface ResultList{
  results: ResultModel[];
  total: number;
  page: number;
  pageSize: number;
}

export interface File{
  file?: string;
}

export interface ResultFormModel{
  resultId: number;
  candidateId: number;
  english: ResultRankEnum | string;
  logical: ResultRankEnum | string;
  flexibility: ResultRankEnum | string;
  oral: ResultRankEnum | string;
  score: string | number;
  remark?: string;
  file?: File[];
}
