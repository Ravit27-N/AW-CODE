import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EnvelopeReferenceFormComponent } from './envelope-reference-form.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
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
    SharedPipesModule,
    SharedDirectivesTitleCaseModule,
    SharedFragmentsReturnAddressModule,
  ],
  declarations: [EnvelopeReferenceFormComponent],

  exports: [EnvelopeReferenceFormComponent],
})
export class FeaturedEnvelopeReferenceFormModule {}
