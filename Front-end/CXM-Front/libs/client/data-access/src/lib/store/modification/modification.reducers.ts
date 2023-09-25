import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { createReducer, on } from '@ngrx/store';
import * as payloadUtils from './client-payload.utils';
import {
  configurationsToForms,
  immutableConfigurations,
  preferenceToDistributeCriteria,
  updateDistributeCriteria
} from './client-payload.utils';
import * as fromActions from './modification.actions';
import {
  CriteriaDistributionFormConstant,
  DepositModes,
  IClientDepositModePayload,
  InitialClientStateModel
} from '../../models';
import { moveItemInArray } from '@angular/cdk/drag-drop';
import { TitleCaseUtil } from '@cxm-smartflow/shared/utils';

export const featureClientCreationModify = 'feature-client-creation-modify';

const buttons = {
  backToList: 'button.cancel',
  nextStep: 'button.next',
  validateCreation: 'client.button.validationCreation',
  backPrev: 'button.previous',
  backToStep2: 'client.button.backToStep2',
  create: 'button.create',
  modify: 'button.modify'
}

const BY_DAYS_OFFLOAD_CONFIG = [
  { label: 'MON', check: false, hours: []},
  { label: 'TUE', check: false, hours: []},
  { label: 'WED', check: false, hours: []},
  { label: 'THU', check: false, hours: []},
  { label: 'FRI', check: false, hours: []},
  { label: 'SAT', check: false, hours: []},
  { label: 'SUN', check: false, hours: []}
]

export const initialState: InitialClientStateModel = {
  mode: 0, // 0: create, 1: modify
  isLocked: false,
  clientId: undefined,
  client: {
    name: '',
    contactFirstName: '',
    contactLastname: '',
    email: '',
    file: { }
  },
  divisions: [],
  upload: {
    progressing: false, uploaded: false
  },
  navigation: {
    allstep: 3,
    step: 1,
    canNext: false, canPrev: true,
    nextButton: buttons.nextStep, prevButton: buttons.backToList,
    ready: false, // wait for extrat steps to completed
    isAdmin: false
  },
  functionalities: [],
  offloadConfig: {
    byDays: BY_DAYS_OFFLOAD_CONFIG,
    byHolidays: [],
    ready: false
  },
  fillers: [
    { key: 'Filler1', value: '', enabled: false },
    { key: 'Filler2', value: '', enabled: false },
    { key: 'Filler3', value: '', enabled: false },
    { key: 'Filler4', value: '', enabled: false },
    { key: 'Filler5', value: '', enabled: false },
  ],
  depositModes: [],
  depositModesPayload: [],
  portalConfigEnable: false,
  configurations: [],
  configurationVersion: [],
  beforeConfigurationsChanged: [],
  configuration_clientName: '',
  beforeClientModify: {},
  criteriaDistributions: [],
  criteriaDistributionsSnapshot: [],
  criteriaDistributionPayload: {
     name: '',
    active: false,
  },
  modifyDocId: '',
  referenceConfigurationVersion: undefined,
  isPreviewConfigurationMode: false,
  configurationHasModified: false
}





