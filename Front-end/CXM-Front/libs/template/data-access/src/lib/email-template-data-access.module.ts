import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { EmailTemplateListResolverService } from './services/email-template-resolver.service';
import { StoreModule } from '@ngrx/store';
import {
  CreateUpdateTemplatePopupEffect,
  createUpdateTemplatePopupKey,
  createUpdateTemplatePopupReducer,
} from './store/create-update-template-popup';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { MatDialogModule } from '@angular/material/dialog';
import {
  featureTemplateFormKey,
  SmsTemplateEffect,
  smsTemplateReducer,
} from './store/template-form';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TemplateService } from './services/template.service';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(
      createUpdateTemplatePopupKey,
      createUpdateTemplatePopupReducer
    ),
    StoreModule.forFeature(featureTemplateFormKey, smsTemplateReducer),
    EffectsModule.forFeature([
      CreateUpdateTemplatePopupEffect,
      SmsTemplateEffect,
    ]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule,
    MatDialogModule,
    SharedUiComfirmationMessageModule
  ],
  providers: [EmailTemplateListResolverService, TemplateService],
})
export class EmailTemplateDataAccessModule {}
