import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CalendarViewComponent } from './calendar-view/calendar-view.component';

const routes: Routes = [
  {
    path: '',
    component: CalendarViewComponent,
    data: {
      breadcrumb: 'List calendar',
    },
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CalendarRoutingModule {

}
