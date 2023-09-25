import { createAction, props } from '@ngrx/store';
import {
  ConfigurationForm,
  ConfigurationsModel,
  CriteriaDistributionFormModel,
  Functionality,
  HubAccountDto,
  HubDistributePopupModel,
  IClientDepositModePayload,
  MetadataModel,
  MetadataPayloadType,
  PreferencePayload,
  ServiceProviderDto
} from '../../models';
import { HttpErrorResponse } from '@angular/common/http';
import { PostalConfigurationVersion } from '../../models/postal-configuration-version.model';

export const uploadClientDocument = createAction(
  '[cxm client / upload client file]',
  props<{ form: any }>()
);
export const uploadClientDocumentSuccess = createAction('[cxm client / upload client file success]', props<{ file: { fileId: string, filename: string, fileSize: number } }>());
export const uploadClientDocumentFail = createAction('[cxm client / upload client file fail]');
export const uploadClientDocumentProgress = createAction('[cxm client / upload client file progress]');
export const removeUploadedFile = createAction('[cxm client / upload client file remove]');
export const removeUploadedFileSuccess = createAction('[cxm client / remove uploaded file success]');

export const initFormConfig = createAction('[cxm client / init client form]');
export const loadClientForm = createAction('[cxm client/ load client form]', props<{ clientId?: string }>());
export const unloadClientForm = createAction('[cxm client / unload client form]');
export const loadClientInfoSuccess = createAction('[cxm client / load client info success]', props<{ clientInfo: any, holidays: any }>());
export const loadClientInfoFail = createAction('[cxm client / load client info fail ]', props<{ httpError: any }>());

export const updateClientForm = createAction('[cxm client / update client form]', props<{ form: any }>());
export const validateFormHasChange = createAction('[cxm client / validation form has changes]');
export const updateClientDivision = createAction('cxm client / update client division', props<{ divisions: any }>());
export const updateClientFunctionality = createAction('cxm client / update client functionality', props<{ functionalities: Array<string> }>());
export const updateClientOfloading = createAction('[cxm client / update client offload]', props<{ offloading: any }>());

export const updateClientFillers = createAction('[cxm client / update fillers]', props<{ fillers: any }>());

export const loadClientFunctionality = createAction('cxm client / load client functionalities');
export const loadClientFunctionalitySuccess = createAction('[cxm client / load client functionalities success]', props<{ data: Array<Functionality> }>());
export const loadClientFunctionalityFail = createAction('[cxm client / load client functionalities fail ]', props<{ httpError: any }>());

export const attempToMoveNextStep = createAction('[cxm client / move next step]');
export const attempToMovePrevStep = createAction('[cxm client / move prev step]');

export const switchToStep = createAction('[cxm client / move to step]', props<{ step: number }>());

export const validteBeforeSubmit = createAction('[cxm client / valid before submit]');
export const submitCreateClient = createAction('[cxm client / submit create]');
export const submitCreateClientSuccess = createAction('[cxm client / submit create success]');
export const submitCreateClientFail = createAction('[cxm client / submit create fail]');

export const setLockedFormFalse = createAction('[cxm client / fetch locked form false]');
export const setLockedFormTrue = createAction('[cxm client / fetch locked form true]');

export const submitModifyClient = createAction('[cxm client / submit modify]');
export const submitModifyClientSuccess = createAction('[cxm client / submit modify success ]');
export const submitModifyClientFail = createAction('[cxm client / submit modify fail]', props<{ httpError: any }>());

export const loadHolidaySuccess = createAction('[cxm client / load holiday defail success]', props<{ holiday: any[] }>())

export const nonceAction = createAction('[cxm client / nonce]');

export const forceDechargement = createAction('[cxm client / force dechargement]');
export const forceDechargementSuccess = createAction('[cxm client / force dechargement success]');
export const forceDechargementFail = createAction('[cxm client / force dechargement fail]');

// Deposit mode.
export const attemptDepositModeToForm = createAction('[cxm client / attempt deposit mode to form]', props<{ depositModes: IClientDepositModePayload[] }>());

