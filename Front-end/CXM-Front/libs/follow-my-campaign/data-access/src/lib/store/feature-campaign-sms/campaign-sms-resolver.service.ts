import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import {
  loadCampaignSms,
  selectCampaignSmsLoader
} from '@cxm-smartflow/follow-my-campaign/data-access';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { filter, withLatestFrom } from 'rxjs/operators';

@Injectable()
export class CampaignSmsRouteResolver implements Resolve<any> {


  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): any | Observable<any> | Promise<any> {
    const { id } = route.params;
    const { templateId } = route?.queryParams;

    this.store.dispatch(loadCampaignSms({ campaignId: id, templateId }));
    return withLatestFrom(this.store.select(selectCampaignSmsLoader).pipe(filter(x => x.loading === false)));
  }

  constructor(private store: Store) {}

}
