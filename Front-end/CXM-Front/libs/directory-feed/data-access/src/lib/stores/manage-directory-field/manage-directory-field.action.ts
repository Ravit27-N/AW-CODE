import { createAction, props } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { DirectoryFeedField } from '../../models';

// Get directory feed details.
export const getDirectoryField = createAction(
  '[directory field / get directory field]',
  props<{ directoryId: number }>()
);
export const getDirectoryFieldSuccess = createAction(
  '[directory field / get directory field success]',
  props<{
    fields: DirectoryFeedField;
  }>()
);
export const getDirectoryFieldFail = createAction(
  '[directory field / get directory field fail]',
  props<{
    httpErrorResponse: HttpErrorResponse;
  }>()
);

export const destroyDirectoryFields = createAction(
  '[directory field / destroy directory fields]'
);
