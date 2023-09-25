import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { SharedCommonTypoModule } from '@cxm-smartflow/shared/common-typo';
import { SharedDirectivesDragDropModule } from '@cxm-smartflow/shared/directives/drag-drop';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SharedUiButtonModule } from '@cxm-smartflow/shared/ui/button';
import { getBreadcrumb } from '@cxm-smartflow/shared/utils';
import { NgDynamicBreadcrumbModule } from 'ng-dynamic-breadcrumb';
import { ClientCreationPageComponent } from './client-creation-page/client-creation-page.component';
import { ClientFormComponent } from './client-form/client-form.component';
import { ClientModificationPageComponent } from './client-modification-page/client-modification-page.component';
import { FileUploadFragementComponent } from './file-upload-fragement/file-upload-fragement.component';
import { AssociateListComponent } from './associate-list/associate-list.component';
import { SharedPipesModule } from '@cxm-smartflow/shared/pipes';
import { SharedDirectivesTooltipModule } from '@cxm-smartflow/shared/directives/tooltip';
import { InlineInputComponent } from './associate-list/inline-input';
import { FunctionalityPageComponent } from './functionality-page/functionality-page.component';
import { OffloadingFormComponent } from './offloading-form/offloading-form.component';
import { FillersFormComponent } from './fillers-form/fillers-form.component';
import { SharedTranslateModule } from '@cxm-smartflow/shared/translate';
import { FluxFormComponent } from './flux-form/flux-form.component';
import { ConfigurationFilePageComponent } from './configration-file-page/configuration-file-page.component';
import { VersionHistoryComponent } from './version-history/version-history.component';
import { FileConfigComponent } from './file-config/file-config.component';
import { ConfigFileGuardService, ClientControlService } from '@cxm-smartflow/client/data-access';
import { FileConfigEditorComponent } from './file-config-editor/file-config-editor.component';
import { FileConfigEditorService } from './file-config-editor/file-config-editor.service';
import { DraggableElementComponent } from './draggable-element/draggable-element.component';
import { DistributeCriteriaComponent } from './distribute-criteria/distribute-criteria.component';
import { SettingDigitalChannelPageComponent } from './setting-digital-channel-page/setting-digital-channel-page.component';
import { SharedUiFormInputSelectionModule } from '@cxm-smartflow/shared/ui/form-input-selection';
import { ClientUiClientPopupDialogModule } from '@cxm-smartflow/client/ui/client-popup-dialog';
import {SharedDirectivesTitleCaseModule} from "@cxm-smartflow/shared/directives/title-case";
import { SharedDirectivesInfoTooltipModule } from '@cxm-smartflow/shared/directives/info-tooltip';
import { ConfirmRevertConfigurationVersionComponent } from './confirm-revert-configuration-version/confirm-revert-configuration-version.component';
import {
  ConfirmRevertConfigurationVersionService
} from "./confirm-revert-configuration-version/confirm-revert-configuration-version.service";
import {SharedFragmentsReturnAddressModule} from "@cxm-smartflow/shared/fragments/return-address";

const routes: Routes = [
  {
    path: 'create',
    component: ClientCreationPageComponent,
    data: {
      breadcrumb: getBreadcrumb().client.create
    },
    canDeactivate: [ClientControlService],
  },
  {
    path: 'modify/:id',
    component: ClientModificationPageComponent,
    data: {
      breadcrumb: getBreadcrumb().client.modify
    },
    canDeactivate: [ClientControlService],
  },
  {
    path: 'configuration/:clientName',
    component: ConfigurationFilePageComponent,
    data: {
      breadcrumb: getBreadcrumb().client.configuration
    },
    canActivate: [ConfigFileGuardService],
    canDeactivate: [ClientControlService],
  },
  {
    path: 'setting-digital-channel/:clientName',
    component: SettingDigitalChannelPageComponent,
    data: {
      breadcrumb: getBreadcrumb().client.configuration
    },
    canActivate: [ConfigFileGuardService],
    canDeactivate: [ClientControlService],
  },
]


@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedCommonTypoModule,
    SharedPipesModule,
    SharedTranslateModule.forRoot(),
    SharedUiButtonModule,
    NgDynamicBreadcrumbModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    SharedUiButtonModule,
    SharedDirectivesTooltipModule,
    SharedDirectivesDragDropModule,
    SharedUiFormInputSelectionModule,
    ClientUiClientPopupDialogModule,
    SharedDirectivesTitleCaseModule,
    SharedDirectivesInfoTooltipModule,
    SharedFragmentsReturnAddressModule,
  ],
  declarations: [
    ClientCreationPageComponent,
    ClientModificationPageComponent,
    ClientFormComponent,
    FileUploadFragementComponent,
    AssociateListComponent,
    InlineInputComponent,
    FunctionalityPageComponent,
    OffloadingFormComponent,
    FillersFormComponent,
    FluxFormComponent,
    ConfigurationFilePageComponent,
    VersionHistoryComponent,
    FileConfigComponent,
    FileConfigEditorComponent,
    DraggableElementComponent,
    DistributeCriteriaComponent,
    SettingDigitalChannelPageComponent,
    ConfirmRevertConfigurationVersionComponent,
  ],
  providers: [
    FileConfigEditorService,
    ConfirmRevertConfigurationVersionService,
  ]
})
export class ClientUiFeatureClientModificationModule {}
