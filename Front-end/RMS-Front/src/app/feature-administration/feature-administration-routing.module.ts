import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Access, AccessGuardService } from '../auth';
import { AwAdministrationComponent } from './aw-administration';
import { FeatureRoleListDeactivate } from './feature-roles/feature-role-list/feature-role-list.deactivate';
import { FeatureUserListDeactivate } from './feature-users/feature-user-list';

const routes: Routes = [
  {
    path: '',
    component: AwAdministrationComponent,
  },
  {
    path: 'users',
    loadChildren: () =>
      import('./feature-users/feature-users.module').then(
        (m) => m.FeatureUsersModule,
      ),
    canDeactivate: [FeatureUserListDeactivate],
    canActivate: [AccessGuardService],
    data: { perm: Access.user, breadcrumb: 'Users' },
  },
  {
    path: 'roles',
    loadChildren: () =>
      import('./feature-roles/feature-role.module').then(
        (m) => m.FeatureRoleModule,
      ),
    canActivate: [AccessGuardService],
    canDeactivate: [FeatureRoleListDeactivate],
    data: { perm: Access.user, breadcrumb: 'Roles' },
  },
  {
    path: 'groups',
    loadChildren: () =>  import('./../features/feature-group/feature-group.module').then(m => m.FeatureGroupModule),
    canActivate: [AccessGuardService],
    data: { perm: Access.user, breadcrumb: 'Groups' },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FeatureAdministrationRoutingModule {}
