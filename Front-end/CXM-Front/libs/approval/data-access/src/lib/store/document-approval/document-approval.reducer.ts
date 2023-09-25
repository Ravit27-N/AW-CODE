import { createReducer, on } from "@ngrx/store";
import { ApprovalDocModel, ApproveDocResponse } from "../../models/approval-doc.model";
import * as fromActions from './document-approval.action';


export const featureApprovalDocKey = 'feature-document-approval-key';

const initialState: {
  open: boolean,
  approveDocList: {
    contents: Array<ApprovalDocModel>,
    flowName: string,
    page: number, pageSize: number, total: number
  },
  filters: {
    sortByField: string, sortDirection: string, page: number, pageSize: number
  }
} = {
  open: false,
  approveDocList: {
    flowName: '',
    contents: [],
    page: 0, pageSize: 0, total: 0
  },
  filters: {
    sortByField: 'recipient', sortDirection: 'desc', page: 1, pageSize: 10
  }
}



export const approvalDocReducer = createReducer(initialState,

  on(fromActions.openApprovalDocPanel, (state) => ({...state, open: true })),
  on(fromActions.closeApprovalDocPanel, (state) => ({...state, open: false })),
  on(fromActions.loadApprovalDocumentListSuccess, (state, props) => {
    return { ...state, approveDocList: {  ...props.response , flowName: state.approveDocList.flowName  } }
  }),
  on(fromActions.loadApprovalDocumentList, (state, props) => ({ ...state, filters: props.filters })),
  on(fromActions.setFlowName, (state, props) => ({ ...state, approveDocList: { ...state.approveDocList, flowName: props.name } })),
  on(fromActions.unloadApproveDoc, (state) => ({ ...initialState }))
)
