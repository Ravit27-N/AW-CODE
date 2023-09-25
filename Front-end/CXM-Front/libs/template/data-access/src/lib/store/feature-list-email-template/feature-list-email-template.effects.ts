import { Inject, Injectable } from '@angular/core';
import {
  CanAccessibilityService,
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
import * as fromAction$ from './feature-list-email-template.actions';
import * as fromSelector from './feature-list-email-template.selectors';
import * as fromDeleteEmail from '../delete-email-template';
import {
  CampaignConstant,
  EntityResponseHandler, SmsTemplate, TemplateConstant,
  TemplateList,
  TemplateModel,
  TemplatePrivilegeModel, TemplateyType
} from '@cxm-smartflow/shared/data-access/model';
import { templateEnv as cxmTemplateEnvironment } from '@env-cxm-template';
import { ConfigurationService } from '@cxm-smartflow/shared/data-access/api';
import { IAppSettings } from '@cxm-smartflow/shared/app-config';

@Injectable()
export class FeatureListEmailTemplateEffect {

  settings: IAppSettings;

  constructor(
    private templateService: TemplateService,
    private actions$: Actions,
    private store: Store,
    private snackbar: SnackBarService,
    private translate: TranslateService,
    private canVisibilityService: CanVisibilityService,
    private canModificationService: CanModificationService,
    private canAccessibilityService: CanAccessibilityService,
    configuration: ConfigurationService
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    Object.assign(this, { settings: configuration.getAppSettings() } );
  }

  loadFeatureListEmailTemplate = createEffect(() =>
    this.actions$.pipe(
      ofType(fromAction$.loadFeatureListEmailTemplate),
      exhaustMap((arg) =>
        this.templateService.getAllTemplate(arg.page, arg.pageSize, arg.sortByField, arg.sortDirection, arg.filter, arg.templateType)
          .pipe(map((response) =>
              fromAction$.loadFeatureListEmailTemplateSuccess({ response: this.transformTemplateList(response) })
            ),
            catchError(() => {
              return of(fromAction$.loadFeatureListEmailTemplateFailure());
            })
          )
      )
    )
  );

  loadFeatureListChooseModelEmailTemplate = createEffect(() =>
    this.actions$.pipe(
      ofType(fromAction$.loadFeatureListChooseModelEmailTemplate),
      exhaustMap((arg) =>
        this.templateService
          .listChooseModelEmailTemplate(
            arg.page,
            arg.pageSize,
            arg.sortByField,
            arg.sortDirection,
            arg.filter,
            arg.templateType
          )
          .pipe(
            map((response) =>
              fromAction$.loadFeatureListEmailTemplateSuccess({
                response
              })
            ),
            catchError(() => {
              return of(
                fromAction$.loadFeatureListEmailTemplateFailure()
              );
            })
          )
      )
    )
  );

  loadFeatureListEmailTemplateSuccessEffect = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromAction$.loadFeatureListEmailTemplateSuccess),
        withLatestFrom(
          this.store.select(fromSelector.getFeatureListEmailTemplateFilter)
        ),
        tap(([args, filters]) => {
          if (
            args.response.total != undefined &&
            args.response.total <= 0 &&
            filters &&
            filters.filter
          ) {
            // this.translate.get('cxmCampaign.followMyCampaign.list.tableHeader.notFound').toPromise().then(msg => this.snackbar.openSuccess(msg))
          }
        })
      ),
    { dispatch: false }
  );

  // loadFeatureListEmailTemplateFailureEffect = createEffect(
  //   () =>
  //     this.actions$.pipe(
  //       ofType(fromAction$.loadFeatureListEmailTemplateFailure),
  //       tap((args) =>
  //         this.translate
  //           .get('errorCases.cxmTemplate.emailTemplate.loadFail')
  //           .toPromise()
  //           .then((msg) => this.snackbar.openCustomSnackbar({ message: msg, icon: 'close', type: 'error' }))
  //       )
  //     ),
  //   { dispatch: false }
  // );

  deleteEmailTemplateSuccessEffect = createEffect(() =>
    this.actions$.pipe(
      ofType(fromDeleteEmail.deleteEmailTemplateSuccess),
      withLatestFrom(
        this.store.select(fromSelector.getFeatureListEmailTemplateFilter),
        this.store.select(fromSelector.selectTemplateModelList)
      ),
      switchMap(([args, filter, list]) => {
        const params = {
          ...filter,
          page: list.contents.length === 1 ? 1 : filter.page
        };
        return of(fromAction$.loadFeatureListEmailTemplate(params));
      })
    )
  );

  downloadTemplateAsFileEffect$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromAction$.downloadTemplateAsFile),
        tap((args) => {
          const { template } = args;

          this.templateService
            .downloadTemplate(template)
            .toPromise()
            .then((response: any) => {
              const dataType = response.type;
              const binaryData = [];
              binaryData.push(response);
              const downloadLink = document.createElement('a');
              downloadLink.href = window.URL.createObjectURL(
                new Blob(binaryData, { type: dataType })
              );
              downloadLink.setAttribute(
                'download',
                `${template.modelName}.${
                  template.templateType === 'EMAILING' ? 'html' : 'txt'
                }`
              );
              document.body.appendChild(downloadLink);
              downloadLink.click();
              downloadLink.remove();
            });
        })
      ),
    { dispatch: false }
  );

  transformTemplateList = (response: EntityResponseHandler<TemplateModel>): TemplateList => {
    const finalContents = response?.contents?.map((template: TemplateModel) => {
      return {
        ...template,
        downloadOption: this.getDownloadOption(template),
        imgUrl: this.getImgUrl(template),
        privilege: this.validatePrivilege(template)
      }
    });

    return {
      contents: finalContents,
      page: response.page,
      pageSize: response.pageSize,
      total: response.total
    };
  };

  getImgUrl = (template: TemplateModel): string => {
    if (!template.fileName) return '';

    return `${this.settings.apiGateway}${cxmTemplateEnvironment.templateContext}/templates/composition/load-file/${template.fileName}`;
  }

  getDownloadOption = (template: TemplateModel): string [] => {
    if(template.templateType === TemplateyType.SMS){
      return ["TXT"];
    }else{
      return ["HTML"];
    }
  }

  validatePrivilege = (template: TemplateModel): TemplatePrivilegeModel => {
    const privilege = {
      canDelete: this.canDelete(template),
      canModify: this.canModify(template),
      canCopy: this.canCopy(template),
      canDownload: this.canDownload(template),
      canSelect: this.canSelected(template),
      canView: this.canView(template),
      canCreate: this.canCreate(template),
      canList: this.canList(template)
    };

    return {
      ...privilege,
      canShowToggleButton: privilege.canDelete || privilege.canDownload || privilege.canCopy
    }
  };

  canList = (template: TemplateModel): boolean => {
    let module = "";
    let feature = "";

    if(template.templateType === TemplateyType.SMS){
      module = SmsTemplate.CXM_SMS_TEMPLATE;
      feature = SmsTemplate.LIST;
    }else{
      module = TemplateConstant.CXM_TEMPLATE;
      feature = TemplateConstant.LIST;
    }

    return this.canAccessibilityService.canAccessible(module, feature, true);
  }

  canCreate = (template: TemplateModel): boolean => {
    let module = "";
    let feature = "";

    if(template.templateType === TemplateyType.SMS){
      module = SmsTemplate.CXM_SMS_TEMPLATE;
      feature = SmsTemplate.CREATE;
    }else{
      module = TemplateConstant.CXM_TEMPLATE;
      feature = TemplateConstant.CREATE;
    }

    return this.canAccessibilityService.canAccessible(module, feature, true);
  }

  canView = (template: TemplateModel): boolean => {
    let module = "";
    let feature = "";

    if(template.templateType === TemplateyType.SMS){
      module = SmsTemplate.CXM_SMS_TEMPLATE;
      feature = SmsTemplate.EDIT;
    }else{
      module = TemplateConstant.CXM_TEMPLATE;
      feature = TemplateConstant.EDIT;
    }

    return this.canVisibilityService.hasVisibility(module, feature, (template.ownerId || 0), true);
  }

  canSelected = (template: TemplateModel): boolean => {
    let module = "";
    let feature = "";

    if(template.templateType === TemplateyType.SMS){
      module = CampaignConstant.CXM_CAMPAIGN_SMS;
      feature = CampaignConstant.CHOOSE_MODEL_SMS;
    }else{
      module = CampaignConstant.CXM_CAMPAIGN;
      feature = CampaignConstant.CHOOSE_MODEL;
    }

    return this.canVisibilityService.hasVisibility(module, feature, (template.ownerId || 0), true);
  }

  canDownload = (template: TemplateModel): boolean => {
    let module = "";
    let feature = "";

    if(template.templateType === TemplateyType.SMS){
      module = SmsTemplate.CXM_SMS_TEMPLATE;
      feature = SmsTemplate.EDIT;
    }else{
      module = TemplateConstant.CXM_TEMPLATE;
      feature = TemplateConstant.EDIT;
    }

    return this.canVisibilityService.hasVisibility(module, feature, (template.ownerId || 0), true);
  }

  canCopy = (template: TemplateModel): boolean => {
    let module = "";
    let feature = "";

    if(template.templateType === TemplateyType.SMS){
      module = SmsTemplate.CXM_SMS_TEMPLATE;
      feature = SmsTemplate.CREATE_BY_DUPLICATE;
    }else{
      module = TemplateConstant.CXM_TEMPLATE;
      feature = TemplateConstant.DUPLICATE;
    }

    return this.canVisibilityService.hasVisibility(module, feature, (template.ownerId || 0), true);
  }

  canModify = (template: TemplateModel): boolean => {
    let module = "";
    let feature = "";

    if(template.templateType === TemplateyType.SMS){
      module = SmsTemplate.CXM_SMS_TEMPLATE;
      feature = SmsTemplate.MODIFY;
    }else{
      module = TemplateConstant.CXM_TEMPLATE;
      feature = TemplateConstant.MODIFY;
    }

    return this.canModificationService.hasModify(module, feature, template.ownerId || 0, true);
  }

  canDelete = (template: TemplateModel): boolean => {
    let module = "";
    let feature = "";

    if(template.templateType === TemplateyType.SMS){
      module = SmsTemplate.CXM_SMS_TEMPLATE;
      feature = SmsTemplate.DELETE;
    }else{
      module = TemplateConstant.CXM_TEMPLATE;
      feature = TemplateConstant.DELETE;
    }

    return this.canModificationService.hasModify(module, feature, template.ownerId || 0, true);
  }
}
