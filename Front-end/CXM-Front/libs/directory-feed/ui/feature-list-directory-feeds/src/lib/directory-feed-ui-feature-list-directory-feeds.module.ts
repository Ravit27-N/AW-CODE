import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureListDirectoryFeedsComponent } from './feature-list-directory-feeds.component';
import { RouterModule } from '@angular/router';
import { MatDividerModule } from '@angular/material/divider';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { TranslateModule } from '@ngx-translate/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { DirectoryFeedDataAccessModule } from '@cxm-smartflow/directory-feed/data-access';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([
      { path: '', component: FeatureListDirectoryFeedsComponent },
    ]),
    MatDividerModule,
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    TranslateModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatSortModule,
    MatTableModule,
    DirectoryFeedDataAccessModule,
  ],
  declarations: [FeatureListDirectoryFeedsComponent],
})
export class DirectoryFeedUiFeatureListDirectoryFeedsModule {}
