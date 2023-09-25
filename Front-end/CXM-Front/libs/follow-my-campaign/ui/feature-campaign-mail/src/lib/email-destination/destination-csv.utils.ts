const splitPattern = /,(?=(?:[^"]*"[^"]*")*[^"]*$)/;
const removeDoubleQuotesPattern = /['"]+/g;

export const REGEXP = {
  splitPattern,
  removeDoubleQuotesPattern
};


export const isValidCsvFile = (file: File) => file.name.endsWith('.csv');

export const getTableContentMeta = (records: any[]) => {
  const columns = records
    .reduce((columns, row) => {
      return [...columns, ...Object.keys(row)];
    }, [])
    .reduce((columns: string | any[], column: any) => {
      return columns.includes(column) ? columns : [...columns, column];
    }, []);

  return columns.map((column: string | number) => {
    return {
      columnDef: column,
      header: column,
      cell: (element: any) => `${element[column] ? element[column] : ``}`,
    };
  });
}

export const getTableContentMeta2 = (columns: Array<string|number>): Array<any> =>
columns.map((column: string | number) => {
  return {
    columnDef: column,
    header: column,
    cell: (element: any) => `${element[column] ? element[column] : ``}`,
  };
});

const getDataRecordsArrayFromCSVFile = (
  csvRecordsArray: string[],
  limit?: number
) => {
  const csvArr = [];
  for (let i = 1; i < csvRecordsArray.length; i++) {
    const currentRecord = (<string>csvRecordsArray[i]).split(
      REGEXP.splitPattern
    );
    csvArr.push(currentRecord);
    if (limit != undefined && i == limit) break; // set limit of item
  }
  return csvArr;
};


export const pattern = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
/**
 * Method used to validate email address.
 * @param email
 * @returns value of boolean.
 */
export const validateEmail = (email: string): boolean => {
  return pattern.test(String(email).toLowerCase());
 }
