/* eslint-disable @typescript-eslint/ban-ts-comment */
// @ts-ignore
import * as grapesjs from 'grapesjs';
import 'grapesjs-preset-newsletter';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  fetchTemplateDefaultVars,
  GrapeJsAsset,
  initFormChange,
  loadEmailTemplateHTML,
  loadGraphJsAssets,
  modelNameChangeEvent,
  navigateToList,
  selectedGraphAssets,
  selectEmailTemplateDefaultVar,
  selectHtmlFile,
  selectTemplateForm,
  selectTemplateVariables,
  TemplateConstant,
  TemplateService
} from '@cxm-smartflow/template/data-access';
import { Store } from '@ngrx/store';
import { pluck, take, takeUntil, withLatestFrom } from 'rxjs/operators';
import { BehaviorSubject, pipe, ReplaySubject, Subject } from 'rxjs';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { getVariableTemp, keepVariableTemp, removeVariableTemp } from '@cxm-smartflow/template/util';
import { TranslateService } from '@ngx-translate/core';
import { CanModificationService, SnackBarService } from '@cxm-smartflow/shared/data-access/services';

@Component({
  selector: 'cxm-smartflow-email-template-editor',
  templateUrl: './email-template-editor.component.html',
  styleUrls: ['./email-template-editor.component.scss']
})
export class EmailTemplateEditorComponent implements OnInit, OnDestroy {
  editor: any;
  widthGrapeJs = 503.2375; // millimeter
  heightGrapeJs = 1024; // millimeter
  previewHTML = '';
  isPreview = false;
  isReadonly = false;
  private editTime = 0;

  destroyed$ = new ReplaySubject<boolean>(1);
  formGroup: FormGroup;
  isModelNameDuplicated$ = new BehaviorSubject(false);
  destroy$ = new Subject<boolean>();
  templateId$ = new BehaviorSubject(0);
  formType$ = new BehaviorSubject('');
  sourceTemplateId$ = new BehaviorSubject(0);
  showTooltip$ = new BehaviorSubject(false);
  grapeJsRichTextBoxLabel: any;
  unauthorized: any;

  constructor(
    private activatedRoute: ActivatedRoute,
    private store: Store,
    private templateService: TemplateService,
    private formBuilder: FormBuilder,
    private translate: TranslateService,
    private modificationService: CanModificationService,
    private snackBarService: SnackBarService
  ) {
    this.formGroup = this.formBuilder.group({
      modelName: new FormControl('', [
        Validators.required,
        Validators.maxLength(128)
      ])
    });

    this.activatedRoute.queryParams.pipe(take(1)).subscribe((query) => {
      this.formType$.next(query?.mode || '');
      this.sourceTemplateId$.next(<number>query?.sourceTemplateId);
    });

    const { id } = this.activatedRoute.snapshot.params;
    this.templateId$.next(<number>id);
  }

