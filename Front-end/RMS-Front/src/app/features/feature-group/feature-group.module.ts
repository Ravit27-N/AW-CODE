import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../../material';
import { CoreModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared';
import { PageGroupCreateComponent } from './pages/page-group-create/page-group-create.component';
import { PageGroupEditComponent } from './pages/page-group-edit/page-group-edit.component';
import { PageGroupDetailsComponent } from './pages/page-group-details/page-group-details.component';
import { PageGroupListComponent } from './pages/page-group-list/page-group-list.component';
import { FeatureGroupRoutingModule } from './feature-group-routing.module';

@NgModule({
  declarations: [
    PageGroupCreateComponent,
    PageGroupEditComponent,
    PageGroupDetailsComponent,
    PageGroupListComponent,
  ],
  imports: [
    CommonModule,
    CommonModule,
    MaterialModule,
    CoreModule,
    ReactiveFormsModule,
    SharedModule,
    FormsModule,
    MaterialModule,
    FeatureGroupRoutingModule,
  ],
})
export class FeatureGroupModule {}
