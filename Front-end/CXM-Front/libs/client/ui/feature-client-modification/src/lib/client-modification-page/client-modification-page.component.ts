import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Confirmable, CriteriaDistributionFormModel,
  fromClientActions,
  fromModifyClientActions,
  fromModifyClientSelector, IClientDepositModePayload, Preference, PreferencePayload
} from '@cxm-smartflow/client/data-access';
import { Store } from '@ngrx/store';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { Observable } from 'rxjs';
import { ClientFormComponent } from '../client-form/client-form.component';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { filter, take } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-client-modification-page',
  templateUrl: './client-modification-page.component.html',
  styleUrls: ['./client-modification-page.component.scss'],
})
export class ClientModificationPageComponent
  implements OnInit, OnDestroy, Confirmable
{
  isAdmin = UserUtil.isAdmin();

  uploading$: Observable<any>;
  clientData$: Observable<any>;
  navigation$: Observable<any>;
  clientDivision$: Observable<any>;
  clientFunctionality$: Observable<any>;
  holidayConfig$: Observable<any>;
  fillConfig$: Observable<any>
  depositMode$: Observable<any>;
  fluxIdentificationMode$: Observable<any>;
  distributeCriteria$: Observable<Array<CriteriaDistributionFormModel>>;


  @ViewChild(ClientFormComponent) clientFormComponent: ClientFormComponent;


  handleFormChange(formResult: any) {
    this.store.dispatch(fromModifyClientActions.updateClientForm({ form: formResult.form }));
  }

  handleDivisionChange(divisionChanged: any) {
    const { divisions } = divisionChanged;
    this.store.dispatch(fromModifyClientActions.updateClientDivision({ divisions }));
  }

  handleDelete(): void {
    const { id } = this.activateRoute.snapshot.params;
    this.store.dispatch(fromClientActions.attempToDeleteClient({ id }));
  }

  async handleNext() {
    const form = await this.clientFormComponent?.submit();
    if (form?.error && Object.keys(form?.error).length > 0) {
      return;
    }

    this.store.dispatch(fromModifyClientActions.attempToMoveNextStep());
  }

  handlePrev() {
    this.store.dispatch(fromModifyClientActions.attempToMovePrevStep());
  }

  async handleForceDechargement() {
    const msg = await this.translate.get('client.confirmUnloading').toPromise();

    this.confirmMsgService.showConfirmationPopup({
      ...msg,
      icon: 'close',
      type: 'Secondary',
    }).pipe(take(1), filter(e => e)).subscribe(() => {
      this.store.dispatch(fromModifyClientActions.forceDechargement());
    });
  }

  dropfile(event: any) {
    if(event.files && event.files.length > 0) {
      const form = new FormData();
      Array.from(event.files as FileList).forEach(f => form.append('file', f));
      this.store.dispatch(fromModifyClientActions.uploadClientDocument({ form }));
    }
  }

  removeFile() {
    this.store.dispatch(fromModifyClientActions.removeUploadedFile());
  }

  // switch tabs

  switchStepHandler(step: number) {
    this.store.dispatch(fromModifyClientActions.switchToStep({ step }));
  }

  handleFunctionalityChange(functionalChanged: any) {
    this.store.dispatch(
      fromModifyClientActions.updateClientFunctionality({ functionalities: functionalChanged })
    );
  }

  offloadFormChange(offlocadChanged: any) {
    this.store.dispatch(fromModifyClientActions.updateClientOfloading({ offloading: offlocadChanged }));
  }

  fillerConfigChagend(fillerChanged: any) {
    this.store.dispatch(fromModifyClientActions.updateClientFillers({ fillers: fillerChanged }));
  }

  isLocked(): Observable<boolean> {
    return this.store.select(fromModifyClientSelector.selectIsLocked);
  }

  fluxDepositMode(depositModes: IClientDepositModePayload[]) {
    this.store.dispatch(fromModifyClientActions.attemptDepositModeToForm({ depositModes }))
  }

  manageConfigurationChange(enabled: boolean) {
    if (enabled) {
      this.store.dispatch(fromModifyClientActions.manageConfigurationFile());
    }
  }

  configurationFileChanged(enabled: boolean) {
    this.store.dispatch(fromModifyClientActions.attemptToSwitchIdentificationMode({ enabled }));
  }

  distributeCriteriaChange(distributeCriteria: PreferencePayload): void {
    this.store.dispatch(fromModifyClientActions.attemptDistributeCriteriaToForm({ distributeCriteria }));
  }

  manageDigitalCriteria(distributeCriteria: CriteriaDistributionFormModel): void {
    if (distributeCriteria.key === 'client.distribution_criteria_digital') {
      this.store.dispatch(fromModifyClientActions.manageDigitalDistributeCriteria({ distributeCriteria }))
    }
  }

  ngOnDestroy(): void {
    this.store.dispatch(fromModifyClientActions.unloadClientForm());
  }

  ngOnInit(): void {
    this.uploading$ = this.store.select(fromModifyClientSelector.selectClientUpload);
    this.clientData$ = this.store.select(fromModifyClientSelector.selectClientData);
    this.navigation$ = this.store.select(fromModifyClientSelector.selectNavigation);
    this.clientDivision$ = this.store.select(fromModifyClientSelector.selectClientDivision);
    this.clientFunctionality$ = this.store.select(fromModifyClientSelector.selectClientFunctionality);
    this.holidayConfig$ = this.store.select(fromModifyClientSelector.selectOffloadConfig);
    this.fillConfig$ = this.store.select(fromModifyClientSelector.selectClientFillers);
    this.depositMode$ = this.store.select(fromModifyClientSelector.selectClientDepositModes);
    this.fluxIdentificationMode$ = this.store.select(fromModifyClientSelector.selectIdentificationMode);
    this.distributeCriteria$ = this.store.select(fromModifyClientSelector.selectDistributeCriteria);

    this.store.dispatch(fromModifyClientActions.initFormConfig());
    const id = this.activateRoute.snapshot.params.id;
    this.store.dispatch(fromModifyClientActions.loadClientForm({ clientId: id }));

    if (!this.isAdmin) {
      this.store.dispatch(fromModifyClientActions.switchToStep({ step: 2 }));
    }
  }


  constructor(
    private readonly translate: TranslateService,
    private readonly activateRoute: ActivatedRoute,
    private readonly store: Store,
    private readonly router: Router,
    private readonly confirmMsgService: ConfirmationMessageService,
  ) {}
}
