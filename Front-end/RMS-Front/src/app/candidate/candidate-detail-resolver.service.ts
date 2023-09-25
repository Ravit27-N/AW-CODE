import { CandidateDetail } from '../core';
import { EMPTY, Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve} from '@angular/router';
import { CandidateService } from '../core';
@Injectable()
export class CandidateDetailResolverService implements Resolve<any>{

  constructor(
    private candidateService: CandidateService
  ) {
  }
  resolve(route: ActivatedRouteSnapshot): Observable<CandidateDetail>{
    if(route.paramMap.has('id')){
      // eslint-disable-next-line radix
      return this.candidateService.getCandidateDetail(Number.parseInt(route.paramMap.get('id')));
    }else{
      return EMPTY;
    }
  }

}
