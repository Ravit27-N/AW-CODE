export class FileListValidator {
  private readonly fileList: FileList;
  private allowedExtensions: string[] = [];
  private allowedMimeTypes: string[] = [];

  constructor(fileList: FileList) {
    this.fileList = fileList;
  }

  public withAllowedExtensions(extensions: string[]): FileListValidator {
    this.allowedExtensions = extensions;
    return this;
  }

  public withAllowedMimeTypes(mimeTypes: string[]): FileListValidator {
    this.allowedMimeTypes = mimeTypes;
    return this;
  }

  public validate(): boolean {
    const fileArray = Array.from(this.fileList);

    return fileArray.every((file) => {
      if (this.allowedExtensions.length > 0 && !this.isValidExtension(file.name)) {
        return false;
      }

      return !(this.allowedMimeTypes.length > 0 && !this.isValidMimeType(file.type));
    });
  }

  private isValidExtension(filename: string): boolean {
    const extension = filename.split('.').pop()?.toLowerCase();
    return extension ? this.allowedExtensions.includes(extension) : false;
  }

  private isValidMimeType(mimeType: string | undefined): boolean {
    return mimeType ? this.allowedMimeTypes.includes(mimeType) : false;
  }
}
