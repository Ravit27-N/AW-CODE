import env from '../../../env.config';

export const viewImage = (path: string) => {
  return `${env.VITE_BASE_URL_CORPORATE_PUBLIC}/api/corporate-settings/view/content?fileName=${path}`;
};
