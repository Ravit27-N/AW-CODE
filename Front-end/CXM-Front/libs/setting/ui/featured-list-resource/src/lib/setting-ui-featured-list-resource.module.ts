import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeaturedListResourceComponent } from './featured-list-resource.component';
import { RouterModule } from '@angular/router';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedUiFilterBoxModule } from '@cxm-smartflow/shared/ui/filter-box';
import { SharedUiSearchBoxModule } from '@cxm-smartflow/shared/ui/search-box';
import { FeatureListResourceTableComponent } from './feature-list-resource-table/feature-list-resource-table.component';
import { FeatureManageResourcePopupComponent } from './feature-manage-resource-popup/feature-manage-resource-popup.component';
import { ManageResourcePopupService } from './feature-manage-resource-popup/manage-resource-popup.service';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';
import { SharedUiUploadingFragmentModule } from '@cxm-smartflow/shared/ui/uploading-fragment';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';

@NgModule({
  imports: [
    CommonModule,
    NgDynamicBreadcrumbModule,
    MaterialModule,
    SharedCommonTypoModule,
    SharedUiButtonModule,
    SharedUiFilterBoxModule,
    SharedUiSearchBoxModule,
    SharedTranslateModule.forRoot(),
    RouterModule.forChild([
      {
        path: '',
        component: FeaturedListResourceComponent,
        data: {
          breadcrumb: getBreadcrumb().setting.list,
        },
      },
    ]),
    ReactiveFormsModule,
    SharedUiFormInputSelectionModule,
    SharedUiUploadingFragmentModule,
    SharedDirectivesTooltipModule,
  ],
  declarations: [
    FeaturedListResourceComponent,
    FeatureListResourceTableComponent,
    FeatureManageResourcePopupComponent,
  ],
  providers: [ManageResourcePopupService],
})
export class SettingUiFeaturedListResourceModule {}
