import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { ProfileHeaderComponent } from './profile-header.component';
import { SharedUiRoundButtonModule } from '@cxm-smartflow/shared/ui/round-button';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedTranslateModule.forRoot(),
    SharedCommonTypoModule,
    NgDynamicBreadcrumbModule,
    SharedUiButtonModule,
    SharedUiRoundButtonModule,
    MaterialModule
  ],
  declarations: [ProfileHeaderComponent],
  exports: [ProfileHeaderComponent],
})
export class ProfileUiProfileHeaderModule {}
