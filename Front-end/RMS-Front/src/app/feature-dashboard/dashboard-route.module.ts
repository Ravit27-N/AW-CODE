import { NgModule } from '@angular/core';
import { FeatureDashboardComponent } from './feature-dashboard.component';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    component: FeatureDashboardComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
})
export class DashboardRouteModule {}
