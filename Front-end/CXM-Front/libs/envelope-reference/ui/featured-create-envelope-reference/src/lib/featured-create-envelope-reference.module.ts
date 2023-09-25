import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Route, RouterModule } from '@angular/router';
import { CreateEnvelopeReferenceComponent } from './create-envelope-reference.component';
import { HttpClientModule } from '@angular/common/http';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { MatPasswordStrengthModule } from '@angular-material-extensions/password-strength';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import {FeaturedEnvelopeReferenceFormModule} from "../../../featured-envelope-reference-form/src";
import {EnvelopeReferenceDataAccessModule} from "@cxm-smartflow/envelope-reference/data-access";

export const envelopeReferenceUiCreateRoutes: Route[] = [
  {
    path: '',
    component: CreateEnvelopeReferenceComponent,
  },
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    SharedCommonTypoModule,
    RouterModule.forChild(envelopeReferenceUiCreateRoutes),
    HttpClientModule,
    NgDynamicBreadcrumbModule,
    SharedTranslateModule.forRoot(),
    MatSlideToggleModule,
    MatPasswordStrengthModule,
    FeaturedEnvelopeReferenceFormModule,
    EnvelopeReferenceDataAccessModule
  ],
  declarations: [CreateEnvelopeReferenceComponent],
})
export class FeaturedCreateEnvelopeReferenceModule {}
