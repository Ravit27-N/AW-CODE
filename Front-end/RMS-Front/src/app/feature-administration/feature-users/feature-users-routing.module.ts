import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeatureUserListComponent } from './feature-user-list';
import {
  FeatureUserAddComponent,
  FeatureUserAddDeactivate,
} from './feature-user-add';
import { Access, AccessGuardService } from '../../auth';

const routes: Routes = [
  {
    path: '',
    component: FeatureUserListComponent,
    canDeactivate: [FeatureUserAddDeactivate],
    canActivate: [AccessGuardService],
    data: { perm: Access.user, breadcrumb: 'List User' },
  },
  {
    path: 'add',
    component: FeatureUserAddComponent,
    canDeactivate: [FeatureUserAddDeactivate],
    canActivate: [AccessGuardService],
    data: { perm: Access.user, breadcrumb: 'Create User' },
  },
  {
    path: 'update',
    component: FeatureUserAddComponent,
    canDeactivate: [FeatureUserAddDeactivate],
    canActivate: [AccessGuardService],
    data: { perm: Access.user, breadcrumb: 'Update User' },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FeatureUsersRoutingModule {}
