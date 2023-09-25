import { SharedModule } from '../shared';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { QuillModule } from 'ngx-quill';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../material';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RemindersRoutingModule } from './reminders-routing.module';
import { ReminderListComponent } from './reminder-list';
import { AddReminderComponent } from './add-reminder';
import { EditReminderComponent } from './edit-reminder';
import { ReminderDetailComponent } from './reminder-detail';
import { DeleteReminderComponent } from './delete-reminder';
import { AuthModule } from '../auth';
import {CoreModule} from '../core/core.module';
import { ReminderListOldComponent } from './reminder-list-old/reminder-list-old.component';

const reminderComponent = [
  ReminderListComponent,
  AddReminderComponent,
  EditReminderComponent,
  ReminderDetailComponent,
  DeleteReminderComponent,
  ReminderListOldComponent,
];

@NgModule({
  declarations: [reminderComponent],
  imports: [
    CommonModule,
    RemindersRoutingModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    QuillModule,
    NgxMatSelectSearchModule,
    CoreModule,
    SharedModule,
    AuthModule
  ],
  exports: [
    reminderComponent
  ],
  providers: []
})
export class RemindersModule { }
