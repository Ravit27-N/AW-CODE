import { directoryEnv as env } from '@env-cxm-directory';
import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import {
  addClientId,
  adjustFormStep1,
  submitForm,
  removeClientId,
  selectDefinitionDirectoryForm,
  DirectoryField,
  adjustDirectoryFields,
  unloadDefinitionDirectoryForm,
  DirectoryDefinitionForm,
  setupHistoryForm,
  FormMode,
  adjustDefinitionDirectoryFormEditor, selectDefinitionDirectoryFormEditor, selectDefinitionDirectoryOldName
} from "@cxm-smartflow/definition-directory/data-access";
import {FormDirectoryStepOneType} from "./form-directory-step-one/form-directory-step-one.type";
import {Subscription} from "rxjs";
import {appRoute} from "@cxm-smartflow/shared/data-access/model";
import { CxmDirectoryService } from '@cxm-smartflow/shared/data-access/api';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import {skip} from "rxjs/operators";

@Component({
  selector: 'cxm-smartflow-feature-form-directory',
  templateUrl: './feature-form-directory.component.html',
  styleUrls: ['./feature-form-directory.component.scss'],
})
export class FeatureFormDirectoryComponent implements OnInit, OnDestroy {
  @Input() formDirectoryMode: FormMode = 'create';

  formDirectoryStep: 1 | 2 | 3 = 1;
  formStep1: FormDirectoryStepOneType = { name: '',  displayName: ''};
  formStep2: DirectoryField[] = [];
  formStep2Editor: any;
  step3SelectedClient: number[] = [];
  ownerId: number;
  directoryId: number;
  oldName = '';
  alreadyFeed = false;

  #definitionDirectoryForm: DirectoryDefinitionForm;
  #subscription: Subscription = new Subscription();

  constructor(private router: Router, private store: Store,
              private directoryService: CxmDirectoryService,
              private snackBar: SnackBarService,
              private translate: TranslateService,
              private confirmationService: ConfirmationMessageService) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }

  ngOnInit(): void {
    const definitionDirectoryFormHistory = JSON.parse(localStorage.getItem('definitionDirectoryForm') || '{}');
    this.formDirectoryStep = definitionDirectoryFormHistory?.step || 1;
    if (Object.keys(definitionDirectoryFormHistory).length > 0) {
      this.store.dispatch(setupHistoryForm({ definitionDirectoryForm: definitionDirectoryFormHistory.definitionDirectoryForm }));
    }
    this.subscribeFormValue();
  }

  ngOnDestroy() {
    this.#subscription.unsubscribe();
    this.store.dispatch(unloadDefinitionDirectoryForm());
  }

  /**
   * Get page title.
   */
  get formDirectoryTitle(): string {
    return `directory.definition_directory_${this.formDirectoryMode}_step_${this.formDirectoryStep}_title`;
  }

  /**
   * Get page subtitle.
   */
  get formDirectorySubTitle(): string {
    return `directory.definition_directory_${this.formDirectoryMode}_step_${this.formDirectoryStep}_sub_title`;
  }

  goPreviousStep(currentStep: number): void {
    switch (currentStep) {
      case 1: {
        this.router.navigateByUrl(appRoute.cxmDirectory.navigateToListDefinitionDirectory);
        break;
      }

      case 2: {
        this.formDirectoryStep = 1;
        break;
      }

      case 3: {
        this.formDirectoryStep = 2;
        break;
      }
    }

    this.updateFormHistory(this.#definitionDirectoryForm, this.formDirectoryStep);
  }

  goNextStep(currentStep: number): void {
    switch (currentStep) {
      case 1: {
        this.formDirectoryStep = 2;
        break;
      }

      case 2: {
        this.formDirectoryStep = 3;
        break;
      }

      case 3: {
        if (this.formDirectoryMode === 'create') {
          this.store.dispatch(submitForm({ formType: this.formDirectoryMode }));
        }
        if(this.formDirectoryMode === 'modify'){
          this.translate.get('directory.definition_directory_confirm_update')
            .toPromise().then(message => {
            this.confirmationService.showConfirmationPopup({
              type: 'Secondary',
              title: message?.title,
              message: message?.message,
              paragraph: message?.paragraph,
              confirmButton: message?.confirm,
              cancelButton: message?.cancel
            })
              .subscribe(confirm => {
                if (confirm) {
                  this.store.dispatch(submitForm({formType: this.formDirectoryMode}));
                }
              });
          });
        }
        break;
      }
    }


    this.updateFormHistory(this.#definitionDirectoryForm, this.formDirectoryStep);
  }

  stepOneValueChangedEvent(formStepOne: FormDirectoryStepOneType): void {
    this.store.dispatch(adjustFormStep1({ formStepOne }));
    this.formStep1 = formStepOne;
  }

  private subscribeFormValue(): void {

    const subscription: Subscription = this.store.select(selectDefinitionDirectoryForm).subscribe(data => {
      this.directoryId = data.id || 0;
      this.formStep1 = { name: data.name || '', displayName: data.displayName ||  ''}
      this.formStep2 = data.directoryFields || [];
      this.step3SelectedClient = data.clients || [];
      this.ownerId = data.ownerId || 0;
      this.#definitionDirectoryForm = data;
      this.alreadyFeed = data?.hasFeeding || false;
    });
    this.#subscription.add(subscription);

    const subscription1: Subscription = this.store.select(selectDefinitionDirectoryForm).pipe(skip(1)).subscribe(data => {
      this.updateFormHistory(data, this.formDirectoryStep);
    });
    this.#subscription.add(subscription1);

    const subscription2 = this.store.select(selectDefinitionDirectoryFormEditor).subscribe(data => {
      this.formStep2Editor = data;
    });
    this.#subscription.add(subscription2);

    const subscription3 = this.store.select(selectDefinitionDirectoryOldName).subscribe(data => {
      this.oldName = data;
    });
    this.#subscription.add(subscription3);
  }

  selectClientEvent(clientId: number) : void {
    this.store.dispatch(addClientId({ clientId }))
  }

  removeClientEvent(clientId: number): void {
    this.store.dispatch(removeClientId({ clientId }));
  }

  fieldListItemChangeEvent(directoryFields: DirectoryField[]): void {
    this.store.dispatch(adjustDirectoryFields({ directoryFields }));
  }

  private updateFormHistory(definitionDirectoryForm: DirectoryDefinitionForm, step: number): void {
    localStorage.setItem('definitionDirectoryForm', JSON.stringify({
      step,
      definitionDirectoryForm,
    }));
  }

  formStep2EditorChangeEvent(definitionDirectoryFormEditor: any): void {
    this.store.dispatch(adjustDefinitionDirectoryFormEditor({ definitionDirectoryFormEditor }));
  }
}
