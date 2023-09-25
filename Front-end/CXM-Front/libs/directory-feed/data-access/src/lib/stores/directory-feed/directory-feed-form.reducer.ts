import { createReducer, on } from '@ngrx/store';
import * as feedFormActions from './directory-feed-form.actions';

export const featureFeedFormKey = 'FEAUTRE_FEED_FORM_KEY';


const initialData: any = {
  selectedRow: null,
  schemes: {
    columns: [], fields: {}, directoryId: '', directoryName: ''
  },
  data: [],
  page: 1,
  pageSize: 1,
  total: 0
}

const modifyCellValue = (state: any, props: any) => {
  const data = Array.from(state.data).map((d: any) => {
    if (d.lineNumber === props.row.lineNumber) {
      let { values } = d;
      const cell = values[props.order];
      const newCell = { ...cell, value: props.value };
      values = { ...values, [props.order]: newCell };
      return { ...d, values }
    }
    return d;
  })
  return { ...state, data };
}

const addNewRow = (state: any) => {
  const { data } = state;
  const lastLineNumber: number = Array.from(data).reduce((prev: number, cur: any) => Math.max(prev, cur.lineNumber), 0);

  const values = state.schemes.columns
  .reduce((prev: any, cur: any) =>  Object.assign(prev, { [state.schemes.fields[cur].order]: { value: '', fieldOrder: state.schemes.fields[cur].order  } } ) , {});

  const row = { lineNumber: lastLineNumber + 1, directoryId: state.directoryId, values  };

  return {...state, data: [ ...data, row ], selectedRow: row};
}

const removeRow = (state: any, props: any) => {
  const { row } = props;
  let { data } = state;
  data = Array.from(data).filter((x: any) => x.lineNumber !== row.lineNumber);

  return {...state, data, selectedRow: null };
}

export const reducer = createReducer(
  initialData,
  // on(feedFormActions.loadFeedForm, (state) => state),
  on(feedFormActions.loadFeedFormSuccess, ((state, schemes) => ({ ...state, schemes }))),
  on(feedFormActions.unloadFeedForm, () => initialData),
  on(feedFormActions.loadFeedDataSuccess, (state, props) => ({ ...state, data: props.content, page: props.page, pageSize: props.pageSize, total: props.totoal })),
  on(feedFormActions.selectRowFeed, (state, props) => ({ ...state, selectedRow: props.row })),
  on(feedFormActions.changeCellValue, modifyCellValue),
  on(feedFormActions.createRow, addNewRow),
  on(feedFormActions.removeSelectedRow, removeRow),
  on(feedFormActions.removeAllRow, (state) => ({ ...state, data: [], selectedRow: null }))
)
