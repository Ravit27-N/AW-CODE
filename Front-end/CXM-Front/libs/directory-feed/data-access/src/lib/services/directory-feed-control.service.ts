import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ActivatedRouteSnapshot,
  CanDeactivate,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';

export interface Confirmable {
  isLocked(): Promise<boolean>;
}

@Injectable({
  providedIn: 'root',
})
export class DirectoryFeedControl implements CanDeactivate<Confirmable> {
  canDeactivate(
    component: Confirmable,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    return this.controlConfirmation(component.isLocked());
  }

  private async controlConfirmation(
    isLocked: Promise<boolean>
  ): Promise<boolean> {
    const locked = await isLocked;
    return !locked;
  }
}
