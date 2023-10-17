import {store} from '@/redux';
import {baseQuery} from '@/redux/slides/RTKBaseQuery';
import {Verb} from '../request/interface/type';
import {graviteeTransactionId} from './Gravitee';
import {ApiFacade, formatBaseAPI} from '@/utils/common/ApiFacade';

type IDownloadLink = {
  token: string;
  docId: string;
  company_uuid: string;
};

export const downloadLink = ({
  company_uuid,
  docId,
  token,
}: IDownloadLink): string => {
  return `${formatBaseAPI(
    baseQuery.signProcess,
    `/${company_uuid}`,
  )}/api/sign/download/${company_uuid}?token=${token}&docId=${docId}`;
};
export const downloadCurrentDocumentProject = async (
  docId: string,
  flowId: string,
) => {
  try {
    const res = await fetch(
      `${ApiFacade(
        baseQuery.signProcess,
      )}/sign/download/${flowId}?docId=${docId}`,
      {
        method: Verb.Get,
        headers: {
          'Content-Type': 'blob',
          Authorization: `Bearer ` + store.getState().authentication.userToken,
          'X-Gravitee-Transaction-Id': graviteeTransactionId(),
        },
      },
    );
    const blob = await res.blob();
    const file = window.URL.createObjectURL(blob);
    return file;
  } catch (e) {
    console.warn(e);
  }
};
