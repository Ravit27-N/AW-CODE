export class FileContentTypeUtil {

  public static EXCEl_CONTENT_TYPE = [
    'text/csv',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'application/vnd.ms-excel'
  ];

  public static WORD_CONTENT_TYPES = [
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  ];

  public static PDF_CONTENT_TYPE = [
    'application/pdf'
  ];

  public static ZIP_CONTENT_TYPES = [
    'application/x-zip-compressed',
    '.zip'
  ];

  public static IMAGE_CONTENT_TYPES = [
    'image/png',
    'image/svg+xml',
    'image/apng',
    'image/avif',
    'image/gif',
    'image/jpeg',
    'image/webp'
  ];
}
