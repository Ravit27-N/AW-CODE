import { Router } from '@angular/router';
import { Component } from '@angular/core';
import { globalPropertiesLable, globalPropertiesLable as Label } from '@cxm-smartflow/shared/data-access/model';
@Component({
  selector: 'cxm-smartflow-feature-summary',
  templateUrl: './feature-summary.component.html',
  styleUrls: ['./feature-summary.component.scss']
})
export class FeatureSummaryComponent{
  summrayLabel = globalPropertiesLable.cxmCampaign.manageMyCampaign;
  summrayButton = globalPropertiesLable.button;
  constructor(private router: Router) { }

  onNext() {
    this.router.navigateByUrl('cxm-smartflow/cxm-campaign/manage-my-campaign/summary');
  }

  onPrevious() {
    this.router.navigateByUrl('cxm-smartflow/cxm-campaign/manage-my-campaign/destination');
  }

}
