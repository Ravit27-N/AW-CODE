import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {FlowDepositService} from '@cxm-smartflow/flow-deposit/data-access';
import {SnackBarService, UserUtil} from '@cxm-smartflow/shared/data-access/services';
import {ConfirmationMessageService} from '@cxm-smartflow/shared/ui/comfirmation-message';
import {appRoute} from '@cxm-smartflow/template/data-access';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Store} from '@ngrx/store';
import {TranslateService} from '@ngx-translate/core';
import {forkJoin, of} from 'rxjs';
import {catchError, exhaustMap, filter, map, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {
  createModifyConfigurationFilePayload,
  DEFAULT_SECTION_KEY,
  fromModifyClientActions,
  fromModifyClientSelector
} from '.';
import {ClientService} from '../../services/client.service';
import * as payloadUtils from './client-payload.utils';
import * as fromActions from './modification.actions';
import {initialState} from './modification.reducers';
import {FileSaverUtil} from '@cxm-smartflow/shared/utils';
import * as fromClientFluxExt from './flux-ext';
import {
  HubDistributePopupService,
  MetaDataPopupService,
  ServiceProviderPopupService
} from '@cxm-smartflow/client/ui/client-popup-dialog';
import {ConfigurationRegistrationModel, DayOfWeekUnloadingModel, MetadataModel} from '../../models';
import {PostalConfigurationVersion} from '../../models/postal-configuration-version.model';
import {
  fetchConfigurationVersion,
  fetchModelConfigurationsSuccess,
  fetchTheLatestVersionConfiguration, revertConfiguration
} from './modification.actions';

@Injectable()
export class ClientCeationModifyEffect {
  uploadClientDocumentEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.uploadClientDocument),
      exhaustMap((arg) => {
        return this.service.uploadClientDocument(arg.form).pipe(
          map((event) => {
            if (event.type === HttpEventType.Response) {
              if (event.ok) {
                const body = event.body;
                return fromActions.uploadClientDocumentSuccess({
                  file: {
                    fileId: body.fileId,
                    filename: body.fileName,
                    fileSize: body.fileSize,
                  },
                });
              } else {
                return fromActions.uploadClientDocumentFail();
              }
            }
            return fromActions.uploadClientDocumentFail();
          })
        );
      })
    )
  );

  attempToMoveNextEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.attempToMoveNextStep),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectNavigation),
        this.store.select(fromModifyClientSelector.selectMode),
        this.store.select(fromModifyClientSelector.selectClientFunctionality)
      ),
      filter((x) => x[2] === 0), // only in create mode
      switchMap(([args, navigation, mode, functionalities]) => {
        // evaluate condition to next step
        if (navigation.step === 1) {
          return [fromActions.switchToStep({ step: 2 })];
        }

        if (navigation.step === 2) {
          // validate requirement for step 2 creation

          return [fromActions.validteBeforeSubmit()];
          // return [fromActions.submitCreateClient()];
        }

        if (navigation.step === 3) {
          // validate requirement for step 3 creation
          if (functionalities.length === 0) {
            this.translate
              .get('client.messages.atLeastOneFunctionality')
              .toPromise()
              .then((message) => {
                this.snackbar.openCustomSnackbar({
                  icon: 'closed',
                  message,
                  type: 'error',
                });
              });
            return [fromActions.nonceAction()];
          }
          return mode === 0
            ? [fromActions.submitCreateClient()]
            : [fromActions.submitModifyClient()]; //create : modify
          // return [fromActions.submitCreateClient()];
        }

        return [fromActions.unloadClientForm()];
      })
    )
  );

  attempToMovePrevEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.attempToMovePrevStep),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectNavigation),
        this.store.select(fromModifyClientSelector.selectClientData),
        this.store.select(fromModifyClientSelector.selectMode),
        this.store.select(fromModifyClientSelector.selectTempClient),
        this.store.select(fromModifyClientSelector.selectFormClientState)
      ),
      switchMap(
        ([args, navigation, clientData, mode, tempClient, formClientState]) => {
          const { divisions, functionalities } = formClientState;
          const payload = payloadUtils.prepareCreateClientPayload(
            clientData,
            divisions,
            functionalities
          );
          const originalInfo = payloadUtils.prepareCreateClientPayload(
            mode ? tempClient?.clientInfo?.client : initialState.client,
            mode ? tempClient?.clientInfo?.divisions : initialState.divisions,
            mode
              ? tempClient?.clientInfo?.functionalities
              : initialState.functionalities
          );

          const { step } = navigation;
          if (step !== 1) {
            if (mode) {
              return [fromActions.switchToStep({ step: navigation.step })];
            } else {
              return [fromActions.switchToStep({ step: step - 1 })];
            }
          }

          if (navigation.step === 1) {
            if (JSON.stringify(payload) !== JSON.stringify(originalInfo)) {
              this.router.navigateByUrl(
                appRoute.cxmClient.navigateToListClient
              );
              return [fromActions.switchToStep({ step: navigation.step })];
            }
            this.router.navigateByUrl(appRoute.cxmClient.navigateToListClient);
          }

          return [fromActions.unloadClientForm()];
        }
      )
    )
  );

  submitCreateClientEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.submitCreateClient),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientData),
        this.store.select(fromModifyClientSelector.selectClientDivision),
        this.store.select(fromModifyClientSelector.selectClientFunctionality)
      ),
      exhaustMap(([args, clientData, divisionData, functionalityData]) => {
        const payload = payloadUtils.prepareCreateClientPayload(
          clientData,
          divisionData,
          functionalityData
        );

        return this.service.createClient(payload).pipe(
          map((res) => fromModifyClientActions.submitCreateClientSuccess()),
          catchError((err) =>
            of(fromModifyClientActions.submitCreateClientFail())
          )
        );
      })
    )
  );

  loadClientFormEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.loadClientForm),
      filter((args) => args.clientId !== undefined && args.clientId !== null),
      withLatestFrom(this.store.select(fromClientFluxExt.selectClientFluxExt)),
      exhaustMap(([args, clientFlux]) => {
        const { clientId } = args;

        // restore state from previous modify form
        if (clientFlux.preserved) {
          return [
            fromActions.restoreFormData({
              data: {
                ...clientFlux.data,
                criteriaDistributions:
                  clientFlux.data.criteriaDistributionsSnapshot,
              },
            }),
          ];
        }

        return forkJoin([
          this.service.getClientInfo(clientId || ''),
          this.service.getHoliday(),
        ]).pipe(
          map(([res, h]) => {
            const clientInfo = payloadUtils.prepareClientInfoResponse(res);
            const holidays = payloadUtils.aggregateHoliday(h);

            return fromActions.loadClientInfoSuccess({ clientInfo, holidays });
          }),
          catchError((err) =>
            of(fromActions.loadClientInfoFail({ httpError: err }))
          )
        );
      })
    )
  );

  restoreFormDataEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.restoreFormData),
      switchMap((args) => {
        return [fromClientFluxExt.clearPreserveState()];
      })
    )
  );

  attempToMoveNext2Effect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.attempToMoveNextStep),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectNavigation),
        this.store.select(fromModifyClientSelector.selectMode)
      ),
      filter((x) => x[2] === 1), // only in edit mode
      switchMap(([args, navigation, mode]) => {
        // return [fromActions.submitModifyClient()]
        return [fromActions.validteBeforeSubmit()];
      })
    )
  );

  attepmToMovePrev2Effect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.attempToMovePrevStep),
        withLatestFrom(
          this.store.select(fromModifyClientSelector.selectNavigation),
          this.store.select(fromModifyClientSelector.selectMode)
        ),
        filter((x) => x[2] === 1), // only in edit mode
        tap(([args, navigation, mode]) => {
          this.router.navigateByUrl(appRoute.cxmClient.navigateToListClient);
        })
      ),
    { dispatch: false }
  );

  submitModifyClientEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.submitModifyClient),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientData),
        this.store.select(fromModifyClientSelector.selectClientDivision),
        this.store.select(fromModifyClientSelector.selectClientId),
        this.store.select(fromModifyClientSelector.selectClientFunctionality),
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(
        ([
          args,
          clientData,
          divisionData,
          clientId,
          functionalities,
          clientStates,
        ]) => {
          const {
            offloadConfig,
            fillers,
            depositModesPayload,
            portalConfigEnable,
          } = clientStates;

          const payload = payloadUtils.prepareModifyClientPayload(
            clientId,
            clientData,
            divisionData,
            functionalities,
            offloadConfig,
            fillers,
            depositModesPayload,
            portalConfigEnable
          );

          return this.service.updateClient(payload).pipe(
            map((res) => fromModifyClientActions.submitModifyClientSuccess()),
            catchError((err) =>
              of(
                fromModifyClientActions.submitModifyClientFail({
                  httpError: err,
                })
              )
            )
          );
        }
      )
    )
  );

  submitModifyClientSuccessEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromModifyClientActions.submitModifyClientSuccess),
        tap((args) => {
          this.translate
            .get('client.messages.modifySuccess')
            .toPromise()
            .then((msg) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'success',
                message: msg,
              });
            });
          this.router.navigateByUrl(appRoute.cxmClient.navigateToListClient);
        })
      ),
    { dispatch: false }
  );

  submitModifyClientFailEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromModifyClientActions.submitModifyClientFail),
        tap((args) => {
          this.translate
            .get('client.messages.modifyFail')
            .toPromise()
            .then((msg) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message: msg,
              });
            });
        })
      ),
    { dispatch: false }
  );

  submitCreateClientSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.submitCreateClientSuccess),
        tap(() => {
          this.translate
            .get('client.create.createSuccess')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'success',
                message,
              });
            });
          this.store.dispatch(fromModifyClientActions.unloadClientForm());
          this.router
            .navigateByUrl(appRoute.cxmClient.navigateToListClient)
            .then();
        })
      ),
    { dispatch: false }
  );

  submitCreateClientFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.submitCreateClientFail),
        tap(() => {
          this.translate
            .get('client.create.createFail')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  deleteUploadedFile$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.removeUploadedFile),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientData),
        this.store.select(fromModifyClientSelector.modifyDocIdSelector)
      ),
      exhaustMap(([args, clientData, modifyDocId]) => {
        if (modifyDocId && clientData?.file?.fileId === modifyDocId) {
          return of(fromActions.removeUploadedFileSuccess());
        }
        return !clientData?.file?.fileId
          ? of(fromActions.removeUploadedFileSuccess())
          : this.service
              .deleteClientPrivacyDoc(clientData?.file?.fileId)
              .pipe(map(() => fromActions.removeUploadedFileSuccess()));
      })
    )
  );

  validateFormHasChange$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.validateFormHasChange),
        withLatestFrom(
          this.store.select(fromModifyClientSelector.selectClientAllStates)
        ),
        tap(([args, allStates]) => {
          if (payloadUtils.checkFormHasChange(allStates)) {
            this.store.dispatch(fromActions.setLockedFormTrue());
          } else {
            this.store.dispatch(fromActions.setLockedFormFalse());
          }
        })
      ),
    { dispatch: false }
  );

  updateClientForm$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.updateClientForm),
      switchMap(() => [fromActions.validateFormHasChange()])
    )
  );

  updateClientDivision$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.updateClientDivision),
      switchMap(() => [fromActions.validateFormHasChange()])
    )
  );

  uploadClientDocumentSuccess$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.uploadClientDocumentSuccess),
      switchMap(() => [fromActions.validateFormHasChange()])
    )
  );

  removeUploadedFileSuccess$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.removeUploadedFileSuccess),
      switchMap(() => [fromActions.validateFormHasChange()])
    )
  );

  attempToMovePrevStep$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.attempToMovePrevStep),
      switchMap(() => [fromActions.validateFormHasChange()])
    )
  );

  updateClientFunctionality$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.updateClientFunctionality),
      switchMap(() => [fromActions.validateFormHasChange()])
    )
  );

  updateClientOfloading$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.updateClientOfloading),
      switchMap(() => [fromActions.validateFormHasChange()])
    )
  );

  attemptDepositModeToForm$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.attemptDepositModeToForm),
      switchMap(() => [fromActions.validateFormHasChange()])
    )
  );

  setLockedFormFalse$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.submitCreateClient, fromActions.submitModifyClient),
        tap(() => {
          this.store.dispatch(fromActions.setLockedFormFalse());
        })
      ),
    { dispatch: false }
  );

  loadClientInfoFailEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromModifyClientActions.loadClientInfoFail),
        tap((args) => {
          const { apierrorhandler } = args.httpError.error;
          if (apierrorhandler) {
            if ([403, 404, 401].includes(apierrorhandler.statusCode)) {
              Promise.all([
                this.translate.get('template.message').toPromise(),
                this.translate
                  .get('client.messages.clientNotExist')
                  .toPromise(),
              ]).then(([messageProps, clientNotExist]) => {
                this.confirmMessageService
                  .showConfirmationPopup({
                    icon: 'close',
                    title: messageProps.unauthorize,
                    message: clientNotExist,
                    cancelButton: messageProps.unauthorizeCancel,
                    confirmButton: messageProps.unauthorizeLeave,
                    type: 'Warning',
                  })
                  .subscribe(() =>
                    this.router.navigateByUrl(
                      appRoute.cxmClient.navigateToListClient
                    )
                  );
              });
            }
          }
        })
      ),
    { dispatch: false }
  );

  valiateBeforeCreateEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromModifyClientActions.validteBeforeSubmit),
        withLatestFrom(
          this.store.select(fromModifyClientSelector.selectClientDivision),
          this.store.select(fromModifyClientSelector.selectMode),
          this.store.select(fromModifyClientSelector.selectClientFunctionality),
          this.store.select(fromModifyClientSelector.selectClientFillers),
          this.store.select(fromModifyClientSelector.selectOffloadConfig)
        ),
        switchMap(
          ([
            args,
            clientDivision,
            mode,
            functionalityData,
            fillers,
            unloading,
          ]) => {
            const inValidUnloading = this.isInValidUnloading(
              unloading.byDays as DayOfWeekUnloadingModel[]
            );
            if (inValidUnloading) {
              this.translate
                .get('client.messages.registerNotPossible')
                .toPromise()
                .then((message) => {
                  this.snackbar.openCustomSnackbar({
                    icon: 'closed',
                    message,
                    type: 'error',
                  });
                });

              return [fromActions.nonceAction()];
            }
            const zeroLength = clientDivision.length === 0;
            const zeroLengthServices = Array.from(clientDivision).some(
              (d: any) => Array.from(d.services).length === 0
            );
            const isExceedFillerLength = Array.from(fillers).some(
              (x: any) => x.value.length > 20
            );

            if (isExceedFillerLength) return [fromActions.nonceAction()];

            if (!(zeroLength || zeroLengthServices)) {
              if (UserUtil.isAdmin()) {
                // return mode === 0 ? [fromActions.switchToStep({ step: 3 })] : [fromActions.submitModifyClient()]; //create : modify
                if (mode === 0) {
                  return [fromActions.switchToStep({ step: 3 })];
                } else {
                  if (this.validateClientFunctionality(functionalityData)) {
                    return [fromActions.nonceAction()];
                  }
                  return [fromActions.submitModifyClient()];
                }
              } else {
                return mode === 0
                  ? [fromActions.submitCreateClient()]
                  : [fromActions.submitModifyClient()]; //create : modify
              }
            } else {
              this.translate
                .get('client.messages.atleastOndivService')
                .toPromise()
                .then((message) => {
                  this.snackbar.openCustomSnackbar({
                    icon: 'closed',
                    message,
                    type: 'error',
                  });
                });
            }

            return [fromActions.nonceAction()];
          }
        )
      ),
    { dispatch: true }
  );

  forceDechargementEffect$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.forceDechargement),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientId)
      ),
      exhaustMap(([args, clientid]) => {
        return this.flowService.forceDechargement(clientid || '').pipe(
          map((res) => {
            return fromActions.forceDechargementSuccess();
          }),
          catchError((err) => of(fromActions.forceDechargementFail()))
        );
      })
    )
  );

  forceDechargementSuccessEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.forceDechargementSuccess),
        tap(() => {
          this.translate
            .get('client.messages.force_dechargement_success')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'closed',
                message,
                type: 'success',
              });
            });
        })
      ),
    { dispatch: false }
  );

  forceDechargementFailEffect$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.forceDechargementFail),
        tap(() => {
          this.translate
            .get('client.messages.force_dechargement_fail')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'closed',
                message,
                type: 'error',
              });
            });
        })
      ),
    { dispatch: false }
  );

  switchIdentificationMode$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.switchIdentificationMode),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) => {
        const { client, portalConfigEnable } = allStates;
        return this.service
          .switchIdentificationMode(client.name, portalConfigEnable)
          .pipe(
            map((data) =>
              fromModifyClientActions.switchIdentificationModeSuccess()
            )
          );
      })
    )
  );

  attemptToSwitchIdentificationMode$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.attemptToSwitchIdentificationMode),
      switchMap(() => [fromActions.switchIdentificationMode()])
    )
  );

  manageConfigurationFile$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.manageConfigurationFile),
        withLatestFrom(
          this.store.select(fromModifyClientSelector.selectClientAllStates)
        ),
        switchMap(([args, allStates]) => {
          this.router.navigateByUrl(
            `${appRoute.cxmClient.navigateToConfigurationFileClient}/${allStates.configuration_clientName}`
          );
          return [
            fromClientFluxExt.preserveClientFormState({ data: allStates }),
          ];
        })
      ),
    { dispatch: true }
  );

  fetchOrderModelConfiguration$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.fetchModelConfigurations),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) =>
        this.service.getModelsByVersionId(allStates.configuration_clientName, <number>allStates.referenceConfigurationVersion).pipe(
          map((res) =>
            fromActions.fetchModelConfigurationsSuccess({
              configurations: res.configurations,
            })
          ),
          catchError((httpErrorResponse) => [
            fromActions.fetchModelConfigurationsFails({ httpErrorResponse }),
          ])
        )
      )
    )
  );

  fetchTheLatestVersionConfiguration$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.fetchTheLatestVersionConfiguration),
      withLatestFrom(this.store.select(fromModifyClientSelector.selectClientAllStates)),
      exhaustMap(([args, allStates]) =>
        this.service.getModels(allStates.configuration_clientName).pipe(
          map((res) =>
            fromActions.fetchModelConfigurationsSuccess({
              configurations: res.configurations,
            })
          ),
          catchError((httpErrorResponse) => [
            fromActions.fetchModelConfigurationsFails({ httpErrorResponse }),
          ])
        )
      )
    )
  );

  fetchOrderModelConfigurationFails$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.fetchModelConfigurationsFails),
        tap((args) => {
          this.translate
            .get('client.configuration_fetch_configurations')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  fetchConfigurationVersion$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.fetchConfigurationVersion),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) =>
        this.service.getPostalConfigurationVersion(allStates.configuration_clientName).pipe(
          map((configurationVersion: PostalConfigurationVersion []) =>
            fromActions.fetchConfigurationVersionSuccess({
              configVersion: this.sortConfigurationVersion(configurationVersion)
            })
          ),
          catchError((httpErrorResponse) => [
            fromActions.fetchConfigurationVersionFail({ httpErrorResponse })
          ])
        )
      )
    )
  );

  attemptClientNameInConfigurationFile$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.attemptClientNameInConfigurationFile),
      switchMap(() => [fromActions.fetchConfigurationVersion(), fromActions.fetchTheLatestVersionConfiguration()])
    )
  );

  fetchTheVersionConfigurationById$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.fetchTheVersionConfigurationById),
    switchMap(() => [fromActions.fetchModelConfigurations()]),
  ));

  revertConfiguration$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.revertConfiguration),
    withLatestFrom(
      this.store.select(fromModifyClientSelector.selectClientAllStates)
    ),
    switchMap(([args, allStates]) => this.service.revertConfiguration(allStates.configuration_clientName, <number>args.referenceVersion)
      .pipe(
      map((response) => fromActions.revertConfigurationSuccess({ response: response })),
      catchError((error: HttpErrorResponse) => [fromActions.revertConfigurationFail({ error: error })])
    ))
  ));

  revertConfigurationSuccess$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.revertConfigurationSuccess),
    switchMap(() => {
      this.translate
        .get('client.configuration_register_success')
        .toPromise()
        .then((message) => {
          this.snackbar.openCustomSnackbar({
            icon: 'close',
            type: 'success',
            message
          });
        });

      return [fromActions.fetchConfigurationVersion(), fromActions.fetchTheLatestVersionConfiguration()];
    })
  ));

  revertConfigurationFail$ = createEffect(() => this.actions.pipe(
    ofType(fromActions.revertConfigurationFail),
    tap(() => {
      this.translate
        .get('client.configuration_register_fail')
        .toPromise()
        .then((message) => {
          this.snackbar.openCustomSnackbar({
            icon: 'close',
            type: 'error',
            message
          });
        });
    })
  ), { dispatch: false });

  downloadINIConfigurationFile$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.downloadINIConfigurationFile),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) =>
        this.service.downloadINIFile(allStates.configuration_clientName).pipe(
          map((file) =>
            fromActions.downloadINIConfigurationFileSuccess({ file })
          ),
          catchError((httpErrorResponse) => [
            fromActions.downloadINIConfigurationFileFail({ httpErrorResponse }),
          ])
        )
      )
    )
  );

  downloadINIConfigurationFileSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.downloadINIConfigurationFileSuccess),
        withLatestFrom(
          this.store.select(fromModifyClientSelector.selectClientAllStates)
        ),
        tap(([args, allStates]) => {
          this._fileSaverUtil.downloadBase64(
            args.file,
            `${allStates.configuration_clientName}.ini`
          );
        })
      ),
    { dispatch: false }
  );

  downloadINIConfigurationFileFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.downloadINIConfigurationFileFail),
        tap(() => {
          this.translate
            .get('client.configuration_download_configuration_fail')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  registerNewConfiguration$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.registerNewConfiguration),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) => {
        allStates = JSON.parse(JSON.stringify(allStates));
        allStates.configurations.forEach((configuration) => {
          if (configuration.name !== DEFAULT_SECTION_KEY) {
            const sectionModelEntry = {
              key: 'Modele',
              value: configuration.name,
            };
            const exists = configuration.entries.some(
              (entry) =>
                entry.key.trim() === sectionModelEntry.key &&
                entry.value.trim() === sectionModelEntry.value
            );
            if (!exists) {
              configuration.entries.unshift(sectionModelEntry);
            }
          }
        });
        const { configuration_clientName, configurations } = allStates;
        const payload: ConfigurationRegistrationModel = {
          client: configuration_clientName,
          configurations: createModifyConfigurationFilePayload(configurations),
        };

        return this.service.registerNewConfiguration(payload).pipe(
          map(() => fromModifyClientActions.registerNewConfigurationSuccess()),
          catchError((httpErrorResponse) => [
            fromModifyClientActions.registerNewConfigurationFails({
              httpErrorResponse,
            }),
          ])
        );
      })
    )
  );

  registerNewConfigurationSuccess$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.registerNewConfigurationSuccess),
      tap(() => {
        this.translate
          .get('client.configuration_register_success')
          .toPromise()
          .then((message) => {
            this.snackbar.openCustomSnackbar({
              icon: 'close',
              type: 'success',
              message,
            });
          });
      }),
      switchMap(() => [fromActions.fetchConfigurationVersion(), fetchTheLatestVersionConfiguration()])
    )
  );

  registerNewConfigurationFails$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.registerNewConfigurationFails),
        tap(() => {
          this.translate
            .get('client.configuration_register_fail')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  manageDistributeCriteria$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.manageDigitalDistributeCriteria),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) => {
        this.router.navigateByUrl(
          `${appRoute.cxmClient.navigateToManageDigitalChannel}/${allStates.client.name}`
        );
        return [fromClientFluxExt.preserveClientFormState({ data: allStates })];
      })
    )
  );

  updateSettingCriteria$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.updateSettingCriteria),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) => {
        return this.service
          .updateSettingCriteria({
            customer: allStates.client.name,
            preference: allStates.criteriaDistributionPayload,
          })
          .pipe(
            map(() => fromActions.updateSettingCriteriaSuccess()),
            catchError((httpErrorResponse) => [
              fromActions.updateSettingCriteriaFail({ httpErrorResponse }),
            ])
          );
      })
    )
  );

  attemptDistributeCriteriaToForm$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.attemptDistributeCriteriaToForm),
      switchMap(() => [fromActions.updateSettingCriteria()])
    )
  );

  fetchMetadataByType$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.fetchMetadataByType),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) => {
        return this.service
          .getMetadata(allStates.client.name, args.metadataType)
          .pipe(
            map((res) => {
              let metadata: Array<MetadataModel> = [];
              switch (args.metadataType) {
                case 'sender_name': {
                  metadata = res.senderName;
                  break;
                }

                case 'sender_mail': {
                  metadata = res.senderMail;
                  break;
                }

                case 'unsubscribe_link': {
                  metadata = res.unsubscribeLink;
                  break;
                }

                case 'sender_label': {
                  metadata = res.smsSenderLabel;
                  break;
                }

                default:
                  metadata = [];
              }
              return fromActions.attemptToModifyMetadata({
                metadataType: args.metadataType,
                metadata,
              });
            }),
            catchError((httpErrorResponse) => [
              fromActions.fetchMetadataByTypeFail({ httpErrorResponse }),
            ])
          );
      })
    )
  );

  fetchMetadataByTypeFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.fetchMetadataByTypeFail),
        tap((args) => {
          this.translate
            .get('client.metadata_fetch_fail')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  attemptToModifyMetadata$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.attemptToModifyMetadata),
        tap((args) => {
          this._metaDataPopupService
            .show(args.metadataType, args.metadata)
            .toPromise()
            .then((metadata) => {
              if (metadata !== undefined) {
                this.store.dispatch(
                  fromActions.modifyMetadataByType({
                    metadata,
                    metadataType: args.metadataType,
                  })
                );
              }
            });
        })
      ),
    { dispatch: false }
  );

  modifyMetadataByType$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.modifyMetadataByType),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) =>
        this.service
          .updateMetadataByType({
            customer: allStates.client.name,
            type: args.metadataType,
            metadata: args.metadata,
          })
          .pipe(
            map(() => fromActions.modifyMetadataByTypeSuccess()),
            catchError((httpErrorResponse) => [
              fromActions.modifyMetadataByTypeFail({ httpErrorResponse }),
            ])
          )
      )
    )
  );

  modifyMetadataByTypeSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.modifyMetadataByTypeSuccess),
        tap(() => {
          this.translate
            .get('client.metadata_update_success')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'success',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  modifyMetadataByTypeFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.modifyMetadataByTypeFail),
        tap(() => {
          this.translate
            .get('client.metadata_update_fail')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  fetchHubAccessAccount$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.fetchHubAccessAccount),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) =>
        this.service.getHubAccessAccount(allStates.client.name).pipe(
          map((hubAccount) =>
            fromActions.attemptToModifyHubAccount({
              hubAccount: {
                client: hubAccount.client,
                username: hubAccount.username || '',
                password: '',
              },
            })
          ),
          catchError((httpErrorResponse) => [
            fromActions.fetchHubAccessAccountFail({ httpErrorResponse }),
          ])
        )
      )
    )
  );

  fetchHubAccessAccountFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.fetchHubAccessAccountFail),
        tap(() => {
          this.translate
            .get('client.fetch_hub_access_account_fail')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  attemptToModifyHubAccount$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.attemptToModifyHubAccount),
        withLatestFrom(
          this.store.select(fromModifyClientSelector.selectClientAllStates)
        ),
        tap(([args, allStates]) => {
          this._hubDistributionPopupService
            .show({
              email: args.hubAccount.username,
              password: args.hubAccount.password,
            })
            .toPromise()
            .then((res) => {
              if (res) {
                this.store.dispatch(
                  fromActions.modifyHubAccessAccount({
                    hubAccount: {
                      client: allStates.client.name,
                      username: res.email.toLowerCase().trim(),
                      password: res.password.trim(),
                    },
                  })
                );
              }
            });
        })
      ),
    { dispatch: false }
  );

  modifyHubAccessAccount$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.modifyHubAccessAccount),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) =>
        this.service
          .updateHubAccessAccount({
            client: allStates.client.name,
            username: args.hubAccount.username,
            password: args.hubAccount.password,
          })
          .pipe(
            map(() => fromActions.modifyHubAccessAccountSuccess()),
            catchError((httpErrorResponse) => [
              fromActions.modifyHubAccessAccountFail({ httpErrorResponse }),
            ])
          )
      )
    )
  );

  modifyHubAccessAccountSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.modifyHubAccessAccountSuccess),
        tap(() => {
          this.translate
            .get('client.update_hub_account_successfully')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'success',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  modifyHubAccessAccountFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.modifyHubAccessAccountFail),
        tap(() => {
          this.translate
            .get('client.fail_update_hub_account')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  fetchServiceProvider$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.fetchServiceProvider),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) =>
        forkJoin([
          this.service.getServiceProvider(allStates.client.name),
          this.service.getServiceProviderCriteria(allStates.client.name),
        ]).pipe(
          map(([serviceProvider, serviceProviderCriteria]) =>
            fromActions.attemptToModifyServiceProvider({
              serviceProvider,
              serviceProviderCriteria,
            })
          ),
          catchError((httpErrorResponse) => [
            fromActions.fetchServiceProviderFail({ httpErrorResponse }),
          ])
        )
      )
    )
  );

  fetchServiceProviderFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.fetchServiceProviderFail),
        tap(() => {
          this.translate
            .get('client.fail_to_fetch_service_provider')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  attemptToModifyServiceProvider$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.attemptToModifyServiceProvider),
        withLatestFrom(
          this.store.select(fromModifyClientSelector.selectClientAllStates)
        ),
        tap(([args, allStates]) => {
          this._serviceProviderPopupService
            .show({
              selected: args.serviceProvider,
              initial: args.serviceProviderCriteria,
            })
            .toPromise()
            .then((res) => {
              if (res) {
                this.store.dispatch(
                  fromActions.modifyServiceProvider({
                    serviceProvider: {
                      MAIL: res.MAIL,
                      SMS: res.SMS,
                      customer: allStates.client.name,
                    },
                  })
                );
              }
            });
        })
      ),
    { dispatch: false }
  );

  modifyServiceProvider$ = createEffect(() =>
    this.actions.pipe(
      ofType(fromActions.modifyServiceProvider),
      withLatestFrom(
        this.store.select(fromModifyClientSelector.selectClientAllStates)
      ),
      exhaustMap(([args, allStates]) =>
        this.service
          .updateServiceProvider({
            customer: allStates.client.name,
            SMS: args.serviceProvider.SMS.map((item) => ({
              id: item.id,
              priority: item.priority,
            })),
            MAIL: args.serviceProvider.MAIL.map((item) => ({
              id: item.id,
              priority: item.priority,
            })),
          })
          .pipe(
            map(() => fromActions.modifyServiceProviderSuccess()),
            catchError((httpErrorResponse) => [
              fromActions.modifyServiceProviderFail({ httpErrorResponse }),
            ])
          )
      )
    )
  );

  modifyServiceProviderSuccess$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.modifyServiceProviderSuccess),
        tap(() => {
          this.translate
            .get('client.update_service_provider_successfully')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'success',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  modifyServiceProviderFail$ = createEffect(
    () =>
      this.actions.pipe(
        ofType(fromActions.modifyServiceProviderFail),
        tap(() => {
          this.translate
            .get('client.fail_update_service_provider')
            .toPromise()
            .then((message) => {
              this.snackbar.openCustomSnackbar({
                icon: 'close',
                type: 'error',
                message,
              });
            });
        })
      ),
    { dispatch: false }
  );

  private sortConfigurationVersion = (configurationVersion: PostalConfigurationVersion []) => {
    return configurationVersion?.sort((a: any, b: any) => a.order - b.order);
  }

  private validateClientFunctionality(functionalityData: any) {
    const notSelectFunc = functionalityData.length === 0;
    if (notSelectFunc) {
      this.translate
        .get('client.messages.atLeastOneFunctionality')
        .toPromise()
        .then((message) => {
          this.snackbar.openCustomSnackbar({
            icon: 'closed',
            message,
            type: 'error',
          });
        });
    }

    return notSelectFunc;
  }

  private isInValidUnloading = (unloading: DayOfWeekUnloadingModel[]) => {
    return (
      unloading.filter((day) => day.check && day.hours.length === 0).length > 0
    );
  };

  constructor(
    private actions: Actions,
    private service: ClientService,
    private store: Store,
    private router: Router,
    private snackbar: SnackBarService,
    private _fileSaverUtil: FileSaverUtil,
    private _metaDataPopupService: MetaDataPopupService,
    private _hubDistributionPopupService: HubDistributePopupService,
    private _serviceProviderPopupService: ServiceProviderPopupService,
    private translate: TranslateService,
    private confirmMessageService: ConfirmationMessageService,
    private flowService: FlowDepositService
  ) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }
}
