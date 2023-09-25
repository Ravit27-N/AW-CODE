export interface InterviewTemplateModel {
  id?: number;
  name: string;
  type?: string;
  active: boolean;
  employees?: Array<number>;
  employee?: Array<EmployeeModel>;
  interviewer?: string;
  remark?: string;
  isUpdated?: boolean;
  createdAt?: Date | number;
  updatedAt?: Date | number;
}

export interface EmployeeInterviewTemplateModel {
  contents?: Array<EmployeeModel>;
  page?: number;
  pageSize?: number;
  total?: number;
}

export interface EmployeeModel {
  id?: number;
  lastName?: string;
  name?: string;
  fullName?: string;
  email?: string;
  phone?: string;
}

export interface InterviewTemplateList {
  contents: InterviewTemplateModel[];
  total: number;
  page: number;
  pageSize: number;
}
