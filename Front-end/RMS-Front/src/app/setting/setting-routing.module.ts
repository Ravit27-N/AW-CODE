import { ViewJobComponent } from './job';
import { ViewstatusinterviewComponent } from './statusInterview';
import {
  AddmailstatuschangeComponent,
  MailstatuschangeComponent,
  UpdatemailstatuschangeComponent,
} from './MailStatusChange';
import { StatuecandidateComponent } from './statuecandidate';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MailTemplateComponent } from './mail-template';
import { SystemConfigComponent } from './mailconfig';
import { CompanyprofileComponent } from './feature-company-profile';
import { ViewUniversityComponent } from './university';
import {
  FeatureViewComponent,
  RoleViewComponent,
  UserViewComponent,
} from './administration';
import { Access, AccessGuardService } from '../auth';
import { ProjectComponent } from './project';
import { FeatureFileManagerComponent } from './feature-file-manager/feature-file-manager.component';
import { AwSettingComponent } from './aw-setting';
import { CompanyOldProfileComponent } from './company-old-profile';
import { FeatureInterviewTemplateListComponent } from './feature-interview-template/feature-interview-template-list';
import { FeatureInterviewTemplateListDeactivate } from './feature-interview-template/feature-interview-template-list/feature-interview-template-list.deactivate';
import { FeatureListJobComponent } from './job/feature-list-job';
import { FeatureMailTemplateComponent } from './feature-mail-template/feature-mail-template.component';
import { FeatureStatusCandidateComponent } from './feature-status-candidate/feature-status-candidate.component';
import { FeatureSystemConfigComponent } from './feature-system-config/feature-system-config.component';
import { FeatureUniversityComponent } from './feature-university/feature-university.component';

const routes: Routes = [
  {
    path: '',
    component: AwSettingComponent,
  },
  {
    path: 'statuscandidate/old',
    component: StatuecandidateComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.candidate },
  },
  {
    path: 'statuscandidate',
    component: FeatureStatusCandidateComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.candidate, breadcrumb: 'Status candidate' },
  },
  {
    path: 'system-config/old',
    component: SystemConfigComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.system },
  },
  {
    path: 'system-config',
    component: FeatureSystemConfigComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.system, breadcrumb: 'System config' },
  },
  {
    path: 'mailtemplate/old',
    component: MailTemplateComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.template },
  },
  {
    path: 'mailtemplate',
    component: FeatureMailTemplateComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.template, breadcrumb: 'Mail template' },
  },
  {
    path: 'mailconfiguration/old',
    component: MailstatuschangeComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.mail },
  },
  {
    path: 'mail-configuration',
    canActivate: [AccessGuardService],
    data: { perm: Access.mail, breadcrumb: 'Mail configuration' },
    loadChildren: () =>
      import('./feature-mail-config/feature-mail-config.module').then(
        (m) => m.FeatureMailConfigModule,
      ),
  },
  {
    path: 'mailconfiguration/add',
    component: AddmailstatuschangeComponent,
  },
  {
    path: 'mailconfiguration/update/:id',
    component: UpdatemailstatuschangeComponent,
  },
  {
    path: 'feature-company-profile',
    component: CompanyprofileComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.company, breadcrumb: 'Company profile' },
  },
  {
    path: 'feature-company-profile/old',
    component: CompanyOldProfileComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.company },
  },
  {
    path: 'interviewtemplate',
    component: FeatureInterviewTemplateListComponent,
    canActivate: [AccessGuardService],
    canDeactivate: [FeatureInterviewTemplateListDeactivate],
    data: {
      perm: Access.interview,
      breadcrumb: 'List interview template',
    },
  },
  {
    path: 'interviewtemplate/old',
    component: ViewstatusinterviewComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.interview },
  },
  {
    path: 'university/old',
    component: ViewUniversityComponent,
  },
  {
    path: 'university',
    component: FeatureUniversityComponent,
    data: { breadcrumb: 'University' },
  },
  {
    path: 'project/old',
    component: ProjectComponent,
  },
  {
    path: 'projects',
    data: {
      breadcrumb: 'Projects',
    },
    loadChildren: () =>
      import('./feature-projects/feature-projects.module').then(
        (m) => m.FeatureProjectsModule,
      ),
  },
  {
    path: 'file-manager',
    component: FeatureFileManagerComponent,
    data: {
      breadcrumb: 'Advance File manager',
    },
  },
  {
    path: 'manage/users/old',
    component: UserViewComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.user },
  },
  {
    path: 'manage/roles',
    component: RoleViewComponent,
    canActivate: [AccessGuardService],
    data: { perm: Access.user },
  },
  {
    path: 'manage/features',
    component: FeatureViewComponent,
  },
  {
    path: 'job',
    component: FeatureListJobComponent,
    data: { perm: Access.user, breadcrumb: 'List Job Description' },
  },
  {
    path: 'job/old',
    component: ViewJobComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SettingRoutingModule {}
