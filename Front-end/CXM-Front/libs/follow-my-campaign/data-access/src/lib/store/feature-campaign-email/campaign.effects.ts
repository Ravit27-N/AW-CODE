import * as fromAction from './campaign.actions';
import {
  closeEmailLoading,
  getEmailMetadata,
  getMaxFileSizeUpload,
  getMaxFileSizeUploadFailed,
  getMaxFileSizeUploadSuccess, previousEmailCampaignParameterStep,
  removeAttachmentUploadedWhenLeavePage, submitDestinationStep
} from './campaign.actions';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  exhaustMap,
  filter,
  map,
  switchMap,
  take,
  tap,
  withLatestFrom
} from 'rxjs/operators';
import { FollowMyCampaignService } from '../../services/follow-my-campaign.service';
import { interval, of } from 'rxjs';
import * as fromSelector from './campaign.selectors';
import { selectCampaignWithAttachments, selectEmailState } from './campaign.selectors';
import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { createAction, Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
// eslint-disable-next-line @nrwl/nx/enforce-module-boundaries
import { appRoute, CustomFileModel, TemplateModel, TemplateService } from '@cxm-smartflow/template/data-access';
import { TranslateService } from '@ngx-translate/core';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { CampaignModel, CsvFileData, getEmailCampaign, MetadataPayloadType } from '../../models';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import {StepOnActivated} from "./campaign-step";

@Injectable()
export class CampaignEffect {
  generateEmailLabel: any;
  messageProps: any;

  constructor(
    private actions: Actions,
    private service: FollowMyCampaignService,
    private store: Store,
    private route: Router,
    private templateService: TemplateService,
    private translate: TranslateService,
    private snackBarService: SnackBarService,
    private activateRoute: ActivatedRoute,
    private router: Router,
    private confirmService: ConfirmationMessageService
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
    this.translate
      .get('cxmCampaign.followMyCampaign.generateEmail')
      .subscribe((v) => (this.generateEmailLabel = v));
    this.translate
      .get('cxmCampaign.message')
      .pipe(take(1))
      .subscribe((v) => (this.messageProps = v));
  }

  submitEmailCampaignParameterStep$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.submitEmailCampaignParameterStep),
      withLatestFrom(this.store.select(fromSelector.selectCampaignDetail)),
      switchMap(([args, campaign]) => {
        let { details } = campaign;
        details = {
          ...details,
          senderMail: args.parameter.senderMail,
          unsubscribeLink: args.parameter.unsubscribeLink
        };

        campaign = {
          ...campaign,
          details,
          campaignName: args.parameter.campaignName,
          senderName: args.parameter.senderName,
          subjectMail: args.parameter.subjectMail,
          step: 3,
          attachments: args?.parameter?.attachments
        };

        return this.service.updateEmailCampaign(campaign as CampaignModel).pipe(
          map((response) =>
            fromAction.submitEmailCampaignParameterSuccess({
              emailCampaign: response
            })
          ),
          catchError(() => of(fromAction.submitEmailCampaignSummaryStepFail()))
        );
      })
    )
  );

  submitEmailCampaignParameterSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.submitEmailCampaignParameterSuccess),
        tap((args) => {
          // this.snaceBarService.openCustomSnackbar({message: this.generateEmailLabel?.message?.updateSuccess, icon: 'close', type: 'success'});
          this.route.navigate([appRoute.cxmCampaign.followMyCampaign.emailCampaignSummary, args?.emailCampaign?.id]);
        })
      ),
    { dispatch: false }
  );

  submitEmailCampaignParameterFail$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.submitEmailCampaignParameterFail),
    exhaustMap(args => {
      const { apierrorhandler } = args.httpErrorResponse.error;

      if(apierrorhandler) {
        if([4005].includes(apierrorhandler.statusCode)) {
          return [fromAction.distributionEmailChannelDoesNotConfig()];
        }
      }

      return [fromAction.alertSubmitEmailCampaignParameterFail()];
    })
  ));

  alertSubmitEmailCampaignParameterFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.alertSubmitEmailCampaignParameterFail),
        tap(() => {
          this.snackBarService.openCustomSnackbar({
            message: this.generateEmailLabel?.message?.updateFail,
            icon: 'close',
            type: 'error'
          });
        })
      ),
    { dispatch: false }
  );

  distributionEmailChannelDoesNotConfig$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.distributionEmailChannelDoesNotConfig),
    tap(() => {
      this.translate.get('flow.deposit.channel_category_not_activated').toPromise().then(messages => {
        this.confirmService.showConfirmationPopup({
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
          }
        })
      });
    })
  ), { dispatch: false });

  previousEmailCampaignParameterStep$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.previousEmailCampaignParameterStep),
        withLatestFrom(this.store.select(fromSelector.selectCampaignDetail)),
        exhaustMap(([args, form]) => {
          this.route.navigate(
            [appRoute.cxmCampaign.followMyCampaign.emailCampaignDestination],
            { queryParams: { templateId: form?.templateId, id: form.id } }
          );

          // Remove attachments that has uploaded, that not yet save in campaign.
          return of(removeAttachmentUploadedWhenLeavePage());
        })
      )
  );

  previousEmailCampaignEnvoiStep$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.previousEmailCampaignEnvoiStep),
        withLatestFrom(this.store.select(fromSelector.selectCampaignDetail)),
        exhaustMap(([args, form]) => {
          this.route.navigate([appRoute.cxmCampaign.followMyCampaign.emailCampaignParameter, form.id]);
          return of(closeEmailLoading());
        })
      )
  );

  csvUploadEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.uploadCSVFileAction),
      withLatestFrom(this.store.select(fromSelector.selectEmailState)),
      exhaustMap(([args, emailState]) => {
        const { mode, csvData, templateId, hasHeader, checkSameMail, campaign } = emailState;

        const dirs = mode === 1 ? 'csv' : 'tmp';
        let isKeepOriginalName = false;
        let filename = undefined;

        if (mode === 1) {
          isKeepOriginalName = true;
          filename = csvData.fileName || campaign?.details?.csvName;
        }

        return this.service
          .uploadFile(args.file, {
            dirs,
            filename,
            isKeepOriginalName,
            templateId,
            type: 'EMAIL',
            hasHeader,
            removeDuplicate: checkSameMail
          })
          .pipe(
            map(event => {
              if (event.type == HttpEventType.Response && event.ok) {
                return fromAction.uploadCSVFileActionResponse({
                  res: event.body
                });
              } else if (event.type == HttpEventType.UploadProgress) {
                if (event.total) {
                  const progress = Math.round((event.loaded / event.total) * 100);
                  return fromAction.uploadCSVProgresssion({ progress });
                }
              }


              return fromAction.nonceAction();
            }),
            catchError(err => of(fromAction.uploadCSVFileActionFail({ error: err })))
          );
      })
    )
  );

  csvUploadFailEffect$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.uploadCSVFileActionFail),
    tap(args => {

      this.translate.get('cxmCampaign.followMyCampaign.import_file_upload_fail').toPromise()
        .then(message => this.snackBarService.openCustomSnackbar({ icon: 'close', type: 'error', message }));

    })
  ), { dispatch: false });


  submitDestinationStep$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.submitDestinationStep),
      withLatestFrom(this.store.select(fromSelector.selectEmailState)),
      exhaustMap(([arg, emailState]) => {
        // eslint-disable-next-line prefer-const
        let { mode, campaign, csvFilter, checkSameMail, csvData, hasHeader, isCheckHeaderChange } = emailState;

        if (mode === 1) {
          let details = {
            ...campaign?.details,
            csvRecordCount: csvData.count,
            removeDuplicate: checkSameMail,
            csvOriginalName: csvData?.originalName,
            errorCount: csvData.invalidCount,
          };

          // Check if checkbox is update new data.
          if (isCheckHeaderChange) {
            details = {
              ...details,
              csvHasHeader: hasHeader
            }
          }

          campaign = {
            ...campaign,
            details: {
              ...details
            },
            step: 2,
            mode: 'Manual',
            channel: 'Digital'
          };
          return this.service.updateEmailCampaign(campaign).pipe(
            map((value) =>
              fromAction.submitDestinationSuccess({ campaign: value })
            ),
            catchError(() => of(fromAction.submitDestinationFail(false)))
          );
        } else {
          const details = {
            ...arg?.emailCampaign?.details,
            csvRecordCount: csvData.count,
            removeDuplicate: checkSameMail
            // errorCount: csvData.invalidCount
          };

          const payload = {
            ...arg?.emailCampaign,
            details: {
              ...details
            },
            mode: 'Manual',
            channel: 'Digital'
          };
          return this.service.addEmailCampaign(payload).pipe(
            map((value) =>
              fromAction.submitDestinationSuccess({ campaign: value })
            ),
            catchError(() => of(fromAction.submitDestinationFail(false)))
          );
        }
      })
    )
  );

  submitDestinationStepSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.submitDestinationSuccess),
        tap((args) => {
          this.store.dispatch(fromAction.emailFilterCsvFilterChanged({
            filter: { page: 1, pageSize: 10 }
          }));
        })
      ),
    { dispatch: false }
  );

  submitDestinationStepFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.submitDestinationFail),
        tap(() => {
          // this.snackBarService.openCustomSnackbar({
          //   message: this.generateEmailLabel?.message?.createFail,
          //   icon: 'close',
          //   type: 'error'
          // });
        })
      ),
    { dispatch: false }
  );

  loadTemplateDetail$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.loadTemplateDetail),
      switchMap((arg) =>
        this.templateService.getTemplateById(arg.templateId).pipe(
          map((v) => {
            return fromAction.loadTemplateDetailSuccess({ templateDetails: v });
          }),
          catchError((err: HttpErrorResponse) => {
            const { apierrorhandler } = err.error;
            return of(
              fromAction.loadTemplateDetailFail({ error: apierrorhandler })
            );
          })
        )
      )
    )
  );

  loadTemplateDetailSuccessEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.loadTemplateDetailSuccess),
      withLatestFrom(this.store.select(fromSelector.selectEmailState)),
      exhaustMap(([args, emailState]) => {
        const { mode } = emailState;
        if (mode === 1) {
          return of(
            fromAction.emailFilterCsvFilterChanged({
              filter: { page: 1, pageSize: 10 }
            })
          );
        }

        return of(fromAction.nonceAction());
      })
    )
  );

  loadCampaignDetail$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.loadEmailCampaignDetail),
      switchMap((arg) =>
        this.service.getEmailCampaignById(arg.campaignId).pipe(
          map((v) => {
            return fromAction.loadEmailCampaignDetailSuccess({ campaign: v });
          }),
          catchError((err: HttpErrorResponse) => {
            const { apierrorhandler } = err.error;
            return of(
              fromAction.loadEmailCampaignDetailFail({ error: apierrorhandler })
            );
          })
        )
      )
    )
  );

  submitEmailCampaignSummaryStep$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.submitEmailCampaignSummaryStep),
      withLatestFrom(this.store.select(fromSelector.selectCampaignDetail)),
      switchMap(([args, campaign]) => {
        campaign = {
          ...campaign,
          isValidate: true,
          sendingSchedule: args?.summary?.sendingSchedule,
          step: 4
        };

        return this.service.updateEmailCampaign(campaign as CampaignModel).pipe(
          map((response) =>
            fromAction.submitEmailCampaignSummaryStepSuccess({
              emailCampaign: response
            })
          ),
          catchError((httpErrorResponse) => of(fromAction.submitEmailCampaignParameterFail({ httpErrorResponse })))
        );
      })
    )
  );

  submitEmailCampaignSummaryStepSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.submitEmailCampaignSummaryStepSuccess),
        tap(() => {
          this.store.dispatch(StepOnActivated({ active: false, leave: true }));
          this.route.navigate(
            [appRoute.cxmCampaign.followMyCampaign.emailSuccess]
          );
        }),
        switchMap(() => of(fromAction.unloadEmailCampaignFormData()))
      )
  );

  submitEmailCampaignSummaryStepFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.submitEmailCampaignSummaryStepFail),
        tap(() => {
          this.snackBarService.openCustomSnackbar({
            message: this.generateEmailLabel?.message?.updateFail,
            icon: 'close',
            type: 'error'
          });
        })
      ),
    { dispatch: false }
  );

  sendMailTest$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.sendMailTest),
      withLatestFrom(this.store.select(fromSelector.selectCampaignDetail)),
      switchMap(([args, campaign]) => {
        campaign = {
          ...campaign,
          recipientAddress: args.recipientAddress
        };

        return this.service.sendTestMail(campaign as CampaignModel).pipe(
          map(() => fromAction.sendMailTestSuccess({ total: (args.recipientAddress as string[])?.length })),
          catchError(() => of(fromAction.sendMailTestFail({ total: (args.recipientAddress as string[])?.length })))
        );
      })
    )
  );

  sendMailTestSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.sendMailTestSuccess),
        tap((args) => {
          const message = args.total > 1 ? this.generateEmailLabel?.dialog?.message?.successes :
            this.generateEmailLabel?.dialog?.message?.success;

          this.snackBarService.openCustomSnackbar({
            message: message,
            icon: 'close',
            type: 'success'
          });
        })
      ),
    { dispatch: false }
  );

  sendMailTestFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.sendMailTestFail),
        tap((args) => {
          const message = args.total > 1 ? this.generateEmailLabel?.dialog?.message?.fails :
            this.generateEmailLabel?.dialog?.message?.fail;

          this.snackBarService.openCustomSnackbar({
            message: message,
            icon: 'close',
            type: 'error'
          });
        })
      ),
    { dispatch: false }
  );

  prepareEmailCsvUploadEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.prepareEmailCsvUpload),
      withLatestFrom(this.store.select(fromSelector.selectTemplateDetails)),
      exhaustMap(([args, template]) => {
        const { firstrow, matchLength } = args;
        return of(fromAction.emailCheckCsvHeaderValueResult({
          csvOK: matchLength === 0,
          matchLength,
          file: args.file
        }));
      })
    )
  );

  validateEmailCsvNotOkEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.emailCheckCsvHeaderValueResult),
        filter((args) => args.csvOK === false),
        tap((args) => {
          const { csvOK, matchLength } = args;
          // Ooops not correct translate
          Promise.all([
            this.translate
              .get('cxmCampaign.followMyCampaign.import_compliant_file_message')
              .toPromise(),
            this.translate
              .get(
                'cxmCampaign.followMyCampaign.csv_contain_more_column_tooltip'
              )
              .toPromise(),
            this.translate
              .get(
                'cxmCampaign.followMyCampaign.csv_contain_fewer_column_tooltip'
              )
              .toPromise()
          ]).then((msg) => {
            this.snackBarService.openCustomSnackbar({ message: msg[0], icon: 'close', type: 'error' });
          });
        })
      ),
    { dispatch: false }
  );

  validateEmailCsvOkEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.emailCheckCsvHeaderValueResult),
      filter((args) => args.csvOK === true),
      switchMap((args) => {
        if (args.file) {
          return of(fromAction.uploadCSVFileAction({ file: args.file }));
        }

        return of(fromAction.nonceAction());
      })
    )
  );

  uploadCSVFileActionResponseEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.uploadCSVFileActionResponse),
      withLatestFrom(this.store.select(selectEmailState)),
      tap(([args, emailState]) => {
        const { mode } = emailState;

        if (mode === 0) {
          if(args.res.invalidCount !== args.res.lineCount) {
            this.store.dispatch(fromAction.createEmailCampaignAfterUploadedFile());
          } else {
            this.store.dispatch(fromAction.emailFilterCsvFilterChanged({
              filter: { page: 1, pageSize: 10 }
            }));
          }
        } else {
          const { campaign } = emailState;
          this.store.dispatch(
            submitDestinationStep({
              emailCampaign: campaign,
              editMode: true,
            })
          );
        }

        if (args.res.invalidCount > 0) {
          Promise.all([
            this.translate.get('cxmCampaign.followMyCampaign.imported_file_has_error', { invalidCount: args.res.invalidCount }).toPromise(),
            this.translate.get('cxmCampaign.followMyCampaign.imported_file_has_error_instruction').toPromise()
          ])
            .then(msg => this.snackBarService.openCustomSnackbar({
              icon: 'close',
              message: msg[0],
              type: 'error',
              details: msg[1]
            }));
        } else {
          this.translate.get('cxmCampaign.followMyCampaign.imported_file_success').toPromise().then(msg => this.snackBarService.openCustomSnackbar({
            icon: 'close',
            message: msg,
            type: 'success'
          }));
        }
      })
    ), {dispatch: false}
  );

  emailFilterCsvFilterChangedEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.emailFilterCsvFilterChanged),
      withLatestFrom(this.store.select(fromSelector.selectEmailState)),
      exhaustMap(([args, emailState]) => {
        const { filter } = args;
        const {
          csvData,
          templateDetails,
          checkSameMail,
          hasHeader,
          mode
        } = emailState;

        return this.service
          .getCsvRecord(filter.page, filter.pageSize, {
            dir: mode === 1 ? 'csv' : 'tmp',
            fileName: csvData.fileName,
            templateId: templateDetails.id,
            type: 'EMAIL',
            isCount: true,
            removeDuplicate: false,
            hasHeader: false,
            sortByField: filter.sortByField || 'lineNumber',
            sortDirection: filter.sortDirection || 'ASC'
          })
          .pipe(
            map((csvContent) => {
              return fromAction.recordEmailDataFromCsv({
                data: csvContent.contents,
                filter: {
                  page: csvContent.page,
                  pageSize: csvContent.pageSize,
                  total: csvContent.total
                }
              });
            })
          );
      })
    )
  );

  fetchCampaignCsv$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.fetchCampaignCsv),
      withLatestFrom(this.store.select(fromSelector.selectCampaignDetail)),
      exhaustMap(([args, campaign]) => {
        return this.service
          .getEmailCampaignCsv(
            campaign.details.csvName,
            campaign.details.csvPath
          )
          .pipe(
            map((res) => fromAction.fetchCampaignCsvResponse({ csv: res }))
          );
      })
    )
  );

  preloadCampaignEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.preloadEmailCampaign),
      switchMap((args) =>
        of(fromAction.loadEmailCampaignDetail({ campaignId: args.campaignId }))
      )
    )
  );

  preloadTemplateEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromAction.loadEmailCampaignDetailSuccess),
      tap(args => {
        const isValidCSVRecord = (args.campaign?.details?.csvRecordCount || 0) - (args.campaign?.details?.errorCount || 0) > 0;
        if (!isValidCSVRecord && !location.pathname.includes(appRoute.cxmCampaign.followMyCampaign.emailCampaignDestination)) {
          this.store.dispatch(previousEmailCampaignParameterStep({}));
        }

        this.store.dispatch(fromAction.loadTemplateDetail({
          templateId: args.campaign.templateId || 0
        }));

      })
    ), { dispatch: false }
  );

  loadTemplateDetailFailEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.loadTemplateDetailFail),
        tap((args) => {
          const { error } = args;
          if (error) {
            this.translate
              .get('template.message')
              .toPromise()
              .then((messageProps) => {
                if (error.statusCode === 403) {
                  this.confirmService.showConfirmationPopup({
                    icon: 'close',
                    title: messageProps.unauthorize,
                    message: messageProps.unauthorizeAccess,
                    cancelButton: messageProps.unauthorizeCancel,
                    confirmButton: messageProps.unauthorizeLeave,
                    type: 'Warning'
                  })
                    .subscribe(() => {
                      this.route.navigateByUrl(`${appRoute.cxmCampaign.followMyCampaign.emailChoiceOfModel}`);
                    });
                } else if (error.statusCode === 404) {
                  this.confirmService.showConfirmationPopup({
                    icon: 'close',
                    title: messageProps.unauthorizeAccess,
                    cancelButton: messageProps.unauthorizeCancel,
                    confirmButton: messageProps.unauthorizeLeave,
                    type: 'Warning'
                  })
                    .subscribe(() => {
                      this.route.navigateByUrl(`${appRoute.cxmCampaign.followMyCampaign.emailChoiceOfModel}`);
                    });
                } else {
                  this.confirmService.showConfirmationPopup({
                    icon: 'close',
                    title: messageProps.unknownError,
                    cancelButton: messageProps.unauthorizeCancel,
                    confirmButton: messageProps.unauthorizeLeave,
                    type: 'Warning'
                  })
                    .subscribe(() => {
                      this.route.navigateByUrl(`${appRoute.cxmCampaign.followMyCampaign.emailChoiceOfModel}`);
                    });
                }
              });
          }
        })
      ),
    { dispatch: false }
  );

  loadEmailCampaignDetailFailEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.loadEmailCampaignDetailFail),
        tap((args) => {
          const { error } = args;

          if (error) {
            this.translate
              .get('template.message')
              .toPromise()
              .then((messageProps) => {
                if (error.statusCode === 403) {
                  this.confirmService.showConfirmationPopup({
                    icon: 'close',
                    title: messageProps.unauthorize,
                    message: messageProps.unauthorizeAccess,
                    cancelButton: messageProps.unauthorizeCancel,
                    confirmButton: messageProps.unauthorizeLeave,
                    type: 'Warning'
                  }).subscribe(() => {
                    this.route.navigateByUrl(`${appRoute.cxmCampaign.followMyCampaign.emailChoiceOfModel}`);
                  });
                } else if (error.statusCode === 404) {
                  this.confirmService.showConfirmationPopup({
                    icon: 'close',
                    title: messageProps.unauthorizeAccess,
                    cancelButton: messageProps.unauthorizeCancel,
                    confirmButton: messageProps.unauthorizeLeave,
                    type: 'Warning'
                  }).subscribe(() => {
                    this.route.navigateByUrl(`${appRoute.cxmCampaign.followMyCampaign.emailChoiceOfModel}`);
                  });
                } else {
                  this.confirmService.showConfirmationPopup({
                    icon: 'close',
                    title: messageProps.unknownError,
                    cancelButton: messageProps.unauthorizeCancel,
                    confirmButton: messageProps.unauthorizeLeave,
                    type: 'Warning'
                  }).subscribe(() => {
                    this.route.navigateByUrl(`${appRoute.cxmCampaign.followMyCampaign.emailChoiceOfModel}`);
                  });
                }
              });
          }
        })
      ),
    { dispatch: false }
  );

  alertEmailCampaignDetailError$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromAction.alertEmailCampaignDetailError),
        tap((args) => {
          const { statusCode } = args.httpError.error.apierrorhandler;

          if (args.httpError.error.apierrorhandler) {
            if (statusCode === 403 || statusCode === 401) {
              this.confirmService.showConfirmationPopup({
                icon: 'close',
                title: this.messageProps?.unauthorizedTitle,
                message: this.messageProps?.unauthorizedMessage,
                cancelButton: this.messageProps.unauthorizeCancel,
                confirmButton: this.messageProps?.unauthorizedLeave,
                type: 'Warning'
              }).subscribe(() => {
                this.route.navigateByUrl(`${appRoute.cxmCampaign.followMyCampaign.listEmailCampaign}`);
              });
            } else if (statusCode === 404) {
              this.confirmService.showConfirmationPopup({
                icon: 'close',
                title: this.messageProps?.notFoundCampaignDetailTitle,
                message: this.messageProps?.notFoundViewCampaignDetailMessage,
                cancelButton: this.messageProps.unauthorizeCancel,
                confirmButton: this.messageProps?.unauthorizedLeave,
                type: 'Warning'
              }).subscribe(() => {
                this.route.navigateByUrl(`${appRoute.cxmCampaign.followMyCampaign.listEmailCampaign}`);
              });
            } else if (statusCode >= 500) {
              this.snackBarService.openCustomSnackbar({
                message: this.messageProps?.unknownError,
                icon: 'close',
                type: 'error'
              });
            }
          }
        })
      ),
    { dispatch: false }
  );

  getMaxFileSizeUpload$ = createEffect(() => this.actions.pipe(
    ofType(getMaxFileSizeUpload),
    switchMap(() => this.service.getLimitUploadFileSize()
      .pipe(
        map(limitSize => getMaxFileSizeUploadSuccess({ limitSize })), catchError(() => [getMaxFileSizeUploadFailed()])))));

  getMaxFileSizeUploadFailed$ = createEffect(() => this.actions.pipe(ofType(getMaxFileSizeUploadFailed), tap(() => {
    this.translate.get('cxmCampaign.followMyCampaign.limitSizeLoadFailed').toPromise().then(message => {
      this.snackBarService.openCustomSnackbar({
        icon: 'close',
        message,
        type: 'error'
      });
    });
  })), { dispatch: false });

  uploadAttachments$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.uploadAttachments),
    withLatestFrom(this.store.select(fromSelector.selectAttachmentsUploaded)),
    switchMap(([args, selectAttachmentsUploaded]) => this.service.uploadAttachments(args.formData).pipe(
      map((event: any) => {
        if (event?.type === HttpEventType.Sent) {
          return fromAction.uploadAttachmentsAction();
        } else if (event?.type === HttpEventType.UploadProgress) {
          if (event?.total) {
            const progress = Math.round((event.loaded / event.total) * 100);
            return fromAction.uploadAttachmentsProgression({ progress: progress });
          }
        } else if (event.type === HttpEventType.Response) {
          if (event?.ok) {
            return fromAction.uploadAttachmentSuccess({
              attachmentResponse: event?.body,
              oldAttachments: selectAttachmentsUploaded
            });
          } else {
            return fromAction.uploadAttachmentFail({ error: event?.body });
          }
        }
        return fromAction.uploadAttachmentsAction();
      }),
      catchError((error: HttpErrorResponse) => of(fromAction.uploadAttachmentFail({ error: error })))
    ))
  ));

  uploadAttachmentFail$ = createEffect(() => this.actions.pipe(
      ofType(fromAction.uploadAttachmentFail),
      tap(() => {
        this.translate.get('cxmCampaign.followMyCampaign.settingParameter.attachment.message.uploadFail').toPromise()
          .then(message => this.snackBarService.openCustomSnackbar({ icon: 'close', type: 'error', message }));
      })
    ),
    { dispatch: false });

  removeAttachmentUploadedWhenLeavePage$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.removeAttachmentUploadedWhenLeavePage),
    withLatestFrom(this.store.select(selectCampaignWithAttachments)),
    exhaustMap(([args, selectCampaignWithAttachments]) => {
      const { campaign, attachments } = selectCampaignWithAttachments;
      const campaignAttachments = campaign?.attachments as CustomFileModel[];
      const attachmentsUploaded = attachments as CustomFileModel[];

      // True, if attachments upload not equal with campaign attachment.
      // Then, we remove attachments upload that not save yet.
      if (!(JSON.stringify(attachmentsUploaded) === JSON.stringify(campaignAttachments))) {

        // Set new fileId that has uploaded not yet save email campaign.
        const fileIdsAttachmentUploadAble = attachmentsUploaded?.filter((attachment: CustomFileModel) => {
          return campaignAttachments.indexOf(attachment) === -1;
        }).map(value => value.fileId) as string [];

        if (fileIdsAttachmentUploadAble.length > 0) {
          return of(fromAction.removeAttachment({ fileIds: fileIdsAttachmentUploadAble }));
        }
      }

      return of(fromAction.leavePageNoNeedRemoveAttachment());
    })
  ));

  updateCampaignParameterWhenLeavePage$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.updateCampaignParameterWhenLeavePage),
    withLatestFrom(this.store.select(fromSelector.selectParameterFormTemporary), this.store.select(fromSelector.selectCampaignDetail)),
    exhaustMap(([args, selectParameterFormTemporary, selectCampaignDetail]) => {

      const {
        campaignName,
        subjectMail,
        senderMail,
        senderName,
        unsubscribeLink,
        attachments
      } = selectParameterFormTemporary;

      let { details } = selectCampaignDetail;
      details = {
        ...details,
        senderMail: senderMail,
        unsubscribeLink: unsubscribeLink
      };

      const campaign: CampaignModel = {
        ...selectCampaignDetail,
        details,
        campaignName: campaignName,
        senderName: senderName,
        subjectMail: subjectMail,
        attachments: attachments
      };

      return this.service.updateEmailCampaign(campaign).pipe(
        map(() => fromAction.updateCampaignParameterWhenLeavePageSuccess()),
        catchError(() => of(fromAction.updateCampaignParameterWhenLeavePageFail()))
      );
    })
  ));

  removeAttachment$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.removeAttachment),
    withLatestFrom(this.store.select(fromSelector.selectAttachmentsUploaded)),
    switchMap(([args, selectAttachmentsUploaded]) => this.service.removeAttachment(args.fileIds).pipe(
        map(() => {
          return fromAction.removeAttachmentSuccess({ fileIds: args.fileIds, attachments: selectAttachmentsUploaded });
        }),
        catchError((error: HttpErrorResponse) => of(fromAction.removeAttachmentFail({ error: error })))
      )
    )
  ));

  removeAttachmentTemporary$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.removeAttachmentOnTemporary),
    withLatestFrom(this.store.select(fromSelector.selectAttachmentsUploaded)),
    switchMap(([args, selectAttachmentsUploaded]) => [fromAction.removeAttachmentSuccess({
      fileIds: args.fileIds,
      attachments: selectAttachmentsUploaded
    })])
  ));

  removeAttachmentSuccess$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.removeAttachmentSuccess),
    tap(() => {
    })
  ), { dispatch: false });

  removeAttachmentFail$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.removeAttachmentFail),
    tap(() => {
      alert('Fail to remove attachment');
    })
  ), { dispatch: false });

  createEmailCampaignAfterUploadedFile$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.createEmailCampaignAfterUploadedFile),
    withLatestFrom(
      this.store.select(fromSelector.selectEmailCsvForm),
      this.store.select(fromSelector.selectTemplateDetails),
      this.store.select(fromSelector.selectCsvState),
      this.store.select(fromSelector.selectEmailState)),
    exhaustMap(([arg, emailCsvForm, templateDetail, csvState, emailState]) => {
      const { checkSameMail, csvData } = emailState;

      const emailCampaign = getEmailCampaign(
        templateDetail as TemplateModel,
        csvState as CsvFileData,
        2,
        emailCsvForm.hasHeader as boolean
      );
      const details = {
        ...emailCampaign?.details,
        csvRecordCount: csvData.count,
        removeDuplicate: checkSameMail
      };

      const payload = {
        ...emailCampaign,
        details: {
          ...details
        },
        mode: 'Manual',
        channel: 'Digital'
      };
      return this.service.addEmailCampaign(payload).pipe(
        map((campaignResponse) => fromAction.createEmailCampaignAfterUploadedFileSuccess({ campaignResponse })),
        catchError(() => of(fromAction.createEmailCampaignAfterUploadedFileFail()))
      );
    })
  ));

  createEmailCampaignAfterUploadedFileSuccess$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.createEmailCampaignAfterUploadedFileSuccess),
    withLatestFrom(this.store.select(fromSelector.selectEmailState)),
    exhaustMap(([args, emailState]) => {
      const {
        csvData,
        templateDetails,
      } = emailState;

      const { id } = args.campaignResponse;
      this.activateRoute.queryParams.pipe(take(1)).subscribe((params) => {
        this.router.navigate([appRoute.cxmCampaign.followMyCampaign.emailCampaignDestination], { queryParams: {...params, id }, replaceUrl: true});
      });

      return this.service
        .getCsvRecord(1, 10, {
          dir: 'csv',
          fileName: csvData.fileName,
          templateId: templateDetails.id,
          type: 'EMAIL',
          isCount: true,
          removeDuplicate: false,
          hasHeader: false,
          sortByField: 'lineNumber',
          sortDirection: 'ASC'
        })
        .pipe(
          map((csvContent) => {
            return fromAction.recordEmailDataFromCsv({
              data: csvContent.contents,
              filter: {
                page: csvContent.page,
                pageSize: csvContent.pageSize,
                total: csvContent.total
              }
            });
          })
        );
    })
  ));

  recordEmailDataFromCsv$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.recordEmailDataFromCsv),
    tap(() => {
      interval(500).pipe(take(1)).subscribe(() => {
        this.store.dispatch(fromAction.closeEmailLoading());
      })
    })
  ), { dispatch: false });

  getEmailMetadata$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.getEmailMetadata),
    exhaustMap((args) => {
      const types: Array<MetadataPayloadType> = ['unsubscribe_link', 'sender_name', 'sender_mail'];
      return this.service.getMetadata(types).pipe(
        map(metadataResponse => fromAction.getEmailMetadataSuccess({ metadataResponse })),
        catchError(httpErrorResponse => [fromAction.getEmailMetadataFail({ httpErrorResponse })]),
      )
    })
  ));

  getEmailMetadataFail$ = createEffect(() => this.actions.pipe(
    ofType(fromAction.getEmailMetadataFail),
    tap(() => {
      this.translate.get('client.metadata_fetch_fail').toPromise().then(message => {
        this.snackBarService.openCustomSnackbar({ icon: 'close', type: 'error', message });
      });
    })
  ), { dispatch: false });
}
