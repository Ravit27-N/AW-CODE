import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ShowComfirmationComponent } from './show-comfirmation/show-comfirmation.component';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { RouterModule } from '@angular/router';

import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    SharedUiButtonModule,
    RouterModule.forChild([
      {
        path: '',
        component: ShowComfirmationComponent
      }
    ])
  ],
  declarations: [
    ShowComfirmationComponent
  ],
  exports: [
    ShowComfirmationComponent
  ]
})
export class ManageMyCampaignUiShowComfirmationModule {}
