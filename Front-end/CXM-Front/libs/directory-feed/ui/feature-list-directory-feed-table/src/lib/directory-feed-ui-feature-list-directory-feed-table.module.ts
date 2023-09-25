import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureDirectoryFeedTableComponent } from './feature-directory-feed-table.component';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { SharedUiPaginatorModule } from '@cxm-smartflow/shared/ui/paginator';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    SharedUiPaginatorModule,
    MatTableModule,
    MatSortModule
  ],
  declarations: [
    FeatureDirectoryFeedTableComponent
  ],
  exports: [FeatureDirectoryFeedTableComponent]
})
export class DirectoryFeedUiFeatureListDirectoryFeedTableModule {
}
