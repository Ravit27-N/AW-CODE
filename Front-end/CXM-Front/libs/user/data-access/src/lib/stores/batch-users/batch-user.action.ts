import { createAction, props } from '@ngrx/store';
import { BatchUserResponse } from '../../models';
import { HttpErrorResponse } from '@angular/common/http';

// Validate CSV mime type.
export const validateBatchUserCSV = createAction('[BATCH USERS / VALIDATE BATCH USER]', props<{ files: any }>());
export const validateBatchUserCSVSuccess = createAction('[BATCH USERS / VALIDATE BATCH USER SUCCESS]', props<{ files: FileList }>());
export const validateBatchUserCSVFail = createAction('[BATCH USERS / VALIDATE BATCH USER FAIL]', props<{ httpErrorResponse?: HttpErrorResponse }>());

// Create a batch of user.
export const createBatchUserSuccess = createAction('[BATCH USERS / CREATE BATCH USER SUCCESS]', props<{  batchUserResponse: BatchUserResponse }>());
export const waitingBatchUser = createAction('[BATCH USERS/ WAITING BATCH USER PROCESSING]');
