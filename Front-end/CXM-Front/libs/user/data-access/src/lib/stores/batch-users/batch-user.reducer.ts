import { createReducer } from '@ngrx/store';
import { BatchUserStateModel } from '../../models';

const initialState: BatchUserStateModel = {
  fileName: '',
};

export const manageBatchUserKey = 'manage-batch-user-key';

export const batchUserReducer = createReducer(initialState);
