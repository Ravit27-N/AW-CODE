import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureListDirectoriesComponent } from './feature-list-directories.component';
import { RouterModule } from '@angular/router';
import { MatDividerModule } from '@angular/material/divider';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { TranslateModule } from '@ngx-translate/core';
import { UserUiListUserTableModule } from '@cxm-smartflow/user/ui/list-user-table';
import { MatIconModule } from '@angular/material/icon';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { DirectoryDefinitionDataAccessModule } from '@cxm-smartflow/definition-directory/data-access';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      { path: '', component: FeatureListDirectoriesComponent },
    ]),
    MatDividerModule,
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    TranslateModule,
    UserUiListUserTableModule,
    MatIconModule,
    MatSortModule,
    MatTableModule,
    MatButtonModule,
    MatMenuModule,
    DirectoryDefinitionDataAccessModule,
  ],
  declarations: [FeatureListDirectoriesComponent],
})
export class DefinitionDirectoryUiFeatureListDirectoriesModule {}
