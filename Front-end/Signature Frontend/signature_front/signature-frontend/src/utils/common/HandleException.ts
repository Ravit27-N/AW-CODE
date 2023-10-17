import {t, exists} from 'i18next';
import {Localization} from '@/i18n/lan';

export type IStatusCode = 400 | 401 | 403 | 404 | 406 | 500;
export type IResponseServer = {
  status: IStatusCode;
  data: {error: {message: string}};
};
const exception = {
  400: 'Bad request',
  401: 'Unauthorize',
  403: 'Forbidden',
  404: 'Not found',
  406: 'Not accepted',
  409: 'Conflict',
  500: 'Internal server error',
};

export const HandleException = (status: IStatusCode) => {
  const key = Localization('backend-error-status-code', `${status}`);
  return exists(key) ? t(key) : undefined;
};

export const ErrorServer = (error: IResponseServer): string => {
  const {
    status,
    data: {
      error: {message},
    },
  } = error;

  if (status === 500) return exception[status];

  return `${exception[status]}: ${message}`;
};
