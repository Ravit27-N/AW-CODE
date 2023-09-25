import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { ClientFormComponent } from '../client-form/client-form.component';
import {
  Confirmable,
  fromModifyClientActions,
  fromModifyClientSelector,
} from '@cxm-smartflow/client/data-access';
import { Observable } from 'rxjs';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Router } from '@angular/router';
import { appRoute } from '@cxm-smartflow/template/data-access';

@Component({
  selector: 'cxm-smartflow-client-creation-page',
  templateUrl: './client-creation-page.component.html',
  styleUrls: ['./client-creation-page.component.scss'],
})
export class ClientCreationPageComponent
  implements OnInit, OnDestroy, Confirmable
{
  uploading$: Observable<any>;
  clientData$: Observable<any>;
  navigation$: Observable<any>;
  clientDivision$: Observable<any>;

  @ViewChild(ClientFormComponent) clientFormComponent: ClientFormComponent;

  handleFormSubmit(formResult: any) {
    if(formResult.error && Object.keys(formResult.error).length > 0) {
      return;
    }

    this.store.dispatch(fromModifyClientActions.attempToMoveNextStep());
  }

  handleFormChange(formResult: any) {
    this.store.dispatch(fromModifyClientActions.updateClientForm({ form: formResult.form }));
  }

  handleDivisionChange(divisionChanged: any) {
    const { divisions } = divisionChanged;
    this.store.dispatch(
      fromModifyClientActions.updateClientDivision({ divisions })
    );
  }

  handleFunctionalityChange(functionalChanged: any) {
    this.store.dispatch(
      fromModifyClientActions.updateClientFunctionality({ functionalities: functionalChanged })
    );
  }

  handleNext() {
    this.clientFormComponent?.submit();
  }

  handlePrev() {
    this.store.dispatch(fromModifyClientActions.attempToMovePrevStep());
  }

  dropfile(event: any) {
    if (event.files && event.files.length > 0) {
      const form = new FormData();
      Array.from(event.files as FileList).forEach((f) =>
        form.append('file', f)
      );
      this.store.dispatch(
        fromModifyClientActions.uploadClientDocument({ form })
      );
    }
  }

  removeFile() {
    this.store.dispatch(fromModifyClientActions.removeUploadedFile());
  }

  constructor(
    private readonly translate: TranslateService,
    private readonly store: Store,
    private confirm: ConfirmationMessageService,
    private router: Router
  ) {}

  isLocked(): Observable<boolean> {
    return this.store.select(fromModifyClientSelector.selectIsLocked);
  }

  ngOnInit(): void {
    // Only admin can access this page
    if(this.checkAdminPrivilege()==false) {
      return;
    }

    this.uploading$ = this.store.select(
      fromModifyClientSelector.selectClientUpload
    );
    this.clientData$ = this.store.select(
      fromModifyClientSelector.selectClientData
    );
    this.navigation$ = this.store.select(
      fromModifyClientSelector.selectNavigation
    );
    this.clientDivision$ = this.store.select(
      fromModifyClientSelector.selectClientDivision
    );

    this.store.dispatch(fromModifyClientActions.loadClientForm({}));
  }

  ngOnDestroy(): void {
    this.store.dispatch(fromModifyClientActions.unloadClientForm());
  }

  // TODO: check user privilege for the form create client
  checkAdminPrivilege(): boolean {
    const isAdmin = UserUtil.isAdmin();
    if(!isAdmin) {
      this.translate
      .get('template.message')
      .toPromise()
      .then((messageProps) => {
        this.confirm.showConfirmationPopup({
          icon: 'close',
          title: messageProps.unauthorize,
          message: '',
          cancelButton: messageProps.unauthorizeCancel,
          confirmButton: messageProps.unauthorizeLeave,
          type: 'Warning'
        }).subscribe(() => {
            this.router.navigateByUrl(`${appRoute.cxmClient.navigateToListClient}`);
          });
      })
    }

    return isAdmin;
  }
}
