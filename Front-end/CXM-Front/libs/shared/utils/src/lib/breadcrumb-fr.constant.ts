import { appRoute } from '@cxm-smartflow/shared/data-access/model';

export const BREADCRUMB_FR = {
  deposit: {
    listPortal: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Envois',
        url: '/cxm-deposit'
      },
      {
        label: 'A partir d’un fichier PDF',
        url: '/cxm-deposit/list/postal'
      }
    ],
    acquisition: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Envois',
        url: '/cxm-deposit'
      },
      {
        label: 'À partir d\'un fichier PDF',
        url: '/cxm-deposit/acquisition'
      }
    ],
    communicationInteractive: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Envois',
        url: '/cxm-deposit/communication-interactive'
      },
      {
        label: 'Communication interactive',
        url: '/cxm-deposit/communication-interactive'
      }
    ],
    communicationInteractiveEditor: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Envois',
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
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Modèles',
        url: appRoute.cxmTemplate.template.listEmailTemplate
      },
      {
        label: 'Emailing',
        url: appRoute.cxmTemplate.template.listEmailTemplate
      }
    ],

    listSms: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Modèles',
        url: appRoute.cxmTemplate.template.listSMSTemplate
      },
      {
        label: 'SMS',
        url: appRoute.cxmTemplate.template.listSMSTemplate
      }
    ],

    create: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Modèles',
        url: '/cxm-template/design-model'
      },
      {
        label: 'Gérer modèles',
        url: '/cxm-template/design-model/feature-create-email-template'
      }
    ],
    edit: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Modèles',
        url: '/cxm-template/design-model'
      },
      {
        label: 'Gérer modèles',
        url: '/cxm-template/design-model/feature-update-email-template/:id'
      }
    ],
    summaryEmailTemplateFromEditThenComposition: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Modèles',
        url: '/cxm-template/design-model'
      },
      {
        label: 'Gérer modèles',
        url: '/cxm-template/design-model/feature-summary-email-template/:id'
      }
    ]
  },
  campaign: {
    campaignList: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Envois',
        url: '/cxm-campaign/follow-my-campaign/campaign-list'
      },
      {
        label: 'À partir d\'un modèle',
        url: '/cxm-campaign/follow-my-campaign/campaign-list'
      }
    ],
    campaignDetails: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Campagnes',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Campagne',
        url: '/cxm-campaign/follow-my-campaign/campaign-detail/:id'
      }
    ],
    campaignSMSDetails: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Campagnes',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Campagne',
        url: '/cxm-campaign/follow-my-campaign/campaign-sms-detail/:id'
      }
    ],
    choiceOfModels: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Campagnes',
        url: '/cxm-campaign/follow-my-campaign/list-emailing-choice-of-model'
      },
      {
        label: 'Créer campagne emailing',
        url: '/cxm-campaign/follow-my-campaign/list-emailing-choice-of-model'
      }
    ],
    smsModelList: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Campagnes',
        url: '/cxm-campaign/follow-my-campaign/list-sms'
      },
      {
        label: 'Créer campagne sms',
        url: '/cxm-campaign/follow-my-campaign/list-sms'
      }
    ],
    createCampaignSms: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Campagnes',
        url: '/cxm-campaign/follow-my-campaign/feature-campaign-list'
      },
      {
        label: 'Créer campagne sms',
        url: '/cxm-campaign/follow-my-campaign/list-sms'
      }
    ],
    createDestination: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Campagnes',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Créer campagne emailing',
        url: '/cxm-campaign/follow-my-campaign/email/destination'
      }
    ],
    editDestination: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Campagnes',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Créer campagne emailing',
        url: '/cxm-campaign/follow-my-campaign/email/destination/:templateId'
      }
    ],
    createParameter: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Campagnes',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Créer campagne emailing',
        url: '/cxm-campaign/follow-my-campaign/email/parameter'
      }
    ],
    editParameter: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Campagnes',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Créer campagne emailing',
        url: '/cxm-campaign/follow-my-campaign/email/parameter/:?campaignId'
      }
    ],
    summary: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Campagnes',
        url: '/cxm-campaign/follow-my-campaign'
      },
      {
        label: 'Créer campagne emailing',
        url: '/cxm-campaign/follow-my-campaign/email/envoi/:campaignId'
      }
    ]
  },
  flowTraceability: {
    list: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Suivi',
        url: '/cxm-flow-traceability/list'
      }
    ],
    flowTracking: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Suivi',
        url: '/cxm-flow-traceability/list'
      }
    ],
    documentDetail: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Suivi',
        url: '/cxm-flow-traceability/list'
      },
      {
        label: 'Suivre flux',
        url: '/cxm-flow-traceability/document-detail-from-flow/:id'
      }
    ],
    documentDetailFromDocumentPage: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Suivi',
        url: '/cxm-flow-traceability/list'
      },
      {
        label: 'Suivre envois',
        url: '/cxm-flow-traceability/document-detail-from-document/:id'
      }
    ],
    flowTraceabilityDepositDetail: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Suivi',
        url: '/cxm-flow-traceability/list'
      },
      {
        label: 'Détail du lot',
        url: '/cxm-flow-traceability/flow-detail-deposit'
      }
    ],
    flowDigitalDetail: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Suivi',
        url: '/cxm-flow-traceability/list'
      },
      {
        label: 'Détail de la campagne',
        url: '/cxm-flow-traceability/flow-detail-digital'
      }
    ],
    flowDocumentPortalDetail: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Suivi',
        url: '/cxm-flow-traceability/list'
      },
      {
        label: 'Détail de l’envoi unitaire',
        url: '/cxm-flow-traceability/flow-document-detail'
      }
    ]
  },
  definitionDirectory: {
    list: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Définition annuaire',
        url: '/cxm-directory/list-definition-directory'
      }
    ],
    create: [
      {
        label: 'Accueil ',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Utilisateurs',
        url: '/cxm-profile/users/list-user'
      },
      {
        label: 'Créer un annuaire',
        url: '/cxm-directory/create'
      }
    ],
    edit: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Gérer annuaire',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Définition annuaire',
        url: '/cxm-directory/edit'
      }
    ],
    view: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Gérer annuaire',
        url: '/cxm-directory/list-definition-directory'
      },
      {
        label: 'Définition annuaire',
        url: '/cxm-directory/edit'
      }
    ]
  },
  directoryFeed: {
    list: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-directory/directory-feed'
      },
      {
        label: 'Alimentation annuaire',
        url: '#'
      }
    ],
    directoryDetail: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-directory/directory-feed'
      },
      {
        label: 'Alimentation annuaire',
        url: '#'
      }
    ],
    directoryAdd: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-directory/directory-feed'
      },
      {
        label: 'Alimentation annuaire',
        url: '/cxm-directory/directory-feed/detail'
      },
      {
        label: 'Insérer une ligne',
        url: '#'
      }
    ]
  },
  profile: {
    list: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-profile/list-profiles'
      },
      {
        label: 'Profils',
        url: '/cxm-profile/list-profiles'
      }
    ],
    create: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-profile/list-profiles'
      },
      {
        label: 'Profils',
        url: '/cxm-profile/list-profiles'
      },
      {
        label: 'Créer un profil',
        url: '/cxm-profile/create-profile'
      }
    ],
    edit: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-profile/list-profiles'
      },
      {
        label: 'Profils',
        url: '/cxm-profile/list-profiles'
      },
      {
        label: 'Modifier un profil',
        url: '/cxm-profile/update-profile/:id'
      }
    ]
  },
  user: {
    list: [
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
      }
    ],
    create: [
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
        label: 'Créer un utilisateur',
        url: '/cxm-profile/users/list-user'
      }
    ],
    edit: [
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
        label: 'Modifier un utilisateur',
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
        label: 'Modifier les utilisateurs',
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
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client',
        url: '/cxm-profile/client/list'
      }
    ],
    create: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Créer Client',
        url: '/cxm-profile/client/c/create'
      }
    ],
    modify: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Modification Client',
        url: '/cxm-profile/client/c/modify/:id'
      }
    ],
    configuration: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Modification Client',
        url: '/cxm-profile/client/c/configuration'
      }
    ],
    settingDigitalChannel: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Client',
        url: '/cxm-profile/client/list'
      },
      {
        label: 'Modification Client',
        url: '/cxm-profile/client/c/setting-digital-channel'
      }
    ],
  },
  setting: {
    list: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-setting/manage-resource/list'
      },
      {
        label: 'Ressources',
        url: '/cxm-setting/manage-resource/list'
      }
    ],
  },
  analytics: {
    list: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Statistiques',
        url: '/cxm-analytics/report-space'
      },
    ]
  },
  envelope_reference: {
    list: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-setting/envelope-reference/list'
      },
      {
        label: 'Référence envelope',
        url: '/cxm-setting/envelope-reference/list'
      }
    ],
    create: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-setting/envelope-reference/list'
      },
      {
        label: 'Référence enveloppe',
        url: '/cxm-setting/envelope-reference/list'
      },
      {
        label: 'Ajout une référence',
        url: '/cxm-setting/envelope-reference/create'
      }
    ],
    edit: [
      {
        label: 'Accueil',
        url: '/dashboard'
      },
      {
        label: 'Paramétrage',
        url: '/cxm-setting/envelope-reference/list'
      },
      {
        label: 'Référence enveloppe',
        url: '/cxm-setting/envelope-reference/list'
      },
      {
        label: 'Modifier une référence',
        url: '/cxm-setting/envelope-reference/update'
      }
    ],
  },
};
