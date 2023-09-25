import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ApiService } from './api.service';
import { CxmCampaignService } from './cxm-campaign.service';
import { CxmTemplateService } from './cxm-template.service';
import { CxmFlowDepositService } from './cxm-flow-deposit.service';
import { ConfigurationService } from './configuration.service';
import { CxmAnalyticsService } from './cxm-analytics.service';

@NgModule({
  imports: [CommonModule],
  providers: [
    ConfigurationService,
    ApiService,
    CxmCampaignService,
    CxmTemplateService,
    CxmFlowDepositService,
    CxmAnalyticsService,
  ],
})
export class SharedDataAccessApiModule {}
