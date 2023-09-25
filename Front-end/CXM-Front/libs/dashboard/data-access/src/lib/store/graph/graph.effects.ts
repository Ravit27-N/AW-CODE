import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { catchError, exhaustMap, map, mergeMap, switchMap, tap, withLatestFrom, } from 'rxjs/operators';
import { graphSelector } from '.';
import { IGraphCannelEnvoyResult, IGraphDepositModeResult } from '../../models';
import { AnalysticService } from '../../services/graph.service';
import * as graphActions from './graph.actions';
import { Router } from '@angular/router';
import { formatDateRequest, } from '@cxm-smartflow/flow-traceability/data-access';
import { DatePipe } from '@angular/common';
import { formatDateToRequest } from "@cxm-smartflow/shared/utils";

const sortByKeyName = (array: any[]) => {
  return array.sort((a: any, b: any) => b.key.localeCompare(a.key));
};

@Injectable()
export class DashboardGraphEffect {

  fetchGraphlEffect$ = createEffect(() => this.actions.pipe(
    ofType(graphActions.fetchGraph),
    mergeMap(args => {
      return [
        graphActions.fetchGraphChannel(),
        graphActions.fetchGraphDepositMode(),
        graphActions.fetchGraphFlowTracking(),
        graphActions.fetchGraphEvolution()
      ]
    })
  ));

  fetchGraphChannelEffect$ = createEffect(() => this.actions.pipe(
    ofType(graphActions.fetchGraphChannel),
    withLatestFrom(this.translate.get('dashboard'), this.store.select(graphSelector.selectGraphDashboardStatesRequestedAt)),
    exhaustMap(([args, message, requestedAt]) => this.graphService.fetchGraphChannel({ requestedAt: requestedAt }).pipe(
      map(res => graphActions.fetchGraphChannelSuccess({ data: this.mapKeyToNameWithTranslate(res, message) })),
      catchError(error => [graphActions.fetchGraphChannelFail({ error })])
    ))
  ))


  fetchGraphDepositMode$ = createEffect(() => this.actions.pipe(
    ofType(graphActions.fetchGraphDepositMode),
    withLatestFrom(this.translate.get("dashboard"), this.store.select(graphSelector.selectGraphFilter), this.store.select(graphSelector.selectGraphDashboardStatesRequestedAt)),
    exhaustMap(([args, message, filter, requestedAt]) => {
      return this.graphService.fetchGraphDepositMode({ requestedAt: requestedAt }).pipe(
        map(res => graphActions.fetchGraphDepositModeSuccess({ data: this.mapKeyToNameWithTranslate(sortByKeyName(res), message) })),
        catchError((error) => [graphActions.fetchGraphDepositModeFail({ error })])
      )
    })
  ));

  fetchGraphFlowTracking$ = createEffect(() => this.actions.pipe(
    ofType(graphActions.fetchGraphFlowTracking),
    withLatestFrom(this.store.select(graphSelector.selectGraphDashboardStatesRequestedAt)),
    exhaustMap(([args, requestedAt]) => this.graphService.fetchGraphFlowTracking({ requestedAt: requestedAt }).pipe(
      map(res => graphActions.fetchGraphFlowtrackingSuccess({ data: res })),
      catchError((error) => [graphActions.fetchGraphFlowtrackingFail({ error })])
    ))
  ))


  fetchGraphEvolution$ = createEffect(() => this.actions.pipe(
    ofType(graphActions.fetchGraphEvolution),
    withLatestFrom(this.store.select(graphSelector.selectGraphDashboardStatesRequestedAt)),
    exhaustMap(([args, requestedAt]) => this.graphService.fetchGraphEvolution({ requestedAt: requestedAt }).pipe(
      map(res =>{
        const result = Array.from(res).map(item => {
          const series = Array.from(item.data).map((x: any) => ({ name: x.date, value: x.value }));
          return { name: item.channel, series }
        })
        return  graphActions.fetchGraphEvolutionSuccess({ data: { result } })
      }),
      catchError((error) => [graphActions.fetchGraphEvolutionFail({ error })])
    ))
  ) )


  filterGraphChangedEffect$ = createEffect(() => this.actions.pipe(
    ofType(graphActions.filterGraphChanged),
    // switchMap(args => {
    //   return [graphActions.fetchGraph()]
    // })
    exhaustMap((args) => this.graphService.updateUserFilter({ selectDateType: args.option, customEndDate: this.toRequestDateFormat(this.endOfDate(args.end)), customStartDate: this.toRequestDateFormat(this.startOfDate(args.start)) }).pipe(
      map(res => graphActions.setUserGraphFilter({filter: res})),
      catchError((err) => {
        console.error('fail to update current user filter', err)
        return [graphActions.graphNonceAction()]
      })
    ))
  ))

  fetchUserGraphFilterEffect$ = createEffect(() => this.actions.pipe(
    ofType(graphActions.fetchUserGraphFilter),
    exhaustMap(args => this.graphService.fetchUserFilter().pipe(
      map(res => graphActions.fetchUserGraphFilterSuccess({filter: res}))
    ))
  ))

  fetchUserGraphUpdatedEffect$ = createEffect(() => this.actions.pipe(
    ofType(graphActions.setUserGraphFilter),
    switchMap(args => [graphActions.fetchGraph()])
  ))

  private mapKeyToNameWithTranslate(array: any[], message: any): IGraphDepositModeResult | IGraphCannelEnvoyResult {
    return {
      result: array.map(x => ({ name: message[this.tkey(x.key)], value: x.value }))
    };
  }

  private tkey(key: string) {
    return key.replace(new RegExp(/\./gm), '_') ?? key;
  }

  private toRequestDateFormat(date: Date) {
    return formatDateToRequest(date) || '';
  }

  private startOfDate(date: Date) {
    // date.setHours(0, 0, 0, 0);
    return date;
  }

  private endOfDate(date: Date) {
    // date.setHours(23, 59, 59, 999);
    return date;
  }

  private formatDateToRequest = (date: Date | string) => new DatePipe('en-US').transform(date, formatDateRequest, 'short');

  constructor(private actions: Actions, private graphService: AnalysticService, private translate: TranslateService,
    private store: Store, private _router: Router,
    ) { }
}
