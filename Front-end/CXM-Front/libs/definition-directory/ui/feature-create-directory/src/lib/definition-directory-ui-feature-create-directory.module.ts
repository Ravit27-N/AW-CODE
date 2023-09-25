import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureCreateDirectoryComponent } from './feature-create-directory.component';
import { RouterModule } from '@angular/router';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { DefinitionDirectoryUiFeatureFormDirectoryModule } from '@cxm-smartflow/definition-directory/ui/feature-form-directory';
import {DefinitionDirectoryControl} from "@cxm-smartflow/definition-directory/data-access";

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureCreateDirectoryComponent,
        canDeactivate: [DefinitionDirectoryControl],
      },
    ]),
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    DefinitionDirectoryUiFeatureFormDirectoryModule,
  ],
  declarations: [FeatureCreateDirectoryComponent],
})
export class DefinitionDirectoryUiFeatureCreateDirectoryModule {}
