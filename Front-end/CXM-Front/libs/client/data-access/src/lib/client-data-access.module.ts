import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { EffectsModule } from '@ngrx/effects';

import { clientReducers } from './store/client';
import { ClientEffects  } from './store/client'
import { ClientService } from './services/client.service';
import { modifyClientReducer, ClientCeationModifyEffect, fromClientFluxExt } from './store/modification';
import { ClientControlService } from './services/client-control.service';
import { ClientUiClientPopupDialogModule } from '@cxm-smartflow/client/ui/client-popup-dialog';


@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(clientReducers.clientFeatureKey, clientReducers.reducers),
    StoreModule.forFeature(modifyClientReducer.featureClientCreationModify, modifyClientReducer.reducer),
    StoreModule.forFeature(fromClientFluxExt.featureFluxExt, fromClientFluxExt.reducer),
    EffectsModule.forFeature([ClientEffects, ClientCeationModifyEffect]),
    ClientUiClientPopupDialogModule,
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule
  ],
  providers: [
    ClientService,
    ClientControlService
  ]
})
export class ClientDataAccessModule { }
