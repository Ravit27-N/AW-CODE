import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DirectoryFeedComponent } from './directory-feed.component';
import { RouterModule } from '@angular/router';
import { DirectoryFeedUiFeatureDirectoryFeedNavigatorModule } from '@cxm-smartflow/directory-feed/ui/feature-directory-feed-navigator';
import { DirectoryFeedDataAccessModule } from '@cxm-smartflow/directory-feed/data-access';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    SharedDirectivesCanVisibilityModule,
    DirectoryFeedUiFeatureDirectoryFeedNavigatorModule,
    DirectoryFeedDataAccessModule,
    NgDynamicBreadcrumbModule,
    RouterModule.forChild([
      {
        path: '',
        component: DirectoryFeedComponent,
        children: [
          {
            path: '',
            data: {
              breadcrumb: getBreadcrumb().directoryFeed.list,
            },
            loadChildren: () =>
              import(
                '@cxm-smartflow/directory-feed/ui/feature-list-directory-feeds'
              ).then((m) => m.DirectoryFeedUiFeatureListDirectoryFeedsModule),
          },
          {
            path: 'detail',
            data: {
              breadcrumb: getBreadcrumb().directoryFeed.directoryDetail,
            },
            loadChildren: () =>
              import(
                '@cxm-smartflow/directory-feed/ui/feature-directory-feed-details'
              ).then((m) => m.DirectoryFeedUiFeatureDirectoryFeedDetailsModule),
          },
          {
            path: 'add',
            data: {
              breadcrumb: getBreadcrumb().directoryFeed.directoryAdd,
            },
            loadChildren: () =>
              import(
                '@cxm-smartflow/directory-feed/ui/feature-add-directory-feed'
              ).then((m) => m.DirectoryFeedUiFeatureAddDirectoryFeedModule),
          },
        ],
      },
    ]),
  ],
  declarations: [DirectoryFeedComponent],
})
export class DirectoryFeedFeatureModule {}
