import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {
  /**
   * Method used remove item from local storage.
   * @param itemKey
   */
  removeItemFromLocalStorage(itemKey: string): void{
    localStorage.removeItem(itemKey);
  }
}
