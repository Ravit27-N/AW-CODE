import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import {
  featureFeedFormKey,
  FeedFormEffect,
  listDirectoryFeedKey,
  listDirectoryFeedReducer,
  ListDirectoryFeedTableEffect,
  ManageDirectoryFeedEffect,
  ManageDirectoryFeedKey,
  ManageDirectoryFeedReducer,
  ManageDirectoryFieldEffect,
  manageDirectoryFieldKey,
  ManageDirectoryFieldReducer,
  reducer,
} from './stores';
import { EffectsModule } from '@ngrx/effects';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(listDirectoryFeedKey, listDirectoryFeedReducer),
    StoreModule.forFeature(ManageDirectoryFeedKey, ManageDirectoryFeedReducer),
    StoreModule.forFeature(
      manageDirectoryFieldKey,
      ManageDirectoryFieldReducer
    ),
    EffectsModule.forFeature([
      ListDirectoryFeedTableEffect,
      ManageDirectoryFeedEffect,
      ManageDirectoryFieldEffect,
    ]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule,
    SharedUiComfirmationMessageModule,
    StoreModule.forFeature(featureFeedFormKey, reducer),
    EffectsModule.forFeature([FeedFormEffect]),
  ],
})
export class DirectoryFeedDataAccessModule {}
