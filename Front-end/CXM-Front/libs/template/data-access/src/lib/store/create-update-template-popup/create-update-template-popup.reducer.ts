import { createReducer, on } from '@ngrx/store';
import * as fromTemplatePopup from './create-update-template-popup.action';

export const createUpdateTemplatePopupKey = 'create-update-template-p';

const initialState = {};

export const createUpdateTemplatePopupReducer = createReducer(
  initialState,
  on(fromTemplatePopup.showCreateTemplatePopup, (state, props) => ({
    ...state,
    ...props,
  }))
);
