import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureDestinationComponent } from './feature-destination/feature-destination.component';
import { RouterModule } from '@angular/router';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
@NgModule({
  imports: [CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedUiButtonModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureDestinationComponent
      }
    ])
  ],
  declarations: [
    FeatureDestinationComponent
  ],
  exports: [FeatureDestinationComponent]
})
export class ManageMyCampaignUiFeatureDestinationModule {}
