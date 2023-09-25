import { createAction, props } from '@ngrx/store';
import {DepositedFlowModel} from "../../model";


export const dropFilesAction = createAction(
  '[deposit acquisition upload/ file drop]',
  props<{ form: any, fileName: string }>()
);

export const uploadFileAction = createAction(
  '[deposit acquisition upload/ sending file]'
)

export const uploadFileDoneAction = createAction(
  '[deposit acquisition upload/ sending done]',
  props<{ response: DepositedFlowModel }>()
)

export const uploadFileProgression = createAction(
  '[deposit acquisition upload/ sending progression]',
  props<{ progress: number }>()
)

export const uploadFileFailAction = createAction(
  '[deposit acquisition upload/ sending fail]', props<{ error: any }>()
)

export const unloadUploadFileAction = createAction(
  '[deposit acquisition upload/ unload]'
);

export const initPreAnalysis = createAction(
  '[deposit acquisition] / init preAnalysis state',
  props<any>()
);

export const initAcquisitionFileUploadFeature = createAction(
  '[deposit acquisition] / init file upload feature object',
  props<any>()
);

export const unlockWhenNoDocumentValid = createAction(
  '[unlock screen / when no document valid]'
)

export const checkIsDocumentCannotIdentify = createAction('[deposit acquisition] / check isDocumentCanIdentify', props<{ isCannotIdentify: any }>());

export const validateDocumentFail = createAction(
  '[deposit acquisition] / validate document fail',
  props<{ done?: boolean, isCannotIdentify?: any, error?: boolean, prepared?: boolean, progress?: number, isValidateBeforeUpload: boolean, errorStatusCode: number}>()
);
