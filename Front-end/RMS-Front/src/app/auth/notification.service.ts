import { Injectable } from '@angular/core';
import 'firebase/messaging';
import { ReplaySubject } from 'rxjs';
import { UserService } from './user.service';
import { NotificationSubscriptionService } from '../core';
import {getMessaging, getToken, onMessage} from 'firebase/messaging';
import {environment} from '../../environments/environment';

@Injectable()
export class NotificationService {

  private notificatonSubject$ = new ReplaySubject(10);
  private messaging: any ={};

  public notification$ = this.notificatonSubject$.asObservable();

  constructor(private subscriptionService: NotificationSubscriptionService, private userService: UserService) {

  }

  init(): void {

    // firebase.initializeApp(environment.firebase);

    this.messaging = getMessaging();
    onMessage(this.messaging, (payload) => {
      this.notificatonSubject$.next(payload);
    });

    getToken(this.messaging,
      { vapidKey: environment.firebase.vapidKey}).then(
      (currentToken) => {
        if (currentToken) {
          console.log('Hurraaa!!! we got the token.....');
          console.log(currentToken);

          const user = this.userService.getCurrentUser();
          this.subscriptionService.subscriptToken({ token: currentToken, deviceId: user.preferred_username }).subscribe();
        } else {
          console.log('No registration token available. Request permission to generate one.');
        }
      }).catch((err) => {
      console.log('An error occurred while retrieving token. ', err);
    });
  }
}
