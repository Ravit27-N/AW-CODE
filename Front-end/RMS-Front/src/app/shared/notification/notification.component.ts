import { CdkConnectedOverlay } from '@angular/cdk/overlay';
import { Component, OnInit, ViewChild } from '@angular/core';
import { BehaviorSubject, merge, Observable } from 'rxjs';
import { map, mapTo } from 'rxjs/operators';
import { NotificationService } from '../../auth';
import { getAssetPrefix } from '../utils';

interface INotificationMessage {
  title?: string;
  body?: string;
  unread: boolean;
}

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss']
})
export class NotificationComponent implements OnInit {

  // messages: Array<INotificationMessage>;
  messages$ = new BehaviorSubject<Array<INotificationMessage>>([]);
  unreadLength$: Observable<number>;
  showNotice$: Observable<boolean>;

  private isOpenNotice$ = new BehaviorSubject<boolean>(false);
  private isHiddenPanel$: Observable<boolean>;

  @ViewChild(CdkConnectedOverlay, { static: true })
  private connectedOverlay: CdkConnectedOverlay;
  private arrEach: any;

  constructor(private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.unreadLength$ = this.messages$.pipe(
      map(value => value.filter(x => x.unread).length),
    );

    this.connectedOverlay.backdropClass = 'zero-opacity';
    this.isHiddenPanel$ = this.connectedOverlay.backdropClick.pipe(mapTo(false));

    this.showNotice$ = merge(this.isOpenNotice$, this.isHiddenPanel$);

    this.notificationService.notification$
      .subscribe((x: any) => {
        const arr = this.messages$.value;
        arr.unshift({ ...x.notification, unread: true } as INotificationMessage);
        this.messages$.next(arr);
        for(const arrItem of arr){
          console.log(arrItem.title);
          this.showCustomNotification(arrItem);
        }
        console.log(arr);
      });
    this.notificationService.init();
  }

  noticeClick(): void {
    this.isOpenNotice$.next(true);
  }

  readNotice(item: INotificationMessage): void {
    item.unread = false;
    this.messages$.next(this.messages$.value);
  }

  markAsRead(event: Event): void {
    event.preventDefault();
    const arr = this.messages$.value;
    this.messages$.next(arr.map( x => ({ ...x, unread: false })));
  }

  showCustomNotification(payload: any){
    const currentDate = new Date().toLocaleString('km');
    const title =   payload.title;
    const finalBody = `Date-Time: (${currentDate})` + '\nReminder: ' + this.replaceDiv(payload.body);
    const options = {
      body: finalBody,
      icon: `${getAssetPrefix()}/assets/img/3.png`,
      badge: `${getAssetPrefix()}/assets/img/3.png`,
      image: `${getAssetPrefix()}/assets/img/3.png`,
    };
    const notify: Notification  = new Notification(title, options);

    notify.onclick = events => {
      events.preventDefault();
      window.location.href = 'https://www.google.com';
    };
  }
  replaceDiv(body: string): string{
    if(body && typeof body === 'string'){
     body = body.replace(/&(#[0-9]+|[a-z]+);|(&#(\d+));|<\/?\w(?:[^"'>]|"[^"]*"|'[^']*')*>|\/script|script/gi, '');
    }
    return body;
  }
}
