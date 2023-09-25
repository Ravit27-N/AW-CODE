import { htmlToText } from './model';
import { CandidateReportModel } from './model';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

type ExportDateFormatter = (value: any) => string;

const getAge = (date: string) => {
  const age = getAge(date);
  return isNaN(age) || age <= 0 ? 'N/A' : age;
};

const getGpa = (gpa) => (!gpa || gpa <= 0 ? 'N/A' : gpa);
const getExp = (exp) => !exp || 'N/A';

export const exportToXlsx = (
  filename: string,
  data: CandidateReportModel[],
  formatterFunction: ExportDateFormatter = (value) => value,
) => {
  const workbook = XLSX.utils.book_new();
  workbook.Props = {
    // eslint-disable-next-line @typescript-eslint/naming-convention
    Title: 'Candidate Report',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    Subject: 'Candidate Report',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    Author: 'Recruiter',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    CreatedDate: new Date(),
  };
  const sheetdata = data.map((item) => [
    item.fullName,
    item.gender,
    getAge(item.dateOfBirth),
    item.telephone,
    item?.universities
      ?.map((x) => (x.name !== undefined ? x.name : ''))
      .join(', '),
    item.priority,
    getGpa(item.gpa),
    getExp(item.yearOfExperience),
    formatterFunction(item.interviews?.dateTime),
    item.interviews?.title,
    item.interviews?.result?.score?.quiz?.score,
    item.interviews?.result?.score?.coding?.score,
    item.interviews?.result?.average,
    item.interviews?.result?.english,
    item.interviews?.result?.logical,
    item.interviews?.result?.flexibility,
    item.interviews?.result?.oral,
    (item.interviews.description = htmlToText(item.interviews?.description)),
  ]);

  sheetdata.unshift([
    'Full Name',
    'Gender',
    'Age',
    'Phone',
    'University',
    'Priority',
    'GPA',
    'Experience',
    'Interviewed',
    'Apply For',
    'Quiz',
    'Coding',
    'Average',
    'English',
    'Logical',
    'Flexibility',
    'Oral',
    'Remark',
  ]);

  const candidateSheet = XLSX.utils.aoa_to_sheet(sheetdata);
  candidateSheet['!cols'] = [
    { width: 20 },
    { width: 20 },
    { width: 5 },
    { width: 20 },
    { width: 30 },
    { width: 20 },
    { width: 5 },
    { width: 10 },
    { width: 20 },
    { width: 20 },
    { width: 20 },
    { width: 20 },
    { width: 20 },
    { width: 20 },
    { width: 20 },
    { width: 20 },
    { width: 20 },
    { width: 20 },
  ];
  workbook.SheetNames.push('Candidate');
  workbook.Sheets.Candidate = candidateSheet;
  const sheet = XLSX.write(workbook, { bookType: 'xlsx', type: 'binary' });
  return saveAs(
    new Blob([s2ab(sheet)], { type: 'application/octet-stream' }),
    filename,
  );
};

const s2ab = (arrayData) => {
  const buf = new ArrayBuffer(arrayData.length); // convert s to arrayBuffer
  const view = new Uint8Array(buf); // create uint8array as viewer
  for (let i = 0; i < arrayData.length; i++) {
    // eslint-disable-next-line no-bitwise
    view[i] = arrayData.charCodeAt(i) & 0xff;
  }
  return buf;
};

export const exportToCsv = (
  filename: string,
  data: CandidateReportModel[],
  formatterFunction: ExportDateFormatter = (value) => value,
) => {
  const workbook = XLSX.utils.book_new();
  workbook.Props = {
    // eslint-disable-next-line @typescript-eslint/naming-convention
    Title: 'Candidate Report',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    Subject: 'Candidate Report',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    Author: 'Recruiter',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    CreatedDate: new Date(),
  };

  const sheetData = data.map((item) => [
    item?.fullName,
    item?.gender,
    getAge(item.dateOfBirth),
    item?.telephone,
    item?.universities
      ?.map((x) => (x.name !== undefined ? x.name : ''))
      .join(', '),
    item?.priority,
    getGpa(item.gpa),
    getExp(item.yearOfExperience),
    formatterFunction(item.interviews?.dateTime),
    item?.interviews?.title,
    item?.interviews?.result?.score?.quiz?.score,
    item?.interviews?.result?.score?.coding?.score,
    item?.interviews?.result?.average,
    item?.interviews?.result?.english,
    item?.interviews?.result?.logical,
    item?.interviews?.result?.flexibility,
    item?.interviews?.result?.oral,
    (item.interviews.description = htmlToText(item.interviews?.description)),
  ]);

  sheetData.unshift([
    'Full Name',
    'Gender',
    'Age',
    'Phone',
    'University',
    'Priority',
    'GPA',
    'Experience',
    'Interviewed',
    'Apply For',
    'Quiz',
    'Coding',
    'Average',
    'English',
    'Logical',
    'Flexibility',
    'Oral',
    'Remark',
  ]);

  const candidateSheet = XLSX.utils.aoa_to_sheet(sheetData);
  workbook.SheetNames.push('Candidate');
  workbook.Sheets.Candidate = candidateSheet;
  const sheet = XLSX.write(workbook, { bookType: 'csv', type: 'binary' });
  return saveAs(
    new Blob([s2csv(sheet)], { type: 'application/octet-stream' }),
    filename,
  );
};

const s2csv = (arrayData) => {
  const buf = new ArrayBuffer(arrayData.length); // convert s to arrayBuffer
  const view = new Uint8Array(buf); // create uint8array as viewer
  for (let i = 0; i < arrayData.length; i++) {
    // eslint-disable-next-line no-bitwise
    view[i] = arrayData.charCodeAt(i) & 0xff;
  }
  return buf;
};
