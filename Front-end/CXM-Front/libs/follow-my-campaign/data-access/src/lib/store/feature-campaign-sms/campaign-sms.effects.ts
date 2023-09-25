import { HttpEventType } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { appRoute, TemplateService } from '@cxm-smartflow/template/data-access';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';
import { interval, of } from 'rxjs';
import {
  catchError,
  exhaustMap,
  filter,
  map,
  switchMap,
  take,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { CampaignModel, CampaignSmsSendTestModel } from '../../models';
import { FollowMyCampaignService } from '../../services/follow-my-campaign.service';
import * as fromAction from './campaign-sms.actions';
import { smsAttempToStep } from './campaign-sms.actions';
import * as fromSelector from './campaign-sms.selectors';
import { selectSmsCsvFilter, selectSmsState } from './campaign-sms.selectors';
import { StepOnActivated } from '../feature-campaign-email';

@Injectable({ providedIn: 'root' })
export class CampaignSmsEffect {
  smsMessageLabel: any;

  campaignSmsLoadEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.loadCampaignSms),
      exhaustMap((args) => {
        const { campaignId, templateId } = args;

        if (campaignId) {
          return of(fromAction.loadCampaignSmsDetail({ campaignId }));
        }

        if (templateId) {
          return of(fromAction.loadCampaignSmsTemplate({ templateId }));
        }

        return of(fromAction.loadCampaignSmsSuccess());
      })
    )
  );

  loadCampaignSmsDetailEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.loadCampaignSmsDetail),
      exhaustMap((args) => {
        const { campaignId } = args;
        return this.campaignService.getEmailCampaignById(campaignId).pipe(
          map((campaign) => {
            const campaignForm = {
              ...campaign,
              sendingSchedule: campaign?.sendingSchedule
                ? campaign?.sendingSchedule
                : new Date(),
            };
            return fromAction.loadCampaignSmsDetailSuccess({
              campaign: campaignForm,
            });
          }),
          catchError((error) =>
            of(fromAction.loadCampaignSmsDetailFail({ httpError: error }))
          )
        );
      })
    )
  );

  loadCampaignSmsDetailSuccess$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.loadCampaignSmsDetailSuccess),
    withLatestFrom(this.store.select(fromSelector.selectSmsState)),
    tap(([args, smsState]) => {
      const { step, campaign } = smsState;
      if (step === 2) {
        this.store.dispatch(fromAction.fetchSMSCsv());
      }

      const isValidCSVRecord = (campaign.details.csvRecordCount - campaign.details.errorCount) > 0;
      if (!location.pathname.includes(appRoute.cxmCampaign.followMyCampaign.smsCampaignDestination) && !isValidCSVRecord) {
        this.store.dispatch(smsAttempToStep({ step: 2 }));
      }
    })
  ), { dispatch: false });

  loadCampaignSmsTemplateEffect = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.loadCampaignSmsTemplate),
      exhaustMap((args) => {
        const { templateId } = args;

        return this.templateServices.getTemplateById(parseInt(templateId)).pipe(
          map((smsTemplate) =>
            fromAction.loadCampaignSmsTemplateSuccess({ template: smsTemplate })
          ),
          catchError((error) => {
            return of(
              fromAction.loadCampaignSmsTemplateFail({ httpError: error })
            );
          })
        );
      })
    )
  );

  // load template in case mode edit effect$
  loadCampaignSmsTemplateSuccessEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.loadCampaignSmsTemplateSuccess),
      withLatestFrom(this.store.select(fromSelector.selectSmsState)),
      exhaustMap(([args, smsState]) => {
        const { mode } = smsState;

        if (mode === 1) {
          return of(
            fromAction.smsFilterCsvFilterChanged({
              filter: { page: 1, pageSize: 10 },
            })
          );
        }
        return of(fromAction.loadCampaignSmsSuccess());
      })
    )
  );

  csvFilterChangedEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsFilterCsvFilterChanged),
      withLatestFrom(this.store.select(fromSelector.selectSmsState)),
      exhaustMap(([args, smsState]) => {
        const { filter } = args;
        const {
          mode,
          template,
          csvData,
          templateId,
          checkSameNumber,
          hasHeader,
        } = smsState;

        return this.campaignService
          .getCsvRecord(filter.page, filter.pageSize, {
            dir: mode === 1 ? 'csv' : 'tmp',
            fileName: csvData.fileName,
            templateId: templateId ?? template?.id,
            type: 'SMS',
            isCount: true,
            removeDuplicate: false,
            hasHeader: false,
            sortByField: filter.sortByField || 'lineNumber',
            sortDirection: filter.sortDirection || 'ASC',
          })
          .pipe(
            map((csvContent) =>
              fromAction.recordSmsDataFromCsv({
                data: csvContent.contents,
                filter: {
                  page: csvContent.page,
                  pageSize: csvContent.pageSize,
                  total: csvContent.total,
                },
              })
            )
          );
      })
    )
  );

  uploadSmsCsvFileEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.uploadSmsCsvFile),
      withLatestFrom(this.store.select(fromSelector.selectSmsState)),
      exhaustMap(([args, smsState]) => {
        const { campaign, template, hasHeader, checkSameNumber } = smsState;
        const path = campaign === undefined ? 'tmp' : 'csv';
        const filename =
          campaign === undefined ? undefined : campaign?.details?.csvName;
        const isKeepOriginalName = campaign !== undefined;

        return (
          this.campaignService
            // .uploadFile(args.file, path, filename, isKeepOriginalName)
            .uploadFile(args.file, {
              dirs: path,
              filename,
              isKeepOriginalName,
              templateId: template.id,
              type: 'SMS',
              hasHeader,
              removeDuplicate: checkSameNumber,
            })
            .pipe(
              map((res) => {
                if (res.type == HttpEventType.Response && res.ok) {
                  if (res.body)
                    return fromAction.uploadSmsCsvFileSuccess({
                      res: res.body,
                    });
                } else if (res.type === HttpEventType.UploadProgress) {
                  if (res.total) {
                    const progress = Math.round(res.loaded / res.total) * 100;
                    return fromAction.uploadSmsCsvProgression({ progress });
                  }
                }
                return fromAction.uploadSmsCsvFileNonce();
              }),
              catchError((err) =>
                of(fromAction.uploadSmsCsvFileFail({ error: err }))
              )
            )
        );
      })
    )
  );

  csvUploadFailEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.uploadSmsCsvFileFail),
        tap((args) => {
          this.translateService
            .get('cxmCampaign.followMyCampaign.import_file_upload_fail')
            .toPromise()
            .then((message) =>
              this.snackbarService.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              })
            );
        })
      ),
    { dispatch: false }
  );

  uploadSmsCsvFileSuccess$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.uploadSmsCsvFileSuccess),
      withLatestFrom(this.store.select(fromSelector.selectCampaignSmsLoader), this.store.select(fromSelector.selectSmsState)),
      tap(([args, campaignSms, campaignState]) => {
        if (args.res.invalidCount > 0) {
          Promise.all([
            this.translateService
              .get('cxmCampaign.followMyCampaign.imported_file_has_error', {
                invalidCount: args.res.invalidCount,
              })
              .toPromise(),
            this.translateService
              .get(
                'cxmCampaign.followMyCampaign.imported_file_has_error_instruction'
              )
              .toPromise(),
          ]).then((msg) =>
            this.snackbarService.openCustomSnackbar({
              icon: 'close',
              message: msg[0],
              type: 'error',
              details: msg[1],
            })
          );
        } else {
          this.translateService
            .get('cxmCampaign.followMyCampaign.imported_file_success')
            .toPromise()
            .then((msg) =>
              this.snackbarService.openCustomSnackbar({
                icon: 'close',
                message: msg,
                type: 'success',
              })
            );
        }

        const { count, invalidCount } = campaignState.csvData;
        if(count === invalidCount && campaignState.mode !== 1) {
          this.store.dispatch(fromAction.smsFilterCsvFilterChanged({ filter: { page: 1, pageSize: 10 }}));
        } else {
          this.store.dispatch(fromAction.smsSubmitDestination({ isUpdate: false }));
        }

      })
    ), { dispatch: false }
  );

  prepareUploadCsvEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.prepareUploadCsv),
      withLatestFrom(this.store.select(fromSelector.selectSmsState)),
      exhaustMap(([args, smsState]) => {
        const { template, mode } = smsState;
        const { firstrow, matchLength } = args;

        // return this.campaignService
        //   .checkValidateCSVFile(firstrow, template.id, 'SMS')
        //   .pipe(
        //     map((csvOK) =>
        //       fromAction.smsCheckCsvHeaderValueResult({
        //         csvOK,
        //         matchLength,
        //         file: args.file,
        //       })
        //     )
        //   );
        return of(
          fromAction.smsCheckCsvHeaderValueResult({
            csvOK: matchLength === 0,
            matchLength,
            file: args.file,
          })
        );
      })
    )
  );

  recordSmsDataFromCsvEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.recordSmsDataFromCsv),
      withLatestFrom(this.store.select(fromSelector.selectSmsState)),
      exhaustMap(([{data}, smsState]) => {
        const { mode, step } = smsState;
        const validRecords =
          data?.filter((e: any) => JSON.parse(e.valid)) || [];
        if (validRecords.length > 0 && mode === 0 && step === 2) {
          this.store.dispatch(fromAction.smsValidateStep({ step: 2 }));
        }

        interval(500).pipe(take(1)).subscribe(() => {
          this.store.dispatch(fromAction.closeLoading());
        });
        return of(fromAction.uploadSmsCsvFileNonce());
      })
    )
  );

  validateCsvOkEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsCheckCsvHeaderValueResult),
      filter((args) => args.csvOK === true),
      switchMap((args) => {
        if (args.file) {
          return of(fromAction.uploadSmsCsvFile({ file: args.file }));
        }
        return of(fromAction.uploadSmsCsvFileNonce()); // nonce
      })
    )
  );

  validateCsvNotOkEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.smsCheckCsvHeaderValueResult),
        filter((args) => args.csvOK === false),
        tap((args) => {
          // const { csvOK, matchLength } = args;
          // if(!csvOK) {
          Promise.all([
            this.translateService
              .get('cxmCampaign.followMyCampaign.import_compliant_file_message')
              .toPromise(),
            this.translateService
              .get(
                'cxmCampaign.followMyCampaign.csv_contain_more_column_tooltip'
              )
              .toPromise(),
            this.translateService
              .get(
                'cxmCampaign.followMyCampaign.csv_contain_fewer_column_tooltip'
              )
              .toPromise(),
          ]).then((msg) => {
            // const error =
            //   matchLength > 0 ? msg[1] : matchLength < 0 ? msg[2] : '';
            // this.snackbarService.openError(`${msg[0]}${error}`);
            this.snackbarService.openCustomSnackbar({
              icon: 'close',
              type: 'error',
              message: msg[0],
            });
          });
          // }
        })
      ),
    { dispatch: false }
  );

  smsSubmitDestinationEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsSubmitDestination),
      withLatestFrom(this.store.select(fromSelector.selectSmsState)),
      exhaustMap(([args, smsState]) => {
        const {
          mode,
          template,
          csvData,
          checkSameNumber,
          hasHeader,
          csvFilter,
          campaign,
          step,
        } = smsState;
        const campaignName = 'SMS'
          .concat('_')
          .concat(new Date().toLocaleString());
        if (campaign === undefined) {
          const campaignPayload: CampaignModel = {
            id: 0,
            templateId: template.id,
            modelName: template.modelName,
            campaignName,
            details: {
              variables: template.variables,
              htmlTemplate: template.htmlFile,
              csvPath: csvData.fileUrl,
              csvTmpPath: 'tmp',
              csvName: csvData.fileName,
              csvOriginalName: csvData.originalName,
              csvRecordCount: csvData.count,
              csvHasHeader: hasHeader,
              removeDuplicate: checkSameNumber,
              errorCount: csvData.invalidCount
            },
            type: 'SMS',
            step: 2,
            mode: 'Manual',
            channel: 'Digital',
          };
          return this.campaignService.addEmailCampaign(campaignPayload).pipe(
            map((campaign) =>
              fromAction.smsSubmitDestinationSuccess({ campaign, isUpdate: args.isUpdate })
            ),
            catchError((err) => of(fromAction.smsSubmitDestinationFail()))
          );
        }
        // if(mode === 1)
        else {
          const { campaign, checkSameNumber, hasHeader } = smsState;
          let { details } = campaign;
          details = {
            ...details,
            csvPath: csvData.fileUrl,
            csvName: csvData.fileName,
            csvOriginalName: csvData.originalName,
            csvRecordCount: csvData.count,
            csvHasHeader: hasHeader,
            removeDuplicate: checkSameNumber,
            errorCount: csvData.invalidCount
          };


          const payload = {
            ...campaign,
            step: 2,
            details,
            mode: 'Manual',
            channel: 'Digital',
          };

          return this.campaignService.updateEmailCampaign(payload).pipe(
            map((updatedcampaign) =>
              fromAction.smsSubmitDestinationSuccess({
                campaign: updatedcampaign,
                isUpdate: args.isUpdate
              })
            ),
            catchError((err) => of(fromAction.smsSubmitDestinationFail()))
          );
        }
      })
    )
  );

  smsSubmitDestinationSuccessEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.smsSubmitDestinationSuccess),
        withLatestFrom(this.store.select(selectSmsState)),
        tap(([args, smsState]) => {

          this.translateService
            .get('cxmCampaign.followMyCampaign.generateEmail')
            .toPromise()
            .then((messages) => {
              const { step } = smsState;
              this.store.dispatch(fromAction.smsValidateStep({ step }));
            });

          if (!args.isUpdate) {
            this.activatedRoute.queryParams.pipe(take(1)).subscribe(params =>  {
              this.router.navigate([appRoute.cxmCampaign.followMyCampaign.smsCampaignDestination], { queryParams: { ...params, id: smsState.campaign.id }})
            });
          }

          this.store.dispatch(fromAction.fetchSMSCsv());
        })
      ),
    { dispatch: false }
  );

  loadCampaignSmsDetailSuccessEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(
        fromAction.loadCampaignSmsDetailSuccess,
        fromAction.smsSubmitDestinationSuccess
      ),
      switchMap((args) => {
        return of(fromAction.smsValidateStep({ step: 3 }));
      })
    )
  );

  smsInitStepEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsInitStep),
      switchMap(({ step }) => {
        return of(fromAction.smsValidateStep({ step }));
      })
    )
  );

  smsInitStep4Effect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsInitStep),
      filter((args) => args.step === 4),
      switchMap((args) => {
        return of(
          fromAction.smsFilterCsvFilterChanged({
            filter: { page: 1, pageSize: 10 },
          })
        );
      })
    )
  );

  smsSubmitParameterEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsSubmitParameter),
      withLatestFrom(
        this.store.select(fromSelector.selectSmsCampaign),
        this.store.select(selectSmsCsvFilter)
      ),
      exhaustMap(([args, campaign, csvFilter]) => {
        const campaignForm = {
          ...campaign,
          senderName: campaign?.senderName || 'Tessi Newsletter',
          details: {
            ...campaign?.details,
            csvPath: 'csv',
            csvTmpPath: 'tmp',
          },
        };

        return this.campaignService
          .updateEmailCampaign({ ...campaignForm, step: 3 })
          .pipe(
            map((campaign) =>
              fromAction.smsSubmitParameterSuccess({ campaign })
            ),
            catchError((err) => of(fromAction.smsSubmitParameterFail()))
          );
      })
    )
  );

  smsSubmitParameterSuccessEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.smsSubmitParameterSuccess),
        tap((args) => {
          this.translateService
            .get('cxmCampaign.followMyCampaign.generateEmail')
            .toPromise()
            .then((messages) => {
              // this.snackbarService.openCustomSnackbar({message: messages.message?.createSuccess, icon: 'close', type: 'success'});
              this.router.navigate([
                '/cxm-campaign/follow-my-campaign/sms/envoy',
                args?.campaign?.id,
              ]);
            });
        })
      ),
    { dispatch: false }
  );

  smsSubmitParameterFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.smsSubmitParameterFail),
        tap(() => {
          this.translateService
            .get('cxmCampaign.followMyCampaign.generateEmail')
            .toPromise()
            .then((messages) => {
              this.snackbarService.openCustomSnackbar({
                message: messages.message?.createFail,
                icon: 'close',
                type: 'error',
              });
            });
        })
      ),
    { dispatch: false }
  );

  smsSendingFormChangedEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsSendingFormChanged),
      switchMap((args) => of(fromAction.smsValidateStep({ step: 4 })))
    )
  );

  smsSubmitEnvoyEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsSubmitEnvoy),
      withLatestFrom(
        this.store.select(fromSelector.selectSmsCampaign),
        this.store.select(fromSelector.selectStep)
      ),
      exhaustMap(([args, campaign, step]) => {
        const campaignForm = {
          ...campaign,
          sendingSchedule: args?.sendingSchedule || new Date(),
          isValidate: true,
          step: step,
          details: {
            ...campaign?.details,
            csvPath: 'csv',
            csvTmpPath: '',
          },
        };

        return this.campaignService.updateEmailCampaign(campaignForm).pipe(
          map(() => {
            return fromAction.smsSubmitEnvoySuccess();
          }),
          catchError((httpErrorResponse) => of(fromAction.smsSubmitEnvoyFail({ httpErrorResponse })))
        );
      })
    )
  );

  smsSubmitEnvoySuccessEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsSubmitEnvoySuccess),
      tap((args) => {
        this.translateService
          .get('cxmCampaign.followMyCampaign.generateEmail')
          .toPromise()
          .then(() => {
            this.store.dispatch(StepOnActivated({ active: false, leave: true, specification: { stepFor: 'SMS' } }));
            this.router.navigate([
              appRoute.cxmCampaign.followMyCampaign.smsSuccess,
            ]);
          });
      }),
      switchMap((_) => of(fromAction.unloadCampaignSms()))
    )
  );

  smsSubmitEnvoyFailEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsSubmitEnvoyFail),
      exhaustMap((args) => {
        const { apierrorhandler } = args.httpErrorResponse.error;

        if(apierrorhandler) {
          if([4005].includes(apierrorhandler.statusCode)) {
            return [fromAction.distributionSMSChannelDoesNotConfig()];
          }
        }

        return [fromAction.alertSmsSubmitEnvoyFail()];
      }),
    )
  );

  alertSmsSubmitEnvoyFail$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.alertSmsSubmitEnvoyFail),
    tap(() => {
      this.translateService
        .get('cxmCampaign.followMyCampaign.generateEmail')
        .toPromise()
        .then((messages) => {
          this.snackbarService.openCustomSnackbar({
            message: messages.message?.updateFail,
            icon: 'close',
            type: 'error',
          });
        });
    })
  ), { dispatch: false });

  distributionSMSChannelDoesNotConfig$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.distributionSMSChannelDoesNotConfig),
    tap(() => {
      this.translateService.get('flow.deposit.channel_category_not_activated').toPromise().then(messages => {
        this.confirmMessageService.showConfirmationPopup({
          icon: 'close',
          title: messages.title,
          message: messages.message,
          paragraph: messages.message2,
          cancelButton: messages.cancelButton,
          confirmButton: messages.confirmButton,
          type: 'Warning'
        }).subscribe(ok => {
          if(ok) {
            // quit
            this.router.navigateByUrl(`${appRoute.cxmCampaign.followMyCampaign.listEmailCampaign}`);
            this.store.dispatch(fromAction.unloadCampaignSms());
          }
        })
      });
    })
  ), { dispatch: false });

  smsAttempToStepEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.smsAttempToStep),
        withLatestFrom(this.store.select(fromSelector.selectSmsCampaign)),
        tap(([args, campaign]) => {
          switch (args.step) {
            case 1:
              this.router.navigate([
                appRoute.cxmCampaign.followMyCampaign.smsCampaignList,
              ]);
              break;

            case 2:
              this.router.navigate(
                [appRoute.cxmCampaign.followMyCampaign.smsCampaignDestination],
                { queryParams: { templateId: campaign.templateId, id: campaign.id } }
              );
              break;

            case 3:
              this.router.navigate([
                appRoute.cxmCampaign.followMyCampaign.smsCampaignParameter,
                campaign.id,
              ]);
              break;
          }
        })
      ),
    { dispatch: false }
  );

  smsSubmitTestSendBatEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.smsSubmitTestSendBat),
      withLatestFrom(this.store.select(fromSelector.selectSmsCampaign)),
      exhaustMap(([args, campaign]) => {
        const payload: CampaignSmsSendTestModel = {
          campaignId: campaign?.id,
          destinations: args.recipients,
        };
        return this.campaignService.sendTestSms(payload).pipe(
          map(() => fromAction.smsSubmitTestSendBatSuccess({ show: false, total: (args.recipients as string [])?.length})),
          catchError(() =>
            of(fromAction.smsSubmitTestSendBatFail({ show: false, total: (args.recipients as string [])?.length }))
          )
        );
      })
    )
  );

  smsSubmitTestSendBatSuccessEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.smsSubmitTestSendBatSuccess),
        tap((args) => {
          const message = args.total > 1 ? this.smsMessageLabel?.smsSuccesses : this.smsMessageLabel?.smsSuccess;
          this.snackbarService.openCustomSnackbar({
            message: message,
            icon: 'close',
            type: 'success',
          });
        })
      ),
    { dispatch: false }
  );

  smsSubmitTestSendBatFailEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.smsSubmitTestSendBatFail),
        tap((args) => {
          const message = args.total > 1 ? this.smsMessageLabel?.smsFails : this.smsMessageLabel?.smsFail;
          this.snackbarService.openCustomSnackbar({
            message: message,
            icon: 'close',
            type: 'error',
          });
        })
      ),
    { dispatch: false }
  );

  loadCampaignSmsTemplateFailEffect = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.loadCampaignSmsTemplateFail),
        tap((args) => {
          const { apierrorhandler } = args.httpError.error;
          if (apierrorhandler) {
            this.translateService
              .get('template.message')
              .toPromise()
              .then((messageProps) => {
                if (apierrorhandler.statusCode === 403) {
                  this.confirmMessageService
                    .showConfirmationPopup({
                      icon: 'close',
                      title: messageProps.unauthorize,
                      message: messageProps.unauthorizeAccess,
                      cancelButton: messageProps.unauthorizeCancel,
                      confirmButton: messageProps.unauthorizeLeave,
                      type: 'Warning',
                    })
                    .pipe(take(1))
                    .subscribe(() => {
                      this.router.navigateByUrl(
                        `${appRoute.cxmCampaign.followMyCampaign.smsCampaignChoiceOfModel}`
                      );
                    });
                } else if (apierrorhandler.statusCode === 404) {
                  this.confirmMessageService
                    .showConfirmationPopup({
                      icon: 'close',
                      title: messageProps.unauthorizeAccess,
                      cancelButton: messageProps.unauthorizeCancel,
                      confirmButton: messageProps.unauthorizeLeave,
                      type: 'Warning',
                    })
                    .pipe(take(1))
                    .subscribe(() => {
                      this.router.navigateByUrl(
                        `${appRoute.cxmCampaign.followMyCampaign.smsCampaignChoiceOfModel}`
                      );
                    });
                } else {
                  this.confirmMessageService
                    .showConfirmationPopup({
                      icon: 'close',
                      title: messageProps.unknownError,
                      cancelButton: messageProps.unauthorizeCancel,
                      confirmButton: messageProps.unauthorizeLeave,
                      type: 'Warning',
                    })
                    .pipe(take(1))
                    .subscribe(() => {
                      this.router.navigateByUrl(
                        `${appRoute.cxmCampaign.followMyCampaign.smsCampaignChoiceOfModel}`
                      );
                    });
                }
              });
          }
        })
      ),
    { dispatch: false }
  );

  loadCampaignSmsDetailFailEffect = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.loadCampaignSmsDetailFail),
        tap((args) => {
          const { apierrorhandler } = args.httpError.error;
          if (apierrorhandler) {
            this.translateService
              .get('template.message')
              .toPromise()
              .then((messageProps) => {
                if (apierrorhandler.statusCode === 403) {
                  this.confirmMessageService
                    .showConfirmationPopup({
                      icon: 'close',
                      title: messageProps.unauthorize,
                      message: messageProps.unauthorizeAccess,
                      cancelButton: messageProps.unauthorizeCancel,
                      confirmButton: messageProps.unauthorizeLeave,
                      type: 'Warning',
                    })
                    .subscribe(() => {
                      this.router.navigateByUrl(
                        `${appRoute.cxmCampaign.followMyCampaign.smsCampaignChoiceOfModel}`
                      );
                    });
                } else if (apierrorhandler.statusCode === 404) {
                  this.confirmMessageService
                    .showConfirmationPopup({
                      icon: 'close',
                      title: messageProps.campaignNotFound,
                      cancelButton: messageProps.unauthorizeCancel,
                      confirmButton: messageProps.unauthorizeLeave,
                      type: 'Warning',
                    })
                    .pipe(take(1))
                    .subscribe(() => {
                      this.router.navigateByUrl(
                        `${appRoute.cxmCampaign.followMyCampaign.smsCampaignChoiceOfModel}`
                      );
                    });
                } else {
                  this.confirmMessageService
                    .showConfirmationPopup({
                      icon: 'close',
                      title: messageProps.unknownError,
                      cancelButton: messageProps.unauthorizeCancel,
                      confirmButton: messageProps.unauthorizeLeave,
                      type: 'Warning',
                    })
                    .pipe(take(1))
                    .subscribe(() => {
                      this.router.navigateByUrl(
                        `${appRoute.cxmCampaign.followMyCampaign.smsCampaignChoiceOfModel}`
                      );
                    });
                }
              });
          }
        })
      ),
    { dispatch: false }
  );

  // smsFormChangedEffect$ = createEffect(() =>
  //   this.actions.pipe(
  //     ofType(fromAction.smsFormChanged),
  //     withLatestFrom(this.store.select(fromSelector.selectSmsCsvError)),
  //     switchMap(([args, csvError]) => {
  //       if (csvError.csvOK) {
  //         return of(
  //           fromAction.smsFilterCsvFilterChanged({
  //             filter: { page: 1, pageSize: 12 },
  //           })
  //         );
  //       }
  //       return of(fromAction.uploadSmsCsvFileNonce());
  //     })
  //   )
  // );

  fetchSMSCsv$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.fetchSMSCsv),
    withLatestFrom(this.store.select(fromSelector.selectSmsState)),
    exhaustMap(([args, smsState]) => {
      const {
        template,
        csvData,
        templateId,
      } = smsState;

      return this.campaignService
        .getCsvRecord(1, 10, {
          dir: 'csv',
          fileName: csvData.fileName,
          templateId: templateId ?? template?.id,
          type: 'SMS',
          isCount: true,
          removeDuplicate: false,
          hasHeader: false,
          sortByField: 'lineNumber',
          sortDirection: 'ASC',
        })
        .pipe(
          map((csvContent) =>
            fromAction.recordSmsDataFromCsv({
              data: csvContent.contents,
              filter: {
                page: csvContent.page,
                pageSize: csvContent.pageSize,
                total: csvContent.total,
              },
            })
          )
        );
    })
  ));

  getSmsMetadata$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.getSmsMetadata),
    exhaustMap(args => this.campaignService.getMetadata(['sender_label']).pipe(
      map(metadataResponse => fromAction.getSmsMetadataSuccess({ metadataResponse })),
      catchError(httpErrorResponse => [fromAction.getSmsMetadataFail({ httpErrorResponse })]),
    ))
  ));

  getSmsMetadataFail$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.getSmsMetadataFail),
    tap(() => {
      this.translateService.get('client.metadata_fetch_fail').toPromise().then(message => {
        this.snackbarService.openCustomSnackbar({ icon: 'close', type: 'error', message });
      });
    })
  ), { dispatch: false });

  constructor(
    private actions: Actions,
    private templateServices: TemplateService,
    private campaignService: FollowMyCampaignService,
    private translateService: TranslateService,
    private snackbarService: SnackBarService,
    private confirmMessageService: ConfirmationMessageService,
    private router: Router,
    private store: Store,
    private activatedRoute: ActivatedRoute
  ) {
    this.translateService.use(localStorage.getItem('locale') || 'fr');
    this.translateService
      .get('cxmCampaign.followMyCampaign.generateEmail.dialog.message')
      .subscribe((value) => (this.smsMessageLabel = value));
  }
}
