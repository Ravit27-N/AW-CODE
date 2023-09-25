import { SharedUiImageModule } from '@cxm-smartflow/shared/ui/image';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { EmailTemplateCardComponent } from './email-template-card/email-template-card.component';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { SharedDirectivesCanModificationModule } from '@cxm-smartflow/shared/directives/can-modification';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    SharedUiImageModule,
    AuthDataAccessModule.forRoot(),
    SharedDirectivesCanModificationModule,
    SharedDirectivesCanVisibilityModule,
    TranslateModule
  ],
  declarations: [EmailTemplateCardComponent],
  exports: [EmailTemplateCardComponent],
})
export class SharedUiThumbnailCardModule {}
