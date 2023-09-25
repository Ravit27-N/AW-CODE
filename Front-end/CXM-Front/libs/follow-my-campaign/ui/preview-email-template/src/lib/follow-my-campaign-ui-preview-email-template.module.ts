import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PreviewEmailTemplateComponent } from './preview-email-template.component';
import { HttpClientModule } from '@angular/common/http';
import { SharedUiImageModule } from '@cxm-smartflow/shared/ui/image';
import { PreViewEmailTemplateService } from './pre-view-email-template.service';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { SharedUiIframelyEmbedModule } from '@cxm-smartflow/shared/ui/iframely-embed';
import { PreviewSmsTemplateComponent } from './preview-sms-template.component';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedUiImageModule,
    HttpClientModule,
    MaterialModule,
    MatDialogModule,
    SharedUiIframelyEmbedModule,
    SharedPipesModule,
    SharedTranslateModule.forRoot(),
  ],
  declarations: [PreviewEmailTemplateComponent, PreviewSmsTemplateComponent],
  exports: [PreviewEmailTemplateComponent, PreviewSmsTemplateComponent],
  providers: [
    {
      provide: MatDialogRef,
      useValue: {},
    },
    PreViewEmailTemplateService,
  ],
})
export class FollowMyCampaignUiPreviewEmailTemplateModule {}
