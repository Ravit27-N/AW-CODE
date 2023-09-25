import { Injectable } from '@angular/core';
import { CanDeactivate, Router, UrlTree } from '@angular/router';
import { AwConfirmMessageService } from '../../../shared';

@Injectable({ providedIn: 'root' })
export class FeatureUserAddDeactivate implements CanDeactivate<any> {
  constructor(
    private awConfirmMessageService: AwConfirmMessageService,
    private router: Router,
  ) {}

  async canDeactivate(): Promise<boolean | UrlTree> {
    if (localStorage.getItem('create-user')) {
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
      localStorage.removeItem('create-user');
      return true;
    }
    return false;
  }
}
