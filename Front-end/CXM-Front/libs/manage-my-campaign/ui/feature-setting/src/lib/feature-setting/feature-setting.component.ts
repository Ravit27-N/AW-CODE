
import { globalPropertiesLable, sendMailForm } from '@cxm-smartflow/shared/data-access/model';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { FormGroup, FormBuilder, Validators, FormArray } from '@angular/forms';
import { Component } from '@angular/core';
import { createFeaturedSetting, getSetFeaturedSetting } from '@cxm-smartflow/manage-my-campaign/data-access';
import { Observable, of } from 'rxjs';
import { globalPropertiesLable as Label } from '@cxm-smartflow/shared/data-access/model';
export interface Template {
  text: string;
  value: string;
}

@Component({
  selector: 'cxm-smartflow-feature-setting',
  templateUrl: './feature-setting.component.html',
  styleUrls: ['./feature-setting.component.scss'],
})
export class FeatureSettingComponent {
  settingLabel = globalPropertiesLable.cxmCampaign.manageMyCampaign;
  settingButton = globalPropertiesLable.button;
  templateDemoData: Template[];
  selectTemplate: string;
  sendMailForm: sendMailForm;
  formGroup: FormGroup;
  isLoading$: any;
  canSubmit: boolean;
  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private store: Store,
  ) {

    this.store.select(getSetFeaturedSetting).subscribe((response) => {
      this.isLoading$ = response?.isLoading;
    });

    this.templateDemoData = [{
      text: 'CAMPAGNE TEST JUIN 2021',
      value: 'CAMPAGNE TEST JUIN 2021'
    }];

    this.formGroup = this.formBuilder.group({
      templateName: ['', [Validators.required]],
      firstname: ['', [Validators.required]],
      lastname: ['', [Validators.required]],
      replyToAddress: ['', [Validators.required]],
      nom: ['', [Validators.required]],
      prenom: ['', [Validators.required]],
      email: ['', [Validators.required]],
      webview_url: ['', [Validators.required]],
      unsubscribe_url: ['', [Validators.required]],
      sender: new FormArray([]),
    });

    this.addSender();

    this.sendMailForm = {

      template: {
        name: '',
      },
      email: {
        isPublished: '1',
        replyToAddress: '',
      },
      contacts: [
        {
          contact: {
            firstname: '',
            lastname: '',
          },
          tokens: {
            nom: '',
            prenom: '',
            email: '',
            webview_url: 'https://tessi.fr',
            unsubscribe_url: 'https://tessi.fr',
          },
        },
      ],
    };

  }

  get f() {
    return this.formGroup.controls;
  }
  get t() {
    return this.f.sender as FormArray;
  }

  addSender(): void {
    this.t.push(
      this.formBuilder.group({
        sender: ['', [Validators.required]],
      })
    );
  }

  removeSender(index: any) {
    this.t.removeAt(index);
  }

  onNext() {
    this.router.navigateByUrl(
      'cxm-smartflow/cxm-campaign/manage-my-campaign/destination'
    );
  }

  onPrevious() {
    this.router.navigateByUrl(
      'cxm-smartflow/cxm-campaign/manage-my-campaign/model'
    );
  }

  onSubmit() {

    this.checkFormInvalid();

    this.tranformDataToSendMailForm().subscribe((response) => {
      this.store.dispatch(createFeaturedSetting({ data: response , isLoading: true, status: 'Loading'}));
    });

  }

  tranformDataToSendMailForm(): Observable<sendMailForm> {
    this.sendMailForm = {
      template: {
        name: this.formGroup.controls['templateName'].value
      },
      email: {
        isPublished: '1',
        replyToAddress: this.formGroup.controls['replyToAddress'].value
      },
      contacts: [
        {
          contact: {
            firstname: this.formGroup.controls['firstname'].value,
            lastname: this.formGroup.controls['lastname'].value
          },
          tokens: {
            nom: this.formGroup.controls['nom'].value,
            prenom: this.formGroup.controls['prenom'].value,
            email: this.formGroup.controls['email'].value,
            webview_url: this.formGroup.controls['webview_url'].value,
            unsubscribe_url: this.formGroup.controls['unsubscribe_url'].value
          }
        }
      ]
    }
    return of<sendMailForm>(this.sendMailForm);
  };

  checkFormInvalid() {
    this.canSubmit = !this.formGroup.controls['templateName'].invalid && !this.formGroup.controls['replyToAddress'].invalid && !this.formGroup.controls['firstname'].invalid &&
      !this.formGroup.controls['lastname'].invalid && !this.formGroup.controls['nom'].invalid && !this.formGroup.controls['prenom'].invalid &&
      !this.formGroup.controls['email'].invalid && !this.formGroup.controls['webview_url'].invalid && !this.formGroup.controls['unsubscribe_url'].invalid;
  }

}
