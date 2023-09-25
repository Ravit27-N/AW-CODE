import { SharedModule } from '../shared';
import { QuillModule } from 'ngx-quill';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../material';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivitiesRoutingModule } from './activities-routing.module';
import { ActivitiesListComponent } from './activities-list';
import { AddActivityComponent } from './add-activity';
import { EditActivityComponent } from './edit-activity/edit-activity.component';
import { ActivityDetailComponent } from './activity-detail';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { AuthModule } from '../auth';
import { CoreModule } from '../core/core.module';
import {
  FeatureActivityDetailComponent,
  FeatureActivityListComponent,
} from './feature-activity-list';

const activitiesComponent = [
  ActivitiesListComponent,
  AddActivityComponent,
  EditActivityComponent,
  ActivityDetailComponent,
];

@NgModule({
  declarations: [
    activitiesComponent,
    FeatureActivityListComponent,
    FeatureActivityDetailComponent,
  ],
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    ActivitiesRoutingModule,
    QuillModule,
    NgxMatSelectSearchModule,
    CoreModule,
    SharedModule,
    AuthModule,
  ],
  exports: [activitiesComponent],
})
export class ActivitiesModule {}
