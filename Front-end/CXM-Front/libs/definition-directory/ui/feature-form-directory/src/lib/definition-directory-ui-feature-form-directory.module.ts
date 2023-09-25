import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureFormDirectoryComponent } from './feature-form-directory.component';
import { FormDirectoryStepOneComponent } from './form-directory-step-one/form-directory-step-one.component';
import { FormDirectoryStepTwoComponent } from './form-directory-step-two/form-directory-step-two.component';
import { FormDirectoryStepThreeComponent } from './form-directory-step-three/form-directory-step-three.component';
import { MatDividerModule } from '@angular/material/divider';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { TranslateModule } from '@ngx-translate/core';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { ComponentFieldItemComponent } from './form-directory-step-two/component-field-item/component-field-item.component';
import { MatButtonModule } from '@angular/material/button';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { ClientFormComponent } from './form-directory-step-three/client-form/client-form.component';

@NgModule({
  imports: [
    CommonModule,
    MatDividerModule,
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    TranslateModule,
    SharedUiButtonModule,
    ReactiveFormsModule,
    SharedDirectivesTooltipModule,
    MatIconModule,
    MatListModule,
    MatButtonModule,
    DragDropModule,
    SharedUiFormInputSelectionModule,
    MatCheckboxModule,
    SharedUiFormInputSelectionModule,
  ],
  declarations: [
    FeatureFormDirectoryComponent,
    FormDirectoryStepOneComponent,
    FormDirectoryStepTwoComponent,
    FormDirectoryStepThreeComponent,
    ComponentFieldItemComponent,
    ClientFormComponent,
  ],
  exports: [FeatureFormDirectoryComponent],
})
export class DefinitionDirectoryUiFeatureFormDirectoryModule {}
