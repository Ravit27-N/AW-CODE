import { AuthGuard, AuthorizeGuard } from '@cxm-smartflow/auth/data-access';
import { Routes } from '@angular/router';
import { CxmSmartflowComponent } from './cxm-smartflow/cxm-smartflow.component';
import { AboutComponent } from './about/about.component';
import { ConfirmLogoutComponent } from './confirm-logout/confirm-logout.component';
import { IntegrationComponent } from './integration/integration.component';
import { IntegrationFlowComponent } from './integration/integration-flow.component';

export const APP_ROUTES: Routes = [
  {
    path: '',
    component: CxmSmartflowComponent,
    canActivate: [AuthGuard],
    children: [],
  },
  {
    path: 'login',
    loadChildren: () =>
      import('@cxm-smartflow/auth/feature').then((m) => m.AuthFeatureModule),
  },
  {
    path: 'forgot-password',
    loadChildren: () =>
      import('@cxm-smartflow/forgot-password').then(
        (m) => m.ForgotPasswordModule
      ),
  },
  {
    path: 'about',
    component: AboutComponent,
  },
  {
    path: 'confirm-logout',
    component: ConfirmLogoutComponent,
  },
  {
    path: 'preview-document',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('@cxm-smartflow/shared/ui/preview-document').then(
        (m) => m.PreviewDocumentModule
      ),
  },
  {
    path: 'preview-image',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('@cxm-smartflow/shared/ui/preview-image').then(
        (m) => m.SharedUiPreviewImageModule
      ),
  },
  {
    path: 'integration',
    component: IntegrationComponent,
  },
  {
    path: 'integration/flow/:flowid',
    component: IntegrationFlowComponent,
  },
  {
    path: 'blocked-account',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('@cxm-smartflow/blocked-account').then(
        (m) => m.BlockedAccountModule
      ),
  }
];
