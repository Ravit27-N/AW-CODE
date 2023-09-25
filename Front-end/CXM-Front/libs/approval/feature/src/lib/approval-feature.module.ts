import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EspaceValidationComponent } from './espace-validation.component';
import { RouterModule, Routes } from '@angular/router';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

const routes: Routes = [
  {
    path: '',
    component: EspaceValidationComponent,
    children: [
      {
        path: '',
        loadChildren: () =>
          import(
            '@cxm-smartflow/approval/ui/feature-espace-validation-list'
          ).then((m) => m.ApprovalUiFeatureEspaceValidationListModule),
      },
    ],
  },
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedTranslateModule.forRoot(),
  ],
  declarations: [EspaceValidationComponent],
})
export class ApprovalFeatureModule {}
