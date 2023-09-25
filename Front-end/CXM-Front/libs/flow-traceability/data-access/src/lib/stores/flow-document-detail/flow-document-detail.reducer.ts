import { createReducer, on } from '@ngrx/store';
import * as fromActions from './flow-document-detail.action';
import { EventHistoryInfo } from '@cxm-smartflow/flow-traceability/ui/featured-flow-event-history';

export const featuredFlowDocumentDetailKey = 'featured-flow-document-detail-key';

const flowDocumentDetailInitial = {
  id: 0,
  documentDetail: {},
  associateDocument: {},
  eventHistoryInfo: {}
};

export const flowDocumentDetailReducer = createReducer(
  flowDocumentDetailInitial,
  on(fromActions.loadFlowDocumentDetailSuccess, (state, props) => ({ ...state, documentDetail: props.documentDetail })),
  on(fromActions.unloadFlowDocumentDetail, () => ({ ...flowDocumentDetailInitial })),
  on(fromActions.loadAssociateDocumentSuccess, (state, props) => ({ ...state, associateDocument: props.associateDocument })),
  on(fromActions.viewStatusInfoSuccess, (state, props) => ({ ...state, eventHistoryInfo: props.eventHistoryInfo})),
);
