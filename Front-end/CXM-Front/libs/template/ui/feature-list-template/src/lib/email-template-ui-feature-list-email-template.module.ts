import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { RouterModule } from '@angular/router';
import {
  DeleteEmailTemmplateEffect,
  deleteEmailTemplateKey,
  deleteEmailTemplateReducer,
  EmailTemplateDataAccessModule,
  EmailTemplateListResolverService,
  FeatureListEmailTemplateEffect,
  featureListEmailTemplateFeatureKey,
  featureListTemplateReducer,
} from '@cxm-smartflow/template/data-access';
import { SharedComfirmDialogModule } from '@cxm-smartflow/shared/comfirm-dialog';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { SharedUiThumbnailCardModule } from '@cxm-smartflow/shared/ui/thumbnail-card';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { TranslateService } from '@ngx-translate/core';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedUiSearchModule } from '@cxm-smartflow/shared/ui/search';
import { EmailTemplateUiFeatureCreateUpdateTemplatePopupModule } from '@cxm-smartflow/template/ui/feature-create-update-template-popup';
import { ListEmailTemplatePageComponent } from './list-email-template-page/list-email-template-page.component';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { ListSmsTemplatePageComponent } from './list-sms-template-page/list-sms-template-page.component';
import { GridListTemplateComponent } from './grid-list-template/grid-list-template.component';
import { TemplateFilterComponentComponent } from './template-filter-component/template-filter-component.component';
import { SharedUiImageModule } from '@cxm-smartflow/shared/ui/image';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiSearchBoxModule } from '@cxm-smartflow/shared/ui/search-box';

@NgModule({
  imports: [
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    EmailTemplateUiFeatureCreateUpdateTemplatePopupModule,
    SharedTranslateModule.forRoot(),
    EmailTemplateDataAccessModule,
    CommonModule,
    MaterialModule,
    SharedUiButtonModule,
    SharedUiSpinnerModule,
    SharedUiPaginatorModule,
    SharedUiThumbnailCardModule,
    SharedComfirmDialogModule,
    SharedDataAccessServicesModule,
    SharedDirectivesCanVisibilityModule,
    SharedCommonTypoModule,
    SharedUiSearchModule,
    NgDynamicBreadcrumbModule,
    SharedUiImageModule,
    SharedDirectivesTooltipModule,
    RouterModule.forChild([
      {
        path: 'email',
        component: ListEmailTemplatePageComponent,
        resolve: { paginationProps: EmailTemplateListResolverService },
        data: {
          breadcrumb: getBreadcrumb().template.list,
        },
      },
      {
        path: 'sms',
        component: ListSmsTemplatePageComponent,
        resolve: { paginationProps: EmailTemplateListResolverService },
        data: {
          breadcrumb: getBreadcrumb().template.listSms,
        },
      },
    ]),

    StoreModule.forFeature(
      featureListEmailTemplateFeatureKey,
      featureListTemplateReducer
    ),
    StoreModule.forFeature(deleteEmailTemplateKey, deleteEmailTemplateReducer),
    EffectsModule.forFeature([
      FeatureListEmailTemplateEffect,
      DeleteEmailTemmplateEffect,
    ]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule,
    SharedUiSearchBoxModule,
  ],
  declarations: [
    ListEmailTemplatePageComponent,
    ListSmsTemplatePageComponent,
    GridListTemplateComponent,
    TemplateFilterComponentComponent,
  ],
  providers: [TranslateService],
})
export class EmailTemplateUiFeatureListEmailTemplateModule {}
