import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomHeaderComponent } from './header/custom-header.component';
import { CustomFooterComponent } from './footer/custom-footer.component';
import { NavigationBarComponent } from './header/navigation-bar/navigation-bar.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';
import { AuthUiFeatureChangePasswordModule } from '@cxm-smartflow/auth/ui/feature-change-password';
import { SharedDirectivesToggleElementModule } from '@cxm-smartflow/shared/directives/toggle-element';
import { ValidationPopupService } from './header/validation-popup/validation-popup.service';
import { ValidationPopupComponent } from './header/validation-popup/validation-popup.component';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    TranslateModule,
    RouterModule,
    AuthUiFeatureChangePasswordModule,
    SharedDirectivesToggleElementModule
  ],
  declarations: [
    CustomHeaderComponent,
    CustomFooterComponent,
    NavigationBarComponent,
    ValidationPopupComponent,
  ],
  exports: [
    CustomHeaderComponent,
    CustomFooterComponent,
  ],
  providers: [ValidationPopupService],
})
export class SharedUiLayoutModule {}
