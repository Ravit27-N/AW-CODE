export enum TemplateConstant {
  CXM_TEMPLATE = 'cxm_template',
  LIST = 'cxm_template_list',
  EDIT = 'cxm_template_edit',
  MODIFY = 'cxm_template_modify',
  DELETE = 'cxm_template_delete',
  CREATE = 'cxm_template_create_from_scratch',
  DUPLICATE = 'cxm_template_create_by_duplicate',
}

export enum CampaignConstant {
  // Email campaign.
  CXM_CAMPAIGN = 'cxm_campaign',
  LIST = 'cxm_campaign_list',
  SELECT_AND_VIEW = 'cxm_campaign_select_and_view',
  VIEW_EMAIL = 'cxm_campaign_view_the_email',
  VIEW_CONTACTS = 'cxm_campaign_view_the_contacts',
  CANCEL = 'cxm_campaign_cancel',
  FINALIZE = 'cxm_campaign_finalize',
  CHOOSE_MODEL = 'cxm_campaign_choose_model',
  CREATE = 'cxm_campaign_create',
  TEST_SEND_MAIL = 'cxm_campaign_test_send_mail',

  // SMS campaign.
  CXM_CAMPAIGN_SMS = 'cxm_sms_campaign',
  LIST_SMS = 'cxm_sms_campaign_list',
  CANCEL_SMS = 'cxm_sms_campaign_cancel',
  TEST_SEND_SMS = 'cxm_sms_campaign_test_send_mail',
  CHOOSE_MODEL_SMS = 'cxm_sms_campaign_choose_model',
  FINALIZE_SMS = 'cxm_sms_campaign_finalize',
  CREATE_SMS = 'cxm_sms_campaign_create',
  SELECT_AND_VIEW_SMS = 'cxm_sms_campaign_select_and_view',
}

export enum FlowTraceability {
  CXM_FLOW_TRACEABILITY = 'cxm_flow_traceability',
  LIST = 'cxm_flow_traceability_list',
  SELECT_AND_OPEN = 'cxm_flow_traceability_select_and_open',
  CANCEL = 'cxm_flow_traceability_cancel',
  CANCEL_IN_CREATION = 'cxm_flow_traceability_cancel_in_creation',
  CANCEL_SCHEDULED = 'cxm_flow_traceability_cancel_scheduled',
  CANCEL_VALIDATED = 'cxm_flow_traceability_cancel_validated',
  CANCEL_FLOW = 'cxm_flow_traceability_cancel_flow',
  VALIDATE = 'cxm_flow_traceability_validate',
  FINALIZE = 'cxm_flow_traceability_finalize',
  DOWNLOAD = 'cxm_flow_traceability_download',
  VIEW_EVENT_AND_HISTORY = 'cxm_flow_traceability_view_event_and_history',
  // flow document tracking
  VIEW_DOCUMENT = 'cxm_flow_traceability_view_document',
  SELECT_AND_OPEN_DOCUMENT = 'cxm_flow_traceability_select_and_open_document',
  DOWNLOAD_DOCUMENT = 'cxm_flow_traceability_download_document',
  VIEW_EVENT_HISTORY_DOCUMENT = 'cxm_flow_traceability_view_event_history_document',
  OPEN_AND_DOWNLOAD_RELATED_ITEM = 'cxm_flow_traceability_open_and_download_document',
  VALIDATE_DOCUMENT = 'cxm_flow_traceability_validate_document',
  CONSULT_DETAIL_DOCUMENT = 'cxm_flow_traceability_consult_detail_document',
}

export enum UserManagement {
  CXM_USER_MANAGEMENT = 'cxm_user_management',
  LIST_USER = 'cxm_user_management_list_user',
  CREATE_USER = 'cxm_user_management_create_user',
  MODIFY_USER = 'cxm_user_management_modify_user',
  DELETE_USER = 'cxm_user_management_delete_user',
  EDIT_USER = 'cxm_user_management_edit_user',
  ASSIGN_USER_SERVICE = 'cxm_user_management_assign_user_to_service',
  // Profile
  CREATE_PROFILE = 'cxm_user_management_create_profile',
  MODIFY_PROFILE = 'cxm_user_management_modify_profile',
  LIST_PROFILE = 'cxm_user_management_list_profile',
  DELETE_PROFILE = 'cxm_user_management_delete_profile',
  EDIT_PROFILE = 'cxm_user_management_edit_profile',
}

export enum EnvelopeReferenceManagement {
  CXM_REFERENCE_ENVELOPE_MANAGEMENT = "cxm_reference_envelope_management",
  CREATE = 'cxm_reference_envelope_management_create',
  LIST= 'cxm_reference_envelope_management_list',
  DELETE= 'cxm_reference_envelope_management_delete',
  EDIT = 'cxm_reference_envelope_management_edit',
}


export enum ClientManagement {
  CXM_CLIENT_MANAGEMENT = 'cxm_client_management',
  LIST = 'cxm_client_management_list',
  MODIFY = 'cxm_client_management_modify',
}

