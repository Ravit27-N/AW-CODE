import { CandidateListComponent } from './candidate-list';
import { CandidateDetailResolverService } from './candidate-detail-resolver.service';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../material';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateCandidateComponent } from './create-candidate';
import { CandiateRoutingModule } from './candiate-routing.module';
import { SharedModule } from '../shared';
import { QuillModule } from 'ngx-quill';
import { EditCandidateComponent } from './edit-candidate';
import { CandidateDetailComponent } from './candidate-detail';
import { DeleteCandidateComponent } from './delete-candidate';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { ComfirmDialogComponent } from './comfirm-dialog';
import { CandidateReportComponent } from './candidate-report';
import { DialogAddUniversityComponent } from './dialog-add-university';
import { ArchiveDialogComponent } from './archive-dialog';
import { RemoveFileDialogComponent } from './remove-file-dialog';
import { AuthModule } from '../auth';
import { CandidateAdvanceReportComponent } from './candidate-advance-report';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { FeatureCandidateListComponent } from './feature-candidate-list';
import { CoreModule } from '../core/core.module';
import { FeatureCandidateDetailComponent } from './feature-candidate-detail/feature-candidate-detail.component';
import { FeatureCandidateActivityComponent } from './feature-candidate-activity/feature-candidate-activity.component';
import { FeatureCandidateInterviewComponent } from './feature-candidate-interview/feature-candidate-interview.component';
import { FeatureCandidateProfileDetailComponent } from './feature-candidate-profile-detail/feature-candidate-profile-detail.component';
import { CandidateReportOldComponent } from './candidate-report-old/candidate-report-old.component';
import { FeatureAddCandidateComponent } from './feature-add-candidate/feature-add-candidate.component';
import { AddCandidateStepOneComponent } from './feature-add-candidate/add-candidate-step-one/add-candidate-step-one.component';
import { AddCandidateStepTwoComponent } from './feature-add-candidate/add-candidate-step-two/add-candidate-step-two.component';
import { AddCandidateStepThreeComponent } from './feature-add-candidate/add-candidate-step-three/add-candidate-step-three.component';
import { AddCandidateStepFourthComponent } from './feature-add-candidate/add-candidate-step-fourth/add-candidate-step-fourth.component';
import { AddCandidateStepFifthComponent } from './feature-add-candidate/add-candidate-step-fifth/add-candidate-step-fifth.component';
import { FeatureCandidateListDeactivate } from './feature-candidate-list/feature-candidate-list.deactivate';
import { TableReportStaffComponent } from './candidate-advance-report/table-report-staff';
import { TableReportInternComponent } from './candidate-advance-report/table-report-intern';
import { TableReportFollowingUpComponent } from './candidate-advance-report/table-report-following-up/table-report-following-up.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { FeatureAddCandidateDeactivate } from './feature-add-candidate/feature-add-candidate.deactivate';
import { FeatureCandidateInformationDetailComponent } from './feature-candidate-imformation-detail/feature-candidate-information-detail.component';
import { CandidateExperienceComponent } from './feature-candidate-detail/candidate-experience/candidate-experience.component';
import { CandidateEducationComponent } from './feature-candidate-detail/candidate-education/candidate-education.component';

const candidateComponent = [
  CandidateListComponent,
  CreateCandidateComponent,
  EditCandidateComponent,
  CandidateDetailComponent,
  DeleteCandidateComponent,
  CandidateReportComponent,
  ComfirmDialogComponent,
  DialogAddUniversityComponent,
  ArchiveDialogComponent,
  RemoveFileDialogComponent,
  CandidateAdvanceReportComponent,
];

@NgModule({
  declarations: [
    candidateComponent,
    FeatureCandidateListComponent,
    FeatureCandidateDetailComponent,
    FeatureCandidateActivityComponent,
    FeatureCandidateInterviewComponent,
    FeatureCandidateProfileDetailComponent,
    CandidateReportOldComponent,
    FeatureAddCandidateComponent,
    AddCandidateStepOneComponent,
    AddCandidateStepTwoComponent,
    AddCandidateStepThreeComponent,
    AddCandidateStepFourthComponent,
    AddCandidateStepFifthComponent,
    TableReportStaffComponent,
    TableReportInternComponent,
    TableReportFollowingUpComponent,
    FeatureCandidateInformationDetailComponent,
    CandidateExperienceComponent,
    CandidateEducationComponent,
  ],

  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    CandiateRoutingModule,
    SharedModule,
    QuillModule,
    NgxMatSelectSearchModule,
    CoreModule,
    AuthModule,
    MatTableModule,
    MatSortModule,
    DragDropModule,
  ],

  exports: [candidateComponent],

  providers: [
    CandidateDetailResolverService,
    FeatureCandidateListDeactivate,
    FeatureAddCandidateDeactivate,
  ],
})
export class CandidateModule {}
