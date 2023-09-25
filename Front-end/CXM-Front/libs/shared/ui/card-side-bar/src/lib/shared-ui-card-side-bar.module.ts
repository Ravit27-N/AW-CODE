import { RouterModule } from '@angular/router';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardSideBarComponent } from './card-side-bar/card-side-bar.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule
  ],
  declarations: [
    CardSideBarComponent
  ],
  exports: [
    CardSideBarComponent
  ]
})
export class SharedUiCardSideBarModule {}
