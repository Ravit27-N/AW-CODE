import { Injectable } from '@angular/core';
import { CanDeactivate, UrlTree } from '@angular/router';
import { AwConfirmMessageService } from '../../shared';

@Injectable({ providedIn: 'root' })
export class FeatureAddInterviewDeactivate implements CanDeactivate<any> {
  constructor(private awConfirmMessageService: AwConfirmMessageService) {}

  async canDeactivate(): Promise<boolean | UrlTree> {
    if (localStorage.getItem('form-interview')) {
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
      localStorage.removeItem('form-interview');
      return true;
    }
    return false;
  }
}
