import { KeyValue } from '@angular/common';

export interface CandidateStatusModel {
  id: number;
  title?: string;
  active?: boolean;
}

export interface QuizCandidateModel {
  score: number;
  max: number;
}

export interface CodeCandidateModel {
  score: number;
  max: number;
}

export interface ScoreCandidateModel {
  quiz?: QuizCandidateModel;
  coding: CodeCandidateModel;
}

export interface ResultCandidateModel {
  average?: number | string;
  oral?: string;
  english?: string;
  logical?: string;
  flexibility?: string;
  quizScore?: string | number;
  codingScore?: string | number;
  score?: ScoreCandidateModel;
}

export interface InterviewsCandidateModel {
  id: number;
  createdBy: string;
  title: string;
  description: string;
  dateTime: Date;
  result?: ResultCandidateModel;
}

export interface ActivitiesCandidateModel {
  id: number;
  title: string;
  userId: string;
  description: string;
  updatedAt: Date;
}

export interface UniversityCandidateModel {
  id: number;
  name: string;
}

export interface LinksCandidateModel {
  rel: string;
  href: string;
}

export interface DefaultCriteria {
  pageIndex: number;
  pageSize: number;
  sortByField: string;
  sortDirection: string;
}

export interface CandidateCriteria {
  defaultCriteria: DefaultCriteria;
  filter?: string;
  filterReminderOrInterview?: string;
  status?: string;
  isDeleted?: boolean;
}

export interface CandidateModel {
  id: number;
  firstname: string;
  lastname: string;
  salutation: string;
  gender: string;
  email: string;
  telephone: string;
  fullName: string;
  photoUrl?: any;
  gpa: number;
  priority: string;
  active?: boolean;
  filesCV?: any[];
  description?: string;
  statusId?: number;
  countInterview?: number;
  countReminder?: number;
  dateOfBirth?: Date;
  yearOfExperience?: number | string;
  candidateStatus: CandidateStatusModel;
  interviews?: InterviewsCandidateModel;
  activities?: Array<ActivitiesCandidateModel>;
  universities?: Array<UniversityCandidateModel>;
  arrUniversities?: string;
  createdAt?: Date;
  updatedAt?: Date;
  createdBy?: string;
  deleted?: boolean;
  links?: Array<LinksCandidateModel>;
  candidateExperiences?: Array<any>;
  candidateUniversities?: Array<any>;
  telephones?: Array<any>;
}

export interface CandidateList {
  contents: CandidateModel[];
  total: number;
  page: number;
  pageSize: number;
}

export interface CandidateFormModel {
  id: number;
  firstname: string;
  lastname: string;
  sortName: string;
  salutation: string;
  gender: string;
  dateOfBirth: Date;
  email: string;
  fullName: string;
  photoUrl: string;
  priority: string;
  description: string;
  active: boolean;
  statusId: number;
  countInterview: number;
  cvFileName: string;
  countReminder: number;
  filenames: string[];
  candidateStatus: {
    id: number;
    title: string;
    description: string;
    active: boolean;
    createdAt: Date;
    updatedAt: Date;
    deletable: boolean;
    deleted: boolean;
  };
  interviews: any[];
  activities: any[];
  createdAt: Date;
  updatedAt: Date;
  createdBy: string;
  telephones: string[];
  candidateExperiences: [
    {
      companyName: string;
      position: string;
      startDate: Date;
      endDate: Date;
      level: string;
      projectType: string;
      technology: string;
      remarks: string;
    },
  ];
  candidateUniversities: [
    {
      id: number;
      universityId: number;
      university: {
        id: number;
        name: string;
        address: string;
        createdAt: Date;
        updatedAt: Date;
      };
      degree: string;
      major: string;
      startDate: Date;
      endDate: Date;
      graduate: boolean;
      gpa: number;
      remarks: string;
    },
  ];
  deleted: boolean;
}

export interface CandidateReportModel {
  id?: number;
  firstname?: string;
  lastname?: string;
  salutation?: string;
  dateOfBirth?: string;
  gender?: string;
  email?: string;
  telephone?: string;
  fullName?: string;
  photoUrl?: string | File;
  gpa?: number;
  yearOfExperience: number;
  priority?: string;
  active?: boolean;
  description?: string;
  statusId?: number;
  countInterview?: number;
  countReminder?: number;
  candidateStatus?: {
    id?: number;
    title?: string;
    active?: boolean;
  };
  interviews?: {
    id?: number;
    title?: string;
    description?: string;
    dateTime?: Date;
    result?: {
      id?: number;
      average?: number;
      oral?: string;
      english?: string;
      logical?: string;
      flexibility?: string;
      score?: {
        quiz?: {
          score?: number;
          max?: number;
        };
        coding?: {
          score?: number;
          max?: number;
        };
      };
    };
  };
  activities?: [
    {
      id: number;
      title: string;
      userId: string;
      description: string;
      updatedAt: Date;
    },
  ];
  universities?: [
    {
      id?: number;
      name?: string;
    },
  ];
  arrUniversities?: string;
  createdAt?: Date;
  updatedAt?: Date;
  deleted?: boolean;
}

