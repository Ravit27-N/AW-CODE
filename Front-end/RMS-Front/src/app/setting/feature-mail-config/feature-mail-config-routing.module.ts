import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeatureMailConfigListComponent } from './feature-mail-config-list/feature-mail-config-list.component';

const routes: Routes = [
  {
    path: '',
    component: FeatureMailConfigListComponent,
    data: {
      breadcrumb: 'List',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FeatureMailConfigRoutingModule {}
