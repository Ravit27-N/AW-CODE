import { createAction, props } from "@ngrx/store";
import { ITabData } from './flow-tab-menu.reducer';


export const enqueTab = createAction(
  '[Flow deposit/tab enque]',
  props<ITabData>()
)


export const dequeTab = createAction(
  '[Flow deposit/tab deque]',
  props<ITabData>()
)

export const linkTab = createAction(
  '[Flow deposit/tab link]',
  props<ITabData>()
)

export const goBack = createAction(
  '[Flow deposit/tab go back]'
)

// Back page (from flow traceability).
export const goBackAcquisition = createAction(
  '[Flow deposit / tap to back to acquisition'
);

export const goBackToPreAnalysis = createAction(
  '[Flow deposit / tab go back to pre analysis]'
);

export const goBackToAnalysisResult = createAction(
  '[Flow deposit / tab go back to analysis result]'
);

export const goBackProductCriterial = createAction(
  '[Flow deposit / tab go back to product criterail]'
);

export const goToFinishedPage = createAction(
  '[Flow deposit / tab go back to finished deposit page]'
)

export const enqueRoute = (router: any, tab: ITabData) => enqueTab({ ...tab, link: router.url });
