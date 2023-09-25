import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header.component';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    SharedTranslateModule.forRoot()
  ],
  declarations: [
    HeaderComponent
  ],
  exports: [HeaderComponent]
})
export class FlowDepositUiHeaderModule {}
