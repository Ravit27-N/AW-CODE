import {
  ClientManagement,
  ConsomablesConstant,
  DirectoryManagement, EnrichmentMailing,
  EspaceValidation, FlowTraceability,
  Interactive,
  LibraryResourceManagement, StatisticReport, WatermarkResource
} from '@cxm-smartflow/shared/data-access/model';

export interface PredefinedFormPrevilege {
  name: string;
  code: string;
  checked: boolean;
  visibility?: string;
  modification?: string;
  id?: string;
  v?: number,
  m?: number,
  allowed?: boolean
}

export interface PredefinedFormModel extends PredefinedFormPrevilege {
  func: Array<PredefinedFormPrevilege>;
}

export const PREDEFINE_PERMISSION_FORM: PredefinedFormModel[] = [
  {
    name: 'profile.form.label.traceability',
    code: 'cxm_flow_traceability',
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: 'cxm_flow_traceability_list',
        visibility: 'user',
        name: 'profile.form.label.privilege.tracking_space_consult',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_select_and_open',
        visibility: 'user',
        name: 'profile.form.label.privilege.tracking_space_select_flow',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_cancel_flow',
        modification: 'owner',
        name: 'profile.form.label.privilege.tracking_space_cancel_flow',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_validate',
        modification: 'owner',
        name: 'profile.form.label.privilege.tracking_space_validate',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_finalize',
        modification: 'owner',
        name: 'profile.form.label.privilege.tracking_space_finalize',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_download',
        visibility: 'user',
        name: 'profile.form.label.privilege.tracking_space_download',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_view_event_and_history',
        visibility: 'user',
        name: 'profile.form.label.privilege.tracking_space_history',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_view_document',
        visibility: 'user',
        name: 'profile.form.label.privilege.tracking_space_doc_consult',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_select_and_open_document',
        visibility: 'user',
        name: 'profile.form.label.privilege.tracking_space_select_doc',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_download_document',
        visibility: 'user',
        name: 'profile.form.label.privilege.tracking_space_download_doc',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_view_event_history_document',
        visibility: 'user',
        name: 'profile.form.label.privilege.tracking_space_history_doc',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_open_and_download_document',
        visibility: 'user',
        name: 'profile.form.label.privilege.tracking_space_relate_item',
      },
      {
        checked: false,
        code: 'cxm_flow_traceability_validate_document',
        modification: 'owner',
        name: 'profile.form.label.privilege.tracking_space_validate_doc',
      },
    ],
  },
  {
    name: 'profile.form.label.statistic',
    code: StatisticReport.CXM_STATISTIC_REPORT,
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        name: 'profile.form.label.privilege.statistic_generate',
        checked: false,
        code: StatisticReport.CXM_STATISTIC_REPORT_GENERATE_STATISTIC,
        visibility: 'user',
      },
      {
        name: 'profile.form.label.privilege.statistic_download',
        checked: false,
        code: StatisticReport.CXM_STATISTIC_REPORT_DOWNLOAD_STATISTIC,
      },
    ],
  },
  {
    name: 'profile.form.label.mailModel',
    code: 'cxm_template',
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: 'cxm_template_list',
        visibility: 'user',
        name: 'profile.form.label.privilege.mailModel_list',
      },
      {
        checked: false,
        code: 'cxm_template_create_from_scratch',
        name: 'profile.form.label.privilege.mailModel_create',
      },
      {
        checked: false,
        code: 'cxm_template_create_by_duplicate',
        name: 'profile.form.label.privilege.mailModel_create_copy',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_template_edit',
        name: 'profile.form.label.privilege.mailModel_edit',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_template_modify',
        name: 'profile.form.label.privilege.mailModel_modify',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_template_delete',
        name: 'profile.form.label.privilege.mailModel_delete',
        modification: 'owner',
      },
    ],
  },
  {
    name: 'profile.form.label.smsModel',
    code: 'cxm_sms_template',
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: 'cxm_sms_template_list',
        name: 'profile.form.label.privilege.smsModel_list',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_sms_template_create_from_scratch',
        name: 'profile.form.label.privilege.smsModel_create',
      },
      {
        checked: false,
        code: 'cxm_sms_template_create_by_duplicate',
        name: 'profile.form.label.privilege.smsModel_create_copy',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_sms_template_edit',
        name: 'profile.form.label.privilege.smsModel_edit',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_sms_template_modify',
        name: 'profile.form.label.privilege.smsModel_modify',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_sms_template_delete',
        name: 'profile.form.label.privilege.smsModel_delete',
        modification: 'owner',
      },
    ],
  },
  {
    name: 'profile.form.label.mailCampaign',
    code: 'cxm_campaign',
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: 'cxm_campaign_list',
        name: 'profile.form.label.privilege.mailCampaign_list',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_campaign_select_and_view',
        name: 'profile.form.label.privilege.mailCampaign_detail',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_campaign_cancel',
        name: 'profile.form.label.privilege.mailCampaign_cancel',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_campaign_finalize',
        name: 'profile.form.label.privilege.mailCampaign_finalize',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_campaign_create',
        name: 'profile.form.label.privilege.mailCampaign_create',
      },
      {
        checked: false,
        code: 'cxm_campaign_choose_model',
        name: 'profile.form.label.privilege.mailCampaign_select',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_campaign_test_send_mail',
        name: 'profile.form.label.privilege.mailCampaign_send_proof',
      },
    ],
  },
  {
    name: 'profile.form.label.smsCampaign',
    code: 'cxm_sms_campaign',
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: 'cxm_sms_campaign_list',
        name: 'profile.form.label.privilege.smsCampaign_list',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_sms_campaign_select_and_view',
        name: 'profile.form.label.privilege.smsCampaign_detail',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_sms_campaign_cancel',
        name: 'profile.form.label.privilege.smsCampaign_cancel',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_sms_campaign_finalize',
        name: 'profile.form.label.privilege.smsCampaign_finalized',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_sms_campaign_create',
        name: 'profile.form.label.privilege.smsCampaign_create',
      },
      {
        checked: false,
        code: 'cxm_sms_campaign_choose_model',
        name: 'profile.form.label.privilege.smsCampaign_select',
        visibility: 'user',
      },
      // {
      //   checked: false,
      //   code: 'cxm_sms_campaign_sort_and_filter_variable_template',
      //   name: 'profile.form.label.privilege.smsCampaign_local_filter',
      // },
      // {
      //   checked: false,
      //   code: 'cxm_sms_campaign_define_creation_parameter',
      //   name: 'profile.form.label.privilege.smsCampaign_create_parameter',
      // },
      // {
      //   checked: false,
      //   code: 'cxm_sms_campaign_import_recipient_file',
      //   name: 'profile.form.label.privilege.smsCampaign_import',
      // },
      {
        checked: false,
        code: 'cxm_sms_campaign_test_send_mail',
        name: 'profile.form.label.privilege.smsCampaign_send_proof',
      },
      // {
      //   checked: false,
      //   code: 'cxm_sms_campaign_generate',
      //   name: 'profile.form.label.privilege.smsCampaign_generate',
      // }
    ],
  },
  {
    name: 'profile.form.label.deposit',
    code: 'cxm_flow_deposit',
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: 'cxm_flow_deposit_send_a_letter',
        name: 'profile.form.label.privilege.send_a_letter',
      },
      {
        checked: false,
        code: 'cxm_flow_deposit_list_deposits',
        name: 'profile.form.label.privilege.list_deposits',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_flow_deposit_modify_a_deposit',
        name: 'profile.form.label.privilege.modify_a_deposit',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_flow_deposit_delete_a_deposit',
        name: 'profile.form.label.privilege.delete_a_deposit',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_flow_deposit_modify_or_correct_an_address',
        name: 'profile.form.label.privilege.modify_or_correct_an_address',
        modification: 'owner',
      }
    ],
  },
  {
    name: 'profile.form.label.modelSetting',
    code: 'cxm_setting_up_document_template',
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: 'cxm_setting_up_document_template_list',
        name: 'profile.form.label.privilege.modelSetting_list',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_setting_up_document_template_create',
        name: 'profile.form.label.privilege.modelSetting_create',
      },
      {
        checked: false,
        code: 'cxm_setting_up_document_template_define_recognition_criteria',
        name: 'profile.form.label.privilege.modelSetting_create_parameter',
      },
      {
        checked: false,
        code: 'cxm_setting_up_document_template_associate_documentary',
        name: 'profile.form.label.privilege.modelSetting_associate',
      },
      {
        checked: false,
        code: 'cxm_setting_up_document_template_define_and_identify',
        name: 'profile.form.label.privilege.modelSetting_identity',
      },
      {
        checked: false,
        code: 'cxm_setting_up_document_template_edit',
        name: 'profile.form.label.privilege.modelSetting_edit',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_setting_up_document_template_modify',
        name: 'profile.form.label.privilege.modelSetting_modify',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_setting_up_document_template_delete',
        name: 'profile.form.label.privilege.modelSetting_delete',
        modification: 'owner',
      },
    ],
  },
  {
    name: 'profile.form.label.usermgt',
    code: 'cxm_user_management',
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: 'cxm_user_management_list_profile',
        name: 'profile.form.label.privilege.usermgt_list_profile',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_user_management_create_profile',
        name: 'profile.form.label.privilege.usermgt_create_profile',
      },
      {
        checked: false,
        code: 'cxm_user_management_edit_profile',
        name: 'profile.form.label.privilege.edit_profile',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_user_management_modify_profile',
        name: 'profile.form.label.privilege.usermgt_modify_profile',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_user_management_delete_profile',
        name: 'profile.form.label.privilege.usermgt_delete_profile',
        modification: 'owner',
      },
      // {
      //   checked: false,
      //   code: 'cxm_user_management_manage_permission',
      //   name: 'profile.form.label.privilege.usermgt_permission',
      //   modification: 'owner',
      // },
      {
        checked: false,
        code: 'cxm_user_management_list_user',
        name: 'profile.form.label.privilege.usermgt_list',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_user_management_create_user',
        name: 'profile.form.label.privilege.usermgt_create',
      },
      {
        checked: false,
        code: 'cxm_user_management_edit_user',
        name: 'profile.form.label.privilege.edit_user',
        visibility: 'user',
      },
      {
        checked: false,
        code: 'cxm_user_management_modify_user',
        name: 'profile.form.label.privilege.usermgt_modify',
        modification: 'owner',
      },
      {
        checked: false,
        code: 'cxm_user_management_delete_user',
        name: 'profile.form.label.privilege.usermgt_delete_user',
        modification: 'owner',
      },
      // {
      //   checked: false,
      //   code: 'cxm_user_management_assign_user_to_service',
      //   name: 'profile.form.label.privilege.usermgt_assign_service',
      //   visibility: 'user',
      // }
    ],
  },
  {
    name: 'profile.form.label.client_management',
    code: ClientManagement.CXM_CLIENT_MANAGEMENT,
    checked: false,
    func: [
      {
        checked: false,
        code: ClientManagement.LIST,
        name: 'profile.form.label.privilege.client_management_list',
      },
      {
        checked: false,
        code: ClientManagement.MODIFY,
        name: 'profile.form.label.privilege.client_management_modify',
      },
    ],
  },
  {
    name: 'profile.form.label.directoryMgt',
    code: DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: DirectoryManagement.LIST_DEFINITION_DIRECTORY,
        name: 'profile.form.label.privilege.directoryMgtListDefinitionDirectory',
        visibility: 'user',
      },
      {
        checked: false,
        code: DirectoryManagement.CREATE_DEFINITION_DIRECTORY,
        name: 'profile.form.label.privilege.directoryMgtCreateDefinitionDirectory',
      },
      {
        checked: false,
        code: DirectoryManagement.EDIT_DEFINITION_DIRECTORY,
        name: 'profile.form.label.privilege.directoryMgtEditDefinitionDirectory',
        visibility: 'user',
      },
      {
        checked: false,
        code: DirectoryManagement.MODIFY_DEFINITION_DIRECTORY,
        name: 'profile.form.label.privilege.directoryMgtModifyDefinitionDirectory',
        modification: 'owner',
      },
      {
        checked: false,
        code: DirectoryManagement.DELETE_DEFINITION_DIRECTORY,
        name: 'profile.form.label.privilege.directoryMgtDeleteDefinitionDirectory',
        modification: 'owner',
      },
      {
        checked: false,
        code: DirectoryManagement.LIST_DIRECTORY_FEED,
        name: 'profile.form.label.privilege.directoryMgtListDirectoryFeed',
        visibility: 'user',
      },
      {
        checked: false,
        code: DirectoryManagement.EDIT_DATA_DIRECTORY_FEED,
        name: 'profile.form.label.privilege.directoryMgtEditDirectoryFeed',
        visibility: 'user',
      },
      {
        checked: false,
        code: DirectoryManagement.MANUAL_POPULATE_DATA_DIRECTORY_FEED,
        name: 'profile.form.label.privilege.directoryMgtManualPopulateDirectoryFeed',
        modification: 'owner',
      },
      {
        checked: false,
        code: DirectoryManagement.IMPORT_DATA_DIRECTORY_FEED,
        name: 'profile.form.label.privilege.directoryMgtImportDirectoryFeed',
        modification: 'owner',
      },
      {
        checked: false,
        code: DirectoryManagement.EXPORT_DIRECTORY_FEED,
        name: 'profile.form.label.privilege.directoryMgtExportDirectoryFeed',
        visibility: 'user',
      },
      {
        checked: false,
        code: DirectoryManagement.MODIFY_DATA_DIRECTORY_FEED,
        name: 'profile.form.label.privilege.directoryMgtModifyDirectoryFeed',
        modification: 'owner',
      },
      {
        checked: false,
        code: DirectoryManagement.DELETE_DATA_DIRECTORY_FEED,
        name: 'profile.form.label.privilege.directoryMgtDeleteDirectoryFeed',
        modification: 'owner',
      },
    ],
  },
  {
    name: 'profile.form.label.interactive',
    code: Interactive.CXM_INTERACTIVE,
    checked: false,
    visibility: 'user',
    func: [
      {
        checked: false,
        code: Interactive.LIST_MODEL,
        name: 'profile.form.label.privilege.interactive_list_model',
        visibility: 'user',
      },
    ],
  },
  {
    name: 'profile.form.label.espace_validation',
    code: EspaceValidation.CXM_ESPACE_VALIDATION,
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: EspaceValidation.LIST,
        name: 'profile.form.label.privilege.espace_validation_list',
        visibility: 'user',
      },
      {
        checked: false,
        code: EspaceValidation.VALIDATE_REFUSE,
        name: 'profile.form.label.privilege.espace_validation_validate_refuse',
        modification: 'owner',
      },
    ],
  },
  {
    name: 'profile.form.label.management_library_resource',
    code: LibraryResourceManagement.CXM_MANAGEMENT_LIBRARY_RESOURCE,
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: LibraryResourceManagement.LIST,
        name: 'profile.form.label.privilege.management_library_resource_list',
        visibility: 'user',
      },
      {
        checked: false,
        code: LibraryResourceManagement.CREATE,
        name: 'profile.form.label.privilege.management_library_resource_create',
      },
      {
        checked: false,
        code: LibraryResourceManagement.DELETE,
        name: 'profile.form.label.privilege.management_library_resource_delete',
        modification: 'owner',
      },
    ],
  },
  {
    name: 'profile.form.label.cxm_enrichment_mailing',
    code: EnrichmentMailing.CXM_ENRICHMENT_MAILING,
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: EnrichmentMailing.ADD_RESOURCE,
        name: 'profile.form.label.privilege.cxm_enrichment_mailing_add_resource',
        visibility: 'user'
      },
      {
        checked: false,
        code: EnrichmentMailing.USE_RESOURCE_IN_LIBRARY,
        name: 'profile.form.label.privilege.cxm_enrichment_mailing_use_resource_in_library',
        visibility: 'user'
      },
      {
        checked: false,
        code: EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE,
        name: 'profile.form.label.privilege.cxm_enrichment_mailing_upload_a_single_resource',
        modification: 'owner'
      },
      {
        checked: false,
        code: EnrichmentMailing.MODIFY_DEFAULT_RESOURCE,
        name: 'profile.form.label.privilege.cxm_enrichment_mailing_modify_a_default_resource',
        modification: 'owner'
      },
      {
        checked: false,
        code: EnrichmentMailing.DELETE_DEFAULT_RESOURCE,
        name: 'profile.form.label.privilege.cxm_enrichment_mailing_delete_a_default_resource',
        modification: 'owner'
      },
      {
        checked: false,
        code: EnrichmentMailing.MODIFY_CUSTOM_RESOURCE,
        name: 'profile.form.label.privilege.cxm_enrichment_mailing_modify_a_custom_resource',
        modification: 'owner'
      },
      {
        checked: false,
        code: EnrichmentMailing.DELETE_CUSTOM_RESOURCE,
        name: 'profile.form.label.privilege.cxm_enrichment_mailing_delete_a_custom_resource',
        modification: 'owner'
      },
    ]
  },
  {
    name: 'profile.form.label.cxm_watermark_enhancement_postal_delivery',
    code: WatermarkResource.CXM_WATERMARK_ENHANCEMENT_POSTAL_DELIVERY,
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: WatermarkResource.CXM_CREATE_WATERMARK,
        name: 'profile.form.label.privilege.cxm_enrichment_mailing_add_watermark',
        visibility: 'user'
      },
      {
        checked: false,
        code: WatermarkResource.CXM_MODIFY_WATERMARK,
        name: 'profile.form.label.privilege.cxm_enrichment_mailing_modify_watermark',
        modification: 'owner'
      },
      {
        checked: false,
        code: WatermarkResource.CXM_DELETE_WATERMARK,
        name: 'profile.form.label.privilege.cxm_enrichment_mailing_delete_watermark',
        modification: 'owner'
      }
    ]
  },
  {
    name: 'profile.form.label.consumablesSetting',
    code: ConsomablesConstant.CXM_SETTING_UP_CONSUMABLES,
    checked: false,
    visibility: 'user',
    modification: 'owner',
    func: [
      {
        checked: false,
        code: ConsomablesConstant.LIST,
        name: 'profile.form.label.privilege.cxm_setting_up_consumables_list',
      
      },
      {
        checked: false,
        code: ConsomablesConstant.DELETE,
        name: 'profile.form.label.privilege.cxm_setting_up_consumables_creat_modify',
      },
      {
        checked: false,
        code: ConsomablesConstant.CREATE_MODIFY,
        name: 'profile.form.label.privilege.cxm_setting_up_consumables_delete',
      
      }
    ]
  },
];
