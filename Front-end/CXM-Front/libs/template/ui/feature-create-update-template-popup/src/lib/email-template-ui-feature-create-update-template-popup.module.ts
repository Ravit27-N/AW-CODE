import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureCreateUpdateTemplatePopupComponent } from './feature-create-update-template-popup.component';
import { CreateUpdateTemplatePopupService } from './create-update-template-popup.service';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedUtilsModule } from '@cxm-smartflow/shared/utils';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    SharedDataAccessServicesModule,
    MaterialModule,
    SharedDirectivesTooltipModule,
    SharedTranslateModule.forRoot(),
    SharedUtilsModule
  ],
  declarations: [FeatureCreateUpdateTemplatePopupComponent],
  providers: [CreateUpdateTemplatePopupService],
})
export class EmailTemplateUiFeatureCreateUpdateTemplatePopupModule {}
