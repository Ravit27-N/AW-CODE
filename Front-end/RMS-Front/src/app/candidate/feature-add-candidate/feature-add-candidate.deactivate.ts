import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanDeactivate,
  RouterStateSnapshot,
} from '@angular/router';
import { AwConfirmMessageService } from '../../shared';

@Injectable({
  providedIn: 'root',
})
export class FeatureAddCandidateDeactivate implements CanDeactivate<any> {
  constructor(private awConfirmMessageService: AwConfirmMessageService) {}

  async canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot,
  ): Promise<boolean> {
    if (
      currentRoute.params.id &&
      localStorage.getItem('candidate-form-has-change') &&
      !localStorage.getItem('submit-form-candidate')
    ) {
      const storage = await this.confirmLeave();
      if (storage) {
        this.removeFromStorage();
      }

      return storage;
    } else if (
      !currentRoute.params.id &&
      localStorage.getItem('candidate-form-step-1') &&
      !localStorage.getItem('submit-form-candidate')
    ) {
      const storage = await this.confirmLeave();
      if (storage) {
        this.removeFromStorage();
      }

      return storage;
    }
    return true;
  }

  private removeFromStorage(): void {
    localStorage.removeItem('candidate-form');
    localStorage.removeItem('candidate-form-step-1');
    localStorage.removeItem('candidate-form-step-2');
    localStorage.removeItem('candidate-form-step-3');
    localStorage.removeItem('candidate-form-step-4');
    localStorage.removeItem('candidate-form-has-change');
    localStorage.removeItem('submit-form-candidate');
    localStorage.removeItem('currentStep');
  }

  private async confirmLeave() {
    return await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Discard all changes?',
        message:
          'Are you sure you want to discard all changes?\nThis action is irreversible.',
        cancelButton: 'Cancel',
        confirmButton: 'Confirm',
      })
      .toPromise();
  }
}
