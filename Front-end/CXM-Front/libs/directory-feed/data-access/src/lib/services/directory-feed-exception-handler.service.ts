import { Injectable } from '@angular/core';
import { DebugMessage } from '../models';
import { TranslateService } from '@ngx-translate/core';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';

@Injectable({
  providedIn: 'root',
})
export class DirectoryFeedExceptionHandlerService {
  constructor(
    private translateService: TranslateService,
    private snackbarService: SnackBarService
  ) {
    this.translateService.use(localStorage.getItem('locale') || 'fr');
  }

  async handleError(
    statusCode: number,
    debugMsg?: DebugMessage,
    defaultMessage?: string
  ): Promise<void> {
    let messageKey =
      defaultMessage || 'directory.directory_feed_import_csv_fail';
    switch (statusCode) {
      case 4001:
        messageKey =
          'directory.directory_feed_details_upload_invalid_extension';
        break;
      case 4002:
        messageKey = 'directory.directory_feed_details_upload_invalid_max_size';
        break;
      case 4003:
        messageKey =
          'directory.directory_feed_details_upload_invalid_directory_structure';
        break;
      case 4008:
        messageKey =
          'directory.directory_feed_details_upload_has_duplicated_field';
        break;
      case 4004:
        messageKey = 'directory.directory_feed_details_upload_file_empty';
        break;
      case 4005:
      case 4006:
      case 4007:
      case 4009:
      case 4010:
        messageKey =
          'directory.directory_feed_details_upload_invalid_file_structure';
        break;
      case 4000:
        messageKey = 'directory.directory_feed_import_no_file_selected';
        break; // no file selected
    }

    let message = await this.translateService.get(messageKey).toPromise();
    if (statusCode === 4008) {
      message = message?.replace('${directoryKey}', debugMsg?.directory_key);
    }

    const errorStructureCode = [4005, 4006, 4007, 4009, 4010];
    if (errorStructureCode.includes(statusCode)) {
      const typeTranslated = await this.translateDataType(
        debugMsg?.field_data_type
      );
      console.log({ typeTranslated });
      message = message
        ?.replace('${field_name}', debugMsg?.field_name || '')
        .replace('${field_value}', debugMsg?.field_value || '')
        .replace('${field_format}', typeTranslated || '');
    }
    this.snackbarService.openCustomSnackbar({
      icon: 'close',
      type: 'error',
      message,
    });
  }

  private async translateDataType(dataType = ''): Promise<string> {
    dataType = dataType.toLowerCase();
    const translateType = await this.translateService
      .get(
        'directory.definition_directory_create_step_2_section_field_properties_data_types'
      )
      .toPromise();

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
  }
}
