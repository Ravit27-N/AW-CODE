import { Location } from '@angular/common';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { GrapeJsUtil } from '@cxm-smartflow/template/util';
import {
  appRoute,
  EMAILING,
  EMAILING_KEY,
  EMAILING_KEY_REGEX,
  EMAILING_VALUE,
  EMAILING_VALUE_REGEX,
  getVariableValue,
  SMS,
  SMS_KEY,
  SMS_KEY_REGEX,
  SMS_VALUE,
  SMS_VALUE_REGEX,
  SmsTemplate,
  TemplateConstant,
  TemplateModel,
  TemplateType
} from '@cxm-smartflow/shared/data-access/model';
import {
  CanModificationService,
  CanVisibilityService,
  SnackBarService
} from '@cxm-smartflow/shared/data-access/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import { catchError, exhaustMap, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { TemplateService } from '../../services/template.service';
import * as fromSelector from './template-form.selector';
import * as fromAction from './template-form.actions';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { HttpErrorResponse } from '@angular/common/http';
import { StringUtil } from '@cxm-smartflow/shared/utils';

@Injectable()
export class SmsTemplateEffect {
  messageProps: any;
  templateVariableLabel: any;

  transformVariableValue = (variables: string[], templateType: string): string [] => {
    if (templateType.toUpperCase() === TemplateType.SMS) {
      return variables.map(variable => {
        if (variable === SMS.key) {
          return SMS.value;
        } else {
          return variable;
        }
      });
    } else {
      return variables.map(variable => {
        if (variable === EMAILING.key) {
          return EMAILING.value;
        } else {
          return variable;
        }
      });
    }
  };

  transformHtmlContentWithVariableValue = (htmlContent: string, templateType: string): string => {
    if (templateType.toUpperCase() === TemplateType.SMS) {
      return StringUtil.replaceAllByRegex(htmlContent, SMS_KEY_REGEX, SMS_VALUE);
    } else {
      return StringUtil.replaceAllByRegex(htmlContent, EMAILING_KEY_REGEX, EMAILING_VALUE);
    }
  };

  hasPrivilege = (template: TemplateModel, mode: string): boolean => {
    if(template === null || template === undefined) return false;

    const module = template.templateType === TemplateType.SMS ? SmsTemplate.CXM_SMS_TEMPLATE : TemplateConstant.CXM_TEMPLATE;
    let feature: string;

    if(mode === 'edit'){
      feature = template.templateType === TemplateType.SMS ? SmsTemplate.EDIT : TemplateConstant.EDIT;
      return this.canVisibilityService.hasVisibility(module, feature, (template.ownerId || 0), true);
    }else if(mode === 'modified' || mode === 'modify'){
      feature = template.templateType === TemplateType.SMS ? SmsTemplate.MODIFY : TemplateConstant.MODIFY;
      return this.canModifyService.hasModify(module, feature, (template.ownerId || 0), true);
    }else {
      feature = template.templateType === TemplateType.SMS ? SmsTemplate.CREATE_BY_DUPLICATE : TemplateConstant.DUPLICATE;
      return this.canVisibilityService.hasVisibility(module, feature, (template.ownerId || 0), true);
    }
  };

  loadTemplateEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.loadTemplate),
      exhaustMap((args) => {
        // Edit email of sms template.
        if (args.mode === 'edit' || args.mode === 'modified' || args.mode === 'modify') {
          return this.templateService.getTemplateById(args.id).pipe(
            map((response: TemplateModel) => {
              // check user privilege.
              if(!this.hasPrivilege(response, args.mode)){
                return fromAction.userAccessDenied({error: '', templateType: args.templateType});
              }

              // Email template type.
              if (response.templateType === 'EMAILING') {
                // Populate email editor with assets saved from server
                GrapeJsUtil.removeGrapeJsProperties().subscribe(() =>
                  this.loadGraphEmailAssets(response)
                );
              }

              // SMS template type.
              const { modelName, variables, templateType, htmlFile, ownerId } = response as any;
              return fromAction.loadTemplateSuccess({
                modelName,
                vars: this.transformVariableValue(variables, templateType),
                mode: args.mode,
                templateType,
                id: args.id,
                htmlFile: this.transformHtmlContentWithVariableValue(htmlFile, templateType),
                ownerId: ownerId
              });
            }),
            catchError((err) => this.handleHttpError(args.templateType, err))
          );
        } else {
          // Create template form source or on duplicate.
          const { sourceTemplateId } = args;
          if (sourceTemplateId && sourceTemplateId !== '0') {
            return this.templateService
              .getTemplateById(parseInt(sourceTemplateId))
              .pipe(
                map((response: TemplateModel) => {
                  // check user privilege.
                  if(!this.hasPrivilege(response, 'duplicate')){
                    return fromAction.userAccessDenied({error: '', templateType: args.templateType});
                  }

                  if (response.templateType === 'EMAILING') {
                    GrapeJsUtil.removeGrapeJsProperties().subscribe(() =>
                      this.loadGraphEmailAssets(response)
                    );
                  }

                  const { variables, htmlFile } = response as any;
                  const form = {
                    modelName: args.modelName,
                    vars: this.transformVariableValue(variables, args.templateType),
                    mode: '',
                    htmlFile: this.transformHtmlContentWithVariableValue(htmlFile, args.templateType),
                    templateType: args.templateType,
                    id: 0,
                    source: sourceTemplateId
                  };
                  return fromAction.loadTemplateSuccess(form);
                }),
                catchError((err) => this.handleHttpError(args.templateType, err))
              );
          } else {
            const form = {
              modelName: args.modelName,
              vars: getVariableValue(args.templateType),
              template: '',
              source: 0,
              mode: '',
              templateType: args.templateType,
              id: 0
            };
            return of(fromAction.loadTemplateSuccess(form));
          }
        }
      })
    )
  );

  userAccessDenied$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.userAccessDenied),
    tap((args) => {
      const contextPath = args.templateType === TemplateType.SMS ? 'sms' : 'email';
      this.confirmMessageService
        .showConfirmationPopup({
          icon: 'feedback',
          heading: this.messageProps.unauthorize,
          message: this.messageProps.unauthorize,
          cancelButton: this.messageProps.unauthorizeCancel,
          confirmButton: this.messageProps.unauthorizeLeave,
          type: 'Warning'
        })
        .subscribe(() => {
            this.router.navigateByUrl(`cxm-template/design-model/feature-list-email-template/${contextPath}`);
          }
        );
    })
  ), { dispatch: false });

  transformVariableKey = (variables: string[], templateType: string): string [] => {
    if (templateType.toUpperCase() === TemplateType.SMS) {
      return variables.map(variable => {
        if (variable === SMS.value) {
          return SMS.key;
        } else return variable;
      });
    } else {
      return variables.map(variable => {
        if (variable === EMAILING.value) {
          return EMAILING.key;
        } else return variable;
      });
    }
  };

  transformHtmlContentWithVariableKey = (htmlContent: string, templateType: string): string => {
    if (templateType.toUpperCase() === TemplateType.SMS) {
      return StringUtil.replaceAllByRegex(htmlContent, SMS_VALUE_REGEX, SMS_KEY);
    } else {
      return StringUtil.replaceAllByRegex(htmlContent, EMAILING_VALUE_REGEX, EMAILING_KEY);
    }
  };

  submitFormEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.submitFormVar),
      withLatestFrom(this.store.select(fromSelector.selectTemplateFormState)),
      exhaustMap(([args, form]) => {
        const { modelName, htmlFile, templateType, mode, id, source, ownerId } = form;

        const htmlStorage = localStorage.getItem('gjs-html') || localStorage.getItem('gjs-html-saved') || '';
        const htmlFileStorage = localStorage.getItem('htmlFile') || localStorage.getItem('gjs-html-saved') || '';

        // FIXME: Hack to resolve bcColor HTML attribute does not support RGB color in modern browser.
        const rgb2hex = (rgb: any) => `#${rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/).slice(1)
          .map((n: any) => parseInt(n, 10).toString(16).padStart(2, '0')).join('')}`;
        const toHex = (content: string) => {
          const value = content.match(/(")+([^"])+(")/g)?.map((e: string) => e?.slice(1, -1))[0] || '';
          return `bgcolor="${rgb2hex(value)}"`;
        }
        const remove = (dom: string) => {
          let DOM = dom;
          const bgColorPattern = /(bgcolor="rgb)+([^"])+(")/g;
          const bgColors = DOM.match(bgColorPattern) || [];
          bgColors.forEach(e => {
            DOM = StringUtil.replaceAll(DOM, e, toHex(e));
          });

          return DOM;
        }
        // FIXME end.

        const htmlContent = templateType === TemplateType.SMS ?
          this.transformHtmlContentWithVariableKey(htmlFile, TemplateType.SMS) :
          this.transformHtmlContentWithVariableKey((htmlStorage || ''), TemplateType.EMAILING);

        const htmlFileContent = templateType === TemplateType.SMS ? htmlContent :
          this.transformHtmlContentWithVariableKey((htmlFileStorage || ''), TemplateType.EMAILING);

        let payload = {
          id,
          modelName,
          variables: this.transformVariableKey(args?.variables || [], templateType),
          templateType,
          source: source,
          active: true,
          html: htmlContent,
          htmlFile: htmlFileContent,
          width: '500', // default mm
          height: templateType === TemplateType.SMS ? '520' : '700', // default mm
          ownerId: ownerId
        };

        if (templateType === 'EMAILING') {
          const data = this.getEmailEditorPayload(htmlFileStorage);
          payload = Object.assign(payload, data);
          payload = {
            ...payload,
            html: remove(payload.html),
            htmlFile: remove(payload.htmlFile),
          };
        }

        // modified template
        if (mode === 'modified') {
          return this.templateService.updateEmailTemplate(payload).pipe(
            map((res) =>
              fromAction.submitFormVarSuccess({ mode: 'modified', result: res })
            ),
            catchError(() =>
              of(fromAction.submitFormVarFail({ mode: 'modified' }))
            )
          );
        }

        // create template
        return this.templateService.createEmailTemplate(payload).pipe(
          map((res) =>
            fromAction.submitFormVarSuccess({ mode: 'create', result: res })
          ),
          catchError(() => of(fromAction.submitFormVarFail({ mode: 'create' })))
        );
      })
    )
  );

  submitFormSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.submitFormVarSuccess),
        tap((args) => {
          const template = args.result;
          template.templateType;

          if (args.mode === 'create') {
            this.router.navigate([
              appRoute.cxmTemplate.template.successPage,
              template.templateType,
              template.id
            ]);
          } else {
            this.router.navigate([
              appRoute.cxmTemplate.template.successPage,
              template.templateType,
              template.id
            ]);
          }
        })
      ),
    { dispatch: false }
  );

  submitFormFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.submitFormVarFail),
        tap((args) => {
          if (args.mode === 'create') {
            this.snackBar.openCustomSnackbar({ message: this.messageProps?.createError, icon: 'close', type: 'error' });
          } else {
            this.snackBar.openCustomSnackbar({ message: this.messageProps?.updateError, icon: 'close', type: 'error' });
          }
        })
      ),
    { dispatch: false }
  );

  navigateToList = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.navigateToList),
        withLatestFrom(this.store.select(fromSelector.selectTemplateType)),
        tap(([args, templateType]) => {
          if (templateType === 'SMS') {
            this.router.navigate([
              appRoute.cxmTemplate.template.listSMSTemplate
            ]);
          } else {
            this.router.navigate([
              appRoute.cxmTemplate.template.listEmailTemplate
            ]);
          }
        })
      ),
    { dispatch: false }
  );

  loadGraphJsAssets$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.loadGraphJsAssets),
      exhaustMap((args) =>
        this.templateService.loadAllTemplateCompositions().pipe(
          catchError((err) =>
            of(fromAction.submitFormVarFail({ mode: 'create' }))
          ),
          map((res) => fromAction.loadGraphJsAssetsSuccess({ assets: res }))
        )
      )
    )
  );

  formHasChangedTrackEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(
        fromAction.addFormVar,
        fromAction.removeFormVar,
        fromAction.updateModelNameFormVar,
        fromAction.modifyVariableForm,
        fromAction.modifyEmailEditor,
        fromAction.changeSmsTemplateText
      ),
      switchMap((args) => of(fromAction.setSMSFormHasChange({ changed: true })))
    )
  );

  loadTemplateFailEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.loadTemplateFail),
        tap((args) => {
          const { templateType, error } = args;
          const pathByTemplateType = templateType === 'EMAILING' ? 'email' : 'sms';

          if (error) {
            if (error.statusCode === 403 || error.statusCode === 401) {
              this.confirmMessageService
                .showConfirmationPopup({
                  icon: 'feedback',
                  heading: this.messageProps.unauthorize,
                  message: this.messageProps.unauthorizeAccess,
                  cancelButton: this.messageProps.unauthorizeCancel,
                  confirmButton: this.messageProps.unauthorizeLeave,
                  type: 'Warning'
                })
                .subscribe(() =>
                  this.router.navigateByUrl(
                    `cxm-template/design-model/feature-list-email-template/${pathByTemplateType}`
                  )
                );
            } else if (error.statusCode === 404) {
              this.confirmMessageService
                .showConfirmationPopup({
                  icon: 'feedback',
                  heading: this.messageProps.notFoundTemplateTitle,
                  message: this.messageProps.notFoundMessage,
                  cancelButton: this.messageProps.unauthorizeCancel,
                  confirmButton: this.messageProps.unauthorizeLeave,
                  type: 'Warning'
                })
                .subscribe(() =>
                  this.router.navigateByUrl(
                    `cxm-template/design-model/feature-list-email-template/${pathByTemplateType}`
                  )
                );
            } else if (error.statusCode >= 500) {
              this.snackBar.openCustomSnackbar({
                message: this.messageProps?.unknownError,
                type: 'error',
                icon: 'close'
              });
            }
          }
        })
      ),
    { dispatch: false }
  );

  createTemplateByDuplicate$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.createTemplateByDuplicate),
        tap((args) => {
          let copyWord = 'copie';
          this.translate
            .get('template.words.copy')
            .subscribe((v) => (copyWord = v));

          if (args.template?.templateType === 'SMS') {
            this.router.navigate(
              [`${appRoute.cxmTemplate.template.smsTemplateComposition}`],
              {
                queryParams: {
                  modelName: copyWord.concat('_').concat(args.template?.modelName),
                  modelType: args.template?.templateType,
                  sourceTemplateId: args.template?.id,
                  mode: 'copy'
                }
              }
            );
          } else {
            this.router.navigate(
              [`${appRoute.cxmTemplate.template.emailTemplateComposition}`],
              {
                queryParams: {
                  modelName: copyWord.concat('_').concat(args.template?.modelName),
                  modelType: args.template?.templateType,
                  sourceTemplateId: args.template?.id,
                  mode: 'copy'
                }
              }
            );
          }
        })
      ),
    { dispatch: false }
  );

  editTemplate$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.editTemplate),
        tap((args) => {
          if (args.template?.templateType === 'SMS') {
            this.router.navigate(
              [
                `${appRoute.cxmTemplate.template.smsTemplateComposition}/${args.template?.id}`
              ],
              {
                queryParams: {
                  mode: 'edit',
                  modelName: args.template?.modelName,
                  modelType: args.template.templateType
                }
              }
            );
          } else {
            this.router.navigate(
              [
                `${appRoute.cxmTemplate.template.emailTemplateComposition}/${args.template?.id}`
              ],
              {
                queryParams: {
                  mode: 'edit',
                  modelName: args.template?.modelName,
                  modelType: args.template?.templateType
                }
              }
            );
          }
        })
      ),
    { dispatch: false }
  );

  modifiedTemplate$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.modifiedTemplate),
        tap((args) => {
          if (args.template?.templateType === 'SMS') {
            this.router.navigate(
              [
                `${appRoute.cxmTemplate.template.smsTemplateComposition}/${args.template?.id}`
              ],
              {
                queryParams: {
                  mode: 'modified',
                  modelName: args.template?.modelName,
                  modelType: args.template.templateType
                }
              }
            );
          } else {
            this.router.navigate(
              [
                `${appRoute.cxmTemplate.template.emailTemplateComposition}/${args.template?.id}`
              ],
              {
                queryParams: {
                  mode: 'modified',
                  modelName: args.template?.modelName,
                  modelType: args.template?.templateType
                }
              }
            );
          }
        })
      ),
    { dispatch: false }
  );

  fetchDefaultVarEffect$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.fetchTemplateDefaultVars),
    exhaustMap(() => this.templateService.getDefaultVariable().pipe(
      map((res) => fromAction.setTemplateDefaultVars({ defaultVars: res }))
    ))
  ));

  getEmailEditorPayload = (htmlFile: any) => {
    return {
      html: this.transformHtmlContentWithVariableKey(localStorage.getItem('gjs-html')?.toString() || '', TemplateType.EMAILING),
      css: localStorage.getItem('gjs-css')?.toString(),
      styles: localStorage.getItem('gjs-styles')?.toString(),
      assets: localStorage.getItem('gjs-assets'),
      components: localStorage.getItem('gjs-components'),
      htmlFile: this.transformHtmlContentWithVariableKey(htmlFile, TemplateType.EMAILING)
    };
  };


  loadGraphEmailAssets(res: any) {
    localStorage.setItem('gjs-html', this.transformHtmlContentWithVariableValue(res?.html, TemplateType.EMAILING));
    localStorage.setItem('gjs-css', res?.css);
    localStorage.setItem('gjs-styles', res?.styles);
    localStorage.setItem('gjs-assets', res?.assets);
    localStorage.setItem('gjs-components', res?.components);

    //Just keep htmlFile to validate when grapeJs change.
    localStorage.setItem('gjs-html-saved', this.transformHtmlContentWithVariableValue(res?.htmlFile, TemplateType.EMAILING));
  }

  handleHttpError = (templateType: string, httpError: HttpErrorResponse) => {
    const { apierrorhandler } = httpError.error;
    if (apierrorhandler) {
      return of(fromAction.loadTemplateFail({ templateType, error: apierrorhandler }));
    }
    return of(fromAction.loadTemplateFail({ templateType, error: null }));
  };

  constructor(
    private actions: Actions,
    private templateService: TemplateService,
    private store: Store,
    private router: Router,
    private location: Location,
    private snackBar: SnackBarService,
    private translate: TranslateService,
    private confirmMessageService: ConfirmationMessageService,
    private canModifyService: CanModificationService,
    private canVisibilityService: CanVisibilityService
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate.get('template.message').subscribe(message => this.messageProps = message);
    this.translate.get('template.template-variable').subscribe(label => this.templateVariableLabel = label);
  }
}
