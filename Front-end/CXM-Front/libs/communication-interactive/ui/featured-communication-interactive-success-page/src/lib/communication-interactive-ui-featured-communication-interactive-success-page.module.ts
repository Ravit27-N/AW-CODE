import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CommunicationInteractiveSuccessPageComponent } from './communication-interactive-success-page.component';
import { RouterModule } from '@angular/router';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';

@NgModule({
  imports: [
    CommonModule,
    SharedCommonTypoModule,
    RouterModule.forChild([{ path: '', component: CommunicationInteractiveSuccessPageComponent }])
  ],
  declarations: [
    CommunicationInteractiveSuccessPageComponent
  ]
})
export class CommunicationInteractiveUiFeaturedCommunicationInteractiveSuccessPageModule {
}
