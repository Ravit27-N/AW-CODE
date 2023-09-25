import { Injectable } from '@angular/core';
import { CanDeactivate, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class FeatureCandidateListDeactivate implements CanDeactivate<any> {
  canDeactivate():
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    localStorage.removeItem('candidate-form');
    localStorage.removeItem('candidate-list');
    localStorage.removeItem('candidate-form-step-1');
    localStorage.removeItem('candidate-form-step-2');
    localStorage.removeItem('candidate-form-step-3');
    localStorage.removeItem('candidate-form-step-4');
    localStorage.removeItem('candidate-form-has-change');
    localStorage.removeItem('submit-form-candidate');
    localStorage.removeItem('currentStep');
    return true;
  }
}
