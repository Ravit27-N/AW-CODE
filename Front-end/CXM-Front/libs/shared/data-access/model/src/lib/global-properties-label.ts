import { globalPropertiesIcon as icon } from './global-properties-icon';

const required = 'This field is required !';
const maxLength = 'This field must be smaller or equal to 128 characters !';

export const globalPropertiesLable = {
  cxmTemplate: {
    emailingTemplate: {
      headerTitle: 'Design an emailing',
      create: {
        cardTitle: {
          parameterTitle: 'Creation of a model',
          summaryTitle: 'Summary',
          compositionTitle: 'Composition of a model',
        },
        parameter: {
          propertiesModel: {
            title: 'Properties',
            modelName: {
              label: 'Name of model*',
              placeholder: 'MODEL TEST',
              required,
              maxLength,
              isExist: 'Model name is already exist !',
              notValid: 'Please enter a valid model name !'
            },
            subjectMail: {
              label: 'Subject of mail*',
              placeholder: 'Test : {MOIS}',
              required,
              maxLength,
              isValid: 'Please enter a valid subject mail !',
              isMatched:
                'Subject mail does not match with the variable field !',
            },
            senderEmail: {
              label: 'Email of the sender*',
              placeholder: 'noreply@tessi.fr',
              required,
              maxLength,
              isValid: 'Please enter a valid sender mail !',
            },
            senderName: {
              label: 'Name of the sender*',
              placeholder: 'Tessi Communication',
              required,
            },
            unsubscribeLink: {
              label: 'Unsubscribe link*',
              placeholder: 'mailto:se.desabonner@tessi.fr',
              required,
              isValid:
                'Please enter a valid UnSubscribeLink (URL or mailto:test@gmail.com) !',
            },
          },
          variableField: {
            title: 'Variable fields',
            description:
              'The variable fields use to link with the column headers of the CSV file, and it is used as input when sending an email using this template. The name defined must correspond to the label of the associated column. The declared fields will be available in the template design as dynamic data.',
            empty: 'This field cannot be empty!',
            defaultValue:
              'Variable field must contain at least a value equal to email or mail !',
            duplicate: 'This variable is already exist !',
            required: 'This field cannot be empty !',
          }
        },
        summary: {
          label: {
            modelName: 'Name of model',
            subjectMail: 'Subject of mail',
            senderEmail: 'Email of the sender',
            senderName: 'Name of the sender',
            unsubscribeLink: 'Unsubscribe link',
            variableFields: 'Variable fields',
          },
          required
        },
        dialog: {
          title: 'Warning',
          body: 'Creating an emailing template was failed !',
        },
      },
      update: {
        cardTitle: {
          parameterTitle: 'Update of a model',
          summaryTitle: 'Summary',
          compositionTitle: 'Composition of a model',
        },
        label: {},
      },
      delete: {
        dialog: {
          buttonSave: 'Save',
          buttonCancel: 'Cancel'
        }
      },
      emailTemplateComposition: {
        popUpConfirmation: {
          title: 'Show Confirmation',
          message: 'Changes have not been saved!'
        },
        snackBar: {
          success: 'Email template composition was saved successfully.',
          notSuccess: 'Email template composition was failed to save!'
        },
        dialog: {
          buttonCancel: 'Cancel',
          buttonConfirm: 'Confirm',
          message: 'Your changes have not been saved. If you wish to proceed it will be lost.',
          title: ''
        }
      },
      summary: {
        buttonReturn: 'Return',
        snackBar: {
          error: 'Fail to load data!'
        }
      },
      list: {
        button: {
          createModel: 'Create a model',
        },
        title: 'List of models',
      },
      sideBar: [
        {
          icon: icon.setting,
          text: 'Parameters',
        },
        {
          icon: icon.edit,
          text: 'Composition',
        },
        {
          icon: icon.description,
          text: 'Summary',
        },
      ],
      button: {
        accessComposition: 'Access to the composition',
      },
    },
  },
  cxmCampaign: {
    followMyCampaign: {
      headerTitle: 'Follow my campaigns',
      list: {
        tilte: 'List of campaignes',
        tableHeader: {
          creationDate: 'Creation date',
          name: 'Last name',
          model: 'Model',
          status: 'Status',
          statusDate: 'Status date',
          progress: '%',
          date: 'Data',
        },
      },
      detail: {
        title: 'Detail of a campaign : ',
        label: {
          campaignName: 'Name of the campaign ',
          status: 'Status',
          statusDate: 'Status date',
          creationDate: 'Creation date',
          createdBy: 'Created by',
          numberOfShipment: 'Number of shipments',
          sendingTime: 'Date / time of sending',
        },
      },
      button: {
        createCampaign: 'Create a campaign',
        viewMail: 'View the email',
        viewContact: 'View contacts',
      },
    },
    manageMyCampaign: {
      headerTitle: 'Manage my campaigns',
      choiceOfModel: {
        title: 'Creation of a campaign (1/4): choice of model',
      },
      settingParameter: {
        title: 'Creation of a campaign (2/4): Definition of parameters',
        linkEdit: 'Edit template settings',
        selectedModel: {
          label: 'Selected model',
          placeholder: '',
        },
        campaignName: {
          label: 'Name of the campaign',
          placeholder: '',
        },
        subjectMail: {
          label: 'Subject of mail',
          placeholder: '',
        },
        sender: {
          label: 'Sender',
          placeholder: '',
        },
        sendingSchedule: {
          label: 'Sending schedule',
          placeholder: '',
        },
      },
      recipientSelection: {
        title: 'Creation of a campaign (3/4): recipients',
        tableHeader: {
          email: 'Email',
          lastName: 'Last name',
          firstName: 'First name',
          amount: 'Amount',
        }
      },
      generateEmail: {
        title: 'Creation of a campaign (4/4): Summary',
        label: {
          selectedModel: 'Selected model: ',
          subjectMail: 'Subject of mail: ',
          sender: 'Sender: ',
          sendingSchedule: 'Sending schedule: ',
          recipient: 'Recipients: ',
          bat: 'B.A.T.',
        },
        dialog: {
          cardTitle: 'Send one B.A.T.',
          title: 'List of destinators',
          description:
            'Variable fields are powered with the data of the first recipient.',
        },
        button: {
          send: 'Send',
          sendTest: 'Send test',
          preview: 'Preview',
          validate: 'Validate'
        },
      },
    },
  },
  button: {
    summary: 'Summary',
    save: 'Save',
    return: 'Return',
    next: 'Next',
    previous: 'Previous',
    submit: 'Submit',
    ok: 'OK',
    cancel: 'Cancel',
    comfirmDelete: 'Yes, delete it!',
    browse:'Browse'
  },
  input: {
    placeholder: {
      search: 'Search',
    },
  },
  dialog: {
    warning: {
      title: '',
      body: '',
    },
    success: {
      title: '',
      body: '',
    },
    error: {
      title: '',
      body: '',
    },
    comfirmDelete: {
      title: 'Are you sure?',
      body: 'You want to remove this.',
    },
  },
  navBar: [
    {
      text: 'Submit a letter',
      link: '',
    },
    {
      text: 'Design an emailing or an SMS',
      link: 'cxm-template/design-model',
    },
    {
      text: 'Manage my campaigns',
      link: 'cxm-campaign/manage-my-campaign',
    },
    {
      text: 'Follow my campaigns',
      link: 'cxm-campaign/follow-my-campaign',
    },
    {
      text: 'Manage contacts',
      link: '',
    },
    {
      text: 'Manage inserts',
      link: '',
    },
    {
      text: 'Reporting',
      link: '',
    },
  ],
  footerBar: [
    {
      text: 'CGV',
      link: '',
    },
    {
      text: 'Legal Notice',
      link: '',
    },
    {
      text: 'Personal data',
      link: '',
    },
    {
      text: 'Cookies',
      link: '',
    },
    {
      text: 'Contact',
      link: '',
    },
    {
      text: 'FAG',
      link: '',
    },
  ],
  errorCases: {
    messages: {
      notBeenSaved: 'Changes have not been saved.',
      noMatchFound: 'No match found.',
      required: 'All required fields are not filled in.',
      cannotBePerformed:
        'This variable field is used in the publication. Its removal is impossible.',
      invalidProperties: 'There is invalidate input in the properties',
    },
    cxmTemplate: {
      emailTemplate: {
        saveSuccess: 'The template has been saved.',
        updateSuccess: 'The template has been updated.',
        deleteSuccess: 'You deleted an emailing template successfully.',
      },
    },
  },
  login: {
    label: 'Login',
    username: {
      placeholder: '',
      label: 'Username',
      error: 'Please enter username !',
    },
    password: {
      placeholder: '',
      label: 'Password',
      error: ' Please enter password !',
    },
    information: {
      descriptionTitle: 'Welcome',
      description:
        'Until now, when implementing micro-frontends, you had to dig a little into the bag of tricks. One reason is sure that current build tools and frameworks do not know this concept. Module Federation initiates a change of course here.',
    },
    forgotPassword: 'Forgot your password ?',
  },
  color:{
    red:'red',
    lawngreen:'#32CC00'
  }

};
