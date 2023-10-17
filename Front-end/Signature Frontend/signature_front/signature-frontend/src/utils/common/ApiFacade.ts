import {store} from '@/redux';
import env from '../../../env.config';
import {splitUserCompany} from '@/utils/common/String';
import {baseQuery, isLocalBackend} from '@/redux/slides/RTKBaseQuery';

export const ApiFacade = (key: string) => {
  const companyInfo = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );

  if (isLocalBackend) {
    return `${env.VITE_GATEWAY}${key}${env.VITE_CONTEXT_API}${env.VITE_VERSION}`;
  }

  const api = `${companyInfo.companyUuid}${key}${env.VITE_CONTEXT_API}${env.VITE_VERSION}`;
  return api;
};

/**
 * Format base api to support local backend,
 * @prop `key`: port or path that run the service
 * @prop `middleText`: use with Gravitee, it the text that stay between `baseApiUrl` and `key`
 */
export const formatBaseAPI = (key = '', middleText = '') => {
  if (isLocalBackend) return `${env.VITE_GATEWAY}${key}`;
  return `${baseQuery.baseApiUrl}${middleText}${key}`;
};

/**
 * formated keycloak api
 */
export const formatKeycloakAPI = isLocalBackend
  ? `${baseQuery.keycloak}`
  : `${baseQuery.baseApiUrl}${baseQuery.keycloak}`;
