import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeaturedChooseTemplateComponent } from './featured-choose-template.component';
import { RouterModule } from '@angular/router';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { MatIconModule } from '@angular/material/icon';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { CommunicationInteractiveDataAccessModule } from '@cxm-smartflow/communication-interactive/data-access';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';


@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild([{ path: '', component: FeaturedChooseTemplateComponent }]),
    SharedCommonTypoModule,
    NgDynamicBreadcrumbModule,
    MatIconModule,
    CommunicationInteractiveDataAccessModule,
    SharedTranslateModule.forRoot(),
    SharedUiButtonModule,
    SharedDirectivesTooltipModule,
    MaterialModule
  ],
  declarations: [
    FeaturedChooseTemplateComponent
  ]
})
export class CommunicationInteractiveUiFeaturedChooseTemplateModule { }
