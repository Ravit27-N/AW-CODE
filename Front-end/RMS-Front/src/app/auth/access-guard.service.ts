import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot } from '@angular/router';
import { UserService } from './user.service';
import { CanAccess } from './can-access.directive';
import { MessageService } from '../core';
import { IsLoadingService } from '@service-work/is-loading';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class AccessGuardService extends CanAccess implements CanActivate {

  constructor(
    private userService: UserService,
    private message: MessageService,
    private isLoadingService: IsLoadingService) {
    super();

    this.userService.userAccess$.subscribe(value => this.setAccessRight(value));
    this.right = ['view']; // require only view:access
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.userService.userAccess$.pipe(map(value => {
      this.setAccessRight(value);
      if (this.check(route.data.perm)) {
        return true;
      } else {
        this.message.showError('You don\'t permission to use this action', 'Permissoin denied');
        this.isLoadingService.remove({ key: 'on-route' });
        return false;
      }
    })
    );
  }

  public check(module: string): boolean {
    this.module = module;
    return this.checkRight();
  }
}
