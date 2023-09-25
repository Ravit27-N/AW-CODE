import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { SharedUiInputModule } from '@cxm-smartflow/shared/ui/input';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { RouterModule } from '@angular/router';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedUiLayoutModule } from '@cxm-smartflow/shared/ui/layout';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedTranslateModule.forRoot(),
    AuthDataAccessModule.forRoot(),
    SharedUiButtonModule,
    SharedUiInputModule,
    SharedUiSpinnerModule,
    SharedCommonTypoModule,
    SharedDirectivesTooltipModule,
    SharedUiLayoutModule
  ],
  declarations: [
    LoginComponent
  ],
  exports: [
    LoginComponent
  ]
})
export class AuthUiLoginModule {}
