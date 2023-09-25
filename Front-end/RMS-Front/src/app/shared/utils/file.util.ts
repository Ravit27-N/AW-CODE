export class FileUtil {
  /**
   * Get the file extension from a given filename.
   *
   * @param filename The filename from which to extract the extension.
   * @returns The file extension (without the dot).
   */
  static getFileExtension(filename: string): string {
    const lastIndex = filename.lastIndexOf('.');
    if (lastIndex === -1) {
      return ''; // No extension found
    }
    return filename.slice(lastIndex + 1);
  }

  static formatFileSize(fileSize: number): string {
    if (fileSize < 1024) {
      return `${fileSize} bytes`;
    } else if (fileSize < 1024 * 1024) {
      return `${(fileSize / 1024).toFixed(2)} KB`;
    } else if (fileSize < 1024 * 1024 * 1024) {
      return `${(fileSize / (1024 * 1024)).toFixed(2)} MB`;
    } else if (fileSize < 1024 * 1024 * 1024 * 1024) {
      return `${(fileSize / (1024 * 1024 * 1024)).toFixed(2)} GB`;
    } else {
      return `${(fileSize / (1024 * 1024 * 1024 * 1024)).toFixed(2)} TB`;
    }
  }
}
