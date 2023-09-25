import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, exhaustMap, map, switchMap, tap, withLatestFrom } from "rxjs/operators";
import * as formFeedAction from './directory-feed-form.actions';
import { of } from 'rxjs';
import { DirectoryFeedService } from '../../services/directory-feed.service';
import { Store } from '@ngrx/store';
import { selectFeedData, selectTableSchemas } from './directory-feed-form.selector';
import * as CSV from './csv';
import { HttpEventType } from '@angular/common/http';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';
import {DirectoryFeedField, DirectoryFeedValue} from "../../models";

const mapToTableForm = (model: DirectoryFeedField) => model.fields
  .sort((a, b) => a.fieldOrder - b.fieldOrder)
  .reduce((prev, cur) => {
    const f = cur.field as string;
    const d = cur.type as string;
    const dn = cur.properties?.displayName as string;
    const require = cur.properties?.type as boolean;
    const order = cur.fieldOrder;
    const key = cur.key;
    return Object.assign(prev, { [f]: { name: f, data: d, require, displayName: dn, order, key } });
  }, {});


const mapToDictionary = (rowFeed: DirectoryFeedValue) => {
  const arr = rowFeed.values.reduce((prev, cur) => Object.assign(prev, { [cur.fieldOrder]: cur }), {});
  return { ...rowFeed, values: arr };
}



@Injectable({ providedIn: 'root' })
export class FeedFormEffect {



  // loadFeedFormEffect$ = createEffect(() => this.actions$.pipe(
  //   ofType(formFeedAction.loadFeedForm),
  //   exhaustMap(args => this.directoryService.getDirectoryDefenition(args.directoryId).pipe(
  //     map(res => {
  //       const directoryId = res.directoryId;
  //       const directoryName = res.directoryName;
  //       const columns = res.fields.sort((a, b) => a.fieldOrder - b.fieldOrder).map(x => x.field);
  //       const fields = mapToTableForm(res);
  //       return formFeedAction.loadFeedFormSuccess({ columns, fields, directoryId, directoryName });
  //     }),
  //     catchError(error => of(formFeedAction.loadFeedFormFail({ error })))
  //   )
  //   )
  // ));


  loadFeedDataEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formFeedAction.loadFeedData),
    exhaustMap(args => {
      const { page, pageSize } = args;
      return this.directoryService.getDirectoryFeedValue(Number(args.directoryId), { page: Number(page), pageSize: Number(pageSize) }).pipe(
      map(res => {
        const content = res.contents.map(mapToDictionary);
        return formFeedAction.loadFeedDataSuccess({ content, page: res.page, pageSize: res.pageSize, totoal: res.total });
      }),
      catchError(error => of(formFeedAction.loadFeedDataFail({ error })))
    ) })
  ));


  filterChangedEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formFeedAction.filterChanged),
    withLatestFrom(this.store.select(selectTableSchemas)),
    switchMap(([args, schemas]) => {
      const { page, pageSize } = args;
      return of(formFeedAction.loadFeedData({ directoryId: schemas.directoryId, page, pageSize }));
    })
  ))

  exportSchemas$ = createEffect(() => this.actions$.pipe(
    ofType(formFeedAction.exportDirectorySchema),
    withLatestFrom(this.store.select(selectTableSchemas)),
    tap(([args, schemas]) => {
      CSV.generateCsvWithHeader(schemas, `${schemas.directoryName}.csv`);
    })
  ), { dispatch: false })


  importData$ = createEffect(() => this.actions$.pipe(
    ofType(formFeedAction.importDirectoryData),
    withLatestFrom(this.store.select(selectTableSchemas)),
    switchMap(([args, schemas]) => {

      const { columns, fields } = schemas;

      if(args.json) {
        const importedData = Array.from(args.json).map((record, index) => {
          const feedData: DirectoryFeedValue = {
            id: 0, // TODO: fixing.
            directoryId: schemas.directoryId,
            lineNumber: index+'',
            values: [],
            clientId: ''
          }
          feedData.values.push(...Array.from(columns).map((k: any) => ({ value: record[k], fieldOrder: fields[k].order, id: null })) as any);

          return feedData;
        })

        const content = importedData.map(mapToDictionary);
        return of(formFeedAction.loadFeedDataSuccess({ content, page: 1, pageSize: 1, totoal: content.length }));
      } else {
        return of(formFeedAction.attempToUploadCsv({ schemas, form: args.form, filename: args.filename }));
      }
    })
  ));

  beforeUploadCsvEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formFeedAction.attempToUploadCsv),
    tap(args => {

      Promise.all([
        this.translate.get('directory.feed.dialog.validate').toPromise(),
        this.translate.get('directory.feed.dialog.message_validate_confirm').toPromise(),
        this.translate.get('directory.feed.dialog.validButton').toPromise(),
        this.translate.get('directory.feed.dialog.cancelButton').toPromise()
      ]).then(lines => this.validateMessage(lines, args.filename)
        .subscribe(v => {
          if(v) {
            this.store.dispatch(formFeedAction.uploadCsv({ ...args }));
          }
        })
      )
    })
  ), { dispatch: false })


  // // version 2 import from csv
  // importDataServer$ = createEffect(() => this.actions$.pipe(
  //   ofType(formFeedAction.uploadCsv),
  //   exhaustMap(args => this.directoryService.uploadCsvFile(args.form).pipe(
  //     map(event => {
  //       if(event.type === HttpEventType.Response) {
  //         if(event.ok) {
  //           // chain to load data
  //           return formFeedAction.loadFeedData({ directoryId: args.schemas.directoryId });
  //         } else {
  //           //
  //           return formFeedAction.loadFeedDataFail({ error: { } });
  //         }
  //       }
  //
  //       return formFeedAction.loadFeedDataFail({ error: null });
  //     }),
  //     catchError((httpError) => {
  //       const { apierrorhandler } =  httpError.error;
  //       if(apierrorhandler) {
  //         return of(formFeedAction.uploadCsvError({ error: apierrorhandler, filename: args.filename }))
  //       }
  //
  //       return of(formFeedAction.loadFeedDataFail({ error: httpError }))
  //      })
  //   ))
  // ));


  uploadCsvErrorFailEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formFeedAction.uploadCsvError),
    tap(request => {
      const { message } = request.error;
      const json = JSON.parse(message);

      const { key, messageError } = json;
      switch(key) {
      case 'csv.error.header.not_match':
       case 'csv.error.column.length':
          Promise.all([
            this.translate.get('directory.feed.dialog.error').toPromise(),
            this.translate.get('directory.feed.dialog.errormessage_column_length').toPromise(),
            this.translate.get('directory.feed.dialog.okbutton').toPromise()
          ]).then(args => this.showMessage(args, request.filename));
       break;
        case 'file.not_support':
          Promise.all([
            this.translate.get('directory.feed.dialog.error').toPromise(),
            this.translate.get('directory.feed.dialog.errormessage_filenotsupport').toPromise(),
            this.translate.get('directory.feed.dialog.okbutton').toPromise()
          ]).then(args => this.showMessage(args, request.filename));
        break;

        case 'csv.error.value.invalid_with_predefined':
          Promise.all([
            this.translate.get('directory.feed.dialog.error').toPromise(),
            this.translate.get('directory.feed.dialog.errormessage_predefine', messageError).toPromise(),
            this.translate.get('directory.feed.dialog.okbutton').toPromise()
          ]).then(args => this.showMessage(args, request.filename));
        break;

        case 'csv.error.value.must_unique':
          Promise.all([
            this.translate.get('directory.feed.dialog.error').toPromise(),
            this.translate.get('directory.feed.dialog.errormessage_unique', messageError).toPromise(),
            this.translate.get('directory.feed.dialog.okbutton').toPromise()
          ]).then(args => this.showMessage(args, request.filename));
        break;

        default:
          Promise.all([
            this.translate.get('directory.feed.dialog.error').toPromise(),
            this.translate.get(message.key).toPromise(),
            this.translate.get('directory.feed.dialog.okbutton').toPromise()
          ]).then(args => this.showMessage(args, request.filename));
        break;

      }
    })
  ), { dispatch: false })

  attempToSubmitFeedDirectory$ = createEffect(() => this.actions$.pipe(
    ofType(formFeedAction.attempToSubmitFeedDirectory),
    switchMap(args => {
      return of(formFeedAction.submitFeedDirectory());
    })
  ));

  // submitFeedDirectory$ = createEffect(() => this.actions$.pipe(
  //   ofType(formFeedAction.submitFeedDirectory),
  //   withLatestFrom(this.store.select(selectFeedData)),
  //   exhaustMap(([args, data]) => {
  //     return this.directoryService.getDirectoryDefenition('1').pipe(
  //       map(res => formFeedAction.loadFeedDataFail({ error: {} })),
  //       catchError(error => of(formFeedAction.loadFeedDataFail({ error })))
  //     );
  //   })
  // ))

  exportDataEffect$ = createEffect(() => this.actions$.pipe(
    ofType(formFeedAction.exportDirectoryData),
    withLatestFrom(this.store.select(selectTableSchemas)),
    // exhaustMap(([args, schemas]) => this.directoryService.getDirectoryDataFile(schemas.directoryName))
    tap(([args, schems]) => {
      this.directoryService.getDirectoryDataFile(schems.directoryId).toPromise().then(response => {
        const dataType = response.type;
        const binaryData = [];
        binaryData.push(response);
        const downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: dataType }));
        downloadLink.setAttribute('download', `${schems.directoryName}.csv`);
        document.body.appendChild(downloadLink);
        downloadLink.click();
      })
    })
  ), {  dispatch: false })

  constructor(private actions$: Actions, private directoryService: DirectoryFeedService, private store: Store, private messageService: ConfirmationMessageService, private translate: TranslateService) {
    this.translate.use(localStorage.getItem('locale') || 'fr');
  }

  showMessage = (params: any,title: string ) =>  this.messageService.showConfirmationMessage('cancel', params[0], title , params[1], undefined, undefined, params[2], '#D6D6D6','Warning');
  validateMessage = (params: any,title: string ) =>  this.messageService.showConfirmationMessage('feedback', params[0], title , params[1], params[3], '#D6D6D6', params[2], '#D6D6D6');
}
