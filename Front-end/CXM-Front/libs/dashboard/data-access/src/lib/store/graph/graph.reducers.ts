import { createReducer, on } from "@ngrx/store";
import { IAsyncLoader, IGraphCannelEnvoyResult, IGraphDepositModeResult, IGraphEvolutionResult, IGraphFlowTrackingResult } from '../../models'
import * as graphActions from './graph.actions';
import { DateFormatter } from "@cxm-smartflow/analytics/util";

const toLocalDate = (isoString: string) => {
 return new Date(isoString);
}

export const featureDashboardGraphKey = 'feature-dashboard-graph-key';

const initialState:{
  graphChannel: IAsyncLoader<IGraphCannelEnvoyResult>,
  graphDeposit: IAsyncLoader<IGraphDepositModeResult>,
  graphFlowtracking: IAsyncLoader<IGraphFlowTrackingResult>,
  graphEvolution: IAsyncLoader<IGraphEvolutionResult>
  refreshTime: Date,
  filter: {
    option: number,
    start: any,
    end: any
  },
  requestedAt: string,
  requestedAtDate: Date,
} = {
  graphChannel: {
    fetching: false, isError: false,
    data: null,
    error: null
  },
  graphDeposit: {
    fetching: false, isError: false,
    data: null,
    error: null
  },
  graphFlowtracking: {
    fetching: false, isError: false,
    data: null,
    error: null
  },
  graphEvolution: {
    fetching: false, isError: false,
    data: null,
    error: null
  },
  filter: { option: 1, end: null,start: null },
  refreshTime: new Date(),
  requestedAt: '',
  requestedAtDate: new Date(),
}



export const reducer = createReducer(initialState,
    on(graphActions.filterGraphChanged, (state, props) => {
      const requestedAtDate = new Date();
      const formatter = new DateFormatter();
      const requestedAt = formatter
        .setYear(requestedAtDate.getFullYear())
        .setMonth(requestedAtDate.getMonth() + 1)
        .setDay(requestedAtDate.getDate())
        .setHours(requestedAtDate.getHours())
        .setMinutes(requestedAtDate.getMinutes())
        .setSeconds(requestedAtDate.getSeconds())
        .formatDate();

      return { ...state, filter: { option: props.option, end: props.end, start: props.start }, requestedAt, requestedAtDate };
    }),
    on(graphActions.fetchGraphChannel, (state) => {
      return { ...state, graphChannel: { ...state.graphChannel, fetching: true } }
    }),
    on(graphActions.fetchGraphChannelSuccess, (state, props) => {
      return { ...state, graphChannel: { ...state.graphChannel, fetching: false, isError: false, data: props.data }, refreshTime: state.requestedAtDate };
    }),
    on(graphActions.fetchGraphChannelFail, (state) => {
      return { ...state, graphChannel: { ...state.graphChannel, fetching: false, isError: true } }
    }),

    on(graphActions.fetchGraphDepositMode, (state) => {
      return { ...state, graphDeposit: { ...state.graphDeposit, fetching: true, isError: false } }
    }),
    on(graphActions.fetchUserGraphFilter, (state) => {
      const requestedAtDate = new Date();
      const formatter = new DateFormatter();
      const requestedAt = formatter
        .setYear(requestedAtDate.getFullYear())
        .setMonth(requestedAtDate.getMonth() + 1)
        .setDay(requestedAtDate.getDate())
        .setHours(requestedAtDate.getHours())
        .setMinutes(requestedAtDate.getMinutes())
        .setSeconds(requestedAtDate.getSeconds())
        .formatDate();

      return { ...state, requestedAt, requestedAtDate }
    }),
    on(graphActions.fetchGraphDepositModeSuccess, (state, props) => {
      return { ...state, graphDeposit: { ...state.graphDeposit, fetching: false, isError: false, data: props.data  }, refreshTime: state.requestedAtDate };
    }),
    on(graphActions.fetchGraphDepositModeFail, (state) => {
      return { ...state, graphDeposit: { ...state.graphDeposit, fetching: false, isError: true } }
    }),

    on(graphActions.fetchGraphFlowTracking, (state) => {
      return { ...state, graphFlowtracking: { ...state.graphFlowtracking, fetching: true, isError: false } }
    }),
    on(graphActions.fetchGraphFlowtrackingSuccess, (state, props) => {
      return { ...state, graphFlowtracking: { ...state.graphFlowtracking, fetching: false, isError: false, data: props.data  }, refreshTime: state.requestedAtDate };
    }),
    on(graphActions.fetchGraphFlowtrackingFail, (state) => {
      return { ...state, graphFlowtracking: { ...state.graphFlowtracking, fetching: false, isError: true } }
    }),
    on(graphActions.fetchUserGraphFilterSuccess, (state, props) => {

      return { ...state, filter: {
        option: parseInt(props.filter.selectDateType+''),
        end: toLocalDate(props.filter.customEndDate),
        start: toLocalDate(props.filter.customStartDate)
      } }
    }),


    on(graphActions.fetchGraphEvolution, (state) => {
      return { ...state, graphEvolution: { ...state.graphEvolution, fetching: true, isError: false  } }
    }),
    on(graphActions.fetchGraphEvolutionSuccess, (state, props) => {
      return { ...state, graphEvolution: { ...state.graphEvolution, fetching: false, isError: false, data: props.data } }
    }),
    on(graphActions.fetchGraphEvolutionFail, (state) => {
      return { ...state, graphEvolution: {  ...state.graphEvolution, fetching: false, isError: true } }
    }),


    on(graphActions.setUserGraphFilter, (state, props) => {
      return { ...state, filter: {
        option: parseInt(props.filter.selectDateType+''),
        end: props.filter.customEndDate ? new Date(props.filter.customEndDate): null,
        start: props.filter.customStartDate ? new Date(props.filter.customStartDate): null
      }, refreshTime: state.requestedAtDate }
    })
  )
