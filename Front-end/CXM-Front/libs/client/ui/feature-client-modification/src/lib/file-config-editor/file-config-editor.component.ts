import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ConfigurationForm, contentToConfiguration, immutableConfigurations } from '@cxm-smartflow/client/data-access';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { BehaviorSubject, Subscription } from 'rxjs';
import { ConfigEditorMode } from './file-config-editor.service';

@Component({
  selector: 'cxm-smartflow-file-config-editor',
  templateUrl: './file-config-editor.component.html',
  styleUrls: ['./file-config-editor.component.scss'],
})
export class FileConfigEditorComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  error$ = new BehaviorSubject<boolean>(false);
  errorMsg$ = new BehaviorSubject<string>('');
  private readonly MODEL_PROPERTY = 'Modele=';
  private readonly IMUTABLE_MODELS = ["DEFAULT", "PORTAIL", "PORTAIL_ANALYSE", "PORTAIL_PREVIEW"];
  private unsubscription$: Subscription;

  btnConfirmEnable = true;
  constructor(
    @Inject(MAT_DIALOG_DATA) public config: { data: ConfigurationForm, modelNames: string[], editMode: ConfigEditorMode },
    private _dialogRef: MatDialogRef<FileConfigEditorComponent>,
    private _fb: FormBuilder
  ) {
    this.btnConfirmEnable = config.editMode !== ConfigEditorMode.VIEW;
  }

  ngOnInit(): void {
    this.formGroup = this._fb.group({
      content: new FormControl(this.config.data.content),
    });

    this.unsubscription$ = this.formGroup.valueChanges.subscribe(() => this.error$.next(false));
  }

  ngOnDestroy() {
    this.formGroup.reset();
    this.unsubscription$.unsubscribe();
  }

  closeModal(): void {
    this._dialogRef.close(false);
  }

  confirm(): void {
    const config = this.getConfig();

    if (config) {
      this._dialogRef.close(config);
    }
  }

  getConfig(): ConfigurationForm | boolean {
    if (this.checkInvalidForm()) {
      this.error$.next(true);
      return false;
    }

    const { content } = this.formGroup.getRawValue();
    return contentToConfiguration({ ...this.config.data, content }, true).configurations;
  }

  private checkInvalidForm(): boolean {
    this.config.data.content = this.formGroup.getRawValue().content;
    const { configurations, hasSection } = contentToConfiguration({ ...this.config.data }, false);
    if (immutableConfigurations.some(n => n === this.config.data.name) && configurations.name !== this.config.data.name && this.config.data.order < 5) {
        this.errorMsg$.next('client.configuration_popup_modele_cannot_override');
        return true;
    }

    if (!configurations.name) {
        this.errorMsg$.next('client.configuration_popup_required_model')
        return true;
    }

    if (!this.config.data.content) {
      this.errorMsg$.next('client.configuration_popup_required_model_name');
      return true;
    }

    if (this.config.modelNames.some(name => configurations.name === name) && configurations.name !== this.config.data.name) {
      this.errorMsg$.next('client.configuration_popup_modele_existed');
      return true;
    }

    return false;
  }

  focusEditor(editor: HTMLTextAreaElement) {
    editor.scrollTop = 0;
    editor.setSelectionRange(0, 0, 'forward');
    editor.focus();
  }
}
