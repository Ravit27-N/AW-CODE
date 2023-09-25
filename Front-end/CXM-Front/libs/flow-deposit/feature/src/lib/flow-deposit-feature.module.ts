import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlowDepositComponent } from './flow-deposit.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FlowDepositDataAccessModule } from '@cxm-smartflow/flow-deposit/data-access';
import { FlowDepositUiDepositNavigatorModule } from '@cxm-smartflow/flow-deposit/ui/deposit-navigator';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedGuardPendingChangeModule } from '@cxm-smartflow/flow-deposit/guard/pending-change';
import { SharedUiStepperModule } from '@cxm-smartflow/shared/ui/stepper';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { FlowDepositResolverService } from '../../../data-access/src/lib/services/flow-deposit-resolver-service';
import { SharedUiSpinnerModule } from '@cxm-smartflow/shared/ui/spinner';
import { SharedUiStepModule } from '@cxm-smartflow/shared/ui/step';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SharedTranslateModule.forRoot(),
    SharedUiSpinnerModule,
    FlowDepositDataAccessModule,
    FlowDepositUiDepositNavigatorModule,
    HttpClientModule,
    NgDynamicBreadcrumbModule,
    SharedCommonTypoModule,
    SharedUiStepperModule,
    SharedUiStepModule,
    SharedGuardPendingChangeModule,
    MaterialModule,
    RouterModule.forChild([
      {
        path: '',
        component: FlowDepositComponent,
        children: [
          {
            path: '',
            redirectTo: 'list',
          },
          {
            path: 'list',
            loadChildren: () =>
              import('@cxm-smartflow/flow-deposit/ui/feature-list').then(
                (m) => m.FlowDepositUiFeatureListModule
              ),
          },
          {
            path: 'acquisition',
            data: {
              breadcrumb: getBreadcrumb().deposit.acquisition,
            },
            loadChildren: () =>
              import('@cxm-smartflow/flow-deposit/ui/acquisition').then(
                (m) => m.FlowDepositUiAcquisitionModule
              ),
          },
          {
            path: 'pre-analysis',
            data: {
              breadcrumb: getBreadcrumb().deposit.acquisition,
            },
            loadChildren: () =>
              import('@cxm-smartflow/flow-deposit/ui/pre-analysis').then(
                (m) => m.FlowDepositUiPreAnalysisModule
              ),
            resolve: { flowDepositInitData: FlowDepositResolverService },
          },
          {
            path: 'analysis-result',
            data: {
              breadcrumb: getBreadcrumb().deposit.acquisition,
            },
            loadChildren: () =>
              import('@cxm-smartflow/flow-deposit/ui/analysis-result').then(
                (m) => m.FlowDepositUiAnalysisResultModule
              ),
            resolve: { flowDepositInitData: FlowDepositResolverService },
          },
          {
            path: 'production-criteria',
            data: {
              breadcrumb: getBreadcrumb().deposit.acquisition,
            },
            loadChildren: () =>
              import('@cxm-smartflow/flow-deposit/ui/production-criteria').then(
                (m) => m.FlowDepositUiProductionCriteriaModule
              ),
            resolve: { flowDepositInitData: FlowDepositResolverService },
          },
          {
            path: 'finished',
            data: {
              breadcrumb: getBreadcrumb().deposit.acquisition,
            },
            loadChildren: () =>
              import('@cxm-smartflow/flow-deposit/ui/finished').then(
                (m) => m.FlowDepositUiFinishedModule
              ),
            resolve: { flowDepositInitData: FlowDepositResolverService },
          },
          {
            path: 'validate-result',
            loadChildren: () =>
              import('@cxm-smartflow/flow-deposit/ui/validate-result').then(
                (m) => m.FlowDepositUiValidateResultModule
              ),
          },
        ],
      },
    ]),
  ],
  declarations: [FlowDepositComponent],
  exports: [FlowDepositComponent],
})
export class FlowDepositFeatureModule {}
