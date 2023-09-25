export class BlobFile {
  static blobToFile(blob: Blob, fileName: string): File {
    const fileOptions: FilePropertyBag = {
      type: blob.type,
      lastModified: Date.now()
    };
    return new File([blob], fileName, fileOptions);
  }

  static saveFile(file: File): void {
    const downloadLink = document.createElement('a');
    downloadLink.href = URL.createObjectURL(file);
    downloadLink.download = file.name;
    downloadLink.click();
  }

  static saveFileFromBase64(base64: string, filename: string): void {
    const source = `data:application/octet-stream;base64,${base64}`;
    const link = document.createElement('a');
    link.href = source;
    link.download = filename || 'file download';
    link.click();
  }
}
