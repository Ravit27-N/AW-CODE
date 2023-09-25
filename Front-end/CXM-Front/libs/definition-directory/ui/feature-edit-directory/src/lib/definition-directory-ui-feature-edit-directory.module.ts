import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureEditDirectoryComponent } from './feature-edit-directory.component';
import { RouterModule } from '@angular/router';
import { DefinitionDirectoryControl } from '@cxm-smartflow/definition-directory/data-access';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { DefinitionDirectoryUiFeatureFormDirectoryModule } from '@cxm-smartflow/definition-directory/ui/feature-form-directory';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureEditDirectoryComponent,
        canDeactivate: [DefinitionDirectoryControl],
      },
    ]),
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    DefinitionDirectoryUiFeatureFormDirectoryModule,
  ],
  declarations: [FeatureEditDirectoryComponent],
})
export class DefinitionDirectoryUiFeatureEditDirectoryModule {}
