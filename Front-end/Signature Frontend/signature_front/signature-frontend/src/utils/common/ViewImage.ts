import {baseQuery} from '@/redux/slides/RTKBaseQuery';
import {formatBaseAPI} from './ApiFacade';

export const viewImage = (path: string) => {
  return `${formatBaseAPI(
    baseQuery.corporatePublic,
  )}/api/corporate-settings/view/content?fileName=${path}`;
};
