import env from '../../../env.config';

/**
 * Use to check and change environment that support local backend
 */
export const isLocalBackend: boolean =
  `${env.VITE_LOCAL_BACKEND}`.toLowerCase() === 'true';

export const baseQuery = {
  baseApiUrl: `${env.VITE_GATEWAY}${
    isLocalBackend ? env.VITE_DEFAULT_GATEWAY_PORT : ''
  }`, // we add a default port when use with local backend
  // baseApiUrlKeyCloak: env.VITE_GATEWAY_KEYCLOAK,
  profileManagement: env.VITE_PROFILE_MANAGEMENT,
  projectManagement: env.VITE_PROJECT_MANAGEMENT,
  signProcess: env.VITE_SIGN_PROCESS,
  forgotPassword: env.VITE_FORGOT_PASSWORD,
  processControl: env.VITE_PROCESS_CONTROL,
  corporateProfile: env.VITE_CORPORATE_PROFILE,
  corporatePublic: env.VITE_CORPORATE_PUBLIC,
  keycloak: env.VITE_KEYCLOAK,
  api: env.VITE_CONTEXT_API,
  v1: env.VITE_VERSION,
};
