import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeatureProjectListComponent } from './feature-project-list/feature-project-list.component';

const routes: Routes = [
  {
    path: '',
    component: FeatureProjectListComponent,
    data: {
      breadcrumb: 'List',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FeatureProjectsRoutingModule {}
