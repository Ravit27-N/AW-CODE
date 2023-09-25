import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchBoxComponent } from './search-box.component';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    SharedDirectivesTooltipModule,
    MatIconModule,
    ReactiveFormsModule,
    SharedTranslateModule,
  ],
  declarations: [SearchBoxComponent],
  exports: [SearchBoxComponent],
})
export class SharedUiSearchBoxModule {}
