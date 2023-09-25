import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DepositTabNavComponent } from './deposit-tab-nav/deposit-tab-nav.component';
import { DepositNavControlComponent } from './deposit-nav-control/deposit-nav-control.component';
import { FlowDepositDataAccessModule } from '@cxm-smartflow/flow-deposit/data-access';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    FlowDepositDataAccessModule,
    SharedUiButtonModule,
  ],
  declarations: [DepositTabNavComponent, DepositNavControlComponent],
  exports: [DepositNavControlComponent, DepositTabNavComponent],
})
export class FlowDepositUiDepositNavigatorModule {}
