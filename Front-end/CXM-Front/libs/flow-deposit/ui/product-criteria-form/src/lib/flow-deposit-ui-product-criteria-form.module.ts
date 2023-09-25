import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';
import { SharedUiInputModule } from '@cxm-smartflow/shared/ui/input';
import { SharedUiUploadingFragmentModule } from '@cxm-smartflow/shared/ui/uploading-fragment';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { AttachementButtonComponent } from './attachement-button/attachement-button.component';
import { BackgroundPrewviewComponent } from './background-preview/background-preview.component';
import { BrowseButtonComponent } from './browse-button/browse-button.component';
import { CriteriaFormComponent } from './criteria-form/criteria-form.component';
import { CriteriaOptionsComponent } from './criteria-options/criteria-options.component';
import { InfoTooltipComponent } from "./criteria-options/info-tooltip.component";

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedUiInputModule,
    SharedTranslateModule.forRoot(),
    SharedUiFormInputSelectionModule,
    SharedUiButtonModule,
    SharedUiUploadingFragmentModule,
    NgxExtendedPdfViewerModule,
    SharedDirectivesTooltipModule,
    SharedPipesModule
  ],
  declarations: [
    CriteriaFormComponent,
    CriteriaOptionsComponent,
    BrowseButtonComponent,
    AttachementButtonComponent,
    BackgroundPrewviewComponent,
    InfoTooltipComponent
  ],
  exports: [
    CriteriaFormComponent,
    CriteriaOptionsComponent
  ]
})
export class FlowDepositUiProductCriteriaFormModule {}