export const reducer = createReducer(initialState,
  on(fromActions.loadClientForm, (state, props) => {
    const isAdmin = UserUtil.isAdmin();
    const mode = props.clientId ? 1: 0;

    return { ...state, clientId: props.clientId, mode,
      navigation: { ...state.navigation, ready: props.clientId ? false: true,  isAdmin, step: mode === 1 && isAdmin===false ? 2:1 }}
  }),

  on(fromActions.unloadClientForm, (state) => ({ ...initialState })),

  on(fromActions.updateClientForm, (state, props) => {
    const final = JSON.parse(JSON.stringify({ ...state, client: { ...state.client, ...props.form }}));
    final.client.contactLastname = TitleCaseUtil.convert(final.client.contactLastname)
    return final;
  }),

  on(fromActions.updateClientDivision, (state, props) => {
    const { divisions } = props;
    const e = { ...state, divisions };
    return e;
  }),

  on(fromActions.uploadClientDocument, (state, props) => ({ ...state, upload: { progressing: true, uploaded: false } })),
  on(fromActions.uploadClientDocumentFail, (state) => ({  ...state, upload: { progressing: false, uploaded: false }  })),
  on(fromActions.uploadClientDocumentSuccess, (state, props) => {
    let { client } = state;
    client = { ...client, file: { ...props.file } }
    return { ...state, upload: { progressing: false, uploaded: true }, client }
  }),
  on(fromActions.removeUploadedFile, (state, props) => {
    // may need to remove for client state
    return { ...state, upload: { progressing: false, uploaded: false } }
  }),
  on(fromActions.removeUploadedFileSuccess, (state, props) => {
    return {...state, client: {...state.client, file: { fileId: null, fileSize: null, filename: null }}}
  }),

  on(fromActions.switchToStep, (state, props) => {
    if(state.mode === 0) {
      switch (props.step) {
        case 3:
          return {
            ...state,
            navigation: {
              ...state.navigation,
              step: 3,
              nextButton: buttons.validateCreation,
              prevButton: buttons.backToStep2,
            },
          };
        case 2:
          return {
            ...state,
            navigation: {
              ...state.navigation,
              step: 2,
              nextButton: buttons.nextStep,
              prevButton: buttons.backPrev,
            },
          };
        default:
          return {
            ...state,
            navigation: {
              ...state.navigation,
              step: 1,
              nextButton: buttons.nextStep,
              prevButton: buttons.backToList,
            },
          };
      }
    }

    if (state.mode === 1) {
      switch (props.step) {
        case 6: {
          return {
            ...state,
            navigation: {
              ...state.navigation,
              step: 6
            }
          }
        }
        case 5: {
          return {
            ...state,
            navigation: {
              ...state.navigation,
              step: 5
            }
          }
        }
        case 4: {
          return {
            ...state,
            navigation: {
              ...state.navigation,
              step: 4
            }
          }
        }
        case 3:
          return {
            ...state,
            navigation: {
              ...state.navigation,
              step: 3,
              nextButton: buttons.modify,
              prevButton: buttons.backToList,
            },
          };
        case 2:
          return {
            ...state,
            navigation: {
              ...state.navigation,
              step: 2,
              nextButton: buttons.modify,
              prevButton: buttons.backToList,
            },
          };
        default:
          return {
            ...state,
            navigation: {
              ...state.navigation,
              step: 1,
              nextButton: buttons.modify,
              prevButton: buttons.backToList,
            },
          };
      }
    }



    return {...state};
  }),

  on(fromActions.loadClientInfoSuccess, (state, props) => {
    const { client, divisions, functionalities,  unloads, publicHolidays, fillers, portalConfigEnable, criteriaDistributions } = props.clientInfo;
    let { depositModes } = props.clientInfo;
    const upload = { progressing: false, uploaded: client.file.fileId ? true : false };
    const navigation = { ...state.navigation, nextButton: buttons.modify, prevButton: buttons.backToList, ready: true }

    const offloadConfig = payloadUtils.aggregateOffloadConfig(state.offloadConfig, unloads, publicHolidays,  props.holidays);
    const fillersConfig = payloadUtils.aggregateFillers(fillers, state.fillers);

    depositModes = Array.from(DepositModes as IClientDepositModePayload[])
      .map(dm => {
        if (dm.key === 'flow.traceability.deposit.mode.portal') {
          return { ...dm, scanActivation: functionalities.includes('cxm_flow_deposit') }
        }

        const scanActivation = Array.from(depositModes).find((d: any) => d.scanActivation && d.key == dm.key);
        return { ...dm, scanActivation: Boolean(scanActivation) };
      });

    const beforeClientModify = { clientInfo: props.clientInfo, holidays: props.holidays };
    const preferences = UserUtil.isAdmin()? preferenceToDistributeCriteria(CriteriaDistributionFormConstant, criteriaDistributions.preferences) : CriteriaDistributionFormConstant;
    return { ...state, client, divisions, upload, navigation, modifyDocId: client.file.fileId, portalConfigEnable, beforeClientModify,
      criteriaDistributions: preferences, criteriaDistributionsSnapshot: preferences,
      functionalities, offloadConfig, fillers: fillersConfig, depositModes, depositModesPayload: depositModes }
  }),
  on(fromActions.setLockedFormFalse, (state, props) => {
    return { ...state, isLocked: false };
  }),
  on(fromActions.setLockedFormTrue, (state, props) => {
    return { ...state, isLocked: true };
  }),
  on(fromActions.updateClientFunctionality, (state, props) => {
    const { functionalities } = props;
    const depositModes: any = Array.from(state.depositModesPayload)
      .map((dm: any) => (dm.key == 'flow.traceability.deposit.mode.portal'? { ...dm, scanActivation: functionalities.includes('cxm_flow_deposit') } : dm));
    return { ...state, functionalities, depositModes, depositModesPayload: depositModes };
  }),
  // Offloading feature
  on(fromActions.loadHolidaySuccess, (state, props) => {
    return { ...state, offloadConfig: {
      ...state.offloadConfig,
      byHolidays: props.holiday,
    }}
  }),
  on(fromActions.updateClientOfloading, (state, props) => {
    return { ...state,
      offloadConfig: {
        ...state.offloadConfig,
        byDays: props.offloading.byDays,
        byHolidays: props.offloading.byHolidays
      }
    }
  }),
  on(fromActions.updateClientFillers, (state, props) => {
    return {
      ...state,
      fillers: props.fillers
    }
  }),
  on(fromActions.attemptDepositModeToForm, (state, props) => {
    return { ...state, depositModesPayload: props.depositModes };
  }),
  on(fromActions.attemptToSwitchIdentificationMode, (state, props) => {
    return { ...state, portalConfigEnable: props.enabled };
  }),
  on(fromActions.manageConfigurationFile, (state, props) => {
    return { ...state, configuration_clientName: state.client.name, isLocked: false };
  }),
  on(fromActions.reorderModelConfiguration, (state, props) => {
    let { configurations } = JSON.parse(JSON.stringify(state));
    const { currentIndex, previousIndex } = props;

    moveItemInArray(
      configurations,
      previousIndex,
      currentIndex,
    );

    configurations = configurations.map((configuration: any, index: number) => {
      return { ...configuration, order: index}
    }).sort((a: any, b: any) => a.order - b.order);

    const configurationHasModified = hasConfigurationModified(state.beforeConfigurationsChanged, configurations);
    return { ...state, configurations, configurationHasModified };
  }),
  on(fromActions.fetchModelConfigurationsSuccess, (state, props) => {
    const isPreviewConfigurationMode = state?.isPreviewConfigurationMode;
    const configurations: any = configurationsToForms(props.configurations, immutableConfigurations, isPreviewConfigurationMode);
    const configurationHasModified = state?.configurationVersion?.length === 0;

    return { ...state, configurations, beforeConfigurationsChanged: configurations, configurationHasModified};
  }),
  on(fromActions.attemptClientNameInConfigurationFile, (state, props) => {
    return { ...state, configuration_clientName: props.clientName}
  }),
  on(fromActions.registerNewConfigurationSuccess, (state) => ({
    ...state,
    configurationHasModified: false,
    isPreviewConfigurationMode: false
  })),
  on(fromActions.fetchConfigurationVersion, (state, props) => ({
    ...state,
    configurationVersion: []
  })),
  on(fromActions.fetchConfigurationVersionSuccess, (state, props) => {
    return {
      ...state,
      configurationVersion: props?.configVersion || [],
      configurationHasModified: props?.configVersion?.length === 0, //TODO: Temporary solution: Show confirm button when version = 0.
    }
  }),
  on(fromActions.fetchConfigurationVersionFail, (state) => ({
    ...state,
    configurationVersion: []
  })),
  on(fromActions.addModelConfiguration, (state, props) => {
    const configurations = [...state.configurations, props.configuration];
    const configurationHasModified = hasConfigurationModified(state.beforeConfigurationsChanged, configurations);
    return { ...state, configurations, configurationHasModified };
  }),
  on(fromActions.modifyModelConfiguration, (state, props) => {
    const configurations: any = state.configurations.map(c => c.order === props.configuration.order? props.configuration: c)
      .sort((a: any, b: any) => a.order - b.order);
    const configurationHasModified = hasConfigurationModified(state.beforeConfigurationsChanged, configurations);
    return { ...state, configurations , configurationHasModified};
  }),
  on(fromActions.deleteModelConfiguration, (state, props) => {
    const configurations = state.configurations.filter(c => c.name !== props.configuration.name).sort((a: any, b: any) => a.order - b.order);
    const configurationHasModified = hasConfigurationModified(state.beforeConfigurationsChanged, configurations);
    return { ...state, configurations , configurationHasModified};
  }),
  on(fromActions.restoreFormData, (state, props) => {
    const fillersConfig = props.data.fillers;
    return {...props.data, fillers: fillersConfig};
  }),
  on(fromActions.manageDigitalDistributeCriteria, (state, props) => {
    return { ...state, isLocked: false };
  }),
  on(fromActions.attemptServiceProviderForm, (state, props) => {
    return { ...state, serviceProviderForm: props.serviceProviderForm };
  }),
  on(fromActions.attemptDistributeCriteriaToForm, (state, props) => {
    const criteriaDistributionsSnapshot = updateDistributeCriteria(state.criteriaDistributionsSnapshot, props.distributeCriteria);
    return { ...state, criteriaDistributionsSnapshot, criteriaDistributionPayload: props.distributeCriteria };
  }),
  on(fromActions.attemptToModifyMetadata, (state, props) => {
    return { ...state, metadataPopupModal: { metadata: props.metadata, metadataType: props.metadataType }};
  }),
  on(fromActions.setClientName, (state, props) => {
    return { ...state, client: { ...state.client, name: props.clientName }};
  }),
  on(fromActions.fetchTheVersionConfigurationById, (state, props) => {
    return { ...state, referenceConfigurationVersion: props.versionId, isPreviewConfigurationMode: props.isPreview,
      configurationHasModified: props?.configurationHasChanged || false };
  }),
  on(fromActions.revertConfigurationSuccess, (state) => ({
    ...state,
    isPreviewConfigurationMode: false,
    configurationHasModified: false
  })),
  on(fromActions.revertConfigurationFail, (state) => ({
    ...state,
    isPreviewConfigurationMode: false,
    configurationHasModified: false
  }))
);

const hasConfigurationModified = (originalConfiguration: any, newConfiguration: any) => {
  return JSON.stringify(transformToOriginalConfiguration(originalConfiguration))
    !== JSON.stringify(transformToOriginalConfiguration(newConfiguration));
};

const transformToOriginalConfiguration = (stateConfiguration: any) => {
  return  stateConfiguration?.map((configuration: { name?: any; order?: any; entries?: any; }) => {
    return {
      name: configuration?.name,
      order: configuration?.order,
      entries: configuration?.entries
    };
  })?.sort((a: any, b: any) => a.order - b.order);
};
