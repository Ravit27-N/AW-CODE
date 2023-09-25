import {
  KeyValue,
  PositionSetting,
} from '@cxm-smartflow/flow-deposit/data-access';

export class SettingOptionCriteriaConstant {
  public static readonly ADD_RESOURCE_TYPE: Array<KeyValue> = [
    { key: '1', value: 'setting_option_popup_source_type_from_resource'},
    { key: '2', value: 'setting_option_popup_source_type_form_upload' },
  ];

  public static readonly ATTACHMENT_POSITION: Array<PositionSetting> = [
    { key: '1', value: 'setting_option_popup_position_first', mode: 1, val: 'FIRST_POSITION' },
    { key: '2', value: 'setting_option_popup_position_second', mode: 2, val: 'SECOND_POSITION' },
    { key: '3', value: 'setting_option_popup_position_third', mode: 3, val: 'THIRD_POSITION' },
    { key: '4', value: 'setting_option_popup_position_fourth', mode: 4, val: 'FOURTH_POSITION' },
    { key: '5', value: 'setting_option_popup_position_fifth', mode: 5, val: 'FIFTH_POSITION' },
  ];

  public static readonly BACKGROUND_POSITION: Array<PositionSetting> = [
    { key: '1', value: 'setting_option_popup_all_pages', mode: 1, val: 'ALL_PAGES' },
    { key: '2', value: 'setting_option_popup_first_page', mode: 2, val: 'FIRST_PAGE' },
    { key: '3', value: 'setting_option_popup_next_page', mode: 3, val: 'NEXT_PAGES' },
    { key: '4', value: 'setting_option_popup_last_page', mode: 4, val: 'LAST_PAGE' },
  ];

  public static readonly WATERMARK_POSITION: Array<PositionSetting> = [
    { key: '1', value: 'setting_option_popup_all_pages', mode: 1, val: 'ALL_PAGES' },
    { key: '2', value: 'setting_option_popup_original_document_page', mode: 2, val: 'ONLY_DOC' },
    { key: '3', value: 'setting_option_popup_enhancement_page', mode: 3, val: 'OTHER' },
  ];
}
