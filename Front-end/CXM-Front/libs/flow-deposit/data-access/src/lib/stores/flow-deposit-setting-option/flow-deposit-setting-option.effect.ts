import {Injectable} from '@angular/core';
import {SettingOptionPopupService} from '@cxm-smartflow/flow-deposit/ui/setting-option-popup';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import * as fromActions from './flow-deposit-setting-option.action';
import * as fromSelectors from './flow-deposit-setting-option.selector';
import {selectSettingOptionAllStates} from './flow-deposit-setting-option.selector';
import {catchError, exhaustMap, map, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {
  FlowDepositService,
  FlowDocumentAddressDto,
  initAnalyzeResponse,
  initOkDocumentProcessed,
  initProcessControlResponse,
  initProductionForm,
  PositionSetting,
  SettingOptionCriteriaConstant,
  UpdateOptionAttribute,
  WatermarkAttribute
} from '@cxm-smartflow/flow-deposit/data-access';
import {SnackBarService, UserProfileUtil, UserUtil} from '@cxm-smartflow/shared/data-access/services';
import {ActivatedRoute} from '@angular/router';
import {Store} from '@ngrx/store';
import {EnrichmentMailing} from '@cxm-smartflow/shared/data-access/model';
import {EnrichmentPrivilegeUtil, WatermarkColorUtil} from '@cxm-smartflow/flow-deposit/util';
import {FlowDepositResolverService} from "../../services/flow-deposit-resolver-service";

@Injectable({
  providedIn: 'root',
})
export class FlowDepositSettingOptionEffect {
  constructor(
    private _typeSettingOptionPopupService: SettingOptionPopupService,
    private _translationService: TranslateService,
    private _flowDepositService: FlowDepositService,
    private _flowDepositServiceResolver: FlowDepositResolverService,
    private _activateRoute: ActivatedRoute,
    private _snackbar: SnackBarService,
    private _store: Store,
    private _action$: Actions,
  ) {
  }

  attachSettingOptionPopup$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.attachSettingOptionPopup),
    switchMap(args => {
      switch (args.popupType) {
        default:
          return [fromActions.setupAttachmentPopup({popupType: args.popupType})];
      }
    })),
  );

  setupAttachmentPopup$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.setupAttachmentPopup),
    withLatestFrom(
      this._translationService.get('flow.deposit'),
      this._store.select(fromSelectors.selectSettingOptionAllStates)
    ),
    switchMap(([args, messages, allStates]) => {
      //#region Add resource type
      const addResourceType = SettingOptionCriteriaConstant.ADD_RESOURCE_TYPE
        .filter(item => {
          if (allStates.mode === 'edit') {
            return true;
          }

          const library = UserProfileUtil.getInstance().canVisibility({
            func: EnrichmentMailing.CXM_ENRICHMENT_MAILING,
            priv: EnrichmentMailing.USE_RESOURCE_IN_LIBRARY,
            ownerId: UserUtil.getOwnerId(),
            checkAdmin: false,
          });

          if (!library && item.key === '1') {
            return false
          }

          const uploading = UserProfileUtil.getInstance().canModify({
            func: EnrichmentMailing.CXM_ENRICHMENT_MAILING,
            priv: EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE,
            ownerId: UserUtil.getOwnerId(),
            checkAdmin: false,
          });

          if (!uploading && item.key === '2') {
            return false
          }

          return true;
        })
        .map(type => {
          const value = messages[`${type.value}`];
          return {...type, value};
        });
      //#endregion

      //#region Attachment position
      let positionSettings: Array<PositionSetting> = [];

      switch (args.popupType) {
        case 'Attachment': {
          positionSettings = SettingOptionCriteriaConstant.ATTACHMENT_POSITION.filter(e => {
            if (allStates.mode === 'edit') {
              const position = SettingOptionCriteriaConstant.ATTACHMENT_POSITION.find(e => allStates.selectedAttachmentPosition === e.key)?.val || '';
              return allStates.attachments.every(item => item.position !== e.val || item.position === position);
            }

            return allStates.attachments.every(item => item.position !== e.val);
          });

          break;
        }

        case 'Background': {
          positionSettings = SettingOptionCriteriaConstant.BACKGROUND_POSITION.filter(e => {
            if (allStates.mode === 'edit') {
              if (allStates.backgrounds.length > 1 && e.val === 'ALL_PAGES') {
                return false;
              }

              const position = SettingOptionCriteriaConstant.BACKGROUND_POSITION.find(background => allStates.selectedAttachmentPosition === background.key)?.val || '';
              return allStates.backgrounds.every(item => item.position !== e.val || item.position === position);
            }

            if (allStates.backgrounds.length > 0 && !allStates.backgrounds.some(background => background.position === 'ALL_PAGES')) {
              return allStates.backgrounds.every(item => item.position !== e.val) && e.val !== 'ALL_PAGES';
            }

            return true;
          });

          break;
        }

        case 'Watermark': {
          positionSettings = SettingOptionCriteriaConstant.WATERMARK_POSITION.filter(e => {
            return allStates.watermark.every(item => item.position !== e.val);
          });
          break;
        }

      }

      const attachmentPosition = positionSettings.map(position => {
        const value = messages[`${position.value}`];
        return {...position, value};
      })
      //#endregion


      return [fromActions.attachAttachmentPopup({addResourceType, attachmentPosition, popupType: args.popupType})];
    }),
  ));

  switchAddResourceType$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.switchAddResourceType),
    switchMap(args => {
      if (`${args.selectResourceType}` === '1') {
        return [fromActions.fetchResources({popupType: args.popupType})];
      }

      return [fromActions.cancelFetchResource()];
    })
  ));

  fetchResources$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchResources),
    exhaustMap((args) => {
      return this._flowDepositService.getAllResources({}, args.popupType).pipe(
        map(resources => fromActions.fetchResourcesSuccess({resources})),
      )
    })
  ));

  httpErrorResponse$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchResourceFail),
    tap(args => {
      this._translationService.get('flow.deposit.fail_to_fetch_resources').toPromise().then(message => {
        this._snackbar.openCustomSnackbar({type: 'error', message, icon: 'close'});
      });
    })
  ), {dispatch: false});

  attachAttachmentPopup$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.attachAttachmentPopup),
    tap(args => {
      this._typeSettingOptionPopupService.show(args.popupType).toPromise().then(() => {
        this._store.dispatch(fromActions.clearSettingOptionStates());
      });
    })
  ), {dispatch: false});

  fileUploadChange$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fileUploadChange),
    withLatestFrom(this._activateRoute.queryParams),
    switchMap(([args, params]) => {
      const formData = new FormData();
      formData.append('file', args.files[0]);
      return this._flowDepositService.storeBackground(params.fileId, args.popupType, formData).pipe(
        map(response => {
          if (response.type !== 4) {
            return fromActions.fileUploadingInProgress({response})
          }

          return fromActions.uploadFileSuccessfully({response: response.body});
        }),
        catchError(httpErrorResponse => [fromActions.uploadingFileFail({httpErrorResponse})])
      );
    }))
  );

  uploadingFileFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.uploadingFileFail),
    withLatestFrom(
      this._translationService.get('flow.deposit'),
      this._store.select(fromSelectors.selectSettingOptionAllStates),
    ),
    tap(([args, messages, allStates]) => {
      const statusCode = args.httpErrorResponse.error.apierrorhandler.statusCode;
      switch (statusCode) {
        case 404:
        case 4000: {
          if (allStates.popupType === 'Background') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_background_file_missing,
              icon: 'close'
            });
          } else if (allStates.popupType === 'Attachment') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_attachment_file_missing,
              icon: 'close'
            });
          } else if (allStates.popupType === 'Signature') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_signature_file_missing,
              icon: 'close'
            });
          }
          break;
        }

        case 4001: {
          if (allStates.popupType === 'Signature') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_upload_not_png_for_signature,
              icon: 'close'
            });
          } else if (allStates.popupType === 'Background') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_upload_not_pdf_for_background,
              icon: 'close'
            });
          } else if (allStates.popupType === 'Attachment') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_upload_not_pdf_for_attachment,
              icon: 'close'
            });
          }
          break;
        }

        case 4002: {
          if (allStates.popupType === 'Background') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_background_page_limit,
              icon: 'close'
            });
          } else if (allStates.popupType === 'Attachment') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_attachment_page_limit,
              icon: 'close'
            });
          }
          break;
        }

        case 4003: {
          if (allStates.popupType === 'Background') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_attachment_max_file_size,
              icon: 'close'
            });
          } else if (allStates.popupType === 'Attachment') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_attachment_max_file_size,
              icon: 'close'
            });
          } else if (allStates.popupType === 'Signature') {
            this._snackbar.openCustomSnackbar({
              type: 'error',
              message: messages.setting_option_popup_signature_max_file_size,
              icon: 'close'
            });
          }
          break;
        }

        default: {
          this._snackbar.openCustomSnackbar({
            type: 'error',
            message: messages.setting_option_popup_background_unknown,
            icon: 'close'
          });
        }
      }
    })
  ), {dispatch: false});

  switchResourceLabel$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.switchResourceLabel),
    withLatestFrom(this._store.select(fromSelectors.selectSettingOptionAllStates)),
    exhaustMap(([args, allStates]) => {
      const fileId = allStates.resources.contents.find(item => item.label === args.label)?.fileId || '';
      return this._flowDepositService.getResourceFileById(fileId).pipe(
        map(base64 => fromActions.switchResourceLabelSuccess({base64})),
        catchError(httpErrorResponse => [fromActions.switchResourceLabelFail({httpErrorResponse})]),
      );
    })
  ));

  fetchAddedAttachmentDetail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchAddedAttachmentDetail),
    withLatestFrom(this._store.select(selectSettingOptionAllStates)),
    exhaustMap(([args, allStates]) => {
      return this._flowDepositService.getOptionAttributeDetail(`${allStates.attributeId}`).pipe(
        map(resourceDetail => {
          return fromActions.fetchAddedAttachmentDetailSuccess({resourceDetail});
        }),
      );
    })
  ));

  fetchAddedAttachmentDetailSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchAddedAttachmentDetailSuccess),
    withLatestFrom(this._store.select(selectSettingOptionAllStates)),
    switchMap(([args, allStates]) => {
      return [fromActions.attachSettingOptionPopup({popupType: allStates.popupType})];
    })
  ));

  fetchAddedAttachmentDetailFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchAddedAttachmentDetailFail),
    withLatestFrom(
      this._translationService.get('flow.deposit'),
      this._store.select(fromSelectors.selectSettingOptionAllStates),
    ),
    tap(([args, messages, allStates]) => {
      if (allStates.popupType === 'Attachment') {
        this._snackbar.openCustomSnackbar({
          type: 'error',
          icon: 'close',
          message: messages.fail_to_fetch_attachment_detail
        });
      } else if (allStates.popupType === 'Background') {
        this._snackbar.openCustomSnackbar({
          type: 'error',
          icon: 'close',
          message: messages.fail_to_fetch_background_detail
        });
      }
    })
  ), {dispatch: false});

  updateAttachmentSettingOption$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.updateAttachmentSettingOption),
    withLatestFrom(
      this._store.select(fromSelectors.selectSettingOptionAllStates),
      this._activateRoute.queryParams,
    ),
    exhaustMap(([args, allStates, params]) => {
      const source = allStates.selectedAddResourceType == '1' ? 'Library' : 'ONE_TIME_UPLOAD';
      let fileId = source === 'Library' ? allStates.resources.contents.find(item => item.label === allStates.selectedResource)?.fileId || '' : allStates.uploadingFileId;
      if (allStates.mode === 'edit') {
        fileId = source === 'Library' ? allStates.fileId : allStates.uploadingFileId;
      }

      let position = '';
      if (allStates.popupType === 'Attachment') {
        position = SettingOptionCriteriaConstant.ATTACHMENT_POSITION.find(e => e.key === allStates.selectedAttachmentPosition)?.val || '';
      } else if (allStates.popupType === 'Background') {
        position = SettingOptionCriteriaConstant.BACKGROUND_POSITION.find(e => e.key === allStates.selectedAttachmentPosition)?.val || '';
      }

      const payload: UpdateOptionAttribute = {
        flowId: params.fileId,
        type: args.popupType,
        fileId: fileId,
        source,
        position,
        id: allStates.attributeId,
      };
      return this._flowDepositService.UpdateOptionAttribute(payload).pipe(
        map(() => fromActions.updateAttachmentSettingOptionSuccess()),
        catchError(httpErrorResponse => [fromActions.updateAttachmentSettingOptionFail({httpErrorResponse})]),
      );
    })
  ));

  updateAttachmentSettingOptionSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.updateAttachmentSettingOptionSuccess),
    withLatestFrom(this._translationService.get('flow.deposit')),
    switchMap(([args, messages]) => {
      return [fromActions.getAllAttachmentSettingOption()];
    })
  ));

  updateAttachmentSettingOptionFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.updateAttachmentSettingOptionFail),
    withLatestFrom(
      this._translationService.get('flow.deposit'),
      this._store.select(fromSelectors.selectSettingOptionAllStates)
    ),
    tap(([args, messages, allStates]) => {
      if (allStates.popupType === 'Attachment') {
        this._snackbar.openCustomSnackbar({
          type: 'error',
          message: messages.setting_option_popup_update_attachment_fail,
          icon: 'close'
        });
      } else if (allStates.popupType === 'Background') {
        this._snackbar.openCustomSnackbar({
          type: 'error',
          message: messages.setting_option_popup_update_background_fail,
          icon: 'close'
        });
      }
    })
  ), {dispatch: false});

  getAllAttachmentSettingOption$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.getAllAttachmentSettingOption),
    withLatestFrom(this._activateRoute.queryParams),
    exhaustMap(([args, params]) => {
      return this._flowDepositService.fetchAllResourceFile(params.fileId).pipe(
        map(resourceFileResponse => {
          //#region set backgrounds privileges.
          let {Attachment, Background, Signature} = resourceFileResponse;
          Background = Background.map(background => {
            const {modifyPrivKey, deletePrivKey} = EnrichmentPrivilegeUtil.getPrivillegKey(background);
            const modifiable = UserProfileUtil.getInstance().canModify({
              func: EnrichmentMailing.CXM_ENRICHMENT_MAILING,
              priv: modifyPrivKey,
              ownerId: background.ownerId,
              checkAdmin: false,
            });

            const deletable = UserProfileUtil.getInstance().canModify({
              func: EnrichmentMailing.CXM_ENRICHMENT_MAILING,
              priv: deletePrivKey,
              ownerId: background.ownerId,
              checkAdmin: false,
            });

            return {...background, deletable, modifiable};
          });
          //#endregion

          //#region set attachment privileges.
          Attachment = Attachment.map(attachment => {
            const {modifyPrivKey, deletePrivKey} = EnrichmentPrivilegeUtil.getPrivillegKey(attachment);
            const modifiable = UserProfileUtil.getInstance().canModify({
              func: EnrichmentMailing.CXM_ENRICHMENT_MAILING,
              priv: modifyPrivKey,
              ownerId: attachment.ownerId,
              checkAdmin: false,
            });

            const deletable = UserProfileUtil.getInstance().canModify({
              func: EnrichmentMailing.CXM_ENRICHMENT_MAILING,
              priv: deletePrivKey,
              ownerId: attachment.ownerId,
              checkAdmin: false,
            });

            return {...attachment, modifiable, deletable};
          });
          //#endregion

          //#region set signature privileges.
          Signature = Signature.map(signature => {
            const {modifyPrivKey, deletePrivKey} = EnrichmentPrivilegeUtil.getPrivillegKey(signature);
            const modifiable = UserProfileUtil.getInstance().canModify({
              func: EnrichmentMailing.CXM_ENRICHMENT_MAILING,
              priv: modifyPrivKey,
              ownerId: signature.ownerId,
              checkAdmin: false,
            });

            const deletable = UserProfileUtil.getInstance().canModify({
              func: EnrichmentMailing.CXM_ENRICHMENT_MAILING,
              priv: deletePrivKey,
              ownerId: signature.ownerId,
              checkAdmin: false,
            });

            return {...signature, modifiable, deletable};
          });
          //#endregion

          return fromActions.getAllAttachmentSettingOptionSuccess({
            response: {
              ...resourceFileResponse,
              Attachment,
              Background,
              Signature
            }
          });
        }),
      );
    })
  ));

  getAllAttachmentSettingOptionFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.getAllAttachmentSettingOptionFail),
    withLatestFrom(
      this._translationService.get('flow.deposit'),
      this._store.select(fromSelectors.selectSettingOptionAllStates),
    ),
    tap(([args, messages, allStates]) => {
      if (allStates.popupType === 'Attachment') {
        this._snackbar.openCustomSnackbar({
          type: 'error',
          message: messages.setting_option_popup_get_all_attachment_fail,
          icon: 'close'
        });
      } else if (allStates.popupType === 'Background') {
        this._snackbar.openCustomSnackbar({
          type: 'error',
          message: messages.setting_option_popup_get_all_background_fail,
          icon: 'close'
        });
      }
    })
  ), {dispatch: false});

  deleteOptionAttribute$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.deleteOptionAttribute),
    exhaustMap(args => {
      return this._flowDepositService.deleteFlowBackground(args.attributeId).pipe(
        map(() => fromActions.deleteOptionAttributeSuccess()),
        catchError(httpErrorResponse => [fromActions.deleteOptionAttributeFail({httpErrorResponse})]),
      )
    })
  ));

  deleteOptionAttributeSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.deleteOptionAttributeSuccess),
    switchMap(() => {
      return [fromActions.getAllAttachmentSettingOption()];
    })
  ));

  deleteOptionAttributeFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.deleteOptionAttributeFail),
    withLatestFrom(
      this._translationService.get('flow.deposit'),
      this._store.select(fromSelectors.selectSettingOptionAllStates),
    ),
    tap(([args, messages, allStates]) => {
      if (allStates.popupType === 'Background') {
        this._snackbar.openCustomSnackbar({
          type: 'error',
          message: messages.setting_option_popup_delete_background_fail,
          icon: 'close'
        });
      } else if (allStates.popupType === 'Attachment') {
        this._snackbar.openCustomSnackbar({
          type: 'error',
          message: messages.setting_option_popup_delete_attachment_fail,
          icon: 'close'
        });
      }
    })
  ), {dispatch: false});

  validatePortalSignatureConfig$ = createEffect(() =>
    this._action$.pipe(
      ofType(fromActions.hasPortalSignatureConfig),
      exhaustMap((args) =>
        this._flowDepositService
          .hasPortalSignatureConfig(args.modelName)
          .pipe(
            map((isValidSignature) =>
              fromActions.hasPortalSignatureConfigSuccess({isValidSignature})
            )
          )
      )
    )
  );

  createWaterMark$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.createWaterMark),
    withLatestFrom(
      this._store.select(fromSelectors.selectSettingOptionAllStates),
      this._activateRoute.queryParams,
    ),
    exhaustMap(([args, allStates, params]) => {

      const position = SettingOptionCriteriaConstant.WATERMARK_POSITION.find(e => e.key === allStates.selectedWatermarkPosition)?.val || '';
      const color = WatermarkColorUtil.colorPickerToText(allStates.watermarkAttribute.color);

      const payload: WatermarkAttribute = {
        id: allStates.attributeId ? allStates.attributeId : 0,
        flowId: params.fileId,
        text: allStates.watermarkAttribute.text,
        position: position,
        size: allStates.watermarkAttribute.size,
        rotation: allStates.watermarkAttribute.rotation,
        color: color,
      };
      if (payload.id != 0) {

        return this._flowDepositService.updateWaterMark(payload).pipe(
          map(() => fromActions.updateWatermarkSuccess()),
          catchError(httpErrorResponse => [fromActions.updateWatermarkFail({httpErrorResponse})]),
        );
      }
      return this._flowDepositService.createWaterMark(payload).pipe(
        map(() => fromActions.createWaterMarkSuccess()),
        catchError(httpErrorResponse => [fromActions.createWaterMarkFail({httpErrorResponse})]),
      );
    })
  ));

  createWaterMarkFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.createWaterMarkFail),
    withLatestFrom(
      this._translationService.get('flow.deposit'),
      this._store.select(fromSelectors.selectSettingOptionAllStates)
    ),
    tap(([args, messages, allStates]) => {
      this._snackbar.openCustomSnackbar({
        type: 'error',
        message: messages.setting_option_popup_create_watermark_fail,
        icon: 'close'
      });
    })
  ), {dispatch: false});

  createWaterMarkSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.createWaterMarkSuccess),
    switchMap(() => {
      return [fromActions.fetchWatermark()];
    })
  ));
  fetchWatermark$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchWatermark),
    withLatestFrom(
      this._store.select(fromSelectors.selectSettingOptionAllStates),
      this._activateRoute.queryParams,
    ),
    exhaustMap(([args, allStates, params]) => {
      const flowId: string = params.fileId;
        return this._flowDepositService.fetchWaterMark(flowId).pipe(
          map(waterMarkAttribute => fromActions.fetchWatermarkSuccess({waterMarkAttribute})),
          catchError(httpErrorResponse => [fromActions.fetchWatermarkFail({httpErrorResponse})]),
        );
    })
  ));

  fetchWatermarkEdit$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchWatermarkEdit),
    withLatestFrom(
      this._store.select(fromSelectors.selectSettingOptionAllStates),
      this._activateRoute.queryParams,
    ),
    exhaustMap(([args, allStates, params]) => {
      const flowId: string = params.fileId;
      return this._flowDepositService.fetchWaterMark(flowId).pipe(
        map(waterMarkAttribute => {

          const position = SettingOptionCriteriaConstant.WATERMARK_POSITION.find(watermark => watermark.val == waterMarkAttribute.position)?.key || '';
          return fromActions.fetchWatermarkEditSuccess({waterMarkAttribute, waterMarkKey:position});
        }),
        catchError(httpErrorResponse => [fromActions.fetchWatermarkFail({httpErrorResponse})]),
      );
    })
  ));

  fetchWatermarkEditSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchWatermarkEditSuccess),
    withLatestFrom(this._store.select(selectSettingOptionAllStates)),
    switchMap(([args, allStates]) => {
      return [fromActions.attachSettingOptionPopup({popupType: allStates.popupType})];
    })
  ));

  updateWaterMark$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.updateWatermark),
    withLatestFrom(
      this._store.select(fromSelectors.selectSettingOptionAllStates),
      this._activateRoute.queryParams,
    ),
    exhaustMap(([args, allStates, params]) => {

      const position = SettingOptionCriteriaConstant.WATERMARK_POSITION.find(e => e.key === allStates.selectedWatermarkPosition ? allStates.selectedWatermarkPosition : 1)?.val || "";

      const payload: WatermarkAttribute = {
        id: allStates.attributeId,
        flowId: params.fileId,
        text: allStates.watermarkAttribute.text,
        position: position,
        size: allStates.watermarkAttribute.size,
        rotation: allStates.watermarkAttribute.rotation,
        color: allStates.watermarkAttribute.color,
      };

      return this._flowDepositService.updateWaterMark(payload).pipe(
        map(() => fromActions.updateWatermarkSuccess()),
        catchError(httpErrorResponse => [fromActions.updateWatermarkFail({httpErrorResponse})]),
      );
    })
  ));

  updateWaterMarkSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.updateWatermarkSuccess),
    withLatestFrom(this._translationService.get('flow.deposit')),
    switchMap(([args, messages]) => {
      return [fromActions.fetchWatermark()];
    })
  ));

  updateWaterMarkFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.updateWatermarkFail),
    withLatestFrom(
      this._translationService.get('flow.deposit'),
      this._store.select(fromSelectors.selectSettingOptionAllStates)
    ),
    tap(([args, messages, allStates]) => {
      this._snackbar.openCustomSnackbar({
        type: 'error',
        message: messages.setting_option_popup_update_watermark_fail,
        icon: 'close'
      });
    })
  ), {dispatch: false});

  deleteWaterMark$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.deleteWatermark),
    withLatestFrom(
      this._activateRoute.queryParams,
    ),
    exhaustMap(([args, params]) => {
      return this._flowDepositService.deleteWaterMark(params.fileId).pipe(
        map(() => fromActions.deleteWatermarkSuccess()),
        catchError(httpErrorResponse => [fromActions.deleteWatermarkFail({httpErrorResponse})]),
      )
    }),
  ));

  deleteWaterMarkFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.deleteWatermarkFail),
    withLatestFrom(
      this._translationService.get('flow.deposit'),
      this._store.select(fromSelectors.selectSettingOptionAllStates)
    ),
    tap(([args, messages, allStates]) => {
      this._snackbar.openCustomSnackbar({
        type: 'error',
        message: messages.setting_option_popup_delete_watermark_fail,
        icon: 'close'
      });
    })
  ), {dispatch: false});

  deleteWaterMarkSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.deleteWatermarkSuccess),
    switchMap(() => {
      return [fromActions.fetchWatermark()];
    })
  ));

  fetchFlowDocumentAddress$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchFlowDocumentAddress),
    withLatestFrom(
      this._store.select(fromSelectors.selectSettingOptionAllStates),
      this._activateRoute.queryParams,
    ),
    exhaustMap(([args, allStates, params]) => {
      const flowId: string = params.fileId;
      const docId:string = allStates.docUuid;
      return this._flowDepositService.fetchFlowDocumentAddress(flowId,docId).pipe(
        map(flowDocumentAddressResponse => fromActions.fetchFlowDocumentAddressSuccess({flowDocumentAddressResponse})),
        catchError(httpErrorResponse => [fromActions.fetchFlowDocumentAddressFail({httpErrorResponse})]),
      );
    })
  ));

  updateFlowDocumentAddress$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.updateFlowDocumentAddress),
    withLatestFrom(
      this._store.select(fromSelectors.selectSettingOptionAllStates),
      this._activateRoute.queryParams,
    ),
    exhaustMap(([args, allStates, params]) => {

      const payload: FlowDocumentAddressDto = {
        flowId: params.fileId,
        docId: allStates.docUuid,
        flowDocumentAddress: allStates.flowDocumentAddresses
      };

      return this._flowDepositService.updateFlowDocumentAddress(payload).pipe(
        map(() => fromActions.updateFlowDocumentAddressSuccess()),
        catchError(httpErrorResponse => [fromActions.updateFlowDocumentAddressFail({httpErrorResponse})]),
      );
    })
  ));

  updateFlowDocumentAddressSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.updateFlowDocumentAddressSuccess),
    withLatestFrom(   this._translationService.get('flow.deposit'),this._activateRoute.queryParams),
    exhaustMap(([args,message, params]) => {
      this._snackbar.openCustomSnackbar({
        type: 'success',
        message: message.address_modify_success,
        icon: 'close'
      });
      return this._flowDepositService.getDepositFlow(params.fileId, params.step).pipe(
        map((response: any) => {
          return fromActions.initAnalyzeResponseSuccess({response})
        }),
        catchError(httpErrorResponse => [fromActions.initAnalyzeResponseFail({httpErrorResponse})])
      )
    }),
  ));

  updateFlowDocumentAddressFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.updateFlowDocumentAddressFail),
    withLatestFrom(
      this._translationService.get('flow.deposit')
    ),
    tap(([args, messages]) => {
      this._snackbar.openCustomSnackbar({
        type: 'error',
        message: messages.address_modify_error,
        icon: 'close'
      });
    })
  ), {dispatch: false});

  initAnalyzeResponseSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.initAnalyzeResponseSuccess),
    withLatestFrom(this._store.select(fromSelectors.selectSettingOptionAllStates), this._activateRoute.queryParams),
    switchMap(([args, allstates, params]) => {
      this._flowDepositServiceResolver.initializeAnalysisResultStep3(allstates.response, params.composedFileId)
      return [fromActions.initAnalyzeResponseStepThree()];
    })
  ));

}
