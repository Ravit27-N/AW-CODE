import { CxmCampaignComponent } from './cxm-campaign/cxm-campaign.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    component: CxmCampaignComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'follow-my-campaign'
      },
      {
        path: 'manage-my-campaign',
        loadChildren: () =>
          import('@cxm-smartflow/manage-my-campaign/feature').then(
            (m) => m.ManageMyCampaignFeatureModule
          ),
      },
      {
        path: 'follow-my-campaign',
        loadChildren: () =>
          import('@cxm-smartflow/follow-my-campaign/feature').then(
            (m) => m.FollowMyCampaignFeatureModule
          ),
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CxmCampaignRoutingModule {}
