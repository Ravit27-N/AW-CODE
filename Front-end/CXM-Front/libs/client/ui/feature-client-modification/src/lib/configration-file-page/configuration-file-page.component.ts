import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  ConfigurationForm,
  Confirmable,
  DEFAULT_SECTION_KEY,
  fromModifyClientActions,
  fromModifyClientSelector,
  PostalConfigurationVersion
} from '@cxm-smartflow/client/data-access';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { ConfigEditorMode, FileConfigEditorService } from '../file-config-editor/file-config-editor.service';
import { filter, take } from 'rxjs/operators';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-configuration-file-page',
  templateUrl: './configuration-file-page.component.html',
  styleUrls: ['./configuration-file-page.component.scss'],
})
export class ConfigurationFilePageComponent implements OnInit, Confirmable, OnDestroy {
  clientName = '';
  configurations$: Observable<any>;
  configurationVersion$ = new BehaviorSubject<PostalConfigurationVersion []> ([]);
  isPreviewConfigurationMode = false;
  configurationHasModified = false;
  disableSubmitButton = true;
  // Confirmable message.
  confirmMsg = 'client.confirm_discard_client_configuration';

  private _subscription$= new Subscription();

  constructor(private _store: Store, private _activateRoute: ActivatedRoute,
              private _confirmMessageService: ConfirmationMessageService,
              private _translate: TranslateService,
              private _fileConfigEditorService: FileConfigEditorService) {}

  ngOnInit(): void {
    this.clientName = this._activateRoute.snapshot.params.clientName;
    this._store.dispatch(fromModifyClientActions.attemptClientNameInConfigurationFile({ clientName: this.clientName }));
    this.configurations$ = this._store.select(fromModifyClientSelector.selectConfigurations);
    this._subscription$.add(this._store.select(fromModifyClientSelector.selectConfigurationVersion).subscribe(configurationVersion => {
      this.configurationVersion$.next(configurationVersion);
    }));
    this._subscription$.add(
      this._store.select(fromModifyClientSelector.selectIsPreviewConfigurationMode).subscribe(value => {
        this.isPreviewConfigurationMode = value;
        this.checkDisableSubmitButton();
      })
    );

   this._subscription$.add( this._store.select(fromModifyClientSelector.selectConfigurationModified).subscribe((hasModified: boolean) => {
     this.configurationHasModified = hasModified;
     this.checkDisableSubmitButton();
   }));
  }

  checkDisableSubmitButton() {
    if (this.isPreviewConfigurationMode) {
      this.disableSubmitButton = true;
    } else {
      this.disableSubmitButton = !this.configurationHasModified;
    }
  }

  ngOnDestroy(): void {
    this._store.dispatch(fromModifyClientActions.unloadClientForm());
    this._subscription$.unsubscribe();
  }

  isLocked(): Observable<boolean>{
    if(this.configurationVersion$.value.length === 0){
      return of(false);
    }
    return this._store.select(fromModifyClientSelector.selectConfigurationModified);
  }

  back() {
    history.back();
  }

  orderModel($event: any) {
    const { currentIndex , previousIndex } = $event;
    this._store.dispatch(fromModifyClientActions.reorderModelConfiguration({ currentIndex , previousIndex }));
  }

  addModel(config: ConfigurationForm[]) {
    const modeNames = config.map(c => c.name);
    this._fileConfigEditorService.show({ name: '', draggable: true, order: config.length + 1, modifiable: true, removable: true, content: '', entries: []}, modeNames, ConfigEditorMode.MODIFY)
      .pipe(filter(c => c), take(1)).subscribe((configuration: ConfigurationForm) => {
      this.defineConfigSectionModelEntry(configuration);
      this._store.dispatch(fromModifyClientActions.addModelConfiguration({ configuration }))
    });
  }

  modifyModel(config: ConfigurationForm, configs: ConfigurationForm[]) {
    const editMode = this.isPreviewConfigurationMode ? ConfigEditorMode.VIEW : ConfigEditorMode.MODIFY;
    const modeNames = configs.map(c => c.name);
    this._fileConfigEditorService.show(config, modeNames, editMode)
      .pipe(filter(c => c), take(1)).subscribe((configuration: ConfigurationForm) => {
      if (configuration.name !== DEFAULT_SECTION_KEY) {
        this.defineConfigSectionModelEntry(configuration)
      }
      this._store.dispatch(fromModifyClientActions.modifyModelConfiguration({ configuration }))
    });
  }

  private defineConfigSectionModelEntry(configuration: ConfigurationForm) {
    const sectionModelEntry = {key: 'Modele', value: configuration.name};
    const exists = configuration.entries.some(entry => entry.key.trim() === sectionModelEntry.key
      && entry.value.trim() === sectionModelEntry.value);
    if (!exists) {
      configuration.entries.unshift(sectionModelEntry);
    }
  }

  deleteModel(configuration: ConfigurationForm) {
    this._translate.get('client').toPromise().then(message => {
      this._confirmMessageService.showConfirmationPopup(
        {
          type: 'Warning',
          heading: message.configuration_confirm_delete_title,
          title: message.configuration_confirm_delete_title,
          message: message.configuration_confirm_delete_message,
          confirmButton: message.configuration_confirm_delete_deleteBtn,
          cancelButton: message.configuration_confirm_delete_cancelBtn,
        }).pipe(filter(e => e), take(1)).subscribe(() => {
          this._store.dispatch(fromModifyClientActions.deleteModelConfiguration({ configuration }));
      })
    });
  }

  downloadINI() {
    this._store.dispatch(fromModifyClientActions.downloadINIConfigurationFile());
  }

  register(enabled: boolean | null) {
    if (!enabled) {
      return;
    }

    this._store.dispatch(fromModifyClientActions.registerNewConfiguration());
  }

  onViewConfigurationVersion(configVersion: PostalConfigurationVersion): void {
    // if (this.configurationHasModified) {
    //   this._translate.get('client.confirm_discard_client_configuration').toPromise().then(translate => {
    //     const { confirmButton, cancelButton, title_switch_version, message } = translate;
    //     this._confirmMessageService.showConfirmationPopup(
    //       {
    //         type: 'Warning',
    //         // heading: message.configuration_confirm_delete_title,
    //         title: title_switch_version,
    //         message: message,
    //         confirmButton: confirmButton,
    //         cancelButton: cancelButton
    //       }).pipe(filter(e => e), take(1)).subscribe(() => {
    //       this._store.dispatch(fromModifyClientActions.fetchTheVersionConfigurationById(
    //         { versionId: configVersion.version, isPreview: true, configurationHasChanged: false }));
    //     })
    //   });
    // } else {
    //   this._store.dispatch(fromModifyClientActions.fetchTheVersionConfigurationById(
    //     { versionId: configVersion.version, isPreview: true }));
    // }

    this._store.dispatch(fromModifyClientActions.fetchTheVersionConfigurationById(
      { versionId: configVersion.version, isPreview: true }));
  }

  onPreviousConfigurationVersion(configVersion: PostalConfigurationVersion): void {
    this._store.dispatch(fromModifyClientActions.revertConfiguration(
      { referenceVersion: configVersion.version }));
  }

  onCurrentVersion(configVersion: PostalConfigurationVersion): void {
    this._store.dispatch(fromModifyClientActions.fetchTheVersionConfigurationById({ versionId: configVersion.version, isPreview: false }));
  }
}
