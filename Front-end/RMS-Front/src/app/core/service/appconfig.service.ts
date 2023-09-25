import { Injectable } from '@angular/core';
import { MailconfigService } from './mailconfig.service';
import { RoleService } from './user-role.service';

@Injectable()
export class AppConfigService {
  private config: any;
  private roleMapper = {};

  constructor(
    private configService: MailconfigService,
    private roleService: RoleService,
  ) {}

  get(key: string): any {
    return this.config?.[key];
  }

  tryLoad(): Promise<[any, any]> {
    // return merge(from(this.loadAppRoleInfo()), from(this.loadDefaultConfig()));
    return Promise.all([this.loadAppRoleInfo(), this.loadDefaultConfig()]);
  }

  loadAppRoleInfo(): Promise<void> {
    return new Promise((resolve) => {
      this.roleService.get(1, 100).subscribe((data) => {
        data.contents.forEach((item) => {
          this.roleMapper[item.name] = item.privileges;
          // Object.assign(this.roleMapper, item.name, item.privilege);
        });
        resolve();
      });
    });
  }

  loadDefaultConfig(): Promise<void> {
    return new Promise((resolve) => {
      this.config = JSON.parse(sessionStorage.getItem('RMS__APP_CONFIG'));
      if (this.config && Object.keys(this.config).length > 0) {
        resolve();
      } else {
        this.configService.getConfigList(1, 100).subscribe((list) => {
          const format = list.contents.find(
            (x) => x.configKey === 'system.config.datetime.format',
          );
          this.config = {
            datetimeFormat: format?.configValue
              ? format.configValue
              : 'dd/MMM/yyyy hh:mm a',
            datetimeFormatNoTime: 'dd/MMM/yyyy',
          };

          localStorage.setItem('RMS__APP_CONFIG', JSON.stringify(this.config));
          resolve();
        });
      }
    });
  }

  getRoleProjection(roles: string[]) {
    const mapToAccess = (access) => ({
      id: access.moduleId,
      name: access.moduleName,
      deleteAble: access.deleteAble,
      editAble: access.editAble,
      insertAble: access.insertAble,
      viewAble: access.viewAble,
    });

    const mergeAccess = (access, cur) => {
      if (access[cur.moduleName]) {
        // Need to merge access
        const currentAccess = { ...access[cur.moduleName] };
        const otherAccess = mapToAccess(cur);

        if (otherAccess.insertAble) {
          currentAccess.insertAble = otherAccess.insertAble;
        }
        if (otherAccess.viewAble) {
          currentAccess.viewAble = otherAccess.viewAble;
        }
        if (otherAccess.editAble) {
          currentAccess.editAble = otherAccess.editAble;
        }
        if (otherAccess.deleteAble) {
          currentAccess.deleteAble = otherAccess.deleteAble;
        }

        access[cur.moduleName] = currentAccess;
      } else {
        access[cur.moduleName] = mapToAccess(cur);
      }

      return access;
    };

    return roles
      .map((role) => this.roleMapper[role])
      .filter((role) => !!role)
      .reduce((prev, cur) => prev.concat(cur), [])
      .reduce(mergeAccess, {});
  }
}