// Distribute criteria.
export const attemptDistributeCriteriaToForm = createAction('[cxm client / attempt distribute criteria]', props<{ distributeCriteria: PreferencePayload }>());
export const manageDigitalDistributeCriteria = createAction('[cxm client / management digital distribute criteria]', props<{ distributeCriteria: CriteriaDistributionFormModel }>());
export const updateSettingCriteria = createAction('[cxm client / update setting criteria distribution]');
export const updateSettingCriteriaSuccess = createAction('[cxm client / update setting criteria distribution success]');
export const updateSettingCriteriaFail = createAction('[cxm client / update setting criteria distribution fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Configuration file management.

export const manageConfigurationFile = createAction('[cxm client / manage configuration file]');
export const switchIdentificationMode = createAction('[cxm client / switch identification mode]');
export const switchIdentificationModeSuccess = createAction('[cxm client / switch identification mode success]');
export const attemptToSwitchIdentificationMode = createAction('[cxm client / attempt to switch identification mode]', props<{ enabled: boolean }>());

// Get all model configurations.
export const attemptClientNameInConfigurationFile = createAction('[cxm client / attempt client name in configuration]', props<{ clientName: string }>());
export const fetchModelConfigurations = createAction('[cxm client / fetch model configuration]');
export const fetchModelConfigurationsSuccess = createAction('[cxm client / fetch model configuration success]', props<{ configurations: ConfigurationsModel[]}>());
export const fetchModelConfigurationsFails = createAction('[cxm client / fetch model configuration fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Get all configuration version.
export const fetchConfigurationVersion = createAction('[cxm client] / fetch configuration version');
export const fetchConfigurationVersionSuccess = createAction('[cxm client] / fetch configuration version success', props<{ configVersion: PostalConfigurationVersion [] }>());
export const fetchConfigurationVersionFail = createAction('[cxm client] / fetch configuration version fail', props<{ httpErrorResponse: HttpErrorResponse }>());

// Order, add, modify, delete model configuration.
export const reorderModelConfiguration = createAction('[cxm client / reorder model configuration]', props<{ previousIndex: number, currentIndex: number }>());
export const addModelConfiguration = createAction('[cxm client / add model configuration]', props<{ configuration: ConfigurationForm }>());
export const modifyModelConfiguration = createAction('[cxm client / modify model configuration]', props<{ configuration: ConfigurationForm }>());
export const deleteModelConfiguration = createAction('[cxm client / delete model configuration]', props<{ configuration: ConfigurationForm }>());

// Download INI configuration file.
export const downloadINIConfigurationFile = createAction('[cxm client / download INI configuration file]');
export const downloadINIConfigurationFileSuccess = createAction('[cxm client / download INI configuration file success]', props<{ file: string}>());
export const downloadINIConfigurationFileFail = createAction('[cxm client / download INI configuration file fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// View client version configuration.
export const fetchTheVersionConfigurationById = createAction('[cxm client / fetch the configuration]', props<{ versionId: number, isPreview: boolean, configurationHasChanged?: boolean }>());

// Fetch the latest version configuration.
export const fetchTheLatestVersionConfiguration = createAction('[cxm client / fetch the latest version configuration]');

// Revert client version configuration.
export const revertConfiguration = createAction('cxm-client / revert the version configuration', props<{ referenceVersion: number }>());
export const revertConfigurationSuccess = createAction('cxm-client / revert the version configuration success', props<{response: PostalConfigurationVersion}>());
export const revertConfigurationFail = createAction('cxm-client / revert the version configuration fail', props<{error: HttpErrorResponse}>());

// Register new version.
export const registerNewConfiguration = createAction('[cxm client / register new configuration]');
export const registerNewConfigurationSuccess = createAction('[cxm client / register new configuration success]');
export const registerNewConfigurationFails = createAction('[cxm client / register new configuration fails]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Distribution HUB.
export const attemptServiceProviderForm = createAction('[cxm client / attempt service provider form]', props<{ serviceProviderForm: HubDistributePopupModel }>());

// Modify Digital channel metadata.
export const fetchMetadataByType = createAction('[cxm client / fetch metadata by type]', props<{ metadataType: MetadataPayloadType }>());
export const fetchMetadataByTypeFail = createAction('[cxm client / fetch metadata by type fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
export const attemptToModifyMetadata = createAction('[cxm client / attempt to modify metadata]', props<{ metadataType: MetadataPayloadType, metadata: Array<MetadataModel> }>());
export const modifyMetadataByType = createAction('[cxm client / modify metadata by type]', props<{ metadataType: MetadataPayloadType, metadata: Array<MetadataModel> }>());
export const modifyMetadataByTypeSuccess = createAction('[cxm client / modify metadata by type success]');
export const modifyMetadataByTypeFail = createAction('[cxm client / modify metadata by type fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
export const setClientName = createAction('[cxm client] / set client name]', props<{ clientName: string }>());

// Modify Hub access account.
export const fetchHubAccessAccount = createAction('[cxm client / fetch hub access account]');
export const fetchHubAccessAccountFail = createAction('[cxm client / fetch hub access account fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
export const attemptToModifyHubAccount = createAction('[cxm client / attempt to modify hub account]', props<{ hubAccount: HubAccountDto }>());
export const modifyHubAccessAccount = createAction('[cxm client / modify hub access account]', props<{ hubAccount: HubAccountDto }>());
export const modifyHubAccessAccountSuccess = createAction('[cxm client / modify hub access account successfully]');
export const modifyHubAccessAccountFail = createAction('[cxm client / modify hub access account fail]', props<{ httpErrorResponse: HttpErrorResponse }>());

// Modify service providers.
export const fetchServiceProvider = createAction('[cxm client / fetch service provider]');
export const fetchServiceProviderFail = createAction('[cxm client / fetch service provider fail]', props<{ httpErrorResponse: HttpErrorResponse }>());
export const attemptToModifyServiceProvider = createAction('[cxm client / attempt to modify service provider]', props<{ serviceProvider: ServiceProviderDto, serviceProviderCriteria: ServiceProviderDto }>());
export const modifyServiceProvider = createAction('[cxm client / modify service provider]', props<{ serviceProvider: ServiceProviderDto }>());
export const modifyServiceProviderSuccess = createAction('[cxm client / modify service provider successfully]');
export const modifyServiceProviderFail = createAction('[cxm client / modify service provider fail]', props<{ httpErrorResponse: HttpErrorResponse }>());


export const restoreFormData = createAction('[cxm client / restore form data]', props<{ data: any }>());
