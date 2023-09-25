import { Injectable } from '@angular/core';
import { ProfileStorageModel } from '@cxm-smartflow/profile/data-access';
import { appLocalStorageConstant } from '@cxm-smartflow/shared/data-access/model';

@Injectable({
  providedIn: 'root',
})
export class ProfileStorageService {
  setProfileListStorage(data: ProfileStorageModel) {
    localStorage.setItem(
      appLocalStorageConstant.ProfileManagement.List,
      JSON.stringify(data)
    );
  }

  getProfileListStorage(): ProfileStorageModel {
    return JSON.parse(
      localStorage.getItem(appLocalStorageConstant.ProfileManagement.List) ||
        '{}'
    );
  }

  removeProfileListStorage(): void {
    this.setProfileListStorage({});
  }
}
