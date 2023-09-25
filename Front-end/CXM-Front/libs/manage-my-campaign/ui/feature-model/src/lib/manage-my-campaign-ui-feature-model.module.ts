import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureModelComponent } from './feature-model/feature-model.component';
import { RouterModule } from '@angular/router';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { HttpClientModule } from '@angular/common/http';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    // For Translate
    HttpClientModule,
    SharedTranslateModule.forRoot(),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedUiButtonModule,
    RouterModule.forChild([
      {
        path: '',
        component: FeatureModelComponent,
      },
    ]),
  ],
  declarations: [FeatureModelComponent],
  exports: [FeatureModelComponent],
})
export class ManageMyCampaignUiFeatureModelModule {}
