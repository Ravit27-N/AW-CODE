import { ReminderListComponent } from './reminder-list';
import { AddReminderComponent } from './add-reminder';
import { EditReminderComponent } from './edit-reminder';

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ReminderListOldComponent} from './reminder-list-old';

const routes: Routes = [
  {
    path: '',
    component: ReminderListComponent,
    data: {
      breadcrumb: 'List remainder',
    },
  },
  {
    path: 'add',
    component: AddReminderComponent,
  },
  {
    path: 'add/:id/:type',
    component: AddReminderComponent,
  },
  {
    path: 'edit',
    component: EditReminderComponent,
  },
  {
    path: 'old',
    component: ReminderListOldComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RemindersRoutingModule { }
