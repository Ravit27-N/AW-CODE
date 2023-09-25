import { DemandRoutingModule } from './demand-routing.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../../material';
import { CoreModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared';
import { QuillModule } from 'ngx-quill';
import { PageDemandListComponent } from './pages/page-demand-list/page-demand-list.component';
import { PageDemandCreateComponent } from './pages/page-demand-create/page-demand-create.component';
import { PageDemandDetailsComponent } from './pages/page-demand-details/page-demand-details.component';
import { PageDemandEditComponent } from './pages/page-demand-edit/page-demand-edit.component';
import { ScopedComponentDemandCreateEditFormComponent } from './components/scoped-component-demand-create-edit-form/scoped-component-demand-create-edit-form.component';
import { SettingModule } from '../../setting/setting.module';

@NgModule({
  declarations: [
    PageDemandListComponent,
    PageDemandCreateComponent,
    PageDemandDetailsComponent,
    PageDemandEditComponent,
    ScopedComponentDemandCreateEditFormComponent,
  ],
  imports: [
    CommonModule,
    MaterialModule,
    CoreModule,
    DemandRoutingModule,
    ReactiveFormsModule,
    SharedModule,
    QuillModule,
    FormsModule,
    MaterialModule,
    SettingModule,
  ],
})
export class DemandModule {}
