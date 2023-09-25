import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared';
import { MaterialModule } from '../material';
import { InterviewListViewComponent } from './interview-list-view.component';
import { InterviewRoutingModule } from './interview-routing.module';
import {
  CandidateByIdResolverService,
  InterviewResolverService,
} from './interview-resolver.service';
import { InterviewCreateViewComponent } from './interview-create-view.component';
import { InterviewFormComponent } from './interview-form.component';
import { QuillModule } from 'ngx-quill';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InterviewCardComponent } from './interview-card.component';
import { ResultCardComponent } from './result-card.component';
import { ResultFormComponent } from './result-form.component';
import {
  InterviewDialogComponent,
  InterviewResultDialogComponent,
  InterviewViewDialogComponent,
} from './dialog.component';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { InterviewViewComponent } from './interview-view.component';
import { AuthModule } from '../auth';
import { CoreModule } from '../core/core.module';
import { FeatureInterviewListComponent } from './feature-interview-list/feature-interview-list.component';
import { FeatureInterviewAddComponent } from './feature-interview-add/feature-interview-add.component';
import { FeatureInterviewUpdateComponent } from './feature-interview-update/feature-interview-update.component';
import { FeatureInterviewListDeactivate } from './feature-interview-list/feature-interview-list.deactivate';

@NgModule({
  declarations: [
    InterviewListViewComponent,
    InterviewCreateViewComponent,
    InterviewFormComponent,
    InterviewDialogComponent,
    InterviewViewDialogComponent,
    InterviewResultDialogComponent,
    InterviewCardComponent,
    ResultCardComponent,
    ResultFormComponent,
    InterviewViewComponent,
    FeatureInterviewListComponent,
    FeatureInterviewAddComponent,
    FeatureInterviewUpdateComponent,
  ],
  imports: [
    CommonModule,
    CoreModule,
    ReactiveFormsModule,
    FormsModule,
    InterviewRoutingModule,
    SharedModule,
    MaterialModule,
    QuillModule,
    NgxMatSelectSearchModule,
    AuthModule,
  ],
  providers: [
    InterviewResolverService,
    CandidateByIdResolverService,
    FeatureInterviewListDeactivate,
  ],
})
export class InterviewModule {}
