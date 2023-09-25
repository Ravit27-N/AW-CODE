import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileTabNavComponent } from './profile-tab-nav.component';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [CommonModule, SharedTranslateModule.forRoot()],
  declarations: [
    ProfileTabNavComponent
  ],
  exports: [
    ProfileTabNavComponent
  ]
})
export class ProfileUiProfileNavigatorModule {}
