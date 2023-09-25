import { Injectable } from '@angular/core';
import { UserService } from './user.service';

export abstract class IUserAccessService {
  abstract canView(module: string): boolean;
  abstract canAdd(module: string): boolean;
  abstract canDelete(module: string): boolean;
  abstract canEdit(module: string): boolean;
  abstract hasAll(module: string): boolean;
}

@Injectable()
export class UserAccessService extends IUserAccessService {
  private accessRight: any;

  constructor(private userService: UserService) {
    super();
    this.userService.userAccess$.subscribe(value => {
      this.accessRight = value;
    });
  }

  canEdit(module: string): boolean {
    return this.accessRight[module].editAble;
  }

  canView(module: string): boolean {
    return this.accessRight[module].viewAble;
  }
  canAdd(module: string): boolean {
    return this.accessRight[module].insertAble;
  }
  canDelete(module: string): boolean {
    return this.accessRight[module].deleteAble;
  }

  hasAll(module: string): boolean {
    return this.canAdd(module) && this.canDelete(module) && this.canEdit(module) && this.canView(module);
  }
}
