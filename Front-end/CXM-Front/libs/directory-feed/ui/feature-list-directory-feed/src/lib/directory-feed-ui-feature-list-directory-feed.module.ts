import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureListDirectoryFeedComponent } from './feature-list-directory-feed.component';
import { RouterModule } from '@angular/router';
import { SharedUiRoundButtonModule } from '@cxm-smartflow/shared/ui/round-button';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { DirectoryFeedUiFeatureListDirectoryFeedTableModule } from '@cxm-smartflow/directory-feed/ui/feature-list-directory-feed-table';
import { DirectoryFeedDataAccessModule } from '@cxm-smartflow/directory-feed/data-access';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    SharedUiRoundButtonModule,
    SharedPipesModule,
    DirectoryFeedUiFeatureListDirectoryFeedTableModule,
    DirectoryFeedDataAccessModule,
    SharedCommonTypoModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureListDirectoryFeedComponent,
      },
    ]),
  ],
  declarations: [FeatureListDirectoryFeedComponent],
  exports: [FeatureListDirectoryFeedComponent],
})
export class DirectoryFeedUiFeatureListDirectoryFeedModule {}
