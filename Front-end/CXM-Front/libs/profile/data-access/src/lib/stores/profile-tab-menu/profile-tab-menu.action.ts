import { createAction, props } from "@ngrx/store";
import { ITabData } from './profile-tab-menu.reducer';


export const enqueTab = createAction(
  '[Flow profile/tab enque]',
  props<ITabData>()
)


export const dequeTab = createAction(
  '[Flow profile/tab deque]',
  props<ITabData>()
)

export const linkTab = createAction(
  '[Flow profile/tab link]',
  props<ITabData>()
)

export const goBack = createAction(
  '[Flow profile/tab go back]'
)


export const enqueRoute = (router: any, tab: ITabData) => enqueTab({ ...tab, link: router.url });
