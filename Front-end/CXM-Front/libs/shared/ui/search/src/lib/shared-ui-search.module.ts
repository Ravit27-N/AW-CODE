import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SearchComponent } from './search/search.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule
  ],
  declarations: [
    SearchComponent,
  ],
  exports: [
    SearchComponent
  ]
})
export class SharedUiSearchModule {}
