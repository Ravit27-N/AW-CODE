import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { tap, withLatestFrom } from 'rxjs/operators';
import * as tabActions from './profile-tab-menu.action';
import { getTabState } from './profile-tab-menu.selector';


@Injectable()
export class TabMenuEffect {

  goBackTab$ = createEffect(() => this.actions$.pipe(
    ofType(tabActions.goBack),
    withLatestFrom(this.store$.select(getTabState)),
    tap(([args, menu]) => {
      if(menu.current?.parent) {
        this.router.navigateByUrl(menu.current?.parent?.link);
        this.store$.dispatch(tabActions.dequeTab(menu.current?.parent));
      }
    }),
  ), { dispatch: false });

  constructor(private actions$: Actions, private store$: Store, private router: Router) {

  }

}
