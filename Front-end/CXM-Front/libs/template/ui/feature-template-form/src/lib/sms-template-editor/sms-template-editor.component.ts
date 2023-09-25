import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatMenuTrigger } from '@angular/material/menu';
import {
  changeSmsTemplateText, fetchSmsTemplateForm,
  modelNameChangeEvent,
  navigateToList,
  selectHtmlFile,
  selectTemplateVariables,
  TemplateService
} from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { BehaviorSubject, pipe, ReplaySubject } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  take,
  takeUntil,
} from 'rxjs/operators';
import { ActivatedRoute } from '@angular/router';
import {
  getVariableTemp,
  keepVariableTemp,
  removeVariableTemp,
} from '@cxm-smartflow/template/util';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'cxm-smartflow-sms-template-editor',
  templateUrl: './sms-template-editor.component.html',
  styleUrls: ['./sms-template-editor.component.scss'],
})
export class SmsTemplateEditorComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;
  destroyed$ = new ReplaySubject(1);
  vars: string[] = [];

  topLeftPos = { x: 0, y: 0 };
  cursor = { start: 0, end: 0, text: '', submit: false };

  @ViewChild(MatMenuTrigger, { static: true }) menuTrigger: MatMenuTrigger;
  @ViewChild('texteditor') texteditor: ElementRef<HTMLInputElement>;
  isPreview: boolean;
  isReadonly = false;
  isEditable = false;

  isModelNameDuplicated$ = new BehaviorSubject(false);
  templateId = 0;
  formType$ = new BehaviorSubject('');
  showTooltip$ = new BehaviorSubject(false);

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private templateService: TemplateService,
    private translate: TranslateService
  ) {
    this.formGroup = new FormGroup({
      modelName: new FormControl('', [
        Validators.required,
        Validators.maxLength(128),
      ]),
      body: new FormControl([]),
    });

    this.store
      .select(selectTemplateVariables)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((variables) => {
        keepVariableTemp(variables?.filter((x: string) => x.length > 0));
      });

    this.activatedRoute.queryParams.pipe(take(1)).subscribe((query) => {
      this.formType$.next(query?.mode || '');
    });

    const { id } = this.activatedRoute.snapshot.params;
    this.templateId = id;
  }

  ngOnInit(): void {
    // validate form.
    if (this.formType$.value === 'edit') {
      this.formGroup.disable();
      this.doPreview(true);
    }

    // Init body.
    this.store
      .select(selectHtmlFile)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((htmlFile) => this.formGroup.get('body')?.setValue(htmlFile));

    // Init modelName by queryParamMap.
    this.modelName?.setValue(
      this.activatedRoute.snapshot.queryParamMap.get('modelName')
    );

    // validate unique name for create duplicate form.
    if (this.formType$.value === 'copy') {
      this.validateModelName(this.modelName?.value, this.templateId);
    }

    // validate model name by event.
    if (this.formType$.value !== 'edit') {
      this.formGroup
        .get('modelName')
        ?.valueChanges?.pipe(
          pipe(
            distinctUntilChanged(),
            debounceTime(100),
            takeUntil(this.destroyed$)
          )
        )
        .subscribe((value) => {
          this.validateModelName(value, this.templateId);
        });
    }
  }

  validateModelName(modelName?: string, templateId?: number) {
    this.templateService
      .validationModelName(<string>modelName, 'SMS', <number>templateId)
      .subscribe((duplicate) => {
        this.isModelNameDuplicated$.next(duplicate);
        this.store.dispatch(
          modelNameChangeEvent({
            value: <string>modelName,
            isDuplicate: duplicate,
            isRequired: (modelName as string)?.length <= 0,
            isMaxLength: (modelName as string)?.length > 128,
            formHasChanged: true,
          })
        );

        // validation to show tooltip.
        const isFormValid =
          !this.maxLength && !this.duplicated && !this.required;
        this.showTooltip$.next(!isFormValid);
      });
  }

  get maxLength() {
    return this.modelName?.errors?.maxlength;
  }

  get required() {
    return this.modelName?.errors?.required;
  }

  get duplicated() {
    return this.isModelNameDuplicated$.value;
  }

  get tooltipMessage() {
    let value = '';
    if (this.required) {
      this.translate
        ?.get('template.popup.errors.modelNameRequired')
        ?.subscribe((v) => (value = v));
    } else if (this.duplicated) {
      this.translate
        ?.get('template.popup.errors.duplicatedModelName')
        ?.subscribe((v) => (value = v));
    } else if (this.maxLength) {
      this.translate
        ?.get('template.popup.errors.maxLength')
        ?.subscribe((v) => (value = v));
    }
    return value;
  }

  get modelName() {
    return this.formGroup?.get('modelName');
  }

  textChanged(event: any) {
    // @smsSendingLimitSize refers to the amount of characters that SMS campaign can send.
    const smsSendingLimitSize = 612;
    this.isEditable = event.target.value.length > smsSendingLimitSize;
    this.store.dispatch(fetchSmsTemplateForm({ smsTextField: event.target.value }));

    if (event.target.value.length > smsSendingLimitSize) {
      return;
    }

    this.store.dispatch(changeSmsTemplateText({ value: event.target.value }));
  }

  onRightClick(mouseEvent: any) {
    getVariableTemp().subscribe((variables) => {
      this.vars = variables?.filter((x: string) => x.length > 0);
    });

    mouseEvent.preventDefault();
    this.topLeftPos.x = mouseEvent.clientX;
    this.topLeftPos.y = mouseEvent.clientY + 15;

    this.cursor.start = this.texteditor.nativeElement.selectionStart || 0;
    this.cursor.end = this.texteditor.nativeElement.selectionEnd || 0;
    this.cursor.text = this.texteditor.nativeElement.value;
    this.cursor.submit = false;

    this.menuTrigger.openMenu();
  }

  insertAfterCursor(insertValue: string) {
    const textvalue =
      this.cursor.text.substring(0, this.cursor.start) +
      `{${insertValue}}` +
      this.cursor.text.substring(this.cursor.end, this.cursor.text.length);

    this.texteditor.nativeElement.value = textvalue;
  }

  submitInsertAfterCusor(insertValue: string) {
    this.insertAfterCursor(insertValue);
    this.texteditor.nativeElement.dispatchEvent(new Event('change'));
    this.cursor.submit = true;
  }

  closeMenuContext() {
    if (this.cursor.submit === false) {
      this.texteditor.nativeElement.value = this.cursor.text;
      this.texteditor.nativeElement.dispatchEvent(new Event('change'));
    }
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
    this.isModelNameDuplicated$.complete();
    this.formType$.complete();
    this.store.complete();
    removeVariableTemp();
  }

  doPreview(active: boolean) {
    this.isPreview = active;
  }

  cancel() {
    this.store.dispatch(navigateToList());
  }
}
