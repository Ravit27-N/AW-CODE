import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { TemplateService } from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { EMPTY, Observable } from 'rxjs';
import { FollowMyCampaignService } from './follow-my-campaign.service';

@Injectable()
export class EmailTemplateByIdResolverService implements Resolve<any> {
  constructor(
    private emailTemplateService: TemplateService,
    private followMyCampaignService: FollowMyCampaignService,
    private store: Store
  ) {}

  resolve(route: ActivatedRouteSnapshot): Observable<any> {
    if (route.paramMap.has('templateId')) {
      return this.emailTemplateService.getTemplateById(
        route.params['templateId']
      );
    } else if (route.paramMap.has('campaignId')) {
      return this.followMyCampaignService.getEmailCampaignById(
        route.params['campaignId']
      );
    } else {
      return EMPTY;
    }
  }
}
