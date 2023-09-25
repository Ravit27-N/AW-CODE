/**
 * Convert number of bytes into human readable format.
 *
 * @param bytes Size of file as bytes.
 * @param decimals Number of digits after the decimal separator.
 * @returns string.
 */
import {
  appLocalStorageConstant,
  FILE_SIZE_PATTERN,
  FILE_STORAGE_PATTERN,
  NUMBER_PATTERN
} from '@cxm-smartflow/shared/data-access/model';

const formatBytes = (bytes: number, decimals = 2): string => {
  if (bytes === 0) return '0 Bytes';

  const k = 1024;
  const dm = decimals < 0 ? 0 : decimals;
  const sizes = ['Bytes', 'Kb', 'Mb', 'Gb', 'Tb', 'Pb', 'Eb', 'Zb', 'Yb'];

  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
};

const formFragment = (value: number) => Number(value.toFixed(3));

/**
 * User to convert from all storage size type in string to Kilobyte in number.
 * @param fileSize
 */
export const convertToKB = (fileSize: string): number => {

  if (!fileSize?.match(FILE_STORAGE_PATTERN)) return 0;
  const storageType = fileSize.split(NUMBER_PATTERN).filter(e => e)[0];
  const storageSize = fileSize.split(FILE_SIZE_PATTERN).filter(e => e)[0];

  switch (storageType) {
    case 'B': {
      return formFragment(Number(storageSize) / 1024);
    }
    case 'KB': {
      return formFragment(Number(storageSize));
    }
    case 'GB': {
      return formFragment(Number(storageSize) * 1048576);
    }
    case 'MB': {
      return formFragment(Number(storageSize) * 1024);
    }
    case 'TB': {
      return formFragment(Number(storageSize) * 1073741824);
    }
    default : {
      return 0;
    }
  }
};

export const getLimitSize = (size: string): string => {
  const isEng = localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) === 'en';
  if(isEng) {
    return size?.toLowerCase()
      .replace('mb', ' Mb')
      .replace('bytes', ' bytes')
      .replace('byte', ' byte')
      .replace('gb', ' Gb')
      .replace('tb', ' Tb')
      .replace('kb', ' Kb');
  }

  return size?.toLowerCase()
    .replace('mb', ' Mo')
    .replace('bytes', ' octets')
    .replace('byte', ' octet')
    .replace('gb', ' Go')
    .replace('tb', ' To')
    .replace('kb', ' Ko');
}

/**
 * File Utility.
 */
export const FileUtils = {
  formatBytes,
  convertToKB,
  getLimitSize,
};
