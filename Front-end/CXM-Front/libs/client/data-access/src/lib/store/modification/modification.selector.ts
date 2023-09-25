import { featureClientCreationModify } from './modification.reducers';
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { InitialClientStateModel } from '../../models';
import { PostalConfigurationVersion } from '../../models/postal-configuration-version.model';


const featureClientModify = createFeatureSelector<InitialClientStateModel>(featureClientCreationModify);

export const selectClientUpload = createSelector(featureClientModify, (state) => state.upload);

export const selectClientData = createSelector(featureClientModify, (state) => state.client);

export const selectClientDivision = createSelector(featureClientModify, (state) => state.divisions);

export const selectNavigation = createSelector(featureClientModify, (state) => state.navigation);

export const selectIsLocked = createSelector(featureClientModify, (state) => state.isLocked)

export const selectTempClient = createSelector(featureClientModify, (state) => state.beforeClientModify);

export const selectMode = createSelector(featureClientModify, (state) => state.mode);

export const selectClientId = createSelector(featureClientModify, (state) => state.clientId);

export const modifyDocIdSelector = createSelector(featureClientModify, (state) => state.modifyDocId);

export const selectClientFunctionality = createSelector(featureClientModify, (state) => state.functionalities);

export const selectFormClientState = createSelector(featureClientModify, (state) => ({divisions: state.divisions, functionalities: state.functionalities}));

export const selectOffloadConfig = createSelector(featureClientModify, (state) => state.offloadConfig);

export const selectClientFillers = createSelector(featureClientModify, (state) => state.fillers);

// Flux form selectors.
export const selectClientDepositModes = createSelector(featureClientModify, (state) => state.depositModes);
export const selectIdentificationMode = createSelector(featureClientModify, (state) => state.portalConfigEnable);
export const selectDistributeCriteria = createSelector(featureClientModify, (state) => state.criteriaDistributions);

// Configuration file selectors.
export const selectConfigurations = createSelector(featureClientModify, (state) => state.configurations);
export const selectClientAllStates = createSelector(featureClientModify, (state) => state);
export const selectConfigurationModified = createSelector(featureClientModify, (state) => state.configurationHasModified as boolean);

export const selectConfigurationVersion = createSelector(featureClientModify,
  (state: any) => state?.configurationVersion as PostalConfigurationVersion []);


// Configuration File action button.
export const selectIsPreviewConfigurationMode = createSelector(featureClientModify, (state) => state.isPreviewConfigurationMode);
