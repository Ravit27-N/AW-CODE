import { Injectable } from "@angular/core";
import { FlowDepositService, UpdateOptionAttribute, watermarkform } from "@cxm-smartflow/flow-deposit/data-access";
import { SettingService } from "@cxm-smartflow/setting/data-access";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { createAction, createFeatureSelector, createReducer, createSelector, on, props, Store } from "@ngrx/store";
import { catchError, exhaustMap, filter, map, switchMap, tap, withLatestFrom } from "rxjs/operators";
import * as depositSelector from '../flow-deposit/flow-deposit.selector';
import { depositBackground } from '../flow-deposit'
import { SnackBarService } from "@cxm-smartflow/shared/data-access/services";
import { TranslateService } from "@ngx-translate/core";
import { backgroundTypeCtrl, getbackgroundPerm } from "../permission-utils";
import { FOUR } from '@angular/cdk/keycodes';

export const watermarkformKey = 'feature-watermark-form';
export const weatermarkformFeature = createFeatureSelector(watermarkformKey);

const perm = getbackgroundPerm();

const DefaultBackgroundPost = [
  { key: 'ALL_PAGES', value: 'background.position.ALL_PAGES', val: 6 },
  { key: 'FIRST_PAGE', value: 'background.position.FIRST_PAGE', val: 1 },
  { key: 'NEXT_PAGES', value: 'background.position.NEXT_PAGES', val: 2 },
  { key: 'LAST_PAGE', value: 'background.position.LAST_PAGE', val: 3 }
]

let DefaultBackgroundType = [
  { key: 'Library', value: 'background.type.watermark_form_library' },
  { key: 'ONE_TIME_UPLOAD', value: 'background.type.watermark_form_upload' },
]

DefaultBackgroundType = backgroundTypeCtrl(DefaultBackgroundType, perm);

const initialState: {
  uploadForm: any;
  libraryForm: any;
  watermarkForm: {
    type: any,
    position?: string,
    resourceId?: string,
    id: number
  },
  dialog: {
    event: 'closed'|'close'|'open',
    hash: number
  },
  resources: any[],
  position: any[],
  backgroudType: any[]
} = {
  uploadForm: { },
  libraryForm: { },
  watermarkForm: {
    id: 0,
    type: DefaultBackgroundType[0] ? DefaultBackgroundType[0].key: '',
    position: undefined,
    resourceId: undefined
  },
  dialog: {
    event: 'closed',
    hash: 0
  },
  resources: [],
  position: [ ...DefaultBackgroundPost ],
  backgroudType: [...DefaultBackgroundType]
}


export const selectWatermarkUpload = createSelector(weatermarkformFeature, (state: any) => state.uploadForm);
export const selectWatermarkLibrayForm = createSelector(weatermarkformFeature, (state: any) => state.libraryForm);
export const selectWatermarkForm = createSelector(weatermarkformFeature, (state: any) => state.watermarkForm);
const selectWatermarkState = createSelector(weatermarkformFeature, (state: any) => state);
export const selectWatermarkDialog = createSelector(weatermarkformFeature, (state: any) => state.dialog);
export const selsectWatermarkResouce = createSelector(weatermarkformFeature, (state: any) => state.resources);
export const selectBackgroundPosition = createSelector(weatermarkformFeature, (state: any) => state.position);
export const selectBackgroundTypes = createSelector(weatermarkformFeature, (state: any) => state.backgroudType);


export const watermarkUpload = createAction('[watermark form / upload]', props<{ files: any[]}>());
const watermarkUploadSuccess = createAction('[watermark form / upload success]', props<{ res: any}>());
const watermarkRecieveBackground = createAction('[watermark form / retrieve success]', props<{ res: any}>())
const watermarkBackgrounChanged = createAction('[watermark form / background changed success]', props<{ res: any}>())
const watermarkUploadfail = createAction('[watermark from / upload fail]');
const watermarkNonceAction = createAction('[watermark form / nonce]');
export const watermarkUploadClear = createAction('[watermark form / clear]');
export const editBackground = createAction('[watermark form / edit]', props<{ background: any }>())
const httpErrorActions = createAction('[watermar form / http error]', props<{ error: any, scope: string }>());
const validateErrorActions = createAction('[watermark form / validate error]', props<{ error: any }>());

export const loadWatermarkform = createAction('[watermark form / load]');
export const unloadWatermarkform = createAction('[watermark form / unload]');
const loadWatermarkFormSuccess = createAction('[watermark form / load success]', props<{ resources: any[], positions: any[], selectPosition: any }>())

export const updateWatermarkForm = createAction('[watermark form / update]', props<{ name: string, value: string }>());
export const submitWatermarkForm = createAction('[watermark form / submit]');
const submitWatermarkFormSuccess = createAction('[watermark form / submit success]');
const submitWatermarkFormFail = createAction('[watermark form / submit fail]');

