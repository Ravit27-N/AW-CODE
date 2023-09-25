import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DefinitionComponent } from './definition.component';
import { RouterModule } from '@angular/router';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    SharedDirectivesCanVisibilityModule,
    NgDynamicBreadcrumbModule,
    RouterModule.forChild([
      {
        path: '',
        component: DefinitionComponent,
        children: [
          {
            path: '',
            redirectTo: 'list-definition-directory',
          },
          {
            path: 'list-definition-directory',
            data: {
              breadcrumb: getBreadcrumb().definitionDirectory.list
            },
            loadChildren: () =>
              import('@cxm-smartflow/definition-directory/ui/feature-list-directories').then((m) => m.DefinitionDirectoryUiFeatureListDirectoriesModule),
          },
          {
            path: 'create',
            data: {
              breadcrumb: getBreadcrumb().definitionDirectory.create
            },
            loadChildren: () =>
              import('@cxm-smartflow/definition-directory/ui/feature-create-directory').then((m) => m.DefinitionDirectoryUiFeatureCreateDirectoryModule),
          },
          {
            path: 'edit',
            data: {
              breadcrumb: getBreadcrumb().definitionDirectory.edit
            },
            loadChildren: () =>
              import('@cxm-smartflow/definition-directory/ui/feature-edit-directory').then(m => m.DefinitionDirectoryUiFeatureEditDirectoryModule)
          },
          {
            path: 'view',
            data: {
              breadcrumb: getBreadcrumb().definitionDirectory.view
            },
            loadChildren: () =>
              import('@cxm-smartflow/definition-directory/ui/feature-view-directory').then(m => m.DefinitionDirectoryUiFeatureViewDirectoryModule)
          },
        ],
      },
    ]),
  ],
  declarations: [DefinitionComponent],
})
export class DirectoryDefinitionFeatureModule {}
