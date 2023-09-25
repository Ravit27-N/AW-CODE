import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FragmentReturnAddressComponent } from './fragment-return-address.component';
import { TranslateModule } from "@ngx-translate/core";
import { SharedUiFormInputSelectionModule } from "@cxm-smartflow/shared/ui/form-input-selection";
import { ReactiveFormsModule } from "@angular/forms";
import {SharedDirectivesTooltipModule} from "@cxm-smartflow/shared/directives/tooltip";

@NgModule({
    imports: [
        CommonModule,
        TranslateModule,
        SharedUiFormInputSelectionModule,
        ReactiveFormsModule,
        SharedDirectivesTooltipModule,
    ],
  declarations: [
    FragmentReturnAddressComponent,
  ],
  exports: [
    FragmentReturnAddressComponent,
  ],
})
export class SharedFragmentsReturnAddressModule {}
