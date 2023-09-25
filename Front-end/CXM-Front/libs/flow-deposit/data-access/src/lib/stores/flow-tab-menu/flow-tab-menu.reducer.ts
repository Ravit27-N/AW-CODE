import { createReducer, on } from "@ngrx/store";
import * as tabMenuAction from './flow-tab-menu.action';

export interface ITabData { name: string; id: string; active: boolean, link: string, parent?: ITabData };

export const FlowTabs = {
  acquisition: { id: 'acquisition', name: 'flow.deposit.acquisition.title.pageTitle', active: false, link: "acquisition" },
  preAnalysis: { id: 'pre-analysis', name: 'flow.deposit.preAnalysis.title', active: false, link: 'pre-analysis' },
  analysisResult: { id: 'analysis-result', name: 'flow.deposit.analysisResult.title', active: false, link: 'analysis-result' },
  productionCriteria: { id: 'production-criteria', name: 'flow.deposit.productionCriteria.title.pageTitle', active: false, link: 'production-criteria' },
  fin: { id: 'finished', name: 'flow.deposit.finished.menuTitle', active: false, link: 'finished'  }
}


export const featureFlowTabMenuKey = 'feature-flow-deposit-menu';

export interface FlowTabmenuState {
  lasttab?: ITabData,
  current?: ITabData,
  linkNext: boolean
}

const initialTabMenuState: FlowTabmenuState = {
  linkNext: false
}

export const featureTabMenuReducer = createReducer(
  initialTabMenuState,
  on(tabMenuAction.enqueTab, (state, props) => {
    if (state.linkNext) {
      const theLastTab = state.lasttab ? { ...state.lasttab, active: false } : undefined;
      const newtab: ITabData = { ...props, parent: theLastTab, active: true };
      return { ...state, current: newtab, linkNext: false }
    } else {
      return { ...state, lasttab: state.current, current: {...props, active: true} };
    }
  }),
  on(tabMenuAction.dequeTab, (state, props) => {
    return { ...state, lasttab: props.parent, linkNext: true };
  }),
  on(tabMenuAction.linkTab, (state) => {
    return { ...state, linkNext: true, lasttab: state.current }
  })
)
