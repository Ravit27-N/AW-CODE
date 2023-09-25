/* eslint-disable prefer-rest-params */
/* eslint-disable no-var */

const flat = function (arraParam: any, dept?: number): any {
  const depth = dept === undefined ? 1 : dept;
  const result: any[] = [];
  const forEach = result.forEach;

  var flatDeep = function (arr: any, depth: any) {
    forEach.call(arr, function (val) {
      if (depth > 0 && Array.isArray(val)) {
        flatDeep(val, depth - 1);
      } else {
        result.push(val);
      }
    });
  };

  flatDeep(arraParam, depth);
  return result;
}


import {
  appLocalStorageConstant,
  ClientManagement,
  UserManagement,
} from '@cxm-smartflow/shared/data-access/model';
import { UserProfileUtil } from './user-profile-util';

/**
 * This utility class is used to manipulate user information.
 *
 * @author Chamrong THOR
 * @since 07/10/2022
 */
export class UserUtil {
  /**
   * Get all accessible functionalities and privileges of the current user.
   * This method will return a list of functionality and privilege keys.
   */
  public static getCurrentUserPrivilege(): string[] {
    let privileges: any[] = [];
    const profiles: any = JSON.parse(
      <string>(
        localStorage.getItem(
          appLocalStorageConstant.UserManagement.UserPrivilege
        )
      )
    );
    profiles?.functionalities.forEach((v: any) => {
      // Add functionality keys.
      v?.privileges?.forEach((v: any) => {
        if (!privileges.includes(v?.key)) {
          privileges = [...privileges, v?.key];
        }
      });

      // Add privilege keys.
      if (
        v?.privileges?.length > 0 &&
        !privileges.includes(v?.functionalityKey)
      ) {
        privileges = [...privileges, v?.functionalityKey];
      }
    });
    return privileges;
  }


  public static getUserPermissionLevel(): Map<string, { v: number, m: number }> {
    // Permission Level
    // 0: specification and owner
    // 1: service
    // 2: divisions
    // 3 client


    const profiles: any = JSON.parse(<string>(localStorage.getItem(appLocalStorageConstant.UserManagement.UserPrivilege)));
    const { functionalities } = profiles;

    const finalUserLv = flat(Array.from(functionalities).map((f: any) => {
      const pLevels = Array.from(f.privileges).map((p: any) => this.toPermissionLvKeyValue(p, p.key));
      const fLevel = this.toPermissionLvKeyValue(f, f.functionalityKey);
      return [fLevel, ...pLevels]
    }), 1);

    return new Map(finalUserLv);
  }
  public static getTargetPermiisionlevel(permission: any): Map<string, { v: number, m: number }> {

    const finalUserLv = flat(Array.from(permission).map((f: any) => {
      const pLevels = Array.from(f.privileges).map((p: any) => this.toPermissionLvKeyValue(p, p.key));
      const fLevel = this.toPermissionLvKeyValue(f, f.functionalityKey);
      return [fLevel, ...pLevels]
    }), 1);

    return new Map(finalUserLv);
  }

  private static toPermissionLvKeyValue(f: any, key: string) {
    const vMap = ["specific","user","service", "division", "client"];
    const mMap = ["specific","owner", "service", "division", "client"];

    const v = Math.max(1, vMap.indexOf(f.visibilityLevel));
    const m = Math.max(1, mMap.indexOf(f.modificationLevel));
    return [key, { v, m }]
  }

  public static isLesserLvThanProfile(lv: any, functionality: any){
    const vMap = ["specific","user","service", "division", "client"];
    const mMap = ["specific","owner", "service", "division", "client"];
    return (lv.v >= vMap.indexOf(functionality.visibility)) && (lv.m >= mMap.indexOf(functionality.modification));
  }

  public static isLesserLvThanProfilev2(lv: any, targetLv: any) {
    return targetLv ? (lv.v - targetLv.v >= 0) && (lv.m - targetLv.m >= 0) : true;
  }

  /**
   * To check the current user is admin.
   * This method will return true if the current user is admin.
   */
  public static isAdmin(): boolean {
    return JSON.parse(
      <string>(
        localStorage.getItem(
          appLocalStorageConstant.UserManagement.UserPrivilege || ''
        )
      )
    )?.admin;
  }

  public static getOwnerId(): number {
    return JSON.parse(
      <string>(
        localStorage.getItem(
          appLocalStorageConstant.UserManagement.UserPrivilege || ''
        )
      )
    )?.id;
  }

  /**
   * To check current user is modifiable.
   * Return true if current user modifiable.
   */
  public static modifyClient(): boolean {
    if (UserUtil.isAdmin()) {
      return true;
    } else {
      return UserProfileUtil.canAccess(
        ClientManagement.CXM_CLIENT_MANAGEMENT,
        ClientManagement.MODIFY
      );
    }
  }

  /**
   *
   */
  public static canAccess(func: string, priv: string) {
    return UserProfileUtil.canAccess(func, priv);
  }



  public static aggregateProfileFormBaseOnUser(form: any, permission: any) {
    const isAdmin = UserUtil.isAdmin();
    if (isAdmin) {

      return Array.from(form)
      .map((item: any) => {
        const func = Array.from(item.func).map((f: any) => {
          return { ...f, v: 4, m: 4, allowed: true }
        })
        return { ...item, v: 4, m: 4, func, allowed: true }
      });
    } else {
      const authzPermission = UserUtil.getUserPermissionLevel();
      const result = Array.from(form)
        .map((item: any) => {
          const lv = authzPermission.get(item.code);
          let isFunctionAllowed = lv !== undefined && UserUtil.isLesserLvThanProfilev2(lv, permission.get(item.code));

          const func = Array.from(item.func).map((f: any) => {
            const flv = authzPermission.get(f.code);
            const isPrevilegeAllowed = flv !== undefined && UserUtil.isLesserLvThanProfilev2(flv, permission.get(f.code));

            return isPrevilegeAllowed ?
            { ...f, v: flv?.v, m: flv?.m, allowed: true } :
            { ...f, v:4, m: 4, allowed: false }
          })


          isFunctionAllowed = isFunctionAllowed && func.every(x => x.allowed===true);

          return isFunctionAllowed ?
          { ...item, v: lv?.v, m: lv?.m, func, allowed: true } :
          { ...item, func, v:4, m: 4, allowed: false }
        });
      return this.updateFunctionalFromPrivilegeDetails(result);
    }
  }

  private static updateFunctionalFromPrivilegeDetails(form: any) {
    return  Array.from(form)
    .map((item: any) => {
      const func = Array.from(item.func).map((f: any) => {
        return {...f}
      })
      //Check the greatest privilege of visibility/modification to each item.
      let visibility = 0;
      let modify = 0;
      func.forEach(f => {
        if (f.visibility && visibility < f.v) {
          visibility = f.v;
        }
        if (f.modification && modify < f.m) {
          modify = f.m;
        }
      });

      //assign visibility/modification with the greatest privilege to each item.
      item.v = visibility > 0 ? visibility : item.v;
      item.m = modify > 0 ? modify : item.m;

      //assign visibility/modification with the greatest privilege to each sub item.
      func.forEach(f => {
        if (f.visibility) {
          f.v = visibility;
        }
        if (f.modification) {
          f.m = modify;
        }
      });
      return {...item, func};
    });
  }
}
