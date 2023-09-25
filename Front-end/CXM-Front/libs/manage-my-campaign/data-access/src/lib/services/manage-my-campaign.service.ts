import {sendMailForm} from '@cxm-smartflow/shared/data-access/model';
import { Observable, of } from 'rxjs';
import {CxmCampaignService} from '@cxm-smartflow/shared/data-access/api';
import { Injectable } from '@angular/core';
import { delay } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})

export class ManageMyCampaignService {

  constructor(
    private manageMyCampaignService: CxmCampaignService
  ) { }

  create(data?: sendMailForm): Observable<any>{
   return this.manageMyCampaignService.post(`/campaign/api/v1/campaigns/sendMail`, data);
    // return of([{id: ''}]);
  }

}
