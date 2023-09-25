import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import {
  ManageDefinitionDirectoryReducer,
  ManageDefinitionDirectoryReducerKey,
  ManageDirectoryEffect,
} from './stores';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(ManageDefinitionDirectoryReducerKey, ManageDefinitionDirectoryReducer),
    EffectsModule.forFeature([
      ManageDirectoryEffect,
    ]),
    StoreDevtoolsModule.instrument(),
    SharedTranslateModule.forRoot(),
  ]
})
export class DirectoryDefinitionDataAccessModule {
}
