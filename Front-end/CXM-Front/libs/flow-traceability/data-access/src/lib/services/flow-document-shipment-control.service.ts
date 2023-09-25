import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import {
  ActivatedRouteSnapshot,
  CanDeactivate,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { appLocalStorageConstant, appRoute } from '@cxm-smartflow/shared/data-access/model';
import { FlowCriteriaSessionService } from './flow-criteria-session.service';

@Injectable({
  providedIn: 'root',
})
export class FlowDocumentShipmentControlService implements CanDeactivate<any> {
  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
      if (nextState?.url.includes(appRoute.cxmFlowTraceability.navigateToFlowDetailDigital)) {
        localStorage.setItem(appLocalStorageConstant.FlowTraceability.ListDocumentShipment, 'true');
        return of(true);
      }

      if (nextState?.url.includes(appRoute.cxmFlowTraceability.navigateToViewDocumentShipment)) {
        const isClear = localStorage.getItem(appLocalStorageConstant.FlowTraceability.ListDocumentShipment);
        if (!isClear) {
          localStorage.removeItem(appLocalStorageConstant.FlowTraceability.ListDocumentShipment);
          this.storageService.setDocumentShipmentCriteria({});
        }
        return of(true);
      }

      if(currentState.url.includes(appRoute.cxmFlowTraceability.navigateToViewDocumentShipment) &&
      nextState?.url.includes(appRoute.cxmFlowTraceability.navigateToViewDocumentDetail)) {
        return of(true);
      }


    this.storageService.setDocumentShipmentCriteria({});
    localStorage.removeItem(appLocalStorageConstant.FlowTraceability.ListDocumentShipment);
    return of(true);
  }


  constructor(private storageService: FlowCriteriaSessionService) {}
}
