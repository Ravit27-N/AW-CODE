import { Injectable } from '@angular/core';
import { SettingService } from '../../service';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as fromActions from './manage-resource.action';
import {
  catchError,
  delay,
  exhaustMap,
  map,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { Store } from '@ngrx/store';
import {
  selectDeleteFileId,
  selectMessages,
  selectPopupForm,
  selectResourceAllStates,
  selectResourceCriteria,
  selectResourceListCriteria,
  selectUploadedFiles,
} from './manage-resource.selector';
import {
  CanModificationService,
  SnackBarService,
} from '@cxm-smartflow/shared/data-access/services';
import {
  FileExtensionUtil,
  FileSaverUtil,
  FileUtils,
} from '@cxm-smartflow/shared/utils';
import { ResourceResponseList, ResourceTypeConstant } from '../../model';
import { LibraryResourceManagement } from '@cxm-smartflow/shared/data-access/model';
import { TranslateService } from '@ngx-translate/core';
import { ResourceCriteriaResponse } from '../../model/resource-criteria.response';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';

@Injectable({
  providedIn: 'root',
})
export class ManageResourceEffect {

  constructor(
    private _resourceService: SettingService,
    private _canModification: CanModificationService,
    private _translate: TranslateService,
    private _action$: Actions,
    private _store$: Store,
    private _snackbar: SnackBarService,
    private _fileSaverUtil: FileSaverUtil,
    private _confirmMessageService: ConfirmationMessageService,
  ) {
    this._translate.use(localStorage.getItem('locale') || 'fr');
  }

  fetchListResourceCriteria$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchListResourceCriteria),
    withLatestFrom(this._store$.select(selectMessages)),
    exhaustMap(([args, messages]) => this._resourceService.getCriteria().pipe(
      map(resourceCriteria => fromActions.fetchListResourceCriteriaSuccess({ resourceCriteria })),
      catchError(httpErrorResponse => [fromActions.fetchListResourceCriteriaFail({ httpErrorResponse })])
    ))
  ));

  fetchResources$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fetchResources),
    withLatestFrom(this._store$.select(selectResourceListCriteria), this._store$.select(selectResourceCriteria)),
    exhaustMap(([args, state, messages]) => this._resourceService.getAll({
      page: state.page, pageSize: state.pageSize, sortDirection: state.sortDirection,
      sortByField: state.sortByField, filter: state.filter,
    }, state.types).pipe(
      map(resources => fromActions.fetchResourcesSuccess({ resources: this.mapResource(resources, messages) })),
      catchError(httpErrorResponse => [fromActions.fetchResourcesFail({ httpErrorResponse })])
    ))
  ));

  filterTypeBoxChange$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.filterTypeBoxChange),
    switchMap(() => [fromActions.fetchResources()])
  ));

  searchBoxChange$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.searchBoxChange),
    switchMap(() => [fromActions.fetchResources()])
  ));

  tableSortChange$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.tableSortChange),
    switchMap(() => [fromActions.fetchResources()])
  ));

  paginationChange$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.paginationChange),
    switchMap(() => [fromActions.fetchResources()])
  ));

  getTranslationMsg$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.getTranslationMsg),
    exhaustMap(() => this._translate.get('cxm_setting').pipe(
      map(message => fromActions.getTranslationMsgSuccess({ message }))
    ))
  ));

  fileUploadChange$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.fileUploadChange),
    withLatestFrom(this._store$.select(selectMessages)),
    switchMap(([args, messages]) => {
      let propertyKey = '';
      if(ResourceTypeConstant.background === args.resourceType){
        if (args.files.length > 1) {
          propertyKey = 'popup_invalidTotalFile';
        } else if (FileUtils.convertToKB(`${args.files[0].size}B`) > FileUtils.convertToKB('20MB')) {
          propertyKey = 'popup_invalidFileSize';
        }
      }

      if(ResourceTypeConstant.signature === args.resourceType){
        if (args.files.length > 1) {
          propertyKey = 'popup_invalidTotalFile';
        }
      }

      // Validate file upload of resource type attachment.
      if(ResourceTypeConstant.attachment === args.resourceType){
        // Check total file.
        if(args?.files?.length > 1){
          propertyKey = 'attachment_invalid_total_file';
        }else{
          // Check pdf file format.
          const extension = (args?.files[0]?.name as string)?.split(".").pop();
          if(FileExtensionUtil.PDF_EXTENSION !== extension){
            propertyKey = 'attachment_invalid_format';
          }

          // Check file size.
          if(FileUtils.convertToKB(`${args.files[0].size}B`) > FileUtils.convertToKB('20MB')){
            propertyKey = 'attachment_invalid_file_size';
          }
        }
      }

      if (propertyKey) {
        this._snackbar.openCustomSnackbar({ message: messages[`${propertyKey}`], type: 'error', icon: 'close' });
        return [fromActions.cancelUploadResourceFile()];
      }

      return [fromActions.uploadResourceFile({resourceType: args.resourceType})];
    }),
  ));

  uploadResourceFile$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.uploadResourceFile),
    withLatestFrom(this._store$.select(selectUploadedFiles)),
    exhaustMap(([args, files]) => {
      const formData = new FormData();
      formData.append('file', files[0]);
      return this._resourceService.uploadFile(formData, args.resourceType).pipe(
        map(res => {
          if (res.type !== 4) {
            return fromActions.uploadResourceFileInProgress({response: res})
          }

          return fromActions.uploadResourceFileSuccess({ resourceLibraryResponse: res.body });
        }),
        catchError(httpErrorResponse => [fromActions.uploadResourceFileFail({ httpErrorResponse })])
      )
    })
  ));

  uploadResourceFileFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.uploadResourceFileFail),
    withLatestFrom(
      this._store$.select(selectResourceAllStates)
    ),
    tap(([args, allStates]) => {
      this._translate.get('flow.deposit').toPromise().then(messages => {
        const statusCode = args.httpErrorResponse.error.apierrorhandler.statusCode;

        switch (statusCode) {

          case 404:
          case 4000: {
            if (allStates.popupForm.resourceType === 'Background') {
              this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_background_file_missing, icon: 'close' });
            } else if(allStates.popupForm.resourceType === 'Attachment') {
              this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_attachment_file_missing, icon: 'close' });
            }
            break;
          }

          case 4001: {
            if(allStates.popupForm.resourceType === 'Background'){
              this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_upload_not_pdf_for_background, icon: 'close' });
            }else if(allStates.popupForm.resourceType === 'Attachment'){
              this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_upload_not_pdf_for_attachment, icon: 'close' });
            }else if(allStates.popupForm.resourceType === 'Signature'){
              this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_upload_not_png_for_signature, icon: 'close' });
            }
            break;
          }

          case 4002: {
            if (allStates.popupForm.resourceType === 'Background') {
              this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_background_page_limit, icon: 'close' });
            } else if(allStates.popupForm.resourceType === 'Attachment') {
              this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_attachment_page_limit, icon: 'close' });
            }
            break;
          }

          case 4003: {
            if (allStates.popupForm.resourceType === 'Background') {
              this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_attachment_max_file_size, icon: 'close' });
            } else if(allStates.popupForm.resourceType === 'Attachment') {
              this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_attachment_max_file_size, icon: 'close' });
            }else if(allStates.popupForm.resourceType === 'Signature') {
              this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_signature_max_file_size, icon: 'close' });
            }
            break;
          }

          default: {
            this._snackbar.openCustomSnackbar({ type: 'error', message: messages.setting_option_popup_background_unknown, icon: 'close' });
          }
        }
      })


    }),
    switchMap(() => [fromActions.resetUploadFile()])
  ));


  validateCreateResourceForm$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.validateCreateResourceForm),
    delay(1000),
    withLatestFrom(this._store$.select(selectPopupForm), this._store$.select(selectMessages)),
    switchMap(([args, popupForm, messages]) => {
      const { resourceType, label, fileId, isLabelDuplicate, isLabelIsChecking } = popupForm;
      if (resourceType && label.trim() && !isLabelDuplicate && !fileId) {
        this._snackbar.openCustomSnackbar({ message: messages.please_select_attachment_file, type: 'error', icon: 'close' });
      }
      if (!resourceType || !label.trim() || isLabelDuplicate || !fileId || isLabelIsChecking) {
        return [fromActions.cancelResourceForm()];
      }

      return [fromActions.createResource()];
    })
  ));

  checkDuplicateLabel$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.checkDuplicateLabel),
    exhaustMap(args => this._resourceService.checkDuplicateLabel(args.name, args.resourceType).pipe(
      map(isLabelDuplicate => fromActions.checkDuplicateLabelSuccess({ isLabelDuplicate })),
      catchError(httpErrorResponse => [fromActions.checkDuplicateLabelFails({ httpErrorResponse })])
    ))
  ));

  createResource$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.createResource),
    withLatestFrom(this._store$.select(selectPopupForm)),
    exhaustMap(([args, popup]) => this._resourceService.create({
      label: popup.label,
      fileSize: popup.fileSizePayload,
      type: popup.resourceType,
      fileId: popup.fileId,
      fileName: popup.fileName,
      pageNumber: popup.pageNumber,
    }).pipe(
      map(() => fromActions.createResourceSuccess()),
      catchError(httpErrorResponse => [fromActions.createResourceFail({ httpErrorResponse })])
    ))
  ));

  createResourceSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.createResourceSuccess),
    withLatestFrom(this._store$.select(selectMessages)),
    tap(([args, messages]) => {
      this._snackbar.openCustomSnackbar({message: messages.create_new_resource_success, type: 'success', icon: 'close'});
    }),
    switchMap(() => [fromActions.resetCreationForm(), fromActions.fetchResources()])
  ));

  createResourceFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.createResourceFail),
    withLatestFrom(this._store$.select(selectMessages)),
    tap(([args, messages]) => {
      this._snackbar.openCustomSnackbar({ message: messages.fail_to_create, type: 'error', icon: 'close' });
    })
  ), { dispatch: false });

  deleteResourceOrTemptFile$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.deleteResourceOrTemptFile),
    withLatestFrom(this._store$.select(selectDeleteFileId)),
    exhaustMap(([args, fileId]) => {
      if (!fileId) {
        return [fromActions.cancelDeleteTemptFile()];
      }


      if (args.deleteType === 'resource') {
        return this._resourceService.delete(fileId).pipe(
          map(data => fromActions.deleteResourceSuccess())
        );
      }

      return this._resourceService.deleteTemp(fileId).pipe(
        map(data => fromActions.deleteTemptFileSuccess())
      );
    })
  ));

  attemptToDeleteResource$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.attemptToDeleteResource),
    switchMap(() => [fromActions.deleteResourceOrTemptFile({ deleteType: 'resource' })])
  ));

  copyClipboardTechnicalName$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.getTechnicalName),
    exhaustMap(args =>
      this._resourceService.fetchTechnicalName(args.fileId).pipe(
        map(file => fromActions.copyClipboardTechnicalNameSuccess({file})),
        catchError(httpErrorResponse => [fromActions.getTechnicalNameFail({ httpErrorResponse })])
  ))));

  getTechnicalNameFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.getTechnicalNameFail),
    withLatestFrom(this._store$.select(selectMessages)),
    tap(([args, message]) => {
      this._snackbar.openCustomSnackbar({ message: message.noInformationFound, type: 'error', icon: 'close' });
    })
  ), {dispatch: false})

  copyClipboardTechnicalNameSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.copyClipboardTechnicalNameSuccess),
    withLatestFrom(this._store$.select(selectMessages)),
    tap(([args, messages]) => {
      const {technicalName} = args.file;
      this._confirmMessageService.showInformationPopup(messages?.title_information, messages?.technicalName, technicalName);
    })
  ), {dispatch: false})

  deleteResourceSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.deleteResourceSuccess),
    withLatestFrom(this._store$.select(selectMessages)),
    tap(([args, messages]) => {
      this._snackbar.openCustomSnackbar({ message: messages.delete_resource_success, type: 'success', icon: 'close' });
    }),
    switchMap(() => [fromActions.fetchResources()])
  ));

  deleteResourceOrTemptFileFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.deleteResourceOrTemptFileFail),
    withLatestFrom(this._store$.select(selectMessages)),
    tap(([args, message]) => {
      this._snackbar.openCustomSnackbar({ message: message.delete_resource_fail, type: 'error', icon: 'close' });
    })
  ), { dispatch: false });

  downloadResourceFile$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.downloadResourceFile),
    exhaustMap(args => this._resourceService.downloadFile(args.fileId).pipe(
      map(byteCode => fromActions.downloadResourceFileSuccess({ byteCode, fileName: args.fileName })),
      catchError(httpErrorResponse => [fromActions.downloadResourceFileFail({ httpErrorResponse })]))
    ))
  );

  downloadResourceFileSuccess$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.downloadResourceFileSuccess),
    tap(args => {
      this._fileSaverUtil.downloadBase64(args.byteCode, args.fileName);
    })
  ), { dispatch: false });

  downloadResourceFileFail$ = createEffect(() => this._action$.pipe(
    ofType(fromActions.downloadResourceFileFail),
    withLatestFrom(this._store$.select(selectMessages)),
    tap(([args, messages]) => {
      this._snackbar.openCustomSnackbar({ message: messages.fail_to_download, type: 'error', icon: 'close' });
    })
  ), { dispatch: false })

  mapResource(rc: ResourceResponseList, messages: any[]): ResourceResponseList {
    const contents = rc.contents.map(r => {
      const fs = FileUtils.convertToKB(`${r.fileSize}B`);
      return {
        ...r,
        type: messages.filter(e => e.id === r.type)[0]?.name || '',
        fileSize: FileUtils.getLimitSize(`${fs}KB`),
        canDelete: this._canModification.hasModify(LibraryResourceManagement.CXM_MANAGEMENT_LIBRARY_RESOURCE, LibraryResourceManagement.DELETE, r.ownerId),
      };
    });
    return {...rc, contents};
  }

  mapResourceCriteria(rc: string[], messages: any): ResourceCriteriaResponse[] {
    return rc.map(r => ({ id: r, name: messages[`${r.toLowerCase()}`]})) as any[];
  }

}
