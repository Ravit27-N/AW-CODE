import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeatureRoleRoutingModule } from './feature-role-routing.module';
import { FeatureRoleListDeactivate } from './feature-role-list/feature-role-list.deactivate';
import { FeatureRoleListComponent } from './feature-role-list/feature-role-list.component';
import { FeatureRoleCreateComponent } from './feature-role-create/feature-role-create.component';
import { FeatureRoleEditComponent } from './feature-role-edit/feature-role-edit.component';
import { FeatureRoleFormComponent } from './feature-role-form/feature-role-form.component';
import { MaterialModule } from '../../material';
import { CoreModule } from '../../core/core.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared';
import { QuillModule } from 'ngx-quill';
import { AwTreeChecklistComponent } from './aw-tree-checklist';

@NgModule({
  declarations: [
    FeatureRoleListComponent,
    FeatureRoleCreateComponent,
    FeatureRoleEditComponent,
    FeatureRoleFormComponent,
    AwTreeChecklistComponent,
  ],
  imports: [
    CommonModule,
    FeatureRoleRoutingModule,
    CommonModule,
    CoreModule,
    ReactiveFormsModule,
    SharedModule,
    QuillModule,
    FormsModule,
    MaterialModule,
  ],
  providers: [FeatureRoleListDeactivate],
})
export class FeatureRoleModule {}
