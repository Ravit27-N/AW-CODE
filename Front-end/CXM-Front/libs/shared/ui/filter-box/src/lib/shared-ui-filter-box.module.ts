import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FilterBoxComponent } from './filter-box.component';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { TranslateModule } from '@ngx-translate/core';
import { MatMenuModule } from '@angular/material/menu';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  imports: [
    CommonModule,
    SharedCommonTypoModule,
    TranslateModule,
    MatMenuModule,
    ReactiveFormsModule,
    MatIconModule,
  ],
  declarations: [FilterBoxComponent],
  exports: [FilterBoxComponent],
})
export class SharedUiFilterBoxModule {}
