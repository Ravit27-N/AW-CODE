import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InterviewListViewComponent } from './interview-list-view.component';
import {
  CandidateByIdResolverService,
  InterviewResolverService,
} from './interview-resolver.service';
import { InterviewCreateViewComponent } from './interview-create-view.component';
import { FeatureInterviewListComponent } from './feature-interview-list/feature-interview-list.component';
import { FeatureInterviewAddComponent } from './feature-interview-add/feature-interview-add.component';
import { FeatureInterviewUpdateComponent } from './feature-interview-update/feature-interview-update.component';
import { FeatureAddInterviewDeactivate } from './feature-interview-add/feature-interview-add.deactivate';

const routes: Routes = [
  {
    path: '',
    component: FeatureInterviewListComponent,
    data: {
      breadcrumb: 'List interviews',
    },
  },
  {
    path: 'list/:month/:status',
    component: InterviewListViewComponent,
  },
  {
    path: 'create',
    component: FeatureInterviewAddComponent,
    canDeactivate: [FeatureAddInterviewDeactivate],
    data: {
      breadcrumb: 'Create Interview',
    },
  },
  {
    path: 'update/:id',
    component: FeatureInterviewUpdateComponent,
    canDeactivate: [FeatureAddInterviewDeactivate],
    data: {
      breadcrumb: 'Update Interview',
    },
  },
  {
    path: 'create/old',
    component: InterviewCreateViewComponent,
  },
  {
    path: 'candidate',
    pathMatch: 'full',
    component: FeatureInterviewAddComponent,
    canDeactivate: [FeatureAddInterviewDeactivate],
  },
  {
    path: 'view/:id',
    component: InterviewCreateViewComponent,
    data: {
      editorMode: true,
    },
    resolve: {
      interview: InterviewResolverService,
    },
  },
  {
    path: 'old',
    component: InterviewListViewComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InterviewRoutingModule {}
