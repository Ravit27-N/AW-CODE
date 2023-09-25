import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UpdateEnvelopeReferenceComponent } from './update-envelope-reference.component';
import { RouterModule } from '@angular/router';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { MatPasswordStrengthModule } from '@angular-material-extensions/password-strength';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import {FeaturedEnvelopeReferenceFormModule} from "../../../featured-envelope-reference-form/src";
import {EnvelopeReferenceDataAccessModule} from "../../../../data-access/src/lib/envelope-reference-data-access.module";

@NgModule({
  imports: [
    CommonModule,
    SharedCommonTypoModule,
    NgDynamicBreadcrumbModule,
    SharedUiButtonModule,
    RouterModule.forChild([
      {
        path: '',
        component: UpdateEnvelopeReferenceComponent,
      },
    ]),
    SharedTranslateModule.forRoot(),
    MatSlideToggleModule,
    MatPasswordStrengthModule,
    FeaturedEnvelopeReferenceFormModule,
    EnvelopeReferenceDataAccessModule
  ],
  declarations: [UpdateEnvelopeReferenceComponent],
})
export class FeaturedUpdateEnvelopeReferenceModule {}
