import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SettingComponent } from './setting.component';
import { RouterModule } from '@angular/router';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { SettingDataAccessModule } from '@cxm-smartflow/setting/data-access';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    NgDynamicBreadcrumbModule,
    SettingDataAccessModule,
    SettingDataAccessModule,
    SharedTranslateModule.forRoot(),
    RouterModule.forChild([
      {
        path: '',
        component: SettingComponent,
        children: [
          {
            path: '',
            redirectTo: 'resources',
          },
          {
            path: 'resources',
            data: {
              breadcrumb: getBreadcrumb().setting.list,
            },
            loadChildren: () =>
              import('@cxm-smartflow/setting/ui/featured-list-resource').then(
                (m) => m.SettingUiFeaturedListResourceModule
              ),
          },
        ],
      },
    ]),
  ],
  declarations: [SettingComponent],
})
export class SettingFeatureModule {}
