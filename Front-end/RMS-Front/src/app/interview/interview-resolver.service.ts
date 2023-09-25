import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { Observable, EMPTY } from 'rxjs';
import { CandidateFormModel, CandidateService, Interview, InterviewService } from '../core/';

@Injectable()
export class CandidateByIdResolverService implements Resolve<any> {

  constructor(private candidateService: CandidateService) { }

  resolve(route: ActivatedRouteSnapshot): Observable<CandidateFormModel> {
    if (route.paramMap.has('id')) {
      return this.candidateService.getById(Number.parseInt(route.paramMap.get('id'), 10));
    } else {
      return EMPTY;
    }
  }
}


@Injectable()
export class InterviewResolverService implements Resolve<any> {
  constructor(private interviewService: InterviewService) { }

  resolve(route: ActivatedRouteSnapshot): Observable<Interview> {
    if (route.paramMap.has('id')) {
      return this.interviewService.getById(Number.parseInt(route.paramMap.get('id'), 10));
    }
  }
}
