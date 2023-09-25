export interface Education {
  id: number;
  universityId: number;
  academicYearStart: Date;
  academicYearEnd: Date;
  graduate: boolean;
  gpa: number;
  remarks: string;
  major: string;
  degree: string;
}