  ngOnInit(): void {
    this.translate.get('cxmTemplate.emailingTemplate.create.parameter.grapeJs')?.subscribe(translate => this.grapeJsRichTextBoxLabel = translate);
    this.translate.get('template.message')?.subscribe(translate => this.unauthorized = translate);

    this.setup();

    // Validate form.
    if (this.formType$.value === 'edit') {
      this.formGroup.disable();
      this.doPreview(true);
    }

    // keep variable to storage.
    this.store
      .select(selectTemplateVariables)
      .pipe(takeUntil(this.destroy$))
      .subscribe((variables) => keepVariableTemp(variables));

    // Init variable to richTextEditor.
    this.store
      .select(selectTemplateForm)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        getVariableTemp().pipe(withLatestFrom(this.store.select(selectEmailTemplateDefaultVar))).toPromise().then(([variableTemp, defaultVars]) => {
          if (variableTemp) {

            let customOptions: any = Array.from(variableTemp).filter(x => !!x).map(
              (item) => `<option value='{${item}}'>` + item + `</option>`
            );

            customOptions.unshift(`<option value=''>-select-</option>`);
            customOptions.push(`<option value='{${defaultVars[1]}}'>${this.grapeJsRichTextBoxLabel?.unSubscribePage}</option>`);
            customOptions.push(`<option value='{${defaultVars[0]}}'>${this.grapeJsRichTextBoxLabel?.mirrorPage}</option>`);

            customOptions = customOptions.reduce((prev: any, nex: any) => prev + nex, '');
            if (this.editor) {
              const customVars = this.editor.RichTextEditor.get('custom-vars');

              if (customVars) {

                customVars.btn.children[0].innerHTML = '';
                customVars.btn.children[0].innerHTML = customOptions;
              } else {
                // customOptions = customOptions.reduce((prev: any, nex: any) => prev + nex , '');
                this.editor.RichTextEditor.add('custom-vars', {
                  icon:
                    `<select id='custom-vars-select' class='gjs-field' style='background-color: #D9D9D9; color: black;'>` +
                    customOptions +
                    `</select>`,
                  // Bind the 'result' on 'change' listener.
                  event: 'change',
                  result: (rte: any, action: any) => {
                    rte.insertHTML(action.btn.firstChild.value);
                  },

                  // Reset the select on change
                  update: (rte: any, action: any) => {
                    action.btn.firstChild.value = '';
                  }
                });
              }
            }
          }
        });
      });

    // Init modelName by queryParamMap.
    this.modelName?.setValue(
      this.activatedRoute.snapshot.queryParamMap.get('modelName')
    );

    // validate unique name for create duplicate form.
    if (this.formType$.value === 'copy') {
      this.validateModelName(this.modelName?.value, this.templateId$.value);
    }

    // validate model name by event.
    if (this.formType$.value !== 'edit') {
      this.modelName?.valueChanges
        ?.pipe(
          pipe(takeUntil(this.destroy$))
        )
        .subscribe((value) => {
          this.validateModelName(value, this.templateId$.value);
        });
    }
  }

  validateModelName(modelName?: string, templateId?: number) {
    this.templateService
      .validationModelName(<string>modelName, 'EMAILING', <number>templateId)
      .subscribe((duplicate) => {
        this.isModelNameDuplicated$.next(duplicate);
        this.store.dispatch(
          modelNameChangeEvent({
            value: <string>modelName,
            isDuplicate: duplicate,
            isRequired: (modelName as string)?.length <= 0,
            isMaxLength: (modelName as string)?.length > 128,
            formHasChanged: true
          })
        );

        // validation to show tooltip.
        const isFormValid =
          !this.maxLength && !this.duplicated && !this.required;
        this.showTooltip$.next(!isFormValid);
      });
  }

  setup() {
    this.store.dispatch(fetchTemplateDefaultVars());

    this.editor = this.setupGrapes('#gjs', false);

    const onRemoveAsset = (assetId: number) => {
      return this.templateService.deleteAssetOfGrapeJs(assetId);
    };

    const onRemoveAssetFail = () => {
      this.snackBarService.openCustomSnackbar({ message: this.unauthorized?.removeAssetFail, icon: 'close', type: 'error' });
    }

    const showUnauthorized = () => {
      this.snackBarService.openCustomSnackbar({ message: this.unauthorized?.unauthorizedRemoveAsset, icon: 'close', type: 'error' });
    };

    if (this.editor) {
      // override function of remove asset from AssetManager of GrapeJs.
      this.editor.AssetManager.addType('image', {
        view: {
          onRemove(e: any) {
            e.stopPropagation();
            const { model } = this as any;
            const asset = model?.attributes as GrapeJsAsset;
            if (asset.canModify && asset.assetId) {
              onRemoveAsset(asset.assetId).subscribe(() => {
                model.collection.remove(model);
              }, () => {
                onRemoveAssetFail();
              });
            } else {
              showUnauthorized();
            }
          }
        }
      });
    }

    this.editor.on('load', () => {
      this.activatedRoute.queryParams
        .pipe(take(1), pluck('mode'))
        .subscribe((v) => {
          // if (v != 'edit' || v != 0) {
          //   localStorage.setItem('htmlFile', '');
          // }
          this.editor.setComponents(localStorage.getItem('gjs-html'));
          this.editor.setStyle(localStorage.getItem('gjs-css'));
          // this.setupGjsCSS();
        });

      this.addToolsToGrapesJs();
      this.addCommandsForToolsOfGrapesJs();
    });

    this.editor.on('update', () => {

      if (this.formType$.value === 'modified') {
        this.editTime += 1;
        if (this.editTime > 2) this.prepareDataToSubmit();
      } else {
        this.prepareDataToSubmit();
      }

      this.store.dispatch(loadEmailTemplateHTML({ emailTemplateHTML: this.getEditorHtmlCss()}));
    });

    this.store
      .select(selectedGraphAssets)
      .pipe(takeUntil(this.destroyed$))
      .subscribe((assets) => this.addListAssetToAssetManager(assets));
    this.store.dispatch(loadGraphJsAssets());
  }

  doPreview(active: boolean) {
    this.store
      .select(selectHtmlFile)
      .subscribe((htmlFile) => this.previewHTML = htmlFile);
    this.isPreview = active;
  }

  setupGrapes(container: string, fromElement: boolean = true): any {
    return grapesjs.init({
      // dragMode: 'absolute',
      container: container,
      fromElement: fromElement,
      // width: '1001px',
      // height: '560px',
      height: 'calc(100vh - 80px - 64px - 64px)',
      // height: '624px',
      plugins: ['gjs-preset-newsletter'],
      forceClass: true,
      pluginsOpts: {
        'gjs-preset-newsletter': {
          modalTitleImport: 'Import template'
        }
      },
      colorPicker: {
        appendTo: 'parent',
        offset: { top: 26, left: -166 }
      },
      deviceManager: {
        devices: [
          {
            name: 'Desktop',
            width: '',
            priority: 3
          },
          {
            name: 'Tablet',
            width: '768px',
            priority: 2
          },
          {
            name: 'Mobile',
            width: '360px',
            priority: 1
          }
        ]
      },
      assetManager: {
        storageType: '',
        storeOnChange: true,
        storeAfterUpload: true,
        multiple: true,
        uploadFile: (e: any) => {
          const files = e.dataTransfer ? e.dataTransfer.files : e.target.files;
          if (files) {
            this.templateService.uploadAssetsOfGrapeJs(files[0]).subscribe(
              (res) => {
                if (res?.body) {
                  this.addAssetToAssetManager(res?.body as GrapeJsAsset);
                }
              }
            );
          }
        },
        // upload image url to server
        handleAdd: (e: any) => {
          this.templateService.saveAssetUrl({ imageUrl: e }).subscribe(
            (response: GrapeJsAsset) => {
              this.addAssetToAssetManager(response);
            }
          );
        }
      }
    });
  }

  private canRemoveAsset(ownerId: number): boolean {
    return this.modificationService.hasModify(TemplateConstant.CXM_TEMPLATE, TemplateConstant.MODIFY, ownerId, true);
  }

  addListAssetToAssetManager(assets: GrapeJsAsset[]): void {
    const finalAssets = assets.map(asset => {
      return {
        ...asset,
        canModify: this.canRemoveAsset(asset.ownerId || 0)
      };
    });
    this.editor.AssetManager.add(finalAssets);
  }

  addAssetToAssetManager(asset: GrapeJsAsset): void {
    const finalAsset: GrapeJsAsset = {
      ...asset,
      canModify: this.canRemoveAsset(asset.ownerId || 0)
    };
    this.editor.AssetManager.add(finalAsset);
  }

  addToolsToGrapesJs(): void {
    this.editor.Panels.addButton('options', [
      {
        id: 'undo',
        className: 'fa fa-undo',
        command: 'undo',
        attributes: { title: 'Undo' }
      },
      {
        id: 'redo',
        className: 'fa fa-repeat',
        command: 'redo',
        attributes: { title: 'Redo' }
      }
    ]);
  }

  addCommandsForToolsOfGrapesJs(): void {
    const cmdm = this.editor.Commands;
    cmdm.add('undo', {
      run: (editor: any, sender: any) => {
        sender.set('active', false);
        editor.UndoManager.undo(1);
      }
    });

    cmdm.add('redo', {
      run: (editor: any, sender: any) => {
        sender.set('active', false);
        editor.UndoManager.redo(1);
      }
    });

    cmdm.add('set-device-desktop', {
      run: (editor: any) => {
        editor.setDevice('Desktop');
        this.widthGrapeJs = 503.2375; // millimeter
      }
    });
    cmdm.add('set-device-tablet', {
      run: (editor: any) => {
        editor.setDevice('Tablet');
        this.widthGrapeJs = 203.2; // millimeter
      }
    });
    cmdm.add('set-device-mobile', {
      run: (editor: any) => {
        editor.setDevice('Mobile');
        this.widthGrapeJs = 84.666666667; // millimeter
      }
    });
  }

  prepareDataToSubmit() {
    if (this.editor) {
      const htmlFile = this.getEditorHtmlCss();
      // validate html file change.
      this.validateHtmlFileChange(htmlFile);

      localStorage.setItem('htmlFile', htmlFile);
    }
  }

  getEditorHtmlCss(): string {
    if (this.editor) {
      const htmlWithCss = this.editor.runCommand('gjs-get-inlined-html');
      const parser = new DOMParser();
      const htmlDoc = parser.parseFromString(htmlWithCss, 'text/html');
      const htmlFile = htmlDoc.documentElement.innerHTML;
      localStorage.setItem('htmlFile', htmlFile);
      return htmlFile;
    }
    return '';
  }

  validateHtmlFileChange(newHtmlFile: string) {
    const oldHtmlFile: string = <string>localStorage.getItem('gjs-html-saved');

    if ((newHtmlFile?.length !== oldHtmlFile?.length) && this.formType$.value !== 'edit') {
      this.store.dispatch(initFormChange({ hasChange: true }));
    }
  }

  ngOnDestroy(): void {
    this.showTooltip$.complete();
    this.destroyed$.next(true);
    this.destroyed$.complete();
    this.isModelNameDuplicated$.complete();
    this.templateId$.complete();
    this.formType$.complete();
    this.sourceTemplateId$.complete();
    this.store.complete();
    this.editTime = 0;
    removeVariableTemp();
  }

  get modelName() {
    return this.formGroup?.get('modelName');
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

  cancel() {
    this.store.dispatch(navigateToList());
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
}
