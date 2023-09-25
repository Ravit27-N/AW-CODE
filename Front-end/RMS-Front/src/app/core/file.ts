export const ACCEPT = '.pdf,.doc,.docx';

export const splitFileName = (text: string) => {
  const index = text.indexOf('_', 0);
  const rs = text.slice(index + 1, text.length);
  return rs;
};

export const checkFileType = (respone: any) => {
  const responeType = respone.type;
  const index = responeType.indexOf('/', 0);
  const type = responeType.slice(index + 1, responeType.length);
  return type;
};


export const previewPdfFile = (respone) => {
  const blob = new Blob([respone], { type: 'application/pdf' });
  const url = window.URL.createObjectURL(blob);
  window.open(url);
};

export const downloadDocumentFile = (respone, filename?: string) => {
  const blob = new Blob([respone], { type: 'application/octet-stream' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = filename;
  link.click();
};


export const getFileIcon = (filename: string) => {
  const index = filename.indexOf('.', 0);
  const type = filename.slice(index + 1, filename.length);
  if (type === 'pdf'){
    return 'picture_as_pdf';
  }else{
    return 'description';
  }
};