export enum DirectoryManagement {
  CXM_DIRECTORY_MANAGEMENT = 'cxm_directory',
  // definition directory
  LIST_DEFINITION_DIRECTORY = 'cxm_directory_definition_list_directory',
  CREATE_DEFINITION_DIRECTORY = 'cxm_directory_definition_create_directory',
  EDIT_DEFINITION_DIRECTORY = 'cxm_directory_definition_edit_directory',
  MODIFY_DEFINITION_DIRECTORY = 'cxm_directory_definition_modify_directory',
  DELETE_DEFINITION_DIRECTORY = 'cxm_directory_definition_delete_directory',
  // directory feed
  LIST_DIRECTORY_FEED = 'cxm_directory_feed_list_directory',
  MANUAL_POPULATE_DATA_DIRECTORY_FEED = 'cxm_directory_feed_manual_populate_data',
  IMPORT_DATA_DIRECTORY_FEED = 'cxm_directory_feed_import_data',
  EDIT_DATA_DIRECTORY_FEED = 'cxm_directory_feed_edit_directory',
  MODIFY_DATA_DIRECTORY_FEED = 'cxm_directory_feed_modify_data',
  DELETE_DATA_DIRECTORY_FEED = 'cxm_directory_feed_delete_data',
  EXPORT_DIRECTORY_FEED = 'cxm_directory_feed_export_directory',
}

export enum DepositManagement {
  CXM_FLOW_DEPOSIT = 'cxm_flow_deposit',
  CXM_FLOW_DEPOSIT_LIST_DEPOSITS = 'cxm_flow_deposit_list_deposits',
  CXM_FLOW_DEPOSIT_SEND_A_LETTER = 'cxm_flow_deposit_send_a_letter',
  CXM_FLOW_DEPOSIT_DELETE_A_DEPOSIT = 'cxm_flow_deposit_delete_a_deposit',
  CXM_FLOW_DEPOSIT_MODIFY_A_DEPOSIT = 'cxm_flow_deposit_modify_a_deposit',
  SEND_A_LETTER = 'send_a_letter',
  MODIFY_A_DEPOSIT = 'modify_a_deposit',
  MODIFY_OR_CORRECT_AN_ADDRESS = "cxm_flow_deposit_modify_or_correct_an_address",
}

export enum SmsTemplate {
  CXM_SMS_TEMPLATE = 'cxm_sms_template',
  CREATE_BY_DUPLICATE = 'cxm_sms_template_create_by_duplicate',
  DELETE = 'cxm_sms_template_delete',
  MODIFY = 'cxm_sms_template_modify',
  EDIT = 'cxm_sms_template_edit',
  LIST = 'cxm_sms_template_list',
  CREATE = 'cxm_sms_template_create_from_scratch',
}

export enum Interactive {
  CXM_INTERACTIVE = 'cxm_communication_interactive',
  LIST_MODEL = 'cxm_communication_interactive_list',
}

export enum EspaceValidation {
  CXM_ESPACE_VALIDATION = 'cxm_espace_validation',
  LIST = 'cxm_espace_validation_list',
  VALIDATE_REFUSE = 'cxm_espace_validation_validate_or_refuse',
}

export enum LibraryResourceManagement {
  CXM_MANAGEMENT_LIBRARY_RESOURCE = 'cxm_management_library_resource',
  LIST = 'cxm_management_library_resource_list',
  CREATE = 'cxm_management_library_resource_create',
  DELETE = 'cxm_management_library_resource_delete',
}

export enum EnrichmentMailing {
  CXM_ENRICHMENT_MAILING = 'cxm_enrichment_mailing',
  ADD_RESOURCE = 'cxm_enrichment_mailing_add_resource',
  USE_RESOURCE_IN_LIBRARY = 'cxm_enrichment_mailing_use_resource_in_library',
  UPLOAD_A_SINGLE_RESOURCE = 'cxm_enrichment_mailing_upload_a_single_resource',
  MODIFY_CUSTOM_RESOURCE = 'cxm_enrichment_mailing_modify_a_custom_resource',
  DELETE_CUSTOM_RESOURCE = 'cxm_enrichment_mailing_delete_a_custom_resource',
  MODIFY_DEFAULT_RESOURCE = 'cxm_enrichment_mailing_modify_a_default_resource',
  DELETE_DEFAULT_RESOURCE = 'cxm_enrichment_mailing_delete_a_default_resource',
}

export enum StatisticReport {
  CXM_STATISTIC_REPORT = 'cxm_statistic_report',
  CXM_STATISTIC_REPORT_GENERATE_STATISTIC = 'cxm_statistic_report_generate_statistic',
  CXM_STATISTIC_REPORT_DOWNLOAD_STATISTIC = 'cxm_statistic_report_download_statistic'
}

export enum WatermarkResource {
  CXM_WATERMARK_ENHANCEMENT_POSTAL_DELIVERY = 'cxm_watermark_enhancement_postal_delivery',
  CXM_CREATE_WATERMARK = 'cxm_watermark_enhancement_postal_delivery_create',
  CXM_DELETE_WATERMARK = 'cxm_watermark_enhancement_postal_delivery_delete',
  CXM_MODIFY_WATERMARK = 'cxm_watermark_enhancement_postal_delivery_modify',
}
export enum ConsomablesConstant {
  CXM_SETTING_UP_CONSUMABLES = 'cxm_setting_up_consumables',
  LIST = 'cxm_setting_up_consumables_list',
  CREATE_MODIFY = 'cxm_setting_up_consumables_creat_modify',
  DELETE = 'cxm_setting_up_consumables_delete',
  //CREATE = 'cxm_setting_up_consumables_create',
}

