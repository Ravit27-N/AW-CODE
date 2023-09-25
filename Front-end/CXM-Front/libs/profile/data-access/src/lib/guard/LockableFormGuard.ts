import { ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IDeactivateComponent } from './IDeactivateComponent';

@Injectable()
export class LockableFormGuard implements CanDeactivate<IDeactivateComponent> {
  canDeactivate(component: IDeactivateComponent,
                currentRoute: ActivatedRouteSnapshot,
                currentState: RouterStateSnapshot,
                nextState?: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    return component?.canExit ? component?.canExit() : true;
  }
}
