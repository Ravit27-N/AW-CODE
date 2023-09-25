import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { InterviewList, InterviewService } from '../core';
import { formatDateWithoutTime, getMonthly } from '../shared';

@Injectable()
export class CalendarResolverService implements Resolve<InterviewList> {
  constructor(private interviewService: InterviewService) {}

  resolve(): Observable<any> {
    const { start, end } = getMonthly(new Date());

    return this.interviewService.getList(100, 1, {
      startDate: formatDateWithoutTime(start),
      endDate: formatDateWithoutTime(end),
    });
  }
}
