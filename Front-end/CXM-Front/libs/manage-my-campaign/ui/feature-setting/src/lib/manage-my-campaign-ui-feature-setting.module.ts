
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { SharedUiThumbnailCardModule } from '@cxm-smartflow/shared/ui/thumbnail-card';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureSettingComponent } from './feature-setting/feature-setting.component';
import { SharedUiInputModule } from '@cxm-smartflow/shared/ui/input';

@NgModule({
  imports: [
    SharedUiInputModule,
    SharedUiThumbnailCardModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedUiSpinnerModule,
    AuthDataAccessModule.forRoot(),
    RouterModule.forChild([
      {
        path: '',
        component: FeatureSettingComponent,
      },
    ]),
    SharedUiButtonModule,
  ],
  declarations: [FeatureSettingComponent],
  exports: [FeatureSettingComponent],
})
export class ManageMyCampaignUiFeatureSettingModule {}
