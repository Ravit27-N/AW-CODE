import { TranslateService } from '@ngx-translate/core';
import { ErrorValidationDirectiveModel } from '@cxm-smartflow/directory-feed/util';
import {
  ErrorType,
  FeedField,
} from '@cxm-smartflow/directory-feed/data-access';

export class DirectoryFeedErrorHandler {
  private directoryFeedMessageError: any;
  private translateType: () => any;
  private cellErrors = new Map<string, { value: boolean; message: string }>();

  constructor(private translate: TranslateService) {
    this.translate
      .get('directory.directory_feed_details_error')
      .subscribe((message: any) => {
        this.directoryFeedMessageError = message;
      });
  }

  set(
    cellName: string,
    error: ErrorValidationDirectiveModel,
    column: FeedField
  ) {
    if (error.required) {
      this.cellErrors.set(cellName, {
        value: error.required,
        message: this.getCellMessageError(ErrorType.REQUIRED, column),
      });
    } else if (error.maximum) {
      this.cellErrors.set(cellName, {
        value: error.maximum,
        message: this.getCellMessageError(ErrorType.MAX, column),
      });
    } else if (error.mask) {
      this.cellErrors.set(cellName, {
        value: error.mask,
        message: this.getCellMessageError(ErrorType.MASK, column),
      });
    } else if (error.dateType) {
      this.cellErrors.set(cellName, {
        value: error.dateType,
        message: this.getCellMessageError(ErrorType.DATA_TYPE, column),
      });
    } else {
      this.cellErrors.set(cellName, { value: false, message: '' });
    }

    if (error.duplicate) {
      this.cellErrors.set(cellName, {
        value: error.duplicate,
        message: this.getCellMessageError(ErrorType.DUPLICATE, column),
      });
    }
  }

  get(cellName: string) {
    return this.cellErrors.get(cellName);
  }

  entries() {
    return this.cellErrors.entries();
  }

  private getCellMessageError(type: ErrorType, column: FeedField) {
    const field = column.field;
    let format = column.properties.option?.mask;

    if (
      'date' === column.type.toLowerCase() &&
      column.properties.option.length
    ) {
      format = 'DD/MM/YYYY HH:mm:ss';
    }

    switch (type) {
      case ErrorType.MASK:
        return this.directoryFeedMessageError['format']
          .replace('${field_name}', field)
          .replace('${field_format}', format);
      case ErrorType.REQUIRED:
        return this.directoryFeedMessageError['require'].replace(
          '${field_name}',
          field
        );
      case ErrorType.DATA_TYPE: {
        this.translateDataType(column.type);
        return this.directoryFeedMessageError['format']
          .replace('${field_name}', field)
          .replace('${field_format}', this.translateType());
      }
      case ErrorType.MAX:
        return this.directoryFeedMessageError['length']
          .replace('${field_name}', field)
          .replace('${field_length}', column.properties.option.length || 255);
      case ErrorType.DUPLICATE:
        return this.directoryFeedMessageError['duplicate'];
    }
  }

  private translateDataType(dataType = '') {
    dataType = dataType.toLowerCase();
    this.translate
      .get(
        'directory.definition_directory_create_step_2_section_field_properties_data_types'
      )
      .subscribe((translateType: any) => {
        this.translateType = () => {
          switch (dataType) {
            case 'number':
              return translateType['directory_data_type_number'];
            case 'boolean':
              return translateType['directory_data_type_boolean'];
            case 'string':
              return translateType['directory_data_type_string'];
            case 'integer':
              return translateType['directory_data_type_integer'];
            case 'date':
              return translateType['directory_data_type_date'];
            default:
              return dataType;
          }
        };
      });
  }
}
