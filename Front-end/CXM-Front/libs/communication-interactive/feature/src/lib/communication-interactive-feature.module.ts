import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CommunicationInteractiveRoute } from './communication-interactive.route';
import { CommunicationInteractiveComponent } from './communication-interactive.component';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(CommunicationInteractiveRoute),
    SharedTranslateModule.forRoot(),
  ],
  declarations: [
    CommunicationInteractiveComponent
  ],
  exports: [
    CommunicationInteractiveComponent
  ]
})
export class CommunicationInteractiveFeatureModule {
}
