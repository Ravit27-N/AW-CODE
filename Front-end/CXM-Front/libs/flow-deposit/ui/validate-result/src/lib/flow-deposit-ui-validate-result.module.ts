import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ValidateResultComponent } from './validate-result.component';
import { RouterModule } from '@angular/router';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([{ path: '', component: ValidateResultComponent }]),
    SharedCommonTypoModule,
    SharedTranslateModule.forRoot(),
  ],
  declarations: [ValidateResultComponent],
})
export class FlowDepositUiValidateResultModule {}
