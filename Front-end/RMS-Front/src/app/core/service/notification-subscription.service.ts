import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {environment} from '../../../environments/environment';

interface SubscriptionToken {
  deviceId?: string;
  token: string;
}

@Injectable()
export class NotificationSubscriptionService {

  constructor(private api: ApiService) { }

  subscriptToken(token: SubscriptionToken): Observable<any> {
    return this.api.put(`${environment.rmsContextPath}/user/firebase/token`, token);
  }
}
