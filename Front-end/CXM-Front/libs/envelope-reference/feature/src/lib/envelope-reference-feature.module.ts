import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EnvelopeReferenceComponent } from './envelope-reference.component';
import { RouterModule } from '@angular/router';
import { UserDataAccessModule } from '@cxm-smartflow/user/data-access';
import { ProfileUiProfileNavigatorModule } from '@cxm-smartflow/profile/ui/profile-navigator';
import { SharedDirectivesCanVisibilityModule } from '@cxm-smartflow/shared/directives/can-visibility';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    ProfileUiProfileNavigatorModule,
    UserDataAccessModule,
    SharedDirectivesCanVisibilityModule,
    SharedTranslateModule.forRoot(),
    NgDynamicBreadcrumbModule,
    RouterModule.forChild([
      {
        path: '',
        component: EnvelopeReferenceComponent,
        children: [
          {
            path: 'envelope-references',
            redirectTo: 'list',
          },
          {
            path: 'list',
            data: {
              breadcrumb: getBreadcrumb().envelope_reference.list,
            },
            loadChildren: () =>
              import('@cxm-smartflow/envelope-reference/ui/list-envelope-references').then(
                (m) => m.FeaturedListEnvelopeReferencesModule
              ),
          },
          {
            path: 'create',
            data: {
              breadcrumb: getBreadcrumb().envelope_reference.create,
            },
            loadChildren: () =>
              import('@cxm-smartflow/envelope-reference/ui/featured-create-envelope-reference').then(
                (m) => m.FeaturedCreateEnvelopeReferenceModule
              ),
          },
          {
            path: 'update',
            data: {
              breadcrumb: getBreadcrumb().envelope_reference.edit,
            },
            loadChildren: () =>
              import('@cxm-smartflow/envelope-reference/ui/featured-update-envelope-reference').then(
                (m) => m.FeaturedUpdateEnvelopeReferenceModule
              ),
          }
        ],
      },
    ]),
  ],
  declarations: [EnvelopeReferenceComponent],
})
export class EnvelopeReferenceFeatureModule {}
