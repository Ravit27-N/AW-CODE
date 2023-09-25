import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Store} from "@ngrx/store";
import {
  CheckExistValueParamModel,
  DirectoryFeedService,
  FieldDetail,
  getDirectoryFeedDetail,
  selectDirectoryFieldDetail,
  selectErrorField,
  submitDirectoryFeed
} from "@cxm-smartflow/directory-feed/data-access";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {map, skip} from "rxjs/operators";
import {BehaviorSubject, Observable} from "rxjs";
import * as moment from "moment/moment";
import {Location} from '@angular/common';
import {AddDirectoryFeedValidation} from "./add-directory-feed-validation";
import {TranslateService} from "@ngx-translate/core";

interface DirectoryFieldAdd {
  id: string;
  fieldOrder: number;
  key: boolean;
  field: string;
  type: string;
  require: boolean;
  option: {
    length?: number;
    mask?: string;
  }
}

@Component({
  selector: 'cxm-smartflow-feature-add-directory-feed',
  templateUrl: './feature-add-directory-feed.component.html',
  styleUrls: ['./feature-add-directory-feed.component.scss'],
})
export class FeatureAddDirectoryFeedComponent implements OnInit,OnDestroy {
  constructor(private activatedRoute: ActivatedRoute,
              private store$: Store, private _fb: FormBuilder,
              private _location: Location,
              private translateService: TranslateService,
              private directoryFeedService: DirectoryFeedService) {

    this.formGroupDirectory = this._fb.group({});

  }
  error$ = new BehaviorSubject<boolean>(false);
  formGroupDirectory: FormGroup;
  directoryFiled$: Observable<DirectoryFieldAdd[]>;
  directoryFiled: DirectoryFieldAdd[];

  private checkValueExist: any;
  ngOnInit(): void {
    const {id} = this.activatedRoute.snapshot.queryParams;
    this.setUp(id);
    this.checkError();
  }

  ngOnDestroy() {
    this.store$.complete();
  }

  dateTimeChange(event: any, controlName: string) {
    this.formGroupDirectory.controls[`${controlName}`].setValue(event);
  }

  setUp(id: number) {
    this.store$.dispatch(getDirectoryFeedDetail({id}));

    this.directoryFiled$ = this.store$.select(selectDirectoryFieldDetail).pipe(skip(1)).pipe(map(value => {
      const newDirectoryFields: DirectoryFieldAdd[] = [];
      for (const data of value.fields) {
        const newDirectoryField: DirectoryFieldAdd = {
          id: data.id.toString(),
          fieldOrder: data.fieldOrder,
          key: data.key,
          field: data.field,
          type: data.type,
          require: data.properties.required,
          option: data.properties.option
        };
        newDirectoryFields.push(newDirectoryField);
      }
      return newDirectoryFields;
    }));
    this.directoryFiled$.subscribe(value => this.setUpFormControl(value));
  }

  private setUpFormControl(directoryFieldAdds: DirectoryFieldAdd[]) {
    this.directoryFiled = directoryFieldAdds;

    for (const data of directoryFieldAdds) {
      switch (data.type.toLowerCase()) {
        case 'string':
          this.formGroupDirectory.addControl(String(data.id), new FormControl('', AddDirectoryFeedValidation.validateString(data.require, data?.option?.length, data?.option?.mask)), {emitEvent: false});
          break;
        case 'number':
          this.formGroupDirectory.addControl(String(data.id), new FormControl(0, AddDirectoryFeedValidation.validateNumber(data.require, data?.option?.length)), {emitEvent: false});
          break;
        case 'boolean':
          this.formGroupDirectory.addControl(String(data.id), new FormControl(false), {emitEvent: false});
          break;
        case 'integer':
          this.formGroupDirectory.addControl(String(data.id), new FormControl(0, AddDirectoryFeedValidation.validateInteger(data.require, data?.option?.length)), {emitEvent: false});
          break;
        case 'date':
          this.formGroupDirectory.addControl(String(data.id), new FormControl(new Date()), {emitEvent: false});
          break;
        default:
          break;
      }
    }
  }


  submit() {
    if (this.formGroupDirectory.invalid) {
      this.error$.next(true);
      return;
    }

    const dataDirectoryFeeds: FieldDetail[] = [];
    for (const data of this.directoryFiled) {

      let value = this.formGroupDirectory.getRawValue()[`${data.id}`];

      switch (data.type.toLowerCase()) {
        case 'string':
          value = value.trim();
          break;
        case 'date':
          if (data.option?.mask != undefined) {
            value = moment(value).format(data.option.mask);
          } else {
            const defaultFormat = "DD/MM/yyyy HH:mm:ss";
            value = moment(value).format(defaultFormat);
          }
          break;
        default:
          break;
      }

      const dataDirectoryFeed: FieldDetail = {
        fieldId: Number(data.id),
        value: value
      };
      dataDirectoryFeeds.push(dataDirectoryFeed);
    }

    this.store$.dispatch(submitDirectoryFeed({dataDirectoryFeeds: dataDirectoryFeeds}));
  }

  cancel(){
    this._location.back();
  }

  checkError() {
    this.translateService.get('directory.directory_feed_details_error.duplicate').subscribe(value => {
      this.store$.select(selectErrorField).subscribe(data => {
        if (data != "") {
          for (const dataField of this.directoryFiled) {
            if (dataField.field == data) {
              const fieldError = this.formGroupDirectory.controls[`${dataField.id}`];
              fieldError.setErrors({incorrect: true, message: value});
              this.error$.next(true);
              break;
            }
          }
        }
      })
    }).unsubscribe();

  }

  getErrorMessage(message: string, length?: number, id?: string, mask?: string): string {
    const field: string =
      this.directoryFiled.find((value) => value?.id == id)?.field || '';

    return message
      .replace('${field_length}', `${length}`)
      .replace('${field_name}', `${field}`)
      .replace('${field_format}', mask || '');
  }

  updateValue(control: any) {
    const key: boolean = this.directoryFiled.find(value => value?.id == control)?.key || false;
    if (key) {
      this.validateKey(control);
    }
  }

  validateKey(control: string) {

    const value = this.formGroupDirectory.controls[`${control}`];
    const checkExistValueParamModel: CheckExistValueParamModel = {
      id: 0,
      fieldId: Number(control),
      value: value.value
    }

    clearTimeout(this.checkValueExist);
    this.checkValueExist = setTimeout(() => {
      const {id} = this.activatedRoute.snapshot.queryParams;
      this.directoryFeedService.checkExistValue(id, checkExistValueParamModel).subscribe(value1 => {
        if (value1) {
          this.translateService.get('directory.directory_feed_details_error.duplicate').subscribe(message => {
            value.setErrors({incorrect: true, message: message});
            this.error$.next(true);
          })
        }
      });
    }, 800);

  }

}
