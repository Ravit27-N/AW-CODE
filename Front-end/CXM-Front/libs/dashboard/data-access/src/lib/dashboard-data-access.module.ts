import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';

import { DashboardGraphEffect } from './store/graph/graph.effects';
import { featureDashboardGraphKey, reducer } from './store/graph/graph.reducers'
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { AnalysticService } from './services/graph.service';


@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(featureDashboardGraphKey, reducer),
    EffectsModule.forFeature([DashboardGraphEffect]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule
  ],
  providers: [AnalysticService]
})
export class DashboardDataAccessModule {}
