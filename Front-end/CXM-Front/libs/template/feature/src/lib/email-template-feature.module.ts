/* eslint-disable @nrwl/nx/enforce-module-boundaries */
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {AuthDataAccessModule} from '@cxm-smartflow/auth/data-access';
import {SharedUiCardSideBarModule} from '@cxm-smartflow/shared/ui/card-side-bar';
import {EmailTemplateComponent} from './email-template/email-template.component';
import {NgDynamicBreadcrumbModule} from 'ng-dynamic-breadcrumb';
import {getBreadcrumb} from '@cxm-smartflow/shared/utils';

@NgModule({
  imports: [
    CommonModule,
    AuthDataAccessModule.forRoot(),
    SharedUiCardSideBarModule,
    NgDynamicBreadcrumbModule,
    RouterModule.forChild([
      {
        path: '',
        component: EmailTemplateComponent,
        children: [
          {
            path: '',
            redirectTo: 'feature-list-email-template',
            pathMatch: 'full',
          },
          {
            path: 'feature-list-email-template',
            // data: {
            //   breadcrumb: getBreadcrumb().template.list
            // },
            loadChildren: () =>
              import('@cxm-smartflow/template/ui/feature-list-template').then(
                (m) => m.EmailTemplateUiFeatureListEmailTemplateModule
              ),
          },
          {
            path: 'sms-template-composition',
            data: {
              breadcrumb: getBreadcrumb().template
                .summaryEmailTemplateFromEditThenComposition,
            },
            loadChildren: () =>
              import('@cxm-smartflow/template/ui/feature-template-form').then(
                (m) => m.EmailTemplateUiFeatureSmsTemplateModule
              ),
          },
        ],
      },
    ]),
  ],
  declarations: [EmailTemplateComponent],
})
export class EmailTemplateFeatureModule {}
