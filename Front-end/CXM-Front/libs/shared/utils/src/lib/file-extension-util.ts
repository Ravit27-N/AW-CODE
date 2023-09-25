import {StringUtil} from "./string-util";

export class FileExtensionUtil {
  public static DOT_SEPARATOR = ".";
  public static COMMA_SEPARATOR = ",";
  public static COMMA_SPACE_SEPARATOR = ", ";
  public static readonly PDF_EXTENSION = "pdf";
  public static readonly ZIP_EXTENSION = "zip";

  public static EXCEl_EXTENSIONS = ['csv', 'ods','xls','xlsx'];

  public static WORD_EXTENSIONS = ['doc', 'docx','odt'];

  public static PDF_EXTENSIONS = [
    FileExtensionUtil.PDF_EXTENSION
  ];

  public static ZIP_EXTENSIONS = [
    FileExtensionUtil.ZIP_EXTENSION
  ];

  public static IMAGE_EXTENSIONS = ['png', 'gif', 'jpeg'];

  public static getAll() {
    return [...this.EXCEl_EXTENSIONS,
      ...this.WORD_EXTENSIONS,
      ...this.PDF_EXTENSIONS,
      ...this.ZIP_EXTENSIONS,
      ...this.IMAGE_EXTENSIONS
    ];
  }

  public static getExtensions(extensions: Array<string>): string {
    const dotExtensions = extensions.map(extension => this.DOT_SEPARATOR.concat(extension).toString());
    return StringUtil.replaceAll(dotExtensions.toString(), this.COMMA_SEPARATOR, this.COMMA_SPACE_SEPARATOR);
  }
}
