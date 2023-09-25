import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SmsTemplateComponent } from './sms-template/sms-template.component';
import { RouterModule, Routes } from '@angular/router';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { TemplateVariableFormComponent } from './template-variable-form/template-variable-form.component';
import { SmsTemplateEditorComponent } from './sms-template-editor/sms-template-editor.component';
import { EmailTemplateDataAccessModule } from '@cxm-smartflow/template/data-access';
import { EmailTemplateUiFeatureCreateUpdateTemplatePopupModule } from '@cxm-smartflow/template/ui/feature-create-update-template-popup';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { EmailTemplateComponent } from './email-template/email-template.component';
import { EmailTemplateEditorComponent } from './email-template-editor/email-template-editor.component';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { LockableFormGuardService } from './LockableFormGuard.service';
import { SharedUiIframelyEmbedModule } from '@cxm-smartflow/shared/ui/iframely-embed';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { TemplateSuccessComponent } from './template-success/template-success.component';
import { MatStepperModule } from '@angular/material/stepper';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

const routes: Routes = [
  {
    path: 'sms',
    component: SmsTemplateComponent,
    canDeactivate: [LockableFormGuardService]
  },
  {
    path: 'sms/:id',
    component: SmsTemplateComponent,
    canDeactivate: [LockableFormGuardService]
  },
  {
    path: 'email',
    component: EmailTemplateComponent,
    canDeactivate: [LockableFormGuardService]
  },
  {
    path: 'email/:id',
    component: EmailTemplateComponent,
    canDeactivate: [LockableFormGuardService]
  },
  {
    path: 'done/:content/:id', // has two content type (eg.: done/sms, done/emailing)
    component: TemplateSuccessComponent
  }
];

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    SharedCommonTypoModule,
    MaterialModule,
    SharedUiIframelyEmbedModule,
    SharedPipesModule,
    EmailTemplateUiFeatureCreateUpdateTemplatePopupModule,
    EmailTemplateDataAccessModule,
    SharedTranslateModule.forRoot(),
    SharedDirectivesTooltipModule,
    SharedUiSpinnerModule,
    MatStepperModule,
  ],
  declarations: [
    SmsTemplateComponent,
    TemplateVariableFormComponent,
    SmsTemplateEditorComponent,
    EmailTemplateComponent,
    EmailTemplateEditorComponent,
    TemplateSuccessComponent
  ],
  providers: [LockableFormGuardService]
})
export class EmailTemplateUiFeatureSmsTemplateModule {
}
