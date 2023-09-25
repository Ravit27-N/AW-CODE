const cxmTemplateBaseRoot = 'cxm-template/design-model';
const cxmCampaignBaseRoot = 'cxm-campaign/follow-my-campaign';
const cxmFlowTraceabilityBaseRoot = 'cxm-flow-traceability';
const cxmDepositBaseRoot = 'cxm-deposit';
const cxmProfileRoot = 'cxm-profile';
const cxmSettingRoot = 'cxm-setting';
const cxmAnalyticsRoot = 'cxm-analytics';
const cxmDirectoryRoot = 'cxm-directory';

export const appRoute = {
  cxmTemplate: {

    // New
    baseRoot: cxmTemplateBaseRoot,
    template: {
      createUpdateEmail: cxmTemplateBaseRoot.concat('/sms-template-composition/email'),
      createUpdateSms: cxmTemplateBaseRoot,
      emailTemplateComposition: cxmTemplateBaseRoot.concat('/sms-template-composition/email'),
      smsTemplateComposition: cxmTemplateBaseRoot.concat('/sms-template-composition/sms'),
      listEmailTemplate: cxmTemplateBaseRoot.concat('/feature-list-email-template/email'),
      listSMSTemplate: cxmTemplateBaseRoot.concat('/feature-list-email-template/sms'),
      successPage: cxmTemplateBaseRoot.concat('/sms-template-composition/done')
    },

    // Old
    emailTemplate: {
      navigateToRoot: cxmTemplateBaseRoot,
      navigateToCreate: `${cxmTemplateBaseRoot}/feature-create-email-template`,
      navigateToUpdate: `${cxmTemplateBaseRoot}/feature-update-email-template`,
      navigateToEmailTemplateComposition: `${cxmTemplateBaseRoot}/feature-email-template-composition`,
      navigateToEmailTemplateCompositionFromEdit: `${cxmTemplateBaseRoot}/feature-email-template-composition-from-edit`,
      navigateToSummaryEmailTemplate: `${cxmTemplateBaseRoot}/feature-summary-email-template`,
      navigateToSummaryEmailTemplateFromEdit: `${cxmTemplateBaseRoot}/feature-summary-email-template-from-edit`,
      navigateToEdit: `${cxmTemplateBaseRoot}/feature-edit-email-template`,
    }
  },
  cxmCampaign: {
    followMyCampaign: {

      // New
      baseRoot: cxmCampaignBaseRoot,
      listEmailCampaign: cxmCampaignBaseRoot.concat("/campaign-list"),
      emailCampaignDestination: cxmCampaignBaseRoot.concat("/email/destination"),
      emailCampaignParameter: cxmCampaignBaseRoot.concat("/email/parameter"),
      emailCampaignSummary: cxmCampaignBaseRoot.concat("/email/envoi"),
      emailChoiceOfModel: cxmCampaignBaseRoot.concat('/campaign/emailing'),
      emailSuccess: cxmCampaignBaseRoot.concat('/email/done'),

      smsCampaignDestination: cxmCampaignBaseRoot.concat("/sms/destination"),
      smsCampaignParameter: cxmCampaignBaseRoot.concat("/sms/parameter"),
      smsCampaignEnvoy: cxmCampaignBaseRoot.concat("/sms/envoy"),
      smsCampaignList: cxmCampaignBaseRoot.concat("/campaign-list"),
      smsSuccess: cxmCampaignBaseRoot.concat('/sms/done'),

      campaign: cxmCampaignBaseRoot.concat("/campaign"),
      smsCampaignChoiceOfModel: cxmCampaignBaseRoot.concat("/campaign/sms"),
      emailCampaign: cxmCampaignBaseRoot.concat("/campaign/emailing"),

      // Old
      navigateToSettingParameter: `${cxmCampaignBaseRoot}/feature-setting-parameters`,
      navigateToRoot: cxmCampaignBaseRoot,
      navigateToEmailingChoiceOfModel: `${cxmCampaignBaseRoot}/list-emailing-choice-of-model`,
      navigateToCampaignCreateDestination: `${cxmCampaignBaseRoot}/csv`,
      navigateToCampaignEditDestination: `${cxmCampaignBaseRoot}/update-csv`,
      navigateToSummaryCampaign: `${cxmCampaignBaseRoot}/summary`,
      navigateToSummaryCampaignFromList: `${cxmCampaignBaseRoot}/summary-from-list`,
      navigateToUpdateSettingParameter: `${cxmCampaignBaseRoot}/feature-update-setting-parameters`,
      navigateToUpdateEmailingParameter: `${cxmCampaignBaseRoot}/email/parameter`,
      navigateToCampaignDetail: `${cxmCampaignBaseRoot}/campaign-detail`,
      navigateToCampaignSMSDetail: `${cxmCampaignBaseRoot}/campaign-sms-detail`,
      navigateToUpdateCampaignDestination: `${cxmCampaignBaseRoot}/update-csv`,
      navigateToUpdateCampaignDestinationFromList: `${cxmCampaignBaseRoot}/update-csv-from-list`,
    }
  },
  cxmFlowTraceability: {
    document: {},
    navigateToFlowDetailDeposit: `${cxmFlowTraceabilityBaseRoot}/flow-detail-deposit`,
    navigateToFlowDetailDigital: `${cxmFlowTraceabilityBaseRoot}/flow-detail-digital`,
    navigateToViewDocumentShipment: `${cxmFlowTraceabilityBaseRoot}/document-of-flow-traceability/view-shipment`,
    navigateToViewDocumentDetail: `${cxmFlowTraceabilityBaseRoot}/flow-document-detail`,
    list: `${cxmFlowTraceabilityBaseRoot}/list`,
    navigateToListFLowDocument: `${cxmFlowTraceabilityBaseRoot}/list-flow-document`,

  },
  cxmDeposit: {
    list: `${cxmDepositBaseRoot}/list/postal`,
    navigateToAcquisition: `${cxmDepositBaseRoot}/acquisition`,
    navigateToPreAnalysis: `${cxmDepositBaseRoot}/pre-analysis`,
    navigateToAnalysisResult: `${cxmDepositBaseRoot}/analysis-result`,
    navigateToProductionCriteria: `${cxmDepositBaseRoot}/production-criteria`,
    navigateToFinished: `${cxmDepositBaseRoot}/finished`,
  },
  // new
  dashboard: {
    baseRoot: "dashboard"
  },

  // User
  cxmUser: {
    navigateToList: `${cxmProfileRoot}/users/list-user`,
    navigateToCreate: `${cxmProfileRoot}/users/create-user`,
    navigateToModify: `${cxmProfileRoot}/users/update-user`,
    navigateToModifyBatch: `${cxmProfileRoot}/users/update-batch-user`,
  },

  cxmEnvelopeReference: {
    navigateToList: `${cxmSettingRoot}/envelope-references/list`,
    navigateToCreate: `${cxmSettingRoot}/envelope-references/create`,
    navigateToModify: `${cxmSettingRoot}/envelope-references/update`
  },

  // Profile
  cxmProfile: {
    navigateToList: `${cxmProfileRoot}/list-profiles`,
    navigateToCreate: `${cxmProfileRoot}/create-profile`,
    navigateToModify: `${cxmProfileRoot}/update-profile`,
  },

  // Communication integrative
  cxmCommunicationInteractive: {
    navigateToChooseModel: `${cxmDepositBaseRoot}/communication-interactive`,
    navigateToEditor: `${cxmDepositBaseRoot}/communication-interactive/editor`,
    navigateToSuccessPage: `${cxmDepositBaseRoot}/communication-interactive/success`
  },

  // Approval
  cxmApproval: {
    navigateToValidateFlow: `${cxmFlowTraceabilityBaseRoot}/espace`,
    navigateToValidateFlowDocument: `${cxmFlowTraceabilityBaseRoot}/espace/consult`,
  },

  cxmClient: {
    navigateToCreateClient: `${cxmProfileRoot}/client/c/create`,
    navigateToModifyClient: `${cxmProfileRoot}/client/c/modify`,
    navigateToListClient: `${cxmProfileRoot}/client/list`,
    navigateToConfigurationFileClient: `${cxmProfileRoot}/client/c/configuration`,
    navigateToManageDigitalChannel: `${cxmProfileRoot}/client/c/setting-digital-channel`,
  },

  cxmSetting: {
    navigateToListResource: `${cxmSettingRoot}/resources`,
    navigateToConsultResource: `${cxmSettingRoot}/resources/view`,
  },

  cxmAnalytics: {
    navigateToDashboard: `${cxmAnalyticsRoot}/dashboard`,
    navigateToGlobal: `${cxmAnalyticsRoot}/report-space`,
    navigateToPostal: `${cxmAnalyticsRoot}/report-postal`,
    navigateToEmail: `${cxmAnalyticsRoot}/report-email`,
    navigateToSms: `${cxmAnalyticsRoot}/report-sms`,
  },
  cxmDirectory: {
    navigateToListDefinitionDirectory: `${cxmDirectoryRoot}/list-definition-directory`,
    navigateToCreateDefinitionDirectory: `${cxmDirectoryRoot}/create`,
    navigateToEditDefinitionDirectory: `${cxmDirectoryRoot}/edit`,
    navigateToViewDefinitionDirectory: `${cxmDirectoryRoot}/view`,
    navigateToListDirectoryFeed: `${cxmDirectoryRoot}/directory-feed`,
    navigateToViewDirectoryFeedDetail: `${cxmDirectoryRoot}/directory-feed/detail`,
    navigateToAddDirectoryFeed: `${cxmDirectoryRoot}/directory-feed/add`,
  }
};
