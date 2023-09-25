export interface ITopCandidate {
  fullName?: string;
  gpa?: number;
  photoUrl?: string;
  id?: number;
  gender?: string;
}
export interface IInterviewGraphModel {
  label?: string;
  data: [];
  type?: string;
  fill?: boolean;
}
export interface IListItem {
  totalFailedCandidate: number;
  totalPassedCandidate: number;
  candidates: number;
  results: number;
  interviews: number;
}
