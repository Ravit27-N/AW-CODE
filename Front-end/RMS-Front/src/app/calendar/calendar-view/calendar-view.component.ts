import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  CalendarOptions,
  EventClickArg,
  EventInput,
  FullCalendarComponent,
} from '@fullcalendar/angular';
import { throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Interview, InterviewList, InterviewService } from 'src/app/core';
import { dateInPast, formatDateWithoutTime } from '../../shared';

const transformEventColor = (date) =>
  dateInPast(new Date(date), new Date()) ? '#66ccff' : undefined;

@Component({
  selector: 'app-calendar-view',
  templateUrl: './calendar-view.component.html',
  styleUrls: ['./calendar-view.component.scss'],
})
export class CalendarViewComponent implements OnInit {
  calendarOptions: CalendarOptions;

  @ViewChild('calendar', { static: false }) calendar: FullCalendarComponent;

  constructor(
    private activedRoute: ActivatedRoute,
    private router: Router,
    private interviewService: InterviewService,
  ) {}

  ngOnInit(): void {
    this.calendarOptions = {
      firstDay: 1,
      showNonCurrentDates: false,
      initialView: 'dayGridMonth',
      eventClick: this.handleEventClick.bind(this),
      editable: true,
      eventStartEditable: true,
      eventDurationEditable: false,
      eventDisplay: 'list-item',
      dayMaxEvents: true,
      selectable: false,
      height: '75vh',
      dateClick: this.handleDateClick.bind(this),
      dayCellClassNames: ['x-calendar-cell'],
      eventTimeFormat: {
        hour: 'numeric',
        minute: '2-digit',
        meridiem: 'short',
      },
      headerToolbar: {
        start: 'prev next today',
        center: 'title',
        end: 'timeGridDay timeGridWeek dayGridMonth',
      },
      buttonText: {
        week: 'Week',
        day: 'Day',
        month: 'Month',
      },
      customButtons: {
        createInterview: {
          text: 'Create interview',
          click: () => {
            this.router.navigateByUrl('/admin/interview/create');
          },
        },
      },
      eventSources: [
        {
          events: (args, success, failure) => {
            // TODO: Need fix from api
            this.interviewService
              .getList(1000, 1, {
                startDate: formatDateWithoutTime(args.start),
                endDate: formatDateWithoutTime(args.end),
              })
              .pipe(map(this.mapRouteDataToEvent))
              .pipe(
                catchError((e) => {
                  failure(e);
                  return throwError(e);
                }),
              )
              .subscribe((eventData) => success(eventData));
          },
        },
      ],
      // eventContent: (args, createElement) => {
      //   return {
      //     html: `
      //     <div class="d-flex flex-wrap">
      //       <span class="cal-time-text">${args.timeText}</span>&nbsp;<span class="cal-title-text">${args.event.title}</span>
      //     </div>
      //     `
      //   }
      // }
    };
  }

  handleEventClick(args: EventClickArg): void {
    const interview = args.event.extendedProps as Interview;
    this.router.navigateByUrl(
      `/admin/candidate/candidateDetail/${interview.candidate.id}`,
    );
  }

  handleDateClick(): void {}

  private mapRouteDataToEvent(data: InterviewList): EventInput[] {
    return data.contents.map(
      (x) =>
        ({
          title: x.title,
          date: new Date(x.dateTime),
          extendedProps: { ...x },
          color: transformEventColor(x.dateTime),
        }) as EventInput,
    );
  }
}
