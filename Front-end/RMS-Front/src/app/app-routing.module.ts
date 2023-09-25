import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Access, AccessGuardService, AuthGuardService } from './auth';
import { DefaultComponent } from './layouts/default';
import { WelcomeComponent } from './layouts/welcome';
import { FeatureCandidateListDeactivate } from './candidate/feature-candidate-list/feature-candidate-list.deactivate';
import { FeatureInterviewListDeactivate } from './interview/feature-interview-list/feature-interview-list.deactivate';
import { PageDemandListDeactivate } from './features/feature-demand/pages/page-demand-list/page-demand-list.deactivate';

const routes: Routes = [
  {
    path: 'admin',
    component: DefaultComponent,
    data: { breadcrumb: 'Dashboard' },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'setting',
        loadChildren: () =>
          import('./setting/setting.module').then((m) => m.SettingModule),
        data: {
          breadcrumb: 'Setting',
        },
      },
      {
        path: 'administration',
        loadChildren: () =>
          import('./feature-administration/feature-administration.module').then(
            (m) => m.FeatureAdministrationModule,
          ),
        data: {
          breadcrumb: 'Administration',
        },
      },
      {
        path: 'demand',
        data: { breadcrumb: 'Demands' },
        loadChildren: () =>
          import('./features/feature-demand/demand.module').then(
            (m) => m.DemandModule,
          ),
        canDeactivate: [PageDemandListDeactivate],
      },
      {
        path: 'candidate',
        loadChildren: () =>
          import('./candidate/candidate.module').then((m) => m.CandidateModule),
        canActivate: [AccessGuardService],
        canDeactivate: [FeatureCandidateListDeactivate],
        data: {
          perm: Access.candidate,
          breadcrumb: 'Candidates',
        },
      },
      {
        path: 'activities',
        loadChildren: () =>
          import('./activities/activities.module').then(
            (m) => m.ActivitiesModule,
          ),
        canActivate: [AccessGuardService],
        data: {
          perm: Access.activity,
          breadcrumb: 'Activity',
        },
      },
      {
        path: 'interview',
        loadChildren: () =>
          import('./interview/interview.module').then((m) => m.InterviewModule),
        canActivate: [AccessGuardService],
        canDeactivate: [FeatureInterviewListDeactivate],
        data: {
          perm: Access.interview,
          breadcrumb: 'Interviews',
        },
      },
      {
        path: 'reminders',
        loadChildren: () =>
          import('./reminders/reminders.module').then((m) => m.RemindersModule),
        canActivate: [AccessGuardService],
        data: { perm: Access.reminder, breadcrumb: 'Reminders' },
      },
      {
        path: 'calendar',
        loadChildren: () =>
          import('./calendar/calendar.module').then((m) => m.CalendarModule),
        data: {
          breadcrumb: 'Calendar',
        },
      },
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./feature-dashboard/dashboard.module').then(
            (m) => m.DemandModule,
          ),
      },
    ],
    canActivate: [AuthGuardService],
  },
  {
    path: '',
    redirectTo: '/welcome',
    pathMatch: 'full',
  },
  {
    path: 'welcome',
    component: WelcomeComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
