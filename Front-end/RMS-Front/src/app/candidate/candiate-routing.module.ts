import { CandidateListComponent } from './candidate-list';
import { CandidateDetailResolverService } from './candidate-detail-resolver.service';
import { CandidateReportComponent } from './candidate-report';
import { EditCandidateComponent } from './edit-candidate';
import { CreateCandidateComponent } from './create-candidate';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { Access, AccessGuardService } from '../auth';
import { CandidateAdvanceReportComponent } from './candidate-advance-report';
import { FeatureCandidateListComponent } from './feature-candidate-list';
import { FeatureCandidateDetailComponent } from './feature-candidate-detail/feature-candidate-detail.component';
import { CandidateReportOldComponent } from './candidate-report-old/candidate-report-old.component';
import { FeatureAddCandidateComponent } from './feature-add-candidate/feature-add-candidate.component';
import { FeatureAddCandidateDeactivate } from './feature-add-candidate/feature-add-candidate.deactivate';

const routes: Routes = [
  {
    path: '',
    component: FeatureCandidateListComponent,
    canActivate: [AccessGuardService],
    data: {
      perm: Access.candidate,
      breadcrumb: 'List candidates',
    },
  },
  {
    path: 'old',
    component: CandidateListComponent,
  },
  {
    path: 'list/:gender/:status',
    component: CandidateListComponent,
  },
  {
    path: 'add/old',
    component: CreateCandidateComponent,
  },
  {
    path: 'add',
    component: FeatureAddCandidateComponent,
    data: {
      breadcrumb: 'Add candidate',
    },
    canDeactivate: [FeatureAddCandidateDeactivate],
  },
  {
    path: 'editCandidate/:id',
    component: FeatureAddCandidateComponent,
    canDeactivate: [FeatureAddCandidateDeactivate],
  },
  {
    path: 'editCandidate/old/:id',
    component: EditCandidateComponent,
  },
  {
    path: 'candidateDetail/:id',
    component: FeatureCandidateDetailComponent,
    resolve: {
      data: CandidateDetailResolverService,
    },
  },
  {
    path: 'report',
    component: CandidateReportComponent,
    data: {
      perm: Access.candidate,
      breadcrumb: 'Candidate report',
    },
  },
  {
    path: 'report/old',
    component: CandidateReportOldComponent,
  },
  {
    path: 'advance-report',
    component: CandidateAdvanceReportComponent,
    data: {
      perm: Access.candidate,
      breadcrumb: 'Candidate advance report',
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CandiateRoutingModule {}
