/**
 * It is used rename an original file to signed file by 'signed'
 * **/
export function renameOriginalFile(fileName: string): string {
  const apartFileName = fileName.split('.');
  let str = '';
  for (let i = 0; i < apartFileName.length - 1; i++) {
    str += apartFileName[i];
  }

  return str + '_signed.' + apartFileName[apartFileName.length - 1];
}

export const base64toBlob = (base64Data: string, contentType: string): Blob => {
  contentType = contentType || 'image/jpeg';
  const sliceSize = 1024;
  const byteCharacters = window.atob(base64Data.split(',')[1]);
  const bytesLength = byteCharacters.length;
  const slicesCount = Math.ceil(bytesLength / sliceSize);
  const byteArrays = new Array(slicesCount);

  for (let sliceIndex = 0; sliceIndex < slicesCount; ++sliceIndex) {
    const begin = sliceIndex * sliceSize;
    const end = Math.min(begin + sliceSize, bytesLength);

    const bytes = new Array(end - begin);
    for (let offset = begin, i = 0; offset < end; ++i, ++offset) {
      bytes[i] = byteCharacters[offset].charCodeAt(0);
    }
    byteArrays[sliceIndex] = new Uint8Array(bytes);
  }
  return new Blob(byteArrays, {type: contentType});
};
