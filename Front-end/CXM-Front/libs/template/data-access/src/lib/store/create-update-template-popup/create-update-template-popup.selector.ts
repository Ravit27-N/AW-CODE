import { createFeatureSelector } from '@ngrx/store';
import { createUpdateTemplatePopupKey } from './create-update-template-popup.reducer';

export const createUpdateTemplatePopupState = createFeatureSelector<any>(
  createUpdateTemplatePopupKey
);
