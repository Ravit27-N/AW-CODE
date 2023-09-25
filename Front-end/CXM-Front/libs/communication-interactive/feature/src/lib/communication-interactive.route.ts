import { Routes } from '@angular/router';
import { CommunicationInteractiveComponent } from './communication-interactive.component';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';

export const CommunicationInteractiveRoute: Routes = [
  {
    path: '',
    component: CommunicationInteractiveComponent,
    children: [
      {
        path: '',
        loadChildren: () => import('@cxm-smartflow/communication-interactive/ui/featured-choose-template')
          .then(m => m.CommunicationInteractiveUiFeaturedChooseTemplateModule),
        data: {
          breadcrumb: getBreadcrumb().deposit.communicationInteractive
        }
      },
      {
        path: 'editor',
        loadChildren: () => import('@cxm-smartflow/communication-interactive/ui/featured-communication-interactive-editor')
          .then(m => m.CommunicationInteractiveUiFeaturedCommunicationInteractiveEditorModule)
      },
      {
        path: 'success',
        loadChildren: () => import('@cxm-smartflow/communication-interactive/ui/featured-communication-interactive-success-page')
          .then(m => m.CommunicationInteractiveUiFeaturedCommunicationInteractiveSuccessPageModule)
      }
    ]
  }
];
