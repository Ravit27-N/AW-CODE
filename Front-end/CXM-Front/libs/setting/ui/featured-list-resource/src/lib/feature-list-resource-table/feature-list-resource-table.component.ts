import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  attemptToDeleteResource,
  downloadResourceFile,
  fetchResources,
  getTechnicalName,
  getTranslationMsg,
  paginationChange,
  ResourceResponse,
  ResourceTypeConstant,
  selectIsManageResourceHasFilter,
  selectLength,
  selectPageIndex,
  selectPageSize,
  selectResourceList,
  selectSortColumnActive,
  selectSortDirection,
  SettingService,
  tableSortChange
} from '@cxm-smartflow/setting/data-access';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { filter, take } from 'rxjs/operators';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { API_TYPE } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-feature-list-resource-table',
  templateUrl: './feature-list-resource-table.component.html',
  styleUrls: ['./feature-list-resource-table.component.scss'],
})
export class FeatureListResourceTableComponent implements OnInit {
  dataSource$: Observable<any>;
  isHasFilter$: Observable<any>;
  page$: Observable<any>;
  pageSize$: Observable<any>;
  length$: Observable<any>;

  sortDirection$: Observable<any>;
  sortActiveColumn$: Observable<any>;
  filter: string;
  tableColumns: string[] = ['fileName', 'label', 'type', 'createdAt', 'fileSize', 'pageNumber', 'actions'];
  constructor(private _store$: Store,
              private _resourceService: SettingService,
              private _confirmMessageService: ConfirmationMessageService,
              private _router: Router,
              private _translate: TranslateService) {}

  ngOnInit(): void {
    this._store$.dispatch(getTranslationMsg());
    this._store$.dispatch(fetchResources());
    this.dataSource$ = this._store$.select(selectResourceList);
    this.page$ = this._store$.select(selectPageIndex);
    this.pageSize$ = this._store$.select(selectPageSize);
    this.length$ = this._store$.select(selectLength);
    this.isHasFilter$ = this._store$.select(selectIsManageResourceHasFilter);
    this.sortActiveColumn$ = this._store$.select(selectSortColumnActive);
    this.sortDirection$ = this._store$.select(selectSortDirection);
  }

  sortEvent(sortDirection: any) {
    this._store$.dispatch(tableSortChange({ sortByField: sortDirection.active, sortDirection: sortDirection.direction }));
  }

  paginationChange($event: any) {
    this._store$.dispatch(paginationChange({ page: $event.pageIndex, pageSize: $event.pageSize }));
  }

  delete(fileId: string) {
    this._translate.get('cxm_setting').pipe(take(1)).subscribe(messages => {
      this.confirmDelete(messages, fileId);
    });
  }

  confirmDelete(messages: any, fileId: string) {
    this._confirmMessageService.showConfirmationPopup({
      title: messages?.confirm_delete_title,
      type: 'Warning',
      heading: messages?.confirm_delete_title,
      message: messages?.confirm_delete_message,
      cancelButton: messages?.confirm_cancel_btn,
      confirmButton: messages?.confirm_delete_btn,
    }).pipe(take(1), filter(e => e))
      .subscribe(() => this._store$.dispatch(attemptToDeleteResource({ fileId })));
  }

  getURLConsult(resourceResponse: ResourceResponse): Observable<string> {
    const { fileId, fileName } = resourceResponse;
    if (resourceResponse.type === ResourceTypeConstant.signature) {
      return of(`${document.baseURI}/preview-image?fileId=${fileId}&docName=${fileName}&apiType=${API_TYPE.SETTING}`);
    } else {
      return of(`${document.baseURI}/preview-document?fileId=${fileId}&docName=${fileName}&apiType=${API_TYPE.SETTING}`);
    }
  }

  consult(resourceResponse: ResourceResponse): void {
    this.getURLConsult(resourceResponse).subscribe(url => {
      open(url, '_blank');
    });
  }

  downloadFile(resourceResponse: ResourceResponse) {
    this._store$.dispatch(downloadResourceFile({ fileId: resourceResponse.fileId, fileName: resourceResponse.fileName }));
  }

  information(fileId: string) {
    this._store$.dispatch(getTechnicalName({fileId}));
  }
}
