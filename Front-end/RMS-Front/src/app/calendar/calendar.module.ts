import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FullCalendarModule } from '@fullcalendar/angular';
import dayGridPlugin from '@fullcalendar/daygrid'; // a plugin
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction'; // a plugin
import { CalendarRoutingModule } from './calendar-routing.module';
import { CalendarViewComponent } from './calendar-view/calendar-view.component';
import { CalendarResolverService } from './calendar-resolver.service';
import { CoreModule } from '../core/core.module';
import { SharedModule } from '../shared';
import {MaterialModule} from "../material";

FullCalendarModule.registerPlugins([
  // register FullCalendar plugins
  dayGridPlugin,
  timeGridPlugin,
  interactionPlugin,
]);

@NgModule({
  declarations: [CalendarViewComponent],
  imports: [
    CommonModule,
    CoreModule,
    CalendarRoutingModule,
    FullCalendarModule,
    SharedModule,
    MaterialModule,
  ],
  providers: [CalendarResolverService],
})
export class CalendarModule {}
