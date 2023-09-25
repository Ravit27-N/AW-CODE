import { Component, OnDestroy } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Event, NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router } from '@angular/router';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-router-loading',
  templateUrl: './router-loading.component.html',
  styleUrls: ['./router-loading.component.scss']
})
export class RouterLoadingComponent implements OnDestroy {
  loading$ = new BehaviorSubject(true);

  constructor(private router: Router, private confirmation: ConfirmationMessageService,
              private translate: TranslateService) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.router.events.subscribe((event: Event) => {
      switch (true) {
        case event instanceof NavigationStart: {
          this.loading$.next(true);
          break;
        }
        case event instanceof NavigationEnd:
          this.loading$.next(false);
          break;
        case event instanceof NavigationCancel:
          this.loading$.next(false);
          break;
        case event instanceof NavigationError: {
          const { url } = event as any;
          const feature = (url as string)?.split('/')?.filter(value => value !== '')
            ?.filter((value, index) => index <= 1)
            .join(' > ');
          this.loading$.next(false);
          this.translate.get('router_loading').toPromise().then(message => {
            this.confirmation.showConfirmationPopup({
              type: 'Secondary',
              title: message?.title,
              message: message?.message,
              importanceWorld: feature,
              cancelButton: message?.cancel,
              confirmButton: message?.confirm
            });
          });
          break;
        }
        default:
          break;
      }
    });
  }

  ngOnDestroy(): void {
    this.loading$?.unsubscribe();
  }
}
