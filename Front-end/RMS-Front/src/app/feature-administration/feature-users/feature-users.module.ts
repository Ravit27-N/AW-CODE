import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FeatureUsersRoutingModule } from './feature-users-routing.module';
import { FeatureUserAddComponent } from './feature-user-add';
import { FeatureUserListComponent } from './feature-user-list';
import { CoreModule } from '../../core/core.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared';
import { QuillModule } from 'ngx-quill';
import { MaterialModule } from '../../material';

@NgModule({
  declarations: [FeatureUserAddComponent, FeatureUserListComponent],
  imports: [
    CommonModule,
    CoreModule,
    ReactiveFormsModule,
    SharedModule,
    QuillModule,
    FormsModule,
    MaterialModule,
    FeatureUsersRoutingModule,
  ],
})
export class FeatureUsersModule {}