export interface CandidateListReport {
  contents: CandidateReportModel[];
  total: number;
  page: number;
  pageSize: number;
}

export interface ReportFormData {
  no?: number;
  fullname?: string;
  gender?: string;
  phone?: string;
  from?: string;
  priority?: string;
  gpa?: string;
  lastInterview?: Date;
  applyFor?: string;
  quiz?: number;
  coding?: number;
  average?: number;
  english?: string;
  logical?: string;
  flexibility?: string;
  oral?: string;
  remark?: string;
  dateOfBirth: string;
  yearOfExperience: number;
}

export interface CandidateDetail {
  id: number;
  firstname: string;
  lastname: string;
  salutation: string;
  gender: string;
  email: string;
  telephone: string;
  fullName: string;
  photoUrl?: string;
  gpa: number;
  priority: string;
  active?: boolean;
  filesCV?: any;
  description?: string;
  statusId?: number;
  countInterview?: number;
  countReminder?: number;
  dateOfBirth: string;
  yearOfExperience: string;
  candidateStatus: {
    id: number;
    title?: string;
    active?: boolean;
  };
  interviews?: [
    {
      id: number;
      title: string;
      description: string;
      createdBy: string;
      dateTime: Date;
      result?: {
        id?: number;
        average?: number;
        oral?: string;
        english?: string;
        logical?: string;
        flexibility?: string;
        quiz?: string | number;
        coding?: string | number;
        score?: {
          quiz?: {
            score: number;
            max: number;
          };
          coding: {
            score: number;
            max: number;
          };
        };
      };
    },
  ];

  activities?: [
    {
      id: number;
      title: string;
      userId: string;
      description: string;
      updatedAt: Date;
    },
  ];
  universities?: [
    {
      id: number;
      name: string;
    },
  ];

  arrUniversities?: string;
  createdAt?: Date;
  updatedAt?: Date;
  createdBy?: string;
  deleted?: boolean;
  links?: [
    {
      rel: string;
      href: string;
    },
  ];
}

export interface CandidateDetailList {
  contents: CandidateDetail[];
  total: number;
  page: number;
  pageSize: number;
}

export interface CandidateOnDemand {
  id: number;
  firstname: string;
  lastname: string;
  salutation: string;
  gender: string;
  email: string;
  telephone: string;
  fullname: string;
  photoUrl?: string;
  gpa: number;
  priority: string;
  active?: boolean;
  filesCV?: any;
  description?: string;
  statusId?: number;
  countInterview?: number;
  countReminder?: number;
  dateOfBirth: string;
  yearOfExperience: string;
  // eslint-disable-next-line @typescript-eslint/naming-convention
  candidate_status: {
    id: number;
    title?: string;
    active?: boolean;
  };
  interviews?: [
    {
      id: number;
      title: string;
      description: string;
      dateTime: Date;
      result?: {
        id?: number;
        average?: number;
        oral?: string;
        english?: string;
        logical?: string;
        flexibility?: string;
        quiz?: string | number;
        coding?: string | number;
        score?: {
          quiz?: {
            score: number;
            max: number;
          };
          coding: {
            score: number;
            max: number;
          };
        };
      };
    },
  ];

  activities?: [
    {
      id: number;
      title: string;
      userId: string;
      description: string;
      updatedAt: Date;
    },
  ];
  universities?: [
    {
      id: number;
      name: string;
    },
  ];

  arrUniversities?: string;
  createdAt?: Date;
  updatedAt?: Date;
  deleted?: boolean;
  links?: [
    {
      rel: string;
      href: string;
    },
  ];
}

export interface CandidateOnDemandList {
  contents: CandidateOnDemand[];
  total: number;
  page: number;
  pageSize: number;
}

export interface CandidateAdvanceReportModel {
  id?: number;
  fullname?: string;
  gender?: string;
  gpa?: number;
  dateOfBirth?: string;
  age?: number;
  year_of_experience?: number;
  interviews?: [
    {
      id?: number;
      datetime?: string;
      title?: string;
    },
  ];
  universities?: [
    {
      id?: number;
      name?: string;
    },
  ];
  interviewstatus?: string; // title
  quiz?: number;
  maxquiz?: number;
  coding?: number;
  maxcoding?: number;
  english?: string;
  flexibility?: string;
  logical?: string;
  oral?: string;
  average?: number;
  remark?: string; //description
}

export interface CandidateAdvanceReportList {
  contents: CandidateAdvanceReportModel[];
  total: number;
  page: number;
  pageSize: number;
}

export interface ProfileModel {
  imageURL: string;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  status: string;
  dateOfBirth?: Date;
  description?: string;
  createdBy?: string;
  createdAt?: Date;
  gender?: string;
  details: Array<ProfileDetailsModel>;
}

export interface ProfileDetailsModel {
  title: string;
  value: string | number | Date;
}

export interface NavigationItemModel {
  icon: string;
  title: string;
  link: string;
  queryParams?: any;
  function: () => void;
}
