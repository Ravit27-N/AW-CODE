import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import {
  CommunicationInteractiveEffect,
  communicationInteractiveReducer,
  communicationInteractiveReducerKey
} from './stores';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(communicationInteractiveReducerKey, communicationInteractiveReducer),
    EffectsModule.forFeature([CommunicationInteractiveEffect]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule
  ]
})
export class CommunicationInteractiveDataAccessModule {}
