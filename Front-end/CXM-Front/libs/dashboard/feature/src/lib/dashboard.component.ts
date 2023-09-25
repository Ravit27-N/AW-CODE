import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';
import { Store } from '@ngrx/store';
import {  graphActions, graphSelector } from '@cxm-smartflow/dashboard/data-access'
import { Observable, Subscription, BehaviorSubject } from 'rxjs';
import { MatMenuTrigger } from '@angular/material/menu';
import { DateAdapter } from '@angular/material/core';
import { createCustomDaterange } from './default-ranges';
import * as moment from 'moment';
import { debounceTime } from 'rxjs/operators';
import { CanAccessibilityService, CanVisibilityService } from '@cxm-smartflow/shared/data-access/services';
@Component({
  selector: 'cxm-smartflow-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {

  private customDateranged: any;
  applyHightlightSubscription: Subscription;

  startDatelabel: string;
  endDatelable: string;

  refreshTime$: Observable<any>;
  graphFilter$: Observable<any>;

  hasActiveAccount$ = new BehaviorSubject<boolean>(false);

  @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger;

  mainMenuOpen(){
    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose(){
    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  oncalendarChanged($event: any) {
    this.trigger.closeMenu();
    this.store.dispatch(graphActions.filterGraphChanged({ ...$event }));
  }

  wrapInObject(data: any, name: string) {
    return Object.assign({ }, { [name]: data });
  }

  private shouldApplyHightlight(graphFitler: any) {
    if(graphFitler.option === this.customDateranged.length) {
      const { end, start } = graphFitler;
      this.startDatelabel = this.formatDate(start);
      this.endDatelable = this.formatDate(end);
    } else {
      const range = this.customDateranged[graphFitler.option];
      this.startDatelabel = this.formatDate(range.startDate);
      this.endDatelable = this.formatDate(range.endDate);
    }
  }

  formatDate(date: string): string {
    return moment(date).format('D MMM').replace('.', '');
  }

  ngOnInit(): void {
    this.refreshTime$ = this.store.select(graphSelector.selectGraphRefreshTime);
    this.graphFilter$ = this.store.select(graphSelector.selectGraphFilter);

    this.applyHightlightSubscription = this.graphFilter$.pipe(debounceTime(300))
      .subscribe(filter => this.shouldApplyHightlight(filter))

    this.store.dispatch(graphActions.fetchUserGraphFilter());
  }

  ngOnDestroy(): void {
    this.applyHightlightSubscription.unsubscribe();
  }

  constructor(private store: Store, private dateadapter: DateAdapter<Date>,
              private canVisible: CanVisibilityService,
              private canAccess: CanAccessibilityService) {
    this.hasActiveAccount$.next(canAccess.hasActiveAccount());
    this.customDateranged = createCustomDaterange(this.dateadapter);
    const localeSelected = localStorage.getItem('locale') || 'fr';
    moment.locale(localeSelected);
  }
}
