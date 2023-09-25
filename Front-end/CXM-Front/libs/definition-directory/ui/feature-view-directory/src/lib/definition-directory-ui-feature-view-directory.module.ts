import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureViewDirectoryComponent } from './feature-view-directory.component';
import { RouterModule } from '@angular/router';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { DefinitionDirectoryUiFeatureFormDirectoryModule } from '@cxm-smartflow/definition-directory/ui/feature-form-directory';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureViewDirectoryComponent
      }
    ]),
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    DefinitionDirectoryUiFeatureFormDirectoryModule
  ],
  declarations: [
    FeatureViewDirectoryComponent
  ],
})
export class DefinitionDirectoryUiFeatureViewDirectoryModule {}
