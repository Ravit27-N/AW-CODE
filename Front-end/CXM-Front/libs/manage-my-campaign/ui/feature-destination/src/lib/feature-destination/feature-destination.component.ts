import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { globalPropertiesLable, globalPropertiesLable as Label } from '@cxm-smartflow/shared/data-access/model';
@Component({
  selector: 'cxm-smartflow-feature-destination',
  templateUrl: './feature-destination.component.html',
  styleUrls: ['./feature-destination.component.scss']
})
export class FeatureDestinationComponent  {
  destinationLabel = globalPropertiesLable.cxmCampaign.manageMyCampaign;
  destinationButton = globalPropertiesLable.button;
  constructor(private router: Router) { }

  onNext() {
    this.router.navigateByUrl('cxm-smartflow/cxm-campaign/manage-my-campaign/summary');
  }

  onPrevious() {
    this.router.navigateByUrl('cxm-smartflow/cxm-campaign/manage-my-campaign/setting');
  }
}
