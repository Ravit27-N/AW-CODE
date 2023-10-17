import {Route} from '@/constant/Route';
import {UserRoleEnum} from '@/utils/request/interface/User.interface';
export const ActiveRole = (r: UserRoleEnum) => {
  const allRoles = {
    [UserRoleEnum.COPERATE_ADMIN]: Route.corporate.COMPANY_PAGE,
    [UserRoleEnum.ENDUSER]: Route.HOME_ENDUSER,
    [UserRoleEnum.SUPERADMIN]: Route.HOME_SUPER,
  };
  /** will return the path that match with key of allRoles **/
  return allRoles[r];
};
