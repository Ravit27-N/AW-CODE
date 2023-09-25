import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Route, RouterModule } from '@angular/router';
import { FeedTableFormComponent } from './table/feed-table-form.component';
import { DirectoryFeedFormComponent } from './directory-feed-form.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { DirectoryFeedDataAccessModule } from '@cxm-smartflow/directory-feed/data-access';
import { SharedUiRoundButtonModule } from '@cxm-smartflow/shared/ui/round-button';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

const routes: Route[] = [
  {
    path: ':id',
    component: DirectoryFeedFormComponent
  }
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MaterialModule,
    SharedTranslateModule.forRoot(),
    DirectoryFeedDataAccessModule,
    SharedUiRoundButtonModule,
    SharedCommonTypoModule,
    SharedUiPaginatorModule
  ],
  declarations: [
    FeedTableFormComponent,
    DirectoryFeedFormComponent
  ],
  exports: [
    DirectoryFeedFormComponent
  ]
})
export class DirectoryFeedUiDirectoryFeedFormTableModule {

}