const closeWatermarkDialog = createAction('[watermark form / close dialog]');

export const reducer = createReducer(initialState,
  on(watermarkUploadSuccess, (state, props) => {
    return { ...state, uploadForm: props.res }
  }),
  on(watermarkUploadClear, (state, props) => {
    return { ...state, uploadForm: { }, libraryForm: { } }
  }),
  on(unloadWatermarkform, (state) => {
    return { ...initialState }
  }),
  on(updateWatermarkForm, (state, props) => {
    const { name, value } = props;
    const updated = { [name]: value };
    return { ...state, watermarkForm: { ...state.watermarkForm, ...updated }  }
  }),
  on(closeWatermarkDialog, (state, props) => {
    return { ...state, dialog: { event: 'close', hash: new Date().getTime() } }
  }),
  on(loadWatermarkFormSuccess, (state, props) => {
    return { ...state, resources: props.resources, position: props.positions, watermarkForm: { ...state.watermarkForm, position: props.selectPosition } }
  }),
  on(editBackground, (state, props) => {
    const { type, position, fileId, id } = props.background;
    return { ...state, watermarkForm: {
      type, position, resourceId: fileId, fileId, id
    }}
  }),
  on(watermarkRecieveBackground, (state, props) => {
    if(props.res.type === 'Library') {
      return { ...state, libraryForm: props.res, dialog: { event: 'open', hash: new Date().getTime() } }
    }

    if(props.res.type === 'ONE_TIME_UPLOAD') {
      const isMissing = props.res.fileSize <= 0;
      return { ...state, uploadForm: isMissing? { } : props.res, dialog: { event: 'open', hash: new Date().getTime() } }
    }
    return { ...state, uploadForm: props.res, dialog: { event: 'open', hash: new Date().getTime() } }
  }),
  on(watermarkBackgrounChanged, (state, props) => {
    return { ...state, libraryForm: { base64: props.res } }
  }),
);


const validdatePayload = (payload: any) => {
  const { flowId, id, type, position, fileId } = payload;
  if(!fileId) {
    return {
      error: true,
      message: type === 'Library' ? 'background.errors.selectPageBackgound' : 'background.errors.uploadPageBackgound'
    }
  }

  return { error: false }
}


@Injectable()
export class WatermarkFormEffect {

  watermarkUoloadEffect$ = createEffect(() => this.actions.pipe(
    ofType(watermarkUpload),
    withLatestFrom(this.store.select(depositSelector.selectFlowDepositState)),
    exhaustMap(([args, flow]) => {
      const { files } = args;
      const { navigateParams } = flow as any;
      const formdata = new FormData();
      if(files.length > 0) {
        formdata.append('file', files[0]);
      }

      return this.depositService.storeBackground(navigateParams.fileId, 'Background', formdata).pipe(
        map(res => {
          if(res.type === 4) {
            const { body } = res;
            return watermarkUploadSuccess({ res: body })
          }
          return watermarkNonceAction();
        }),
        catchError((error) => [httpErrorActions({ error, scope: 'upload' })])
      );
    })
  ))


  editBackgroundEffect$ = createEffect(() => this.actions.pipe(
    ofType(editBackground),
    exhaustMap(args => {
      const { background } = args;
      return this.depositService.getOptionAttributeDetail(background.id).pipe(
        map(res => watermarkRecieveBackground({ res }))
      )
    })
  ))

  httpErrorEffect$ = createEffect(() => this.actions.pipe(
    ofType(httpErrorActions),
    withLatestFrom(this.store.select(selectWatermarkState), this.store.select(depositSelector.selectFlowDepositState)),
    tap(([args, watermark, flow]) => {
      //
      const { error, scope } = args;
      if(error && error.error && error.error.apierrorhandler) {
        const { apierrorhandler } = error.error;

        if([500].includes(apierrorhandler.statusCode)) {
          this.translate.get('background.errors').toPromise().then((messages: any) => {
            this.snackbar.openCustomSnackbar({ type: 'error', message: messages.unknown })
          })
        } else if(scope === 'submit') {
          if([404, 400].includes(apierrorhandler.statusCode)) {
            this.translate.get('background.errors').toPromise().then((messages: any) => {
              this.snackbar.openCustomSnackbar({ type: 'error', message: messages.file_missing })
            })
          }
        } else if(scope === 'upload') {
          if([4002].includes(apierrorhandler.statusCode)) {
            this.translate.get('background.errors').toPromise().then((messages: any) => {
              this.snackbar.openCustomSnackbar({ type: 'error', message: messages.page_limit })
            })
          }

          if([4001].includes(apierrorhandler.statusCode)) {
            this.translate.get('background.errors').toPromise().then((messages: any) => {
              this.snackbar.openCustomSnackbar({ type: 'error', message: messages.not_pdf })
            })
          }
        } else if(scope === 'list') {
          this.translate.get('background.errors').toPromise().then((messages: any) => {
            this.snackbar.openCustomSnackbar({ type: 'error', message: messages.unauthorize })
          })
        }

        // if([400, 404].includes(apierrorhandler.statusCode)) {
        //   this.snackbar.openCustomSnackbar({ type: 'error', message: apierrorhandler.message.file_missing })
        // }
      } else {
        this.translate.get('background.errors').toPromise().then((messages: any) => {
          this.snackbar.openCustomSnackbar({ type: 'error', message: messages.unknown })
        })
      }

    })
  ), { dispatch: false })

