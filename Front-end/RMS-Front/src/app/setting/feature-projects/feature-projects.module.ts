import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureProjectListComponent } from './feature-project-list/feature-project-list.component';
import { SharedModule } from '../../shared';
import { MaterialModule } from '../../material';
import { CoreModule } from '../../core/core.module';
import { FeatureProjectsRoutingModule } from './feature-projects-routing.module';
import {QuillModule} from "ngx-quill";

@NgModule({
  declarations: [FeatureProjectListComponent],
  imports: [
    CommonModule,
    MaterialModule,
    SharedModule,
    CoreModule,
    FeatureProjectsRoutingModule,
    QuillModule,
  ],
})
export class FeatureProjectsModule {}
