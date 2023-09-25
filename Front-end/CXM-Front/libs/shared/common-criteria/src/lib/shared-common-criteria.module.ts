import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CxmDatetimeHeaderComponent, CommonCriteriaFilterComponent } from './common-criteria-filter';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';

@NgModule({
  imports: [
    CommonModule,
    TranslateModule,
    ReactiveFormsModule,
    MaterialModule,
    FormsModule,
    SharedPipesModule,
    SharedCommonTypoModule
  ],
  declarations: [
    CxmDatetimeHeaderComponent,
    CommonCriteriaFilterComponent
  ],
  exports: [
    CommonCriteriaFilterComponent
  ]
})
export class SharedCommonCriteriaModule {  }