  // watermarkUploadSuccessEffect$ = createEffect(() => this.actions.pipe(
  //   ofType(watermarkUploadSuccess),
  //   switchMap(args => {
  //     return [depositBackground.invalidateBackground()]
  //   })
  // ))

  watermarkSubmitFormEffect$ = createEffect(() => this.actions.pipe(
    ofType(submitWatermarkForm),
    withLatestFrom(this.store.select(selectWatermarkState), this.store.select(depositSelector.selectFlowDepositState)),
    exhaustMap(([args, watermark, flow]) => {
      const { navigateParams } = flow as any;
      const { uploadForm, watermarkForm } = watermark;

      let payload: UpdateOptionAttribute;
      if(watermarkForm.type === 'ONE_TIME_UPLOAD') {
        payload = {
          flowId: navigateParams.fileId,
          position: watermarkForm.position,
          type: watermarkForm.type,
          fileId: uploadForm.fileId,
          id: watermarkForm.id,
          source: 'Background',
        }
      } else {
        payload = {
          flowId: navigateParams.fileId,
          position: watermarkForm.position,
          type: watermarkForm.type,
          fileId: watermarkForm.resourceId,
          id: watermarkForm.id,
          source: 'Background',
        }
      }

      // validate payload request;
      const validate = validdatePayload(payload);
      if(validate.error) {
        return [validateErrorActions({ error: validate })]
      }

      return this.depositService.UpdateOptionAttribute(payload).pipe(
        map(res => submitWatermarkFormSuccess()),
        catchError(error => [httpErrorActions({ error, scope: 'submit' })])
      )
    })
  ))

  submitFormSuccessEffect$ = createEffect(() => this.actions.pipe(
    ofType(submitWatermarkFormSuccess),
    switchMap(args => [closeWatermarkDialog(), depositBackground.invalidateBackground()])
  ))

  loadWatermarkForm = createEffect(() => this.actions.pipe(
    ofType(loadWatermarkform),
    withLatestFrom(this.store.select(depositBackground.selectBackhgroundList), this.store.select(selectWatermarkState), this.translate.get('background.position')),
    exhaustMap(([args, background, watermarkState, t]) => this.settingService.getAll({ }, '').pipe(
      map(res => {

        let allowedPost = DefaultBackgroundPost.filter(x => background.config.bg.includes(x.val)===false);
        // filter position all page there is any background applied
        if(background.config.bg.length > 0 ) {
          if(background.config.bg.includes(6)) {
            allowedPost = allowedPost.filter(x => x.val === 6);
          } else {
            allowedPost = allowedPost.filter(x => x.val !== 6);
          }
        }

        let { position } = watermarkState.watermarkForm;

        if(position) {
          const originalPost =  DefaultBackgroundPost.filter(x => x.key === position);
          allowedPost = [ ...originalPost,...allowedPost]

          if(background.config.bg.length === 1) {
            allowedPost = [...DefaultBackgroundPost]
          }
        } else {
          position = allowedPost[0].key;
        }

        allowedPost = allowedPost.map(p => ({ ...p, value: t[p.key] }));

        let { contents } = res;
        contents = contents.map((x: any) => ({ ...x, key: x.fileId, value: x.label }));
        return loadWatermarkFormSuccess({ resources: contents, positions: allowedPost, selectPosition: position  });
      }),
      catchError(error => [httpErrorActions({ error, scope: 'list' })])
    ))
  ))


  validateErrorActionsEffect$ = createEffect(() => this.actions.pipe(
    ofType(validateErrorActions),
    tap(args => {
      const { error } = args;
      this.translate.get(error.message).toPromise().then(message => {
        this.snackbar.openCustomSnackbar({ type: 'error', message })
      })
    })
  ), { dispatch: false })


  updateWatermarkFormEffect$ = createEffect(() => this.actions.pipe(
    ofType(updateWatermarkForm),
    filter(args => args.name === 'resourceId'),
    exhaustMap(args => {
      const { value } = args;
      return this.depositService.getResourceFileById(value).pipe(
        map(res => watermarkBackgrounChanged({ res }))
      )
    })
  ))

  constructor(private actions: Actions,
    private depositService: FlowDepositService,
    private settingService: SettingService,
    private snackbar: SnackBarService,
    private translate: TranslateService,
    private store: Store) {

  }

}
