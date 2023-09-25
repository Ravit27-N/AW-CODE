import {BREADCRUMB_FR } from './breadcrumb-fr.constant';
import { BREADCRUMB_EN } from './breadcrumb-en.constant';

/**
 * Method used to get bread crumbs
 * with translate language.
 */
export const getBreadcrumb = () => {
  if (localStorage.getItem('locale') === 'en') {
    return BREADCRUMB_EN;
  } else {
    return BREADCRUMB_FR;
  }
};
