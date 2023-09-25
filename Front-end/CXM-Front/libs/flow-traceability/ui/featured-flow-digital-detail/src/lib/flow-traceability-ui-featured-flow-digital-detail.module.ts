import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeaturedFlowDigitalDetailComponent } from './featured-flow-digital-detail.component';
import { RouterModule } from '@angular/router';
import { FlowTraceabilityUiFlowTraceabilityPageHeaderModule } from '@cxm-smartflow/flow-traceability/ui/featured-flow-page-header';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { InformationBoardComponent } from './information-board/information-board.component';
import { DeliverabilityRateComponent } from './deliverability-rate/deliverability-rate.component';
import { MatIconModule } from '@angular/material/icon';
import { SharedDirectivesInfoTooltipModule } from '@cxm-smartflow/shared/directives/info-tooltip';
import { CampaignRateComponent } from './campaign-rate/campaign-rate.component';
import { SharedUiCircleProgressionModule } from '@cxm-smartflow/shared/ui/circle-progression';
import { RenderEmailTemplateComponent } from './render-email-template/render-email-template.component';
import { RenderSmsTemplateComponent } from './render-sms-template/render-sms-template.component';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { PreviewDomComponent } from './render-email-template/preview-dom/preview-dom.component';
import { FollowMyCampaignUiFeatureCampaignMailModule } from '@cxm-smartflow/follow-my-campaign/ui/feature-campaign-mail';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    RouterModule.forChild([
      { path: '', component: FeaturedFlowDigitalDetailComponent },
    ]),
    FlowTraceabilityUiFlowTraceabilityPageHeaderModule,
    SharedUiButtonModule,
    MatIconModule,
    SharedDirectivesInfoTooltipModule,
    SharedUiCircleProgressionModule,
    SharedUiSpinnerModule,
    FollowMyCampaignUiFeatureCampaignMailModule,
    SharedPipesModule,
    SharedCommonTypoModule
  ],
  declarations: [
    FeaturedFlowDigitalDetailComponent,
    InformationBoardComponent,
    DeliverabilityRateComponent,
    CampaignRateComponent,
    RenderEmailTemplateComponent,
    RenderSmsTemplateComponent,
    PreviewDomComponent,
  ],
})
export class FlowTraceabilityUiFeaturedFlowDigitalDetailModule {}
