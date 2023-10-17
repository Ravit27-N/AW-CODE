import env from '../../../env.config';

export const baseQuery = {
  baseUrlKeycloak: env.VITE_BASE_URL_KEYCLOAK,
  baseUrlProfile: env.VITE_BASE_URL_PROFILE,
  baseUrlForgotPassword: env.VITE_BASE_URL_FORGOT_PASSWORD,
  baseUrlCorporate: env.VITE_BASE_URL_CORPORATE,
  baseurlCorporatePublic: env.VITE_BASE_URL_CORPORATE_PUBLIC,
};
