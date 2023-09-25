import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApprovalControlService } from './services/approval-control.service';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { SharedDataAccessServicesModule } from '@cxm-smartflow/shared/data-access/services';
import { ApprovalEffect } from './store/list-approval/approval.effect';
import {
  approvalReducer,
  featureApprovalKey,
} from './store/list-approval/approval.reducer';
import { ApprovalService } from './services/approval.service';
import {
  approvalDocReducer,
  featureApprovalDocKey,
} from './store/document-approval/document-approval.reducer';
import { DocumentApprovalEffect } from './store/document-approval/document-approval.effect';
import {
  featureApprovalRemainingDateKey,
  RemainingShipmentEffect,
  remainingShipmentReducer,
} from './store';

@NgModule({
  imports: [
    CommonModule,
    StoreModule.forFeature(featureApprovalKey, approvalReducer),
    StoreModule.forFeature(featureApprovalDocKey, approvalDocReducer),
    StoreModule.forFeature(
      featureApprovalRemainingDateKey,
      remainingShipmentReducer
    ),
    EffectsModule.forFeature([
      ApprovalEffect,
      DocumentApprovalEffect,
      RemainingShipmentEffect,
    ]),
    StoreDevtoolsModule.instrument(),
    SharedDataAccessServicesModule,
  ],
  providers: [ApprovalService, ApprovalControlService],
})
export class ApprovalDataAccessModule {}
