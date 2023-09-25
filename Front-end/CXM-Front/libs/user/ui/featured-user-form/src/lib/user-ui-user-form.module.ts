import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserFormComponent } from './user-form.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { MatPasswordStrengthModule } from '@angular-material-extensions/password-strength';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import {SharedDirectivesTitleCaseModule} from "@cxm-smartflow/shared/directives/title-case";
import {SharedFragmentsReturnAddressModule} from "@cxm-smartflow/shared/fragments/return-address";

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    ReactiveFormsModule,
    SharedTranslateModule.forRoot(),
    SharedUiFormInputSelectionModule,
    SharedDirectivesTooltipModule,
    FormsModule,
    MatSlideToggleModule,
    MatPasswordStrengthModule,
    SharedPipesModule,
    SharedDirectivesTitleCaseModule,
    SharedFragmentsReturnAddressModule,
  ],
  declarations: [UserFormComponent],

  exports: [UserFormComponent],
})
export class UserUiUserFormModule {}
