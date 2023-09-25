import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

const s2ab = (s: any) => {
  const buf = new ArrayBuffer(s.length); //convert s to arrayBuffer
  const view = new Uint8Array(buf);  //create uint8array as viewer
  for (let i = 0; i < s.length; i++) view[i] = s.charCodeAt(i) & 0xFF; //convert to octet
  return buf;
}

const generateCsvWithHeader = (schemas: any, fileName: string) => {
  const wb = XLSX.utils.book_new();
  wb.SheetNames.push('shemes');

  const ws = XLSX.utils.aoa_to_sheet([schemas.columns]);
  wb.Sheets['shemes'] = ws;

  XLSX.utils.sheet_to_csv(ws, { FS: ',' });

  const books = XLSX.write(wb, { bookType: 'csv', type: 'binary' });
  saveAs(new Blob([s2ab(books)], { type: 'text/csv;charset=utf-8' }), fileName);
}

const loadCsvAsJson = (file: File) => {

  return new Promise((resolve, reject) => {

    try {
      const reader = new FileReader();
      reader.readAsArrayBuffer(file);

      reader.onload = (e) => {
        const data = new Uint8Array(reader.result as any);
        const wb = XLSX.read(data, { type: 'array' });
        const json = XLSX.utils.sheet_to_json(wb.Sheets['Sheet1']);
        resolve(json);
      }
    } catch (e) { reject(e) }
  })



}

export {
  generateCsvWithHeader,
  loadCsvAsJson
}
