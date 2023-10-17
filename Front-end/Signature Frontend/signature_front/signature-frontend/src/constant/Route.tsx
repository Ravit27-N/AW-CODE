export const Route = {
  ROOT: '/',
  LOGIN: '/login',
  CHANGE_MODE: '/change-mode',
  CHANGE_PASSWORD: '/change-password',
  HOME_SUPER: '/super_admin',
  HOME_CORPORATE: '/corporate/dashboard',
  HOME_ENDUSER: '/dashboard',
  HOME_ENDUSER_WITH_PROJECT_ID: '/dashboard/:projectId',
  MY_PROFILE: '/my-profile',
  FOLDER: '/folder',
  MODEL: '/model',
  MODEL_WITH_ID: '/model/template/:templateId',
  MODEL_PROJECT_WITH_ID: '/model/project/:projectId/template/:templateId',
  SETTING: '/setting',
  QUESTION: '/question',
  REGISTER_CORPORATE: '/corporate-register',
  FORGOT_PASSWORD: '/forgot-password',
  RESET_PASSWORD: `/reset-password`,
  PASSWORD_MODIFY: '/password-modify',
  //      end-user
  project: {
    projectDetail: '/project',
  },
  endUser: {
    viewProfile: '/profile',
    assignedProject: '/assigned-project',
  },
  profile: {
    waitAuthActivate: '/activate-account',
    authActivate: '/activate',
  },
  /**
   * It is used for participant
   * */
  participant: {
    /**
     * It is used for Expired
     * */
    expiredProject: '/expired-project',
    /**
     * It is used for Approve
     * */
    approveInvite: '/approve-invitation',
    approveDocument: '/approve-document',
    viewApproveFile: '/participant-approve/file',

    /**
     * It is used for Signatory
     * */
    root: '/participant-invitation',
    viewFile: '/participant-signatory/file',
    viewSignatoryFile: '/participant-signatory/files',
    signDocument: '/sign-document',
    advance: {
      root: '/participant-advance-invitation',
      expiredProject: '/expired-project',
      identity: '/participant-identity',
      viewFileToSign: '/participant-signatory-advance/files',
      identityDoc: {
        cardNationalElement: {
          cardNationalValidatePage: '/validate-card-national',
        },
        cardNational: '/card-national',
        passport: '/passport',
        stayBook: '/stay-book',
      },
    },

    /**
     * It is used for Recipient
     * */
    recipientInvite: '/reciptient-invitation',
    viewRecipientFile: '/participant-recipient/file',
    receiptDocument: '/receipt-document',
    refuseDocument: '/refuse-document',
    /**
     * It is used for Viewer
     * */
    viewerInvite: '/view-invitation',
    viewDocument: '/participant-view/file',
  },
  corporate: {
    MY_PROFILE: '/corporate/my-profile',
    SIGNATURE_PROJECTS: '/corporate/signature-projects',
    SIGNATURE_PROJECT_DETAIL: '/corporate/signature-projects/detail',
    FILE: '/corporate/file',
    QUESTION: '/corporate/question',
    COMPANY_PAGE: '/corporate/company-page',
    companyPage: {
      BRAND: 'brand',
      SERVICE: 'service',
      SETTING: 'setting',
    },
    GROUP: '/corporate/group',
    CONTACT: '/corporate/contact',
  },
  // super admin
  superAdmin: {
    main: '/super-admin',
    company: '/super-admin/company',
    user: '/super-admin/user',
    question: '/super-admin/question',
    companyPage: {
      BRAND: 'brand',
      SERVICE: 'service',
      SETTING: 'setting',
    },
  },
};
