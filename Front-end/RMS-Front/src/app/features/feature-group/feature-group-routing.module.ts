import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageGroupListComponent } from './pages/page-group-list/page-group-list.component';
import { PageGroupCreateComponent } from './pages/page-group-create/page-group-create.component';
import { PageGroupEditComponent } from './pages/page-group-edit/page-group-edit.component';

const routes: Routes = [
  {
    path: '',
    component: PageGroupListComponent,
    data: {
      breadcrumb: 'List',
    },
  },
  {
    path: 'create',
    component: PageGroupCreateComponent,
    data: {
      breadcrumb: 'Create',
    },
  },
  {
    path: 'edit',
    component: PageGroupEditComponent,
    data: {
      breadcrumb: 'Edit',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FeatureGroupRoutingModule {}
