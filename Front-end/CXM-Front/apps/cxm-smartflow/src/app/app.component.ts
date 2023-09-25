
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { APP_SETTINGS, IAppSettings } from '@cxm-smartflow/shared/app-config';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';

import { buildRemoteRouting } from '../config/remote.config';
import { APP_ROUTES } from './app.route';
import * as buildVersion from '../build-version.json';


@Component({
  selector: 'cxm-smartflow-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnDestroy, OnInit {

  subscription: Subscription ;
  loaded = false;


  async ngOnInit() {
    if (!this.loaded) {
      this.loadRemoteRouting();
      this.loaded = true;
    }
  }


  async loadRemoteRouting() {
    const { apps } = this.settings;
    const lazyRoutes = buildRemoteRouting(apps, buildVersion);
    APP_ROUTES[0].children?.push(...lazyRoutes);
    this.router.resetConfig(APP_ROUTES);
  }

  constructor(private titleService: Title, private translate: TranslateService,
    private router: Router,
    @Inject(APP_SETTINGS) private settings: IAppSettings) {

    this.translate.use(localStorage.getItem('locale') || 'fr' );

    this.titleService.setTitle('Digital Experience by Tessi');
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

}
