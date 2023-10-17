import {store} from '@/redux';
import {splitUserCompany} from '@/utils/common/String';
/**
 * It's used to set gravitee header with companyId_coporateId_endUserId to track usage on gravitee APIM.
 * */

export const graviteeTransactionId = () => {
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  const cUuid = store.getState().authentication.C_UUID ?? 'CUUID';

  return (
    company.companyUuid +
    '_' +
    cUuid +
    '_' +
    store.getState().authentication.sid
  );
};
