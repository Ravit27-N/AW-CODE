import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeatureRoleListComponent } from './feature-role-list/feature-role-list.component';
import { FeatureRoleCreateComponent } from './feature-role-create/feature-role-create.component';
import { FeatureRoleEditComponent } from './feature-role-edit/feature-role-edit.component';
import { FeatureRoleFormDeactivate } from './feature-role-form/feature-role-form.deactivate';

const routes: Routes = [
  {
    path: '',
    component: FeatureRoleListComponent,
    data: { breadcrumb: 'List Roles' },
  },
  {
    path: 'add',
    component: FeatureRoleCreateComponent,
    canDeactivate: [FeatureRoleFormDeactivate],
    data: { breadcrumb: 'Create Roles' },
  },
  {
    path: 'edit',
    component: FeatureRoleEditComponent,
    canDeactivate: [FeatureRoleFormDeactivate],
    data: { breadcrumb: 'Edit Roles' },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FeatureRoleRoutingModule {}
