import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureDirectoryFeedNavigatorComponent } from './feature-directory-feed-navigator.component';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [CommonModule, SharedTranslateModule.forRoot()],
  declarations: [FeatureDirectoryFeedNavigatorComponent],
  exports: [FeatureDirectoryFeedNavigatorComponent],
})
export class DirectoryFeedUiFeatureDirectoryFeedNavigatorModule {}
