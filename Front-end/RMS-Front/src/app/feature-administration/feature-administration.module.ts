import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FeatureAdministrationRoutingModule } from './feature-administration-routing.module';
import { UserGroupAdminService } from '../core';
import { MaterialModule } from '../material';
import { CoreModule } from '../core/core.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../shared';
import { QuillModule } from 'ngx-quill';
import { SettingModule } from '../setting/setting.module';
import { AwAdministrationComponent } from './aw-administration';

@NgModule({
  declarations: [AwAdministrationComponent],
  imports: [
    CommonModule,
    CoreModule,
    FeatureAdministrationRoutingModule,
    ReactiveFormsModule,
    SharedModule,
    QuillModule,
    FormsModule,
    MaterialModule,
    SettingModule,
  ],
  providers: [UserGroupAdminService],
})
export class FeatureAdministrationModule {}
