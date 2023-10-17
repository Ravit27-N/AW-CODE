/**
 * It is used to split “USER_COMPANY": "2;Certigna;37219869-2f29-4590-a80a-8e6abb0e2c6c", by ';'
 * */
import {$isstring} from '@/utils/request/common/type';

export type UserCompanyType = {
  companyId: string | number;
  companyName: string;
  companyUuid: string;
};
export function splitUserCompany(str = '', reg = ';'): UserCompanyType {
  if ($isstring(str)) {
    const arr = str.split(reg);
    return {
      companyId: arr[0] ?? 'companyId',
      companyName: arr[1] ?? 'companyName',
      companyUuid: arr[2] ?? 'companyUuid',
    };
  }
  return {
    companyId: '',
    companyName: '',
    companyUuid: '',
  };
}
