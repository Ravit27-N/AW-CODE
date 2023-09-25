import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { preloadEmailCampaign } from '@cxm-smartflow/follow-my-campaign/data-access';
import { Store } from '@ngrx/store';


@Injectable()
export class EmailTemplateParameterResolverService implements Resolve<any> {


  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

    const { campaignId } = route.params;

    this.store.dispatch(preloadEmailCampaign({ campaignId }));

    return {};
  }

  constructor(private store: Store) {
  }

}
