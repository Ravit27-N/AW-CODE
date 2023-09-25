import { EmailTemplateByIdResolverService } from './services/follow-my-campaign-resolver.service';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import {
  CampaignEffect,
  campaignEmailSteRedcuer,
  campaignReducer,
  CampaignStepEffect,
  EmailCampaignFeatureKey,
  FeatureCampaignEmailStepKey,
} from './store/feature-campaign-email';
import { EffectsModule } from '@ngrx/effects';
import {
  FeatureCampaignListEffect,
  featureCampaignListKey,
  featureCampaignListReducer,
} from './store/feature-campaign-list';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import {
  CampaignSmsEffect,
  campaignSmsReducer,
  campaigSmsFeatureKey,
} from './store/feature-campaign-sms';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import {
  FeatureListChoiceOfModelEffect,
  listChoiceOfModelReducer,
  listChoiceOfTemplateModelKey,
} from './store/feature-list-choice-of-model';
import { FollowMyCampaignUiPreviewEmailTemplateModule } from '@cxm-smartflow/follow-my-campaign/ui/preview-email-template';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(EmailCampaignFeatureKey, campaignReducer),
    StoreModule.forFeature(
      FeatureCampaignEmailStepKey,
      campaignEmailSteRedcuer
    ),
    StoreModule.forFeature(featureCampaignListKey, featureCampaignListReducer),
    StoreModule.forFeature(campaigSmsFeatureKey, campaignSmsReducer),
    StoreModule.forFeature(
      listChoiceOfTemplateModelKey,
      listChoiceOfModelReducer
    ),
    EffectsModule.forFeature([
      CampaignEffect,
      CampaignStepEffect,
      FeatureCampaignListEffect,
      CampaignSmsEffect,
      FeatureListChoiceOfModelEffect,
    ]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule,
    SharedUiComfirmationMessageModule,
    FollowMyCampaignUiPreviewEmailTemplateModule,
    SharedTranslateModule.forRoot(),
  ],
  providers: [EmailTemplateByIdResolverService],
})
export class FollowMyCampaignDataAccessModule {}
