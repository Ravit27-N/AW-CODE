import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as fromDirectoryFeedAction from './list-directory-feed-table.action';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { Params } from '@cxm-smartflow/shared/data-access/model';
import { of } from 'rxjs';
import { DirectoryFeedService } from '../../services';
import { Router } from '@angular/router';
import { directoryFeedTabNav } from '@cxm-smartflow/directory-feed/ui/feature-directory-feed-navigator';

@Injectable({
  providedIn: 'root'
})

export class ListDirectoryFeedTableEffect {
  constructor(
    private action$: Actions,
    private directoryFeedService: DirectoryFeedService,
    private router: Router
  ) {
  }

  fromDirectoryFeedAction$ = createEffect(() =>
    this.action$.pipe(
      ofType(fromDirectoryFeedAction.loadDirectoryFeedList),
      switchMap((arg) =>
        this.directoryFeedService.getDirectoryFeedList(arg.params as Params).pipe(
          map((response) => {
            return fromDirectoryFeedAction.loadDirectoryFeedListSuccess({
              directoryFeedList: response
            });
          }),
          catchError((error) =>
            of(fromDirectoryFeedAction.loadDirectoryFeedListFail({ errors: error }))
          ))))
  );

  refreshDirectoryFeedList$ = createEffect(() => this.action$.pipe(
    ofType(fromDirectoryFeedAction.refreshDirectoryFeedList),
    switchMap(args => [fromDirectoryFeedAction.loadDirectoryFeedList({params: args.params})])
  ))

  navigateToFeedEffect$ = createEffect(() => this.action$.pipe(
    ofType(fromDirectoryFeedAction.navigateToFeed),
    tap(args => {
      const {feedingBy, displayName, createdBy} = args.row;
      this.router.navigate([`${directoryFeedTabNav.detail.link}/${args.row.id}`], {queryParams: { feedingBy, displayName, createdBy }});
    })
  ), { dispatch: false })

}
