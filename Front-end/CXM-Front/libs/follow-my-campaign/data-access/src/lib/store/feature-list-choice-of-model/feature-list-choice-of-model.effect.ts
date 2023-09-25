import { Inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { FollowMyCampaignService } from '../../services/follow-my-campaign.service';
import {
  EMAILING,
  EMAILING_KEY_REGEX,
  EMAILING_VALUE,
  EntityResponseHandler,
  SMS,
  SMS_KEY_REGEX,
  SMS_VALUE,
  TemplateList,
  TemplateModel,
  TemplateService,
  TemplateType
} from '@cxm-smartflow/template/data-access';
// eslint-disable-next-line @nrwl/nx/enforce-module-boundaries
import { PreViewEmailTemplateService } from '@cxm-smartflow/follow-my-campaign/ui/preview-email-template';
import { catchError, exhaustMap, map, pluck, take, tap } from 'rxjs/operators';
import * as fromAction$ from './feature-list-choice-of-model.action';
import { templateEnv as cxmTemplateEnvironment } from '@env-cxm-template';
import { StringUtil } from '@cxm-smartflow/shared/utils';
import { ConfigurationService } from '@cxm-smartflow/shared/data-access/api';
import { IAppSettings } from '@cxm-smartflow/shared/app-config';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';


@Injectable()
export class FeatureListChoiceOfModelEffect {

  accessDeniedMessage: any;
  settings: IAppSettings;

  constructor(private readonly actions$: Actions, private readonly service: FollowMyCampaignService,
              private readonly templateService: TemplateService,
              private readonly preViewEmailTemplateService: PreViewEmailTemplateService,
              private snackBarService: SnackBarService,
              private translate: TranslateService,
              private readonly configuration: ConfigurationService
  ) {

    Object.assign(this, { settings: configuration.getAppSettings() })
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate.get('cannotAccess').subscribe(response => this.accessDeniedMessage = response);
  }

  transformHtmlContent = (htmlContent: string, templateType: string): string => {
    if (templateType.toUpperCase() === TemplateType.SMS) {
      return StringUtil.replaceAllByRegex(htmlContent, SMS_KEY_REGEX, SMS_VALUE);
    } else {
      return StringUtil.replaceAllByRegex(htmlContent, EMAILING_KEY_REGEX, EMAILING_VALUE);
    }
  };

  transformEmailTemplateModel = (templateModel: TemplateModel): TemplateModel => {
    return {
      ...templateModel,
      variables: this.transformVariableValue(templateModel.variables || [],
        templateModel.templateType || '')
    };
  };

  transformVariableValue = (variables: string[], templateType: string): string [] => {
    if (templateType.toUpperCase() === TemplateType.SMS) {
      return variables.map(variable => {
        if (variable === SMS.key) {
          return SMS.value;
        } else return variable;
      });
    } else {
      return variables.map(variable => {
        if (variable === EMAILING.key) {
          return EMAILING.value;
        } else return variable;
      });
    }
  };

  previewTemplate$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromAction$.loadPreviewTemplate),
        tap((args) => {
          if (args.emailTemplateModel.id) {
            this.templateService
              .getTemplateById(args.emailTemplateModel.id)
              .pipe(take(1), pluck('htmlFile'))
              .subscribe((htmlContent: any) => {

                this.preViewEmailTemplateService.previewEmailTemplate(
                  this.transformEmailTemplateModel(args.emailTemplateModel),
                  this.transformHtmlContent(htmlContent, (args.emailTemplateModel.templateType || ''))
                );
              });
          }
        })
      ),
    { dispatch: false }
  );

  loadListChoiceOfModelTemplate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromAction$.loadListChoiceOfModelTemplate),
      exhaustMap((args) => this.service.getChooseTemplateModel(args.page, args.pageSize, args.sortByField, args.sortDirection, args.filter, args.templateType).pipe(
        map((response) => fromAction$.loadListChoiceOfModelTemplateSuccess({ response: this.transformListChoiceOfModelTemplate(response), filter: args.filter })),
        catchError((error: HttpErrorResponse) => [fromAction$.loadListChoiceOfModelTemplateFail({ error: error })])
      ))
    )
  );

  loadListChoiceOfModelTemplateFail$ = createEffect(() => this.actions$.pipe(
    ofType(fromAction$.loadListChoiceOfModelTemplateFail),
    tap((args) => {
      const error = args.error as HttpErrorResponse;
      if (error.status === HttpStatusCode.Forbidden) {
        this.snackBarService.openCustomSnackbar({ message: this.accessDeniedMessage, type: 'error', icon: 'close' });
      }
    })
  ), { dispatch: false });

  transformListChoiceOfModelTemplate = (response: EntityResponseHandler<TemplateModel>): TemplateList => {
    const finalContents = response?.contents?.map((row: TemplateModel) => {
      return {
        ...row,
        imgUrl: this.getImgUrl(row)
      };
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
  };
}
