import * as XLSX from 'xlsx';
import { IAdvanceSearchResultItem } from './model';
import { saveAs } from 'file-saver';

type ExportDateFormatter = (value: any) => string;

export const exportSearchResult = (
  filename: string,
  data: IAdvanceSearchResultItem[],
  formatterFunction: ExportDateFormatter = (value) => value,
) => {
  const workbook = XLSX.utils.book_new();

  workbook.Props = {
    // eslint-disable-next-line @typescript-eslint/naming-convention
    Title: 'Candidate list',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    Subject: 'Collection of good looking candidates',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    Author: 'Recruiter',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    CreatedDate: new Date(),
  };

  const getAge = (date: string) => getAge(date);

  const na = (value: any) => {
    if (!value || (typeof value === 'number' && value <= 0)) {
      return 'N/A';
    }
    return value;
  };

  const sheetdata = data.map((item, index) => [
    index + 1,
    item.fullName,
    item.gender === 'Female' ? 'F' : 'M',
    na(getAge(item.dateOfBirth)),
    item.telephone,
    item.email,
    na(item.gpa),
    na(item.yearOfExperience),
    item.universities.map((x) => x.name).join(','),
    item.candidateStatus.title,
    item.interviews.title,
    formatterFunction(item.interviews.lastInterview), // change to positiion
    item.interviews?.result?.average,
  ]);
  sheetdata.unshift([
    'No',
    'Candidate',
    'Gender',
    'Age',
    'Telephone',
    'Email',
    'GPA',
    'Experience',
    'University',
    'Status',
    'Position',
    'Last interview',
    'Score',
  ]);

  const candidateSheet = XLSX.utils.aoa_to_sheet(sheetdata);
  candidateSheet['!cols'] = [
    { width: 5 },
    { width: 20 },
    { width: 5 },
    { width: 5 },
    { width: 20 },
    { width: 30 },
    { width: 5 },
    { width: 10 },
    { width: 20 },
    { width: 20 },
    { width: 30 },
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
