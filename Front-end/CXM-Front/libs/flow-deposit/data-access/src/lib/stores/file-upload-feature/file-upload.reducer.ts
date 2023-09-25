import { createReducer, on } from '@ngrx/store';
import * as fileUploadAction from './file-upload.action';

export const acquisitionFileUploadFeatureKey = 'acquisition-file-upload-feature';

const initializeState = {
  prepared: false,
  sending: false,
  done: false,
  error: false,
  progress: 0,
  progressFileName: '',
  response: {},
  locked: false,
  loading: false,
  isCannotIdentify: null,
  isValidateBeforeUpload: false
};

export const acquisitionFileUploadFeatureReducer = createReducer(
  initializeState,
  on(fileUploadAction.dropFilesAction, (state, props) => ({ ...state, files: props, prepared: true, progressFileName: props.fileName, loading: false})),
  on(fileUploadAction.uploadFileAction, (state) => ({ ...state, sending: true })),
  on(fileUploadAction.uploadFileProgression, (state, props) => ({ ...state, progress: props.progress })),
  on(fileUploadAction.uploadFileDoneAction, (state, props) => ({ ...state, done: true, sending: false, response: props.response, loading: false })),
  on(fileUploadAction.uploadFileFailAction, (state, props) => {
    const { error } = props;

    return { ...state, error: error.statusCode, errorStatusCode: error.statusCode, sending: false, done: false, loading: false };
  }),
  on(fileUploadAction.unloadUploadFileAction, (state) => ({ ...state, ...initializeState })),
  on(fileUploadAction.initPreAnalysis, (state, props) => ({...state, response: props})),
  on(fileUploadAction.initAcquisitionFileUploadFeature, (state, props) => ({...state, ...props})),
  on(fileUploadAction.unlockWhenNoDocumentValid, (state) => ({ ...state })),
  on(fileUploadAction.checkIsDocumentCannotIdentify, (state, props) => ({
    ...state,
    isCannotIdentify: props.isCannotIdentify,
    errorStatusCode: 5008,
  })),
  on(fileUploadAction.validateDocumentFail, (state, props) => ({ ...state, ...props })),
);
