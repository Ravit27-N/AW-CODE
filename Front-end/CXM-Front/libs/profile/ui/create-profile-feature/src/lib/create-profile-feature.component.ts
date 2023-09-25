import { Component, OnDestroy, OnInit } from '@angular/core';
import { IDeactivateComponent, loadClientModule, selectFormChange, unloadClientModule, unloadProfileForm } from '@cxm-smartflow/profile/data-access';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-create-profile-feature',
  templateUrl: './create-profile-feature.component.html',
  styleUrls: ['./create-profile-feature.component.scss']
})
export class CreateProfileFeatureComponent implements OnInit, IDeactivateComponent, OnDestroy {

  label: any;
  formChange$ = new BehaviorSubject(false);

  constructor(private confirmationService: ConfirmationMessageService, private store: Store, private translate: TranslateService) {
    this.store.select(selectFormChange).subscribe(value => this.formChange$.next(value));
  }

  ngOnInit(): void {
    this.translate.get('profile.form.confirmation').subscribe(json => this.label = json);

    this.store.dispatch(loadClientModule({ profileId: '0' }));
  }

  canExit(): Observable<boolean> {
    if (this.formChange$.value) {
      return this.confirmationService.showConfirmationPopup({
        icon: 'close',
        title: this.label?.title,
        message: this.label?.message,
        cancelButton: this.label?.cancelButton,
        confirmButton: this.label?.confirmButton,
        type: 'Warning'
      });
    } else {
      return of(true);
    }
  }

  ngOnDestroy(): void {
    this.store.dispatch(unloadClientModule());
    this.store.dispatch(unloadProfileForm());
    this.formChange$.complete();
    this.store.complete();
  }
}
