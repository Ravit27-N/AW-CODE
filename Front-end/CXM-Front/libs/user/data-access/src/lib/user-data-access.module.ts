import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import {
  BatchUserEffect,
  batchUserReducer,
  listUserKey,
  listUserReducer,
  ListUserTableEffect,
  manageBatchUserKey
} from './stores';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import {
  ManageUserEffect,
  manageUserKey,
  manageUserReducer,
} from './stores/manage-user';
import { UserUiImportUserCsvDialogModule } from '@cxm-smartflow/user/ui/import-user-csv-dialog';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(listUserKey, listUserReducer),
    StoreModule.forFeature(manageUserKey, manageUserReducer),
    StoreModule.forFeature(manageBatchUserKey, batchUserReducer),
    EffectsModule.forFeature([ListUserTableEffect, ManageUserEffect, BatchUserEffect]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule,
    UserUiImportUserCsvDialogModule,
  ],
})
export class UserDataAccessModule {}
