import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PageDemandListComponent } from './pages/page-demand-list/page-demand-list.component';
import { PageDemandCreateComponent } from './pages/page-demand-create/page-demand-create.component';
import { PageDemandDetailsComponent } from './pages/page-demand-details/page-demand-details.component';
import { PageDemandEditComponent } from './pages/page-demand-edit/page-demand-edit.component';

const routes: Routes = [
  {
    path: '',
    component: PageDemandListComponent,
    data: {
      breadcrumb: 'List demands',
    },
  },
  {
    path: 'create',
    component: PageDemandCreateComponent,
    data: {
      breadcrumb: 'Create demand',
    },
  },
  {
    path: 'view/:id',
    component: PageDemandDetailsComponent,
  },
  {
    path: 'edit/:id',
    component: PageDemandEditComponent,
    data: {
      breadcrumb: 'Edit demand',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DemandRoutingModule {}
