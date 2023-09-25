import { createAction, createFeatureSelector, createReducer, createSelector, on, props } from "@ngrx/store";



export const featureDocumentNavigatorKey = 'feature-doc-nav';



const featureSelector = createFeatureSelector(featureDocumentNavigatorKey);
export const selectDocNavigator = createSelector(featureSelector, (state: any) => ( {
  totalDoc: state.totalDoc, hasNext: state.hasNext, hasPrev: state.hasPrev ,
  currentIndex: state.currentIndex
}));


export const selectDocNavigatorState = createSelector(featureSelector, (state: any) => state);

export const navNextDoc = createAction('[flow doc nav /  next]');
export const navPrevDoc = createAction('[flow doc nav / prev]');
export const setFlowNavDoc = createAction('[flow doc nav / set docs]', props<{ data: any }>());
export const setOpenFlowNavDoc = createAction('[flow doc nav / open doc]', props<{ data: any }>());
export const fetchDocOfFlow = createAction('[flow doc nav / fetch doc of flow]', props<{ flowid: any, docId: any }>());
export const fetchDocumentDetails = createAction('[flow doc details nav / fetch doc details]', props<{ flowid: any, docId: any }>());
export const findOpenedDoc = createAction('[flow doc nav / find open doc]', props<{ alldocument: any, opendocId: any }>())

const initialstate = {
  flowid: null,
  hasNext: false,
  hasPrev: false,
  totalDoc: 0,
  docs: [],
  currentIndex: 0
}


const mapFlowDocumentsToNavState = (contents: any) => {

  const totalDoc = contents.length;
  const hasNext = contents.length > 1;
  const hasPrev = false;
  const currentIndex = 0;
  const docs = contents.map((x: any) => x.id);
  return { totalDoc, hasNext, hasPrev, currentIndex, docs }
}

const findOpenDocInAllDocument = (docs: any, docId: any) => {
  const foundIndex = Array.from(docs).findIndex((x: any) => x === docId);
    if(foundIndex > -1) {
      const hasNext = foundIndex < Math.max(0, docs.length - 1);
      const hasPrev = foundIndex > 0;
      return { currentIndex: foundIndex, hasNext, hasPrev }
    }

    return { }
}

export const reducer = createReducer(initialstate,
  on(setFlowNavDoc, (state, props) => {
    const { response, flowTraceabilityId } = props.data;
    const { contents } = response;
    const navState = mapFlowDocumentsToNavState(contents);

    return { ...state, flowid: flowTraceabilityId, ...navState}
  }),

  on(setOpenFlowNavDoc, (state, props) => {
    const { data } = props;
    const { documentDetail } = data;
    const { id, flowTraceabilityId } = documentDetail;
    const newState = findOpenDocInAllDocument(state.docs, id)

    return { ...state, ...newState  }
  }),

  on(findOpenedDoc, (state, props) => {
    const { alldocument, opendocId } = props;

    const navState = mapFlowDocumentsToNavState(alldocument);
    const newState = findOpenDocInAllDocument(navState.docs, opendocId);

    return { ...state, ...navState, ...newState }
  })
)
