import { FileExtensionUtil } from '@cxm-smartflow/shared/utils';

export interface FileValidator {
  fileExtensionMessage?: string; // Message for show when invalid extension.

  fileSize?: number; // MB.
  fileSizeMessage?: string; // Message for show when file size invalid.

  fileLimit?: number; // Number of file can import.
  fileLimitMessage?: string; // Message for show when file limit invalid.

  checkDuplicate?: boolean; // If true, we check duplicate files.
  duplicateMessage?: string; // Message for show when file is duplicate.
}

export class FileValidatorUtil {
  /**
   * Convert bytes to mega bytes.
   * @param bytes - size in bytes.
   * @return size as MegaBytes.
   */
  public static bytesToMegaBytes(bytes: number): number {
    return bytes / (1024 * 1024);
  }

  public static getFileExtension(): string []{
    return FileExtensionUtil.getAll();
  }
}

