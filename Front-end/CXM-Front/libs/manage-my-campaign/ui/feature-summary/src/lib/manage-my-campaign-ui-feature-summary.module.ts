import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureSummaryComponent } from './feature-summary/feature-summary.component';
import { RouterModule } from '@angular/router';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedUiInputModule } from '@cxm-smartflow/shared/ui/input';
@NgModule({
  imports: [CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedUiButtonModule,
    SharedUiInputModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureSummaryComponent
      }
    ])
  ],
  declarations: [
    FeatureSummaryComponent
  ],
  exports: [FeatureSummaryComponent]
})

export class ManageMyCampaignUiFeatureSummaryModule {}
