import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import {
  globalPropertiesLable,
  globalPropertiesLable as Label,
} from '@cxm-smartflow/shared/data-access/model';
import { TranslateService } from '@ngx-translate/core';
import { TranslateConfigService } from '@cxm-smartflow/shared/data-access/services';

@Component({
  selector: 'cxm-smartflow-feature-model',
  templateUrl: './feature-model.component.html',
  styleUrls: ['./feature-model.component.scss'],
})
export class FeatureModelComponent implements OnInit {
  locale = '';
  modelLabel = globalPropertiesLable.cxmCampaign.manageMyCampaign;
  modelButton = globalPropertiesLable.button;
  constructor(
    private router: Router,
    private translate: TranslateService,
    private translateConfigService: TranslateConfigService
  ) {}
  ngOnInit(): void {
    this.locale = localStorage.getItem('locale') || 'fr';
    this.translate.use(this.locale);
  }

  onNext() {
    this.router.navigateByUrl(
      'cxm-smartflow/cxm-campaign/manage-my-campaign/setting'
    );
  }
}
