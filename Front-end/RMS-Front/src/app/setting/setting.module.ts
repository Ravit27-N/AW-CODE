import { CommonDialogComponent, RestoreDialogComponent } from './_dialog';
import {
  AdduniversityComponent,
  DialogViewUniversityComponent,
  UpdateuniversityComponent,
  ViewUniversityComponent,
} from './university';
import {
  MailconfigService,
  MailtemplateService,
  UniversityService,
  UserGroupAdminService,
} from '../core';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { CompanyprofileComponent } from './feature-company-profile';
import { ToastrModule } from 'ngx-toastr';
import {
  AddmailstatuschangeComponent,
  DialogviewmailstatusComponent,
  MailstatuschangeComponent,
  UpdatemailstatuschangeComponent,
} from './MailStatusChange';
import {
  DialogviewcandidateComponent,
  StatuecandidateComponent,
} from './statuecandidate';
import { DialogviewComponent, MailTemplateComponent } from './mail-template';

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SettingRoutingModule } from './setting-routing.module';
import { MaterialModule } from '../material';
import { AddstatuscandidateComponent } from './addstatuscandidate';
import { UpdatestatuscandidateComponent } from './updatestatuscandidate';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../shared';
import { QuillModule } from 'ngx-quill';
import { AddmailtemplateComponent } from './addmailtemplate';
import { UpdatemailtemplateComponent } from './updatemailtemplate';
import { MatCommonModule } from '@angular/material/core';
import {
  ConfigurationFormComponent,
  ConfigurationFormDialogComponent,
  MailconfigResolver,
  SystemConfigComponent,
} from './mailconfig';
import {
  AddstatusinterviewComponent,
  DialogviewstatusinterviewComponent,
  UpdatestatusinterviewComponent,
  ViewstatusinterviewComponent,
} from './statusInterview';
import {
  FeatureFormComponent,
  FeatureFormDialogComponent,
  FeatureViewComponent,
  RoleFormComponent,
  RoleFormDailogComponent,
  RoleViewComponent,
  UserFormDialogComponent,
  UserGroupDialogComponent,
  UserGroupFormComponent,
  UserGroupViewComponent,
  UserPasswordFormComponent,
  UserViewComponent,
} from './administration';
import {
  AddJobComponent,
  DialogViewJobComponent,
  UpdateJobComponent,
  ViewJobComponent,
} from './job';
import { NgxFileDragDropModule } from 'ngx-file-drag-drop';
import { UserFormComponent } from './administration/user-form';
import { TextMaskModule } from 'angular2-text-mask';
import { LineTruncationLibModule } from 'ngx-line-truncation';
import { AuthModule } from '../auth';
import Signature, { toolbarOption } from './signature';
import {
  AddFormProjectComponent,
  AddProjectComponent,
  ArchiveProjectDialogComponent,
  FeatureAddJobDescriptionComponent,
  FeatureAddJobDescriptionPopupComponent,
  ProjectComponent,
  UpdateProjectComponent,
} from './project';
import { CoreModule } from '../core/core.module';
import { FeatureInterviewTemplateListComponent } from './feature-interview-template/feature-interview-template-list';
import { FeatureFileManagerComponent } from './feature-file-manager/feature-file-manager.component';
import { AwSettingComponent } from './aw-setting';
import { FeatureInterviewTemplateAddComponent } from './feature-interview-template/feature-interview-template-add';
import { FeatureInterviewTemplateDetailComponent } from './feature-interview-template/feature-interview-template-detail';
import { CompanyOldProfileComponent } from './company-old-profile';
import { FeatureListJobComponent } from './job/feature-list-job';
import { FeatureMailTemplateComponent } from './feature-mail-template/feature-mail-template.component';
import { FeatureStatusCandidateComponent } from './feature-status-candidate/feature-status-candidate.component';
import { FeatureSystemConfigComponent } from './feature-system-config/feature-system-config.component';
import { FeatureUniversityComponent } from './feature-university/feature-university.component';

@NgModule({
  declarations: [
    SystemConfigComponent,
    AddstatuscandidateComponent,
    UpdatestatuscandidateComponent,
    StatuecandidateComponent,
    MailTemplateComponent,
    AddmailtemplateComponent,
    UpdatemailtemplateComponent,
    DialogviewComponent,
    DialogviewcandidateComponent,
    CompanyprofileComponent,
    MailstatuschangeComponent,
    AddmailstatuschangeComponent,
    UpdatemailstatuschangeComponent,
    DialogviewmailstatusComponent,
    AddstatusinterviewComponent,
    UpdatestatusinterviewComponent,
    ViewstatusinterviewComponent,
    DialogviewstatusinterviewComponent,
    AdduniversityComponent,
    ViewUniversityComponent,
    UpdateuniversityComponent,
    ConfigurationFormComponent,
    ConfigurationFormDialogComponent,
    CommonDialogComponent,
    RoleViewComponent,
    RoleFormComponent,
    FeatureViewComponent,
    FeatureFormComponent,
    FeatureFormDialogComponent,
    RoleFormDailogComponent,
    AddJobComponent,
    ViewJobComponent,
    UpdateJobComponent,
    DialogViewJobComponent,
    UserViewComponent,
    UserFormDialogComponent,
    UserFormComponent,
    UserGroupViewComponent,
    UserGroupDialogComponent,
    UserGroupFormComponent,
    DialogViewUniversityComponent,
    RestoreDialogComponent,
    UserPasswordFormComponent,
    ProjectComponent,
    AddProjectComponent,
    UpdateProjectComponent,
    ArchiveProjectDialogComponent,
    AddFormProjectComponent,
    FeatureAddJobDescriptionComponent,
    FeatureInterviewTemplateListComponent,
    FeatureFileManagerComponent,
    FeatureInterviewTemplateAddComponent,
    FeatureInterviewTemplateDetailComponent,
    AwSettingComponent,
    CompanyOldProfileComponent,
    FeatureListJobComponent,
    FeatureAddJobDescriptionPopupComponent,
    FeatureMailTemplateComponent,
    FeatureStatusCandidateComponent,
    FeatureSystemConfigComponent,
    FeatureUniversityComponent,
  ],
  imports: [
    CommonModule,
    MatCommonModule,
    SettingRoutingModule,
    MaterialModule,
    ReactiveFormsModule,
    FormsModule,
    SharedModule,
    QuillModule.forRoot({
      customModules: [
        {
          implementation: Signature,
          path: 'modules/signature',
        },
      ],
      modules: {
        keyboard: {
          bindings: [{ key: 'tab', handler: () => true }],
        },
        signature: true,
        toolbar: {
          container: toolbarOption,
          handlers: {
            signature: () => {},
          },
        },
      },
    }),
    ToastrModule.forRoot(),
    NgxMatSelectSearchModule,
    CoreModule,
    NgxFileDragDropModule,
    TextMaskModule,
    LineTruncationLibModule,
    AuthModule,
  ],
  providers: [
    MailconfigResolver,
    UniversityService,
    MailconfigService,
    MailtemplateService,
    UserGroupAdminService,
  ],
  exports: [
    AddFormProjectComponent,
    FeatureAddJobDescriptionComponent,
    FeatureAddJobDescriptionPopupComponent,
  ],
})
export class SettingModule {}
