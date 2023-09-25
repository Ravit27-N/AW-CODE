import { DirectoryField } from '@cxm-smartflow/definition-directory/data-access';
import { DIRECTORIES_FORM } from './directory-constant';

/**
 * Method used to remove directory from storage.
 */
export const removeDirectoryFromStorage = () => {
  localStorage.removeItem(DIRECTORIES_FORM);
}

/**
 * Method used to save object of {@link DirectoryField} to storage.
 * @param directoryField
 */
export const saveDirectoryToStorage = (directoryField: DirectoryField[]) => {
  localStorage.setItem(DIRECTORIES_FORM, JSON.stringify(directoryField));
}
