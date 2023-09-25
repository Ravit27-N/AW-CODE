import { Injectable } from '@angular/core';
import { CanDeactivate, UrlTree } from '@angular/router';
import { AwConfirmMessageService } from '../../../shared';

@Injectable({ providedIn: 'root' })
export class FeatureRoleFormDeactivate implements CanDeactivate<any> {
  constructor(private awConfirmMessageService: AwConfirmMessageService) {}

  async canDeactivate(): Promise<boolean | UrlTree> {
    if (localStorage.getItem('form-role')) {
      return await this.cancel();
    }
    return true;
  }

  async cancel(): Promise<boolean> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Discard all changes?',
        message: 'Are you sure you want to discard all changes?',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();
    if (confirmed) {
      localStorage.removeItem('form-role');
      return true;
    }
    return false;
  }
}
