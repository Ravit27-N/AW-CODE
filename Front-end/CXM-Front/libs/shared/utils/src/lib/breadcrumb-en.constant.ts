import { appRoute } from '@cxm-smartflow/shared/data-access/model';


export const BREADCRUMB_EN = {
  deposit: {
    listPortal: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Shipments',
        url: '/cxm-deposit'
      },
      {
        label: 'From a PDF file',
        url: '/cxm-deposit/list/postal'
      }
    ],
    acquisition: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Shipments',
        url: '/cxm-deposit'
      },
      {
        label: 'From a PDF file',
        url: '/cxm-deposit/acquisition'
      },
    ],
    communicationInteractive: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Shipments',
        url: '/cxm-deposit/communication-interactive'
      },
      {
        label: 'Communication interactive',
        url: '/cxm-deposit/communication-interactive'
      }
    ],
    communicationInteractiveEditor: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Shipments',
        url: '/cxm-deposit/communication-interactive'
      },
      {
        label: 'Communication interactive',
        url: '/cxm-deposit/communication-interactive'
      }
    ]
  },
  template: {
    list: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Template',
        url: appRoute.cxmTemplate.template.listEmailTemplate
      },
      {
        label: 'Emailing',
        url: appRoute.cxmTemplate.template.listEmailTemplate
      }
    ],
    listSms: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Template',
        url: appRoute.cxmTemplate.template.listSMSTemplate
      },
      {
        label: 'SMS',
        url: appRoute.cxmTemplate.template.listSMSTemplate
      }
    ],

    create: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Template',
        url: '/cxm-template/design-model'
      },
      {
        label: 'Manage Templates',
        url: '/cxm-template/design-model/feature-create-email-template'
      }
    ],
    edit: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Template',
        url: '/cxm-template/design-model'
      },
      {
        label: 'Manage Templates',
        url: '/cxm-template/design-model/feature-update-email-template/:id'
      }
    ],
    summaryEmailTemplateFromEditThenComposition: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Template',
        url: '/cxm-template/design-model'
      },
      {
        label: 'Manage Templates',
        url: '/cxm-template/design-model/feature-summary-email-template/:id'
      }
    ]
  },
  campaign: {
    campaignList: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Shipments',
        url: '/cxm-campaign/follow-my-campaign/campaign-list'
      },
      {
        label: 'From a model',
        url: '/cxm-campaign/follow-my-campaign/campaign-list'
      }
    ],
    campaignDetails: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Campaigns',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Campaign',
        url: '/cxm-campaign/follow-my-campaign/campaign-detail/:id'
      }
    ],
    campaignSMSDetails: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Campaigns',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Campaign',
        url: '/cxm-campaign/follow-my-campaign/campaign-detail/:id'
      }
    ],
    choiceOfModels: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Campaigns',
        url: '/cxm-campaign/follow-my-campaign/list-emailing-choice-of-model'
      },
      {
        label: 'Create email campaign',
        url: '/cxm-campaign/follow-my-campaign/list-emailing-choice-of-model'
      }
    ],
    smsModelList: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Campaigns',
        url: '/cxm-campaign/follow-my-campaign/list-sms'
      },
      {
        label: 'Create sms campaign',
        url: '/cxm-campaign/follow-my-campaign/list-sms'
      }
    ],
    createCampaignSms: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Campaigns',
        url: '/cxm-campaign/follow-my-campaign/feature-campaign-list'
      },
      {
        label: 'Create sms campaign',
        url: '/cxm-campaign/follow-my-campaign/list-sms'
      }
    ],
    createDestination: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Campaigns',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Create emailing campaign',
        url: '/cxm-campaign/follow-my-campaign/email/destination'
      }
    ],
    editDestination: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Campaigns',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Create emailing campaign',
        url: '/cxm-campaign/follow-my-campaign/email/destination/:templateId'
      }
    ],
    createParameter: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Campaigns',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Create email campaign',
        url: '/cxm-campaign/follow-my-campaign/email/parameter'
      }
    ],
    editParameter: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Campaign',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Create email campaign',
        url: '/cxm-campaign/follow-my-campaign/email/parameter/:?campaignId'
      }
    ],
    summary: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Campaigns',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Email campaign',
        url: '/cxm-campaign/follow-my-campaign/email/envoi/:campaignId'
      }
    ]
  },
  flowTraceability: {
    list: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Monitoring',
        url: '/cxm-flow-traceability/list'
      }
    ],
    flowTracking: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Monitoring',
        url: '/cxm-flow-traceability/list'
      }
    ],
    documentDetail: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Monitoring',
        url: '/cxm-flow-traceability/list'
      },
      {
        label: 'Flow traceability',
        url: '/cxm-flow-traceability/document-detail-from-flow/:id'
      }
    ],
    documentDetailFromDocumentPage: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Monitoring',
        url: '/cxm-flow-traceability/list'
      },
      {
        label: 'Document traceability',
        url: '/cxm-flow-traceability/document-detail-from-document/:id'
      }
    ],
    flowTraceabilityDepositDetail: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Monitoring',
        url: '/cxm-flow-traceability/list'
      },
      {
        label: 'Detail of the lot',
        url: '/cxm-flow-traceability/flow-detail-deposit'
      }
    ],
    flowDigitalDetail: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Monitoring',
        url: '/cxm-flow-traceability/list'
      },
      {
        label: 'Details of the campaign',
        url: '/cxm-flow-traceability/flow-detail-digital'
      }
    ],
    flowDocumentPortalDetail: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Monitoring',
        url: '/cxm-flow-traceability/list'
      },
      {
        label: 'Detail of the unit shipment',
        url: '/cxm-flow-traceability/flow-document-detail'
      }
    ]
  },
  definitionDirectory: {
    list: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Setting',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Directory definition',
        url: '/cxm-directory/list-definition-directory'
      }
    ],
    create: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Setting',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Users',
        url: '/cxm-profile/users/list-user'
      },
      {
        label: 'Create directory',
        url: '/cxm-directory/create'
      }
    ],
    edit: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Setting',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Manage directory',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Definition directory',
        url: '/cxm-directory/edit'
      }
    ],
    view: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Setting',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Manage directory',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Definition directory',
        url: '/cxm-directory/edit'
      }
    ]
  },
  directoryFeed: {
    list: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Setting',
        url: '/cxm-directory/directory-feed'
      },
      {
        label: 'Directory feed',
        url: '#'
      },
    ],
    directoryDetail: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Setting',
        url: '/cxm-directory/directory-feed'
      },
      {
        label: 'Directory feed',
        url: '#'
      }
    ],
    directoryAdd: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Setting',
        url: '/cxm-directory/directory-feed'
      },
      {
        label: 'Directory feed',
        url: '/cxm-directory/directory-feed/detail'
      },
      {
        label: 'Insert a line',
        url: '#'
      }
    ]
  },
  profile: {
    list: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/list-profiles'
      },
      // {
      //   label: 'Administer users',
      //   url: '/cxm-profile/list-profiles'
      // },
      {
        label: 'Profiles',
        url: '/cxm-profile/list-profiles'
      },
    ],
    create: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/list-profiles'
      },
      {
        label: 'Profiles',
        url: '/cxm-profile/list-profiles'
      },
      {
        label: 'Create a profile',
        url: '/cxm-profile/create-profile'
      }
    ],
    edit: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/list-profiles'
      },
      {
        label: 'Profiles',
        url: '/cxm-profile/list-profiles'
      },
      {
        label: 'Modify a profile',
        url: '/cxm-profile/update-profile/:id'
      }
    ]
  },
  user: {
    list: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/users/list-user'
      },
      {
        label: 'Users',
        url: '/cxm-profile/users/list-user'
      }
    ],
    create: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/users/list-user'
      },
      {
        label: 'User',
        url: '/cxm-profile/users/list-user'
      },
      {
        label: 'Create a user',
        url: '/cxm-profile/users/list-user'
      }
    ],
    edit: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/users/list-user'
      },
      {
        label: 'Users',
        url: '/cxm-profile/users/list-user'
      },
      {
        label: 'Modify a user',
        url: '/cxm-profile/users/update-user'
      }
    ],
    editBatch: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-profile/users/list-user'
      },
      {
        label: 'Utilisateurs',
        url: '/cxm-profile/users/list-user'
      },
      {
        label: "Modify users",
        url: '/cxm-profile/users/update-user'
      }
    ]
  },
  espace: {
    list: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Envois',
        url: '/cxm-flow-traceability/espace'
      },
      {
        label: 'Espace de validation',
        url: '/cxm-flow-traceability/espace'
      },
    ],
    consult: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Envois',
        url: '/cxm-flow-traceability/espace'
      },
      {
        label: 'Espace de validation',
        url: '/cxm-flow-traceability/espace'
      },
      {
        label: 'Détail du lot',
        url: '/cxm-flow-traceability/espace'
      }
    ]
  },
  client: {
    list: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client',
        url: '/cxm-profile/client/list'
      }
    ],
    create: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Create client',
        url: '/cxm-profile/client/c/create'
      }
    ],
    modify: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client modification',
        url: '/cxm-profile/client/c/modify/:id'
      }
    ],
    configuration: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client modification',
        url: '/cxm-profile/client/c/configuration'
      }
    ],
    settingDigitalChannel: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client modification',
        url: '/cxm-profile/client/c/setting-digital-channel'
      }
    ],
  },
  setting: {
    list: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-setting/manage-resource/list'
      },
      {
        label: 'Resource',
        url: '/cxm-setting/manage-resource/list'
      }
    ],
  },
  analytics: {
    list: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Statistics',
        url: '/cxm-analytics/report-space'
      },
    ]
  },
  envelope_reference: {
    list: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-setting/envelope-references/list'
      },
      {
        label: 'Envelope References',
        url: '/cxm-setting/envelope-references/list'
      }
    ],
    create: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-setting/envelope-references/list'
      },
      {
        label: 'Envelope References',
        url: '/cxm-setting/envelope-references/list'
      },
      {
        label: 'Create a Envelope References',
        url: '/cxm-setting/envelope-references/create'
      }
    ],
    edit: [
      {
        label: 'Home',
        url: '/dashboard'
      },
      {
        label: 'Settings',
        url: '/cxm-setting/envelope-references/list'
      },
      {
        label: 'Envelope References',
        url: '/cxm-setting/envelope-references/list'
      },
      {
        label: 'Edit a Envelope References',
        url: '/cxm-setting/envelope-references/update'
      }
    ],
  },
};





